package com.example.therapistbluelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "Phone has rebooted");
            Intent serviceIntent = new Intent(context, WebSocketService.class);
            context.startService(serviceIntent); // Start WebSocket service
        }
    }
}
