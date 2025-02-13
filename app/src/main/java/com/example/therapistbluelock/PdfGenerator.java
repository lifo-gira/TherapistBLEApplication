package com.example.therapistbluelock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfGenerator {

    public static List<Entry> entries = new ArrayList<>();
    public static JSONArray arraydata = new JSONArray();
    public static JSONArray analysedata = new JSONArray();
    public static JSONObject dataobj = new JSONObject();
    public static LineData lineData = new LineData();

    public static List<Entry> entries1 = new ArrayList<>();
    public static List<Entry> entries2 = new ArrayList<>();

    public static LineDataSet dataSet1, dataSet2;


    public static void generateAndDownloadPdf(Context context, LineChart chart, String minAngle, String maxAngle, String flexioncount, String extenstioncount, String rom, String name, String date, String time, String loginid, JSONObject datareportarray, String exername) {

        String datareportarr = datareportarray.toString();

        Log.e("Data Report Array", datareportarr);
        // Generate the PDF file
        File pdfFile = generatePdf(context, chart, minAngle, maxAngle, flexioncount, extenstioncount, rom, name, date, time, loginid, datareportarray, exername);

        if (pdfFile != null) {
            // Get URI for the file using FileProvider
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);

            // Create intent to open PDF using appropriate viewer
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Start the activity to view the PDF
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_LONG).show();
        }
    }

    public static File generatePdf(Context context, LineChart chart, String minAngle, String maxAngle, String flexioncount, String extenstioncount, String rom, String name, String date, String time, String loginid, JSONObject datareportarray, String exername) {
        PdfDocument pdfDocument = new PdfDocument();

        JSONArray leftData = new JSONArray();
        JSONArray rightData = new JSONArray();
        Iterator<String> modeKeys = datareportarray.keys();

        String mode1;

        int pageIndex = 0;

        int pagecount = datareportarray.length();

        int firstpage =0;

        Log.e("Inbasekar 2", String.valueOf(datareportarray));

        while (modeKeys.hasNext()) {
            String mode = modeKeys.next();
            JSONArray modeArray;

            try {
                modeArray = datareportarray.getJSONArray(mode);
                Log.e("PDF Report modearray", String.valueOf(modeArray));
            } catch (JSONException e) {
                throw new RuntimeException("Error retrieving mode array: " + e.getMessage());
            }

            if ("Dynamic Balance Test".equalsIgnoreCase(DetailFrag_5.selectedExercise) || "Staircase Climbing Test".equalsIgnoreCase(DetailFrag_5.selectedExercise) || "Walk and Gait Analysis".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                // Loop through each entry in the mode array
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageIndex + 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                entries.clear();
                entries2.clear();
                JSONObject dataObj = new JSONObject();
                JSONArray arrayData = new JSONArray();
                JSONArray analyseData = new JSONArray();
                JSONArray datarray = new JSONArray();
                mode1 = "";

                for (int i = 0; i < modeArray.length(); i++) {
                    dataObj = new JSONObject();
                    arrayData = new JSONArray();
                    analyseData = new JSONArray();
                    datarray = new JSONArray();


                    try {
                        dataObj = modeArray.getJSONObject(i);
                        Log.e("Inbasekar 6", String.valueOf(dataObj));
                        arrayData = new JSONArray(dataObj.getString("data"));
                        Log.e("PDF data inside the loop", String.valueOf(arrayData));
                        analyseData = new JSONArray(dataObj.getString("analyse"));
                        mode1 = dataObj.getString("mode");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (i == 0) {
                        // Populate entries for the chart
                        for (int j = 0; j < arrayData.length(); j++) {
                            try {
                                entries.add(new Entry((float) j, Float.parseFloat(String.valueOf(arrayData.get(j)))));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        dataSet1 = new LineDataSet(entries, "Data");
                        dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Waved line
                        dataSet1.setDrawValues(false);
                        dataSet1.setDrawCircles(false);
                        dataSet1.setColor(Color.BLUE);
                        dataSet1.setLineWidth(2f);
                    } else {
                        // Populate entries for the chart
                        for (int j = 0; j < arrayData.length(); j++) {
                            try {
                                entries2.add(new Entry((float) j, Float.parseFloat(String.valueOf(arrayData.get(j)))));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        dataSet2 = new LineDataSet(entries2, "Data");
                        dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Waved line
                        dataSet2.setDrawValues(false);
                        dataSet2.setDrawCircles(false);
                        dataSet2.setColor(Color.RED);
                        dataSet2.setLineWidth(2f);
                    }

                }

                LineData lineData = new LineData(dataSet1, dataSet2);
                chart.setData(lineData);
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getAxisRight().setEnabled(false);
                chart.getAxisLeft().setDrawGridLines(false);
                chart.getXAxis().setDrawGridLines(false);
                chart.getDescription().setEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.invalidate();


                Canvas canvas = page.getCanvas();

// Load the pdf_theme image
                Bitmap themeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf_theme); // Replace with actual image resource name

// Define the rectangle for the theme image
                float themeImageX = 0; // Start at the very left
                float themeImageY = 0; // Start at the very top
                float themeImageWidth = 595f; // Full width of the canvas (assuming A4 width at 72 PPI)
                float themeImageHeight = 100f; // Height of 100 units

// Draw the theme image at the top of the canvas
                canvas.drawBitmap(themeBitmap, null, new android.graphics.RectF(themeImageX, themeImageY, themeImageWidth, themeImageHeight), null);

// Offset Y position for the logo and title text (same line)
                float logoY = themeImageHeight + 20f;

// Set up paint for the title text
                Paint titlePaint = new Paint();
                titlePaint.setColor(Color.BLACK);
                titlePaint.setTextSize(24f); // Smaller text size (previously 30f)
                titlePaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsbold));

// Set up paint for the logo
                Paint logoPaint = new Paint();

// Logo image size
                float desiredLogoWidth = 100f;
                float desiredLogoHeight = 30f;
//                    Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.xolabslogo); // Replace with actual logo resource

// Logo position
                float logoX = 0f;
//                    canvas.drawBitmap(logoBitmap, null, new android.graphics.RectF(logoX, logoY, logoX + desiredLogoWidth, logoY + desiredLogoHeight), null);

// Title text position
                float titleTextStartX = logoX + desiredLogoWidth + 30f; // 30f is the gap between logo and text
                float titleTextTop = 50; // Vertically center the title text

// Draw the title text next to the logo
//                    canvas.drawText("Assessment Phase Report", titleTextStartX, titleTextTop, titlePaint);


// Set up paint for name, loginID, email, phone number, etc.
                Paint infoPaintname = new Paint();
                // Add a 40-pixel gap before the "Personal Information" title
                float gapBeforeTitle = 40;
                float titleTop = themeImageHeight + gapBeforeTitle;  // Adjust starting position by adding the gap

                titlePaint.setColor(Color.BLACK);
                titlePaint.setTextSize(22);
                titlePaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinssemibold));

// Load the medical icon
                Bitmap medicalIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.medical_icon);

// Scale the icon to 20x20 pixels
                Bitmap scaledIcon = Bitmap.createScaledBitmap(medicalIcon, 20, 20, false);

// Set the position for the icon (left side of the title)
                float iconLeft = 40;  // X position for the icon
                float iconTop = titleTop - 17;  // Y position for the icon, slightly above the title to align it properly

// Draw the scaled icon on the canvas
                canvas.drawBitmap(scaledIcon, iconLeft, iconTop, null);



                float infoTextTopname =0;
                float rightAlignX = 300f;
                if(firstpage == 0) {
                    firstpage =1;
// Draw "Personal Information" title to the right of the icon
                    // Title "Personal Information"
                    float titleLeft = iconLeft + scaledIcon.getWidth() + 10;  // Position the text 10 pixels to the right of the icon
                    canvas.drawText("Personal Information", titleLeft, titleTop, titlePaint);

// Draw horizontal line under "Personal Information"
                    Paint linePaint = new Paint();
                    linePaint.setColor(Color.BLACK);
                    linePaint.setStrokeWidth(2);  // Line thickness
                    float lineStartX = 40;  // Starting X position
                    float lineEndX = 550;   // Ending X position (adjust based on your layout)
                    float lineY = titleTop + 10;  // Y position just below the title

                    canvas.drawLine(lineStartX, lineY, lineEndX, lineY, linePaint); // Draw the line

// Set up paint for the name
                    Paint infoPaintname1 = new Paint();
                    infoPaintname.setColor(Color.BLACK);
                    infoPaintname.setTextSize(15);
                    infoPaintname.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsbold));
                    infoPaintname1.setColor(Color.BLACK);
                    infoPaintname1.setTextSize(15);
                    infoPaintname1.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));
                    infoTextTopname = lineY + 30;
