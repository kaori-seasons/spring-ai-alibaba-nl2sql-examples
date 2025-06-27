package com.example.autoreport.controller;

import com.example.autoreport.dto.*;
import com.example.autoreport.entity.ReportExecution;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.exception.ReportException;
import com.example.autoreport.repository.ReportExecutionRepository;
import com.example.autoreport.repository.ReportTemplateRepository;
import com.example.autoreport.service.AutoReportService;
import com.example.autoreport.service.ReportScheduler;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@Validated
public class ReportController {

    private final AutoReportService autoReportService;
    private final ReportScheduler reportScheduler;
    private final ReportTemplateRepository templateRepository;
    private final ReportExecutionRepository executionRepository;

    public ReportController(AutoReportService autoReportService,
                            ReportScheduler reportScheduler,
                            ReportTemplateRepository templateRepository,
                            ReportExecutionRepository executionRepository) {
        this.autoReportService = autoReportService;
        this.reportScheduler = reportScheduler;
        this.templateRepository = templateRepository;
        this.executionRepository = executionRepository;
    }

    /**
     * 创建报表模板
     */
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<ReportTemplate>> createTemplate(
            @Valid @RequestBody CreateReportTemplateRequest request) {

        ReportTemplate template = autoReportService.createTemplate(request);

        // 如果有定时表达式，启动调度
        if (template.getCronExpression() != null) {
            reportScheduler.scheduleReport(template);
        }

        return ResponseEntity.ok(ApiResponse.success(template));
    }

    /**
     * 获取报表模板列表
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<Page<ReportTemplate>>> getTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReportTemplate> templates;

        if (StringUtils.hasText(keyword)) {
            templates = templateRepository.findByNameContainingOrDescriptionContaining(
                    keyword, keyword, pageable);
        } else {
            templates = templateRepository.findAll(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    /**
     * 手动执行报表
     */
    @PostMapping("/templates/{id}/execute")
    public ResponseEntity<ApiResponse<ReportExecution>> executeReport(@PathVariable Long id) {
        ReportExecution execution = autoReportService.executeReport(id);
        return ResponseEntity.ok(ApiResponse.success(execution));
    }

    /**
     * 测试自然语言查询
     */
    @PostMapping("/test-query")
    public ResponseEntity<ApiResponse<TestQueryResponse>> testNaturalQuery(
            @Valid @RequestBody TestQueryRequest request) {

        try {
            String generatedSql = autoReportService.testNaturalQuery(request.getQuery());
            TestQueryResponse response = new TestQueryResponse(true, generatedSql, null);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            TestQueryResponse response = new TestQueryResponse(false, null, e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(response));
        }
    }

    /**
     * 获取报表执行历史
     */
    @GetMapping("/templates/{id}/executions")
    public ResponseEntity<ApiResponse<Page<ReportExecution>>> getExecutionHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("executionTime").descending());
        Page<ReportExecution> executions = executionRepository.findByTemplateId(id, pageable);

        return ResponseEntity.ok(ApiResponse.success(executions));
    }

    /**
     * 下载报表文件
     */
    @GetMapping("/executions/{id}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) throws IOException {
        ReportExecution execution = executionRepository.findById(id)
                .orElseThrow(() -> new ReportException("执行记录不存在"));

        if (execution.getOutputFilePath() == null) {
            throw new ReportException("报表文件不存在");
        }

        Resource resource = new FileSystemResource(execution.getOutputFilePath());
        if (!resource.exists()) {
            throw new ReportException("报表文件已被删除");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
