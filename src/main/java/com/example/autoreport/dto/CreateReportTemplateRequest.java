package com.example.autoreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateReportTemplateRequest {

    @NotBlank(message = "报表名称不能为空")
    @Size(max = 200, message = "报表名称长度不能超过200字符")
    private String name;

    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    @NotBlank(message = "自然语言查询不能为空")
    private String naturalQuery;

    private String cronExpression;

    private String outputFormat = "JSON";

    private String chartConfig;

    private String notificationConfig;

    // Constructors
    public CreateReportTemplateRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNaturalQuery() { return naturalQuery; }
    public void setNaturalQuery(String naturalQuery) { this.naturalQuery = naturalQuery; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public String getChartConfig() { return chartConfig; }
    public void setChartConfig(String chartConfig) { this.chartConfig = chartConfig; }

    public String getNotificationConfig() { return notificationConfig; }
    public void setNotificationConfig(String notificationConfig) { this.notificationConfig = notificationConfig; }
}