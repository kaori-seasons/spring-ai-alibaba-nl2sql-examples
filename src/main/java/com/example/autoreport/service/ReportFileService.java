package com.example.autoreport.service;

import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.model.ChartConfig;
import com.example.autoreport.model.ReportData;

public interface ReportFileService {
    String generateReportFile(ReportTemplate template, ReportData reportData, ChartConfig chartConfig) throws Exception;
}