// Starting position for name (left side)
                      // Starting position after the line
                    canvas.drawText("Patient Name", 40, infoTextTopname, infoPaintname);
                    canvas.drawText(name, 40, infoTextTopname + 15, infoPaintname1);

                    canvas.drawText("Date", 40, infoTextTopname + 45, infoPaintname);
                    canvas.drawText(date, 40, infoTextTopname + 60, infoPaintname1);

                    canvas.drawText("Time", 40, infoTextTopname + 90, infoPaintname);
                    canvas.drawText(time, 40, infoTextTopname + 105, infoPaintname1);
// Set up paint for other information (login ID, date, email, phone number)
                    Paint infoPaint = new Paint();
                    infoPaint.setColor(Color.BLACK);
                    infoPaint.setTextSize(12);
                    infoPaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

// Starting position for login ID, date, etc.
                    float infoTextTop = infoTextTopname + 30;  // Offset to position after Name


                    canvas.drawText("Email", rightAlignX, infoTextTopname, infoPaintname); // Assuming email is available
                    canvas.drawText(loginid + "@gmail.com", rightAlignX, infoTextTopname + 15, infoPaintname1); // Assuming email is available
                    infoTextTop += 20;

                    canvas.drawText("Phone Number", rightAlignX, infoTextTopname + 45, infoPaintname);
                    canvas.drawText("phoneNumber", rightAlignX, infoTextTopname + 60, infoPaintname1);
                    infoTextTop += 20;

                    canvas.drawLine(lineStartX, infoTextTopname + 120, lineEndX, infoTextTopname + 120, linePaint); // Draw the line
                }
                else{
                    infoTextTopname =0;
                }



// Set up paint for exercise name
                Paint titlePaint1 = new Paint();
                titlePaint1.setColor(Color.BLACK);
                titlePaint1.setTextSize(18);
                titlePaint1.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinssemibold));

                Paint titlePaint2 = new Paint();
                titlePaint2.setColor(Color.BLACK);
                titlePaint2.setTextSize(16);
                titlePaint2.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

// Starting position for exercise name
                float titleTextTop1 = infoTextTopname+165;  // New page starts at the top, spaced after the info section
                canvas.drawText("Exercise Name", 40, titleTextTop1, titlePaint1);
                canvas.drawText(exername, 40,titleTextTop1+20 , titlePaint2);

// Set up paint for mode
                Paint titlePaintmode = new Paint();
                titlePaintmode.setColor(Color.BLACK);
                titlePaintmode.setTextSize(18);
                titlePaintmode.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsmedium));

// Draw Mode
                float titleTextTopmode = titleTextTop1 + 60;
                // Split the 'Leg' value and display only the part before '-'
                String[] legValueParts = mode1.split("-");
                String legValue = legValueParts.length > 0 ? legValueParts[0].trim() : mode1; // Use everything if no hyphen is present

// Split the 'Mode' value and display only the part after '-'
                String[] modeValueParts = mode1.split("-");
                String modeValue = legValueParts.length > 1 ? modeValueParts[1].trim() : ""; // If there's no '-', leave it empty

// Draw the 'Leg' label and value (Before the '-')
                canvas.drawText("Leg", rightAlignX-50, titleTextTop1, titlePaint1);
                canvas.drawText("Both Leg", rightAlignX-50, titleTextTop1 + 20, titlePaint2);

