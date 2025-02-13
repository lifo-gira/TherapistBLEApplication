package com.example.therapistbluelock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NewPatientAdapter extends RecyclerView.Adapter<NewPatientAdapter.ViewHolder> {

    private List<NewPatient> newPatients = new ArrayList<>();

    public void addPatient(NewPatient patient) {
        newPatients.add(patient);
        notifyItemInserted(newPatients.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewPatient patient = newPatients.get(position);
        holder.nameTextView.setText(patient.getName());
        holder.conditionTextView.setText(patient.getCondition());
        holder.imageView.setImageResource(patient.getImageResource());
    }

    @Override
    public int getItemCount() {
        return newPatients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView conditionTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            conditionTextView = itemView.findViewById(R.id.conditionTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
