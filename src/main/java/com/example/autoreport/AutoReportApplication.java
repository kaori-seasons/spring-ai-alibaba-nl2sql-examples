package com.example.autoreport;

import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {ChatClientAutoConfiguration.class})
@ComponentScan(basePackages = {"com.example.autoreport", "com.alibaba.cloud.ai"})
@EnableAsync
@EnableScheduling
public class AutoReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoReportApplication.class, args);
    }
}
