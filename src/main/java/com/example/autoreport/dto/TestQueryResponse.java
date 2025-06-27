package com.example.autoreport.dto;

public class TestQueryResponse {
    private boolean success;
    private String generatedSql;
    private String errorMessage;

    public TestQueryResponse() {}

    public TestQueryResponse(boolean success, String generatedSql, String errorMessage) {
        this.success = success;
        this.generatedSql = generatedSql;
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getGeneratedSql() { return generatedSql; }
    public void setGeneratedSql(String generatedSql) { this.generatedSql = generatedSql; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}