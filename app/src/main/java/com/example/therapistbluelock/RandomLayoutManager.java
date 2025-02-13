package com.example.therapistbluelock;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLayoutManager extends RecyclerView.LayoutManager {

    private final int radiusPx; // Radius of the circular layout
    private final int paddingPx; // Padding in pixels
    private final Random random = new Random();
    private final int exclusionZoneRadiusPx; // Radius of the central exclusion area in pixels

    public RandomLayoutManager(Context context, int radiusDp, int paddingDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.radiusPx = (int) (radiusDp * displayMetrics.density);
        this.paddingPx = (int) (paddingDp * displayMetrics.density);
        this.exclusionZoneRadiusPx = (int) (75 * displayMetrics.density); // Convert 75dp to pixels
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) {
            return;
        }

        int width = getWidth() - 2 * paddingPx;
        int height = getHeight() - 2 * paddingPx;

        if (width <= 0 || height <= 0) {
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        List<Rect> itemRects = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);

            measureChildWithMargins(view, 0, 0);
            int itemWidth = getDecoratedMeasuredWidth(view);
            int itemHeight = getDecoratedMeasuredHeight(view);

            if (itemWidth <= 0 || itemHeight <= 0) {
                continue;
            }

            boolean placed = false;
            int retries = 0;
            while (!placed && retries < 100) {
                int itemLeftBound = width - itemWidth + 1;
                int itemTopBound = height - itemHeight + 1;

                if (itemLeftBound > 0 && itemTopBound > 0) {
                    int itemLeft = paddingPx + random.nextInt(itemLeftBound);
                    int itemTop = paddingPx + random.nextInt(itemTopBound);

                    Rect newRect = new Rect(itemLeft, itemTop, itemLeft + itemWidth, itemTop + itemHeight);

                    boolean isInExclusionZone = isInExclusionZone(newRect, centerX + paddingPx, centerY + paddingPx);
                    boolean isOverlapping = isOverlapping(newRect, itemRects);

                    if (!isInExclusionZone && !isOverlapping) {
                        itemRects.add(newRect);
                        layoutDecorated(view, itemLeft, itemTop, itemLeft + itemWidth, itemTop + itemHeight);
                        placed = true;
                    }
                }

                retries++;
            }

            if (!placed) {
                Log.w("RandomLayoutManager", "Unable to place item at position " + i);
            }
        }
    }

    private boolean isInExclusionZone(Rect rect, int centerX, int centerY) {
        int rectCenterX = (rect.left + rect.right) / 2;
        int rectCenterY = (rect.top + rect.bottom) / 2;
        int distance = (int) Math.sqrt(Math.pow(rectCenterX - centerX, 2) + Math.pow(rectCenterY - centerY, 2));
        return distance < exclusionZoneRadiusPx;
    }

    private boolean isOverlapping(Rect newRect, List<Rect> itemRects) {
        for (Rect rect : itemRects) {
            if (Rect.intersects(newRect, rect)) {
                return true;
            }
        }
        return false;
    }
}
