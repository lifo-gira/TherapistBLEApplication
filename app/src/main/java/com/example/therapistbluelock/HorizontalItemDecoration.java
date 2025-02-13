package com.example.therapistbluelock;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {
    private final int itemOffset;

    public HorizontalItemDecoration(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == 0) {
            outRect.left = itemOffset; // Apply margin to the left of the first item
        } else {
            outRect.left = 0; // No extra margin for other items
        }
        outRect.right = 0; // Optionally, set right margin if needed
    }
}