// Draw the 'Mode' label and value (After the '-')

                if("Walk and Gait Analysis".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                    canvas.drawText("Cycle", rightAlignX + 100, titleTextTop1, titlePaint1);
                    canvas.drawText(mode1, rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                }
                else{
                    canvas.drawText("Mode", rightAlignX + 100, titleTextTop1, titlePaint1);
                    canvas.drawText(modeValue, rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                }


                XAxis xAxis = chart.getXAxis();
                YAxis leftYAxis = chart.getAxisLeft();
                YAxis rightYAxis = chart.getAxisRight();

// Set the axis line color to black and make it thicker
                xAxis.setAxisLineColor(Color.BLACK);
                xAxis.setAxisLineWidth(2f);  // Thicker line for X axis

                leftYAxis.setAxisLineColor(Color.BLACK);
                leftYAxis.setAxisLineWidth(2f);  // Thicker line for left Y axis

                rightYAxis.setAxisLineColor(Color.BLACK);
                rightYAxis.setAxisLineWidth(2f);  // Thicker line for right Y axis

// Optionally, customize the grid lines for more visibility
                xAxis.setGridColor(Color.BLACK); // Set grid color to black
                xAxis.setGridLineWidth(1.5f);    // Make grid lines thicker

                leftYAxis.setGridColor(Color.BLACK);
                leftYAxis.setGridLineWidth(1.5f);  // Make grid lines thicker

                rightYAxis.setGridColor(Color.BLACK);
                rightYAxis.setGridLineWidth(1.5f);  // Make grid lines thicker


                Bitmap bitmap = getBitmapFromView(chart);
                float pageWidth = 595f;
                float pageHeight = 842f;
                float contentWidth = pageWidth - 40f * 2;
                float scaleFactor = Math.min(contentWidth / bitmap.getWidth(), (pageHeight / 2) / bitmap.getHeight());
                int adjustedChartWidth = (int) (bitmap.getWidth() * scaleFactor);
                int adjustedChartHeight = (int) (bitmap.getHeight() * scaleFactor);
                float chartTop = titleTextTopmode;  // Spaced after the mode text
                canvas.drawBitmap(bitmap, null, new android.graphics.RectF(40, chartTop, 20 + adjustedChartWidth, chartTop + adjustedChartHeight), null);

                Paint textPaint = new Paint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(16); // Set a fixed size for text
                textPaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

                int rectWidth = 160; // Width for the rectangle
                int rectHeight = 80; // Height for the rectangle

// Create a bitmap for the rectangle with a gradient
                Bitmap rectBitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888);
                Canvas gradientCanvas = new Canvas(rectBitmap);

// Create a linear gradient with three colors
                Paint gradientPaint = new Paint();

                LinearGradient low = new LinearGradient(
                        0, 0, rectWidth, rectHeight,
                        new int[]{Color.parseColor("#B2E7E7"), Color.parseColor("#D6E9D5"), Color.parseColor("#E1EBC8")},
                        null, // Positions (evenly distributed)
                        Shader.TileMode.CLAMP
                );

                LinearGradient moderate = new LinearGradient(
                        0, 0, rectWidth, rectHeight,
                        new int[]{Color.parseColor("#F3E4BB"), Color.parseColor("#F8E1BE"), Color.parseColor("#FBD9BD")},
                        null, // Positions (evenly distributed)
                        Shader.TileMode.CLAMP
                );
                gradientPaint.setShader(moderate);

                LinearGradient critical = new LinearGradient(
                        0, 0, rectWidth, rectHeight,
                        new int[]{Color.parseColor("#FAD3C1"), Color.parseColor("#F3CAC4"), Color.parseColor("#F8C9BF")},
                        null, // Positions (evenly distributed)
                        Shader.TileMode.CLAMP
                );

// Draw the gradient onto the bitmap with rounded corners
                float cornerRadius = 10f; // Adjust for rounded corner radius
                gradientCanvas.drawRoundRect(new RectF(0, 0, rectWidth, rectHeight), cornerRadius, cornerRadius, gradientPaint);

                float textTop = chartTop + adjustedChartHeight + 40;
                float columnSpacing = 160; // Adjust for spacing between columns
                float rowSpacing = 20;
                float verticalOffset = 10;
                if ("Dynamic Balance Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                    try {
                        String[] dynamicBalanceLabels = {"Sit Stand Time", "Stand Shift Time", "Walk Time"};
                        String[] dynamicBalanceValues = {
                                String.valueOf(analyseData.get(0)),
                                String.valueOf(analyseData.get(1)),
                                String.valueOf(analyseData.get(2))
                        };
                        float[] dynamicBalanceX = {40, 60 + columnSpacing, 420};

                        // Adjust for vertical positioning

                        for (int k = 0; k < dynamicBalanceLabels.length; k++) {
                            // Draw the gradient rectangle with rounded corners
                            canvas.drawRoundRect(
                                    new RectF(dynamicBalanceX[k], textTop, dynamicBalanceX[k] + rectWidth, textTop + rectHeight),
                                    cornerRadius, cornerRadius, gradientPaint);

                            // Calculate text widths for value and label
                            String value = dynamicBalanceValues[k];
                            String label = dynamicBalanceLabels[k];
                            float valueWidth = textPaint.measureText(value);
                            float labelWidth = textPaint.measureText(label);

                            // Position value at the center
                            float valueX = dynamicBalanceX[k] + (rectWidth / 2) - (valueWidth / 2);
                            float valueY = textTop + (rectHeight / 3) + verticalOffset; // Move further down

                            // Draw the value
                            canvas.drawText(value, valueX, valueY, textPaint);

                            // Position label below the value
                            float labelX = dynamicBalanceX[k] + (rectWidth / 2) - (labelWidth / 2);
                            float labelY = valueY + 20; // Adjust vertical spacing

                            // Draw the label
                            canvas.drawText(label, labelX, labelY, textPaint);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if ("Staircase Climbing Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                    try {
                        String[] staircaseClimbingLabels = {"Steps Covered", "Ascent Time", "Descent Time", "Turn Time"};
                        String[] staircaseClimbingValues = {
                                String.valueOf(analyseData.get(0)),
                                String.valueOf(analyseData.get(1)),
                                String.valueOf(analyseData.get(2)),
                                String.valueOf(analyseData.get(3))
                        };
                        float[] staircaseClimbingX = {40, 420, 40, 420}; // Positioning for each value

                        for (int k = 0; k < staircaseClimbingLabels.length; k++) {
                            // Draw the gradient rectangle with rounded corners
                            float rectTop = textTop + (k < 2 ? 0 : rowSpacing + rectHeight); // Adjust vertical positioning for second row
                            canvas.drawRoundRect(
                                    new RectF(staircaseClimbingX[k], rectTop, staircaseClimbingX[k] + rectWidth, rectTop + rectHeight),
                                    cornerRadius, cornerRadius, gradientPaint);

                            // Calculate text widths for value and label
                            String value = staircaseClimbingValues[k];
                            String label = staircaseClimbingLabels[k];
                            float valueWidth = textPaint.measureText(value);
                            float labelWidth = textPaint.measureText(label);

                            // Position value at the center
                            float valueX = staircaseClimbingX[k] + (rectWidth / 2) - (valueWidth / 2);
                            float valueY = rectTop + (rectHeight / 3) + verticalOffset;

                            // Draw the value
                            canvas.drawText(value, valueX, valueY, textPaint);

                            // Position label below the value
                            float labelX = staircaseClimbingX[k] + (rectWidth / 2) - (labelWidth / 2);
                            float labelY = valueY + 20; // Adjust for vertical spacing

                            // Draw the label
                            canvas.drawText(label, labelX, labelY, textPaint);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if ("Walk and Gait Analysis".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                    try {
                        // Define the labels and values
                        String[] walkAndGaitLabels = {
                                "Distance", "Step Count", "Active Time", "Stand Time",
                                "Swing Time", "Stance Time", "Stride Length", "Stride Length % h",
                                "Step Length", "Meanvelocity", "Cadence"
                        };
                        String[] walkAndGaitValues = {
                                String.valueOf(analyseData.get(0)), String.valueOf(analyseData.get(1)),
                                String.valueOf(analyseData.get(2)), String.valueOf(analyseData.get(3)),
                                String.valueOf(analyseData.get(4)), String.valueOf(analyseData.get(5)),
                                String.valueOf(analyseData.get(6)), String.valueOf(analyseData.get(7)),
                                String.valueOf(analyseData.get(8)), String.valueOf(analyseData.get(9)),
                                String.valueOf(analyseData.get(10))
                        };

                        // Adjust dimensions for smaller rectangles
                        int rectWidth1 = 90; // Smaller width to fit 4 items in one row
                        int rectHeight2 = 50; // Height remains the same
                        float cornerRadius1 = 8f; // Rounded corners
                        float columnSpacing1 = 15; // Reduced spacing between columns
                        float rowSpacing1 = 20; // Row spacing
                        textPaint.setTextSize(10); // Slightly smaller text size

                        int columns = 4; // Number of columns per row
                        int rows = (int) Math.ceil((double) walkAndGaitLabels.length / columns);

                        for (int k = 0; k < walkAndGaitLabels.length; k++) {
                            int row = k / columns;
                            int col = k % columns;

                            // Calculate positions dynamically
                            float rectLeft = 50 + col * (rectWidth1 + columnSpacing1); // Adjusted starting position
                            float rectTop = textTop + row * (rectHeight2 + rowSpacing1);

                            // Create and apply a gradient for each rectangle
                            Paint gradientPaints = new Paint();
                            gradientPaints.setShader(new LinearGradient(
                                    0, 0, rectWidth1, rectHeight2,
                                    new int[]{Color.parseColor("#F3E4BB"), Color.parseColor("#F8E1BE"), Color.parseColor("#E1EBC8")},
                                    null, Shader.TileMode.CLAMP
                            ));

                            // Draw the gradient rectangle
                            canvas.drawRoundRect(
                                    new RectF(rectLeft, rectTop, rectLeft + rectWidth1, rectTop + rectHeight2),
                                    cornerRadius1, cornerRadius1, gradientPaints
                            );

                            // Draw the text
                            String value = walkAndGaitValues[k];
                            String label = walkAndGaitLabels[k];
                            float valueWidth = textPaint.measureText(value);
                            float labelWidth = textPaint.measureText(label);

                            // Position and draw value text
                            float valueX = rectLeft + (rectWidth1 / 2) - (valueWidth / 2);
                            float valueY = rectTop + (rectHeight2 / 3); // Adjusted for smaller rectangles
                            canvas.drawText(value, valueX, valueY, textPaint);

                            // Position and draw label text below the value
                            float labelX = rectLeft + (rectWidth1 / 2) - (labelWidth / 2);
                            float labelY = valueY + 15; // Adjusted for smaller spacing
                            canvas.drawText(label, labelX, labelY, textPaint);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }



                pdfDocument.finishPage(page);
                pageIndex++;

//                    try {
//                        datarray = datareportarray.getJSONArray(mode);
//                        dataObj = datarray.getJSONObject(0);
//                        arraydata = new JSONArray(dataObj.getString("data"));
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }

            }
            else {
                for (int i = 0; i < modeArray.length(); i++) {
                    Log.e("PDF Report",i+" / "+modeArray);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageIndex + 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    entries.clear();

                    JSONObject dataObj;
                    JSONArray arrayData, analyseData, datarray;

                    try {
                        dataObj = modeArray.getJSONObject(i);
                        arrayData = new JSONArray(dataObj.getString("data"));
                        Log.e("PDF data inside the loop", String.valueOf(arrayData));
                        analyseData = new JSONArray(dataObj.getString("analyse"));
                        mode1 = dataObj.getString("mode");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    // Populate entries for the chart
                    for (int j = 0; j < arrayData.length(); j++) {
                        try {
                            entries.add(new Entry((float) j, Float.parseFloat(String.valueOf(arrayData.get(j)))));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Data");
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Waved line
                    dataSet.setDrawValues(false);
                    dataSet.setDrawCircles(false);
                    dataSet.setColor(Color.BLUE);
                    dataSet.setLineWidth(2f);

                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    chart.getAxisRight().setEnabled(false);
                    chart.getAxisLeft().setDrawGridLines(false);
                    chart.getXAxis().setDrawGridLines(false);
                    chart.getDescription().setEnabled(false);
                    chart.getLegend().setEnabled(false);
                    chart.invalidate();

                    // Get the canvas from the page
                    Canvas canvas = page.getCanvas();

// Load the pdf_theme image
                    Bitmap themeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf_theme); // Replace with actual image resource name

// Define the rectangle for the theme image
                    float themeImageX = 0; // Start at the very left
                    float themeImageY = 0; // Start at the very top
                    float themeImageWidth = 595f; // Full width of the canvas (assuming A4 width at 72 PPI)
                    float themeImageHeight = 100f; // Height of 100 units

// Draw the theme image at the top of the canvas
                    canvas.drawBitmap(themeBitmap, null, new android.graphics.RectF(themeImageX, themeImageY, themeImageWidth, themeImageHeight), null);

// Offset Y position for the logo and title text (same line)
                    float logoY = themeImageHeight + 20f;

// Set up paint for the title text
                    Paint titlePaint = new Paint();
                    titlePaint.setColor(Color.BLACK);
                    titlePaint.setTextSize(24f); // Smaller text size (previously 30f)
                    titlePaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsbold));

// Set up paint for the logo
                    Paint logoPaint = new Paint();

// Logo image size
                    float desiredLogoWidth = 100f;
                    float desiredLogoHeight = 30f;
//                    Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.xolabslogo); // Replace with actual logo resource

// Logo position
                    float logoX = 0f;
//                    canvas.drawBitmap(logoBitmap, null, new android.graphics.RectF(logoX, logoY, logoX + desiredLogoWidth, logoY + desiredLogoHeight), null);

// Title text position
                    float titleTextStartX = logoX + desiredLogoWidth + 30f; // 30f is the gap between logo and text
                    float titleTextTop = 50; // Vertically center the title text

// Draw the title text next to the logo
//                    canvas.drawText("Assessment Phase Report", titleTextStartX, titleTextTop, titlePaint);


// Set up paint for name, loginID, email, phone number, etc.
                    Paint infoPaintname = new Paint();
                    // Add a 40-pixel gap before the "Personal Information" title
                    float gapBeforeTitle = 40;
                    float titleTop = themeImageHeight + gapBeforeTitle;  // Adjust starting position by adding the gap

                    titlePaint.setColor(Color.BLACK);
                    titlePaint.setTextSize(22);
                    titlePaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinssemibold));

// Load the medical icon
                    Bitmap medicalIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.medical_icon);

// Scale the icon to 20x20 pixels
                    Bitmap scaledIcon = Bitmap.createScaledBitmap(medicalIcon, 20, 20, false);

// Set the position for the icon (left side of the title)
                    float iconLeft = 40;  // X position for the icon
                    float iconTop = titleTop - 17;  // Y position for the icon, slightly above the title to align it properly

// Draw the scaled icon on the canvas
                    canvas.drawBitmap(scaledIcon, iconLeft, iconTop, null);



                    float infoTextTopname = 0;
                    float rightAlignX = 300f;
                    if(firstpage == 0) {
                        firstpage =1;
// Draw "Personal Information" title to the right of the icon
                        // Title "Personal Information"
                        float titleLeft = iconLeft + scaledIcon.getWidth() + 10;  // Position the text 10 pixels to the right of the icon
                        canvas.drawText("Personal Information", titleLeft, titleTop, titlePaint);

// Draw horizontal line under "Personal Information"
                        Paint linePaint = new Paint();
                        linePaint.setColor(Color.BLACK);
                        linePaint.setStrokeWidth(2);  // Line thickness
                        float lineStartX = 40;  // Starting X position
                        float lineEndX = 550;   // Ending X position (adjust based on your layout)
                        float lineY = titleTop + 10;  // Y position just below the title

                        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, linePaint); // Draw the line

// Set up paint for the name
                        Paint infoPaintname1 = new Paint();
                        infoPaintname.setColor(Color.BLACK);
                        infoPaintname.setTextSize(15);
                        infoPaintname.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsbold));
                        infoPaintname1.setColor(Color.BLACK);
                        infoPaintname1.setTextSize(15);
                        infoPaintname1.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));
                        infoTextTopname = lineY + 30;
