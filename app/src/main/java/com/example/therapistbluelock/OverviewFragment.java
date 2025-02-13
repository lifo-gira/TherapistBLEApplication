package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class OverviewFragment extends Fragment {

    private Spinner doctorSpinner;
    private LineChart overall_chart, left_knee_chart, right_knee_chart, performance_chart;

    TextView docname, patientname, patientage1, patientgender, patientheight, patientweight, bmiStatus, health_check, patientid, leftrom, rightrom;
    ImageView assigndoc;
    JSONObject jsonObject = new JSONObject();
    EditText appointment_date;

    SeekBar bmiSeekBar;

    List<String> doctorlist = new ArrayList<>();
    ArrayAdapter<String> doctoradapter;

    JSONArray doctordata = new JSONArray();

    Calendar calendar;
    String formattedDate, pid, doctorname, doctorid;

    ImageView dicomimage;

    TextView dicomdeform,lefthka,righthka,mptaleft,mptaright,ldfaleft,ldfaright;


    public OverviewFragment() {
        // Default constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        // Find the report card view by its ID
        View reportCard = view.findViewById(R.id.report_card);
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
        patientname = view.findViewById(R.id.patientname);
        patientage1 = view.findViewById(R.id.patient_age_overview);
        patientgender = view.findViewById(R.id.patientgender);
        patientheight = view.findViewById(R.id.patientheight);
        patientweight = view.findViewById(R.id.patientweight);
        bmiStatus = view.findViewById(R.id.bmiStatus);
        health_check = view.findViewById(R.id.health_check);
        patientid = view.findViewById(R.id.patientid);
        bmiSeekBar = view.findViewById(R.id.bmiSeekBar);
        appointment_date = view.findViewById(R.id.appointment_date);
        assigndoc = view.findViewById(R.id.assigndoc);
        leftrom = view.findViewById(R.id.leftrom);
        rightrom = view.findViewById(R.id.rightrom);
        docname = view.findViewById(R.id.docname);

        dicomimage = view.findViewById(R.id.dicomimage);
        dicomdeform = view.findViewById(R.id.dicomdeform);

        lefthka = view.findViewById(R.id.hkaleft);
        righthka = view.findViewById(R.id.hkaright);
        mptaleft = view.findViewById(R.id.mptaleft);
        mptaright = view.findViewById(R.id.mptaright);
        ldfaleft = view.findViewById(R.id.ldfaleft);
        ldfaright = view.findViewById(R.id.ldfaright);

        Log.e("Selected Patient Data", String.valueOf(MainActivity.selectedpatientdata));


        try {
            dicomfetch();
            MainActivity.selectedpatientassesementdata = MainActivity.selectedpatientdata.getJSONArray("Assessment");
            if(MainActivity.selectedpatientdata.getInt("flag") >= 1){
                docname.setText(MainActivity.selectedpatientdata.getString("doctor_assigned"));
            }
            if(MainActivity.selectedpatientdata.getInt("flag")>=3) {
                MainActivity.selectedpatientexercisedata = MainActivity.selectedpatientdata.getJSONArray("Model_Recovery");
            }
            for (int i = 0; i < MainActivity.selectedpatientassesementdata.length(); i++) {
                MainActivity.assessmentmain = MainActivity.selectedpatientassesementdata.getJSONObject(i);
                MainActivity.assessmentexercise = MainActivity.assessmentmain.getJSONObject("exercises");
                Log.e("Patient Assessment Object", String.valueOf(MainActivity.assessmentexercise));
            }


            patientname.setText(MainActivity.selectedpatientdata.getString("patient_name"));
            jsonObject = MainActivity.selectedpatientdata.getJSONObject("PersonalDetails");
            patientage1.setText(String.valueOf(jsonObject.getInt("Age")));
            patientgender.setText(jsonObject.getString("Gender"));
            patientheight.setText(jsonObject.getInt("Height") + " cm");
            patientweight.setText(jsonObject.getInt("Weight") + " Kg");
            bmiStatus.setText(String.valueOf((int) jsonObject.getDouble("BMI")));
            patientid.setText(String.valueOf(MainActivity.selectedpatientdata.getString("user_id")));
            pid = MainActivity.selectedpatientdata.getString("patient_id");
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

        bmiSeekBar.setEnabled(false);
        bmiSeekBar.setClickable(false);

        // Set the BMI value based on the TextView
        String bmiStatusText = bmiStatus.getText().toString();
        if (!bmiStatusText.isEmpty()) {
            float initialBMI = Float.parseFloat(bmiStatusText);
            // Format the value to 2 decimal places
            String formattedBMI = String.format(Locale.US, "%.2f", initialBMI);
            // Set the text using the formatted string
            bmiStatus.setText(formattedBMI);

            // Parse the formatted BMI back to a float and call setBMIValue
            setBMIValue(Float.parseFloat(formattedBMI));
        }

        // Set an OnClickListener on the report card
        reportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the new fragment
                Fragment viewAllFragment = new ReportFragment(); // Replace with your actual fragment class
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, viewAllFragment); // Replace with the appropriate container ID
                transaction.addToBackStack(null); // Add to the back stack for navigation
                transaction.commit();
            }
        });

        // Initialize the exercise spinner
        doctorSpinner = view.findViewById(R.id.doctor_spinner);
        doctoradapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, doctorlist);
        doctoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(doctoradapter);

        loaddoctors();

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();


                for (int i = 0; i < doctordata.length(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1 = doctordata.getJSONObject(i);
                        if (selectedItem.equalsIgnoreCase(jsonObject1.getString("name"))) {
                            Log.e("Doctor Specific Details", String.valueOf(jsonObject1));
                            doctorname = jsonObject1.getString("name");
                            doctorid = jsonObject1.getString("_id");
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        assigndoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder errorBuilder = new StringBuilder();
                if (pid == null || pid.isEmpty()) {
                    errorBuilder.append("Patient ID Not found.\n");
                }
                if (doctorname == null || doctorname.isEmpty() || doctorid == null || doctorid.isEmpty()) {
                    errorBuilder.append("Please Select Doctor.\n");
                }
                if (formattedDate == null || formattedDate.isEmpty()) {
                    errorBuilder.append("Please Schedule the Data.\n");
                }
                if (errorBuilder.length() > 0) {
                    // Show all errors at once
                    Toasty.error(getContext(), errorBuilder.toString().trim(), Toasty.LENGTH_LONG).show();
                } else {
                    assigndoctor();
                }
            }
        });
        overall_chart = view.findViewById(R.id.overall_chart);
        setupOverallChart();
        left_knee_chart = view.findViewById(R.id.left_knee_chart);
        setupLeftKneeChart();
        right_knee_chart = view.findViewById(R.id.right_knee_chart);
        setupRightKneeChart();
        performance_chart = view.findViewById(R.id.performance_chart);
        setupPerformanceChart();

        appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create and show the DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_DeviceDefault_Dialog_Alert,  // Use your custom style if necessary
                        (view1, year1, month1, dayOfMonth) -> {
                            // Create Calendar instances to compare the dates
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year1, month1, dayOfMonth);

                            Calendar today = Calendar.getInstance();  // Get today's date

                            // Compare the selected date with today
                            if (selectedDate.before(today)) {
                                // Show a Toast or a warning if the selected date is before today
                                Toasty.warning(getContext(), "You cannot select a date in the past! ", Toasty.LENGTH_LONG).show();
                            } else {
                                // Format the selected date and set it in the EditText if valid
                                String date = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                                appointment_date.setText(date);  // Set the selected date in the EditText

                                // Format the date for further use (example for the formattedDate variable)
                                formattedDate = String.format("(%d, %02d, %02d, %02d, %02d)",
                                        year1, month1 + 1, dayOfMonth, 23, 0);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        return view;


    }

    private void setupOverallChart() {

        Iterator<String> keys = MainActivity.assessmentexercise.keys();
        while (keys.hasNext()) {
            String testName = keys.next();
            try {
                if ("Mobility Test".equalsIgnoreCase(testName)) {
                    JSONObject testDetails = new JSONObject();
                    JSONArray leftlegdata = new JSONArray();
                    JSONArray rightlegdata = new JSONArray();
                    JSONArray graphdata = new JSONArray();
                    ArrayList<Entry> overallEntries1 = new ArrayList<>();
                    ArrayList<Entry> overallEntries2 = new ArrayList<>();

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

                        for (int j = 0; j < graphdata.length(); j++) {
                            overallEntries1.add(new Entry(j, graphdata.getInt(j)));
                            if (max < graphdata.getInt(j)) {
                                max = graphdata.getInt(j);
                            }
                        }
                    }
                    while(keys1.hasNext()){
                        String type = keys1.next();
                        if(type.contains("right-leg")){
                            rightlegdata = testDetails.getJSONArray(type);
                            break;
                        }
                    }

                    if (rightlegdata.length() > 0) {
                        int max = 0;
                        graphdata = rightlegdata.getJSONArray(1);
                        // Create sample data entries for the first line
                        for (int j = 0; j < graphdata.length(); j++) {
                            overallEntries2.add(new Entry(j, graphdata.getInt(j)));
                            if (max < graphdata.getInt(j)) {
                                max = graphdata.getInt(j);
                            }
                        }
                    }

                    // Create the first dataset
                    LineDataSet overallDataSet1 = new LineDataSet(overallEntries1, "Left Leg");
                    overallDataSet1.setColor(0xFF3D5AFE); // Line color
                    overallDataSet1.setValueTextColor(0xFF000000); // Value text color
                    overallDataSet1.setLineWidth(1f); // Line width
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

        // Create sample data entries for the first line

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

    private void loaddoctors() {
        // Initialize the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // URL for the GET request
        String url = "https://api-wo6.onrender.com/get-all-user/doctor"; // Example URL

        // Create the GET Request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            doctordata = response;
                            for (int i = 0; i < response.length(); i++) {
                                // Get each JSONObject within the JSONArray
                                JSONObject post = response.getJSONObject(i);
                                doctorlist.add(post.getString("name"));
                            }
                            doctoradapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("VolleyError", error.toString());
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest);

    }

    private void assigndoctor() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "https://api-wo6.onrender.com/update_flag/" + pid + "/" + 1 + "/" + doctorname + "/" + doctorid + "/" + formattedDate;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the plain string response
                        Log.e("Response", response);
                        Toasty.info(getContext(), "Doctor Assign " + response, Toasty.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Toasty.error(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("Error", String.valueOf(error));
                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // Fetch the response code
                int statusCode = response.statusCode;
                Log.d("Response Code", "Status Code: " + statusCode);

                // Call the superclass method to ensure normal behavior
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(stringRequest);
    }

    private void setBMIValue(float bmiValue) {
        // Map the BMI value to SeekBar progress and update UI
        int progress = calculateProgressFromBMI(bmiValue);
        bmiSeekBar.setProgress(progress);
        updateBMIScale(bmiValue);
    }

    private void updateBMIScale(float bmiValue) {
        health_check.setText(String.format("BMI: %.1f", bmiValue));  // Display the BMI value

        // Update the status based on the BMI value
        if (bmiValue < 18.5) {
            health_check.setText("Underweight");
        } else if (bmiValue >= 18.5 && bmiValue < 25) {
            health_check.setText("You're Healthy");
        } else if (bmiValue >= 25 && bmiValue < 30) {
            health_check.setText("Overweight");
        } else {
            health_check.setText("Obese");
        }
    }
    private int calculateProgressFromBMI(float bmiValue) {
        if (bmiValue <= 18.5f) {
            return (int) mapRange(bmiValue, 15f, 18.5f, 0, 25);
        } else if (bmiValue <= 25f) {
            return (int) mapRange(bmiValue, 18.5f, 25f, 25, 50);
        } else if (bmiValue <= 30f) {
            return (int) mapRange(bmiValue, 25f, 30f, 50, 75);
        } else {
            return (int) mapRange(bmiValue, 30f, 40f, 75, 100);
        }
    }

    private float mapRange(float value, float fromMin, float fromMax, float toMin, float toMax) {
        return toMin + (value - fromMin) * (toMax - toMin) / (fromMax - fromMin);
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
