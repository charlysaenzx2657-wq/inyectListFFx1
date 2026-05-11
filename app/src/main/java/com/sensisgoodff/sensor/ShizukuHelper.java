package com.sensisgoodff.sensor;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import rikka.shizuku.Shizuku;

public class ShizukuHelper {

    private static final String TAG = "ShizukuHelper";
    private static int lastPointerSpeed = -99;
    private static float lastScrollFriction = -1f;

    public static boolean isAvailable() {
        try {
            return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    // Aplica pointer_speed solo si cambió
    public static void applyPointerSpeed(Context ctx, int speed) {
        if (speed == lastPointerSpeed) return;
        try {
            Settings.System.putInt(ctx.getContentResolver(),
                    Settings.System.POINTER_SPEED, speed);
            lastPointerSpeed = speed;
            Log.d(TAG, "pointer_speed → " + speed);
        } catch (Exception e) {
            Log.e(TAG, "Error pointer_speed: " + e.getMessage());
        }
    }

    // Aplica scroll_friction solo si cambió
    public static void applyScrollFriction(Context ctx, float friction) {
        if (Math.abs(friction - lastScrollFriction) < 0.01f) return;
        try {
            Settings.System.putFloat(ctx.getContentResolver(),
                    "scroll_friction", friction);
            lastScrollFriction = friction;
            Log.d(TAG, "scroll_friction → " + friction);
        } catch (Exception e) {
            Log.e(TAG, "Error scroll_friction: " + e.getMessage());
        }
    }

    // Aplica ambos de una vez
    public static void applyLevel(Context ctx, int level) {
        // level: 0=LENTO, 1=MEDIO, 2=RAPIDO
        switch (level) {
            case 2: // RÁPIDO
                applyPointerSpeed(ctx, 7);
                applyScrollFriction(ctx, 0.1f);
                break;
            case 1: // MEDIO
                applyPointerSpeed(ctx, 5);
                applyScrollFriction(ctx, 0.4f);
                break;
            default: // LENTO
                applyPointerSpeed(ctx, 2);
                applyScrollFriction(ctx, 0.7f);
                break;
        }
    }
}
