package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PatientListFragment extends Fragment implements AssignedPatientListAdapter.OnReportIconClickListener, NewPatientListAdapter.OnItemClickListener {

    private RecyclerView patientsAssignedRecycler, newPatientsRecycler;
    private AssignedPatientListAdapter assignedAdapter;
    private NewPatientListAdapter newPatientsAdapter;
    private List<AssignedPatientList> assignedPatientList = new ArrayList<>();
    private List<NewPatientList> newPatientList = new ArrayList<>();

    List<String> patientname = new ArrayList<>();
    AutoCompleteTextView auto_complete;
    ArrayAdapter<String> adapter;

    TextView profilename;







    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button logic
                // Example: Navigate back or show a confirmation dialog
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        // Attach the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        profilename = view.findViewById(R.id.profilename);
        profilename.setText(MainActivity.therapistname);
        // Initialize Assigned Patients RecyclerView
        patientsAssignedRecycler = view.findViewById(R.id.patients_assigned);
        patientsAssignedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Data and Adapter for Assigned Patients
//        assignedPatientList = getAssignedPatientData();
        assignedAdapter = new AssignedPatientListAdapter(assignedPatientList, this);
        patientsAssignedRecycler.setAdapter(assignedAdapter);

        int space = getResources().getDimensionPixelSize(R.dimen.recycler_assigned_patients); // Define space in dimens.xml
        patientsAssignedRecycler.addItemDecoration(new ItemSpacingDecoration(space));

        // Initialize New Patients RecyclerView
        newPatientsRecycler = view.findViewById(R.id.new_patients_list);

// Apply GridLayoutManager to display items in a 2x2 grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        newPatientsRecycler.setLayoutManager(gridLayoutManager);

// Set Adapter for New Patients
//        newPatientList = getNewPatientData();
        newPatientsAdapter = new NewPatientListAdapter(newPatientList,this);
        newPatientsRecycler.setAdapter(newPatientsAdapter);

// Define both vertical and horizontal spacing
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_item_vertical_spacing); // Larger gap for vertical
        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_item_horizontal_spacing); // Smaller gap for horizontal

