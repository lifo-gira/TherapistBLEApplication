package com.example.therapistbluelock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements ItemTouchHelperAdapter {

    private final List<String> deviceList;
    private final OnItemClickListener onItemClickListener;

    public DeviceAdapter(List<String> deviceList, OnItemClickListener onItemClickListener) {
        this.deviceList = deviceList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_available, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        String device = deviceList.get(position);
        String[] deviceParts = device.split("\n");

        if (deviceParts.length >= 2) {
            String deviceName = deviceParts[0].trim();
            String deviceAddress = deviceParts[1].trim();

            holder.deviceName.setText(!deviceName.isEmpty() ? deviceName : "Unknown Device");
            holder.deviceAddress.setText(deviceAddress);

            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(deviceAddress));
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // Move item in the list
        String movedItem = deviceList.remove(fromPosition);
        deviceList.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void clear() {
        deviceList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String deviceAddress);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView deviceName;
        TextView deviceAddress;

        private int originalBackgroundColor;
        private final float originalScaleX;
        private final float originalScaleY;

        DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.available_devices_name);
            deviceAddress = itemView.findViewById(R.id.available_devices_address);

            // Capture the original scale
            originalScaleX = itemView.getScaleX();
            originalScaleY = itemView.getScaleY();

        }

        @Override
        public void onItemSelected() {
            // Provide visual feedback when an item is selected
            itemView.animate().scaleX(1.1f).scaleY(1.1f).start();
        }

        @Override
        public void onItemClear() {
            // Reset visual feedback when an item is released
            itemView.setBackgroundColor(originalBackgroundColor); // Restore original background color
            itemView.animate().scaleX(originalScaleX).scaleY(originalScaleY).start();
        }
    }
}
