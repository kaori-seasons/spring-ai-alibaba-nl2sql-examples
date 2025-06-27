package com.example.autoreport.service;

import com.alibaba.cloud.ai.service.Nl2SqlService;
import com.example.autoreport.dto.CreateReportTemplateRequest;
import com.example.autoreport.entity.*;
import com.example.autoreport.exception.ReportException;
import com.example.autoreport.model.ChartConfig;
import com.example.autoreport.model.ReportData;
import com.example.autoreport.repository.ReportExecutionRepository;
import com.example.autoreport.repository.ReportTemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AutoReportService {

    private static final Logger log = LoggerFactory.getLogger(AutoReportService.class);

    private final Nl2SqlService nl2SqlService;
    private final ReportTemplateRepository templateRepository;
    private final ReportExecutionRepository executionRepository;
    private final ReportVisualizationService visualizationService;
    private final ReportNotificationService notificationService;
    private final ReportFileService fileService;
    private final ReportDataService reportDataService;
    private final ObjectMapper objectMapper;

    public AutoReportService(Nl2SqlService nl2SqlService,
                             ReportTemplateRepository templateRepository,
                             ReportExecutionRepository executionRepository,
                             ReportVisualizationService visualizationService,
                             ReportNotificationService notificationService,
                             ReportFileService fileService,
                             ReportDataService reportDataService,
                             ObjectMapper objectMapper) {
        this.nl2SqlService = nl2SqlService;
        this.templateRepository = templateRepository;
        this.executionRepository = executionRepository;
        this.visualizationService = visualizationService;
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.reportDataService = reportDataService;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行报表生成
     */
    public ReportExecution executeReport(Long templateId) {
        ReportTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ReportException("报表模板不存在: " + templateId));

        ReportExecution execution = new ReportExecution(templateId);
        execution.setStatus(ExecutionStatus.RUNNING);
        execution = executionRepository.save(execution);

        long startTime = System.currentTimeMillis();

        try {
            log.info("开始执行报表: {} [{}]", template.getName(), templateId);

            // 1. 使用NL2SQL生成SQL
            String naturalQuery = template.getNaturalQuery();
            String generatedSql = nl2SqlService.nl2sql(naturalQuery);
            execution.setGeneratedSql(generatedSql);
            log.info("生成SQL成功: {}", generatedSql);

            // 2. 执行SQL获取数据
            List<Map<String, Object>> resultData = reportDataService.executeQuery(generatedSql);
            String resultJson = objectMapper.writeValueAsString(resultData);
            execution.setResultData(resultJson);
            log.info("查询数据成功，返回 {} 条记录", resultData.size());

            // 3. 数据可视化处理
            ReportData reportData = new ReportData(resultData);
            ChartConfig chartConfig = parseChartConfig(template.getChartConfig());

            // 4. 生成输出文件
            String outputFilePath = generateOutputFile(template, reportData, chartConfig);
            execution.setOutputFilePath(outputFilePath);

            // 5. 发送通知
            sendNotifications(template, execution, reportData);

            execution.setStatus(ExecutionStatus.SUCCESS);
            execution.setCompletionTime(LocalDateTime.now());
            execution.setExecutionDuration(System.currentTimeMillis() - startTime);

            log.info("报表执行成功: {} [{}]", template.getName(), templateId);

        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            execution.setCompletionTime(LocalDateTime.now());
            execution.setExecutionDuration(System.currentTimeMillis() - startTime);
            log.error("报表执行失败, templateId: {}", templateId, e);
        }

        return executionRepository.save(execution);
    }

    /**
     * 创建报表模板
     */
    public ReportTemplate createTemplate(CreateReportTemplateRequest request) {
        // 验证自然语言查询是否有效
        try {
            String testSql = nl2SqlService.nl2sql(request.getNaturalQuery());
            log.info("模板验证成功，生成SQL: {}", testSql);
        } catch (Exception e) {
            throw new ReportException("自然语言查询验证失败: " + e.getMessage());
        }

        ReportTemplate template = new ReportTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setNaturalQuery(request.getNaturalQuery());
        template.setCronExpression(request.getCronExpression());
        template.setOutputFormat(request.getOutputFormat());
        template.setChartConfig(request.getChartConfig());
        template.setNotificationConfig(request.getNotificationConfig());
        template.setStatus(ReportStatus.ACTIVE);

        return templateRepository.save(template);
    }

    /**
     * 测试自然语言查询
     */
    public String testNaturalQuery(String query) throws Exception {
        return nl2SqlService.nl2sql(query);
    }

    /**
     * 批量执行定时报表
     */
    @Async
    public void executeBatchReports(List<Long> templateIds) {
        for (Long templateId : templateIds) {
            try {
                executeReport(templateId);
            } catch (Exception e) {
                log.error("批量执行报表失败, templateId: {}", templateId, e);
            }
        }
    }

    // 私有方法实现
    private ChartConfig parseChartConfig(String chartConfigJson) {
        if (chartConfigJson == null || chartConfigJson.trim().isEmpty()) {
            return new ChartConfig();
        }
        try {
            return objectMapper.readValue(chartConfigJson, ChartConfig.class);
        } catch (Exception e) {
            log.warn("解析图表配置失败: {}", e.getMessage());
            return new ChartConfig();
        }
    }

    private String generateOutputFile(ReportTemplate template, ReportData reportData, ChartConfig chartConfig) {
        try {
            return fileService.generateReportFile(template, reportData, chartConfig);
        } catch (Exception e) {
            log.error("生成报表文件失败: {}", e.getMessage());
            return null;
        }
    }

    private void sendNotifications(ReportTemplate template, ReportExecution execution, ReportData reportData) {
        try {
            notificationService.sendNotification(template, execution, reportData);
        } catch (Exception e) {
            log.error("发送通知失败: {}", e.getMessage());
        }
    }
}