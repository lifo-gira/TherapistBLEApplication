package com.example.therapistbluelock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the notification channel when the service is created
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if the message contains a notification payload
        if (remoteMessage.getData().size() > 0) {
            // Handle the data message
            handleDataMessage(remoteMessage);
        }

        if (remoteMessage.getNotification() != null) {
            String notificationBody = remoteMessage.getNotification().getBody();
            Log.d("FCM", "Notification Body: " + notificationBody);
            showNotification(notificationBody);
        }
    }

    // Method to create a notification channel (only for Android O and above)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to show notification
    private void showNotification(String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "2";  // Use the channel ID you defined

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("New Message")
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        notificationManager.notify(0, notification);
    }

    // Method to handle data messages
    private void handleDataMessage(RemoteMessage remoteMessage) {
        // Handle the custom data from the message payload
        String customData = remoteMessage.getData().get("customKey");
        Log.d("FCM", "Custom Data: " + customData);
    }
}
