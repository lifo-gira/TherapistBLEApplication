package com.example.therapistbluelock;

public class Exercise {
    private String name;
    private String video;
    private int repCount;
    private int setCount;
    private int imageResId;
    private String category; // New field for category

    public Exercise(String name, String video, int repCount, int setCount, int imageResId, String category) {
        this.name = name;
        this.video = video;
        this.repCount = repCount;
        this.setCount = setCount;
        this.imageResId = imageResId;
        this.category = category; // Initialize category
    }

    public String getName() {
        return name;
    }

    public String getVideo() {
        return video;
    }

    public void setRepCount(int repCount) {
        this.repCount = repCount;
    }

    public void setSetCount(int setCount) {
        this.setCount = setCount;
    }

    public int getRepCount() {
        return repCount;
    }

    public int getSetCount() {
        return setCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getCategory() {
        return category; // Getter for category
    }
}
