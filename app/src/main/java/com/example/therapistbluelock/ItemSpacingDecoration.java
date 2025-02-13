package com.example.therapistbluelock;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private int space;

    // Constructor to set space between items
    public ItemSpacingDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Set space for each item
        outRect.top = space;   // Set top spacing
        outRect.bottom = space; // Set bottom spacing

        // Add left and right spacing if needed
        outRect.left = space;
        outRect.right = space;
    }
}
