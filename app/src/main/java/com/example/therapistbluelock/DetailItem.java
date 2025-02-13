package com.example.therapistbluelock;

import android.graphics.Color;

public class DetailItem {
    private int imageResId;
    private String title;
    private String status;
    private int backgroundTint;
    private String itemType; // Add a field for item type

    // Constructor
    public DetailItem(int imageResId, String title, String status, int backgroundTint, String itemType) {
        this.imageResId = imageResId;
        this.title = title;
        this.status = status;
        this.backgroundTint = backgroundTint;
        this.itemType = itemType; // Initialize item type
    }

    // Getters
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public int getBackgroundTint() { return backgroundTint; }
    public String getItemType() { return itemType; } // Getter for item type

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Setter for background tint (to change color)
    public void setBackgroundTint(int backgroundTint) {
        this.backgroundTint = backgroundTint;
    }

    // Method to mark as "Completed" and change background color
    public void markAsCompleted() {
        this.status = "Completed";
        this.backgroundTint = Color.GREEN; // Set background color to green when completed
    }
}
