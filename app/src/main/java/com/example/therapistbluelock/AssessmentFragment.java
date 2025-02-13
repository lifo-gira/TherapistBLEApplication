package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AssessmentFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private ImageView dragHandleImageView;
    private RecyclerView exerciseRecyclerView;
    private TableAdapter tableAdapter;
    private LineChart overall_chart, left_knee_chart, right_knee_chart, performance_chart, flexion_chart;
    TextView patientname, patientage1, patientgender, patientheight, patientweight, bmiStatus, health_check, patientid, leftrom, rightrom;
    JSONObject jsonObject = new JSONObject();
    Spinner exercisespinner,modespinner,cyclespinner;
    List<String> exerciselist = new ArrayList<>();
    ArrayAdapter<String> exerciseadapter;
    List<String> exerciselist1 = new ArrayList<>();
    ArrayAdapter<String> exerciseadapter1;
    List<String> exerciselist2 = new ArrayList<>();
    ArrayAdapter<String> exerciseadapter2;
    public static String exercisename;
    JSONObject matchingJSONObject = new JSONObject();
    JSONObject matchingJSONObject1 = new JSONObject();

    ImageView dicomimage;

    TextView dicomdeform,lefthka,righthka,mptaleft,mptaright,ldfaleft,ldfaright;

    public AssessmentFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assessment, container, false);
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
        patientname = rootView.findViewById(R.id.patientname);
        patientage1 = rootView.findViewById(R.id.patientage1);
        patientgender = rootView.findViewById(R.id.patientgender);
        patientheight = rootView.findViewById(R.id.patientheight);
        patientweight = rootView.findViewById(R.id.patientweight);
        bmiStatus = rootView.findViewById(R.id.bmiStatus);
        health_check = rootView.findViewById(R.id.health_check);
        patientid = rootView.findViewById(R.id.patientid);
        leftrom = rootView.findViewById(R.id.leftrom);
        rightrom = rootView.findViewById(R.id.rightrom);
        exercisespinner = rootView.findViewById(R.id.exercisespinner);
        modespinner = rootView.findViewById(R.id.modespinner);
        cyclespinner = rootView.findViewById(R.id.cyclespinner);

        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        exerciselist.clear();
        exerciselist1.clear();
        exerciselist2.clear();
        exerciselist.add(0,"Select an Exercise");
        while (keys.hasNext()) {
            String testName = keys.next();
            exerciselist.add(testName);
        }

        dicomimage = rootView.findViewById(R.id.dicomimage);
        dicomdeform = rootView.findViewById(R.id.dicomdeform);

        lefthka = rootView.findViewById(R.id.hkaleft);
        righthka = rootView.findViewById(R.id.hkaright);
        mptaleft = rootView.findViewById(R.id.mptaleft);
        mptaright = rootView.findViewById(R.id.mptaright);
        ldfaleft = rootView.findViewById(R.id.ldfaleft);
        ldfaright = rootView.findViewById(R.id.ldfaright);

        try {
            dicomfetch();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        exerciseadapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, exerciselist);
        exerciseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exercisespinner.setAdapter(exerciseadapter);
        exerciseadapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, exerciselist1);
        exerciseadapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modespinner.setAdapter(exerciseadapter1);
        exerciseadapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, exerciselist2);
        exerciseadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cyclespinner.setAdapter(exerciseadapter2);
        overall_chart = rootView.findViewById(R.id.overall_chart);
        left_knee_chart = rootView.findViewById(R.id.left_knee_chart);
        setupLeftKneeChart();
        right_knee_chart = rootView.findViewById(R.id.right_knee_chart);
        setupRightKneeChart();
        performance_chart = rootView.findViewById(R.id.performance_chart);
        setupPerformanceChart();
        flexion_chart = rootView.findViewById(R.id.flexion_chart);
        setupFlexionChart();
        exercisespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                exercisename = selectedItem;
                modespinner.setSelection(0);
                cyclespinner.setSelection(0);
                overall_chart.clear();

                Log.e("Mobility Exercise Cycle List 1", String.valueOf(exerciselist2));

                if("Select an Exercise".equalsIgnoreCase(selectedItem)){
                    cyclespinner.setVisibility(View.GONE);
                    modespinner.setVisibility(View.GONE);
                    exerciselist1.clear();
                    exerciselist2.clear();
                }
                else if("Mobility Test".equalsIgnoreCase(selectedItem)) {
                    exerciselist1.clear();
                    exerciselist1.add(0,"Select a Mode");
                    exerciselist1.add("Active");
                    exerciselist1.add("Passive");
                    exerciseadapter1.notifyDataSetChanged();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    exerciseadapter2.notifyDataSetChanged();
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.VISIBLE);
                }
                else if("Walk and Gait Analysis".equalsIgnoreCase(selectedItem)){
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.GONE);
                    Iterator<String> keys = MainActivity.assessmentexercise.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    exerciseadapter2.notifyDataSetChanged();
                    while (keys.hasNext()) {
                        String testName = keys.next();
                        if("Walk and Gait Analysis".equalsIgnoreCase(testName)){
                            JSONObject testDetails = new JSONObject();
                            JSONArray leftlegdata = new JSONArray();
                            JSONArray rightlegdata = new JSONArray();
                            JSONArray graphdata = new JSONArray();
                            ArrayList<Entry> overallEntries1 = new ArrayList<>();
                            ArrayList<Entry> overallEntries2 = new ArrayList<>();

                            try {
                                testDetails = MainActivity.assessmentexercise.getJSONObject(testName);
                                HashSet<String> uniqueCycles = new HashSet<>();

                                Iterator<String> cycleKeys = testDetails.keys();
                                while (cycleKeys.hasNext()) {
                                    String cycleKey = cycleKeys.next();
                                    String cycleNumber = "";
                                    if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                        cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"

                                        // Only add unique cycle numbers
                                        if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                            exerciselist2.add("Cycle: " + cycleNumber);
                                        }
                                    }
                                }
                                exerciseadapter2.notifyDataSetChanged();
                                //Log.e("Walk and Gait Data", String.valueOf(testDetails));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                else if("Extension Lag Test".equalsIgnoreCase(selectedItem)){
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.GONE);
                    Iterator<String> keys = MainActivity.assessmentexercise.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    exerciseadapter2.notifyDataSetChanged();

                    matchingJSONObject = new JSONObject();
                    JSONObject testDetails = new JSONObject();

                    try {
                        testDetails = MainActivity.assessmentexercise.getJSONObject(selectedItem);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    HashSet<String> uniqueCycles = new HashSet<>();

                    Iterator<String> cycleKeys = testDetails.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        String cycleNumber = "";
                        if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                            cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"

                            // Only add unique cycle numbers
                            if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                exerciselist2.add("Cycle: " + cycleNumber);
                            }
                        }
                    }
                    exerciseadapter2.notifyDataSetChanged();
                    //Log.e("Walk and Gait Data", String.valueOf(testDetails));
                }
                else if("Dynamic Balance Test".equalsIgnoreCase(selectedItem)){
                    exerciselist1.clear();
                    exerciselist1.add(0,"Select a Mode");
                    exerciselist1.add("Without Support");
                    exerciselist1.add("With Support");
                    exerciseadapter1.notifyDataSetChanged();
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.VISIBLE);
                }
                else if("Static Balance Test".equalsIgnoreCase(selectedItem)){
                    exerciselist1.clear();
                    exerciselist1.add(0,"Select a Mode");
                    exerciselist1.add("Eyes Open");
                    exerciselist1.add("Eyes Closed");
                    exerciseadapter1.notifyDataSetChanged();
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.VISIBLE);
                }
                else if("Staircase Climbing Test".equalsIgnoreCase(selectedItem)){
                    exerciselist1.clear();
                    exerciselist1.add(0,"Select a Mode");
                    exerciselist1.add("Without Support");
                    exerciselist1.add("With Support");
                    exerciseadapter1.notifyDataSetChanged();
                    cyclespinner.setVisibility(View.VISIBLE);
                    modespinner.setVisibility(View.VISIBLE);
                }
                else{
                    if("Proprioception Test".equalsIgnoreCase(selectedItem)){
                        cyclespinner.setVisibility(View.VISIBLE);
                        modespinner.setVisibility(View.GONE);
                        Iterator<String> keys = MainActivity.assessmentexercise.keys();
                        exerciselist2.clear();
                        exerciselist2.add(0,"Select a Cycle");
                        exerciseadapter2.notifyDataSetChanged();
                        while (keys.hasNext()) {
                            String testName = keys.next();
                            if("Proprioception Test".equalsIgnoreCase(testName)){
                                JSONObject testDetails = new JSONObject();
                                JSONArray leftlegdata = new JSONArray();
                                JSONArray rightlegdata = new JSONArray();
                                JSONArray graphdata = new JSONArray();
                                ArrayList<Entry> overallEntries1 = new ArrayList<>();
                                ArrayList<Entry> overallEntries2 = new ArrayList<>();

                                try {
                                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);
                                    HashSet<String> uniqueCycles = new HashSet<>();

                                    Iterator<String> cycleKeys = testDetails.keys();
                                    while (cycleKeys.hasNext()) {
                                        String cycleKey = cycleKeys.next();
                                        String cycleNumber = "";
                                        if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                            cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"

                                            // Only add unique cycle numbers
                                            if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                                exerciselist2.add("Cycle: " + cycleNumber);
                                            }
                                        }
                                    }
                                    exerciseadapter2.notifyDataSetChanged();
                                    //Log.e("Walk and Gait Data", String.valueOf(testDetails));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    else{
                    modespinner.setVisibility(View.VISIBLE);
                    }
                }

                Log.e("Mobility Exercise Cycle List 1", String.valueOf(exerciselist2));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cyclespinner.setSelection(0);
                String selectedItem = parent.getItemAtPosition(position).toString();
                JSONObject testDetails = new JSONObject();

                Log.e("Mobility Exercise Cycle List 2", String.valueOf(exerciselist2));

                try {
                    testDetails = MainActivity.assessmentexercise.getJSONObject(exercisename);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if("Mobility Test".equalsIgnoreCase(exercisename)){
                    matchingJSONObject = new JSONObject();
                    Iterator<String> cycleKeys = testDetails.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    String modename = "Select a Mode";
                    // Log.d("Extension Lag Data", String.valueOf(testDetails));

                    HashSet<String> uniqueCycles = new HashSet<>(); // Move outside the loop

                    if(!"Select a Mode".equalsIgnoreCase(selectedItem)) {
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            Log.e("Matching Object keys 1", cycleKey);

                            if ("Active".equalsIgnoreCase(selectedItem)) {
                                modename = "active";
                            } else {
                                modename = "passive";
                            }

                            if (cycleKey.toLowerCase().contains(modename.toLowerCase()) && !"Select a Mode".equalsIgnoreCase(modename)) { // Match keys ending with the selected cycle number
                                try {
                                    Object value = testDetails.get(cycleKey);
//                                    Log.e("Matching Object keys 2", String.valueOf(value));
                                    String cycleNumber = "";
                                    if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                        cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                        // Only add unique cycle numbers
                                        if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                            exerciselist2.add("Cycle: " + cycleNumber);
                                        }
                                    }
                                    matchingJSONObject.put(cycleKey, value);
                                } catch (JSONException e) {
                                    e.printStackTrace(); // Handle JSON exceptions
                                }
                            }
                        }
                    }
                    exerciseadapter2.notifyDataSetChanged();
                    Log.d("Matching Object keys 2", matchingJSONObject.toString());
                }
