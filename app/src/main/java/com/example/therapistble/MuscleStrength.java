package com.example.therapistble;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.therapistble.DetailFrag_5.speedometer1;
import static com.example.therapistble.DetailFrag_5.speedometer2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MuscleStrength extends AppCompatActivity {

    private View clickableLeftArm;
    private View rrc1, rrc2, rrc3, rrc4, rrc5, rrc6, rrc7, rrc8, rrc9;
    private View rlc1, rlc2, rlc3, rlc4, rlc5;
    private View lc1, lc2, lc3, lc4, lc5, lc6, lc7, lc8, lc9;
    private View llc1, llc2, llc3, llc4, llc5;

    private ImageView rr1, rr2, rr3, rr4, rr5, rr6, rr7, rr8, rr9;
    private ImageView rrl1, rrl2, rrl3, rrl4, rrl5;
    private ImageView ll1, ll2, ll3, ll4, ll5, ll6, ll7, ll8, ll9;
    private ImageView lll1, lll2, lll3, lll4, lll5;

    private ImageView bodyOutline;

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothSocket> bluetoothSockets;
    private List<String> deviceAddresses;
    private Map<String, BluetoothGatt> connectedDevices = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    BluetoothConnectionManager connectionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_strength);
        hideSystemUI();
        // Initialize views

        connectionManager = BluetoothConnectionManager.getInstance();

        setupBluetoothConnections();

        Button submitButton = findViewById(R.id.complete_button);

        rrc1 = findViewById(R.id.clickable_r1);
        rrc2 = findViewById(R.id.clickable_r2);
        rrc3 = findViewById(R.id.clickable_r3);
        rrc4 = findViewById(R.id.clickable_r4);
        rrc5 = findViewById(R.id.clickable_r5);
        rrc6 = findViewById(R.id.clickable_r6);
        rrc7 = findViewById(R.id.clickable_r7);
        rrc8 = findViewById(R.id.clickable_r8);
        rrc9 = findViewById(R.id.clickable_r9);

        rlc1 = findViewById(R.id.clickable_rl1);
        rlc2 = findViewById(R.id.clickable_rl2);
        rlc3 = findViewById(R.id.clickable_rl3);
        rlc4 = findViewById(R.id.clickable_rl4);
        rlc5 = findViewById(R.id.clickable_rl5);

        lc1 = findViewById(R.id.clickable_l1);
        lc2 = findViewById(R.id.clickable_l2);
        lc3 = findViewById(R.id.clickable_l3);
        lc4 = findViewById(R.id.clickable_l4);
        lc5 = findViewById(R.id.clickable_l5);
        lc6 = findViewById(R.id.clickable_l6);
        lc7 = findViewById(R.id.clickable_l7);
        lc8 = findViewById(R.id.clickable_l8);
        lc9 = findViewById(R.id.clickable_l9);

        llc1 = findViewById(R.id.clickable_ll1);
        llc2 = findViewById(R.id.clickable_ll2);
        llc3 = findViewById(R.id.clickable_ll3);
        llc4 = findViewById(R.id.clickable_ll4);
        llc5 = findViewById(R.id.clickable_ll5);

        rr1 = findViewById(R.id.r1);
        rr2 = findViewById(R.id.r2);
        rr3 = findViewById(R.id.r3);
        rr4 = findViewById(R.id.r4);
        rr5 = findViewById(R.id.r5);
        rr6 = findViewById(R.id.r6);
        rr7 = findViewById(R.id.r7);
        rr8 = findViewById(R.id.r8);
        rr9 = findViewById(R.id.r9);

        rrl1 = findViewById(R.id.rl1);
        rrl2 = findViewById(R.id.rl2);
        rrl3 = findViewById(R.id.rl3);
        rrl4 = findViewById(R.id.rl4);
        rrl5 = findViewById(R.id.rl5);

        ll1 = findViewById(R.id.l1);
        ll2 = findViewById(R.id.l2);
        ll3 = findViewById(R.id.l3);
        ll4 = findViewById(R.id.l4);
        ll5 = findViewById(R.id.l5);
        ll6 = findViewById(R.id.l6);
        ll7 = findViewById(R.id.l7);
        ll8 = findViewById(R.id.l8);
        ll9 = findViewById(R.id.l9);

        lll1 = findViewById(R.id.ll1);
        lll2 = findViewById(R.id.ll2);
        lll3 = findViewById(R.id.ll3);
        lll4 = findViewById(R.id.ll4);
        lll5 = findViewById(R.id.ll5);

        bodyOutline = findViewById(R.id.image_body);
        String itemType = getIntent().getStringExtra("itemType");
        String itemTitle = getIntent().getStringExtra("itemTitle");
        // Handle clickable items
        setOnClickListeners();

        // Set up click listener for SUBMIT button to navigate to DetailFrag_5
        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MuscleStrength.this, DetailFrag_5.class);
            // Pass the updated values back
            intent.putExtra("itemTitle", itemTitle);
            intent.putExtra("itemStatus", "Completed");
            intent.putExtra("itemColor", Color.GREEN);

            // Start the AssessmentList activity with the updated data
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void setOnClickListeners() {
        rrc1.setOnClickListener(v -> rr1.setVisibility(View.VISIBLE));
        rrc2.setOnClickListener(v -> rr2.setVisibility(View.VISIBLE));
        rrc3.setOnClickListener(v -> rr3.setVisibility(View.VISIBLE));
        rrc4.setOnClickListener(v -> rr4.setVisibility(View.VISIBLE));
        rrc5.setOnClickListener(v -> rr5.setVisibility(View.VISIBLE));
        rrc6.setOnClickListener(v -> rr6.setVisibility(View.VISIBLE));
        rrc7.setOnClickListener(v -> rr7.setVisibility(View.VISIBLE));
        rrc8.setOnClickListener(v -> rr8.setVisibility(View.VISIBLE));
        rrc9.setOnClickListener(v -> rr9.setVisibility(View.VISIBLE));

        rlc1.setOnClickListener(v -> rrl1.setVisibility(View.VISIBLE));
        rlc2.setOnClickListener(v -> rrl2.setVisibility(View.VISIBLE));
        rlc3.setOnClickListener(v -> rrl3.setVisibility(View.VISIBLE));
        rlc4.setOnClickListener(v -> rrl4.setVisibility(View.VISIBLE));
        rlc5.setOnClickListener(v -> rrl5.setVisibility(View.VISIBLE));

        lc1.setOnClickListener(v -> ll1.setVisibility(View.VISIBLE));
        lc2.setOnClickListener(v -> ll2.setVisibility(View.VISIBLE));
        lc3.setOnClickListener(v -> ll3.setVisibility(View.VISIBLE));
        lc4.setOnClickListener(v -> ll4.setVisibility(View.VISIBLE));
        lc5.setOnClickListener(v -> ll5.setVisibility(View.VISIBLE));
        lc6.setOnClickListener(v -> ll6.setVisibility(View.VISIBLE));
        lc7.setOnClickListener(v -> ll7.setVisibility(View.VISIBLE));
        lc8.setOnClickListener(v -> ll8.setVisibility(View.VISIBLE));
        lc9.setOnClickListener(v -> ll9.setVisibility(View.VISIBLE));

        llc1.setOnClickListener(v -> lll1.setVisibility(View.VISIBLE));
        llc2.setOnClickListener(v -> lll2.setVisibility(View.VISIBLE));
        llc3.setOnClickListener(v -> lll3.setVisibility(View.VISIBLE));
        llc4.setOnClickListener(v -> lll4.setVisibility(View.VISIBLE));
        llc5.setOnClickListener(v -> lll5.setVisibility(View.VISIBLE));
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }

    @SuppressLint("MissingPermission")
    private void setupBluetoothConnections() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String addressesString = sharedPreferences.getString("device_addresses", null);
        ArrayList<String> deviceAddresses = addressesString != null
                ? new ArrayList<>(Arrays.asList(addressesString.split(",")))
                : new ArrayList<>();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e("Bluetooth", "Bluetooth not available or not enabled");
            return;
        }

