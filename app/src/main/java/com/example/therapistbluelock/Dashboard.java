package com.example.therapistbluelock;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import static com.example.therapistbluelock.MainActivity.savedUserName;
import static com.example.therapistbluelock.MainActivity.savedUserID;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;

public class Dashboard extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;
    private int res = R.drawable.login_akka;
    private LinearLayout linearLayout;
    private LinearLayout container; // Reference to the LinearLayout (previously CardView)
    private View leftDrawer; // Reference to the left drawer view
    private Toolbar toolbar; // Reference to the Toolbar
    OkHttpClient client;
    WebSocket webSocket;
    private float dX, dY; // Variables for tracking the dragging offset
    private RelativeLayout.LayoutParams toolbarParams; // Store the LayoutParams for repositioning

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        hideSystemUI();
// Initialize FirebaseApp before using Firebase services
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        // Initialize views
        container = findViewById(R.id.container);  // Reference to your LinearLayout (previously CardView)
        leftDrawer = findViewById(R.id.left_drawer); // Reference to your left drawer view
        toolbar = findViewById(R.id.toolbar); // Reference to the Toolbar
        initializeWebSocket();
        setSupportActionBar(toolbar);  // Ensure this is done before the drawer toggle

        HomeFragment homeFragment = HomeFragment.newInstance(); // Create the HomeFragment instance
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, homeFragment) // Replace the content frame with HomeFragment
                .commit(); //

        // Initialize DrawerLayout and set up listener
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout = findViewById(R.id.left_drawer);
        drawerLayout.setScrimColor(Color.TRANSPARENT);  // This makes the background scrim fully transparent
        linearLayout.setOnClickListener(null); // Remove conflicting onClickListener

        setActionBar();
        createMenuList();

        // Initialize ViewAnimator
        viewAnimator = new ViewAnimator<>(this, list, homeFragment, drawerLayout, this);
    }

    private void createMenuList() {
        list.add(new SlideMenuItem(ContentFragment.HOME, R.drawable.home_icon));
        list.add(new SlideMenuItem(ContentFragment.REGIME, R.drawable.calendar_icon));
        list.add(new SlideMenuItem(ContentFragment.CALL, R.drawable.call_icon));
        list.add(new SlideMenuItem(ContentFragment.SETTINGS, R.drawable.settings_icon));
        list.add(new SlideMenuItem(ContentFragment.LOGOUT, R.drawable.logout_icon));
    }

    private void setActionBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                adjustContainerMargin(true);
