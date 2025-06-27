package com.example.autoreport.model;

import java.util.List;
import java.util.Map;

public class ReportData {
    private List<Map<String, Object>> data;
    private int totalCount;
    private List<String> columns;

    public ReportData() {}

    public ReportData(List<Map<String, Object>> data) {
        this.data = data;
        this.totalCount = data.size();
        if (!data.isEmpty()) {
            this.columns = List.copyOf(data.get(0).keySet());
        }
    }

    public List<Map<String, Object>> getData() { return data; }
    public void setData(List<Map<String, Object>> data) { this.data = data; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }
}

