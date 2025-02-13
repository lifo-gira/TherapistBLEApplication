package com.example.therapistbluelock;

import com.github.mikephil.charting.data.Entry;
import java.util.List;

public class Muscle {
    private final String name;
    private final String level;
    private final List<Entry> chartData;

    public Muscle(String name, String level, List<Entry> chartData) {
        this.name = name;
        this.level = level;
        this.chartData = chartData;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public List<Entry> getChartData() {
        return chartData;
    }
}
