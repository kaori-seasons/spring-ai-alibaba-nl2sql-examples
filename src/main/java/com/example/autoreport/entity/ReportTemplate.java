package com.example.autoreport.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "report_templates")
public class ReportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "natural_query", columnDefinition = "TEXT", nullable = false)
    private String naturalQuery;

    @Column(name = "cron_expression", length = 100)
    private String cronExpression;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.ACTIVE;

    @Column(name = "output_format", length = 50)
    private String outputFormat = "JSON";

    @Column(name = "chart_config", columnDefinition = "TEXT")
    private String chartConfig;

    @Column(name = "notification_config", columnDefinition = "TEXT")
    private String notificationConfig;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ReportTemplate() {}

    public ReportTemplate(String name, String naturalQuery) {
        this.name = name;
        this.naturalQuery = naturalQuery;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNaturalQuery() { return naturalQuery; }
    public void setNaturalQuery(String naturalQuery) { this.naturalQuery = naturalQuery; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public String getChartConfig() { return chartConfig; }
    public void setChartConfig(String chartConfig) { this.chartConfig = chartConfig; }

    public String getNotificationConfig() { return notificationConfig; }
    public void setNotificationConfig(String notificationConfig) { this.notificationConfig = notificationConfig; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
