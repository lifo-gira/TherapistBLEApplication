package com.example.therapistbluelock;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ReportFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class HomeFragment extends Fragment implements ScreenShotable, PatientsAssignedAdapter.OnReportIconClickListener {

    private RecyclerView verticalRecyclerView;
    private RecyclerView newPatientRecyclerView;
    private PatientsAssignedAdapter patientsAdapter;
    List<PatientsAssigned> patientsAssigned = new ArrayList<>();
    private NewPatientAdapter newPatientAdapter;
    private ImageView kneeImage;
    LinearLayout viewAllPatient;
    TextView oldpat, newpat, totalpat,user_name,profilename;

    List<String> patientname = new ArrayList<>();
    AutoCompleteTextView auto_complete;
    ArrayAdapter<String> adapter;

    public static String uname, userid,therapistid,therapistname,patientnam;
    public static String patusername,patpassword;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        // Initialize the vertical RecyclerView
        verticalRecyclerView = view.findViewById(R.id.patients_assigned);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        patientsAdapter = new PatientsAssignedAdapter(this, patientsAssigned);
        verticalRecyclerView.setAdapter(patientsAdapter);
        oldpat = view.findViewById(R.id.oldpat);
        newpat = view.findViewById(R.id.newpat);
        totalpat = view.findViewById(R.id.totalpat);
        auto_complete = view.findViewById(R.id.auto_complete);
        adapter = new ArrayAdapter<>(requireContext(), R.layout.drop_list, patientname);
        auto_complete.setAdapter(adapter);
        loadData();
        int space = getResources().getDimensionPixelSize(R.dimen.recycler_item_space); // Define space in dimens.xml
        verticalRecyclerView.addItemDecoration(new ItemSpacingDecoration(space));
        // Load data for vertical RecyclerView

        user_name = view.findViewById(R.id.user_name);
        user_name.setText(MainActivity.therapistname);
        profilename = view.findViewById(R.id.profilename);
        profilename.setText(MainActivity.therapistname);

        auto_complete.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedpatient = (String) parent.getItemAtPosition(position);

            for(int i=0; i<MainActivity.patientdata.length(); i++){
                try {
                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                    if(jsonObject.getInt("flag")>=0){
                        if(selectedpatient.equalsIgnoreCase(jsonObject.getString("patient_name"))){
                            loadData1(jsonObject.getString("patient_id"));
                            Fragment reportFragment = new OverviewFragment(); // Replace with your actual fragment class
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_frame, reportFragment); // Replace with the appropriate container ID
                            transaction.addToBackStack(null); // Add to back stack if you want to return
                            transaction.commit();
                        }

                    }
                    else if(jsonObject.getInt("flag") <= -2){
                        if (selectedpatient.equalsIgnoreCase(jsonObject.getString("user_id"))) {
                            Log.e("Lunar Eclipse", position + " / " + String.valueOf(jsonObject.getInt("flag")));
                            uname = jsonObject.getString("user_id");
                            patusername = uname;
                            userid = jsonObject.getString("patient_id");
                            Intent intent = new Intent(getContext(), CollectionDetails.class);
                            startActivity(intent);
                        }

                    }
                    else if(jsonObject.getInt("flag") == -1){
                        if (selectedpatient.equalsIgnoreCase(jsonObject.getString("patient_name"))) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("PersonalDetails");
                            MainActivity.patientheight = jsonObject1.getInt("Height");
                            uname = jsonObject.getString("user_id");
                            userid = jsonObject.getString("patient_id");
                            patientnam = jsonObject.getString("patient_name");
                            Intent intent = new Intent(getContext(), BluetoothConnection.class);
                            startActivity(intent);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Initialize the horizontal RecyclerView
        newPatientRecyclerView = view.findViewById(R.id.new_patient_recycle);
        newPatientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        newPatientAdapter = new NewPatientAdapter();
        newPatientRecyclerView.setAdapter(newPatientAdapter);
        int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_horizontal_item_space); // Adjust the spacing value as needed
        newPatientRecyclerView.addItemDecoration(new ItemSpacingDecoration(spacing));
        loadNewPatientData();
        kneeImage = view.findViewById(R.id.knee_image);
        startGlitchAnimation();

        viewAllPatient = view.findViewById(R.id.view_all_patient);
        viewAllPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the new fragment
                Fragment viewAllFragment = new PatientListFragment(); // Replace with your actual fragment class
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, viewAllFragment); // Replace with the appropriate container ID
                transaction.addToBackStack(null); // Add to the back stack for navigation
                transaction.commit();
            }
        });

        return view;
    }

    private void startGlitchAnimation() {
        // Floating up and down animation (translationY)
        ObjectAnimator floatUpAndDown = ObjectAnimator.ofFloat(kneeImage, "translationY", -10f, 10f); // Float up and down
        floatUpAndDown.setDuration(1000);  // Duration for the floating animation
        floatUpAndDown.setRepeatCount(ValueAnimator.INFINITE);  // Repeat infinitely
        floatUpAndDown.setRepeatMode(ValueAnimator.REVERSE);  // Smooth reverse effect
        floatUpAndDown.start();  // Start floating animation immediately

        // Flicker animation (alpha) that triggers for 1 second
        ObjectAnimator flicker = ObjectAnimator.ofFloat(kneeImage, "alpha", 1f, 0.7f, 1f); // Flicker effect
        flicker.setDuration(1000);  // Duration for the flicker effect (1 second)
        flicker.setRepeatCount(0);  // No repeat, runs once per trigger

        // Create a handler to trigger the flicker animation every 3 seconds
        Handler handler = new Handler();
        Runnable flickerRunnable = new Runnable() {
            @Override
            public void run() {
                flicker.start();  // Start the flicker animation
                handler.postDelayed(this, 3000);  // Schedule next trigger after 3 seconds
            }
        };

        // Start the first flicker after a short initial delay (optional)
        handler.postDelayed(flickerRunnable, 3000);  // Start after 3 seconds
    }

    @Override
    public void onReportIconClick(String pid) {
        // Handle navigation to a new fragment when the report icon is clicked

        loadData1(pid);
        Fragment reportFragment = new OverviewFragment(); // Replace with your actual fragment class
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, reportFragment); // Replace with the appropriate container ID
        transaction.addToBackStack(null); // Add to back stack if you want to return
        transaction.commit();
    }

    private void loadData() {
        // Example data for vertical RecyclerView
        RequestQueue queue = Volley.newRequestQueue(getContext());
        MainActivity.newpatients = 0;
        MainActivity.oldpatients = 0;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api-wo6.onrender.com/patient-details/all",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Pat", response);
                            MainActivity.completedata = new JSONArray(response);
                            MainActivity.patientdata = new JSONArray();
                            for (int i = 0; i < MainActivity.completedata.length(); i++) {
                                try {
                                    JSONObject jsonObject = MainActivity.completedata.getJSONObject(i);
                                    String therapistid = jsonObject.getString("therapist_id");
                                    String therapistassigned = jsonObject.getString("therapist_assigned");
                                    if (therapistid.equalsIgnoreCase(MainActivity.therapistuserid) && therapistassigned.equalsIgnoreCase(MainActivity.therapistusername)) {
                                        MainActivity.patientdata.put(jsonObject);
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            for(int i=0; i< MainActivity.patientdata.length(); i++){
                                JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);

                                if (jsonObject.getInt("flag") >= 0) {
                                    patientname.add(jsonObject.getString("patient_name"));
                                    MainActivity.oldpatients++;
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("PersonalDetails");
                                    JSONArray jsonArray = jsonObject1.getJSONArray("pain_indication");
                                    StringBuilder stringBuilder = new StringBuilder();


                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        stringBuilder.append(jsonArray.getString(j));
                                        stringBuilder.append(" "); // Custom delimiter
                                    }
                                    String pain = stringBuilder.toString();
//                String pain="Ankle Pain";
                                    patientsAssigned.add(new PatientsAssigned(jsonObject.getString("patient_name"), pain, jsonObject.getString("patient_id"), R.drawable.user_image, Integer.parseInt(jsonObject1.getString("Age")), jsonObject1.getString("Gender"), jsonObject.getString("user_id")));
                                }else if(jsonObject.getInt("flag") == -1){
                                    patientname.add(jsonObject.getString("patient_name"));
                                    MainActivity.newpatients++;
                                }
                                else {
                                    patientname.add(jsonObject.getString("user_id"));
                                    MainActivity.newpatients++;
                                }
                            }

                            adapter.notifyDataSetChanged();

                            oldpat.setText(String.valueOf(MainActivity.oldpatients));
                            newpat.setText(String.valueOf(MainActivity.newpatients));
                            totalpat.setText(String.valueOf(MainActivity.oldpatients + MainActivity.newpatients));
                            patientsAdapter.notifyDataSetChanged();
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

    private void loadNewPatientData() {
        newPatientAdapter.addPatient(new NewPatient("Sam Wilson", "Arm Injury", R.drawable.user_image));
        newPatientAdapter.addPatient(new NewPatient("Lucy Gray", "Wrist Pain", R.drawable.user_image));
        newPatientAdapter.addPatient(new NewPatient("Paul Green", "Hip Pain", R.drawable.user_image));
        newPatientAdapter.addPatient(new NewPatient("Sam Wilson", "Arm Injury", R.drawable.user_image));
        newPatientAdapter.addPatient(new NewPatient("Lucy Gray", "Wrist Pain", R.drawable.user_image));
        newPatientAdapter.addPatient(new NewPatient("Paul Green", "Hip Pain", R.drawable.user_image));
    }

    @Override
    public void takeScreenShot() {
        // Implement screenshot logic if needed
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }
}
