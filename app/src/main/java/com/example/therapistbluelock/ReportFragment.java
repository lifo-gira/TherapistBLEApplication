package com.example.therapistbluelock;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

public class ReportFragment extends Fragment {

    // Fragments for navigation
    private final Fragment assessmentFragment = new AssessmentFragment();
    private final Fragment exerciseFragment = new ExerciseFragment();
    private final Fragment muscleStrengthFragment = new MuscleFragment();
    private final Fragment summaryFragment = new SummaryFragment();
    private final Fragment overviewFragment = new OverviewFragment(); // Add OverviewFragment

    // Views for navigation
    private LinearLayout navAssessment, navExercise, navMuscleStrength, navSummary;
    private TextView textAssessment, textExercise, textMuscleStrength, textSummary;
    private ImageView buttonBack; // Reference to the back button

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        OnBackPressedCallback callback1 = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button logic
                // Example: Navigate back or show a confirmation dialog
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        // Attach the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback1);
        // Initialize navigation LinearLayouts
        navAssessment = view.findViewById(R.id.nav_assessment);
        navExercise = view.findViewById(R.id.nav_exercise);
        navMuscleStrength = view.findViewById(R.id.nav_muscle_strength);
        navSummary = view.findViewById(R.id.nav_summary);

        // Initialize navigation TextViews
        textAssessment = navAssessment.findViewById(R.id.text_assessment);
        textExercise = navExercise.findViewById(R.id.text_exercise);
        textMuscleStrength = navMuscleStrength.findViewById(R.id.text_muscle_strength);
        textSummary = navSummary.findViewById(R.id.text_summary);

        // Initialize the back button
        buttonBack = view.findViewById(R.id.button_back);

        // Set click listeners for navigation
        navAssessment.setOnClickListener(v -> {
            replaceFragment(assessmentFragment);
            updateNavigationState(navAssessment, textAssessment);
        });
        navExercise.setOnClickListener(v -> {
            try {
                if(MainActivity.selectedpatientdata.getInt("flag")>=2) {
                    replaceFragment(exerciseFragment);
                    updateNavigationState(navExercise, textExercise);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        navMuscleStrength.setOnClickListener(v -> {
            replaceFragment(muscleStrengthFragment);
            updateNavigationState(navMuscleStrength, textMuscleStrength);
        });
        navSummary.setOnClickListener(v -> {
            replaceFragment(summaryFragment);
            updateNavigationState(navSummary, textSummary);
        });

        // Set up back button to navigate to OverviewFragment
        buttonBack.setOnClickListener(v -> {
            navigateToOverviewFragment();
        });

        // Load the default fragment (AssessmentFragment)
        if (savedInstanceState == null) { // To avoid reloading on configuration changes
            replaceFragment(assessmentFragment);
            updateNavigationState(navAssessment, textAssessment); // Set default state
        }

        return view;
    }

    // Method to replace the fragment in the fragment_container
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Optionally, add to back stack if needed
        fragmentTransaction.commit();
    }

    // Method to navigate to OverviewFragment
    private void navigateToOverviewFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, overviewFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack if you want to allow back navigation
        fragmentTransaction.commit();
    }

    // Method to update the state of navigation buttons
    private void updateNavigationState(LinearLayout activeNav, TextView activeText) {
        // Reset all navigation buttons
        resetNavigationState();

        // Highlight the active navigation button
        activeNav.setBackgroundResource(R.drawable.nav_card); // Set the background
        activeText.setTextColor(requireContext().getColor(R.color.white)); // Set text color to white
    }

    // Method to reset all navigation buttons
    private void resetNavigationState() {
        // Reset background and text color for all navigation buttons
        navAssessment.setBackground(null);
        textAssessment.setTextColor(requireContext().getColor(R.color.black));

        navExercise.setBackground(null);
        textExercise.setTextColor(requireContext().getColor(R.color.black));

        navMuscleStrength.setBackground(null);
        textMuscleStrength.setTextColor(requireContext().getColor(R.color.black));

        navSummary.setBackground(null);
        textSummary.setTextColor(requireContext().getColor(R.color.black));
    }
}
