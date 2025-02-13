package com.example.therapistbluelock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class CustomMenuItemView extends RelativeLayout {

    public CustomMenuItemView(Context context) {
        super(context);
        init();
    }

    public CustomMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.menu_item_drawer, this, true);
    }
}
