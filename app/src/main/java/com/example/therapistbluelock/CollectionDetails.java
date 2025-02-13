package com.example.therapistbluelock;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CollectionDetails extends AppCompatActivity {
    private EditText editText1,name;
    Calendar calendar;
    private LinearLayout maleOption, femaleOption;
    public static String gender;
    private EditText date_of_report;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private LinearLayout selectedLayout;
    private RecyclerView verticalRuler;
    private TextView txtHeight;
    private NumberPicker numberPickerWeight;
    public static float heightinfo,weightinfo,bmiinfo;
    public static String accidentinfo;
    CheckBox myCheckbox;
    Button buttonBmi;
    TextView textBmi,textAboveBmi;
    private Spinner bloodGroupSpinner;
    public static String bloodgroup;
    Button submitButton;
    public static String dob,doreg,patientname;
    public static int age;
    ImageView backnavigation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        hideSystemUI();

        backnavigation = findViewById(R.id.backnavigation);
        backnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionDetails.this,Dashboard.class);
                startActivity(intent);
            }
        });

        editText1 = findViewById(R.id.dob_calendar);
        name = findViewById(R.id.name);

        // Open the DatePickerDialog when the EditText is clicked
        editText1.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CollectionDetails.this,
                    android.R.style.Theme_DeviceDefault_Dialog_Alert,  // Use your custom style if necessary
                    (view, year1, month1, dayOfMonth) -> {
                        // Format the selected date and set it in the EditText
                        String date = String.format("%02d/%02d/%d", month1 + 1, dayOfMonth, year1);
                        dob = date;
                        updateAge(year1, month1, dayOfMonth);
                        editText1.setText(date);  // Set the selected date in the EditText
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });



        maleOption = findViewById(R.id.maleOption);
        femaleOption = findViewById(R.id.femaleOption);

        maleOption.setOnClickListener(v -> selectGenderOption(maleOption, femaleOption));
        femaleOption.setOnClickListener(v -> selectGenderOption(femaleOption, maleOption));

        date_of_report = findViewById(R.id.date_of_report);

        // Handle Date Picker for Date of Birth
        date_of_report.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CollectionDetails.this,
                    android.R.style.Theme_DeviceDefault_Dialog_Alert,  // Use your custom style if necessary
                    (view1, year1, month1, dayOfMonth) -> {
                        // Format the selected date and set it in the EditText
                        String date = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        doreg=date;
                        date_of_report.setText(date);  // Set the selected date in the EditText
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        linearLayout3 = findViewById(R.id.linearLayout3);

        // Added: Initially, all layouts are unselected
        setUnselectedBackground();

        // Added: Set click listeners for each layout
        linearLayout1.setOnClickListener(v -> selectLayout(linearLayout1));
        linearLayout2.setOnClickListener(v -> selectLayout(linearLayout2));
        linearLayout3.setOnClickListener(v -> selectLayout(linearLayout3));

        verticalRuler = findViewById(R.id.verticalRuler);
        txtHeight = findViewById(R.id.txtHeight);
        numberPickerWeight = findViewById(R.id.numberPickerWeight);

        myCheckbox = findViewById(R.id.myCheckbox);
        textBmi = findViewById(R.id.textBmi);
        textAboveBmi = findViewById(R.id.textAboveBmi);
        accidentinfo="No";

        myCheckbox.setOnCheckedChangeListener((buttonView, isChecked)->{
            if(isChecked){
                accidentinfo ="Yes";
            }else {
                accidentinfo ="No";
            }
        });
        // Initialize the data for the ruler (heights in centimeters)
        List<Integer> heightList = new ArrayList<>();
        for (int i = 250; i >= 0; i--) {
            heightList.add(i); // Heights range from 0 to 250 cm
        }

        // Set up the RecyclerView with the RulerAdapter
        RulerAdapter adapter = new RulerAdapter(this, heightList);
        verticalRuler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        verticalRuler.setAdapter(adapter);

        // Set the initial position of the RecyclerView to the middle
        verticalRuler.scrollToPosition(100); // Start from 150 cm (50 + 100)

        // Add scroll listener to update the selected height
        verticalRuler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisiblePosition >= 0) {
                        float currentHeight = heightList.get(firstVisiblePosition);
                        heightinfo = currentHeight;
                        Log.e("Height", String.valueOf(heightinfo));
                        txtHeight.setText(currentHeight + " cm");
                    }
                }
            }
        });

        // Set up the NumberPicker for weight
        numberPickerWeight.setMinValue(0); // Minimum weight
        numberPickerWeight.setMaxValue(200); // Maximum weight
        numberPickerWeight.setValue(70); // Default weight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            numberPickerWeight.setTextColor(Color.BLACK);
            numberPickerWeight.setTextSize(40);
        }
        setNumberPickerTextColor(numberPickerWeight, Color.BLACK);

        buttonBmi = findViewById(R.id.buttonBmi);
        buttonBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int w=numberPickerWeight.getValue();
                BMI(heightinfo,w);
            }
        });

        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        ArrayAdapter<CharSequence> blood_adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_group_items,
                R.layout.spinner_item
        );
        bloodGroupSpinner.setAdapter(blood_adapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloodgroup= adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(view -> {
            patientname = name.getText().toString();
            Intent intent = new Intent(CollectionDetails.this,PainSelection.class);
            startActivity(intent);
        });
    }

    private void BMI(float h, float w) {
        MainActivity.patientheight = h;
        Log.e("Height", String.valueOf(h));
        Log.e("Weight", String.valueOf(w));

        heightinfo = h;
        weightinfo = w;

        h = h / 100.0f; // Convert height from cm to meters
        if (h == 0) {
            textBmi.setText("Invalid height");
            return;
        }
        bmiinfo = w / (h * h); // BMI calculation: weight (kg) / height^2 (m^2)

        // Ensure valid BMI values and format it to display up to 2 decimal places
        if (bmiinfo > 0) {
            String bmiText = String.format(Locale.getDefault(), "BMI: %.2f", bmiinfo);
            if(bmiinfo<18.5){
                textAboveBmi.setText("Underweight");
            }
            else if(bmiinfo>=18.5 && bmiinfo<25){
                textAboveBmi.setText("Healthyweight");
            }
            else if(bmiinfo>=25 && bmiinfo<30){
                textAboveBmi.setText("Overweight");
            }
            else if(bmiinfo>=30){
                textAboveBmi.setText("Obesity");
            }
            textBmi.setText(bmiText);
        } else {
            textBmi.setText("Invalid BMI");
        }
    }

    private void updateAge(int year, int month, int dayOfMonth) {
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH);
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;
        if (currentMonth < month || (currentMonth == month && currentDay < dayOfMonth)) {
            age=age-1;
        }


        //dobTextView.setText(String.valueOf(age));
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker, int textColor) {
        for (int i = 0; i < numberPicker.getChildCount(); i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setTextColor(textColor);
            }
        }

        // Handle value change
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            for (int i = 0; i < numberPicker.getChildCount(); i++) {
                View child = numberPicker.getChildAt(i);
                if (child instanceof EditText) {
                    EditText editText = (EditText) child;
                    editText.setTextColor(textColor);
                }
            }
        });
    }

    private void setUnselectedBackground() {
        linearLayout1.setBackgroundResource(R.drawable.unselected_option_background);
        linearLayout2.setBackgroundResource(R.drawable.unselected_option_background);
        linearLayout3.setBackgroundResource(R.drawable.unselected_option_background);
    }

    private void selectLayout(LinearLayout selected) {
        // Reset the backgrounds
        setUnselectedBackground();

        // Set the selected background
        selected.setBackgroundResource(R.drawable.selected_option_background);

        // Store the currently selected layout
        selectedLayout = selected;
    }

    private void selectGenderOption(LinearLayout selected, LinearLayout unselected) {

        if(selected.equals(maleOption)){
            gender = "Male";
        }else{
            gender = "Female";
        }
        selected.setSelected(true);
        unselected.setSelected(false);
        selected.setBackgroundResource(R.drawable.selected_option_background);
        unselected.setBackgroundResource(R.drawable.unselected_option_background);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }
}
