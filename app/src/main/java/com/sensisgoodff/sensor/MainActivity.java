package com.sensisgoodff.sensor;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pedir permiso Shizuku si no está otorgado
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(1001);
            }
        }

        // Iniciar servicio
        Intent i = new Intent(this, SensorService.class);
        startForegroundService(i);

        // Cerrar activity (sin UI)
        finish();
    }
}
