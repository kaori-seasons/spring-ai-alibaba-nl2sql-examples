package com.example.autoreport.service.impl;

import com.example.autoreport.model.ChartConfig;
import com.example.autoreport.model.ReportData;
import com.example.autoreport.service.ReportVisualizationService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class DefaultReportVisualizationService implements ReportVisualizationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultReportVisualizationService.class);

    @Override
    public byte[] generateChart(ReportData reportData, ChartConfig.Chart chartConfig) {
        try {
            JFreeChart chart = createChart(reportData, chartConfig);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("生成图表失败: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private JFreeChart createChart(ReportData reportData, ChartConfig.Chart chartConfig) {
        String chartType = chartConfig.getType().toLowerCase();

        switch (chartType) {
            case "bar":
                return createBarChart(reportData, chartConfig);
            case "line":
                return createLineChart(reportData, chartConfig);
            case "pie":
                return createPieChart(reportData, chartConfig);
            default:
                return createBarChart(reportData, chartConfig);
        }
    }

    private JFreeChart createBarChart(ReportData reportData, ChartConfig.Chart chartConfig) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> row : reportData.getData()) {
            String category = String.valueOf(row.get(chartConfig.getXField()));
            Number value = (Number) row.get(chartConfig.getYField());
            dataset.addValue(value, "数据", category);
        }

        return ChartFactory.createBarChart(
                chartConfig.getTitle(),
                chartConfig.getXField(),
                chartConfig.getYField(),
                dataset
        );
    }

    private JFreeChart createLineChart(ReportData reportData, ChartConfig.Chart chartConfig) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> row : reportData.getData()) {
            String category = String.valueOf(row.get(chartConfig.getXField()));
            Number value = (Number) row.get(chartConfig.getYField());
            dataset.addValue(value, "数据", category);
        }

        return ChartFactory.createLineChart(
                chartConfig.getTitle(),
                chartConfig.getXField(),
                chartConfig.getYField(),
                dataset
        );
    }

    private JFreeChart createPieChart(ReportData reportData, ChartConfig.Chart chartConfig) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map<String, Object> row : reportData.getData()) {
            String key = String.valueOf(row.get(chartConfig.getXField()));
            Number value = (Number) row.get(chartConfig.getYField());
            dataset.setValue(key, value);
        }

        return ChartFactory.createPieChart(
                chartConfig.getTitle(),
                dataset,
                true,
                true,
                false
        );
    }
}
