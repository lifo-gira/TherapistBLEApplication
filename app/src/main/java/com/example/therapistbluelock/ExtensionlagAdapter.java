package com.example.therapistbluelock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExtensionlagAdapter extends RecyclerView.Adapter<ExtensionlagAdapter.ExerciseViewHolder> {

    private final List<ExtensionlagCycleAssessment> exerciseList; // Use ExerciseCycleAssessment
    private final Context context;

    public ExtensionlagAdapter(List<ExtensionlagCycleAssessment> exerciseList, Context context) {
        this.exerciseList = exerciseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExtensionlagAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.extensionlagcard, parent, false);
        return new ExtensionlagAdapter.ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtensionlagAdapter.ExerciseViewHolder holder, int position) {
        ExtensionlagCycleAssessment exercise = exerciseList.get(position); // Use ExerciseCycleAssessment

        // Set the Cycle Count based on position (starting from 1)
        holder.cycleCount.setText("Cycle " + (position + 1)); // Display Cycle Count starting from 1

        // Set the Range of Motion text
        holder.activeed.setText(String.valueOf(exercise.getActiveed())); // Convert int to String for display
        holder.passiveed.setText(String.valueOf(exercise.getPassiveed())); // Convert int to String for display
        holder.totaled.setText(String.valueOf(exercise.getTotaled())); // Convert int to String for display

    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView cycleCount; // Reference for Cycle Count
        TextView activeed,passiveed,totaled; // Reference for Range of Motion


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            cycleCount = itemView.findViewById(R.id.cycle_count); // Update to the new IDs
            activeed = itemView.findViewById(R.id.activeed); // Update to the new IDs
            passiveed = itemView.findViewById(R.id.passiveed);
            totaled = itemView.findViewById(R.id.totaled);




        }
    }
}
