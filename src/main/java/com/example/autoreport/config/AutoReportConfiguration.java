package com.example.autoreport.config;

import com.example.autoreport.service.ReportFileService;
import com.example.autoreport.service.ReportNotificationService;
import com.example.autoreport.service.ReportVisualizationService;
import com.example.autoreport.service.impl.DefaultReportFileService;
import com.example.autoreport.service.impl.DefaultReportNotificationService;
import com.example.autoreport.service.impl.DefaultReportVisualizationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.autoreport.repository")
@EntityScan(basePackages = "com.example.autoreport.entity")
@EnableConfigurationProperties({AutoReportProperties.class})
public class AutoReportConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("report-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        return scheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ReportVisualizationService reportVisualizationService() {
        return new DefaultReportVisualizationService();
    }

    @Bean
    public ReportNotificationService reportNotificationService(AutoReportProperties properties) {
        return new DefaultReportNotificationService(properties, null, restTemplate(), null);
    }

    @Bean
    public ReportFileService reportFileService(AutoReportProperties properties) {
        return new DefaultReportFileService(properties, null);
    }
}