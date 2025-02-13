package com.example.therapistbluelock;

import android.content.Context;
import android.os.Environment;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SimpleJSONToCSV {

    public static void exportGraphDataToCSV(Context context, List<Entry> graphData,
                                            JSONArray timeArray, JSONArray angleArray, JSONArray jsonDataArray,
                                            JSONArray jsonDataArray2, JSONArray jsonDataArray3, JSONArray jsonDataArray4,
                                            JSONArray jsonDataArray5, JSONArray jsonDataArray6, JSONArray jsonDataArray7,
                                            JSONArray jsonDataArray8, JSONArray jsonDataArray9, JSONArray jsonDataArray10,
                                             String fileName) {

        // Check if external storage is writable
        if (!isExternalStorageWritable()) {
            System.out.println("External storage is not writable.");
            return;
        }

        // Create the CSV file
        File csvFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        FileWriter writer = null;
        try {
            writer = new FileWriter(csvFile);

            // Write the CSV header
            writer.append("Time (Entry),Angle (Entry),Roll,Yaw,Pitch,AcclX,AcclY,AcclZ,MagX,MagY,MagZ,GyroX,GyroY,GyroZ\n");

            // Get the maximum length of all the data sources
            int maxLength = Math.max(graphData.size(), Math.max(timeArray.length(),
                    Math.max(angleArray.length(), Math.max(jsonDataArray.length(),
                            Math.max(jsonDataArray2.length(), Math.max(jsonDataArray3.length(),
                                    Math.max(jsonDataArray4.length(), Math.max(jsonDataArray5.length(),
                                            Math.max(jsonDataArray6.length(), Math.max(jsonDataArray7.length(),
                                                    Math.max(jsonDataArray8.length(), Math.max(jsonDataArray9.length(),jsonDataArray10.length()))))))))))));

            // Write data to the CSV file
            for (int i = 0; i < maxLength; i++) {
                // Add Time and Angle from Entry (graphData)
                if (i < graphData.size()) {
                    Entry entry = graphData.get(i);
                    writer.append(String.valueOf(entry.getX())).append(",") // Time from Entry
                            .append(String.valueOf(entry.getY())).append(","); // Angle from Entry
                } else {
                    writer.append(",,"); // Empty placeholders if no more Entry data
                }

                // Add Time from JSON array
                if (i < timeArray.length()) {
                    writer.append(timeArray.get(i).toString());
                }
                writer.append(",");

                // Add Angle from JSON array
                if (i < angleArray.length()) {
                    writer.append(angleArray.get(i).toString());
                }
                writer.append(",");

                // Add JSON data from all arrays (12 arrays in total)
                appendJsonData(writer, i, jsonDataArray);
                appendJsonData(writer, i, jsonDataArray2);
                appendJsonData(writer, i, jsonDataArray3);
                appendJsonData(writer, i, jsonDataArray4);
                appendJsonData(writer, i, jsonDataArray5);
                appendJsonData(writer, i, jsonDataArray6);
                appendJsonData(writer, i, jsonDataArray7);
                appendJsonData(writer, i, jsonDataArray8);
                appendJsonData(writer, i, jsonDataArray9);
                appendJsonData(writer, i, jsonDataArray10);

                writer.append("\n");
            }

            writer.flush();
            System.out.println("CSV file exported successfully: " + csvFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to append JSON data
    private static void appendJsonData(FileWriter writer, int i, JSONArray jsonArray) {
        if (i < jsonArray.length()) {
            try {
                writer.append(jsonArray.get(i).toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            writer.append(",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to check if external storage is writable
    public static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
