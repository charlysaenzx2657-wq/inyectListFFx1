package com.sensisgoodff.sensor;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.util.Log;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SensorService onCreate");
        createNotificationChannel();
        startForeground(1, buildNotification());

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

        applyHandler = new Handler(Looper.getMainLooper());
        applyHandler.post(applyRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE) return;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float rawMag = (float) Math.sqrt(x*x + y*y + z*z);
        smoothedMag = ALPHA * rawMag + (1f - ALPHA) * smoothedMag;

        Log.d(TAG, "gyro mag=" + smoothedMag);

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
        super.onDestroy();
        if (sensorManager != null) sensorManager.unregisterListener(this);
        if (applyHandler != null) applyHandler.removeCallbacks(applyRunnable);
        Log.d(TAG, "SensorService destruido");
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
