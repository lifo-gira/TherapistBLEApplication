package com.example.therapistbluelock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class MuscleAdapter extends RecyclerView.Adapter<MuscleAdapter.MuscleViewHolder> {

    private final List<Muscle> muscleList;

    public MuscleAdapter(List<Muscle> muscleList) {
        this.muscleList = muscleList;
    }

    @NonNull
    @Override
    public MuscleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.muscle_recycle_card, parent, false);
        return new MuscleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuscleViewHolder holder, int position) {
        Muscle muscle = muscleList.get(position);
        holder.muscleName.setText(muscle.getName());
        holder.muscleLevel.setText(muscle.getLevel());

        // Configure the LineChart
        LineDataSet dataSet = new LineDataSet(muscle.getChartData(), muscle.getName());
        dataSet.setColor(0xFF3D5AFE); // Line color
        dataSet.setValueTextColor(0xFF000000); // Value text color
        dataSet.setLineWidth(1f); // Line width
        dataSet.setCircleColor(0xFFFFD383); // Circle color at data points
        dataSet.setCircleRadius(4f); // Circle radius
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Make the graph curved
        dataSet.setDrawFilled(true); // Enable fill under the line
        dataSet.setFillColor(0xFDBA5FF); // Fill color under the line
        dataSet.setFillAlpha(70); // Adjust transparency of the fill

        LineData lineData = new LineData(dataSet);
        holder.muscleChart.setData(lineData);

        // Style the chart
        holder.muscleChart.getDescription().setEnabled(false);
        holder.muscleChart.setDrawGridBackground(false);
        holder.muscleChart.setTouchEnabled(true);
        holder.muscleChart.setPinchZoom(true);

        // Remove grid and axis lines
        XAxis xAxis = holder.muscleChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = holder.muscleChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = holder.muscleChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Configure the legend
        Legend legend = holder.muscleChart.getLegend();
        legend.setEnabled(false);

        // Refresh the chart
        holder.muscleChart.invalidate();
    }


    @Override
    public int getItemCount() {
        return muscleList.size();
    }

    public static class MuscleViewHolder extends RecyclerView.ViewHolder {
        TextView muscleName;
        TextView muscleLevel;
        LineChart muscleChart;

        public MuscleViewHolder(@NonNull View itemView) {
            super(itemView);
            muscleName = itemView.findViewById(R.id.muscle_name);
            muscleLevel = itemView.findViewById(R.id.muscle_level);
            muscleChart = itemView.findViewById(R.id.rectus_chart);
        }
    }
}