// Starting position for name (left side)
                          // Starting position after the line
                        canvas.drawText("Patient Name", 40, infoTextTopname, infoPaintname);
                        canvas.drawText(name, 40, infoTextTopname + 15, infoPaintname1);

                        canvas.drawText("Date", 40, infoTextTopname + 45, infoPaintname);
                        canvas.drawText(date, 40, infoTextTopname + 60, infoPaintname1);

                        canvas.drawText("Time", 40, infoTextTopname + 90, infoPaintname);
                        canvas.drawText(time, 40, infoTextTopname + 105, infoPaintname1);
// Set up paint for other information (login ID, date, email, phone number)
                        Paint infoPaint = new Paint();
                        infoPaint.setColor(Color.BLACK);
                        infoPaint.setTextSize(12);
                        infoPaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

// Starting position for login ID, date, etc.
                        float infoTextTop = infoTextTopname + 30;  // Offset to position after Name


                        canvas.drawText("Email", rightAlignX, infoTextTopname, infoPaintname); // Assuming email is available
                        canvas.drawText(loginid + "@gmail.com", rightAlignX, infoTextTopname + 15, infoPaintname1); // Assuming email is available
                        infoTextTop += 20;

                        canvas.drawText("Phone Number", rightAlignX, infoTextTopname + 45, infoPaintname);
                        canvas.drawText("phoneNumber", rightAlignX, infoTextTopname + 60, infoPaintname1);
                        infoTextTop += 20;

                        canvas.drawLine(lineStartX, infoTextTopname + 120, lineEndX, infoTextTopname + 120, linePaint); // Draw the line
