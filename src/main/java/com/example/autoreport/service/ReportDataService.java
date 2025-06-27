package com.example.autoreport.service;

import com.alibaba.cloud.ai.dbconnector.DbAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

@Service
public class ReportDataService {

    private static final Logger log = LoggerFactory.getLogger(ReportDataService.class);

    private final DbAccessor dbAccessor;

    public ReportDataService(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

    /**
     * 执行SQL查询并返回结果
     */
    public List<Map<String, Object>> executeQuery(String sql) throws Exception {
        log.info("执行SQL查询: {}", sql);

        List<Map<String, Object>> results = new ArrayList<>();

        try {
            ResultSet resultSet = dbAccessor.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            log.info("查询完成，返回 {} 条记录", results.size());
            return results;

        } catch (Exception e) {
            log.error("执行SQL查询失败: {}", e.getMessage());
            throw new RuntimeException("查询执行失败: " + e.getMessage(), e);
        }
    }
}