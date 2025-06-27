package com.example.autoreport.service.impl;

import com.example.autoreport.config.AutoReportProperties;
import com.example.autoreport.entity.ReportExecution;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.model.ReportData;
import com.example.autoreport.service.ReportNotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultReportNotificationService implements ReportNotificationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultReportNotificationService.class);

    private final AutoReportProperties properties;
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DefaultReportNotificationService(AutoReportProperties properties,
                                            JavaMailSender mailSender,
                                            RestTemplate restTemplate,
                                            ObjectMapper objectMapper) {
        this.properties = properties;
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendNotification(ReportTemplate template, ReportExecution execution, ReportData reportData) {
        if (template.getNotificationConfig() == null) {
            return;
        }

        try {
            JsonNode config = objectMapper.readTree(template.getNotificationConfig());

            // 发送邮件通知
            if (config.has("email") && properties.getNotification().getEmail().isEnabled()) {
                sendEmailNotification(template, execution, reportData, config.get("email"));
            }

            // 发送钉钉通知
            if (config.has("dingtalk") && properties.getNotification().getDingtalk().isEnabled()) {
                sendDingtalkNotification(template, execution, reportData, config.get("dingtalk"));
            }

        } catch (Exception e) {
            log.error("发送通知失败: {}", e.getMessage(), e);
        }
    }

    private void sendEmailNotification(ReportTemplate template, ReportExecution execution,
                                       ReportData reportData, JsonNode emailConfig) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(properties.getNotification().getEmail().getUsername());

            // 收件人
            if (emailConfig.has("recipients")) {
                String[] recipients = objectMapper.convertValue(emailConfig.get("recipients"), String[].class);
                message.setTo(recipients);
            }

            // 主题
            String subject = emailConfig.has("subject") ?
                    emailConfig.get("subject").asText() :
                    "报表执行完成 - " + template.getName();
            subject = subject.replace("{{date}}",
                    execution.getCompletionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            message.setSubject(subject);

            // 内容
            StringBuilder content = new StringBuilder();
            content.append("报表名称: ").append(template.getName()).append("\n");
            content.append("执行状态: ").append(execution.getStatus()).append("\n");
            content.append("执行时间: ").append(execution.getExecutionTime()).append("\n");
            content.append("完成时间: ").append(execution.getCompletionTime()).append("\n");
            content.append("数据条数: ").append(reportData.getTotalCount()).append("\n");

            if (execution.getOutputFilePath() != null) {
                content.append("输出文件: ").append(execution.getOutputFilePath()).append("\n");
            }

            message.setText(content.toString());

            mailSender.send(message);
            log.info("邮件通知发送成功: {}", template.getName());

        } catch (Exception e) {
            log.error("发送邮件通知失败: {}", e.getMessage(), e);
        }
    }

    private void sendDingtalkNotification(ReportTemplate template, ReportExecution execution,
                                          ReportData reportData, JsonNode dingtalkConfig) {
        try {
            String webhookUrl = properties.getNotification().getDingtalk().getWebhookUrl();
            if (webhookUrl == null) {
                log.warn("钉钉Webhook URL未配置");
                return;
            }

            String message = dingtalkConfig.has("message") ?
                    dingtalkConfig.get("message").asText() :
                    "报表执行完成: " + template.getName();

            Map<String, Object> payload = new HashMap<>();
            payload.put("msgtype", "text");
            Map<String, String> text = new HashMap<>();
            text.put("content", message);
            payload.put("text", text);

            restTemplate.postForObject(webhookUrl, payload, String.class);
            log.info("钉钉通知发送成功: {}", template.getName());

        } catch (Exception e) {
            log.error("发送钉钉通知失败: {}", e.getMessage(), e);
        }
    }
}