package com.sensisgoodff.sensor;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = "SensorService";
    private static final String CHANNEL_ID = "sensis_sensor";

    // Umbrales de velocidad del gyro (rad/s)
    private static final float GYRO_FAST  = 2.5f;
    private static final float GYRO_MID   = 1.0f;

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private Handler applyHandler;
    private int pendingLevel = -1;
    private int lastAppliedLevel = -1;

    // Runnable que aplica el nivel cada 16ms (solo si cambió)
    private final Runnable applyRunnable = new Runnable() {
        @Override
        public void run() {
            if (pendingLevel != lastAppliedLevel) {
                ShizukuHelper.applyLevel(SensorService.this, pendingLevel);
                lastAppliedLevel = pendingLevel;
            }
            applyHandler.postDelayed(this, 16);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildNotification(true));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope != null) {
            // 10ms = 10000 microseconds — ajustado al Honor X5C Plus
            sensorManager.registerListener(this, gyroscope,
                    10000, 10000);
        }

        applyHandler = new Handler(Looper.getMainLooper());
        applyHandler.post(applyRunnable);

        Log.d(TAG, "SensorService iniciado");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude = (float) Math.sqrt(x*x + y*y + z*z);

        if (magnitude >= GYRO_FAST) {
            pendingLevel = 2; // RÁPIDO
        } else if (magnitude >= GYRO_MID) {
            pendingLevel = 1; // MEDIO
        } else {
            pendingLevel = 0; // LENTO
        }
    }

    // Llamado desde TouchAccessibilityService para actualizar nivel por touch
    public static int touchLevel = 0;

    public void updateFromTouch(int level) {
        // Toma el mayor entre gyro y touch
        pendingLevel = Math.max(pendingLevel, level);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        applyHandler.removeCallbacks(applyRunnable);
        // Notificar que el sensor se desactivó
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(2, buildNotification(false));
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Se reinicia automáticamente si Android lo mata
    }

    private Notification buildNotification(boolean active) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(active ? "⚡ Sensor activo" : "⚠️ Sensor desactivado")
                .setContentText(active ? "Optimizando sensibilidad en tiempo real"
                        : "Toca para activar el sensor")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pi)
                .setOngoing(active)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Sensor SENSIS FF",
                    NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Control de sensibilidad en tiempo real");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
    }
}
