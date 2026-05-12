package com.sensisgoodff.sensor;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.util.Log;
import java.io.*;

public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = "SensisFF";
    private static final String CHANNEL_ID = "sensis_sensor";
    private static final float GYRO_FAST = 3.5f;
    private static final float GYRO_MID  = 1.5f;
    private static final float ALPHA = 0.3f;

    private float smoothedMag = 0f;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private Handler applyHandler;
    private Handler watchdogHandler;
    private int pendingLevel = 0;
    private int lastAppliedLevel = -1;
    public static int touchLevel = 0;

    private final Runnable applyRunnable = new Runnable() {
        @Override
        public void run() {
            int level = Math.max(pendingLevel, touchLevel);
            if (level != lastAppliedLevel) {
                ShizukuHelper.applyLevel(SensorService.this, level);
                lastAppliedLevel = level;
                Log.d(TAG, "Nivel=" + level + " gyro=" + smoothedMag);
            }
            applyHandler.postDelayed(this, 16);
        }
    };

    // Watchdog — reinicia el script bash si muere
    private final Runnable watchdogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                java.lang.Process p = Runtime.getRuntime().exec(
                    new String[]{"sh", "-c", "ps | grep sensor.sh | grep -v grep"});
                p.waitFor();
                if (p.exitValue() != 0) {
                    Log.d(TAG, "Script muerto, reiniciando...");
                    startSensorScript();
                }
            } catch (Exception e) {
                Log.e(TAG, "Watchdog error: " + e.getMessage());
            }
            watchdogHandler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SensorService onCreate");
        createNotificationChannel();
        startForeground(1, buildNotification());

        // Intentar gyro
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, 10000, 10000);
                Log.d(TAG, "Gyro OK");
            } else {
                Log.w(TAG, "Gyro NO disponible");
            }
        }

        // Extraer y correr script bash
        extractAndStartScript();

        applyHandler = new Handler(Looper.getMainLooper());
        applyHandler.post(applyRunnable);

        watchdogHandler = new Handler(Looper.getMainLooper());
        watchdogHandler.postDelayed(watchdogRunnable, 5000);
    }

    private void extractAndStartScript() {
        try {
            // Escribir script bash al storage
            String script = "PREV_X=0\n" +
                "LAST_LEVEL=0\n" +
                "getevent -lt /dev/input/event4 | while read line; do\n" +
                "    if echo \"$line\" | grep -q BTN_TOUCH; then\n" +
                "        if echo \"$line\" | grep -q UP; then\n" +
                "            PREV_X=0\n" +
                "            if [ \"$LAST_LEVEL\" != 0 ]; then\n" +
                "                settings put system pointer_speed 2\n" +
                "                settings put system scroll_friction 0.7\n" +
                "                LAST_LEVEL=0\n" +
                "            fi\n" +
                "        fi\n" +
                "    fi\n" +
                "    if echo \"$line\" | grep -q ABS_MT_POSITION_X; then\n" +
                "        X=$(echo \"$line\" | awk '{print $NF}')\n" +
                "        X=$((16#$X))\n" +
                "        if [ \"$PREV_X\" != 0 ]; then\n" +
                "            DX=$((X - PREV_X))\n" +
                "            if [ $DX -lt 0 ]; then DX=$((-DX)); fi\n" +
                "            if [ $DX -lt 100 ]; then\n" +
                "                if [ $DX -gt 12 ] && [ \"$LAST_LEVEL\" != 2 ]; then\n" +
                "                    settings put system pointer_speed 7\n" +
                "                    settings put system scroll_friction 0.1\n" +
                "                    LAST_LEVEL=2\n" +
                "                elif [ $DX -gt 6 ] && [ $DX -le 12 ] && [ \"$LAST_LEVEL\" != 1 ]; then\n" +
                "                    settings put system pointer_speed 5\n" +
                "                    settings put system scroll_friction 0.4\n" +
                "                    LAST_LEVEL=1\n" +
                "                fi\n" +
                "            fi\n" +
                "        fi\n" +
                "        PREV_X=$X\n" +
                "    fi\n" +
                "done\n";

            File scriptFile = new File("/data/local/tmp/sensis_sensor.sh");
            FileWriter fw = new FileWriter(scriptFile);
            fw.write(script);
            fw.close();
            Runtime.getRuntime().exec("chmod 777 /data/local/tmp/sensis_sensor.sh");
            startSensorScript();
        } catch (Exception e) {
            Log.e(TAG, "Error extrayendo script: " + e.getMessage());
        }
    }

    private void startSensorScript() {
        try {
            Runtime.getRuntime().exec(new String[]{
                "rish", "-c",
                "sh /data/local/tmp/sensis_sensor.sh &"
            });
            Log.d(TAG, "Script iniciado");
        } catch (Exception e) {
            Log.e(TAG, "Error iniciando script: " + e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE) return;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float rawMag = (float) Math.sqrt(x*x + y*y + z*z);
        smoothedMag = ALPHA * rawMag + (1f - ALPHA) * smoothedMag;

        if (smoothedMag >= GYRO_FAST) pendingLevel = 2;
        else if (smoothedMag >= GYRO_MID) pendingLevel = 1;
        else pendingLevel = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onCreate();
        if (sensorManager != null) sensorManager.unregisterListener(this);
        if (applyHandler != null) applyHandler.removeCallbacks(applyRunnable);
        if (watchdogHandler != null) watchdogHandler.removeCallbacks(watchdogRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("⚡ Sensor activo")
                .setContentText("Optimizando sensibilidad en tiempo real")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Sensor SENSIS FF",
                    NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
    }
}
