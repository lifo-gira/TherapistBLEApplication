package com.example.therapistbluelock;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.zegocloud.zimkit.services.ZIMKit;
//import com.zegocloud.zimkit.services.ZIMKitConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
//import im.zego.zim.enums.ZIMErrorCode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText emailInputText, passwordEditText;

    public static String therapistusername, therapistuserid, therapistname;
    public static String savedUserID = "user_prefs";
    public static String savedUserName = "user_uid";
    Button loginButton;
    ImageView socialLoginImage;
    private final OkHttpClient client = new OkHttpClient();
    public static JSONArray completedata = new JSONArray();
    public static JSONArray patientdata = new JSONArray();

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    public static String usermail;


    public static int patflag = -3,oldpatients=0,newpatients=0;

    public static JSONObject selectedpatientdata = new JSONObject();
    public static JSONObject assessmentmain = new JSONObject();
    public static JSONObject assessmentexercise = new JSONObject();
    public static JSONArray selectedpatientassesementdata = new JSONArray();
    public static JSONArray selectedpatientexercisedata = new JSONArray();
    public static JSONObject exerciseobject = new JSONObject();
    public static JSONObject subexerciseobject = new JSONObject();
    public static JSONObject indiviexerciseobject = new JSONObject();

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;


    public static float patientheight;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        // Initialize FirebaseApp before using Firebase services
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        startService(serviceIntent); // Start WebSocket service
        // Initialize CometChat UI Kit
        String appID = "2679043e0d2ce72a"; // Replace with your App ID
        String region = "in"; // Replace with your App Region ("EU" or "US")
        String authKey = "3a80812c5b8b6208320802cf8223610a5dd4c524"; // Replace with your Auth Key

        UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder()
                .setRegion(region)
                .setAppId(appID)
                .setAuthKey(authKey)
                .subscribePresenceForAllUsers()
                .build();
        checkIfLoggedIn();
        // Get references to the TextInputEditText for email (username) and password

        checkPermissions();
        if(checkCameraPermission()){
            requestCameraPermission();
        }
        emailInputText = findViewById(R.id.emailInputText);
        passwordEditText = findViewById(R.id.passwordEditText);
        socialLoginImage = findViewById(R.id.socialLoginImage);

        // Retrieve saved user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        savedUserID = prefs.getString("userID", "");
        savedUserName = prefs.getString("userName", "");

        // If there is any saved user data, populate the fields
        if (!savedUserID.isEmpty()) {
            emailInputText.setText(savedUserID);
        }

        if (!savedUserName.isEmpty()) {
            emailInputText.setText(savedUserName);
        }

        // Get reference to the Login button
        loginButton = findViewById(R.id.loginButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        socialLoginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                    // After signing out, start the sign-in intent
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });
            }
        });

        CometChatUIKit.init(this, uiKitSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
                Log.d("CometChat", "UI Kit initialized successfully!");
                // Set OnClickListener to handle login action
                loginButton.setOnClickListener(v -> {
                    // Get the input values
                    String email = emailInputText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    // Validate the inputs (basic validation)
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    } else {

                        // Save the email (as userID and userName) to SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                        editor.putString("userID", email);  // Save the email as userID
                        editor.putString("userName", email);  // Save the email as userName
                        editor.putString("userPassword",password);
                        editor.apply();

                        // Set the email as user ID and username in the UI
                        emailInputText.setText(email);
                        emailInputText.setText(email);

                        loginButton.setEnabled(false);
                        String userId = emailInputText.getText().toString().trim();
                        String userPassword = passwordEditText.getText().toString().trim();
                        loginUser(userId, userPassword);

                        // Proceed to the next activity (e.g., Dashboard)
//                    Intent intent = new Intent(DetailFrag_5.this, Dashboard.class);
//                    intent.putExtra("email", email); // Optionally pass email to the next activity
//                    startActivity(intent);


                    }
                });
                // Check if notifications are enabled
                checkNotifications();
            }

            @Override
            public void onError(com.cometchat.chat.exceptions.CometChatException e) {
                Log.e("CometChat", "Initialization failed", e);
            }
        });
    }

    private void checkIfLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String savedUserID = prefs.getString("userID", null); // Corrected: Use userID as the key
        String savedUserPassword = prefs.getString("userPassword", null);
        if (savedUserID != null) {
            // User is logged in, proceed with auto login
            loginUser(savedUserID, savedUserPassword);
        }
    }


    // Check if notifications are enabled
    private void checkNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (!notificationManager.areNotificationsEnabled()) {
            // Notifications are disabled, prompt the user to enable them
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Enable Notifications")
                    .setMessage("Please enable notifications to stay updated.")
                    .setPositiveButton("Enable", (dialog, which) -> {
                        // Redirect to the app's notification settings
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }


    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    public void onClick(View v) {
        // Placeholder for any other buttons or actions you might want to handle
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, fetch the email
            String email = account.getEmail();
            //Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();
            Log.d("GoogleSignIn", "Email: " + email);
            if (email != null) {
                usermail = email;
                loginmail(email);
                //startActivity(new Intent(LoginActivity.this,UserSelection.class));
            }

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void loginUser(String userId, String userPassword) {
        String url = "https://api-wo6.onrender.com/login";

        RequestQueue queue = Volley.newRequestQueue(this);

        CometChatUIKit.login(userId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("CometChat", "Login Successful: " + user.toString());
//                runOnUiThread(() -> Toast.makeText(DetailFrag_5.this, "Login Successful", Toast.LENGTH_SHORT).show());

                // Register the FCM token after successful login
                registerFCMToken();
            }

            @Override
            public void onError(com.cometchat.chat.exceptions.CometChatException e) {
                if (e.getMessage().contains("ERR_UID_ALREADY_EXISTS")) {
                    // If user exists, just login
                    Log.d("CometChat", "User already exists, logging in...");
                    // Register the FCM token after successful login
                    registerFCMToken();

                } else {
                    // If login fails because the user does not exist, create the user
                    Log.e("CometChat", "Login failed, user doesn't exist. Creating user...");
                    createUser(userId);
                }
            }
        });

        JSONObject postParams = new JSONObject();
        try {
            postParams.put("user_id", userId);
            postParams.put("password", userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Login Object", String.valueOf(postParams));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api-wo6.onrender.com/login?user_id=" + userId + "&password=" + userPassword, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String username = response.getString("user_id");
                            String password = response.getString("password");
                            String uid = response.getString("_id");
                            String pat = response.getString("name");
                            String type = response.getString("type");
                            if (username.equals(userId) && password.equals(userPassword) && type.equals("nurse")) {
                                Log.e("User UID", uid);
                                therapistusername = username;
                                therapistuserid = uid;
                                therapistname = pat;
                                Log.e("Email Response", String.valueOf(response));
                                fetchPatients(uid, username);

                            } else {
                                loginButton.setEnabled(true);
                                Log.e("Login Response", username);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loginButton.setEnabled(true);
                if (volleyError instanceof com.android.volley.ParseError) {
                    Toasty.error(MainActivity.this, "Enter Correct UserName and Password", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(MainActivity.this, "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                }
                Log.e("Login Error", String.valueOf(volleyError));
            }

        });

        queue.add(jsonObjectRequest);
    }

    private void createUser(String uid) {
        String name = uid; // Assuming the name is the same as the username
        String url = "https://2679043e0d2ce72a.api-in.cometchat.io/v3/users";
        MediaType mediaType = MediaType.parse("application/json");

        // Construct the request body with uid and name
        String requestBodyString = "{\"uid\":\"" + uid + "\",\"name\":\"" + name + "\"}";

        RequestBody body = RequestBody.create(mediaType, requestBodyString);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("apikey", "f3ce71f8b5286e06942e060f581ab06cf7220032") // Replace with your API key
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("CometChat", "Error creating user: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("CometChat", "User created successfully: " + response.body().string());
                    SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    String storedPassword = sharedPreferences.getString("password", "");
                    // Proceed with login after successful user creation
                    loginUser(uid, storedPassword);
                } else {
                    Log.e("CometChat", "User creation failed: " + response.body().string());
                }
            }
        });
    }

    // Register the FCM token
    private void registerFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "Failed to get FCM token");
                return;
            }

            // Get the FCM token
            String token = task.getResult();
            Log.d("FCM", "FCM Token: " + token);

            // Register the token with CometChat
            CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.e("onSuccessPN: ", s);
                }

                @Override
                public void onError(com.cometchat.chat.exceptions.CometChatException e) {
                    Log.e("onErrorPN: ", e.getMessage());
                }
            });
        });
    }

    private void fetchPatients(String uid, String username) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api-wo6.onrender.com/patient-details/all",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Pat", response);
                            completedata = new JSONArray(response);

                            ;
                            for (int i = 0; i < completedata.length(); i++) {
                                JSONObject jsonObject = completedata.getJSONObject(i);
                                Log.e("Patients Details", String.valueOf(jsonObject));
                                String therapistid = jsonObject.getString("therapist_id");
                                String therapistassigned = jsonObject.getString("therapist_assigned");
                                if (therapistid.equalsIgnoreCase(uid) && therapistassigned.equalsIgnoreCase(username)) {
                                    patientdata.put(jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent2 = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent2);
                        intent2.putExtra("email", therapistusername);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void loginmail(String email) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api-wo6.onrender.com/users/" + email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response1) {
                        String username, password;
                        try {
                            JSONObject response = new JSONObject(response1);
                            username = response.getString("user_id");
                            password = response.getString("password");
                            String uid = response.getString("_id");
                            String pat = response.getString("name");
                            fetchPatients(uid, username);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);


    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.BLUETOOTH_SCAN);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }
}