//                hideToolbar(); // Hide toolbar when drawer is opened
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                showToolbar(); // Show toolbar when drawer is closed
                linearLayout.removeAllViews(); // Clear views when the drawer closes
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0) {
                    viewAnimator.showMenuContent();
                }

                // Animate container movement with the drawer slide
                if (container != null) {
                    animateContainerMovement(slideOffset);
                }

                // Hide toolbar when container is moved or drawer is opened
                if (slideOffset > 0) {
//                    hideToolbar();
                } else {
                    showToolbar();
                }
            }
        };
        drawerLayout.addDrawerListener(drawerToggle); // Attach drawer listener
    }

    private void initializeWebSocket() {
        client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("wss://api-wo6.onrender.com/patients")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                super.onOpen(webSocket, response);
                System.out.println("WebSocket Connected");
                Log.e("WebSocket Message", "WebSocket Connected");

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
                    }else {
                        Log.d("WebSocket Message", "Flag value is not 1 or 2. No notification sent.");
                    }
                } catch (JSONException e) {
                    Log.e("WebSocket Message", "Error parsing JSON", e);
                }
            }


            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                System.out.println("Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                webSocket.close(1000, null);
                System.out.println("WebSocket Closing: " + reason);
                Log.e("WebSocket Closing", reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("WebSocket Error: " + t.getMessage());
                Log.e("WebSocket Error", t.getMessage());
            }
        });

        client.dispatcher().executorService().shutdown();
    }

    private void sendFlagNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "channel_flag_1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Flag 1 Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Doctor Assigned")
                .setContentText("Your doctor is ready!")
                .setSmallIcon(R.drawable.login_akka) // Replace with your actual icon
                .build();

        notificationManager.notify(1, notification); // Use a unique ID for this type of notification
        Log.d("Notification", "Flag 1 notification sent!");
    }

    private void adjustContainerMargin(boolean isDrawerOpen) {
        // Get the LayoutParams of the container (which is a LinearLayout inside DrawerLayout)
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) container.getLayoutParams();

        // Set the left margin when the drawer is open
        if (isDrawerOpen) {
            params.setMargins(5, params.topMargin, params.rightMargin, params.bottomMargin); // 5px margin for example
        } else {
            // Reset margin when the drawer is closed
            params.setMargins(0, params.topMargin, params.rightMargin, params.bottomMargin);
        }

        // Apply the updated layout parameters
        container.setLayoutParams(params);
    }

    private void animateContainerMovement(float slideOffset) {
        // Calculate the left drawer's width
        float drawerWidth = leftDrawer.getWidth();

        // Calculate the translation X for container (only move a little)
        float translationX = drawerWidth * slideOffset;

        // Apply translation to container
        ObjectAnimator animator = ObjectAnimator.ofFloat(container, "translationX", translationX);
        animator.setDuration(0); // Instant movement
        animator.start();
    }

    private void hideToolbar() {
        if (toolbar != null && toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);  // Hide the toolbar
        }
    }

    private void showToolbar() {
        if (toolbar != null && toolbar.getVisibility() == View.GONE) {
            toolbar.setVisibility(View.VISIBLE);  // Show the toolbar
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState(); // Sync the state of the drawer toggle after activity creation
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;  // Return false to hide the overflow menu (three dots)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the home button (arrow icon) click to close the drawer
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other options if needed
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        // Reveal animation
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        animator.start();

        // Replacing the fragment dynamically
        ContentFragment contentFragment = ContentFragment.newInstance(this.res);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (slideMenuItem.getName()) {
            case ContentFragment.HOME:
                HomeFragment homeFragment = HomeFragment.newInstance();
                transaction.replace(R.id.content_frame, homeFragment);
                break;
            case ContentFragment.REGIME:
                RegimeFragment regimeFragment = RegimeFragment.newInstance();
                transaction.replace(R.id.content_frame, regimeFragment);
                break;
            case ContentFragment.CALL:
                CallFragment callFragment = CallFragment.newInstance();
                transaction.replace(R.id.content_frame, callFragment);
                break;
            case ContentFragment.SETTINGS:
//                SettingsFragment settingsFragment = SettingsFragment.newInstance();
//                transaction.replace(R.id.content_frame, settingsFragment);
                break;
            case ContentFragment.LOGOUT:
                // Clear saved user UID and password from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("userID");  // Remove the saved user ID (email/username)
                editor.remove("userPassword");  // Remove the saved user password
                editor.apply();

                // Optionally, unregister the FCM token if required
                unregisterFCMToken();

                // Logout from CometChat
                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String successMessage) {
                        // CometChat logout successful
                        Log.d("CometChat", "CometChat logout successful");
                    }

                    @Override
                    public void onError(CometChatException e) {
                        // Handle error during CometChat logout
                        Log.e("CometChat", "CometChat logout failed: " + e.getMessage());
                    }
                });

                // Logout from Firebase
                FirebaseAuth.getInstance().signOut();

                // Navigate back to the DetailFrag_5 (login screen)
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Clear the back stack
                startActivity(intent);
                finish();  // Close the current Dashboard activity
                return screenShotable; // No need to replace fragment, just log out


            default:
                return replaceFragment(screenShotable, position);
        }

        // Commit the transaction to make sure the fragment is shown
        transaction.commit();
        return screenShotable;
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public void addViewToContainer(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 130); // Adds a margin of 16dp below each view

        view.setLayoutParams(layoutParams); // Apply the layout parameters to the view
        linearLayout.addView(view);
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }

    private void unregisterFCMToken() {
        // Assuming you have an API or method to unregister the token from your server
        // FirebaseInstanceId can be used to delete the token locally
        FirebaseMessaging.getInstance().deleteToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "FCM Token deleted successfully");
                    } else {
                        Log.e("FCM", "Failed to delete FCM token");
                    }
                });
    }
}
