package com.example.therapistbluelock;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Service {

    public static final String ACTION_DATA_RECEIVED = "com.example.patientbluelock.ACTION_DATA_RECEIVED";
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected = false; // Track connection status

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void connectToDevice(BluetoothDevice device) {
        try {
            // Replace with your UUID for the Bluetooth device
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            isConnected = true; // Update connection status

            // Start a thread to listen for incoming data
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForData();
                }
            }).start();

        } catch (IOException e) {
            Log.e("BluetoothService", "Connection failed", e);
            isConnected = false; // Update connection status on failure
        }
    }

    private void listenForData() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (isConnected) {
            try {
                bytes = inputStream.read(buffer);
                String receivedData = new String(buffer, 0, bytes);
                // Broadcast the received data
                Intent intent = new Intent(ACTION_DATA_RECEIVED);
                intent.putExtra("data", receivedData);
                sendBroadcast(intent);
            } catch (IOException e) {
                Log.e("BluetoothService", "Error reading data", e);
                isConnected = false; // Update connection status on error
                break;
            }
        }
    }

    public void disconnect() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                isConnected = false; // Update connection status
            }
        } catch (IOException e) {
            Log.e("BluetoothService", "Error disconnecting", e);
        }
    }

    public boolean isDeviceConnected() {
        return isConnected;
    }
}
