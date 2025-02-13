package com.example.therapistbluelock;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;
    private RegimeFragment regimeFragment;
    private boolean showDeleteIcon;
    Context context;

    public ExerciseAdapter(RegimeFragment regimeFragment, List<Exercise> exercises, boolean showDeleteIcon,Context context) {
        this.regimeFragment = regimeFragment;
        this.exercises = exercises;
        this.showDeleteIcon = showDeleteIcon;
        this.context = context;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the exercise card layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_selection_card, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        // Set values for the views in the ViewHolder
        holder.nameTextView.setText(exercise.getName());
        holder.videoImageView.setImageResource(R.drawable.model_video_icon); // Set video icon (can be changed)
        holder.exerciseImageView.setImageResource(exercise.getImageResId());

        // Set initial values to 0
        holder.repNumberPicker.setValue(0); // Set initial value for reps to 0
        holder.setNumberPicker.setValue(0); // Set initial value for sets to 0

        // Optionally set min and max values for the NumberPickers
        holder.repNumberPicker.setMinValue(1);
        holder.repNumberPicker.setMaxValue(100);  // Set an upper limit for reps
        holder.setNumberPicker.setMinValue(1);
        holder.setNumberPicker.setMaxValue(50);  // Set an upper limit for sets



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.repNumberPicker.setTextColor(Color.BLACK);
            holder.setNumberPicker.setTextColor(Color.BLACK);
            holder.setNumberPicker.setTextSize(33);
            holder.repNumberPicker.setTextSize(33);
            holder.setNumberPicker.setClickable(false);
            holder.setNumberPicker.setFocusable(false);
            holder.setNumberPicker.setFocusableInTouchMode(false);
            holder.setNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            holder.repNumberPicker.setClickable(false);
            holder.repNumberPicker.setFocusable(false);
            holder.repNumberPicker.setFocusableInTouchMode(false);
            holder.repNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
        setNumberPickerTextColor(holder.repNumberPicker);
        setNumberPickerTextColor(holder.setNumberPicker);

        // Update the values of the NumberPickers when user changes them
        holder.repNumberPicker.setValue(exercise.getRepCount()); // Set the saved rep count value
        holder.setNumberPicker.setValue(exercise.getSetCount()); // Set the saved set count value

        holder.videoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.demovideo);
                VideoView demovid = dialog.findViewById(R.id.demovid);
                int videoUrl=0;
                if(exercise.getName().equalsIgnoreCase("Left-Knee-Bend")){
                    videoUrl = R.raw.leftlegdemo;
                }
                else if(exercise.getName().equalsIgnoreCase("Right-Knee-Bend")){
                    videoUrl = R.raw.rightlegdemo;
                }
                else if(exercise.getName().equalsIgnoreCase("Right-Leg-Bend")){
                    videoUrl = R.raw.rightdemo;
                }
                else if(exercise.getName().equalsIgnoreCase("Left-Leg-Bend")){
                    videoUrl = R.raw.leftdemo;
                }
                else if(exercise.getName().equalsIgnoreCase("Sit-Stand")){
                    videoUrl = R.raw.sitstanddemo;
                }
                Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + videoUrl);
                demovid.setVideoURI(videoUri);
                demovid.setOnCompletionListener(mp -> dialog.dismiss());
                demovid.start();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });

        // Handle visibility of delete icon based on the flag
        if (showDeleteIcon) {
            holder.deleteIcon.setVisibility(View.VISIBLE); // Show delete icon in assigned exercises
        } else {
            holder.deleteIcon.setVisibility(View.INVISIBLE); // Hide delete icon in other RecyclerView
        }

        // Enable dragging for each exercise item
        holder.cardView.setOnLongClickListener(v -> {
            // Start the drag operation on the CardView
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        // Delete icon functionality
        // Delete icon functionality
        holder.deleteIcon.setOnClickListener(v -> {
            // Remove the exercise from the list
            exercises.remove(position);
            notifyItemRemoved(position);  // Notify the adapter that an item was removed
            notifyItemRangeChanged(position, exercises.size());  // Notify the adapter of the change

            // Notify the RegimeFragment to update the main list
            if (regimeFragment != null) {
                regimeFragment.removeExerciseFromAssignedList(exercise);  // Pass the exercise to be removed
            }

            // Update the background for the category after removal
            regimeFragment.changeCategoryBackground(exercise.getCategory());
        });


        // Listen for value changes on NumberPickers and update the Exercise object
        holder.repNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            exercise.setRepCount(newVal);  // Update the Exercise object with new rep count
        });

        holder.setNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            exercise.setSetCount(newVal);  // Update the Exercise object with new set count
        });
    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker) {
        for (int i = 0; i < numberPicker.getChildCount(); i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setTextColor(Color.BLACK);
            }
        }

        // Handle value change
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            for (int i = 0; i < numberPicker.getChildCount(); i++) {
                View child = numberPicker.getChildAt(i);
                if (child instanceof EditText) {
                    EditText editText = (EditText) child;
                    editText.setTextColor(Color.BLACK);
                }
            }
        });
    }

    public void moveItem(int fromPosition, int toPosition) {
        Exercise movedExercise = exercises.remove(fromPosition);
        exercises.add(toPosition, movedExercise);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateExerciseList(List<Exercise> newExercises) {
        this.exercises = newExercises;  // Update the adapter's list with new exercises
        notifyDataSetChanged();  // Notify the adapter that the data has changed and the view should be updated
    }

    // Method to handle item movement during drag-and-drop
    public void onItemMove(int fromPosition, int toPosition) {
        Exercise fromExercise = exercises.remove(fromPosition);
        exercises.add(toPosition, fromExercise);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView videoImageView;
        ImageView exerciseImageView;
        NumberPicker repNumberPicker;
        NumberPicker setNumberPicker;
        CardView cardView;
        ImageView deleteIcon;

        public ExerciseViewHolder(View itemView) {
            super(itemView);

            // Initialize all views for the exercise card layout
            nameTextView = itemView.findViewById(R.id.exercise_name);
            videoImageView = itemView.findViewById(R.id.exercise_video);
            exerciseImageView = itemView.findViewById(R.id.exercise_image);
            repNumberPicker = itemView.findViewById(R.id.rep_count);
            setNumberPicker = itemView.findViewById(R.id.set_count);
            cardView = itemView.findViewById(R.id.exercise_card);
            deleteIcon = itemView.findViewById(R.id.delete_icon);

            // Initially hide the delete icon
            deleteIcon.setVisibility(View.INVISIBLE);
        }
    }
}