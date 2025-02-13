package com.example.therapistbluelock;

public class Staticbalancetestdata {

    float time;
    String eyesstatus;

    public Staticbalancetestdata(float time, String eyesstatus) {
        this.time = time;
        this.eyesstatus=eyesstatus;
    }

    public String getEyesstatus() {
        return eyesstatus;
    }

    public void setEyesstatus(String eyesstatus) {
        this.eyesstatus = eyesstatus;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
