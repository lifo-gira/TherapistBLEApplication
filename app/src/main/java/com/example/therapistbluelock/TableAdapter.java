package com.example.therapistbluelock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ExerciseViewHolder> {

    private List<Table> exerciseList;

    public TableAdapter(List<Table> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the exercise_item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Table exercise = exerciseList.get(position);

        // Set the exercise name
        holder.exerciseNameTextView.setText(exercise.getName());

        // Set up the TableLayout for this exercise
        holder.tlResult.removeAllViews();  // Clear previous content

        // Add header row to TableLayout
        TableRow headerRow = new TableRow(holder.itemView.getContext());
        for (String header : exercise.getHeaders()) {
            TextView headerTextView = createTextView(holder.itemView.getContext(), header, true); // Use header style
            headerRow.addView(headerTextView);
        }
        holder.tlResult.addView(headerRow);

        // Add data rows to TableLayout
        for (TableDetail detail : exercise.getDetails()) {
            TableRow dataRow = new TableRow(holder.itemView.getContext());
            for (String key : exercise.getHeaders()) {
                String value = detail.getParams().get(key);
                TextView dataTextView = createTextView(holder.itemView.getContext(), value, false); // Use data style
                dataRow.addView(dataTextView);
            }
            holder.tlResult.addView(dataRow);
        }

        // Set overall result (if any)
        String overallResult = "Pass";  // This can be dynamic based on data
        holder.tvOverallResult.setText(overallResult);

        // Set the onClickListener to show the details layout when 'Cycles' is clicked
        holder.cyclesTextView.setOnClickListener(v -> {
            if (holder.detailLayout.getVisibility() == View.GONE) {
                // Show details and change the arrow to up
                holder.detailLayout.setVisibility(View.VISIBLE);
                holder.cyclesTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            } else {
                // Hide details and change the arrow to down
                holder.detailLayout.setVisibility(View.GONE);
                holder.cyclesTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        // Initially hide the detail layout and set the arrow to down
        holder.detailLayout.setVisibility(View.GONE);
        holder.cyclesTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    // ViewHolder class to hold references to the views
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        TextView cyclesTextView;
        TableLayout tlResult;
        TextView tvOverallResult;
        LinearLayout detailLayout;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name);
            cyclesTextView = itemView.findViewById(R.id.cycles);
            tlResult = itemView.findViewById(R.id.tl_result);
            tvOverallResult = itemView.findViewById(R.id.tv_overallResult);
            detailLayout = itemView.findViewById(R.id.detail_layout);
        }
    }

    // Helper method to create TextViews for TableRow with context
    private TextView createTextView(Context context, String text, boolean isHeader) {
        TextView textView = new TextView(context);  // Pass context explicitly
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);

        // Set text color to black for both headers and values
        textView.setTextColor(Color.BLACK);

        // Set the background depending on whether it's a header or data
        if (isHeader) {
            textView.setBackgroundResource(R.drawable.header_border);  // Use the header_border drawable for headers
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);  // Center align header text
        } else {
            textView.setBackgroundResource(R.drawable.border_layout);  // Use the border_layout drawable for data cells
        }

        return textView;
    }
}
