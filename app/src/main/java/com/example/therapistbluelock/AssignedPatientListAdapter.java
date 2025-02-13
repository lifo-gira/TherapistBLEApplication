package com.example.therapistbluelock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignedPatientListAdapter extends RecyclerView.Adapter<AssignedPatientListAdapter.AssignedPatientViewHolder> {

    private final List<AssignedPatientList> patientList;
    private OnReportIconClickListener listener;

    public interface OnReportIconClickListener {
        void onReportIconClick(String pid);
    }

    public AssignedPatientListAdapter(List<AssignedPatientList> patientList, OnReportIconClickListener listener) {
        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignedPatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignedpatientlist, parent, false);
        return new AssignedPatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignedPatientViewHolder holder, int position) {
        AssignedPatientList patient = patientList.get(position);

        // Bind data to views
        holder.patientName.setText(patient.getName());
        holder.patientAge.setText(String.valueOf(patient.getAge()));
        holder.patientGender.setText(patient.getGender());
        holder.patientIssue.setText(patient.getIssue());
        holder.patientId.setText("ID: " + patient.getPatientid());

        // Set image resource for patient image
        holder.patientImage.setImageResource(patient.getImageResource());
        // Set the report icon image
        holder.reportImage.setImageResource(R.drawable.report_icon);

        // Set the report icon click listener
        holder.reportImage.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                listener.onReportIconClick(patient.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    // ViewHolder class to hold the views
    static class AssignedPatientViewHolder extends RecyclerView.ViewHolder {
        ImageView patientImage, reportImage;
        TextView patientName, patientAge, patientGender, patientIssue, patientId;

        public AssignedPatientViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            patientImage = itemView.findViewById(R.id.patient_image);
            reportImage = itemView.findViewById(R.id.report_icon);
            patientName = itemView.findViewById(R.id.patient_name);
            patientAge = itemView.findViewById(R.id.patient_age);
            patientGender = itemView.findViewById(R.id.patient_gender);
            patientIssue = itemView.findViewById(R.id.diagno_text);
            patientId = itemView.findViewById(R.id.patient_id);
        }
    }
}
