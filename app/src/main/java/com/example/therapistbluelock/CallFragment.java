package com.example.therapistbluelock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class CallFragment extends Fragment {

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enabling the action bar's "up" button (back button)
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button logic
                // Example: Navigate back or show a confirmation dialog
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        // Attach the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        setHasOptionsMenu(true); // Make sure the fragment can have options menu

        // Check if a Firebase user is logged in
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Firebase user is logged in, proceed to check CometChat login
            User loggedInUser = CometChat.getLoggedInUser();
            if (loggedInUser != null) {
                Log.d("CometChat", "Logged in as: " + loggedInUser.getUid());
                // Continue with showing the chat UI
            } else {
                // CometChat user not logged in, perform CometChat login
                loginToCometChat(firebaseUser.getUid());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);  // Inflate your fragment_call.xml layout
    }

    private void loginToCometChat(String uid) {
        CometChat.login(uid, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                // CometChat login successful, continue with chat UI
                Log.d("CometChat", "CometChat login successful: " + user.getUid());
            }

            @Override
            public void onError(CometChatException e) {
                // Handle error during CometChat login
                Log.e("CometChat", "CometChat login failed: " + e.getMessage());
                Toast.makeText(getActivity(), "CometChat login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
