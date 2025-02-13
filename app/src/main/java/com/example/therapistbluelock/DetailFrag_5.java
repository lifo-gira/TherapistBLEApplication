package com.example.therapistbluelock;

import static com.example.therapistbluelock.DetailFrag_5.postdata;
import static com.example.therapistbluelock.DetailFrag_5.postdataobj;
import static com.example.therapistbluelock.DetailFrag_5.postexedata;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import me.ibrahimsn.lib.Speedometer;

public class DetailFrag_5 extends AppCompatActivity implements DetailCollectionAdapter.OnItemClickListener {

    private static final int REQUEST_UPDATE_ITEM = 1; // Request code for item updates

    public static List<Metric> metricArray = new ArrayList<>();
    public static List<Metric> metricArray1 = new ArrayList<>();
    Button to_complete;
    public static String selectedExercise;
    JSONObject finalexedata = new JSONObject();
    JSONArray finaldataarray = new JSONArray();
    private List<BluetoothSocket> bluetoothSockets;
    private boolean isDeviceConnected;
    public static JSONArray analysereportarray = new JSONArray();
    public static List<Float> angles = new ArrayList<>();
    public static List<Float> angles1 = new ArrayList<>();
    public static List<List<Entry>> highlightArraypass = new ArrayList<>();
    public static List<List<Entry>> highlightArrayact = new ArrayList<>();
    public static List<List<Entry>> highlightArraypassleft = new ArrayList<>();
    public static List<List<Entry>> highlightArrayactleft = new ArrayList<>();
    public static List<List<Entry>> highlightArraypassright = new ArrayList<>();
    public static List<List<Entry>> highlightArrayactright = new ArrayList<>();
    public static int playflag = 0;
    public static List<Entry> entries;
    public static List<Entry> entries1;
    public static LineChart lineChart;
    public static float change, change1;
    public static List<Float> indiviminAngle = new ArrayList<>();
    public static List<Float> indivimaxAngle = new ArrayList<>();
    ;
    public static List<Float> indiviflexionVelocities = new ArrayList<>();
    public static List<Float> indiviextensionVelocities = new ArrayList<>();
    public static List<Integer> indivipain = new ArrayList<>();
    public static List<Long> times = new ArrayList<>();
    public static List<Long> times1 = new ArrayList<>();
    public static List<Entry> tempRow = new ArrayList<>();
    public static List<Entry> tempRow1 = new ArrayList<>();
    public static List<Entry> objectElements = new ArrayList<>();
    public static List<Entry> objectElements1 = new ArrayList<>();
    public static List<Entry> objectElements2 = new ArrayList<>();
    public static List<Float> stackedMetricsArray = new ArrayList<>();
    public static int prevSignChange = 0, prevSignChange1 = 0;
    public static int pain = 0;
    public static int cycleCount = 1, cycleCount1 = 1, cycleCount2 = 1;
    public static long elapsedTime = -1;
    public static int flexionCycles = 0, flexionCycles1 = 0;
    public static int extensionCycles = 0, extensionCycles1 = 0;
    public static int totalCycles = 0, totalCycles1 = 0;
    public static JSONObject exedata = new JSONObject();
    public static JSONObject subexedata = new JSONObject();
    public static JSONArray exevalue = new JSONArray();
    public static JSONArray exepain = new JSONArray();
    public static JSONArray reportarray = new JSONArray();
    public static JSONObject reportobject = new JSONObject();
    public static JSONObject mainreportobject = new JSONObject();
    public static JSONArray datareportarray = new JSONArray();
    public static List<ExerciseCycleAssessment> exerciseListact, exerciseListpass, exerciseListtotal;
    public static List<ExtensionlagCycleAssessment> extensionlagCycleAssessments;
    public static List<Dynamicbalancetestdata> dynamicbalancetestdata;
    public static List<Staticbalancetestdata> staticbalancetestdata;
    public static List<Staircaseclimbingtestdata> staircaseclimbingtestdata;
    public static LineData lineData = new LineData();
    public static LineData lineData1 = new LineData();
    public static LineDataSet dataSet;
    public static LineDataSet dataSet1;
    public static int currentMetricIndex = 0;
    public static String exename = "";
    public static ArrayList<IndiviCardData> indiviCardData = new ArrayList<>();
    public static RecyclerView indiviact, indivipass;
    public static int counter = -1;
    public static AssessmentCycleAdapter indiviCardAdapterpass;
    public static ExtensionlagAdapter extensionlagAdapter;
    public static MobilityCycleAdapter mobilityCycleAdapter;
    public static ActiveAssessmentCycleAdapter indiviCardAdapteract;
    public static TotalAssessmentCycleAdapter indiviCardAdaptertotal;
    public static Dynamicbalanceadapter dynamicbalanceadapter;
    public static Staticbalancetestadapter staticbalancetestadapter;
    public static Staircaseclimbingadapter staircaseclimbingadapter;
    public static ProprioceptionAdapter proprioceptionAdapter;
    public static ArrayList<ExerciseCycleAssessment> exerciseCycleAssessment = new ArrayList<>();
    public static ArrayList<MobilityCycleAssessment> mobilityCycleAssessments = new ArrayList<>();
    public static Walkgaittestadapter walkgaittestadapter;
    public static ArrayList<Walkgaittestdata> walkgaittestdata = new ArrayList<>();

