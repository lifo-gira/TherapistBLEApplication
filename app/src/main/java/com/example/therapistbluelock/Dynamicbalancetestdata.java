package com.example.therapistbluelock;

public class Dynamicbalancetestdata {

    float sittostand, standtoshift, walktime;
    String actpas;

    public Dynamicbalancetestdata(float sittostand, float standtoshift, float walktime, String actpas) {
        this.sittostand = sittostand;
        this.standtoshift = standtoshift;
        this.walktime = walktime;
        this.actpas = actpas;
    }

    public float getSittostand() {
        return sittostand;
    }

    public String getActpas() {
        return actpas;
    }

    public void setActpas(String actpas) {
        this.actpas = actpas;
    }

    public float getStandtoshift() {
        return standtoshift;
    }

    public float getWalktime() {
        return walktime;
    }

    public void setSittostand(float sittostand) {
        this.sittostand = sittostand;
    }

    public void setStandtoshift(float standtoshift) {
        this.standtoshift = standtoshift;
    }

    public void setWalktime(float walktime) {
        this.walktime = walktime;
    }
}