// Apply custom ItemDecoration
        newPatientsRecycler.addItemDecoration(new ItemSpacingDecorationVertical(verticalSpacing, horizontalSpacing));
        auto_complete = view.findViewById(R.id.auto_complete);
        adapter = new ArrayAdapter<>(requireContext(), R.layout.drop_list, patientname);
        auto_complete.setAdapter(adapter);


        fetchPatients(MainActivity.therapistuserid,MainActivity.therapistusername);

        auto_complete.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedpatient = (String) parent.getItemAtPosition(position);

            for(int i=0; i<MainActivity.patientdata.length(); i++){
                try {
                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                    if(jsonObject.getInt("flag")>=0){
                        if(selectedpatient.equalsIgnoreCase(jsonObject.getString("patient_name")) ){
                            loadData1(jsonObject.getString("patient_id"));
                            Fragment reportFragment = new OverviewFragment(); // Replace with your actual fragment class
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_frame, reportFragment); // Replace with the appropriate container ID
                            transaction.addToBackStack(null); // Add to back stack if you want to return
                            transaction.commit();
                        }

                    }
                    else if(jsonObject.getInt("flag") <= -2){
                        if(selectedpatient.equalsIgnoreCase(jsonObject.getString("user_id")) ){
                            Log.e("Lunar Eclipse", position + " / " + String.valueOf(jsonObject.getInt("flag")));
                            HomeFragment.uname = jsonObject.getString("user_id");
                            HomeFragment.patusername =  HomeFragment.uname;
                            HomeFragment.userid = jsonObject.getString("patient_id");
                            Intent intent = new Intent(getContext(), CollectionDetails.class);
                            startActivity(intent);
                        }

                    }
                    else if(jsonObject.getInt("flag") == -1){
                        if(selectedpatient.equalsIgnoreCase(jsonObject.getString("patient_name")) ){
                            JSONObject jsonObject1 = jsonObject.getJSONObject("PersonalDetails");
                            MainActivity.patientheight = jsonObject1.getInt("Height");
                            HomeFragment.uname = jsonObject.getString("user_id");
                            HomeFragment.userid = jsonObject.getString("patient_id");
                            HomeFragment.patientnam = jsonObject.getString("patient_name");
                            Intent intent = new Intent(getContext(), BluetoothConnection.class);
                            startActivity(intent);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        View addIcon = view.findViewById(R.id.add_icon);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom dialog when Add New Patient icon is clicked
                showAddPatientDialog();
            }
        });

        return view;
    }

    @SuppressLint("MissingInflatedId")
    private void showAddPatientDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_patient, null);

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setCancelable(false); // Disable dismissing the dialog by clicking outside

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Get references to the TextInputEditText fields inside TextInputLayout
        TextInputEditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        TextInputEditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        TextInputEditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmpasswordEditText);
        TextInputEditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        Button addPatientButton = dialogView.findViewById(R.id.add_patient_button);
        ImageView closeImageView = dialogView.findViewById(R.id.close); // Reference to the close button


        // Handle the close button click
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the close icon is clicked
                dialog.dismiss();
            }
        });

        // Handle the Add Patient button click
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input from the TextInputEditText fields
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                // Validate the input (optional validation, depending on your requirement)
//                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!password.equals(confirmPassword)) {
//                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                // Dismiss the dialog after processing

                StringBuilder errorBuilder = new StringBuilder();

                if (username.isEmpty()) {
                    errorBuilder.append("Username is required.\n");
                }
                if (password.isEmpty()) {
                    errorBuilder.append("Password is required.\n");
                }
                if (confirmPassword.isEmpty()) {
                    errorBuilder.append("Confirm Password is required.\n");
                }
                if (email.isEmpty()) {
                    errorBuilder.append("Email is required.\n");
                }
                if (!password.equals(confirmPassword)) {
                    errorBuilder.append("Password and Confirm Password must match.\n");
                }

                // Check if there are any errors
                if (errorBuilder.length() > 0) {
                    // Show all errors at once
                    Toasty.error(getContext(), errorBuilder.toString().trim(), Toast.LENGTH_LONG).show();
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(getContext());
                JSONObject postParams = new JSONObject();
                try {
                    postParams.put("type", "patient");
                    postParams.put("name", username);
                    postParams.put("user_id", username);
                    HomeFragment.patusername = username;
                    postParams.put("password", password);
                    HomeFragment.patpassword = password;
                    postParams.put("email", email);
                    postParams.put("data", new JSONArray());
                    postParams.put("videos", new JSONArray());
                    postParams.put("therapist_assigned", MainActivity.therapistusername);
                    postParams.put("therapist_id", MainActivity.therapistuserid);
                    postParams.put("doctor", "");
                    postParams.put("doctor_id", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Login Object", String.valueOf(postParams));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api-wo6.onrender.com/create-patient", postParams,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    loginUser(HomeFragment.patusername,HomeFragment.patpassword);
                                    fetchPatients(MainActivity.therapistuserid,MainActivity.therapistusername);
                                    newpatpopup();

                                    dialog.dismiss();

                                    // Create an Intent to navigate to the CollectionDetails activity
//                                    Intent intent = new Intent(getContext(), CollectionDetails.class);
//                                    startActivity(intent);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            Toasty.error(getContext(), "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                            Log.e("Login Error", String.valueOf(volleyError));
                        }

                    }){
                        @Override
                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                            // Fetch the response code
                            int statusCode = response.statusCode;
                            Log.d("Response Code", "Status Code: " + statusCode);

                            // Optionally handle the status code here if needed

                            // Call the superclass method to ensure normal behavior
                            return super.parseNetworkResponse(response);
                        }
                    };


                    queue.add(jsonObjectRequest);

            }
        });
    }


    @Override
    public void onReportIconClick(String pid) {

        loadData1(pid);
        // Handle navigation to a new fragment when the report icon is clicked
        Fragment reportFragment = new OverviewFragment(); // Replace with your actual fragment class
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, reportFragment); // Replace with the appropriate container ID
        transaction.addToBackStack(null); // Add to back stack if you want to return
        transaction.commit();
    }

    private void loadData1(String pid) {
        for (int i = 0; i < MainActivity.patientdata.length(); i++) {
            try {
                JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                if (pid.equalsIgnoreCase(jsonObject.getString("patient_id"))) {
                    MainActivity.selectedpatientdata = MainActivity.patientdata.getJSONObject(i);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetchPatients(String uid, String username) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api-wo6.onrender.com/patient-details/all",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Pat", response);
                            MainActivity.completedata = new JSONArray();
                            MainActivity.patientdata = new JSONArray();
                            MainActivity.completedata = new JSONArray(response);

                            for(int i=0; i< MainActivity.completedata.length(); i++){
                                JSONObject jsonObject =  MainActivity.completedata.getJSONObject(i);
                                String therapistid = jsonObject.getString("therapist_id");
                                String therapistassigned = jsonObject.getString("therapist_assigned");
                                if(therapistid.equalsIgnoreCase(uid) && therapistassigned.equalsIgnoreCase(username)){
                                    MainActivity.patientdata.put(jsonObject);
                                }
                            }

                            for (int i = 0; i < MainActivity.patientdata.length(); i++) {
                                try {
                                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);

                                    if (jsonObject.getInt("flag") >= 0) {
                                        patientname.add(jsonObject.getString("patient_name"));
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("PersonalDetails");
                                        JSONArray jsonArray = jsonObject1.getJSONArray("pain_indication");
                                        StringBuilder stringBuilder = new StringBuilder();

                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            stringBuilder.append(jsonArray.getString(j));
                                            if (i < jsonArray.length() - 1) {
                                                stringBuilder.append(", "); // Custom delimiter
                                            }
                                        }
                                        String pain = stringBuilder.toString();
                                        assignedPatientList.add(new AssignedPatientList(jsonObject.getString("patient_name"), pain, jsonObject.getString("patient_id"), R.drawable.user_image, Integer.parseInt(jsonObject1.getString("Age")), jsonObject1.getString("Gender"),jsonObject.getString("user_id")));
                                    }
                                    else if(jsonObject.getInt("flag") == -1){
                                        patientname.add(jsonObject.getString("patient_name"));
                                        MainActivity.newpatients++;
                                    }
                                    else {
                                        patientname.add(jsonObject.getString("user_id"));
                                        MainActivity.newpatients++;
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            assignedAdapter.notifyDataSetChanged();

                            for (int i = 0; i < MainActivity.patientdata.length(); i++) {
                                try {
                                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                                    if (jsonObject.getInt("flag") < 0 && jsonObject.getInt("flag") >= -1) {
                                        newPatientList.add(new NewPatientList(jsonObject.getString("patient_name"), R.drawable.user_image, jsonObject.getString("user_id"),jsonObject.getString("user_id")));
                                    }
                                    else if(jsonObject.getInt("flag") < -1){
                                        newPatientList.add(new NewPatientList(jsonObject.getString("user_id"), R.drawable.user_image, jsonObject.getString("patient_id"),jsonObject.getString("user_id")));
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            newPatientsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }


    @Override
    public void onItemClick(int position) {

        try {
            MainActivity.patientdata = new JSONArray();

            for(int i=0; i< MainActivity.completedata.length(); i++){
                JSONObject jsonObject =  MainActivity.completedata.getJSONObject(i);
                String therapistid1 = jsonObject.getString("therapist_id");
                String therapistassigned = jsonObject.getString("therapist_assigned");
                HomeFragment.therapistid=therapistid1;
                HomeFragment.therapistname=therapistassigned;
                if(therapistid1.equalsIgnoreCase(MainActivity.therapistuserid) && therapistassigned.equalsIgnoreCase(MainActivity.therapistusername)){
                    MainActivity.patientdata.put(jsonObject);
                }
            }

            for (int i = 0; i < MainActivity.patientdata.length(); i++) {
                try {
                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);

                    if(newPatientList.get(position).getPatid().equalsIgnoreCase(jsonObject.getString("user_id"))) {
                        if (jsonObject.getInt("flag") <= -2) {
                            Log.e("Lunar Eclipse", position + " / " + String.valueOf(jsonObject.getInt("flag")));
                            HomeFragment.uname = jsonObject.getString("user_id");
                            HomeFragment.patusername = HomeFragment.uname;
                            HomeFragment.userid = jsonObject.getString("patient_id");
                            Intent intent = new Intent(getContext(), CollectionDetails.class);
                            startActivity(intent);
                        }
                        else if (jsonObject.getInt("flag") == -1) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("PersonalDetails");
                            MainActivity.patientheight = jsonObject1.getInt("Height");
                            HomeFragment.uname = jsonObject.getString("user_id");
                            HomeFragment.userid = jsonObject.getString("patient_id");
                            HomeFragment.patientnam = jsonObject.getString("patient_name");
                            Intent intent = new Intent(getContext(), BluetoothConnection.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            newPatientsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void newpatpopup(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.newpatpopup, null);

        TextView msg = dialogView.findViewById(R.id.dialog_message);
        Button button_no = dialogView.findViewById(R.id.button_no);
        Button button_yes = dialogView.findViewById(R.id.button_yes);

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setCancelable(false); // Disable dismissing the dialog by clicking outside

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Dashboard.class);
                startActivity(intent);
            }
        });
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CollectionDetails.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String userId, String userPassword) {
        String url = "https://api-wo6.onrender.com/login";

        RequestQueue queue = Volley.newRequestQueue(getContext());


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
                            if (username.equals(userId) && password.equals(userPassword) && type.equals("patient")) {
                                Log.e("User UID", uid);
                                HomeFragment.userid = uid;

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError instanceof com.android.volley.ParseError) {
                    Toasty.error(getContext(), "Enter Correct UserName and Password", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                }
                Log.e("Login Error", String.valueOf(volleyError));
            }

        });

        queue.add(jsonObjectRequest);
    }
}
