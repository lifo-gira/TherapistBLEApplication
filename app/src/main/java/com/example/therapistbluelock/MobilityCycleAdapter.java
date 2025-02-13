package com.example.therapistbluelock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MobilityCycleAdapter extends RecyclerView.Adapter<MobilityCycleAdapter.ExerciseViewHolder1>{

    private final List<MobilityCycleAssessment> exerciseList;
    private final Context context;

    public MobilityCycleAdapter(List<MobilityCycleAssessment> exerciseList, Context context) {
        this.exerciseList = exerciseList;
        this.context = context;
    }

    @NonNull
    @Override
    public MobilityCycleAdapter.ExerciseViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mobility_card, parent, false);
        return new MobilityCycleAdapter.ExerciseViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MobilityCycleAdapter.ExerciseViewHolder1 holder, int position) {
        MobilityCycleAssessment exercise = exerciseList.get(position); // Use ExerciseCycleAssessment

        // Set the Cycle Count based on position (starting from 1)
        holder.cycle_count.setText("Cycle " + (position + 1)); // Display Cycle Count starting from 1

        // Set the Range of Motion text
        holder.maxflex.setText(String.valueOf(exercise.getMaxflexion())); // Convert int to String for display
        holder.minext.setText(String.valueOf(exercise.getMinextension()));
        holder.cycle_type.setText(String.valueOf(exercise.getMode()));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ExerciseViewHolder1 extends RecyclerView.ViewHolder {

        TextView cycle_count; // Reference for Cycle Count
        TextView maxflex,minext,cycle_type; // Reference for Range of Motion
        public ExerciseViewHolder1(@NonNull View itemView) {
            super(itemView);
            cycle_count =itemView.findViewById(R.id.cycle_count);
            maxflex=itemView.findViewById(R.id.maxflex);
            minext=itemView.findViewById(R.id.minext);
            cycle_type=itemView.findViewById(R.id.cycle_type);
        }
    }
}
