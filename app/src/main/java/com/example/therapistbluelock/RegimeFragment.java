package com.example.therapistbluelock;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.dmoral.toasty.Toasty;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class RegimeFragment extends ContentFragment implements ScreenShotable {

    private Spinner categorySpinner, exerciseSpinner, oldExercisesSpinner;
    private RecyclerView exerciseRecyclerView, assignedExerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter, assignedExerciseAdapter;
    private List<Exercise> exerciseList, assignedExerciseList;
    LinearLayout endurance, strength, balance, flexibility, stretching;
    // Declare the ImageView where items will be dropped
    private ImageView dropTargetImageView;

    List<String> exerciseOptions = new ArrayList<>();
    List<String> oldexercise = new ArrayList<>();
    Set<String> oldexerciseset = new LinkedHashSet<>();
    String selectedpatient, patientid, sselectedexercise;

    ArrayAdapter<String> adapter1, adapter2;

    TextView profilename;

    public static RegimeFragment newInstance() {
        return new RegimeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regime, container, false);

        fetchpatient();
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
        // Initialize the exercise spinner
        exerciseSpinner = view.findViewById(R.id.patient_exercise_spinner);
        oldExercisesSpinner = view.findViewById(R.id.previous_exercises_assigned);
        //String[] exerciseOptions = getResources().getStringArray(R.array.patient_options);

        profilename = view.findViewById(R.id.profilename);
        profilename.setText(MainActivity.therapistname);

        adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, exerciseOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter1);

        // Create adapter for the second spinner
        adapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, oldexercise);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        oldExercisesSpinner.setAdapter(adapter2);

        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                selectedpatient = parent.getItemAtPosition(position).toString();
                if (!selectedpatient.equalsIgnoreCase("Select Patient")) {
                    fetchpatdetails(selectedpatient);
                }

                //Toast.makeText(getContext(), "Selected: " + selectedpatient, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        oldExercisesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                sselectedexercise = parent.getItemAtPosition(position).toString();
                if (!sselectedexercise.equalsIgnoreCase("Selected Exercise")) {
                    List<Exercise> filteredExercises = new ArrayList<>();
                    for (Exercise exercise : exerciseList) {
                        if (exercise.getName().equalsIgnoreCase(sselectedexercise)) {
                            filteredExercises.add(exercise);
                        }
                    }
                    // Update RecyclerView with filtered exercises
                    exerciseAdapter.updateExerciseList(filteredExercises);
                }

                Toast.makeText(getContext(), "Selected: " + selectedpatient, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        categorySpinner = view.findViewById(R.id.category_spinner);

        // Get the string-array from resources
        String[] categoryOptions = getResources().getStringArray(R.array.category_options);

        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, categoryOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };

        // Set the drop-down view resource for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        // Set an OnItemSelectedListener for the Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (selectedCategory.equalsIgnoreCase("All Exercises")) {
                    exerciseList = getExerciseData();
                    exerciseAdapter.updateExerciseList(exerciseList);
                } else {
                    filterExercisesByCategory(selectedCategory); // Filter exercises when category is selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case where no item is selected
            }
        });

        // Initialize RecyclerView for exercises
        exerciseRecyclerView = view.findViewById(R.id.exercise_recycle);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set the exercise list and adapter
        exerciseList = getExerciseData();
        exerciseAdapter = new ExerciseAdapter(this, exerciseList, false,getContext());
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        // Set up ItemTouchHelper for drag-and-drop functionality
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;  // Disable dragging within RecyclerView
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false; // Disable swipe actions
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // No-op, as drag-and-drop within RecyclerView is disabled
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // You can handle swipe actions if needed
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return 0; // Disable any movement flags for RecyclerView
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(exerciseRecyclerView);  // Attach ItemTouchHelper to RecyclerView

        // Initialize the drop target ImageView
        dropTargetImageView = view.findViewById(R.id.drag_and_drop);

        // Initialize the RecyclerView for assigned exercises (dropped items)
        assignedExerciseRecyclerView = view.findViewById(R.id.assigned_exercise_recycle);
        assignedExerciseRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2x2 grid layout
        assignedExerciseList = new ArrayList<>();
        assignedExerciseAdapter = new ExerciseAdapter(this, assignedExerciseList, true,getContext());
        assignedExerciseRecyclerView.setAdapter(assignedExerciseAdapter);

        // Set OnDragListener for the drop target
        dropTargetImageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.GRAY); // Change background color when item enters
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(Color.TRANSPARENT); // Reset background color
                        break;
                    case DragEvent.ACTION_DROP:
                        // Handle the drop event only for items outside the RecyclerView
                        View draggedView = (View) event.getLocalState();

                        // Check if the dragged view is a CardView (or any other view you're dragging)
                        if (draggedView instanceof CardView) {
                            // Handle the drop here
                            CardView droppedCard = (CardView) draggedView;

                            // Get the exercise name from the dragged CardView
                            TextView exerciseNameTextView = droppedCard.findViewById(R.id.exercise_name);
                            String itemData = exerciseNameTextView.getText().toString();

                            // Look for the exercise in the exerciseList based on its name
                            Exercise matchedExercise = null;
                            if (selectedpatient != null && !selectedpatient.equalsIgnoreCase("Select Patient")) {
                                for (Exercise exercise : exerciseList) {
                                    if (exercise.getName().equals(itemData)) {
                                        matchedExercise = exercise;
                                        break;
                                    }
                                }
                                // If the exercise is found in the list
                                if (matchedExercise != null) {
                                    // Check if the exercise is already assigned
                                    boolean isExerciseAlreadyAssigned = false;
                                    for (Exercise exercise : assignedExerciseList) {
                                        if (exercise.getName().equals(itemData)) {
                                            isExerciseAlreadyAssigned = true;
                                            break;
                                        }
                                    }

                                    if (!isExerciseAlreadyAssigned) {
                                        // Use the found exercise details to create a new assigned exercise
                                        Exercise droppedExercise = new Exercise(
                                                matchedExercise.getName(),       // Exercise name
                                                matchedExercise.getVideo(),      // Exercise video URL
                                                matchedExercise.getRepCount(),   // Exercise duration/repetitions
                                                matchedExercise.getSetCount(),   // Exercise set count
                                                matchedExercise.getImageResId(), // Exercise image resource
                                                matchedExercise.getCategory()   // Exercise category
                                        );

                                        // Add the exercise to the assigned list
                                        assignedExerciseList.add(droppedExercise);
                                        assignedExerciseAdapter.notifyDataSetChanged(); // Update the assigned exercise RecyclerView

                                        // Call changeCategoryBackground after adding the exercise
                                        changeCategoryBackground(matchedExercise.getCategory());
                                    } else {
                                        // Show message if the exercise is already assigned
                                        Toast.makeText(requireContext(), "This exercise is already assigned.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Exercise not found in the list.", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                // Show message if the exercise is not found in the list
                                Toasty.warning(getContext(), "Select the Patient to Assign Exercise", Toasty.LENGTH_LONG).show();
                            }
                        } else {
                            // Handle case where the dragged view is not a CardView
                            Toast.makeText(requireContext(), "Dropped non-CardView item", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(Color.TRANSPARENT); // Reset background when drag ends
                        break;
                }
                return true;
            }
        });

        endurance = view.findViewById(R.id.endurance);
        strength = view.findViewById(R.id.strength);
        balance = view.findViewById(R.id.balance);
        flexibility = view.findViewById(R.id.flexibility);
        stretching = view.findViewById(R.id.stretching);
        ImageView viewAll = view.findViewById(R.id.view_all);
        // Set click listeners for each ImageView
        endurance.setOnClickListener(v -> showCustomDialog("Endurance", getExercisesForCategory("Endurance")));
        strength.setOnClickListener(v -> showCustomDialog("Strength", getExercisesForCategory("Strength")));
        balance.setOnClickListener(v -> showCustomDialog("Balance", getExercisesForCategory("Balance")));
        flexibility.setOnClickListener(v -> showCustomDialog("Flexibility", getExercisesForCategory("Flexibility")));
        stretching.setOnClickListener(v -> showCustomDialog("Stretching", getExercisesForCategory("Stretching")));
        viewAll.setOnClickListener(v -> showCustomDialog("View All", getAllExercises()));


        return view;
    }

    private void fetchpatdetails(String selectedpatient) {

        //            MainActivity.patientdata = new JSONArray();
//            for (int i = 0; i < MainActivity.completedata.length(); i++) {
//                JSONObject jsonObject = MainActivity.completedata.getJSONObject(i);
//                String therapistid = jsonObject.getString("therapist_id");
//                String therapistassigned = jsonObject.getString("therapist_assigned");
//                if (therapistid.equalsIgnoreCase(MainActivity.therapistuserid) && therapistassigned.equalsIgnoreCase(MainActivity.therapistusername)) {
//                    MainActivity.patientdata.put(jsonObject);
//                }
//            }
        oldexercise.clear();
        oldexerciseset.clear();
        oldexerciseset.add("Selected Exercise");
        for (int i = 0; i < MainActivity.patientdata.length(); i++) {
            try {
                JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                JSONArray exercise = new JSONArray();
                JSONObject indiviphase = new JSONObject();
                JSONObject indiviexercise = new JSONObject();


                if (selectedpatient.equals(jsonObject.getString("user_id"))) {

                    Iterator<String> keys = jsonObject.keys();

                    while(keys.hasNext()){
                        if("Model_Recovery".equalsIgnoreCase(keys.next())){
                            exercise = jsonObject.getJSONArray("Model_Recovery");
                            patientid = jsonObject.getString("patient_id");

                            if (exercise.length() > 0) {
                                for (int j = 0; j < exercise.length(); j++) {
                                    indiviphase = exercise.getJSONObject(j);
                                    indiviexercise = indiviphase.getJSONObject("Exercise");
                                    Iterator<String> keys1 = indiviexercise.keys();

                                    while (keys1.hasNext()) {
                                        oldexerciseset.add(keys1.next());
                                    }
                                    //Log.e("Exercise OLd", String.valueOf(oldexerciseset));
                                }
                                for (String item : oldexerciseset) {
                                    oldexercise.add(item);
                                }
                                //oldexercise = Arrays.asList(oldexerciseset.toArray(new String[0]));
                                adapter2.notifyDataSetChanged();
                                Log.e("Exercise OLd", String.valueOf(oldexercise));
                            }
//                            else{
//                                Toasty.info(getContext(),"No Previous Assigned Exercise",Toast.LENGTH_SHORT).show();
//                            }
                        }

                    }
                    if(exercise.length() == 0){
                        patientid = jsonObject.getString("patient_id");
                        Toasty.info(getContext(),"No Previous Assigned Exercise",Toast.LENGTH_SHORT).show();
                    }


                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        exerciseList = getExerciseData();
        exerciseAdapter.updateExerciseList(exerciseList);

    }

    private void fetchpatient() {
        exerciseOptions.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api-wo6.onrender.com/patient-details/all",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MainActivity.patientdata = new JSONArray();
                            MainActivity.completedata = new JSONArray(response);
                            for (int i = 0; i < MainActivity.completedata.length(); i++) {
                                JSONObject jsonObject = MainActivity.completedata.getJSONObject(i);
                                String therapistid = jsonObject.getString("therapist_id");
                                String therapistassigned = jsonObject.getString("therapist_assigned");
                                if (therapistid.equalsIgnoreCase(MainActivity.therapistuserid) && therapistassigned.equalsIgnoreCase(MainActivity.therapistusername)) {
                                    MainActivity.patientdata.put(jsonObject);
                                }
                            }
                            exerciseOptions.add("Select Patient");
                            for (int i = 0; i < MainActivity.patientdata.length(); i++) {
                                try {
                                    JSONObject jsonObject = MainActivity.patientdata.getJSONObject(i);
                                    Log.e("Raj", String.valueOf(jsonObject));
                                    if (jsonObject.getInt("flag") >= 1) {
                                        Log.e("Raj 1", String.valueOf(jsonObject));
                                        exerciseOptions.add(jsonObject.getString("user_id"));
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                adapter1.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void showCustomDialog(String title, List<Exercise> exercises) {
        // Create the dialog
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exercise_dialog_layout);

        // Check if the exercises list is empty
        if (exercises.isEmpty()) {
            // Show a toast if there are no exercises
            Toast.makeText(getContext(), "No exercises added yet!", Toast.LENGTH_SHORT).show();

            // Remove the background (set to transparent or null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Optionally, you can also dismiss the dialog or set it to an empty state
            dialog.dismiss();
            return; // Exit the method if no exercises are available
        }

        // Set the title
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        // Set up RecyclerView
        RecyclerView recyclerView = dialog.findViewById(R.id.dialog_exercise_recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Grid layout with 2 columns
        ExerciseAdapter dialogAdapter = new ExerciseAdapter(this, exercises, true, getContext()); // Pass the exercises
        recyclerView.setAdapter(dialogAdapter);

        // Find the LinearLayout to_complete_regime
        LinearLayout toCompleteRegime = dialog.findViewById(R.id.to_complete_regime);
        LinearLayout titleLayout = dialog.findViewById(R.id.title);  // Get reference to the title LinearLayout
        ImageView saveImage = dialog.findViewById(R.id.image1);  // Get reference to the save button image

        // Show 'to_complete_regime' only if the title is "View All"
        if ("View All".equalsIgnoreCase(title)) {
            toCompleteRegime.setVisibility(View.VISIBLE);
        } else {
            toCompleteRegime.setVisibility(View.GONE);
        }

        // Only show the LinearLayout when "View All" is clicked
        if ("View All".equalsIgnoreCase(title)) {
            titleLayout.setVisibility(View.VISIBLE);  // Make the title layout visible
        }

        // Set up Save Button functionality
        saveImage.setOnClickListener(v -> {
            // Find the EditText
            EditText nameEditText = dialog.findViewById(R.id.name);

            // Check if EditText is enabled (for editing)
            if (nameEditText.isEnabled()) {
                // Disable the EditText so the user cannot change it
                nameEditText.setEnabled(false);  // Disable EditText

                // Change the save button image to "Edit" icon (after saving)
                saveImage.setImageResource(R.drawable.baseline_edit_24);  // Change image to Edit icon
            } else {
                // Enable the EditText again for editing
                nameEditText.setEnabled(true);  // Enable EditText

                // Change the save button image to "Save" icon (when editing)
                saveImage.setImageResource(R.drawable.baseline_save_24);  // Change image to Save icon
            }

            // Optionally, save the value entered in EditText (before disabling)
            if (!nameEditText.isEnabled()) {
                String enteredTitle = nameEditText.getText().toString();
                // Save the enteredTitle wherever needed (e.g., in a database or backend)
            }
        });

        // Handle to_complete_regime click event
        toCompleteRegime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getContext());
                JSONObject postParams = new JSONObject();

                try {
                    for (int i = 0; i < exercises.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sets", exercises.get(i).getSetCount());
                        jsonObject.put("reps", exercises.get(i).getRepCount());
                        postParams.put(exercises.get(i).getName(), jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Login Object", String.valueOf(postParams));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "https://api-wo6.onrender.com/patients/" + patientid + "/" + 2 + "/add-exercise-assigned", postParams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toasty.info(getContext(), "Exercises Assigned Successfully", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toasty.error(getContext(), "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                        Log.e("Login Error", String.valueOf(volleyError));
                    }
                }) {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        int statusCode = response.statusCode;
                        Log.d("Response Code", "Status Code: " + statusCode);
                        return super.parseNetworkResponse(response);
                    }
                };

                queue.add(jsonObjectRequest);
            }
        });

        // Close button functionality
        ImageView closeButton = dialog.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

//    private void postexercise(List<Exercise> exercises) {
//
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        JSONObject postParams = new JSONObject();
//        JSONObject exerobj = new JSONObject();
//
//        try {
//            postParams.put("Title", "Recovery Exercise");
//            for (int i = 0; i < exercises.size(); i++) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("values", new JSONArray());
//                jsonObject.put("pain", new JSONArray());
//                jsonObject.put("rom", 0);
//                jsonObject.put("set", exercises.get(i).getSetCount());
//                jsonObject.put("rep", exercises.get(i).getRepCount());
//                jsonObject.put("velocity", 0);
//                jsonObject.put("progress", "");
//                exerobj.put(exercises.get(i).getName(), jsonObject);
//            }
//            postParams.put("Exercise", exerobj);
//            postParams.put("pain_scale", 0);
//
//            Log.e("Post Data", String.valueOf(postParams));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("Login Object", String.valueOf(postParams));
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "https://api-wo6.onrender.com/update-recovery-info/" + patientid + "/" + 2, postParams,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//                Toasty.error(getContext(), "Error: " + volleyError, Toast.LENGTH_SHORT).show();
//                Log.e("Login Error", String.valueOf(volleyError));
//            }
//
//        }) {
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                // Fetch the response code
//                int statusCode = response.statusCode;
//                Log.d("Response Code", "Status Code: " + statusCode);
//
//                // Optionally handle the status code here if needed
//
//                // Call the superclass method to ensure normal behavior
//                return super.parseNetworkResponse(response);
//            }
//        };
//
//
//        queue.add(jsonObjectRequest);
//
//    }

    public void changeCategoryBackground(String category) {
        // Map each category to its respective layout
        Map<String, View> categoryViews = new HashMap<>();
        categoryViews.put("Endurance", endurance);
        categoryViews.put("Strength", strength);
        categoryViews.put("Balance", balance);
        categoryViews.put("Flexibility", flexibility);
        categoryViews.put("Stretching", stretching);

        // Check if there are any exercises in the provided category
        boolean hasExercisesInCategory = !getExercisesForCategory(category).isEmpty();

        // If exercises are found, update the background for the specific category
        View categoryView = categoryViews.get(category);
        if (categoryView != null) {
            if (hasExercisesInCategory) {
                categoryView.setBackgroundResource(R.drawable.image_fill_box); // Set the background to the desired drawable
            } else {
                categoryView.setBackgroundResource(0); // Remove the background if no exercises exist in the category
            }
        }
    }

    private List<Exercise> getExercisesForCategory(String category) {
        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : assignedExerciseList) {
            if (exercise.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(exercise);
            }
        }
        return filteredList;
    }

    private List<Exercise> getAllExercises() {
        return new ArrayList<>(assignedExerciseList); // Return the full list
    }

    // In RegimeFragment
    public void removeExerciseFromAssignedList(Exercise exerciseToRemove) {
        if (assignedExerciseList != null && assignedExerciseList.contains(exerciseToRemove)) {
            assignedExerciseList.remove(exerciseToRemove);  // Remove the exercise from the list
            assignedExerciseAdapter.notifyDataSetChanged();  // Notify the adapter that the data has changed

            // Update the background for the category after removal
            changeCategoryBackground(exerciseToRemove.getCategory());

            // Show a toast message (optional)
            Toast.makeText(requireContext(), "Exercise removed from the list.", Toast.LENGTH_SHORT).show();
        } else {
            // Exercise not found in the list
            Toast.makeText(requireContext(), "Exercise not found in the list.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to filter exercises based on category
    private void filterExercisesByCategory(String selectedCategory) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : exerciseList) {
            if (exercise.getCategory().equalsIgnoreCase(selectedCategory)) {
                filteredExercises.add(exercise);
            }
        }
        // Update RecyclerView with filtered exercises
        exerciseAdapter.updateExerciseList(filteredExercises);
    }

    private List<Exercise> getExerciseData() {
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Left-Leg-Bend", "video1.mp4", 0, 0, R.drawable.right_leg_bend, "Endurance"));
        exerciseList.add(new Exercise("Right-Leg-Bend", "video2.mp4", 0, 0, R.drawable.right_leg_bend, "Endurance"));
        exerciseList.add(new Exercise("Left-Knee-Bend", "video3.mp4", 0, 0, R.drawable.right_leg_bend, "Strength"));
        exerciseList.add(new Exercise("Right-Knee-Bend", "video3.mp4", 0, 0, R.drawable.right_leg_bend, "Strength"));
        exerciseList.add(new Exercise("Sit-Stand", "video3.mp4", 0, 0, R.drawable.right_leg_bend, "Strength"));
        // Add more exercises here as needed
        return exerciseList;
    }

}