//                else if("Extension Lag Test".equalsIgnoreCase(exercisename)){
//                    matchingJSONObject = new JSONObject();
//                    Iterator<String> cycleKeys = testDetails.keys();
////                    Log.d("Extension Lag Data", String.valueOf(testDetails));
//                    while (cycleKeys.hasNext()) {
//                        String cycleKey = cycleKeys.next();
//                        if (cycleKey.toLowerCase().contains(selectedItem.toLowerCase())) { // Match keys ending with the selected cycle number
//                            try {
//                                // Fetch the value and add it to the new JSONObject
//                                Object value = testDetails.get(cycleKey);
//                                matchingJSONObject.put(cycleKey, value);
//                            } catch (JSONException e) {
//                                e.printStackTrace(); // Handle JSON exceptions
//                            }
//                        }
//                    }
//                    Log.d("Extension Lag Data", matchingJSONObject.toString());
//                    setupOverallChart(exercisename);
//                }
                else if ("Dynamic Balance Test".equalsIgnoreCase(exercisename)) {
                    matchingJSONObject = new JSONObject();
                    Iterator<String> cycleKeys = testDetails.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    // Log.d("Extension Lag Data", String.valueOf(testDetails));

                    HashSet<String> uniqueCycles = new HashSet<>(); // Move outside the loop

                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        String modename = "wos";
                        if ("Without Support".equalsIgnoreCase(selectedItem)) {
                            modename = "wos";
                        } else {
                            modename = "ws";
                        }

                        if (cycleKey.toLowerCase().contains(modename.toLowerCase())) { // Match keys ending with the selected cycle number
                            try {
                                Object value = testDetails.get(cycleKey);
                                String cycleNumber = "";
                                if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                    cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                    // Only add unique cycle numbers
                                    if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                        exerciselist2.add("Cycle: " + cycleNumber);
                                    }
                                }
                                matchingJSONObject.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

                    exerciseadapter2.notifyDataSetChanged();
                    Log.d("Extension Lag Data", matchingJSONObject.toString());
                }
                else if ("Static Balance Test".equalsIgnoreCase(exercisename)) {
                    matchingJSONObject = new JSONObject();
                    Iterator<String> cycleKeys = testDetails.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    // Log.d("Extension Lag Data", String.valueOf(testDetails));

                    HashSet<String> uniqueCycles = new HashSet<>(); // Move outside the loop

                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        String modename = "eyes-open";
                        if ("eyes open".equalsIgnoreCase(selectedItem)) {
                            modename = "eyes-open";
                        } else {
                            modename = "eyes-closed";
                        }

                        if (cycleKey.toLowerCase().contains(modename.toLowerCase())) { // Match keys ending with the selected cycle number
                            try {
                                Object value = testDetails.get(cycleKey);
                                String cycleNumber = "";
                                if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                    cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                    // Only add unique cycle numbers
                                    if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                        exerciselist2.add("Cycle: " + cycleNumber);
                                    }
                                }
                                matchingJSONObject.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

                    exerciseadapter2.notifyDataSetChanged();
                    Log.d("Extension Lag Data", matchingJSONObject.toString());
                }
                else if ("Staircase Climbing Test".equalsIgnoreCase(exercisename)) {
                    matchingJSONObject = new JSONObject();
                    Iterator<String> cycleKeys = testDetails.keys();
                    exerciselist2.clear();
                    exerciselist2.add(0,"Select a Cycle");
                    // Log.d("Extension Lag Data", String.valueOf(testDetails));

                    HashSet<String> uniqueCycles = new HashSet<>(); // Move outside the loop

                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        String modename = "wos";
                        if ("Without Support".equalsIgnoreCase(selectedItem)) {
                            modename = "wos";
                        } else {
                            modename = "ws";
                        }

                        if (cycleKey.toLowerCase().contains(modename.toLowerCase())) { // Match keys ending with the selected cycle number
                            try {
                                Object value = testDetails.get(cycleKey);
                                String cycleNumber = "";
                                if (cycleKey.matches(".*-\\d+$")) { // Check if the key ends with digits preceded by "-"
                                    cycleNumber = cycleKey.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                    // Only add unique cycle numbers
                                    if (uniqueCycles.add(cycleNumber)) { // add() returns false if the number is already in the set
                                        exerciselist2.add("Cycle: " + cycleNumber);
                                    }
                                }
                                matchingJSONObject.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

                    exerciseadapter2.notifyDataSetChanged();
                    Log.d("Extension Lag Data", matchingJSONObject.toString());
                }

                Log.e("Mobility Exercise Cycle List 2", String.valueOf(exerciselist2));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        cyclespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                JSONObject testDetails = new JSONObject();

                Log.e("Mobility Exercise Cycle List 3", String.valueOf(exerciselist2));

                try {
                    testDetails = MainActivity.assessmentexercise.getJSONObject(exercisename);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if("Mobility Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");
                    matchingJSONObject1 = new JSONObject();
                    Iterator<String> cycleKeys = matchingJSONObject.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = matchingJSONObject.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }
                    Log.e("Matching Object1 values", String.valueOf(matchingJSONObject1));
                    setupOverallChart(exercisename);
                }
                else if("Walk and Gait Analysis".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");

                    Iterator<String> cycleKeys = testDetails.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = testDetails.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

// Log or use the resulting JSONObject
                    Log.d("Walk and Gait Data", matchingJSONObject1.toString());
                    setupOverallChart(exercisename);
                }
                else if("Dynamic Balance Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");
                    matchingJSONObject1 = new JSONObject();
                    Iterator<String> cycleKeys = matchingJSONObject.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = matchingJSONObject.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }
                    setupOverallChart(exercisename);
                }
                else if("Static Balance Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");
                    matchingJSONObject1 = new JSONObject();
                    Iterator<String> cycleKeys = matchingJSONObject.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = matchingJSONObject.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }
                    setupOverallChart(exercisename);
                }
                else if("Staircase Climbing Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");
                    matchingJSONObject1 = new JSONObject();
                    Iterator<String> cycleKeys = matchingJSONObject.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = matchingJSONObject.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }
                    setupOverallChart(exercisename);
                }
                else if("Proprioception Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");

                    Iterator<String> cycleKeys = testDetails.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = testDetails.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

