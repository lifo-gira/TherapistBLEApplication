package com.example.therapistbluelock;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Walkgaittestadapter extends RecyclerView.Adapter<Walkgaittestadapter.ExerciseViewHolder>{

    private final List<Walkgaittestdata> exerciseList; // Use ExerciseCycleAssessment
    private final Context context;
    ArrayAdapter<String> swing,stance,stride,stridepercent,step,cadence;

    List<String> swingtime = new ArrayList<>();
    List<String> stridele = new ArrayList<>();
    List<String> strideper = new ArrayList<>();

    public Walkgaittestadapter(List<Walkgaittestdata> exerciseList, Context context) {
        this.exerciseList = exerciseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.walk_and_gait_cycle, parent, false);
        return new ExerciseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {


        Walkgaittestdata exercise = exerciseList.get(position);
        Log.e("Inside walkgaittestadapter", String.valueOf(exercise.getTotalDistance()));

        int ind = position+1;

        holder.cycle_count.setText("Cycle Count "+ind);
        holder.distance.setText(String.valueOf(exercise.getTotalDistance()+" m"));
        holder.step_count.setText(String.valueOf(exercise.getStepCountwalk()));
        holder.walk_break.setText(String.valueOf(exercise.getBreakcount()));
        holder.active_time.setText(String.valueOf(exercise.getActiveTime())+" Sec");
        holder.machine_time.setText("0 Sec");
        holder.stand_time.setText(String.valueOf(exercise.getAvgStandtime())+" Sec");
        //holder.avg_swing_time.setText(String.valueOf(exercise.getAvgSwingtime())+" Sec");

        swingtime.clear();
        stridele.clear();
        strideper.clear();

        if(exercise.getStance().size()>0) {
            stance = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    exercise.getStance()
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                    return view;
                }
            };

            stance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.stance_phase_spinner.setAdapter(stance);
        }
        else{
            List<String> st = new ArrayList<>();
            st.add("0 0");
            stance = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    st
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                    return view;
                }
            };

            stance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.stance_phase_spinner.setAdapter(stance);
        }

        step = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                exercise.getStep()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                return view;
            }
        };

        step.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.step_length_spinner.setAdapter(step);

        swing = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                exercise.getSwingtime()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                return view;
            }
        };

        swing.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.swing_time_spinner.setAdapter(swing);


        stride = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                exercise.getStride()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                return view;
            }
        };

        stride.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.stride_length_spinner.setAdapter(stride);
        stridepercent = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                exercise.getStride()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK); // Set text color to black for selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE); // Set text color to white for dropdown items
                return view;
            }
        };

        stridepercent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.stride_length_percentage_h_spinner.setAdapter(stridepercent);

        //holder.stance_phase.setText(String.valueOf(exercise.getAvgStancetime())+" Sec");
        //holder.stride_length.setText(String.valueOf(exercise.getStrideLength())+"m");
        //holder.stride_length_percentage_h.setText("0.0m");
        //holder.step_length.setText("0.0m");
        holder.mean_velocity.setText(String.valueOf(exercise.getMeanVelocity())+"m/s");
        holder.cadence.setText(String.valueOf(exercise.getCade())+" Steps");

    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView cycle_count,distance,step_count,walk_break,active_time,machine_time,stand_time,avg_swing_time,stance_phase,stride_length,stride_length_percentage_h,step_length,mean_velocity,cadence;
        Spinner swing_time_spinner,stance_phase_spinner,stride_length_spinner,stride_length_percentage_h_spinner,step_length_spinner,cadence_spinner;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cycle_count=itemView.findViewById(R.id.cycle_count);
            distance=itemView.findViewById(R.id.distance);
            step_count=itemView.findViewById(R.id.step_count);
            walk_break=itemView.findViewById(R.id.walk_break);
            active_time=itemView.findViewById(R.id.active_time);
            machine_time=itemView.findViewById(R.id.machine_time);
            stand_time=itemView.findViewById(R.id.stand_time);
            avg_swing_time=itemView.findViewById(R.id.avg_swing_time);
            stance_phase=itemView.findViewById(R.id.stance_phase);
            stride_length=itemView.findViewById(R.id.stride_length);
            stride_length_percentage_h=itemView.findViewById(R.id.stride_length_percentage_h);
            step_length=itemView.findViewById(R.id.step_length);
            mean_velocity=itemView.findViewById(R.id.mean_velocity);
            cadence=itemView.findViewById(R.id.cadence);

            swing_time_spinner = itemView.findViewById(R.id.swing_time_spinner);
            stance_phase_spinner = itemView.findViewById(R.id.stance_phase_spinner);
            stride_length_spinner = itemView.findViewById(R.id.stride_length_spinner);
            stride_length_percentage_h_spinner = itemView.findViewById(R.id.stride_length_percentage_h_spinner);
            step_length_spinner = itemView.findViewById(R.id.step_length_spinner);



        }
    }
}
