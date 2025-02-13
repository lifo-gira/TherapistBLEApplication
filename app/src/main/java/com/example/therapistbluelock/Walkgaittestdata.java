package com.example.therapistbluelock;

import java.util.ArrayList;
import java.util.List;

public class Walkgaittestdata {

    double totalDistance,avgStandtime,meanVelocity;
    int stepCountwalk,breakcount;

    List<String> swingtime = new ArrayList<>();
    List<String> stance = new ArrayList<>();
    List<String> stride = new ArrayList<>();
    List<String> strideper = new ArrayList<>();
    List<String> step = new ArrayList<>();
    double cade;

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getAvgStandtime() {
        return avgStandtime;
    }

    public void setAvgStandtime(double avgStandtime) {
        this.avgStandtime = avgStandtime;
    }

    public double getMeanVelocity() {
        return meanVelocity;
    }

    public void setMeanVelocity(double meanVelocity) {
        this.meanVelocity = meanVelocity;
    }

    public int getStepCountwalk() {
        return stepCountwalk;
    }

    public void setStepCountwalk(int stepCountwalk) {
        this.stepCountwalk = stepCountwalk;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public int getBreakcount() {
        return breakcount;
    }

    public void setBreakcount(int breakcount) {
        this.breakcount = breakcount;
    }

    public Walkgaittestdata(double totalDistance, double avgStandtime, List swingtime, List stance, List stride, double meanVelocity, double cade, List step, List strideper ,int stepCountwalk, long activeTime, int breakcount) {
        this.totalDistance = totalDistance;
        this.avgStandtime = avgStandtime;
        this.swingtime = new ArrayList<>(swingtime);
        this.stance = new ArrayList<>(stance);
        this.stride = new ArrayList<>(stride);
        this.meanVelocity = meanVelocity;
        this.cade = cade;
        this.step = new ArrayList<>(step);
        this.strideper = new ArrayList<>(strideper);
        this.stepCountwalk = stepCountwalk;
        this.activeTime = activeTime;
        this.breakcount = breakcount;
    }



    public List<String> getStance() {
        return stance;
    }

    public void setStance(List<String> stance) {
        this.stance = stance;
    }


    public List<String> getSwingtime() {
        return swingtime;
    }

    public void setSwingtime(List<String> swingtime) {
        this.swingtime = swingtime;
    }

    public List<String> getStride() {
        return stride;
    }

    public void setStride(List<String> stride) {
        this.stride = stride;
    }

    public List<String> getStrideper() {
        return strideper;
    }

    public void setStrideper(List<String> strideper) {
        this.strideper = strideper;
    }

    public List<String> getStep() {
        return step;
    }

    public void setStep(List<String> step) {
        this.step = step;
    }

    public double getCade() {
        return cade;
    }

    public void setCade(double cade) {
        this.cade = cade;
    }

    long activeTime;

}
