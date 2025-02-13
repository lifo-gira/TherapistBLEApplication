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

public class PatientsAssignedAdapter extends RecyclerView.Adapter<PatientsAssignedAdapter.PatientViewHolder> {

    private List<PatientsAssigned> patients = new ArrayList<>();
    private OnReportIconClickListener listener;

    public interface OnReportIconClickListener {
        void onReportIconClick(String pid);
    }

    public PatientsAssignedAdapter(OnReportIconClickListener listener,List<PatientsAssigned> patientsAssigned) {
        this.listener = listener;
        this.patients = patientsAssigned;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patients_assigned, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientsAssigned patient = patients.get(position);
        holder.patientName.setText(patient.getName());
        holder.patientAge.setText(String.valueOf(patient.getAge()));
        holder.patientGender.setText(patient.getGender());
        holder.patientId.setText("ID: " + patient.getPatientid());
        holder.diagnoText.setText(patient.getDiagnosis());
        holder.patientImage.setImageResource(patient.getImageResource());
        holder.reportIcon.setImageResource(R.drawable.report_icon);

        holder.reportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onReportIconClick(patient.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void addPatient(PatientsAssigned patient) {
        patients.add(patient);
        notifyDataSetChanged();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientId, diagnoText, patientAge, patientGender;
        ImageView patientImage, reportIcon;

        public PatientViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            patientAge = itemView.findViewById(R.id.patient_age);
            patientGender = itemView.findViewById(R.id.patient_gender);
            patientId = itemView.findViewById(R.id.patient_id);
            diagnoText = itemView.findViewById(R.id.diagno_text);
            patientImage = itemView.findViewById(R.id.patient_image);
            reportIcon = itemView.findViewById(R.id.report_icon);
        }
    }
}