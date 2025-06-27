package com.example.autoreport.service;

import com.example.autoreport.entity.ReportStatus;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.repository.ReportTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@EnableScheduling
public class ReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);

    private final AutoReportService autoReportService;
    private final ReportTemplateRepository templateRepository;
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public ReportScheduler(AutoReportService autoReportService,
                           ReportTemplateRepository templateRepository,
                           TaskScheduler taskScheduler) {
        this.autoReportService = autoReportService;
        this.templateRepository = templateRepository;
        this.taskScheduler = taskScheduler;
    }

    /**
     * 应用启动时初始化所有定时任务
     */
    @PostConstruct
    public void initializeScheduledReports() {
        List<ReportTemplate> activeTemplates = templateRepository
                .findByStatusAndCronExpressionIsNotNull(ReportStatus.ACTIVE);

        for (ReportTemplate template : activeTemplates) {
            scheduleReport(template);
        }

        log.info("初始化了 {} 个定时报表任务", activeTemplates.size());
    }

    /**
     * 调度单个报表
     */
    public void scheduleReport(ReportTemplate template) {
        if (template.getCronExpression() == null) {
            return;
        }

        // 移除已存在的任务
        unscheduleReport(template.getId());

        // 创建新的定时任务
        CronTrigger cronTrigger = new CronTrigger(template.getCronExpression());
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> {
                    log.info("执行定时报表: {} [{}]", template.getName(), template.getId());
                    autoReportService.executeReport(template.getId());
                },
                cronTrigger
        );

        scheduledTasks.put(template.getId(), scheduledTask);
        log.info("报表 {} 已调度, Cron: {}", template.getName(), template.getCronExpression());
    }

    /**
     * 取消调度
     */
    public void unscheduleReport(Long templateId) {
        ScheduledFuture<?> task = scheduledTasks.remove(templateId);
        if (task != null) {
            task.cancel(false);
            log.info("取消报表调度: {}", templateId);
        }
    }

    /**
     * 手动触发报表执行
     */
    public void triggerReport(Long templateId) {
        autoReportService.executeReport(templateId);
    }
}