package com.example.therapistbluelock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class ContentFragment extends Fragment implements ScreenShotable {
    private View containerView;
    protected Bitmap bitmap;
    public static final String HOME = "Home";
    public static final String REGIME = "Regime";
    public static final String CALL = "Call";
    public static final String SETTINGS = "Settings";
    public static final String LOGOUT = "LogOut";

    public static ContentFragment newInstance(int resId) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resId", resId);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize containerView safely after the view is created
        this.containerView = view.findViewById(R.id.container);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button logic
                // Example: Navigate back or show a confirmation dialog
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        // Attach the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        // Log the view for debugging purposes
        if (containerView == null) {
            Log.e("ContentFragment", "Failed to find containerView.");
        } else {
            Log.d("ContentFragment", "Container view initialized: " + containerView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void takeScreenShot() {
        // Ensure that containerView is not null before starting the screenshot
        if (containerView != null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                            containerView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    containerView.draw(canvas);
                    ContentFragment.this.bitmap = bitmap;
                }
            };
            thread.start();
        } else {
            Log.e("ContentFragment", "Container view is null, cannot take screenshot.");
        }
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
