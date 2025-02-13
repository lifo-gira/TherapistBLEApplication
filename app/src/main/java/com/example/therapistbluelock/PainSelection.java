package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class PainSelection extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private ChipGroup chipGroup;
    private String[] suggestions = {"Left shoulder pain", "Ankle pain", "Knee pain"};
    public static int flag1 = -1;
    public static JSONArray selectedpains = new JSONArray();
    Button submit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pain_selection);
        hideSystemUI();
        submit = findViewById(R.id.submit);

        autoCompleteTextView = findViewById(R.id.auto_complete1);
        chipGroup = findViewById(R.id.chipGroup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_list, suggestions);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedPain = (String) parent.getItemAtPosition(position);
            addChip(selectedPain);
            autoCompleteTextView.setText(""); // Clear the text after selecting
            updateSelectedPains();
        });

        submit.setOnClickListener(view -> {
                if(MainActivity.patflag == -3){
                    postdata(HomeFragment.userid);
                }
                else{
                    RequestQueue queue = Volley.newRequestQueue(this);
                    JSONObject postParams = new JSONObject();
                    try {
                        postParams.put("user_id", HomeFragment.patusername);
                        postParams.put("password", HomeFragment.patpassword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api-wo6.onrender.com/login?user_id="+HomeFragment.patusername+"&password="+HomeFragment.patpassword, postParams,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        String username = response.getString("user_id");
                                        String password = response.getString("password");
                                        String uid = response.getString("_id");
                                        String pat = response.getString("name");
                                        String type=response.getString("type");
                                        if(username.equals(HomeFragment.patusername) && password.equals(HomeFragment.patpassword) && type.equals("patient")){
                                            postdata(uid);
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if(volleyError instanceof com.android.volley.ParseError){
                                Toasty.error(PainSelection.this, "Enter Correct UserName and Password", Toast.LENGTH_SHORT, true).show();
                            }else {
                                Toasty.error(PainSelection.this, "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                            }
                            Log.e("Login Error", String.valueOf(volleyError));
                        }

                    });

                    queue.add(jsonObjectRequest);
                }
        });
    }

    private void postdata( String uid){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api-wo6.onrender.com/patient-info/"+uid;
        JSONObject postData = new JSONObject();
        HomeFragment.userid=uid;
        try {

            StringBuilder errorBuilder = new StringBuilder();
Log.e("Pain selection",uid + " / " + HomeFragment.therapistname);
            // Add data to postData with validation
            if (HomeFragment.patusername == null || HomeFragment.patusername.isEmpty()) {
                errorBuilder.append("Patient username is required.\n");
            } else {
                postData.put("user_id", HomeFragment.patusername);
            }

            postData.put("unique_id", ""); // Assuming this can be empty

            if (uid == null || uid.isEmpty()) {
                errorBuilder.append("Patient ID is required.\n");
            } else {
                postData.put("patient_id", uid);
            }

            postData.put("doctor_id", ""); // Assuming this can be empty

            if (MainActivity.therapistuserid == null || MainActivity.therapistuserid.isEmpty()) {
                errorBuilder.append("Therapist user ID is required.\n");
            } else {
                postData.put("therapist_id", MainActivity.therapistuserid);
            }


            HomeFragment.patientnam = CollectionDetails.patientname;
            if (CollectionDetails.patientname == null || CollectionDetails.patientname.isEmpty()) {
                errorBuilder.append("Patient Name is required.\n");
            } else {
                postData.put("patient_name", CollectionDetails.patientname);
            }

            postData.put("profession", ""); // Assuming this can be empty

            // Add data to PersonalDetails with validation
            JSONObject personalDetails = new JSONObject();

            if (CollectionDetails.doreg == null || CollectionDetails.doreg.isEmpty()) {
                errorBuilder.append("Date of registration is required.\n");
            } else {
                personalDetails.put("DORegn", CollectionDetails.doreg);
            }

            if (CollectionDetails.accidentinfo == null || CollectionDetails.accidentinfo.isEmpty()) {
                errorBuilder.append("Accident information is required.\n");
            } else {
                personalDetails.put("Accident", CollectionDetails.accidentinfo);
            }

            if (CollectionDetails.gender == null || CollectionDetails.gender.isEmpty()) {
                errorBuilder.append("Gender is required.\n");
            } else {
                personalDetails.put("Gender", CollectionDetails.gender);
            }

            if(selectedpains.length() == 0){
                errorBuilder.append("Pain Not Selected.\n");
            } else {
                personalDetails.put("pain_indication", selectedpains); // Default value, no validation required
            }

            if (CollectionDetails.bloodgroup == null || CollectionDetails.bloodgroup.isEmpty()) {
                errorBuilder.append("Blood group is required.\n");
            } else {
                personalDetails.put("Blood_Group", CollectionDetails.bloodgroup);
            }

            if (CollectionDetails.weightinfo == 0.0f) {
                errorBuilder.append("Weight information is required and cannot be zero.\n");
            } else {
                personalDetails.put("Weight", CollectionDetails.weightinfo);
            }

            if (CollectionDetails.bmiinfo == 0.0f) {
                errorBuilder.append("BMI information is required and cannot be zero.\n");
            } else {
                personalDetails.put("BMI", CollectionDetails.bmiinfo);
            }

            if (CollectionDetails.heightinfo == 0.0f) {
                errorBuilder.append("Height information is required and cannot be zero.\n");
            } else {
                personalDetails.put("Height", CollectionDetails.heightinfo);
            }
            if (CollectionDetails.age == 0) {
                errorBuilder.append("Age is required.\n");
            } else {
                personalDetails.put("Age", CollectionDetails.age);
            }
            if (CollectionDetails.dob == null || CollectionDetails.dob.isEmpty()) {
                errorBuilder.append("Date of birth is required.\n");
            } else {
                personalDetails.put("DOB", CollectionDetails.dob);
            }

            // Add personalDetails to postData
            postData.put("PersonalDetails", personalDetails);
            postData.put("flag",-1);
            if (MainActivity.therapistusername == null || MainActivity.therapistusername.isEmpty()) {
                errorBuilder.append("Therapist user Name is required.\n");
            } else {
                postData.put("therapist_assigned",MainActivity.therapistusername);
            }



            // Check if there are any errors
            if (errorBuilder.length() > 0) {
                // Show all errors at once
                Toasty.error(PainSelection.this, errorBuilder.toString().trim(), Toast.LENGTH_LONG).show();
                return; // Stop further execution
            }

            // Proceed with the postData (e.g., sending it to the server)
            Log.d("postData", postData.toString());

        }
        catch (JSONException e) {
            // Handle JSON exception
            e.printStackTrace();
            Toasty.error(PainSelection.this, "An error occurred while creating the request.", Toast.LENGTH_SHORT).show();
        }


        CustomJsonObjectRequest patchRequest = new CustomJsonObjectRequest(
                Request.Method.PATCH,
                url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Patient Info patch", String.valueOf(response));
                        MainActivity.patflag = -1;
                        newpatpopup();
//                        Intent intent = new Intent(CollectionDetails.this, BluetoothConnection.class);
//                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PainSelection.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("Patch Data error", String.valueOf(error));
                    }
                }
        );

        queue.add(patchRequest);
    }

    private void newpatpopup(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.newpatpopup, null);

        TextView msg = dialogView.findViewById(R.id.dialog_message);
        Button button_no = dialogView.findViewById(R.id.button_no);
        Button button_yes = dialogView.findViewById(R.id.button_yes);

        msg.setText("Patient Details Added Successfully!");

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false); // Disable dismissing the dialog by clicking outside

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PainSelection.this,Dashboard.class);
                startActivity(intent);
            }
        });
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PainSelection.this,BluetoothConnection.class);
                startActivity(intent);
            }
        });
    }


    private void addChip(String text) {
        // Check if the chip with the same text already exists
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip existingChip = (Chip) chipGroup.getChildAt(i);
            if (existingChip.getText().toString().equals(text)) {
                // Chip already exists, do not add it again
                Toasty.warning(this,"Pain Already Selected",Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Create and add a new chip if it doesn't already exist
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chipitem, chipGroup, false);

        chip.setText(text);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        chip.setEllipsize(null);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            removeChipFromJson(text);
        });

        chipGroup.addView(chip);
    }


    private void updateSelectedPains() {
        selectedpains = new JSONArray(); // Reset the JSON array
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            selectedpains.put(chip.getText().toString()); // Add chip text to JSON array
        }
    }

    private void removeChipFromJson(String text) {
        for (int i = 0; i < selectedpains.length(); i++) {
            try {
                if (selectedpains.getString(i).equals(text)) {
                    selectedpains.remove(i); // Remove chip text from JSON array
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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