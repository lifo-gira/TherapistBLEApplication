package com.example.therapistbluelock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends android.app.Service {

    private OkHttpClient client;
    private WebSocket webSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeWebSocket();
    }

    private void initializeWebSocket() {
        client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("wss://api-wo6.onrender.com/patients") // Replace with your WebSocket server URL
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                super.onOpen(webSocket, response);
                Log.d("WebSocket", "WebSocket Connected");

                // Send a message to the server
                webSocket.send("Hello, WebSocket!");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("Received message: " + text);
                Log.e("WebSocket Message", text);

                try {
                    // Parse the JSON response
                    JSONObject messageJson = new JSONObject(text);

                    // Extract the 'flag' value from the JSON
                    int flag = messageJson.optInt("flag", -1); // Default to -1 if 'flag' is not found

                    // Check if the flag is 1 or 2 and send different notifications
                    if (flag == 3) {
                        sendFlagNotification(); // Send notification for flag 1
                    } else {
                        Log.d("WebSocket Message", "Flag value is not 1 or 2. No notification sent.");
                    }
                } catch (JSONException e) {
                    Log.e("WebSocket Message", "Error parsing JSON", e);
                }
            }


            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d("WebSocket", "Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d("WebSocket", "Closing: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
                Log.e("WebSocket", "Error: " + t.getMessage());
            }
        });
    }

    private void sendFlagNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "channel_flag_1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Flag 1 Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Performed Exercise")
                .setContentText("A Patient has completed Exercise")
                .setSmallIcon(R.drawable.login_akka) // Replace with your actual icon
                .build();

        notificationManager.notify(1, notification); // Use a unique ID for this type of notification
        Log.d("Notification", "Flag 1 notification sent!");
    }


//    private void sendNotification(String messageBody) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        String channelId = "default_channel";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Notification notification = new NotificationCompat.Builder(this, channelId)
//                .setContentTitle("New WebSocket Message")
//                .setContentText(messageBody)
//                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with an actual icon
//                .build();
//
//        notificationManager.notify(0, notification);
//    }

    @Nullable
    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service Destroyed");
        }
    }
}
