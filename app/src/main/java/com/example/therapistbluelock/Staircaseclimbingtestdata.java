package com.example.therapistbluelock;

public class Staircaseclimbingtestdata {
    int steps;
    float ascenttime,decenttime,turntime;
    String actpas;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getAscenttime() {
        return ascenttime;
    }

    public void setAscenttime(float ascenttime) {
        this.ascenttime = ascenttime;
    }

    public float getDecenttime() {
        return decenttime;
    }

    public void setDecenttime(float decenttime) {
        this.decenttime = decenttime;
    }

    public float getTurntime() {
        return turntime;
    }

    public void setTurntime(float turntime) {
        this.turntime = turntime;
    }

    public String getActpas() {
        return actpas;
    }

    public void setActpas(String actpas) {
        this.actpas = actpas;
    }

    public Staircaseclimbingtestdata(int steps, float ascenttime, float decenttime, float turntime, String actpas) {
        this.steps = steps;
        this.ascenttime = ascenttime;
        this.decenttime = decenttime;
        this.turntime = turntime;
        this.actpas = actpas;
    }
}