    public static List<Float> activeeds = new ArrayList<>();
    public static List<Float> passiveeds = new ArrayList<>();
    public static List<Float> totaleds = new ArrayList<>();

    public static long sitToStandStartTime, sitToStandEndTime;
    public static long standToShiftStartTime, standToShiftEndTime;
    public static long walkStartTime, walkEndTime;
    public static long sittostand, standtosit;
    public static final int SIT_ANGLE = 80; // seated knee angle threshold
    public static final int STAND_ANGLE = 15; // standing knee angle threshold
    public static final int WALK_THRESHOLD = 40; // knee angle difference for walking
    public static List<Float> leftlegws = new ArrayList<>();
    public static List<Float> leftlegwos = new ArrayList<>();
    public static List<Float> leftlegcyclewalkgait = new ArrayList<>();
    public static List<Float> rightlegcyclewalkgait = new ArrayList<>();
    public static List<Float> rightws = new ArrayList<>();
    public static List<Float> rightwos = new ArrayList<>();

    public static int leftcyclewalkgati = 0, rightcyclewalkgati = 0;

    public static long startTime = 0, endTime = 0, duration = 0, chartstarttime = 0;
    public static boolean isTesting = true;
    public static final double TARGET_ANGLE = 0.0; // Target knee angle for balance (in degrees)
    public static final double ANGLE_THRESHOLD = 5.0; // Acceptable deviation (in degrees)
    public static List<Float> staticbalanceangles = new ArrayList<>();

    public static long ascentStartTime, ascentEndTime;
    public static long descentStartTime, descentEndTime;
    public static long turnStartTime, turnEndTime;
    public static int stepCount = 0;
    public static final int STEP_ANGLE_THRESHOLD = 40; // Knee angle change to detect step cycle
    public static final int ASCENT_ANGLE = 40; // Angle indicating upward movement
    public static final int DESCENT_ANGLE = 20; // Angle indicating downward movement
    public static final int TURN_ANGLE_THRESHOLD = 10; // Stability threshold for turn
    public static int previousLeftKneeAngle = 0;
    public static int previousRightKneeAngle = 0;
    public static boolean isAscent = false;
    public static boolean isTurn = false;
    public static boolean isDescent = false;
    public static List<Float> leftleg = new ArrayList<>();
    public static List<Float> rightleg = new ArrayList<>();
    public static List<Integer> staircasetime = new ArrayList<>();
    public static List<Float> substaircase = new ArrayList<>();
    public static List<Integer> staircaseindex = new ArrayList<>();
    public static int startind = 0, endind = 0, ascentstart = 0, ascentend = 0, descentstart = 0, descentend = 0, turnstart = 0, turnend = 0, turnflag = 0, descentflag = 0;

