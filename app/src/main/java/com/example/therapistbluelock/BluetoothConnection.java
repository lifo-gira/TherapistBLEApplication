package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BluetoothConnection extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private DeviceAdapter availableDevicesAdapter;
    private ListView pairedListView;
    private RecyclerView availableRecyclerView;
    private List<String> availableDevicesList = new ArrayList<>();
    private ArrayList<String> selectedDevices = new ArrayList<>();
    private ArrayList<String> selectedDevicesnames = new ArrayList<>();
    ArrayList<String> pairedDevices = new ArrayList<>();

    private Set<String> pairedDeviceAddresses = new HashSet<>();
    private ImageView downArrow,backnavigation;
    private static final String TAG = "BluetoothApp";
    private boolean isDeviceConnected;

    TextView therapistname;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bluetooth_connection);
        hideSystemUI();
        OnBackPressedCallback callback1 = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback1);

        ImageView concentricCircles = findViewById(R.id.concentric_circles);

        // Load animations
        final Animation expandFadeOut = AnimationUtils.loadAnimation(this, R.anim.expand_fade_out);
        final Animation shrinkFadeIn = AnimationUtils.loadAnimation(this, R.anim.shrink_fade_in);

        // Set up the animation listener to repeat the sequence
        expandFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Optionally, perform actions when the animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the shrink and fade in animation after the expand animation ends
                concentricCircles.startAnimation(shrinkFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Optionally, perform actions when the animation repeats
            }
        });

        therapistname = findViewById(R.id.therapistname);
        therapistname.setText(MainActivity.therapistname);
        shrinkFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Optionally, perform actions when the animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the expand and fade out animation after the shrink animation ends
                concentricCircles.startAnimation(expandFadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Optionally, perform actions when the animation repeats
            }
        });

        // Start the first animation
        concentricCircles.startAnimation(expandFadeOut);

        backnavigation = findViewById(R.id.backnavigation);
        backnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothConnection.this,Dashboard.class);
                startActivity(intent);
            }
        });
        downArrow = findViewById(R.id.down_arrow);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevicesAdapter = new ArrayAdapter<String>(
                this,
                R.layout.simple_list_item_paired,
                R.id.paired_checked_text,
                pairedDevices) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_paired, parent, false);
                }

                CheckedTextView checkedTextView = convertView.findViewById(R.id.paired_checked_text);
                String device = pairedDevices.get(position);
                String address = device.substring(device.length() - 17);

                checkedTextView.setText(device);
                checkedTextView.setChecked(selectedDevices.contains(address));

                // Set the text color to #516981
                checkedTextView.setTextColor(Color.parseColor("#ffffff"));

                return convertView;
            }
        };
        pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesAdapter);
        availableDevicesList = new ArrayList<>(); // Initialize with your data

        availableDevicesAdapter = new DeviceAdapter(availableDevicesList, this::onAvailableDeviceClick);
        availableRecyclerView = findViewById(R.id.available_devices);

        int paddingDp = 20; // Padding in dp
        int radiusDp = 50; // Radius in dp
        RandomLayoutManager layoutManager = new RandomLayoutManager(this, radiusDp, paddingDp);
        availableRecyclerView.setLayoutManager(layoutManager);
        availableRecyclerView.setAdapter(availableDevicesAdapter);

        // Attach ItemTouchHelper to RecyclerView
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(availableDevicesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(availableRecyclerView);

        ImageView refreshButton = findViewById(R.id.bluetooth_icon);
        // Load the blue pulse animation
        final Animation bluePulse = AnimationUtils.loadAnimation(this, R.anim.blue_pulse);

        refreshButton.setOnClickListener(v -> {
            if (bluetoothAdapter.isEnabled()) {
                // Clear the existing available devices
                availableDevicesList.clear();
                availableDevicesAdapter.notifyDataSetChanged();

                // List paired devices and start discovery
                listPairedDevices();
                startDiscovery();

                // Start the blue pulse animation
                v.startAnimation(bluePulse);
            } else {
                new AlertDialog.Builder(BluetoothConnection.this)
                        .setTitle("Bluetooth Required")
                        .setMessage("Please turn on Bluetooth to refresh the device lists.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        if (bluetoothAdapter.isEnabled()) {
            listPairedDevices();
            startDiscovery();
        } else {
            new AlertDialog.Builder(BluetoothConnection.this)
                    .setTitle("Bluetooth Required")
                    .setMessage("Please turn on Bluetooth to refresh the device lists.")
                    .setPositiveButton("OK", null)
                    .show();
        }
        // Add scroll listener to pairedListView
        pairedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No action needed here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Check if we have scrolled to the end
                boolean atEnd = (firstVisibleItem + visibleItemCount >= totalItemCount);
                if (atEnd) {
                    downArrow.setVisibility(View.GONE);
                } else {
                    downArrow.setVisibility(View.VISIBLE);
                }
            }
        });

        pairedListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        pairedListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                String item = (String) pairedListView.getItemAtPosition(position);

                String address = item.substring(item.length() - 17);
                String name = item.substring(0,item.length() - 17);
                Log.e("Device Name",name);

                if (checked) {
                    selectedDevices.add(address);
                    selectedDevicesnames.add(name);
                } else {
                    selectedDevices.remove(address);
                    selectedDevicesnames.remove(name);
                }

                // Update the CheckedTextView to reflect the checked state
                View view = pairedListView.getChildAt(position - pairedListView.getFirstVisiblePosition());
                if (view != null) {
                    CheckedTextView checkedTextView = view.findViewById(R.id.paired_checked_text);
                    checkedTextView.setChecked(checked);
                }

                mode.setTitle(selectedDevices.size() + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_connect) {
//                    Log.e("Flag value in bluetooth", String.valueOf(DetailFrag_5.flag) + DetailFrag_4.flag1);

                    // Start DetailCollection activity and pass the data
                    Intent intent = new Intent(BluetoothConnection.this, DetailFrag_5.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("device_addresses", TextUtils.join(",", selectedDevices)); // Convert to a comma-separated string
                    editor.apply();

                    intent.putStringArrayListExtra("device_addresses", selectedDevices);
                    intent.putStringArrayListExtra("device_names", selectedDevicesnames);

                    // Pass the milestone identifier (e.g., 5 for DetailFrag_5)
                    intent.putExtra("fragment_milestone", 5);

                    SharedPreferences prefs = getSharedPreferences("BluetoothPrefs", MODE_PRIVATE);
                    isDeviceConnected = prefs.getBoolean("isConnected", false);
                    Log.e("Device connected to mobile", String.valueOf(isDeviceConnected));
                    startActivity(intent);
                    mode.finish();
                    return true;
                }
                return false;
            }


            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedDevices.clear();

                // Optionally, uncheck all items here if needed
                for (int i = 0; i < pairedListView.getChildCount(); i++) {
                    View view = pairedListView.getChildAt(i);
                    if (view != null) {
                        CheckedTextView checkedTextView = view.findViewById(R.id.paired_checked_text);
                        checkedTextView.setChecked(false);
                    }
                }
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }




    private void listPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDeviceAddresses.clear();  // Clear previous paired device addresses
        if (pairedDevices.size() > 0) {
            pairedDevicesAdapter.clear();
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceAddresses.add(device.getAddress());  // Add paired device address
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    private void startDiscovery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted; discovery will not start
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery(); // Cancel any ongoing discovery
        }
        bluetoothAdapter.startDiscovery(); // Start new discovery
    }



    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        private boolean discoveryFinished = false;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();

                    // Skip adding paired devices and devices with null or empty names
                    if (deviceName != null && !deviceName.trim().isEmpty() && !pairedDeviceAddresses.contains(deviceAddress)) {
                        String deviceInfo = deviceName + "\n" + deviceAddress;

                        if (!availableDevicesList.contains(deviceInfo)) {
                            availableDevicesList.add(deviceInfo);
                            availableDevicesAdapter.notifyItemInserted(availableDevicesList.size() - 1);
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                discoveryFinished = true;
                if (availableDevicesList.isEmpty()) {
                    Log.e("No available devices found","");
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress();

                        // Skip adding devices with null or empty names
                        if (deviceName != null && !deviceName.trim().isEmpty()) {
                            String deviceInfo = deviceName + "\n" + deviceAddress;

                            availableDevicesList.remove(deviceInfo);
                            availableDevicesAdapter.notifyDataSetChanged();
                            pairedDevicesAdapter.add(deviceInfo);
                            Toast.makeText(BluetoothConnection.this, "Device paired: " + deviceName, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                listPairedDevices();
                startDiscovery();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onAvailableDeviceClick(String deviceAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device.createBond()) {
            Toast.makeText(BluetoothConnection.this, "Pairing with " + device.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BluetoothConnection.this, "Pairing failed with " + device.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }
}