// Set up paint for exercise name
                    }
                    else{
                        infoTextTopname = 0;
                    }
                    Paint titlePaint1 = new Paint();
                    titlePaint1.setColor(Color.BLACK);
                    titlePaint1.setTextSize(18);
                    titlePaint1.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinssemibold));

                    Paint titlePaint2 = new Paint();
                    titlePaint2.setColor(Color.BLACK);
                    titlePaint2.setTextSize(16);
                    titlePaint2.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

// Starting position for exercise name
                    float titleTextTop1 = infoTextTopname+165;  // New page starts at the top, spaced after the info section
                    canvas.drawText("Exercise Name", 40, titleTextTop1, titlePaint1);
                    canvas.drawText(exername, 40,titleTextTop1+20 , titlePaint2);

// Set up paint for mode
                    Paint titlePaintmode = new Paint();
                    titlePaintmode.setColor(Color.BLACK);
                    titlePaintmode.setTextSize(18);
                    titlePaintmode.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsmedium));

// Draw Mode
                    float titleTextTopmode = titleTextTop1 + 60;
                    if("Mobility Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {

                        String[] parts = mode1.split("-");


                        canvas.drawText("Leg", rightAlignX - 50, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[0]+ "-" + parts[2], rightAlignX - 50, titleTextTop1 + 20, titlePaint2);

                        canvas.drawText("Mode", rightAlignX + 100, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[1], rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                    }
                    else if("Extension Lag Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                        String[] parts = mode1.split("-");


                        canvas.drawText("Leg", rightAlignX - 50, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[0]+ "-" + parts[1], rightAlignX - 50, titleTextTop1 + 20, titlePaint2);

                        canvas.drawText("Cycle", rightAlignX + 100, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[2], rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                    }
                    else if("Proprioception Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)){
                        String[] parts = mode1.split("-");

                        canvas.drawText("Leg", rightAlignX - 50, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[0] + parts[1], rightAlignX - 50, titleTextTop1 + 20, titlePaint2);

                        canvas.drawText("Cycle", rightAlignX + 100, titleTextTop1, titlePaint1);
                        canvas.drawText(parts[2], rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                    }
                    else{
                        canvas.drawText("Leg", rightAlignX - 50, titleTextTop1, titlePaint1);
                        canvas.drawText(mode1, rightAlignX - 50, titleTextTop1 + 20, titlePaint2);

                        canvas.drawText("Mode", rightAlignX + 100, titleTextTop1, titlePaint1);
                        canvas.drawText(mode1, rightAlignX + 100, titleTextTop1 + 20, titlePaint2);
                    }

// Draw the chart bitmap below the info
                    // Assuming 'chart' is your LineChart instance

// Set the X and Y axes appearance
                    XAxis xAxis = chart.getXAxis();
                    YAxis leftYAxis = chart.getAxisLeft();
                    YAxis rightYAxis = chart.getAxisRight();

// Set the axis line color to black and make it thicker
                    xAxis.setAxisLineColor(Color.BLACK);
                    xAxis.setAxisLineWidth(2f);  // Thicker line for X axis

                    leftYAxis.setAxisLineColor(Color.BLACK);
                    leftYAxis.setAxisLineWidth(2f);  // Thicker line for left Y axis

                    rightYAxis.setAxisLineColor(Color.BLACK);
                    rightYAxis.setAxisLineWidth(2f);  // Thicker line for right Y axis

// Optionally, customize the grid lines for more visibility
                    xAxis.setGridColor(Color.BLACK); // Set grid color to black
                    xAxis.setGridLineWidth(1.5f);    // Make grid lines thicker

                    leftYAxis.setGridColor(Color.BLACK);
                    leftYAxis.setGridLineWidth(1.5f);  // Make grid lines thicker

                    rightYAxis.setGridColor(Color.BLACK);
                    rightYAxis.setGridLineWidth(1.5f);  // Make grid lines thicker

// Now, get the bitmap after making the modifications to the chart appearance
                    Bitmap bitmap = getBitmapFromView(chart);

// Scaling the bitmap as per your original code
                    float pageWidth = 595f;
                    float pageHeight = 842f;
                    float contentWidth = pageWidth - 40f * 2;
                    float scaleFactor = Math.min(contentWidth / bitmap.getWidth(), (pageHeight / 2) / bitmap.getHeight());
                    int adjustedChartWidth = (int) (bitmap.getWidth() * scaleFactor);
                    int adjustedChartHeight = (int) (bitmap.getHeight() * scaleFactor);

// Starting position for the chart
                    float chartTop = titleTextTopmode;  // Spaced after the mode text
                    canvas.drawBitmap(bitmap, null, new android.graphics.RectF(40, chartTop, 20 + adjustedChartWidth, chartTop + adjustedChartHeight), null);

// Set up paint for the analysis data
                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.BLACK);
                    textPaint.setTextSize(16); // Set a fixed size for text
                    textPaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsregular));

                    int rectWidth = 160; // Width for the rectangle
                    int rectHeight = 80; // Height for the rectangle

// Create a bitmap for the rectangle with a gradient
                    Bitmap rectBitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888);
                    Canvas gradientCanvas = new Canvas(rectBitmap);

