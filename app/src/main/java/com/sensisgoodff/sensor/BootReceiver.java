package com.sensisgoodff.sensor;

import android.content.*;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, SensorService.class);
            context.startForegroundService(i);
        }
    }
}
