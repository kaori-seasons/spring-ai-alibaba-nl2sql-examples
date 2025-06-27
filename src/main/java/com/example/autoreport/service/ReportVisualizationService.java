package com.example.autoreport.service;

import com.example.autoreport.model.ChartConfig;
import com.example.autoreport.model.ReportData;

public interface ReportVisualizationService {
    byte[] generateChart(ReportData reportData, ChartConfig.Chart chartConfig);
}
