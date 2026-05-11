package com.sensisgoodff.sensor;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.widget.Toast;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "⚠️ Shizuku no disponible", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(1001);
                finish();
                return;
            }

            // Iniciar servicio normal
            Intent i = new Intent(this, SensorService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i);
            } else {
                startService(i);
            }

            Toast.makeText(this, "⚡ Sensor iniciado", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
    }
}
