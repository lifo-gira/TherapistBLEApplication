package com.example.therapistbluelock;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailCollectionAdapter extends RecyclerView.Adapter<DetailCollectionAdapter.ViewHolder> {

    private List<DetailItem> dataList;
    private OnItemClickListener listener;

    public DetailCollectionAdapter(List<DetailItem> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detailcollectionlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailItem item = dataList.get(position);
        holder.textView.setText(item.getItemType());
        holder.statusText.setText(item.getStatus());
        holder.imageView.setImageResource(item.getImageResId());

        // Set background color based on the tint color (green for completed)
        holder.cardIndicator.setBackgroundTintList(ColorStateList.valueOf(item.getBackgroundTint()));

        // Set up click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item); // Call the listener
                item.markAsCompleted(); // Mark as completed
                notifyItemChanged(position); // Notify that this item has been updated
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DetailItem item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView statusText;
        ImageView imageView;
        CardView cardIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.detail_text);
            statusText = itemView.findViewById(R.id.status_text);
            imageView = itemView.findViewById(R.id.detail_image);
            cardIndicator = itemView.findViewById(R.id.card_indicator);
        }
    }
}
