package com.example.autoreport.dto;

import jakarta.validation.constraints.NotBlank;

public class TestQueryRequest {
    @NotBlank(message = "查询语句不能为空")
    private String query;

    public TestQueryRequest() {}

    public TestQueryRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
