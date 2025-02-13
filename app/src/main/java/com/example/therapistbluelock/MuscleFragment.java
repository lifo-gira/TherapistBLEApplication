package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.ValueAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MuscleFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LineChart overall_chart;

    TextView patientname,patientid,patientage,patientgender;

    public MuscleFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_muscle, container, false);

        recyclerView = view.findViewById(R.id.muscle_recycle);
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
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        patientname = view.findViewById(R.id.patientname);
        patientid = view.findViewById(R.id.patientid);
        patientage = view.findViewById(R.id.patientage);
        patientgender = view.findViewById(R.id.patientgender);
        try {
            patientname.setText(MainActivity.selectedpatientdata.getString("patient_name"));
            JSONObject jsonObject = new JSONObject();
            jsonObject = MainActivity.selectedpatientdata.getJSONObject("PersonalDetails");
            patientage.setText(String.valueOf(jsonObject.getInt("Age")));
            patientgender.setText(jsonObject.getString("Gender"));
            patientid.setText(String.valueOf(MainActivity.selectedpatientdata.getString("user_id")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        List<Muscle> muscles = new ArrayList<>();
        muscles.add(new Muscle("Rectus Femoris", "Low", getSampleData()));
        muscles.add(new Muscle("Biceps", "Moderate", getSampleData()));
        muscles.add(new Muscle("Triceps", "High", getSampleData()));
        muscles.add(new Muscle("Deltoid", "High", getSampleData()));

        MuscleAdapter adapter = new MuscleAdapter(muscles);
        recyclerView.setAdapter(adapter);

        // Handle arrow click



        ImageView transitionText = view.findViewById(R.id.transition_text);

        // Make sure the ImageView allows clipping
        transitionText.setClipToOutline(true);
        transitionText.post(() -> animateImageRevealWithFadeIn(transitionText));

        overall_chart = view.findViewById(R.id.just_a_chart);
        setupOverallChart();

        return view;
    }

    private List<Entry> getSampleData() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 20));
        entries.add(new Entry(1, 25));
        entries.add(new Entry(2, 22));
        entries.add(new Entry(3, 28));
        entries.add(new Entry(4, 24));
        entries.add(new Entry(5, 30));
        return entries;
    }

    private void animateImageRevealWithFadeIn(final ImageView imageView) {
        final int imageWidth = imageView.getMeasuredWidth(); // Full width of the ImageView
        final int duration = 1500; // Duration in milliseconds

        // Ensure the ImageView starts fully transparent
        imageView.setAlpha(0f);

        // Use a ValueAnimator to create a left-to-right reveal effect
        ValueAnimator revealAnimator = ValueAnimator.ofInt(0, imageWidth);
        revealAnimator.setDuration(duration);
        revealAnimator.addUpdateListener(animation -> {
            // Update the clipping bounds dynamically
            int animatedWidth = (int) animation.getAnimatedValue();
            imageView.setClipBounds(new Rect(0, 0, animatedWidth, imageView.getHeight()));
        });

        // Simultaneously fade in the image
        imageView.animate().alpha(1f).setDuration(duration).start();

        // Start the reveal animation
        revealAnimator.start();
    }



    private void moveToNextItem() {
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int totalItemCount = layoutManager.getItemCount();

        // Scroll to the next item
        if (firstVisibleItemPosition < totalItemCount - 1) {
            recyclerView.smoothScrollToPosition(firstVisibleItemPosition + 1);
        }

    }

    private void setupOverallChart() {
        // Create sample data entries for the first line
        ArrayList<Entry> overallEntries1 = new ArrayList<>();
        overallEntries1.add(new Entry(0, 20));
        overallEntries1.add(new Entry(1, 25));
        overallEntries1.add(new Entry(2, 22));
        overallEntries1.add(new Entry(3, 28));
        overallEntries1.add(new Entry(4, 24));
        overallEntries1.add(new Entry(5, 30));

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
}
