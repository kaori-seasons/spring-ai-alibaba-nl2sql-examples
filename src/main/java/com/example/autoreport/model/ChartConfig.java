package com.example.autoreport.model;

import java.util.List;

public class ChartConfig {
    private List<Chart> charts;

    public ChartConfig() {}

    public List<Chart> getCharts() { return charts; }
    public void setCharts(List<Chart> charts) { this.charts = charts; }

    public static class Chart {
        private String type;
        private String title;
        private String xField;
        private String yField;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getXField() { return xField; }
        public void setXField(String xField) { this.xField = xField; }

        public String getYField() { return yField; }
        public void setYField(String yField) { this.yField = yField; }
    }
}