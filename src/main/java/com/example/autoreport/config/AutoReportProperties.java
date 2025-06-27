package com.example.autoreport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auto-report")
public class AutoReportProperties {

    private File file = new File();
    private Notification notification = new Notification();
    private Execution execution = new Execution();

    // Getters and Setters
    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }

    public Execution getExecution() { return execution; }
    public void setExecution(Execution execution) { this.execution = execution; }

    public static class File {
        private String storagePath = "/data/reports";
        private String maxFileSize = "100MB";
        private List<String> allowedFormats = List.of("EXCEL", "PDF", "HTML", "JSON");

        // Getters and Setters
        public String getStoragePath() { return storagePath; }
        public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

        public String getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(String maxFileSize) { this.maxFileSize = maxFileSize; }

        public List<String> getAllowedFormats() { return allowedFormats; }
        public void setAllowedFormats(List<String> allowedFormats) { this.allowedFormats = allowedFormats; }
    }

    public static class Notification {
        private Email email = new Email();
        private Dingtalk dingtalk = new Dingtalk();
        private Wechat wechat = new Wechat();

        // Getters and Setters
        public Email getEmail() { return email; }
        public void setEmail(Email email) { this.email = email; }

        public Dingtalk getDingtalk() { return dingtalk; }
        public void setDingtalk(Dingtalk dingtalk) { this.dingtalk = dingtalk; }

        public Wechat getWechat() { return wechat; }
        public void setWechat(Wechat wechat) { this.wechat = wechat; }

        public static class Email {
            private boolean enabled = false;
            private String smtpHost;
            private int smtpPort = 587;
            private String username;
            private String password;

            // Getters and Setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }

            public String getSmtpHost() { return smtpHost; }
            public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

            public int getSmtpPort() { return smtpPort; }
            public void setSmtpPort(int smtpPort) { this.smtpPort = smtpPort; }

            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }

            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }

        public static class Dingtalk {
            private boolean enabled = false;
            private String webhookUrl;

            // Getters and Setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }

            public String getWebhookUrl() { return webhookUrl; }
            public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
        }

        public static class Wechat {
            private boolean enabled = false;
            private String corpId;
            private String corpSecret;

            // Getters and Setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }

            public String getCorpId() { return corpId; }
            public void setCorpId(String corpId) { this.corpId = corpId; }

            public String getCorpSecret() { return corpSecret; }
            public void setCorpSecret(String corpSecret) { this.corpSecret = corpSecret; }
        }
    }

    public static class Execution {
        private int maxConcurrentReports = 5;
        private int timeoutMinutes = 30;
        private int retryCount = 2;

        // Getters and Setters
        public int getMaxConcurrentReports() { return maxConcurrentReports; }
        public void setMaxConcurrentReports(int maxConcurrentReports) { this.maxConcurrentReports = maxConcurrentReports; }

        public int getTimeoutMinutes() { return timeoutMinutes; }
        public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }

        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    }
}
