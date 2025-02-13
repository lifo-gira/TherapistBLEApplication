package com.example.therapistbluelock;

public class NewPatient {
    private String name;
    private String condition;
    private int imageResource;

    public NewPatient(String name, String condition, int imageResource) {
        this.name = name;
        this.condition = condition;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public int getImageResource() {
        return imageResource;
    }
}
