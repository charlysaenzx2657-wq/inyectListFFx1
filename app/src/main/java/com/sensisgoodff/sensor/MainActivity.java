package com.sensisgoodff.sensor;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.util.Log;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SensisFF", "onCreate");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        Shizuku.requestPermission(1001);
                    }
                }
                Intent i = new Intent(this, SensorService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(i);
                } else {
                    startService(i);
                }
                Log.d("SensisFF", "Servicio iniciado OK");
            } catch (Exception e) {
                Log.e("SensisFF", "Error: " + e.getMessage());
            }
            finish();
        }, 500);
    }
}
