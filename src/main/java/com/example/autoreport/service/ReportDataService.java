package com.example.autoreport.service;

import com.alibaba.cloud.ai.dbconnector.DbAccessor;
import com.alibaba.cloud.ai.dbconnector.DbConfig;
import com.alibaba.cloud.ai.dbconnector.bo.DbQueryParameter;
import com.alibaba.cloud.ai.dbconnector.bo.ResultSetBO;
import com.alibaba.cloud.ai.service.Nl2SqlService;
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

    private final DbConfig dbConfig;
    public ReportDataService(DbAccessor dbAccessor, DbConfig dbConfig) {
        this.dbAccessor = dbAccessor;
        this.dbConfig = dbConfig;
    }

    /**
     * 执行SQL查询并返回结果
     */
    public List<Map<String, Object>> executeQuery(String sql) throws Exception {
        log.info("执行SQL查询: {}", sql);

        List<Map<String, Object>> results = new ArrayList<>();

        try {
            DbQueryParameter param = DbQueryParameter.from(dbConfig).setSql(sql);
            ResultSetBO resultSet = dbAccessor.executeSqlAndReturnObject(dbConfig, param);
            results = convertResultSetToList(resultSet);

            log.info("查询完成，返回 {} 条记录", results.size());
            return results;

        } catch (Exception e) {
            log.error("执行SQL查询失败: {}", e.getMessage());
            throw new RuntimeException("查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 ResultSetBO 转换为 List<Map<String, Object>>
     */
    private List<Map<String, Object>> convertResultSetToList(ResultSetBO resultSet) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (resultSet == null || resultSet.getData() == null) {
            return results;
        }

        // 直接使用 ResultSetBO 中的 data 字段
        List<Map<String, String>> dataRows = resultSet.getData();

        for (Map<String, String> row : dataRows) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            // 将 String 值转换为适当的 Object 类型
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // 尝试转换为适当的数据类型
                Object convertedValue = convertStringToObject(value);
                rowMap.put(key, convertedValue);
            }
            results.add(rowMap);
        }

        return results;
    }

    /**
     * 将字符串值转换为适当的对象类型
     */
    private Object convertStringToObject(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // 尝试转换为数字
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // 如果不是数字，返回原字符串
            return value;
        }
    }
}