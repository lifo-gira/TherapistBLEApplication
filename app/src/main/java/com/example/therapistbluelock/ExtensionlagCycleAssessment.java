package com.example.therapistbluelock;

public class ExtensionlagCycleAssessment {
    private final float activeed,passiveed,totaled; // Change to int

    public float getActiveed() {
        return activeed;
    }

    public float getPassiveed() {
        return passiveed;
    }

    public float getTotaled() {
        return totaled;
    }

    public ExtensionlagCycleAssessment(float activeed, float passiveed, float totaled) {
        this.activeed = activeed;
        this.passiveed = passiveed;
        this.totaled = totaled;
    }
}
