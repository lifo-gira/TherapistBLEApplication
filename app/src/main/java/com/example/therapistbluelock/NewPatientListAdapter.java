package com.example.therapistbluelock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewPatientListAdapter extends RecyclerView.Adapter<NewPatientListAdapter.NewPatientViewHolder> {

    private List<NewPatientList> newPatientList;
    private OnItemClickListener onItemClickListener1;

    public NewPatientListAdapter(List<NewPatientList> newPatientList, OnItemClickListener onItemClickListener1) {
        this.newPatientList = newPatientList;
        this.onItemClickListener1 = onItemClickListener1;
    }

    @NonNull
    @Override
    public NewPatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_patient_item, parent, false);  // Inflate your new_patient_item layout
        return new NewPatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPatientViewHolder holder, int position) {
        NewPatientList patient = newPatientList.get(position);

        // Set patient name, image, and ID
        holder.patientName.setText(patient.getName());
        holder.patientId.setText("ID: " + patient.getId());
        holder.patientImage.setImageResource(patient.getImage());
        holder.newpatcard.setOnClickListener(v -> {
            if (onItemClickListener1 != null) {
                onItemClickListener1.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newPatientList.size();
    }

    public static class NewPatientViewHolder extends RecyclerView.ViewHolder {
        ImageView patientImage;
        TextView patientName, patientId;
        LinearLayout newpatcard;

        public NewPatientViewHolder(View itemView) {
            super(itemView);
            patientImage = itemView.findViewById(R.id.patient_image);
            patientName = itemView.findViewById(R.id.patient_name);
            patientId = itemView.findViewById(R.id.patient_id);
            newpatcard = itemView.findViewById(R.id.newpatcard);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
