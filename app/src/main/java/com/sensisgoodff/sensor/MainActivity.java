package com.sensisgoodff.sensor;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.widget.Toast;
import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

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

            // Iniciar servicio via Shizuku con privilegios
            ShizukuRemoteProcess process = Shizuku.newProcess(
                new String[]{"am", "startforegroundservice",
                    "com.sensisgoodff.sensor/.SensorService"},
                null, null);
            process.waitFor();

            Toast.makeText(this, "⚡ Sensor iniciado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
    }
}
