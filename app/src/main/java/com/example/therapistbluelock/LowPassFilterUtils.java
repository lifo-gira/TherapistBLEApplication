package com.example.therapistbluelock;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;

public class LowPassFilterUtils {

    /**
     * Applies a low-pass Butterworth filter to the input data.
     *
     * @param dataJson JSON array of input data to filter.
     * @param cutoff   Desired cutoff frequency in Hz.
     * @param fs       Sampling frequency in Hz.
     * @param order    Order of the filter (e.g., 4).
     * @return Filtered data as a JSON array.
     */
    public static JSONArray applyLowPassFilter(JSONArray dataJson, double cutoff, double fs, int order) {
        try {
            // Convert JSONArray to double array
            double[] data = new double[dataJson.length()];
            for (int i = 0; i < dataJson.length(); i++) {
                data[i] = dataJson.getDouble(i);
            }

            // Low-pass filter coefficients (placeholder example for 3rd order)
            double[] b = {0.2929, 0.5858, 0.2929}; // Numerator coefficients
            double[] a = {1.0000, -0.0000, 0.1716}; // Denominator coefficients

            // Filter the data
            double[] output = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                output[i] = b[0] * data[i]
                        + (i > 0 ? b[1] * data[i - 1] : 0)
                        + (i > 1 ? b[2] * data[i - 2] : 0)
                        - (i > 0 ? a[1] * output[i - 1] : 0)
                        - (i > 1 ? a[2] * output[i - 2] : 0);
            }

            // Convert filtered data back to JSONArray
            JSONArray filteredJson = new JSONArray();
            for (double value : output) {
                filteredJson.put(value);
            }

            return filteredJson;

        } catch (JSONException e) {
            Log.e("LowPassFilterUtils", "Error parsing JSON data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves filtered data to a CSV file.
     *
     * @param timestampsJson JSON array of timestamps corresponding to the data.
     * @param dataJson       JSON array of filtered data.
     * @param filename       Name of the file to save.
     */
    public static void saveToCSV(JSONArray timestampsJson, JSONArray dataJson, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Timestamp,Filtered_Acceleration\n");
            for (int i = 0; i < dataJson.length(); i++) {
                double timestamp = timestampsJson.getDouble(i);
                double filteredValue = dataJson.getDouble(i);
                writer.write(String.format("%.2f,%.2f\n", timestamp, filteredValue));
            }
        } catch (IOException | JSONException e) {
            Log.e("LowPassFilterUtils", "Error writing to file: " + e.getMessage());
        }
    }
}