//        for (int i = 0; i < deviceAddresses.size() && i < 4; i++) {
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddresses.get(i));
//            connectToDevice(device, i);
//        }

        if (deviceAddresses != null && bluetoothAdapter != null) {
            for (int i = 0; i < deviceAddresses.size(); i++) {
                String address = deviceAddresses.get(i);
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                Log.e("Device Name",device.getName());

                // Assign each device to its corresponding TextView
//                if (i == 0) {
//                    deviceTextViews.put(address, dataTextView);  // First device -> dataTextView
//                } else if (i == 1) {
//                    deviceTextViews.put(address, dataTextView1); // Second device -> dataTextView1
//                }

                connectToDevice(device,i);
            }
        }
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void connectToDevice(BluetoothDevice device, int deviceIndex) {
//        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
//
//        try {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
//                return;
//            }
//
//            // Create a BluetoothSocket to connect with the given BluetoothDevice
//            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
//            bluetoothSocket.connect(); // Connect to the device
//
//            // If connection is successful, start a thread to listen for incoming data
//            new Thread(new DataReceiver(bluetoothSocket)).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            try {
//                bluetoothSocket.close(); // Close the socket if there was an error
//            } catch (IOException closeException) {
//                closeException.printStackTrace();
//            }
//        }

        // Avoid duplicate connections
        if (connectionManager.isDeviceConnected(device)) {
            Log.d("BluetoothConnection", "Device already connected: " + device.getAddress());
            return;
        }

        Log.d(TAG, "Connecting to device: " + device.getName() + " - " + device.getAddress());
        @SuppressLint("MissingPermission") BluetoothGatt bluetoothGatt = device.connectGatt(this, false, new CustomGattCallback(device.getAddress(),device.getName(),deviceIndex));
        connectedDevices.put(device.getAddress(), bluetoothGatt);

//        new Thread(() -> {
//            connectionManager.connect(device);
//
//            BluetoothSocket socket = connectionManager.getBluetoothSocket(device);
//            if (socket != null && socket.isConnected()) {
//                //TextView targetTextView = getTextViewByIndex(deviceIndex);
//
//                // Start DataReceiver with the selected TextView
//                DataReceiver dataReceiver = new DataReceiver(socket, deviceIndex, uiHandler, this::handleNewData);
//
//                new Thread(dataReceiver).start();
//                Log.d("BluetoothConnection", "Device " + deviceIndex + " connected");
//            } else {
//                Log.e("BluetoothConnection", "Failed to connect to device: " + device.getAddress());
//            }
//        }).start();
    }

    private class CustomGattCallback extends BluetoothGattCallback {
        private String deviceAddress;
        private String deviceName;

        private int index;

        CustomGattCallback(String deviceAddress, String devicename, int index) {
            this.deviceAddress = deviceAddress;
            this.deviceName = devicename;
            this.index = index;
        }

        @SuppressLint({"MissingPermission", "RestrictedApi"})
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server for device: " + deviceAddress);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w(TAG, "Disconnected from device: " + deviceAddress + ". Reconnecting...");
                connectToDevice(gatt.getDevice(),index);  // Immediate reconnection
            }
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered for device: " + deviceAddress);
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            enableNotifications(gatt, characteristic);
                        }
                    }
                }
            }
        }

        @SuppressLint({"MissingPermission", "RestrictedApi"})
        private void enableNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            gatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                Log.d(TAG, "Enabled notifications for device: " + deviceAddress);
            }
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            if (value != null && value.length > 0) {
                executorService.execute(() -> {

                    String lowerCaseName = deviceName.toLowerCase();
                    if (lowerCaseName.contains("fsr")) {
                        int intValue = bytesToInt(value);
                        String receivedData = String.valueOf(intValue);

                        Log.d(TAG, "Received data from " + deviceName + ": " + receivedData);

                        // Update the corresponding TextView for this device
                        runOnUiThread(() -> {
                            handleNewData(deviceName,receivedData);
                        });
                    }


                });
            }
        }
    }

    private void handleNewData(String deviceName, String value) {
        synchronized (this) {
            String lowerCaseName = deviceName.toLowerCase();
            if (lowerCaseName.contains("fsr")) {
                Log.e("Dynamometer Value",value);
            }


        }
    }

    private void handleDeviceData(int deviceIndex, float value) {

    }

    @SuppressLint("RestrictedApi")
    private int bytesToInt(byte[] bytes) {
        try {
            return Integer.parseInt(new String(bytes).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
