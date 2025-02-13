package com.example.therapistbluelock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveAssessmentCycleAdapter extends RecyclerView.Adapter<ActiveAssessmentCycleAdapter.ExerciseViewHolder1>{
    private final List<ExerciseCycleAssessment> exerciseList; // Use ExerciseCycleAssessment
    private final Context context;
    static String actpas;
    private OnItemClickListener1 onItemClickListener1;

    public ActiveAssessmentCycleAdapter(Context context, List<ExerciseCycleAssessment> exerciseList, OnItemClickListener1 onItemClickListener1, String actpas) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.onItemClickListener1=onItemClickListener1;
        this.actpas=actpas;
    }

    @NonNull
    @Override
    public ExerciseViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assess_cycle, parent, false);
        return new ExerciseViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder1 holder, int position) {
        ExerciseCycleAssessment exercise = exerciseList.get(position); // Use ExerciseCycleAssessment

        // Set the Cycle Count based on position (starting from 1)
        holder.cycleCount.setText("Cycle " + (position + 1)); // Display Cycle Count starting from 1

        // Set the Range of Motion text
        holder.rangeOfMotion.setText(String.valueOf(exercise.getRangeOfMotion())); // Convert int to String for display

        holder.indivicard.setOnClickListener(v -> {
            if (onItemClickListener1 != null) {
                onItemClickListener1.onItemClick(position);
            }
        });



//        if("Mobility".equalsIgnoreCase(DetailFrag_5.selectedExercise) || "Extension Lag Test".equalsIgnoreCase(DetailFrag_5.selectedExercise) || "Proprioception Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
//            if((position+1)%2 == 0){
//
//                holder.indivicard.setBackgroundResource(R.drawable.extensionbg);
//            }
//            else{
//
//                holder.indivicard.setBackgroundResource(R.drawable.flexionbg);
//            }
//        }

    }

    @Override
    public int getItemCount() {
        Log.e("Extension lag test active", String.valueOf(exerciseList.size()));
        return exerciseList.size();
    }

    public class ExerciseViewHolder1 extends RecyclerView.ViewHolder {
        TextView cycleCount; // Reference for Cycle Count
        TextView rangeOfMotion; // Reference for Range of Motion
        CardView indivicard;

        LinearLayout indivilayout, first_linear,second_linear,sit_to_stand,stand_to_shift,walk_time,steps_covered,ascent_time,decent_time,turn_time,support_unsupport;
        TextView cycle_count,range_of_motion,active_ed,time,maximum_rom;
        public ExerciseViewHolder1(@NonNull View itemView) {
            super(itemView);
            cycleCount = itemView.findViewById(R.id.cycle_count); // Update to the new IDs
            rangeOfMotion = itemView.findViewById(R.id.cyclerom); // Update to the new IDs
            indivicard = itemView.findViewById(R.id.indivicard);
//            Log.e("The active passive",actpas);

            indivilayout=itemView.findViewById(R.id.indivilayout);

            first_linear=itemView.findViewById(R.id.first_linear);
            second_linear=itemView.findViewById(R.id.second_linear);
            sit_to_stand=itemView.findViewById(R.id.sit_to_stand);
            stand_to_shift=itemView.findViewById(R.id.stand_to_shift);
            walk_time=itemView.findViewById(R.id.walk_time);
            steps_covered=itemView.findViewById(R.id.steps_covered);
            ascent_time=itemView.findViewById(R.id.ascent_time);
            decent_time=itemView.findViewById(R.id.decent_time);
            turn_time=itemView.findViewById(R.id.turn_time);
            support_unsupport=itemView.findViewById(R.id.support_unsupport);
            cycle_count=itemView.findViewById(R.id.cycle_count);
            range_of_motion=itemView.findViewById(R.id.range_of_motion);
            active_ed=itemView.findViewById(R.id.active_ed);
            time=itemView.findViewById(R.id.time);
            maximum_rom=itemView.findViewById(R.id.maximum_rom);

            Log.e("Inside active assessment",DetailFrag_5.selectedExercise);

            if("Mobility Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.VISIBLE);
                cycle_count.setVisibility(View.VISIBLE);
                range_of_motion.setVisibility(View.VISIBLE);
                active_ed.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.GONE);
                stand_to_shift.setVisibility(View.GONE);
                walk_time.setVisibility(View.GONE);
                steps_covered.setVisibility(View.GONE);
                ascent_time.setVisibility(View.GONE);
                decent_time.setVisibility(View.GONE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.GONE);
            }
            else if("Extension Lag Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.VISIBLE);
                cycle_count.setVisibility(View.VISIBLE);
                range_of_motion.setVisibility(View.GONE);
                active_ed.setVisibility(View.VISIBLE);
                if("active".equalsIgnoreCase(actpas)){
                    active_ed.setText("Active ED");
                }else if("passive".equalsIgnoreCase(actpas)){
                    active_ed.setText("Passive ED");
                }else{
                    active_ed.setText("Total ED");
                }
                time.setVisibility(View.GONE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.GONE);
                stand_to_shift.setVisibility(View.GONE);
                walk_time.setVisibility(View.GONE);
                steps_covered.setVisibility(View.GONE);
                ascent_time.setVisibility(View.GONE);
                decent_time.setVisibility(View.GONE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.GONE);
            }
            else if("Dynamic Balance Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.GONE);
                cycle_count.setVisibility(View.GONE);
                range_of_motion.setVisibility(View.GONE);
                active_ed.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.VISIBLE);
                stand_to_shift.setVisibility(View.VISIBLE);
                walk_time.setVisibility(View.VISIBLE);
                steps_covered.setVisibility(View.GONE);
                ascent_time.setVisibility(View.GONE);
                decent_time.setVisibility(View.GONE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.VISIBLE);
            }
            else if("Static Balance Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.VISIBLE);
                cycle_count.setVisibility(View.VISIBLE);
                range_of_motion.setVisibility(View.GONE);
                active_ed.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.GONE);
                stand_to_shift.setVisibility(View.GONE);
                walk_time.setVisibility(View.GONE);
                steps_covered.setVisibility(View.GONE);
                ascent_time.setVisibility(View.GONE);
                decent_time.setVisibility(View.GONE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.GONE);
            }
            else if("Staircase Climbing Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.GONE);
                cycle_count.setVisibility(View.GONE);
                range_of_motion.setVisibility(View.GONE);
                active_ed.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.GONE);
                stand_to_shift.setVisibility(View.GONE);
                walk_time.setVisibility(View.GONE);
                steps_covered.setVisibility(View.VISIBLE);
                ascent_time.setVisibility(View.VISIBLE);
                decent_time.setVisibility(View.VISIBLE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.VISIBLE);
            }
            else if("Walk and Gait Analysis".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.VISIBLE);
                cycle_count.setVisibility(View.VISIBLE);
                range_of_motion.setVisibility(View.VISIBLE);
                active_ed.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                maximum_rom.setVisibility(View.GONE);
                sit_to_stand.setVisibility(View.GONE);
                stand_to_shift.setVisibility(View.GONE);
                walk_time.setVisibility(View.GONE);
                steps_covered.setVisibility(View.GONE);
                ascent_time.setVisibility(View.GONE);
                decent_time.setVisibility(View.GONE);
                turn_time.setVisibility(View.GONE);
                support_unsupport.setVisibility(View.GONE);
            }
        }
    }
    public interface OnItemClickListener1 {
        void onItemClick(int position);
    }
}
