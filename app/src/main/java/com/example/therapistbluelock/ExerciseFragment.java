package com.example.therapistbluelock;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ProgressBar;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExerciseFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private ImageView dragHandleImageView;
    private Spinner recoveryExerciseSpinner, exerciseSpinner;
    List<String> exerciselist = new ArrayList<>();
    ArrayAdapter<String> exerciseadapter;
    List<String> recoveryexerciselist = new ArrayList<>();
    ArrayAdapter<String> recoveryexerciseadapter;
    private RecyclerView exerciseRecyclerView;
    private TableAdapter tableAdapter;
    private GaugeView gaugeView1,gaugeView2;
    private LineChart overall_chart,left_flexion_chart,right_flexion_chart;

    TextView patientname, patientage1, patientgender, patientheight, patientweight, bmiStatus, health_check, patientid,patname,recoverytitle,compperc,pendingtext;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    int completedcount=0, pendingcount =0,totalcount =0,pain;

    ProgressBar progress_bar_horizontal;

    ImageView dicomimage;

    TextView dicomdeform,lefthka,righthka,mptaleft,mptaright,ldfaleft,ldfaright;

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);
        gaugeView1 = rootView.findViewById(R.id.gaugeView1);
        gaugeView2 = rootView.findViewById(R.id.gaugeView2);
        gaugeView1.setProgressColor(0xFFE53030);
        gaugeView2.setProgressColor(0xFF38D9BC);
        gaugeView1.setTextColor(0xFF000000);
        gaugeView2.setTextColor(0xFF000000);
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
        patname = rootView.findViewById(R.id.patname);
        recoverytitle = rootView.findViewById(R.id.recoverytitle);
        compperc = rootView.findViewById(R.id.compperc);
        pendingtext = rootView.findViewById(R.id.pendingtext);

        progress_bar_horizontal = rootView.findViewById(R.id.progress_bar_horizontal);

        dicomimage = rootView.findViewById(R.id.dicomimage);
        dicomdeform = rootView.findViewById(R.id.dicomdeform);

        lefthka = rootView.findViewById(R.id.hkaleft);
        righthka = rootView.findViewById(R.id.hkaright);
        mptaleft = rootView.findViewById(R.id.mptaleft);
        mptaright = rootView.findViewById(R.id.mptaright);
        ldfaleft = rootView.findViewById(R.id.ldfaleft);
        ldfaright = rootView.findViewById(R.id.ldfaright);

        Log.e("Selected Patient Data", String.valueOf(MainActivity.selectedpatientdata));

        try {
            dicomfetch();
            patientname.setText(MainActivity.selectedpatientdata.getString("patient_name"));
            patname.setText(MainActivity.selectedpatientdata.getString("patient_name")+" | ");
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

        // Get references to the DrawerLayout, ImageView, and RecyclerView
        drawerLayout = rootView.findViewById(R.id.drawer_layout);
        dragHandleImageView = rootView.findViewById(R.id.drag_handle_image);
        exerciseRecyclerView = rootView.findViewById(R.id.exercise_recycler_view);

        // Set up RecyclerView
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the Drawer layout and ImageView listener
        drawerLayout.setScrimColor(android.R.color.transparent);
        dragHandleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });



        // Drawer listener to move the image with the drawer
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float drawerWidth = drawerView.getWidth();
                float initialTranslationX = getResources().getDisplayMetrics().density * 17;
                float translationX = -slideOffset * drawerWidth + initialTranslationX;
                dragHandleImageView.setTranslationX(translationX);
            }

            @Override
            public void onDrawerOpened(View drawerView) {}
            @Override
            public void onDrawerClosed(View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        // Initialize Spinners
        recoveryExerciseSpinner = rootView.findViewById(R.id.recovery_exercise_spinner);
        exerciseSpinner = rootView.findViewById(R.id.exercise_spinner);

        try {
            recoveryexerciselist.clear();
            MainActivity.selectedpatientexercisedata = MainActivity.selectedpatientdata.getJSONArray("Model_Recovery");
            for (int i = 0; i < MainActivity.selectedpatientexercisedata.length(); i++) {
                MainActivity.exerciseobject = MainActivity.selectedpatientexercisedata.getJSONObject(i);
                recoveryexerciselist.add(MainActivity.exerciseobject.getString("Title"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        overall_chart = rootView.findViewById(R.id.overall_chart);
        left_flexion_chart = rootView.findViewById(R.id.left_flexion_chart);

        right_flexion_chart = rootView.findViewById(R.id.right_flexion_chart);


        recoveryexerciseadapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recoveryexerciselist);
        recoveryexerciseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recoveryExerciseSpinner.setAdapter(recoveryexerciseadapter);

        recoveryExerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                recoverytitle.setText(selectedItem);
                completedcount = 0;
                pendingcount =0;
                totalcount =0;

                try {
                    MainActivity.selectedpatientexercisedata = MainActivity.selectedpatientdata.getJSONArray("Model_Recovery");
                    exerciselist.clear();
                    for (int i = 0; i < MainActivity.selectedpatientexercisedata.length(); i++) {
                        MainActivity.exerciseobject = MainActivity.selectedpatientexercisedata.getJSONObject(i);
                        jsonArray = new JSONArray();
                        if(selectedItem.equalsIgnoreCase(MainActivity.exerciseobject.getString("Title"))) {
                            pain = MainActivity.exerciseobject.getInt("pain_scale");
                            MainActivity.subexerciseobject = MainActivity.exerciseobject.getJSONObject("Exercise");
                            Iterator<String> keys = MainActivity.subexerciseobject.keys();
                            while (keys.hasNext()) {
                                String testName = keys.next();
                                jsonArray.put(MainActivity.subexerciseobject);
                                exerciselist.add(testName);

                                JSONObject jsonObject1 = MainActivity.subexerciseobject.getJSONObject(testName);
                                if(jsonObject1.getInt("rep")*jsonObject1.getInt("set") >= jsonObject1.getInt("assigned_rep")*jsonObject1.getInt("assigned_set")){
                                    completedcount++;
                                }
                                else{
                                    pendingcount++;
                                }
                                totalcount++;
                            }
                            setupLeftFlexionChart(selectedItem);
                            setupRightFlexionChart(selectedItem);
                        }
                    }

                    int per = (completedcount*100)/totalcount;
                    if(per>=80){
                        progress_bar_horizontal.setProgressBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        compperc.setTextColor(Color.GREEN);
                    }
                    else if(per>30){
                        progress_bar_horizontal.setProgressBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                        compperc.setTextColor(Color.YELLOW);
                    }
                    else{
                        progress_bar_horizontal.setProgressBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        compperc.setTextColor(Color.RED);
                    }
                    progress_bar_horizontal.setProgress(per);
                    compperc.setText(String.valueOf(per)+"%");
                    pendingtext.setText(String.valueOf(pendingcount)+" left");
                    setupArcGauge();
                    // Notify the adapter about the updated list
                    exerciseadapter.notifyDataSetChanged();
                    // Start fetching exercise data
                    new FetchExerciseDataTask().execute();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Log.e("Anirudh Test",selectedItem);
        exerciseadapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, exerciselist);
        exerciseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseadapter);



        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                setupOverallChart(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set up adapters for Spinners
//        setUpSpinner(recoveryExerciseSpinner, R.array.recovery_options);
//        setUpSpinner(exerciseSpinner, R.array.exercise_options);



        return rootView;
    }
    private void setupArcGauge() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 65);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                gaugeView1.setProgress(pain);
                gaugeView2.setProgress(pain);
            }
        });
        animator.start();

    }
    private void setUpSpinner(Spinner spinner, int arrayResId) {
        String[] options = getResources().getStringArray(arrayResId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private class FetchExerciseDataTask extends AsyncTask<Void, Void, List<Table>> {

        @Override
        protected List<Table> doInBackground(Void... voids) {
            return fetchExercisesFromBackend();
        }

        @Override
        protected void onPostExecute(List<Table> exercises) {
            super.onPostExecute(exercises);
            if (exercises != null && !exercises.isEmpty()) {
                tableAdapter = new TableAdapter(exercises);
                exerciseRecyclerView.setAdapter(tableAdapter);
            } else {
                Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private List<Table> fetchExercisesFromBackend() {
        List<Table> exercises = new ArrayList<>();
        String jsonResponse = getJsonResponse(); // Fetch the JSON response
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class); // Parse JSON as an array

        // Create a Set to track processed exercise names
        Set<String> processedExercises = new HashSet<>();

        // Iterate through the JSON array
        for (JsonElement jsonElement : jsonArray) {
            JsonObject exerciseJson = jsonElement.getAsJsonObject(); // Each item in the array is a JsonObject

            for (Map.Entry<String, JsonElement> entry : exerciseJson.entrySet()) {
                String exerciseName = entry.getKey();

                // Skip if this exercise is already processed
                if (processedExercises.contains(exerciseName)) {
                    continue;
                }

                processedExercises.add(exerciseName); // Mark as processed
                JsonObject paramsJson = entry.getValue().getAsJsonObject();

                // Convert JSON to Map<String, Object> for dynamic parameters
                Map<String, Object> params = gson.fromJson(paramsJson, Map.class);

                // Filter only the required parameters
                Map<String, Object> filteredParams = new HashMap<>();
                for (String key : new String[]{"rom", "rep", "set", "velocity", "progress"}) {
                    if (params.containsKey(key)) {
                        filteredParams.put(key, params.get(key));
                    }
                }

                // Create details for the table
                List<TableDetail> details = new ArrayList<>();
                int rowIndex = 0;
                Map<String, String> row = new HashMap<>();
                for (Map.Entry<String, Object> paramEntry : filteredParams.entrySet()) {
                    row.put(paramEntry.getKey(), String.valueOf(paramEntry.getValue()));
                }
                details.add(new TableDetail(rowIndex++, row));

                // Headers for the table
                List<String> headers = new ArrayList<>(filteredParams.keySet());

                // Add the exercise to the list
                exercises.add(new Table(exerciseName, details, headers));
            }
        }
        return exercises;
    }
    private String getJsonResponse() {

        //Log.e("Anirudh Test", String.valueOf(jsonArray));
        String response = String.valueOf(jsonArray);
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
//                "        [10, 20, 30, 40, 50, 60, 60, 50, 40, 30, 20, 10, 0, 0, 10, 20],\n" +
//                "        [0, 60, 8, 1],\n" +
//                "        [10, 20, 30, 40, 50, 60, 60, 50, 40, 30, 20, 10, 0],\n" +
//                "        [0, 60, 10, 2]\n" +
//                "      ],\n" +
//                "      \"left-leg-passive\": [\n" +
//                "        [60, 60, 50, 40, 30, 20, 10, 0, 0, 10, 20, 30, 40, 50, 60, 60, 50, 40, 30, 20, 10, 0, 0, 10],\n" +
//                "        [0, 60, 5, 1],\n" +
//                "        [60, 60, 50, 40, 30, 20, 10, 0],\n" +
//                "        [0, 60, 17, 2],\n" +
//                "        [0, 10, 20, 30, 40, 50, 60, 60, 50, 40, 30, 20, 10, 0],\n" +
//                "        [0, 60, 9, 1]\n" +
//                "      ],\n" +
//                "      \"right-leg-active\": [\n" +
//                "        [5, 5, 5, 5, 5, 5, 5, 5],\n" +
//                "        [5, 5, 1, 1]\n" +
//                "      ],\n" +
//                "      \"right-leg-passive\": [\n" +
//                "        [5, 5, 5, 5, 5, 5, 5, 5],\n" +
//                "        [5, 5, 1, 1]\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  }\n" +
//                "]";
    }
    private void setupOverallChart(String selecteditem) {

        JSONArray legdata = new JSONArray();
        JSONArray legpaindata = new JSONArray();
        JSONArray graphdata = new JSONArray();
        ArrayList<Entry> overallEntries1 = new ArrayList<>();


        try {
            Iterator<String> keys = MainActivity.subexerciseobject.keys();

            while (keys.hasNext()) {
                String testName = keys.next();
                if(selecteditem.equalsIgnoreCase(testName)){
                    MainActivity.indiviexerciseobject = MainActivity.subexerciseobject.getJSONObject(selecteditem);
                    //Log.e("Anirudh Test", String.valueOf(MainActivity.subexerciseobject));
                    legdata = MainActivity.indiviexerciseobject.getJSONArray("values");
                    legpaindata = MainActivity.indiviexerciseobject.getJSONArray("pain");
                    if (legdata.length() > 0) {
                        int max = 0;
                        //graphdata = legdata.getJSONArray(0);
                        // Create sample data entries for the first line

                        for (int j = 0; j < legdata.length(); j++) {
                            overallEntries1.add(new Entry(j, legdata.getInt(j)));
                            if (max < legdata.getInt(j)) {
                                max = legdata.getInt(j);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        // Create sample data entries for the first line
//        ArrayList<Entry> overallEntries1 = new ArrayList<>();
//        overallEntries1.add(new Entry(0, 20));
//        overallEntries1.add(new Entry(1, 25));
//        overallEntries1.add(new Entry(2, 22));
//        overallEntries1.add(new Entry(3, 28));
//        overallEntries1.add(new Entry(4, 24));
//        overallEntries1.add(new Entry(5, 30));
//
//        // Create sample data entries for the second line
//        ArrayList<Entry> overallEntries2 = new ArrayList<>();
//        overallEntries2.add(new Entry(0, 18));
//        overallEntries2.add(new Entry(1, 20));
//        overallEntries2.add(new Entry(2, 25));
//        overallEntries2.add(new Entry(3, 22));
//        overallEntries2.add(new Entry(4, 26));
//        overallEntries2.add(new Entry(5, 28));

        // Create the first dataset
        LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "Overall Line 1");
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
//        LineDataSet overallDataSet2 = new LineDataSet(overallEntries2, "Overall Line 2");
//        overallDataSet2.setColor(0xFFE91E63); // Line color
//        overallDataSet2.setValueTextColor(0xFF000000); // Value text color
//        overallDataSet2.setLineWidth(1f); // Line width
//        overallDataSet2.setCircleColor(0xFFFFC107); // Circle color at data points
//        overallDataSet2.setCircleRadius(4f); // Circle radius
//        overallDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
//        overallDataSet2.setDrawFilled(true); // Enable fill under the line
//        overallDataSet2.setFillColor(0xFFF8F6D); // Fill color under the line
//        overallDataSet2.setFillAlpha(70); // Adjust transparency of the fill

        // Combine the datasets into a single LineData object
//        LineData overallData = new LineData(overallDataSet1, overallDataSet2);
        LineData overallData = new LineData(overallDataSet1);

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
//        Legend overallLegend = overall_chart.getLegend();
//        overallLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Position at the top
//        overallLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align to the right
//        overallLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Orientation of the legend
//        overallLegend.setDrawInside(false); // Keep outside the chart bounds
//        overallLegend.setTextColor(0xFF000000); // Legend text color

        // Refresh the chart
        overall_chart.invalidate(); // Redraw the chart with updated data
    }
    private void setupLeftFlexionChart(String selectedItem) {

        JSONArray legdata = new JSONArray();
        JSONArray legpaindata = new JSONArray();
        JSONArray graphdata = new JSONArray();
        ArrayList<Entry> overallEntries1 = new ArrayList<>();


        try {
            Iterator<String> keys = MainActivity.subexerciseobject.keys();


            int i=0;
            while (keys.hasNext()) {
                String testName = keys.next();
                if(testName.contains("Left-Leg")){
                    JSONObject jsonObject1 = MainActivity.subexerciseobject.getJSONObject(testName);
                    //Log.e("Anirudh Test", String.valueOf(MainActivity.subexerciseobject));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("rom")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("assigned_rep")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("rep")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("assigned_set")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("set")));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Create the first dataset
        LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "Overall Line 1");
        overallDataSet1.setColor(0xFF3D5AFE); // Line color
        overallDataSet1.setValueTextColor(0xFF000000); // Value text color
        overallDataSet1.setLineWidth(1f); // Line width
        overallDataSet1.setCircleColor(0xFFFFD383); // Circle color at data points
        overallDataSet1.setCircleRadius(4f); // Circle radius
        overallDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
        overallDataSet1.setDrawFilled(true); // Enable fill under the line
        overallDataSet1.setFillColor(0xFDBA5FF); // Fill color under the line
        overallDataSet1.setFillAlpha(70); // Adjust transparency of the fill


        // Combine the datasets into a single LineData object
        LineData overallData = new LineData(overallDataSet1);

        // Find the chart and bind the data
        left_flexion_chart.setData(overallData);

        // Style the chart
        left_flexion_chart.getDescription().setEnabled(false); // Disable the description
        left_flexion_chart.setDrawGridBackground(false);
        left_flexion_chart.setTouchEnabled(true);
        left_flexion_chart.setPinchZoom(true);

        // Remove grid and axis lines
        XAxis overallXAxis = left_flexion_chart.getXAxis();
        overallXAxis.setDrawAxisLine(false); // Remove X axis line
        overallXAxis.setDrawGridLines(false); // Remove X grid lines
        overallXAxis.setDrawLabels(false); // Disable X-axis labels

        YAxis overallLeftAxis = left_flexion_chart.getAxisLeft();
        overallLeftAxis.setEnabled(false);
        overallLeftAxis.setDrawAxisLine(false); // Remove Y axis line (left)
        overallLeftAxis.setDrawGridLines(false); // Remove Y grid lines (left)
        YAxis overallRightAxis = left_flexion_chart.getAxisRight();
        overallRightAxis.setEnabled(false); // Disable right Y-axis

        // Configure the legend
        Legend overallLegend = left_flexion_chart.getLegend();
        overallLegend.setEnabled(false);
        overallLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Position at the top
        overallLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align to the right
        overallLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Orientation of the legend
        overallLegend.setDrawInside(false); // Keep outside the chart bounds
        overallLegend.setTextColor(0xFF000000); // Legend text color

        // Refresh the chart
        left_flexion_chart.invalidate(); // Redraw the chart with updated data
    }
    private void setupRightFlexionChart(String selectedItem) {

        JSONArray legdata = new JSONArray();
        JSONArray legpaindata = new JSONArray();
        JSONArray graphdata = new JSONArray();
        ArrayList<Entry> overallEntries1 = new ArrayList<>();


        try {
            Iterator<String> keys = MainActivity.subexerciseobject.keys();


            int i=0;
            while (keys.hasNext()) {
                String testName = keys.next();
                if(testName.contains("Right-Leg")){
                    JSONObject jsonObject1 = MainActivity.subexerciseobject.getJSONObject(testName);
                    //Log.e("Anirudh Test", String.valueOf(MainActivity.subexerciseobject));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("rom")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("assigned_rep")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("rep")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("assigned_set")));
                    overallEntries1.add(new Entry(i++,jsonObject1.getInt("set")));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        // Create the first dataset
        LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "Overall Line 1");
        overallDataSet1.setColor(0xFF3D5AFE); // Line color
        overallDataSet1.setValueTextColor(0xFF000000); // Value text color
        overallDataSet1.setLineWidth(1f); // Line width
        overallDataSet1.setCircleColor(0xFFFFD383); // Circle color at data points
        overallDataSet1.setCircleRadius(4f); // Circle radius
        overallDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
        overallDataSet1.setDrawFilled(true); // Enable fill under the line
        overallDataSet1.setFillColor(0xFDBA5FF); // Fill color under the line
        overallDataSet1.setFillAlpha(70); // Adjust transparency of the fill


        // Combine the datasets into a single LineData object
        LineData overallData = new LineData(overallDataSet1);

        // Find the chart and bind the data
        right_flexion_chart.setData(overallData);

        // Style the chart
        right_flexion_chart.getDescription().setEnabled(false); // Disable the description
        right_flexion_chart.setDrawGridBackground(false);
        right_flexion_chart.setTouchEnabled(true);
        right_flexion_chart.setPinchZoom(true);

        // Remove grid and axis lines
        XAxis overallXAxis = right_flexion_chart.getXAxis();
        overallXAxis.setDrawAxisLine(false); // Remove X axis line
        overallXAxis.setDrawGridLines(false); // Remove X grid lines
        overallXAxis.setDrawLabels(false); // Disable X-axis labels

        YAxis overallLeftAxis = right_flexion_chart.getAxisLeft();
        overallLeftAxis.setEnabled(false);
        overallLeftAxis.setDrawAxisLine(false); // Remove Y axis line (left)
        overallLeftAxis.setDrawGridLines(false); // Remove Y grid lines (left)
        YAxis overallRightAxis = right_flexion_chart.getAxisRight();
        overallRightAxis.setEnabled(false); // Disable right Y-axis

        // Configure the legend
        Legend overallLegend = right_flexion_chart.getLegend();
        overallLegend.setEnabled(false);
        overallLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Position at the top
        overallLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align to the right
        overallLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Orientation of the legend
        overallLegend.setDrawInside(false); // Keep outside the chart bounds
        overallLegend.setTextColor(0xFF000000); // Legend text color

        // Refresh the chart
        right_flexion_chart.invalidate(); // Redraw the chart with updated data
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
