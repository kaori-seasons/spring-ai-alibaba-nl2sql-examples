package com.example.autoreport.service;

import com.example.autoreport.entity.ReportExecution;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.model.ReportData;

public interface ReportNotificationService {
    void sendNotification(ReportTemplate template, ReportExecution execution, ReportData reportData);
}