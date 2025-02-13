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

public class Dynamicbalanceadapter extends RecyclerView.Adapter<Dynamicbalanceadapter.ExerciseViewHolder>{
    private final List<Dynamicbalancetestdata> exerciseList; // Use ExerciseCycleAssessment
    private final Context context;

    public Dynamicbalanceadapter(Context context, List<Dynamicbalancetestdata> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assess_cycle, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Dynamicbalancetestdata exercise = exerciseList.get(position); // Use ExerciseCycleAssessment
        Log.e("The dynamic balance data", String.valueOf(exercise.getSittostand()));

        // Set the Cycle Count based on position (starting from 1)
        holder.cycleCount.setText("Cycle " + (position + 1)); // Display Cycle Count starting from 1

        // Set the Range of Motion text
        holder.sittostandvalue.setText((exercise.getSittostand())+"Sec"); // Convert int to String for display
        holder.standtoshiftvalue.setText((exercise.getStandtoshift())+"Sec");
        holder.walkvalue.setText((exercise.getWalktime())+"Sec");

        if("active".equalsIgnoreCase(exercise.getActpas())){
            holder.supportvalue.setText("Without Support");
        }else{
            holder.supportvalue.setText("With Support");
        }

//        holder.indivicard.setOnClickListener(v -> {
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView cycleCount; // Reference for Cycle Count
        TextView rangeOfMotion; // Reference for Range of Motion
        CardView indivicard;

        LinearLayout first_linear,second_linear,sit_to_stand,stand_to_shift,walk_time,steps_covered,ascent_time,decent_time,turn_time,support_unsupport;
        TextView range_of_motion,active_ed,time,maximum_rom,sittostandtext,sittostandvalue,standtoshifttext,standtoshiftvalue,walktext,walkvalue,supportvalue;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cycleCount = itemView.findViewById(R.id.cycle_count); // Update to the new IDs
            rangeOfMotion = itemView.findViewById(R.id.cyclerom); // Update to the new IDs
            indivicard = itemView.findViewById(R.id.indivicard);

            first_linear=itemView.findViewById(R.id.first_linear);
            second_linear=itemView.findViewById(R.id.second_linear);
            sit_to_stand=itemView.findViewById(R.id.sit_to_stand);
            sittostandtext=itemView.findViewById(R.id.sittostandtext);
            sittostandvalue=itemView.findViewById(R.id.sittostandvalue);
            stand_to_shift=itemView.findViewById(R.id.stand_to_shift);
            standtoshifttext=itemView.findViewById(R.id.standtoshifttext);
            standtoshiftvalue=itemView.findViewById(R.id.standtoshiftvalue);
            walk_time=itemView.findViewById(R.id.walk_time);
            walktext=itemView.findViewById(R.id.walktext);
            walkvalue=itemView.findViewById(R.id.walkvalue);
            steps_covered=itemView.findViewById(R.id.steps_covered);
            ascent_time=itemView.findViewById(R.id.ascent_time);
            decent_time=itemView.findViewById(R.id.decent_time);
            turn_time=itemView.findViewById(R.id.turn_time);
            support_unsupport=itemView.findViewById(R.id.support_unsupport);
            range_of_motion=itemView.findViewById(R.id.range_of_motion);
            active_ed=itemView.findViewById(R.id.active_ed);
            time=itemView.findViewById(R.id.time);
            maximum_rom=itemView.findViewById(R.id.maximum_rom);
            supportvalue=itemView.findViewById(R.id.supportvalue);

            first_linear.setVisibility(View.VISIBLE);
            second_linear.setVisibility(View.GONE);
            cycleCount.setVisibility(View.VISIBLE);
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
            sittostandtext.setVisibility(View.VISIBLE);
            sittostandvalue.setVisibility(View.VISIBLE);
            standtoshifttext.setVisibility(View.VISIBLE);
            standtoshiftvalue.setVisibility(View.VISIBLE);
            walktext.setVisibility(View.VISIBLE);
            walkvalue.setVisibility(View.VISIBLE);

        }
    }
}
