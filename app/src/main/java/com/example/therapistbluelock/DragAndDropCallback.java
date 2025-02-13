package com.example.therapistbluelock;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Canvas;

import java.util.List;

public class DragAndDropCallback extends ItemTouchHelper.Callback {

    private final ExerciseAdapter exerciseAdapter;
    private final ExerciseAdapter assignedExerciseAdapter;
    private final List<Exercise> exercises;
    private final List<Exercise> assignedExercises;

    public DragAndDropCallback(
            ExerciseAdapter exerciseAdapter,
            ExerciseAdapter assignedExerciseAdapter,
            List<Exercise> exercises,
            List<Exercise> assignedExercises) {
        this.exerciseAdapter = exerciseAdapter;
        this.assignedExerciseAdapter = assignedExerciseAdapter;
        this.exercises = exercises;
        this.assignedExercises = assignedExercises;
    }

    // Enable long press drag
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // Disable swipe to avoid item being removed via swipe
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    // This method defines the movement flags for the drag and drop functionality
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // Allow dragging up and down
        return makeMovementFlags(dragFlags, 0); // 0 because we are not using swipe actions
    }

    // This method handles the actual movement of an item
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        // If dragging from the exercise list
        if (recyclerView.getId() == R.id.exercise_recycle) {
            Exercise movedExercise = exercises.get(fromPosition);
            exercises.remove(fromPosition);
            exercises.add(toPosition, movedExercise);
            exerciseAdapter.notifyItemMoved(fromPosition, toPosition);
        }
        // If dragging from the assigned exercise list
        else if (recyclerView.getId() == R.id.assigned_exercise_recycle) {
            Exercise movedExercise = assignedExercises.get(fromPosition);
            assignedExercises.remove(fromPosition);
            assignedExercises.add(toPosition, movedExercise);
            assignedExerciseAdapter.notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    // This method handles the swipe action (not used here)
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Handle swipe if needed (e.g., for delete)
    }

    // Handle drawing the item while it's being dragged (optional customization)
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
