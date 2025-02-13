package com.example.therapistbluelock;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecorationVertical extends RecyclerView.ItemDecoration {

    private int verticalSpacing;
    private int horizontalSpacing;

    public ItemSpacingDecorationVertical(int verticalSpacing, int horizontalSpacing) {
        this.verticalSpacing = verticalSpacing;
        this.horizontalSpacing = horizontalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Vertical space
        outRect.top = verticalSpacing;
        outRect.bottom = verticalSpacing;

        // Horizontal space (add it only for the left and right sides, except for the first item in a row)
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        if (layoutParams.getSpanIndex() != 0) { // Not the first column
            outRect.left = horizontalSpacing;
        }
        outRect.right = horizontalSpacing;  // Apply horizontal space to the right side
    }
}