// Create a linear gradient with three colors
                    Paint gradientPaint = new Paint();

                    LinearGradient low = new LinearGradient(
                            0, 0, rectWidth, rectHeight,
                            new int[]{Color.parseColor("#B2E7E7"), Color.parseColor("#D6E9D5"), Color.parseColor("#E1EBC8")},
                            null, // Positions (evenly distributed)
                            Shader.TileMode.CLAMP
                    );

                    LinearGradient moderate = new LinearGradient(
                            0, 0, rectWidth, rectHeight,
                            new int[]{Color.parseColor("#F3E4BB"), Color.parseColor("#F8E1BE"), Color.parseColor("#FBD9BD")},
                            null, // Positions (evenly distributed)
                            Shader.TileMode.CLAMP
                    );
                    gradientPaint.setShader(moderate);

                    LinearGradient critical = new LinearGradient(
                            0, 0, rectWidth, rectHeight,
                            new int[]{Color.parseColor("#FAD3C1"), Color.parseColor("#F3CAC4"), Color.parseColor("#F8C9BF")},
                            null, // Positions (evenly distributed)
                            Shader.TileMode.CLAMP
                    );

// Draw the gradient onto the bitmap with rounded corners
                    float cornerRadius = 10f; // Adjust for rounded corner radius
                    gradientCanvas.drawRoundRect(new RectF(0, 0, rectWidth, rectHeight), cornerRadius, cornerRadius, gradientPaint);

                    float textTop = chartTop + adjustedChartHeight + 40;
                    float columnSpacing = 160; // Adjust for spacing between columns
                    float rowSpacing = 20;

                    if("Mobility Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                        try {
                            // First row of analysis data
                            String[] firstRowLabels = {"Max Flexion", "Min Extension"};
                            String[] firstRowValues = {
                                    String.valueOf(analyseData.get(1)),
                                    String.valueOf(analyseData.get(0))
                            };
                            float[] firstRowX = {40, 60 + columnSpacing, 400};
                            float verticalOffset = 10; // Adjust this value to move text and values further down

                            for (int k = 0; k < firstRowLabels.length; k++) {
                                // Draw the gradient rectangle with rounded corners
                                canvas.drawRoundRect(
                                        new RectF(firstRowX[k], textTop, firstRowX[k] + rectWidth, textTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = firstRowValues[k];
                                String label = firstRowLabels[k];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = firstRowX[k] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = textTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = firstRowX[k] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }

//                            // Second row of analysis data
//                            String[] secondRowLabels = {"Max Angle", "Extension Count"};
//                            String[] secondRowValues = {
//                                    String.valueOf(analyseData.get(1)),
//                                    String.valueOf(analyseData.get(3))
//                            };
//                            float[] secondRowX = {40, 60 + columnSpacing};
//
//                            for (int j = 0; j < secondRowLabels.length; j++) {
//                                // Draw the gradient rectangle with rounded corners
//                                float rectTop = textTop + rowSpacing + rectHeight;
//                                canvas.drawRoundRect(
//                                        new RectF(secondRowX[j], rectTop, secondRowX[j] + rectWidth, rectTop + rectHeight),
//                                        cornerRadius, cornerRadius, gradientPaint);
//
//                                // Calculate the value and label positioning
//                                String value = secondRowValues[j];
//                                String label = secondRowLabels[j];
//
//                                // Calculate text widths for value and label
//                                float valueWidth = textPaint.measureText(value);
//                                float labelWidth = textPaint.measureText(label);
//
//                                // Positioning value at the center of the rectangle (both horizontally and vertically)
//                                float valueX = secondRowX[j] + (rectWidth / 2) - (valueWidth / 2);
//                                float valueY = rectTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down
//
//                                // Draw the value
//                                canvas.drawText(value, valueX, valueY, textPaint);
//
//                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
//                                float labelX = secondRowX[j] + (rectWidth / 2) - (labelWidth / 2);
//                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label
//
//                                // Draw the label
//                                canvas.drawText(label, labelX, labelY, textPaint);
//                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if("Extension Lag Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                        try {
                            // First row of analysis data
                            String[] firstRowLabels = {"Active ED", "Passive ED","Total ED"};
                            String[] firstRowValues = {
                                    String.valueOf(analyseData.get(0)),
                                    String.valueOf(analyseData.get(1)),
                                    String.valueOf(analyseData.get(2))
                            };
                            float[] firstRowX = {40, 60 + columnSpacing, 400};
                            float verticalOffset = 10; // Adjust this value to move text and values further down

                            for (int k = 0; k < firstRowLabels.length; k++) {
                                // Draw the gradient rectangle with rounded corners
                                canvas.drawRoundRect(
                                        new RectF(firstRowX[k], textTop, firstRowX[k] + rectWidth, textTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = firstRowValues[k];
                                String label = firstRowLabels[k];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = firstRowX[k] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = textTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = firstRowX[k] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }

//                            // Second row of analysis data
//                            String[] secondRowLabels = {"Max Angle", "Extension Count"};
//                            String[] secondRowValues = {
//                                    String.valueOf(analyseData.get(1)),
//                                    String.valueOf(analyseData.get(3))
//                            };
//                            float[] secondRowX = {40, 60 + columnSpacing};
//
//                            for (int j = 0; j < secondRowLabels.length; j++) {
//                                // Draw the gradient rectangle with rounded corners
//                                float rectTop = textTop + rowSpacing + rectHeight;
//                                canvas.drawRoundRect(
//                                        new RectF(secondRowX[j], rectTop, secondRowX[j] + rectWidth, rectTop + rectHeight),
//                                        cornerRadius, cornerRadius, gradientPaint);
//
//                                // Calculate the value and label positioning
//                                String value = secondRowValues[j];
//                                String label = secondRowLabels[j];
//
//                                // Calculate text widths for value and label
//                                float valueWidth = textPaint.measureText(value);
//                                float labelWidth = textPaint.measureText(label);
//
//                                // Positioning value at the center of the rectangle (both horizontally and vertically)
//                                float valueX = secondRowX[j] + (rectWidth / 2) - (valueWidth / 2);
//                                float valueY = rectTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down
//
//                                // Draw the value
//                                canvas.drawText(value, valueX, valueY, textPaint);
//
//                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
//                                float labelX = secondRowX[j] + (rectWidth / 2) - (labelWidth / 2);
//                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label
//
//                                // Draw the label
//                                canvas.drawText(label, labelX, labelY, textPaint);
//                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if("Proprioception Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                        try {
                            // First row of analysis data
                            String[] firstRowLabels = {"Max Angle"};
                            String[] firstRowValues = {
                                    String.valueOf(analyseData.get(1))
                            };
                            float[] firstRowX = {40, 60 + columnSpacing, 400};
                            float verticalOffset = 10; // Adjust this value to move text and values further down

                            for (int k = 0; k < firstRowLabels.length; k++) {
                                // Draw the gradient rectangle with rounded corners
                                canvas.drawRoundRect(
                                        new RectF(firstRowX[k], textTop, firstRowX[k] + rectWidth, textTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = firstRowValues[k];
                                String label = firstRowLabels[k];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = firstRowX[k] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = textTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = firstRowX[k] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }

//                            // Second row of analysis data
//                            String[] secondRowLabels = {"Max Angle", "Extension Count"};
//                            String[] secondRowValues = {
//                                    String.valueOf(analyseData.get(1)),
//                                    String.valueOf(analyseData.get(3))
//                            };
//                            float[] secondRowX = {40, 60 + columnSpacing};
//
//                            for (int j = 0; j < secondRowLabels.length; j++) {
//                                // Draw the gradient rectangle with rounded corners
//                                float rectTop = textTop + rowSpacing + rectHeight;
//                                canvas.drawRoundRect(
//                                        new RectF(secondRowX[j], rectTop, secondRowX[j] + rectWidth, rectTop + rectHeight),
//                                        cornerRadius, cornerRadius, gradientPaint);
//
//                                // Calculate the value and label positioning
//                                String value = secondRowValues[j];
//                                String label = secondRowLabels[j];
//
//                                // Calculate text widths for value and label
//                                float valueWidth = textPaint.measureText(value);
//                                float labelWidth = textPaint.measureText(label);
//
//                                // Positioning value at the center of the rectangle (both horizontally and vertically)
//                                float valueX = secondRowX[j] + (rectWidth / 2) - (valueWidth / 2);
//                                float valueY = rectTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down
//
//                                // Draw the value
//                                canvas.drawText(value, valueX, valueY, textPaint);
//
//                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
//                                float labelX = secondRowX[j] + (rectWidth / 2) - (labelWidth / 2);
//                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label
//
//                                // Draw the label
//                                canvas.drawText(label, labelX, labelY, textPaint);
//                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(!"Static Balance Test".equalsIgnoreCase(DetailFrag_5.selectedExercise) && !"Mobility Test".equalsIgnoreCase(DetailFrag_5.selectedExercise)) {
                        try {
                            // First row of analysis data
                            String[] firstRowLabels = {"Min Angle", "Flexion Count", "ROM"};
                            String[] firstRowValues = {
                                    String.valueOf(analyseData.get(0)),
                                    String.valueOf(analyseData.get(2)),
                                    String.valueOf(analyseData.get(4))
                            };
                            float[] firstRowX = {40, 60 + columnSpacing, 400};
                            float verticalOffset = 10; // Adjust this value to move text and values further down

                            for (int k = 0; k < firstRowLabels.length; k++) {
                                // Draw the gradient rectangle with rounded corners
                                canvas.drawRoundRect(
                                        new RectF(firstRowX[k], textTop, firstRowX[k] + rectWidth, textTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = firstRowValues[k];
                                String label = firstRowLabels[k];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = firstRowX[k] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = textTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = firstRowX[k] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }

                            // Second row of analysis data
                            String[] secondRowLabels = {"Max Angle", "Extension Count"};
                            String[] secondRowValues = {
                                    String.valueOf(analyseData.get(1)),
                                    String.valueOf(analyseData.get(3))
                            };
                            float[] secondRowX = {40, 60 + columnSpacing};

                            for (int j = 0; j < secondRowLabels.length; j++) {
                                // Draw the gradient rectangle with rounded corners
                                float rectTop = textTop + rowSpacing + rectHeight;
                                canvas.drawRoundRect(
                                        new RectF(secondRowX[j], rectTop, secondRowX[j] + rectWidth, rectTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = secondRowValues[j];
                                String label = secondRowLabels[j];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = secondRowX[j] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = rectTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = secondRowX[j] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        try {
                            // First row of analysis data
                            String[] firstRowLabels = {"Balance Time"};
                            String[] firstRowValues = {
                                    String.valueOf(analyseData.get(0))
                            };
                            float[] firstRowX = {40, 60 + columnSpacing, 400};
                            float verticalOffset = 10; // Adjust this value to move text and values further down

                            for (int k = 0; k < firstRowLabels.length; k++) {
                                // Draw the gradient rectangle with rounded corners
                                canvas.drawRoundRect(
                                        new RectF(firstRowX[k], textTop, firstRowX[k] + rectWidth, textTop + rectHeight),
                                        cornerRadius, cornerRadius, gradientPaint);

                                // Calculate the value and label positioning
                                String value = firstRowValues[k];
                                String label = firstRowLabels[k];

                                // Calculate text widths for value and label
                                float valueWidth = textPaint.measureText(value);
                                float labelWidth = textPaint.measureText(label);

                                // Positioning value at the center of the rectangle (both horizontally and vertically)
                                float valueX = firstRowX[k] + (rectWidth / 2) - (valueWidth / 2);
                                float valueY = textTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down

                                // Draw the value
                                canvas.drawText(value, valueX, valueY, textPaint);

                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
                                float labelX = firstRowX[k] + (rectWidth / 2) - (labelWidth / 2);
                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label

                                // Draw the label
                                canvas.drawText(label, labelX, labelY, textPaint);
                            }

//                            // Second row of analysis data
//                            String[] secondRowLabels = {"Max Angle", "Extension Count"};
//                            String[] secondRowValues = {
//                                    String.valueOf(analyseData.get(1)),
//                                    String.valueOf(analyseData.get(3))
//                            };
//                            float[] secondRowX = {40, 60 + columnSpacing};
//
//                            for (int j = 0; j < secondRowLabels.length; j++) {
//                                // Draw the gradient rectangle with rounded corners
//                                float rectTop = textTop + rowSpacing + rectHeight;
//                                canvas.drawRoundRect(
//                                        new RectF(secondRowX[j], rectTop, secondRowX[j] + rectWidth, rectTop + rectHeight),
//                                        cornerRadius, cornerRadius, gradientPaint);
//
//                                // Calculate the value and label positioning
//                                String value = secondRowValues[j];
//                                String label = secondRowLabels[j];
//
//                                // Calculate text widths for value and label
//                                float valueWidth = textPaint.measureText(value);
//                                float labelWidth = textPaint.measureText(label);
//
//                                // Positioning value at the center of the rectangle (both horizontally and vertically)
//                                float valueX = secondRowX[j] + (rectWidth / 2) - (valueWidth / 2);
//                                float valueY = rectTop + (rectHeight / 3) + verticalOffset; // Adjusted to move further down
//
//                                // Draw the value
//                                canvas.drawText(value, valueX, valueY, textPaint);
//
//                                // Positioning label below the value (centered horizontally, and adjust vertical spacing)
//                                float labelX = secondRowX[j] + (rectWidth / 2) - (labelWidth / 2);
//                                float labelY = valueY + 20; // Adjust for vertical spacing between value and label
//
//                                // Draw the label
//                                canvas.drawText(label, labelX, labelY, textPaint);
//                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

// Finish the page
                    pdfDocument.finishPage(page);
                    pageIndex++;

                    try {
                        datarray = datareportarray.getJSONArray(mode);
                        dataObj = datarray.getJSONObject(0);
                        arraydata = new JSONArray(dataObj.getString("data"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


//        for(int i=0; i<pagecount; i++){
//
//
//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, i+1).create();
//            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
//            entries.clear();
//
//            try {
//                dataobj = datareportarray.getJSONObject(i);
//                arraydata = new JSONArray(dataobj.getString("data"));
//                analysedata = new JSONArray(dataobj.getString("analyse"));
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//
//            for(int j =0; j<arraydata.length(); j++){
//                try {
//                    entries.add(new Entry((float)j, Float.parseFloat(String.valueOf(arraydata.get(j)))));
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            LineDataSet dataSet = new LineDataSet(entries, "Data");
//            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Waved line
//            dataSet.setDrawValues(true); // Hide values
//            dataSet.setColor(Color.BLUE);
//            dataSet.setLineWidth(2f);
//
//            lineData = new LineData(dataSet);
//            chart.setData(lineData);
//            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//            chart.getAxisRight().setEnabled(false);
//            chart.getAxisLeft().setDrawGridLines(false);
//            chart.getXAxis().setDrawGridLines(false);
//            chart.getDescription().setEnabled(false);
//            chart.getLegend().setEnabled(false);
//            chart.invalidate();
//
//            Canvas canvas = page.getCanvas();
//
//            Paint titlePaint = new Paint();
//            titlePaint.setColor(Color.BLACK);
//            titlePaint.setTextSize(30); // Title text size
//            //titlePaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsbold));
//            Paint.FontMetrics titleFontMetrics = titlePaint.getFontMetrics();
//            float titleTextHeight = titleFontMetrics.descent - titleFontMetrics.ascent;
//            float titleTextLeft = (titlePaint.measureText("WAD")-40);
//            float titleTextRight = (595f - titlePaint.measureText("WAD"));
//            float titleTextTop = 50; // Position title 40 pixels from the top of the page
//            canvas.drawText("WAD", titleTextLeft, titleTextTop, titlePaint);
//
//            Paint infoPaint = new Paint();
//            infoPaint.setColor(Color.BLACK);
//            infoPaint.setTextSize(15); // Info text size
//            //infoPaint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsmedium));
//            Paint.FontMetrics infoFontMetrics = infoPaint.getFontMetrics();
//            float infoTextHeight = infoFontMetrics.descent - infoFontMetrics.ascent;
//            float infoTextTop = titleTextTop + titleTextHeight - 10; // Position info text below title with a 20 pixel gap
//            float infoTextLeft = 40;
//            float infoTextRight = 595f - 40 - infoPaint.measureText("Hospital Name: ");
//            canvas.drawText("Name: "+name, infoTextLeft, infoTextTop, infoPaint);
//            canvas.drawText("Date: "+date, infoTextLeft, infoTextTop + infoTextHeight + 10, infoPaint);
//            canvas.drawText("LoginID: "+loginid, infoTextLeft, infoTextTop + (infoTextHeight + 10) * 2, infoPaint);
//            canvas.drawText("XYZ Hospital", infoTextRight, infoTextTop, infoPaint);
//            canvas.drawText("Time: "+time, infoTextRight, infoTextTop + infoTextHeight + 10, infoPaint);
//            canvas.drawText(exername, infoTextRight, infoTextTop + (infoTextHeight + 10) * 2, infoPaint);
//
//            Bitmap bitmap = getBitmapFromView(chart);
//            int chartWidth = bitmap.getWidth();
//            int chartHeight = bitmap.getHeight();
//            float pageWidth = 595f;
//            float pageHeight = 842f;
//            float margin = 20f; // Set a margin around the content
//            float contentWidth = pageWidth - 2 * margin;
//            float contentHeight = pageHeight - 2 * margin;
//            float scaleFactor = Math.min(contentWidth / chartWidth, (contentHeight / 2) / chartHeight);
//            int adjustedChartWidth = (int) (chartWidth * scaleFactor);
//            int adjustedChartHeight = (int) (chartHeight * scaleFactor);
//            float chartLeft = (pageWidth - adjustedChartWidth) / 2;
//            float chartTop = infoTextTop + (infoTextHeight + 10) * 2 + 20; // Position chart below title with a 20 pixel gap
//            canvas.drawBitmap(bitmap, null, new android.graphics.RectF(chartLeft, chartTop, chartLeft + adjustedChartWidth, chartTop + adjustedChartHeight), null);
//
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(18); // Convert dp to pixels
//            //paint.setTypeface(ResourcesCompat.getFont(context.getApplicationContext(), R.font.poppinsmedium)); // Set font family to "sans-serif-medium" as "poppinsmedium" might not be available
//            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//            float textHeight = fontMetrics.descent - fontMetrics.ascent;
//            float totalTextHeight = textHeight * 4 + 16;
//            float textTop = chartTop + adjustedChartHeight + 20;
//            float textTop1 = chartTop + adjustedChartHeight + 40;
//            float textLeft = (pageWidth - paint.measureText("Min Angle: " + minAngle)) / 2;
//            if(i == 0) {
//                try {
//                    canvas.drawText("Min Angle: " + analysedata.get(0), textLeft, textTop + 30, paint);
//                    canvas.drawText("Max Angle: " + analysedata.get(1), textLeft, textTop + (textHeight + 5) * 2, paint);
//                    canvas.drawText("Flexion Count: " + analysedata.get(2), textLeft, textTop + (textHeight + 5) * 3, paint);
//                    canvas.drawText("Extension Count: " + analysedata.get(3), textLeft, textTop + (textHeight + 5) * 4, paint);
//                    canvas.drawText("ROM: " + analysedata.get(4), textLeft, textTop + (textHeight + 5) * 5, paint);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            } else{
//                try {
//                    canvas.drawText("Min Angle: " + analysedata.get(0), textLeft, textTop + 30, paint);
//                    canvas.drawText("Max Angle: " + analysedata.get(1), textLeft, textTop + (textHeight + 5) * 2, paint);
//                    canvas.drawText("Flexion Velocity: " + analysedata.get(2), textLeft, textTop + (textHeight + 5) * 3, paint);
//                    canvas.drawText("Extension Velocity: " + analysedata.get(3), textLeft, textTop + (textHeight + 5) * 4, paint);
//                    canvas.drawText("ROM: " + analysedata.get(4), textLeft, textTop + (textHeight + 5) * 5, paint);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            pdfDocument.finishPage(page);
//
//        }


        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "chart.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(context, "PDF generated and saved to " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.e("PDF Generated", pdfFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
        DrawMainChart(arraydata, chart);
        return pdfFile;
    }

    private static void DrawMainChart(JSONArray arraydata, LineChart chart) {

        entries1.clear();

        for (int j = 0; j < arraydata.length(); j++) {
            try {
                entries1.add(new Entry((float) j + 1, Float.parseFloat(String.valueOf(arraydata.get(j)))));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        LineDataSet dataSet = new LineDataSet(entries1, "Data");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Waved line
        dataSet.setDrawValues(false); // Hide values
        dataSet.setDrawCircles(false);
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);

        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();

    }


    private static Bitmap getBitmapFromView(View view) {
        // Create a bitmap from the chart view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}