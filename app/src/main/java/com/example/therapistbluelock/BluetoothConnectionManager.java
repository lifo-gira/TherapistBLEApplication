package com.example.therapistbluelock;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothConnectionManager {
    private static BluetoothConnectionManager instance;
    private List<BluetoothSocket> bluetoothSockets;
    private List<BluetoothDevice> bluetoothDevices;

    private BluetoothConnectionManager() {
        bluetoothSockets = new ArrayList<>();
        bluetoothDevices = new ArrayList<>();
    }

    public static BluetoothConnectionManager getInstance() {
        if (instance == null) {
            instance = new BluetoothConnectionManager();
        }
        return instance;
    }

    public void connect(BluetoothDevice device) {
        // Check if device is already connected
        if (bluetoothDevices.contains(device)) {
            Log.d("BluetoothConnectionManager", "Device already connected: " + device.getAddress());
            return;
        }

        BluetoothSocket socket = null;

        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect(); // Attempt to connect
            bluetoothSockets.add(socket);
            bluetoothDevices.add(device);
            Log.d("BluetoothConnectionManager", "Connected to: " + device.getAddress());
        } catch (IOException e) {
            Log.e("BluetoothConnectionManager", "Error connecting to " + device.getAddress() + ": " + e.getMessage());
            handleDisconnection(socket); // Handle disconnection if an error occurs
        }
    }

    public BluetoothSocket getBluetoothSocket(BluetoothDevice device) {
        int index = bluetoothDevices.indexOf(device);
        return index != -1 ? bluetoothSockets.get(index) : null;
    }

    public boolean isDeviceConnected(BluetoothDevice device) {
        return bluetoothDevices.contains(device);
    }

    public void handleDisconnection(BluetoothSocket socket) {
        if (socket != null) {
            try {
                socket.close();
                bluetoothSockets.remove(socket);
                Log.d("BluetoothConnectionManager", "Socket closed and removed");
            } catch (IOException e) {
                Log.e("BluetoothConnectionManager", "Error closing socket: " + e.getMessage());
            }
        }
    }

    public void handleAllDisconnections() {
        // Create copies of the lists to safely iterate and modify
        List<BluetoothSocket> socketsCopy = new ArrayList<>(bluetoothSockets);
        List<BluetoothDevice> devicesCopy = new ArrayList<>(bluetoothDevices);

        for (BluetoothSocket socket : socketsCopy) {
            handleDisconnection(socket);
        }

        // Now safely remove devices from the original list
        for (BluetoothDevice device : devicesCopy) {
            bluetoothDevices.remove(device);
        }

        Log.d("BluetoothConnectionManager", "All sockets closed and devices removed");
    }
}