package com.example.therapistbluelock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RulerAdapter extends RecyclerView.Adapter<RulerAdapter.ViewHolder> {

    private List<Integer> heightList;
    private Context context;

    public RulerAdapter(Context context, List<Integer> heightList) {
        this.context = context;
        this.heightList = heightList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ruler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // You can implement logic here if you decide to display something else
        // For example, changing tick marks based on height values if needed
    }

    @Override
    public int getItemCount() {
        return heightList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // No TextView reference needed anymore

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // No need to find any views since there are no TextViews
        }
    }
}
