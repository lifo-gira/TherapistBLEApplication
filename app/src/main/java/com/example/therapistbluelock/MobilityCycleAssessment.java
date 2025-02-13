package com.example.therapistbluelock;

public class MobilityCycleAssessment {

    private final float maxflexion,minextension;
    private final String mode; // Change to int

    public float getMaxflexion() {
        return maxflexion;
    }

    public float getMinextension() {
        return minextension;
    }

    public String  getMode() {
        return mode;
    }

    public MobilityCycleAssessment(float maxflexion, float minextension, String mode) {
        this.maxflexion = maxflexion;
        this.minextension = minextension;
        this.mode = mode;
    }
}
