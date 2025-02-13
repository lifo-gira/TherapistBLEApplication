package com.example.therapistbluelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CallNotificationAction extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle call events, such as answering or rejecting calls
        Log.d(TAG, "Call event received");
        // You can add custom logic for handling call events here.
    }
}