    public static List<Float> proprom = new ArrayList<>();
    public static List<Float> leftrom = new ArrayList<>();
    public static List<Float> rightrom = new ArrayList<>();

    public static int stepCountwalk = 0;
    public static double totalDistance = 0.0;
    public static long breakTime = 0;
    public static long activeTime = 0;
    public static long lastBreakStart = 0;
    public static final double STRIDE_LENGTH = 0.7; // sample
    public static final int BREAK_THRESHOLD = 2000; // sample seconds
    public static long startTimewalk;
    public static boolean isWalking = false;
    public static List<Float> leftlegwalk = new ArrayList<>();
    public static List<Float> rightlegwalk = new ArrayList<>();
    public static double strideLength = 0.7; // sample
    public static double userHeight = 172;  // sample
    public static long cycleStartTime;
    public static int stepCountgait = 0;
    public static float stancetime = 0;
    public static double MIN_ANGLE_THRESHOLD = 25.0; //  heel strike ku
    public static double MAX_ANGLE_THRESHOLD = 65.0; // thukitu
    public static long lastHeelStrikeTime = 0;
    public static long lastToeOffTime = 0;
    public static List<Double> standTimes = new ArrayList<>();
    public static List<Double> swingTimes = new ArrayList<>();
    public static double avgStandtime = 0;
    public static double avgStancetime = 0;
    public static double avgSwingtime = 0;
    public static double meanVelocity = 0;
    public static double cadence = 0;
    public static final int BREAK_THRESHOLD1 = 5;
    public static final int MIN_CHANGE = 2;
    public static int consecutiveNoChangeCount = 0;
    public static boolean isInBreak = false;
    public static long breakStartTime = 0;
    public static int breakCount = 0;
    public static long totalBreakTime = 0, activestarttime = 0, activeendtime = 0, totalstancepahse = 0;
    public static List<Long> walkgaittime = new ArrayList<>();
    public static List<Integer> walkgaitswingtime = new ArrayList<>();
    public static long stepStartTime = 0, stepStartTime1 = 0;

    public static int sec = 0;


    public static int seconds = 0;
    public static int minutes = 0;
    public static boolean isTimerRunning = false;
    public static long startTimegait = 0;
    public static int milliseconds = 0;
    public static int leftstepcount = 0, rightstepcount = 0, leftgaitindex = 0, rightgaitindex = 0;
    public static List<Float> leftlegvalues = new ArrayList<>();
    public static List<Float> rightlegvalues = new ArrayList<>();
    public static int leftprevSignChange = 0, rightprevSignChange = 0;

    public static int selecteddeviceindex = 0;

    public static float latestValueDevice1 = 0;
    public static float latestValueDevice2 = 0;

    public static String activepassive = "active";

    public static String minangle1, maxangle1, flexion1, extension1, rom1;

    public static int cycle = 0, connectflag = 0;
    ;

    public static boolean isIncreasing = false, isIncreasing1 = false;
    public static boolean isDecreasing = false, isDecreasing1 = false;
    public static boolean cycleReady = false; // New flag to ensure stabilization
    public static int ct = 0;
    public static float localMinimum = Float.MAX_VALUE; // Initialize to a very high value


    public static int cyclecount = 0, staticbaleo = 0, staticbalec = 0, mobilecyclecount = 0, propriocyclecount = 0;

    LinearLayout to_complete_layout;

    public static JSONArray postdata = new JSONArray();
    public static JSONArray postsubdata = new JSONArray();
    public static JSONObject postexedata = new JSONObject();
    public static JSONObject postexesubdata = new JSONObject();
    public static JSONObject postdataobj = new JSONObject();
    public static JSONArray postexevalues = new JSONArray();
    public static JSONArray postexeparameters = new JSONArray();

    public static double leftleglength = 0, rightleglength = 0;

    int sumbitflag = 0;

    TextView therapistname;

    public static Speedometer speedometer1, speedometer2;


    public static List<Float> walkgaitrightlegangles = new ArrayList<>();
    public static List<Float> walkgaitleftlegangles = new ArrayList<>();