// Log or use the resulting JSONObject
                    Log.d("Proprioception Test", matchingJSONObject.toString());
                    setupOverallChart(exercisename);

                }
                else if("Extension Lag Test".equalsIgnoreCase(exercisename)){
                    String cycleNumber = selectedItem.replaceAll("Cycle: ", "");

                    Iterator<String> cycleKeys = testDetails.keys();
                    while (cycleKeys.hasNext()) {
                        String cycleKey = cycleKeys.next();
                        if (cycleKey.matches(".*-" + cycleNumber + "$")) { // Match keys ending with the selected cycle number
                            try {
                                // Fetch the value and add it to the new JSONObject
                                Object value = testDetails.get(cycleKey);
                                matchingJSONObject1.put(cycleKey, value);
                            } catch (JSONException e) {
                                e.printStackTrace(); // Handle JSON exceptions
                            }
                        }
                    }

                    setupOverallChart(exercisename);
                }

                Log.e("Extension lag Exercise Cycle List 3", String.valueOf(matchingJSONObject1));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Log.e("Selected Patient Data", String.valueOf(MainActivity.selectedpatientdata));
        Log.e("Patient Assessment Object", String.valueOf(MainActivity.assessmentexercise));

        try {

            patientname.setText(MainActivity.selectedpatientdata.getString("patient_name"));
            jsonObject = MainActivity.selectedpatientdata.getJSONObject("PersonalDetails");
            patientage1.setText(String.valueOf(jsonObject.getInt("Age")));
            patientgender.setText(jsonObject.getString("Gender"));
            patientheight.setText(jsonObject.getInt("Height") + " cm");
            patientweight.setText(jsonObject.getInt("Weight") + " Kg");
            bmiStatus.setText(String.valueOf((int) jsonObject.getDouble("BMI")));
            patientid.setText(String.valueOf(MainActivity.selectedpatientdata.getString("user_id")));
            if (jsonObject.getDouble("BMI") < 18.5) {
                health_check.setText("Underweight");
            } else if (jsonObject.getDouble("BMI") >= 18.5 && jsonObject.getDouble("BMI") < 25) {
                health_check.setText("Healthy Weight");
            } else if (jsonObject.getDouble("BMI") >= 25 && jsonObject.getDouble("BMI") < 30) {
                health_check.setText("Overweight");
            } else if (jsonObject.getDouble("BMI") >= 30) {
                health_check.setText("Obesity");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Initialize DrawerLayout and ImageView
        drawerLayout = rootView.findViewById(R.id.drawer_layout); // Ensure the ID matches
        dragHandleImageView = rootView.findViewById(R.id.drag_handle_image); // Ensure ImageView has this ID
        drawerLayout.setScrimColor(android.R.color.transparent);

        // Set the click listener to open the drawer when the image is clicked
        dragHandleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END); // Open the drawer from the right
            }
        });

        // Add a listener to move the image when the drawer is opened or closed
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // The drawer width is fixed at 280dp, we need to move the image with the drawer
                float drawerWidth = drawerView.getWidth(); // This will be 280dp based on your layout

                // Set initial translationX value (17dp in pixels)
                float initialTranslationX = getResources().getDisplayMetrics().density * 17; // Convert dp to px

                // Move image to the left based on the slide offset of the drawer, adding the initial translation
                float translationX = -slideOffset * drawerWidth + initialTranslationX;
                dragHandleImageView.setTranslationX(translationX); // Move image in sync with drawer slide
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Optional: Handle anything when the drawer is fully opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Optional: Handle anything when the drawer is fully closed
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Optional: Handle state changes if needed
            }
        });

        // Initialize RecyclerView for exercise data
        exerciseRecyclerView = rootView.findViewById(R.id.exercise_recycler_view);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Start the AsyncTask to fetch exercise data
        new FetchExerciseDataTask().execute();


        return rootView;
    }

    /**
     * AsyncTask to simulate fetching exercises from the backend.
     */
    private class FetchExerciseDataTask extends AsyncTask<Void, Void, List<Table>> {

        @Override
        protected List<Table> doInBackground(Void... voids) {
            // Simulating fetching data from an external source (e.g., API, DB, etc.)
            return fetchExercisesFromBackend();
        }

        @Override
        protected void onPostExecute(List<Table> exercises) {
            super.onPostExecute(exercises);
            if (exercises != null && !exercises.isEmpty()) {
                // Set up adapter with the fetched data
                tableAdapter = new TableAdapter(exercises);
                exerciseRecyclerView.setAdapter(tableAdapter);
            } else {
                // If no data, show a toast or handle accordingly
                Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Simulates fetching exercises from a backend.
     * Replace this method with your actual data-fetching mechanism.
     */
    private List<Table> fetchExercisesFromBackend() {
        List<Table> exercises = new ArrayList<>();
        String jsonResponse = getJsonResponse();  // This is where you'd normally fetch the JSON response

        // Parse the JSON response
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);  // Changed to JsonArray

        // Iterate through the JSON array
        for (JsonElement jsonElement : jsonArray) {
            JsonObject exerciseJson = jsonElement.getAsJsonObject();  // Each item in the array is a JsonObject
            for (Map.Entry<String, JsonElement> entry : exerciseJson.entrySet()) {
                String exerciseName = entry.getKey();
                JsonObject paramsJson = entry.getValue().getAsJsonObject();


                // Convert JSON to Map<String, Object> for dynamic parameters
                Map<String, Object> params = gson.fromJson(paramsJson, Map.class);


                for (Map.Entry<String, Object> entry1 : params.entrySet()) {
                    Object paramValue = entry1.getValue();

                    if (paramValue instanceof List) {
                        List<?> valueList = (List<?>) paramValue;
                        List<Object> filteredList = new ArrayList<>();

//                        if ("Mobility Test".equalsIgnoreCase(exerciseName) ) {
//                            // Start filtering from the 3rd subarray (index 2) and keep only odd index subarrays
//                            for (int index = 2; index < valueList.size(); index++) {
//                                if (index % 2 != 0) { // Odd index condition
//                                    Object subArray = valueList.get(index);
//                                    if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
//                                        filteredList.add(subArray);
//                                    }
//                                }
//                            }
//                        }
                        if ("Extension Lag Test".equalsIgnoreCase(exerciseName) ) {
                            // Start filtering from the 3rd subarray (index 2) and keep only odd index subarrays
                            for (int index = 0; index < valueList.size(); index++) {
                                if (index % 2 != 0) { // Odd index condition
                                    Object subArray = valueList.get(index);
                                    if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                        filteredList.add(subArray);
                                    }
                                }
                            }
                        }
                        else if("Mobility Test".equalsIgnoreCase(exerciseName) || "Dynamic Balance Test".equalsIgnoreCase(exerciseName) || "Static Balance Test".equalsIgnoreCase(exerciseName) || "Walk and Gait Analysis".equalsIgnoreCase(exerciseName) || "Staircase Climbing Test".equalsIgnoreCase(exerciseName) || "Proprioception Test".equalsIgnoreCase(exerciseName)){
                            for (int index = 0; index < valueList.size(); index++) {
                                if (index % 2 != 0) { // Odd index condition
                                    Object subArray = valueList.get(index);
                                    if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                        filteredList.add(subArray);
                                    }
                                }
                            }

                        }
                        else {
                            for (int index = 0; index < valueList.size(); index++) {
                                Object subArray = valueList.get(index);
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                        }

                        // Replace the original value with the filtered list
                        entry1.setValue(filteredList);
                    }
                }


                // Create a List to hold all rows
                List<TableDetail> details = new ArrayList<>();

                // Find the maximum size of the lists (i.e., the number of rows)
                int maxSize = 0;
                for (Object paramValue : params.values()) {

                    if (paramValue instanceof List) {
                        List<?> valueList = (List<?>) paramValue;
                        // Filter out empty or null subarrays and update maxSize

                        for (Object subArray : valueList) {
                            //Log.e("Params Anirudh", String.valueOf(subArray));
                            if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                maxSize = maxSize + 1;
                            }
                        }

                    } else {
                        maxSize = Math.max(maxSize, 1);  // For non-list parameters, consider them as a single value
                    }
                }

                // Now iterate through each index (0 to maxSize - 1) and create a row for that index
                if ("Mobility Test".equalsIgnoreCase(exerciseName)) {
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();


                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
//                                    Log.e("Params Anirudh", String.valueOf(filteredList));
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() >= 4) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        row.put("Cycle No", String.valueOf(i + 1)); // i+1 for cycle number
                                        row.put("Leg/Mode", paramName);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Minimum-Extension", String.valueOf(subArray.get(0))); // Min Angle
                                        row.put("Maximum-Flexion", String.valueOf(subArray.get(1))); // Max Angle
                                        row.put("Velocity", String.valueOf(subArray.get(2))); // Velocity
                                        row.put("Pain", String.valueOf(subArray.get(3)));     // Pain
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if ("Extension Lag Test".equalsIgnoreCase(exerciseName)) {
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();



                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    Log.e("Table Extension", subArray.toString());
//                                    Log.e("Params Anirudh", String.valueOf(filteredList));
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 3) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        row.put("Cycle No", String.valueOf(i + 1)); // i+1 for cycle number
                                        int lastHyphenIndex = paramName.lastIndexOf('-');
                                        // Extract the substring up to the last hyphen
                                        String result = paramName.substring(0, lastHyphenIndex);
                                        row.put("Leg", result);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Active ED", String.valueOf(subArray.get(0))); // Min Angle
                                        row.put("Passive ED", String.valueOf(subArray.get(1))); // Max Angle
                                        row.put("Total ED", String.valueOf(subArray.get(2)));
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if ("Proprioception Test".equalsIgnoreCase(exerciseName)) {
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();



                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    Log.e("Table Extension", subArray.toString());
//                                    Log.e("Params Anirudh", String.valueOf(filteredList));
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 4) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                         // i+1 for cycle number
                                        int lastHyphenIndex = paramName.lastIndexOf('-');
                                        // Extract the substring up to the last hyphen
                                        String result = paramName.substring(0, lastHyphenIndex);
                                        row.put("Cycle No", paramName.substring(lastHyphenIndex + 1));
                                        row.put("Leg", result);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Maximum Extension", String.valueOf(subArray.get(0)));
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if("Dynamic Balance Test".equalsIgnoreCase(exerciseName) ){
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();


                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 3) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        String cycleNumber = "";
                                        if (paramName.matches(".*-\\d+$")) { // Check if paramName ends with digits preceded by "-"
                                            cycleNumber = paramName.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                        }
                                        row.put("Cycle No", cycleNumber); // i+1 for cycle number
                                        row.put("Leg/Mode", paramName);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Sit-to-Stand", String.valueOf(subArray.get(0))); // Min Angle
                                        row.put("Stand-to-Shift", String.valueOf(subArray.get(1))); // Max Angle
                                        row.put("Walk-Time", String.valueOf(subArray.get(2))); // Velocity
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if("Static Balance Test".equalsIgnoreCase(exerciseName)){
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();


                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 1) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        String cycleNumber = "";
                                        if (paramName.matches(".*-\\d+$")) { // Check if paramName ends with digits preceded by "-"
                                            cycleNumber = paramName.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                        }
                                        row.put("Cycle No", cycleNumber); // i+1 for cycle number
                                        row.put("Leg/Mode", paramName);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Balance Time (sec)", String.valueOf(subArray.get(0))); // Velocity
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if("Walk and Gait Analysis".equalsIgnoreCase(exerciseName)){
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();




                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
//                            Log.e("Params Anirudh", String.valueOf(filteredList));
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    Log.e("Table Walk Gait", String.valueOf(subArray));
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 9) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        String cycleNumber = "";
                                        if (paramName.matches(".*-\\d+$")) { // Check if paramName ends with digits preceded by "-"
                                            cycleNumber = paramName.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                        }
                                        row.put("Cycle No", cycleNumber); // i+1 for cycle number
                                        row.put("Leg/Mode", paramName);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Distance", String.valueOf(subArray.get(0))); // Velocity
                                        row.put("Stand-Time", String.valueOf(subArray.get(1)));
                                        row.put("Average-Swing-Time", String.valueOf(subArray.get(2)));
                                        row.put("Stance-Phase", String.valueOf(subArray.get(3)));
                                        row.put("Average-Stride-Length", String.valueOf(subArray.get(4)));
                                        row.put("Mean-Velocity", String.valueOf(subArray.get(5)));
                                        row.put("Cadence", String.valueOf(subArray.get(6)));
                                        row.put("Step-Count", String.valueOf(subArray.get(7)));
                                        row.put("Active-Time", String.valueOf(subArray.get(8)));
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }
                else if("Staircase Climbing Test".equalsIgnoreCase(exerciseName)){
                    for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
                        String paramName = paramEntry.getKey();
                        Object paramValue = paramEntry.getValue();


                        // Create the row map for each cycle (i)
                        // Iterate through all params to handle the data
                        // For "Mobility" exercise, process the data
                        // Check if paramValue is a List
                        if (paramValue instanceof List) {
                            List<?> valueList = (List<?>) paramValue;

                            // Filter out empty or null subarrays
                            List<Object> filteredList = new ArrayList<>();
                            for (Object subArray : valueList) {
                                if (subArray instanceof List && !((List<?>) subArray).isEmpty()) {
                                    filteredList.add(subArray);
                                }
                            }
//                            Log.e("Params Anirudh", String.valueOf(filteredList));
                            for (int i = 0; i < filteredList.size(); i++) {
                                Map<String, String> row = new HashMap<>();
                                // If the filtered list has enough subarrays for the current index `i`
                                if (filteredList.size() != 0) {
                                    List<?> subArray = (List<?>) filteredList.get(i);
                                    // Ensure subArray has the expected number of elements (at least 4)
                                    if (subArray.size() == 4) {
                                        // Add the data for cycle number, leg, angles, velocity, and pain to the row
                                        String cycleNumber = "";
                                        if (paramName.matches(".*-\\d+$")) { // Check if paramName ends with digits preceded by "-"
                                            cycleNumber = paramName.replaceAll(".*-(\\d+)$", "$1"); // Extract digits after the last "-"
                                        }
                                        row.put("Cycle No", cycleNumber); // i+1 for cycle number
                                        row.put("Leg/Mode", paramName);                   // leg name (e.g., leftleg or rightleg)
                                        row.put("Step-Count", String.valueOf(subArray.get(0))); // Velocity
                                        row.put("Ascent-Time", String.valueOf(subArray.get(1)));
                                        row.put("Descent-Time", String.valueOf(subArray.get(2)));
                                        row.put("Turn-Time", String.valueOf(subArray.get(3)));
                                    }
                                    details.add(new TableDetail(i, row));
                                    Log.e("Cycle Row Data", String.valueOf(row));
                                }

                            }

                        }
                        // Debug log to see the row data for each cycle

                    }
                }


                // Dynamically generate the headers based on the keys in the paramsJson
                List<String> headers = new ArrayList<>();
                if ("Mobility Test".equalsIgnoreCase(exerciseName)) {
                    headers.add("Cycle No");
                    headers.add("Leg/Mode");
                    headers.add("Minimum-Extension");
                    headers.add("Maximum-Flexion");
                    headers.add("Velocity");
                    headers.add("Pain");
                }
                else if("Proprioception Test".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg");
                    headers.add("Maximum Extension");
                }
                else if("Extension Lag Test".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg");
                    headers.add("Active ED");
                    headers.add("Passive ED");
                    headers.add("Total ED");
                }
                else if("Dynamic Balance Test".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg/Mode");
                    headers.add("Sit-to-Stand");
                    headers.add("Stand-to-Shift");
                    headers.add("Walk-Time");
                }
                else if("Static Balance Test".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg/Mode");
                    headers.add("Balance Time (sec)");
                }
                else if("Walk and Gait Analysis".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg/Mode");
                    headers.add("Distance");
                    headers.add("Stand-Time");
                    headers.add("Average-Swing-Time");
                    headers.add("Stance-Phase");
                    headers.add("Average-Stride-Length");
                    headers.add("Mean-Velocity");
                    headers.add("Cadence");
                    headers.add("Step-Count");
                    headers.add("Active-Time");
                }
                else if("Staircase Climbing Test".equalsIgnoreCase(exerciseName)){
                    headers.add("Cycle No");
                    headers.add("Leg/Mode");
                    headers.add("Step-Count");
                    headers.add("Ascent-Time");
                    headers.add("Descent-Time");
                    headers.add("Turn-Time");
                }
                else {
                    for (String key : params.keySet()) {
                        headers.add(key);  // Add each key as a header
                    }
                }
                // Create an Exercise object with the name, details, and headers
                exercises.add(new Table(exerciseName, details, headers));
            }
        }

        return exercises;
    }

    /**
     * Returns a sample JSON response. Replace this with real data fetching.
     */
    private String getJsonResponse() {


        JSONArray jsonArray = new JSONArray();
        JSONObject subobj = new JSONObject();
        try {
            for (int i = 0; i < MainActivity.selectedpatientassesementdata.length(); i++) {
                JSONObject jsonObject1 = MainActivity.selectedpatientassesementdata.getJSONObject(i);
                JSONObject jsonObject2 = jsonObject1.getJSONObject("exercises");
                Iterator<String> keys = jsonObject2.keys();
                while (keys.hasNext()) {
                    String exename = keys.next();
                    subobj = new JSONObject();
                    subobj.put(exename, jsonObject2.getJSONObject(exename));
                    jsonArray.put(subobj);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String response = String.valueOf(jsonArray);
        Log.e("Params Anirudh", String.valueOf(response));

        return response;
//        return "[\n" +
//                "  {\n" +
//                "    \"Mobility\": {\n" +
//                "      \"leftleg\": [\n" +
//                "        [10, 0, 0, 10, 20, 30, 40, 50, 60, 60, 50, 40, 30],\n" +
//                "        [0, 60, 10, 2],\n" +
//                "        [10, 0, 0, 10, 20, 30, 40, 50, 60, 60, 50, 40, 30],\n" +
//                "        [0, 60, 10, 2],\n" +
//                "      ],\n" +
//                "      \"rightleg\": [\n" +
//                "        [5, 5, 5, 5, 5, 5],\n" +
//                "        [5, 5, 2, 1],\n" +
//                "        [5],\n" +
//                "        [5, 5, 0, 19],\n" +
//                "        [],\n" +
//                "        []\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  },\n" +
//                "  {\n" +
//                "    \"Extension Lag Test\": {\n" +
//                "      \"left-leg-active\": [\n" +
//                "        [10, 20, 30, 40, 50, 60, 60, 50, 40, 30]\n" +
//                "      ],\n" +
//                "      \"right-leg-passive\": [\n" +
//                "        [30, 40, 50, 60, 70]\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  }\n" +
//                "]";
    }

    private void setupOverallChart(String exercisename) {

        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();
            try {
                if (exercisename.equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();
                    JSONArray leftlegdata = new JSONArray();
                    JSONArray rightlegdata = new JSONArray();
                    JSONArray graphdata = new JSONArray();
                    ArrayList<Entry> overallEntries1 = new ArrayList<>();
                    ArrayList<Entry> overallEntries2 = new ArrayList<>();

                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);
//                    if ("Mobility Test".equalsIgnoreCase(testName) || "Proprioception Test".equalsIgnoreCase(testName)) {
//                        leftlegdata = testDetails.getJSONArray("leftleg");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("rightleg");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                    }
                    if("Mobility Test".equalsIgnoreCase(testName)){
                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
//                        Log.e("Matching Object keys", String.valueOf(matchingJSONObject1));
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.e("Mobility Graph Entries 1", String.valueOf(overallEntries1));
                        Log.e("Mobility Graph Entries 2", String.valueOf(overallEntries2));
                    }
                    else if ("Extension Lag Test".equalsIgnoreCase(testName)) {
                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


//                        leftlegdata = testDetails.getJSONArray("left-leg-active");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("right-leg-active");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
                    }
                    else if ("Dynamic Balance Test".equalsIgnoreCase(testName)) {
                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

//                        leftlegdata = testDetails.getJSONArray("left-leg-wos-1");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("right-leg-wos-1");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
                    }
                    else if ("Static Balance Test".equalsIgnoreCase(testName)) {

                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


//                        leftlegdata = testDetails.getJSONArray("left-leg-eyes-open-1");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("right-leg-eyes-open-6");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
                    }
                    else if ("Staircase Climbing Test".equalsIgnoreCase(testName)) {

                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


//                        leftlegdata = testDetails.getJSONArray("left-leg-wos-1");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("right-leg-wos-1");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
                    }
                    else if ("Walk and Gait Analysis".equalsIgnoreCase(testName)) {
                        Iterator<String> cycleKeys = matchingJSONObject1.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("leftleg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("rightleg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if ("Proprioception Test".equalsIgnoreCase(testName)) {
                        Iterator<String> cycleKeys = matchingJSONObject.keys();
                        while (cycleKeys.hasNext()) {
                            String cycleKey = cycleKeys.next();
                            try {
                                // Check if the key contains "left" or "right"
                                if (cycleKey.contains("left-leg")) {
                                    leftlegdata = testDetails.getJSONArray(cycleKey);
                                    if (leftlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = leftlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                } else if (cycleKey.contains("right-leg")) {
                                    rightlegdata = testDetails.getJSONArray(cycleKey);
                                    if (rightlegdata.length() > 0) {
                                        int max = 0;
                                        graphdata = rightlegdata.getJSONArray(0); // First set of data
                                        for (int j = 0; j < graphdata.length(); j++) {
                                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                                            if (max < graphdata.getInt(j)) {
                                                max = graphdata.getInt(j);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
//                        leftlegdata = testDetails.getJSONArray("leftleg-1");
//                        if (leftlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = leftlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries1.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                        rightlegdata = testDetails.getJSONArray("rightleg-1");
//                        if (rightlegdata.length() > 0) {
//                            int max = 0;
//                            graphdata = rightlegdata.getJSONArray(0);
//                            // Create sample data entries for the first line
//                            for (int j = 0; j < graphdata.length(); j++) {
//                                overallEntries2.add(new Entry(j, graphdata.getInt(j)));
//                                if (max < graphdata.getInt(j)) {
//                                    max = graphdata.getInt(j);
//                                }
//                            }
//                        }
//                    }


                    // Create the first dataset
                    LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "Left Leg");
                    overallDataSet1.setColor(0xFF3D5AFE); // Line color
                    overallDataSet1.setValueTextColor(0xFF000000); // Value text color
                    overallDataSet1.setLineWidth(1f); // Line width
                    overallDataSet1.setDrawCircles(false);
                    overallDataSet1.setDrawValues(false);
                    overallDataSet1.setHighlightEnabled(true);
                    overallDataSet1.setCircleColor(0xFFFFD383); // Circle color at data points
                    overallDataSet1.setCircleRadius(4f); // Circle radius
                    overallDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
                    overallDataSet1.setDrawFilled(true); // Enable fill under the line
                    overallDataSet1.setFillColor(0xFDBA5FF); // Fill color under the line
                    overallDataSet1.setFillAlpha(70); // Adjust transparency of the fill

                    // Create the second dataset
                    LineDataSet overallDataSet2 = new LineDataSet(overallEntries2, "Right Leg");
                    overallDataSet2.setColor(0xFFE91E63); // Line color
                    overallDataSet2.setValueTextColor(0xFF000000); // Value text color
                    overallDataSet2.setDrawCircles(false);
                    overallDataSet2.setDrawValues(false);
                    overallDataSet2.setHighlightEnabled(true);
                    overallDataSet2.setLineWidth(1f); // Line width
                    overallDataSet2.setCircleColor(0xFFFFC107); // Circle color at data points
                    overallDataSet2.setCircleRadius(4f); // Circle radius
                    overallDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
                    overallDataSet2.setDrawFilled(true); // Enable fill under the line
                    overallDataSet2.setFillColor(0xFFF8F6D); // Fill color under the line
                    overallDataSet2.setFillAlpha(70); // Adjust transparency of the fill

                    // Combine the datasets into a single LineData object
                    LineData overallData = new LineData(overallDataSet1, overallDataSet2);

                    // Find the chart and bind the data
                    overall_chart.setData(overallData);

                    // Style the chart
                    overall_chart.getDescription().setEnabled(false); // Disable the description
                    overall_chart.setDrawGridBackground(false);
                    overall_chart.setTouchEnabled(true);
                    overall_chart.setPinchZoom(true);

                    // Remove grid and axis lines
                    XAxis overallXAxis = overall_chart.getXAxis();
                    overallXAxis.setDrawAxisLine(false); // Remove X axis line
                    overallXAxis.setDrawGridLines(false); // Remove X grid lines
                    overallXAxis.setDrawLabels(false); // Disable X-axis labels

                    YAxis overallLeftAxis = overall_chart.getAxisLeft();
                    overallLeftAxis.setDrawAxisLine(false); // Remove Y axis line (left)
                    overallLeftAxis.setDrawGridLines(false); // Remove Y grid lines (left)
                    YAxis overallRightAxis = overall_chart.getAxisRight();
                    overallRightAxis.setEnabled(false); // Disable right Y-axis

                    // Configure the legend
                    Legend overallLegend = overall_chart.getLegend();
                    overallLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Position at the top
                    overallLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align to the right
                    overallLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Orientation of the legend
                    overallLegend.setDrawInside(false); // Keep outside the chart bounds
                    overallLegend.setTextColor(0xFF000000); // Legend text color

                    // Refresh the chart
                    overall_chart.invalidate(); // Redraw the chart with updated data

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void setupLeftKneeChart() {
        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();

            try {
                if ("Proprioception Test".equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();
                    JSONArray leftlegdata = new JSONArray();
                    JSONArray graphdata = new JSONArray();
                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);
                    Iterator<String> keys1 = testDetails.keys();
                    while(keys1.hasNext()){
                        String type = keys1.next();
                        if(type.contains("left-leg")){
                            leftlegdata = testDetails.getJSONArray(type);
                            break;
                        }
                    }
                    if (leftlegdata.length() > 0) {
                        int max = 0;
                        graphdata = leftlegdata.getJSONArray(1);
                        // Create sample data entries for the first line
                        ArrayList<Entry> overallEntries1 = new ArrayList<>();
                        for (int j = 0; j < graphdata.length(); j++) {
                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                            if (max < graphdata.getInt(j)) {
                                max = graphdata.getInt(j);
                            }
                        }

                        leftrom.setText(String.valueOf(max));

                        // Create the first dataset
                        LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "");
                        overallDataSet1.setColor(0xFF3D5AFE); // Line color
                        overallDataSet1.setValueTextColor(0xFF000000); // Value text color
                        overallDataSet1.setLineWidth(1f); // Line width
                        overallDataSet1.setDrawCircles(true);
                        overallDataSet1.setDrawValues(true);
                        overallDataSet1.setHighlightEnabled(true);
                        overallDataSet1.setCircleColor(0xFFFFD383); // Circle color at data points
                        overallDataSet1.setCircleRadius(4f); // Circle radius
                        overallDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
                        overallDataSet1.setDrawFilled(false); // Enable fill under the line
                        //overallDataSet1.setFillColor(0xFDBA5FF); // Fill color under the line
                        //overallDataSet1.setFillAlpha(70); // Adjust transparency of the fill


                        // Combine the datasets into a single LineData object
                        LineData overallData = new LineData(overallDataSet1);

                        // Find the chart and bind the data
                        left_knee_chart.setData(overallData);

                        // Style the chart
                        left_knee_chart.getDescription().setEnabled(false); // Disable the description
                        left_knee_chart.setDrawGridBackground(false);
                        left_knee_chart.setTouchEnabled(true);
                        left_knee_chart.setPinchZoom(true);

                        // Remove grid and axis lines
                        XAxis overallXAxis = left_knee_chart.getXAxis();
                        overallXAxis.setDrawAxisLine(false); // Remove X axis line
                        overallXAxis.setDrawGridLines(false); // Remove X grid lines
                        overallXAxis.setDrawLabels(false); // Disable X-axis labels

                        YAxis overallLeftAxis = left_knee_chart.getAxisLeft();
                        overallLeftAxis.setEnabled(false);
                        overallLeftAxis.setDrawAxisLine(false); // Remove Y axis line (left)
                        overallLeftAxis.setDrawGridLines(false); // Remove Y grid lines (left)
                        YAxis overallRightAxis = left_knee_chart.getAxisRight();
                        overallRightAxis.setEnabled(false); // Disable right Y-axis

                        // Configure the legend
//                        Legend overallLegend = left_knee_chart.getLegend();
//                        overallLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Position at the top
//                        overallLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align to the right
//                        overallLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Orientation of the legend
//                        overallLegend.setDrawInside(false); // Keep outside the chart bounds
//                        overallLegend.setTextColor(0xFF000000); // Legend text color

                        // Refresh the chart
                        left_knee_chart.getLegend().setEnabled(false);
                        left_knee_chart.invalidate(); // Redraw the chart with updated data
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }


    }

    private void setupRightKneeChart() {
        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();
            try {
                if ("Proprioception Test".equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();
                    JSONArray leftlegdata = new JSONArray();
                    JSONArray graphdata = new JSONArray();

                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);

                    Iterator<String> keys1 = testDetails.keys();
                    while(keys1.hasNext()){
                        String type = keys1.next();
                        if(type.contains("right-leg")){
                            leftlegdata = testDetails.getJSONArray(type);
                            break;
                        }
                    }
                    if (leftlegdata.length() > 0) {
                        int max = 0;
                        graphdata = leftlegdata.getJSONArray(1);
                        // Create sample data entries for the first line
                        ArrayList<Entry> overallEntries1 = new ArrayList<>();
                        for (int j = 0; j < graphdata.length(); j++) {
                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                            if (max < graphdata.getInt(j)) {
                                max = graphdata.getInt(j);
                            }
                        }

                        rightrom.setText(String.valueOf(max));

                        // Create the first dataset
                        LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "");
                        overallDataSet1.setColor(0xFF3D5AFE); // Line color
                        overallDataSet1.setValueTextColor(0xFF000000); // Value text color
                        overallDataSet1.setLineWidth(1f); // Line width
                        overallDataSet1.setCircleColor(0xFFFFD383); // Circle color at data points
                        overallDataSet1.setDrawValues(true);
                        overallDataSet1.setDrawCircles(true);
                        overallDataSet1.setHighlightEnabled(true);
                        overallDataSet1.setCircleRadius(4f); // Circle radius
                        overallDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
                        overallDataSet1.setDrawFilled(false); // Enable fill under the line
                        //overallDataSet1.setFillColor(0xFDBA5FF); // Fill color under the line
                        //overallDataSet1.setFillAlpha(70); // Adjust transparency of the fill


                        // Combine the datasets into a single LineData object
                        LineData overallData = new LineData(overallDataSet1);

                        // Find the chart and bind the data
                        right_knee_chart.setData(overallData);

                        // Style the chart
                        right_knee_chart.getDescription().setEnabled(false); // Disable the description
                        right_knee_chart.setDrawGridBackground(false);
                        right_knee_chart.setTouchEnabled(true);
                        right_knee_chart.setPinchZoom(true);

                        // Remove grid and axis lines
                        XAxis overallXAxis = right_knee_chart.getXAxis();
                        overallXAxis.setDrawAxisLine(false); // Remove X axis line
                        overallXAxis.setDrawGridLines(false); // Remove X grid lines
                        overallXAxis.setDrawLabels(false); // Disable X-axis labels

                        YAxis overallLeftAxis = right_knee_chart.getAxisLeft();
                        overallLeftAxis.setEnabled(false);
                        overallLeftAxis.setDrawAxisLine(false); // Remove Y axis line (left)
                        overallLeftAxis.setDrawGridLines(false); // Remove Y grid lines (left)
                        YAxis overallRightAxis = right_knee_chart.getAxisRight();
                        overallRightAxis.setEnabled(false); // Disable right Y-axis
                        right_knee_chart.getLegend().setEnabled(false);

                        // Refresh the chart
                        right_knee_chart.invalidate(); // Redraw the chart with updated data

                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setupPerformanceChart() {
        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();
            try {
                if ("Static Balance Test".equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();

                    ArrayList<Entry> leftLegEntries = new ArrayList<>();
                    ArrayList<Entry> rightLegEntries = new ArrayList<>();
                    int leftMax = 0, rightMax = 0;

                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);

                    Iterator<String> keys1 = testDetails.keys();

                    while (keys1.hasNext()) {
                        String key = keys1.next();
                        JSONArray legData = testDetails.getJSONArray(key);

                        // Dynamically identify left or right leg arrays
                        if (key.contains("left-leg")) {
                            JSONArray graphData = legData.getJSONArray(1);
                            for (int j = 0; j < graphData.length(); j++) {
                                leftLegEntries.add(new Entry(j, graphData.getInt(j)));
                            }
                        } else if (key.contains("right-leg")) {
                            JSONArray graphData = legData.getJSONArray(1);
                            for (int j = 0; j < graphData.length(); j++) {
                                rightLegEntries.add(new Entry(j, graphData.getInt(j)));
                            }
                        }
                    }


                    // Create datasets for both legs
                    LineDataSet leftLegDataSet = new LineDataSet(leftLegEntries, "Left Leg");
                    leftLegDataSet.setColor(0xFF3D5AFE); // Blue color for left leg
                    leftLegDataSet.setCircleColor(0xFF3D5AFE);
                    leftLegDataSet.setDrawCircles(true);
                    leftLegDataSet.setLineWidth(1f);
                    leftLegDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    leftLegDataSet.setDrawValues(true);

                    LineDataSet rightLegDataSet = new LineDataSet(rightLegEntries, "Right Leg");
                    rightLegDataSet.setColor(0xFFFF5722); // Orange color for right leg
                    rightLegDataSet.setCircleColor(0xFFFF5722);
                    rightLegDataSet.setDrawCircles(true);
                    rightLegDataSet.setLineWidth(1f);
                    rightLegDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    rightLegDataSet.setDrawValues(true);

                    // Combine datasets
                    LineData lineData = new LineData(leftLegDataSet, rightLegDataSet);

                    // Set data and style the chart
                    performance_chart.setData(lineData);
                    performance_chart.getDescription().setEnabled(false);
                    performance_chart.setDrawGridBackground(false);
                    performance_chart.setTouchEnabled(true);
                    performance_chart.setPinchZoom(true);

                    // Style X axis
                    XAxis xAxis = performance_chart.getXAxis();
                    xAxis.setDrawAxisLine(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawLabels(false);

                    // Style Y axis
                    YAxis leftAxis = performance_chart.getAxisLeft();
                    leftAxis.setDrawAxisLine(false);
                    leftAxis.setDrawGridLines(false);

                    YAxis rightAxis = performance_chart.getAxisRight();
                    rightAxis.setEnabled(false);

                    // Disable legend
                    performance_chart.getLegend().setEnabled(false); // Enable if you want to show labels

                    // Refresh chart
                    performance_chart.invalidate();

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setupFlexionChart() {

        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();
            try {
                if ("Staircase Climbing Test".equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();

                    ArrayList<Entry> leftLegEntries = new ArrayList<>();
                    int leftMax = 0, rightMax = 0;

                    testDetails = MainActivity.assessmentexercise.getJSONObject(testName);

                    Iterator<String> keys1 = testDetails.keys();

                    while (keys1.hasNext()) {
                        String key = keys1.next();
                        JSONArray legData = testDetails.getJSONArray(key);

                        // Dynamically identify left or right leg arrays
                        if (key.contains("left-leg")) {
                            JSONArray graphData = legData.getJSONArray(1);
                            for (int j = 0; j < graphData.length(); j++) {
                                leftLegEntries.add(new Entry(j, graphData.getInt(j)));
                            }
                        }
                    }


                    // Create datasets for both legs
                    LineDataSet leftLegDataSet = new LineDataSet(leftLegEntries, "Left Leg");
                    leftLegDataSet.setColor(0xFF3D5AFE); // Blue color for left leg
                    leftLegDataSet.setCircleColor(0xFF3D5AFE);
                    leftLegDataSet.setDrawCircles(true);
                    leftLegDataSet.setLineWidth(1f);
                    leftLegDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    leftLegDataSet.setDrawValues(true);

                    // Combine datasets
                    LineData lineData = new LineData(leftLegDataSet);

                    // Set data and style the chart
                    flexion_chart.setData(lineData);
                    flexion_chart.getDescription().setEnabled(false);
                    flexion_chart.setDrawGridBackground(false);
                    flexion_chart.setTouchEnabled(true);
                    flexion_chart.setPinchZoom(true);

                    // Style X axis
                    XAxis xAxis = flexion_chart.getXAxis();
                    xAxis.setDrawAxisLine(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawLabels(false);

                    // Style Y axis
                    YAxis leftAxis = flexion_chart.getAxisLeft();
                    leftAxis.setEnabled(false);
                    leftAxis.setDrawAxisLine(false);
                    leftAxis.setDrawGridLines(false);

                    YAxis rightAxis = flexion_chart.getAxisRight();
                    rightAxis.setEnabled(false);
                    rightAxis.setDrawAxisLine(false);
                    rightAxis.setDrawGridLines(false);
                    rightAxis.setDrawLabels(false);

                    // Disable legend
                    flexion_chart.getLegend().setEnabled(false); // Enable if you want to show labels

                    // Refresh chart
                    flexion_chart.invalidate();

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void dicomfetch() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://api-wo6.onrender.com/get_dicom/"+MainActivity.selectedpatientdata.getString("unique_id"),
                null,  // No request body for a GET request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.e("Dicom Response", String.valueOf(response));
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray1 = new JSONArray();
                            jsonArray = response.getJSONArray("data");
                            jsonObject = jsonArray.getJSONObject(0);
                            jsonArray1 = jsonObject.getJSONArray("values_stored");
                            Log.e("Dicom Response", String.valueOf(jsonObject));
                            String base64Image = jsonObject.getString("dicom_image");
                            displayBase64Image(base64Image, dicomimage);
                            dicomdeform.setText("Deformity: "+jsonArray1.getString(2));
                            lefthka.setText(String.format("%.2f", jsonArray1.getDouble(1)));
                            righthka.setText(String.format("%.2f", jsonArray1.getDouble(0)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if("null".equalsIgnoreCase(error.getMessage())){
                            Toast.makeText(getContext(), "Dicom Not applicable", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("Dicom Not found", String.valueOf(error));
                    }
                }
        );

// Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    public void displayBase64Image(String base64String, ImageView imageView) {
        try {
            // Decode Base64 string
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

            Log.e("Inside Display image","");
            // Convert decoded bytes to Bitmap
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Set the Bitmap to ImageView
            imageView.setImageBitmap(decodedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