    public static List<Double> leftaccl = new ArrayList<>();
    public static List<Double> righttaccl = new ArrayList<>();
    public static JSONArray leftacclfilter = new JSONArray();
    public static JSONArray righttacclfilter = new JSONArray();

    public static List<Long> timestamps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        hideSystemUI();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        try {
            finalexedata.put("exercises", exedata);
            finaldataarray.put(finalexedata);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        walkgaitrightlegangles = new ArrayList<>();
        walkgaitrightlegangles.clear();
        walkgaitleftlegangles = new ArrayList<>();
        walkgaitleftlegangles.clear();

        timestamps = new ArrayList<>();


        leftacclfilter = new JSONArray();
        righttacclfilter = new JSONArray();


        therapistname = findViewById(R.id.therapistname);
        therapistname.setText(MainActivity.therapistname);

        to_complete_layout = findViewById(R.id.to_complete_layout);

        objectElements1 = new ArrayList<>();
        objectElements1.clear();
        objectElements = new ArrayList<>();
        objectElements.clear();
        exerciseListpass = new ArrayList<>();
        exerciseListpass.clear();
        exerciseListact = new ArrayList<>();
        exerciseListact.clear();
        highlightArraypass = new ArrayList<>();
        highlightArraypass.clear();
        highlightArrayact = new ArrayList<>();
        highlightArrayact.clear();
        highlightArraypassleft = new ArrayList<>();
        highlightArrayactleft = new ArrayList<>();
        highlightArraypassright = new ArrayList<>();
        highlightArrayactright = new ArrayList<>();
        exerciseListtotal = new ArrayList<>();
        exerciseListtotal.clear();
        activeeds = new ArrayList<>();
        activeeds.clear();
        passiveeds = new ArrayList<>();
        passiveeds.clear();
        dynamicbalancetestdata = new ArrayList<>();
        dynamicbalancetestdata.clear();
        staticbalancetestdata = new ArrayList<>();
        staticbalancetestdata.clear();
        staircaseclimbingtestdata = new ArrayList<>();
        staircaseclimbingtestdata.clear();
        mobilityCycleAssessments = new ArrayList<>();
        mobilityCycleAssessments.clear();
        leftrom = new ArrayList<>();
        leftrom.clear();
        rightrom = new ArrayList<>();
        rightrom.clear();
        indivimaxAngle = new ArrayList<>();
        indivimaxAngle.clear();

        staircasetime = new ArrayList<>();
        staircasetime.clear();

        exerciseCycleAssessment = new ArrayList<>();
        exerciseCycleAssessment.clear();

        walkgaittestdata = new ArrayList<>();
        walkgaittestdata.clear();
        walkgaittime = new ArrayList<>();
        walkgaittime.clear();
        walkgaitswingtime = new ArrayList<>();
        walkgaitswingtime.clear();

        activepassive = "active";

        selecteddeviceindex = 0;

        mainreportobject = new JSONObject();

        leftlegwos = new ArrayList<>();
        rightwos = new ArrayList<>();
        leftlegws = new ArrayList<>();
        rightws = new ArrayList<>();
        leftlegwos.clear();
        rightwos.clear();
        leftlegws.clear();
        rightws.clear();

        mainreportobject = new JSONObject();
        reportobject = new JSONObject();
        datareportarray = new JSONArray();
        mainreportobject = new JSONObject();

        extensionlagCycleAssessments = new ArrayList<>();
        extensionlagCycleAssessments.clear();

        PdfGenerator.entries = new ArrayList<>();
        PdfGenerator.arraydata = new JSONArray();
        PdfGenerator.analysedata = new JSONArray();
        PdfGenerator.dataobj = new JSONObject();
        PdfGenerator.lineData = new LineData();
        PdfGenerator.entries1 = new ArrayList<>();
        PdfGenerator.entries2 = new ArrayList<>();

        cycle = 0;
        sec = 0;

        stepCountwalk = 0;

        cyclecount = 0;
        mobilecyclecount = 0;
        propriocyclecount = 0;
        staticbaleo = 0;
        staticbalec = 0;

        leftlegcyclewalkgait = new ArrayList<>();
        rightlegcyclewalkgait = new ArrayList<>();

        leftcyclewalkgati = 0;
        rightcyclewalkgati = 0;

        leftaccl = new ArrayList<>();
        righttaccl = new ArrayList<>();


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize the shared data list only if it's empty
        if (SharedData.detailItems.isEmpty()) {
            SharedData.detailItems.add(new DetailItem(R.drawable.knee_pic, "Camera", "Pending", Color.RED, "Take a picture of Knee"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Mobility Test", "Pending", Color.RED, "Mobility Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Extension Lag Test", "Pending", Color.RED, "Extension Lag Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Dynamic Balance Test", "Pending", Color.RED, "Dynamic Balance Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Static Balance Test", "Pending", Color.RED, "Static Balance Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Staircase Climbing Test", "Pending", Color.RED, "Staircase Climbing Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Proprioception Test", "Pending", Color.RED, "Proprioception Test"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Walk and Gait Analysis", "Pending", Color.RED, "Walk and Gait Analysis"));
            SharedData.detailItems.add(new DetailItem(R.drawable.wheelchair_annan, "Muscle Strength", "Pending", Color.RED, "Muscle Strength"));
            // Add other items as needed...
        }

        // Set the adapter to RecyclerView
        DetailCollectionAdapter adapter = new DetailCollectionAdapter(SharedData.detailItems, this);
        recyclerView.setAdapter(adapter);

        int spacingInPixels = (int) (16 * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        SharedPreferences prefs = getSharedPreferences("BluetoothPrefs", MODE_PRIVATE);
        isDeviceConnected = prefs.getBoolean("isConnected", false);

        to_complete_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showsubmitpopup();
            }
        });
    }

    @Override
    public void onItemClick(DetailItem item) {
        Intent intent;

        if ("Completed".equalsIgnoreCase(item.getStatus())) {
            // If the item is marked as completed, do not proceed with launching the activity
            Toast.makeText(DetailFrag_5.this, "This Exercise is already completed", Toast.LENGTH_SHORT).show();
            return;  // Exit the method early
        }

        // Check if the item is "Camera"
        if ("Camera".equals(item.getTitle())) {
            intent = new Intent(this, ImageClassification.class);
            startActivityForResult(intent, REQUEST_UPDATE_ITEM);
        } else if ("Muscle Strength".equalsIgnoreCase(item.getTitle())) {
            intent = new Intent(this, MuscleStrength.class);
            startActivityForResult(intent, REQUEST_UPDATE_ITEM);
        } else {
            selectedExercise = item.getItemType();

            // Check if the item is "Walk and Gait Analysis"
            if ("Walk and Gait Analysis".equalsIgnoreCase(selectedExercise)) {
                // Check if the Camera exercise is completed
                DetailItem cameraItem = null;
                for (DetailItem detailItem : SharedData.detailItems) {
                    if ("Camera".equalsIgnoreCase(detailItem.getTitle())) {
                        cameraItem = detailItem;
                        break;
                    }
                }

                // If Camera is not completed, show a warning and reset Walk and Gait Analysis
                if (cameraItem != null && !"Completed".equalsIgnoreCase(cameraItem.getStatus())) {
                    // Show warning message
                    Toasty.warning(DetailFrag_5.this, "Perform the Knee Image Capture to Continue", Toasty.LENGTH_SHORT).show();

                    // Reset Walk and Gait Analysis item to "Pending" state
                    for (int i = 0; i < SharedData.detailItems.size(); i++) {
                        DetailItem detailItem = SharedData.detailItems.get(i);
                        if ("Walk and Gait Analysis".equalsIgnoreCase(detailItem.getTitle())) {
                            SharedData.detailItems.set(i, new DetailItem(
                                    R.drawable.wheelchair_annan,
                                    "Walk and Gait Analysis",
                                    "Pending",
                                    Color.RED,
                                    "Walk and Gait Analysis"
                            ));
                            break;
                        }
                    }

                    // Notify the adapter about the update
                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    recyclerView.getAdapter().notifyDataSetChanged();

                    return; // Exit the method without proceeding further
                }

                // Proceed to Assessment if conditions are met
                if (leftleglength > 0 && rightleglength >= 0 && MainActivity.patientheight >= 0) {
                    intent = new Intent(this, Assessment.class);
                    intent.putExtra("itemTitle", item.getTitle());
                    intent.putExtra("itemStatus", item.getStatus());
                    intent.putExtra("itemColor", item.getBackgroundTint());
                    startActivityForResult(intent, REQUEST_UPDATE_ITEM);
                } else {
                    Toasty.warning(DetailFrag_5.this, "Perform the Knee Image Capture to Continue", Toasty.LENGTH_SHORT).show();
                }
            } else {
                // For other items, proceed to Assessment
                intent = new Intent(this, Assessment.class);
                intent.putExtra("itemTitle", item.getTitle());
                intent.putExtra("itemStatus", item.getStatus());
                intent.putExtra("itemColor", item.getBackgroundTint());
                startActivityForResult(intent, REQUEST_UPDATE_ITEM);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_ITEM && resultCode == RESULT_OK) {
            // Retrieve the updated item details
            String updatedTitle = data.getStringExtra("itemTitle");
            String updatedStatus = data.getStringExtra("itemStatus");
            int updatedColor = data.getIntExtra("itemColor", Color.RED);

            // Update the corresponding item in the list
            if (updatedTitle != null) {
                for (int i = 0; i < SharedData.detailItems.size(); i++) {
                    DetailItem item = SharedData.detailItems.get(i);
                    if (item.getTitle().equals(updatedTitle)) {
                        item.setStatus(updatedStatus);
                        item.setBackgroundTint(updatedColor);

                        // Notify the adapter about the change
                        RecyclerView recyclerView = findViewById(R.id.recycler_view);
                        recyclerView.getAdapter().notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the RecyclerView when returning to this activity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ((DetailCollectionAdapter) recyclerView.getAdapter()).notifyDataSetChanged();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }

    private void cleanupBluetoothSockets() {
        if (bluetoothSockets != null) {
            for (BluetoothSocket socket : bluetoothSockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Metric {
        int index;
        float val;

        Metric(int index, float val) {
            this.index = index;
            this.val = val;
        }

        public float getVal() {
            return val;
        }
    }


    @SuppressLint("MissingInflatedId")
    private void showsubmitpopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.assessment_end_popup, null);

        ImageView mobleftcheck, mobrightcheck, extnleftcheck, extnrightcheck, dynamicleftcheck, dynamicrightcheck, staticleftcheck, staticrightcheck, stairleftcheck, stairrightcheck, proprioleftcheck, propriorightcheck, walkgaitleftcheck, walkgaitrightcheck;

        AppCompatButton button_no, button_yes;

        mobleftcheck = customView.findViewById(R.id.mobleftcheck);
        mobrightcheck = customView.findViewById(R.id.mobrightcheck);
        extnleftcheck = customView.findViewById(R.id.extnleftcheck);
        extnrightcheck = customView.findViewById(R.id.extnrightcheck);
        dynamicleftcheck = customView.findViewById(R.id.dynamicleftcheck);
        dynamicrightcheck = customView.findViewById(R.id.dynamicrightcheck);
        staticleftcheck = customView.findViewById(R.id.staticleftcheck);
        staticrightcheck = customView.findViewById(R.id.staticrightcheck);
        stairleftcheck = customView.findViewById(R.id.stairleftcheck);
        stairrightcheck = customView.findViewById(R.id.stairrightcheck);
        proprioleftcheck = customView.findViewById(R.id.proprioleftcheck);
        propriorightcheck = customView.findViewById(R.id.propriorightcheck);
        walkgaitleftcheck = customView.findViewById(R.id.walkgaitleftcheck);
        walkgaitrightcheck = customView.findViewById(R.id.walkgaitrightcheck);

        button_no = customView.findViewById(R.id.button_no);
        button_yes = customView.findViewById(R.id.button_yes);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .create();

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean mobilityTestPresent = false;
                boolean proprioceptionTestPresent = false;
                JSONArray jsonArray = new JSONArray();
                try {
                    // Parse the JSON data into a JSONArray
                    postexedata.put("exercises", postdataobj);
                    Iterator<String> keys = postdataobj.keys();
                    postdata.put(postexedata);
                    Log.e("Final Posting Data", String.valueOf(postdata));

                    mobleftcheck.setImageResource(R.drawable.cross);
                    mobrightcheck.setImageResource(R.drawable.cross);
                    dynamicleftcheck.setImageResource(R.drawable.cross);
                    dynamicrightcheck.setImageResource(R.drawable.cross);
                    walkgaitleftcheck.setImageResource(R.drawable.cross);
                    walkgaitrightcheck.setImageResource(R.drawable.cross);
                    stairleftcheck.setImageResource(R.drawable.cross);
                    stairrightcheck.setImageResource(R.drawable.cross);
                    extnleftcheck.setImageResource(R.drawable.cross);
                    extnrightcheck.setImageResource(R.drawable.cross);
                    staticleftcheck.setImageResource(R.drawable.cross);
                    staticrightcheck.setImageResource(R.drawable.cross);
                    proprioleftcheck.setImageResource(R.drawable.cross);
                    propriorightcheck.setImageResource(R.drawable.cross);

                    while (keys.hasNext()) {
                        String key = keys.next();
                        if ("Mobility Test".equalsIgnoreCase(key)) {
                            mobilityTestPresent = true;
                            mobleftcheck.setImageResource(R.drawable.tick);
                            mobrightcheck.setImageResource(R.drawable.tick);
                        } else if ("Proprioception Test".equalsIgnoreCase(key)) {
                            proprioceptionTestPresent = true;
                            proprioleftcheck.setImageResource(R.drawable.tick);
                            propriorightcheck.setImageResource(R.drawable.tick);
                        }
                        // If both are found, no need to continue
                        if (mobilityTestPresent && proprioceptionTestPresent) {
                            sumbitflag = 1;
                            break;
                        } else {
                            sumbitflag = 0;
                        }
                    }

                    while (keys.hasNext()) {
                        String key = keys.next();
                        if ("Dynamic Balance Test".equalsIgnoreCase(key)) {
                            dynamicleftcheck.setImageResource(R.drawable.tick);
                            dynamicrightcheck.setImageResource(R.drawable.tick);
                        } else if ("Walk and Gait Analysis".equalsIgnoreCase(key)) {
                            walkgaitleftcheck.setImageResource(R.drawable.tick);
                            walkgaitrightcheck.setImageResource(R.drawable.tick);
                        } else if ("Staircase Climbing Test".equalsIgnoreCase(key)) {
                            stairleftcheck.setImageResource(R.drawable.tick);
                            stairrightcheck.setImageResource(R.drawable.tick);
                        } else if ("Extension Lag Test".equalsIgnoreCase(key)) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = postdataobj.getJSONObject(key);
                            Iterator<String> keys1 = jsonObject.keys();
                            while (keys1.hasNext()) {
                                String key1 = keys1.next();
                                if (key1.contains("left")) {
                                    extnleftcheck.setImageResource(R.drawable.tick);
                                } else if (key1.contains("right")) {
                                    extnrightcheck.setImageResource(R.drawable.tick);
                                }
                            }
                        } else if ("Static Balance Test".equalsIgnoreCase(key)) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = postdataobj.getJSONObject(key);
                            Iterator<String> keys1 = jsonObject.keys();
                            while (keys1.hasNext()) {
                                String key1 = keys1.next();
                                if (key1.contains("left")) {
                                    staticleftcheck.setImageResource(R.drawable.tick);
                                } else if (key1.contains("right")) {
                                    stairrightcheck.setImageResource(R.drawable.tick);
                                }
                            }
                        }

                    }

                    if (sumbitflag == 1) {
                        postdata.put(postexedata);
                        Log.e("Anirudh P Menon", String.valueOf(postdata));
                        try {
                            // Loop through each object in the postdata array
                            for (int i = 0; i < postdata.length(); i++) {
                                JSONObject jsonObject = postdata.getJSONObject(i);

                                // Check if the 'exercises' key exists and contains "Mobility Test"
                                if (jsonObject.has("exercises")) {
                                    JSONObject exercises = jsonObject.getJSONObject("exercises");

                                    // Check if "Mobility Test" key exists and has values
                                    if (exercises.has("Mobility Test")) {
                                        // Get the "Mobility Test" data
                                        JSONObject mobilityTest = exercises.getJSONObject("Mobility Test");

                                        // Check if the "leftleg" array (or any other data within "Mobility Test") has values
                                        if (mobilityTest.has("leftleg") && mobilityTest.has("rightleg")) {
                                            JSONArray leftlegData = mobilityTest.getJSONArray("leftleg");
                                            JSONArray rightleg = mobilityTest.getJSONArray("rightleg");
                                            // Check if the "leftleg" array is not empty
                                            if (leftlegData.length() == 0) {
                                                mobleftcheck.setImageResource(R.drawable.cross);
                                                Toasty.error(DetailFrag_5.this, "Perform Mobility Test to submit data", Toast.LENGTH_SHORT, true).show();
                                                return;
                                            }
                                            if (rightleg.length() == 0) {
                                                mobrightcheck.setImageResource(R.drawable.cross);
                                                Toasty.error(DetailFrag_5.this, "Perform Mobility Test to submit data", Toast.LENGTH_SHORT, true).show();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //jsonArray = new JSONArray(jsonData);
                    Log.e("Final Posting Data", String.valueOf(postdata));
                    // Iterate through each object in the JSONArray
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject testObject = jsonArray.getJSONObject(i);
//
//                        // Iterate through the keys (test names)
//                        Iterator<String> keys = testObject.keys();
//                        while (keys.hasNext()) {
//                            String testName = keys.next();
//                            JSONObject testDetails = testObject.getJSONObject(testName);
//
//                            Log.d("Inba Test Name", testName);
//                            Log.d("Inba Test Details", testDetails.toString());
//                        }
//                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                if (sumbitflag == 1) {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                            Request.Method.PUT,
                            "https://api-wo6.onrender.com/update-assessment-info/" + HomeFragment.userid + "/" + 0,
                            postdata,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        // Convert the first item in the JSONArray to a JSONObject
                                        JSONObject responseObj = response.getJSONObject(0);
                                        Log.e("Posting Response", String.valueOf(response));

                                        if ("Assessment information updated successfully".equals(responseObj.getString("message"))) {
                                            cleanupBluetoothSockets();
                                            SharedPreferences.Editor editor = getSharedPreferences("BluetoothPrefs", MODE_PRIVATE).edit();
                                            editor.putBoolean("isConnected", false);
                                            editor.apply();

                                            dialog.dismiss();

                                            MainActivity.patflag = 0;
                                            SharedData.detailItems.clear();
                                            postdata = new JSONArray();
                                            postexevalues = new JSONArray();
                                            postdataobj = new JSONObject();
                                            postexedata = new JSONObject();
                                            postexeparameters = new JSONArray();
                                            postexesubdata = new JSONObject();
                                            Intent intent = new Intent(DetailFrag_5.this, Dashboard.class);
                                            startActivity(intent);
                                        }
                                        Log.d("Exercise Submit Response", responseObj.getString("message"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Exercise Submit Error", error.toString());
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/json");
                            return params;
                        }
                    };

//                 Add the request to the RequestQueue
                    requestQueue.add(jsonArrayRequest);
                }
                else {
                    if (mobilityTestPresent) {
                        Toasty.error(DetailFrag_5.this, "Proprioception Test Not Done", Toast.LENGTH_LONG).show();
                    } else if (proprioceptionTestPresent) {
                        Toasty.error(DetailFrag_5.this, "Mobility Test Not Done", Toast.LENGTH_LONG).show();
                    } else {
                        Toasty.error(DetailFrag_5.this, "Mobility Test and Proprioception Test Not Done", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });



    }

}
