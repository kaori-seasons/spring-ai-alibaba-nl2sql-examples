package com.example.autoreport.service.impl;

import com.example.autoreport.config.AutoReportProperties;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.model.ChartConfig;
import com.example.autoreport.model.ReportData;
import com.example.autoreport.service.ReportFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DefaultReportFileService implements ReportFileService {

    private static final Logger log = LoggerFactory.getLogger(DefaultReportFileService.class);

    private final AutoReportProperties properties;
    private final ObjectMapper objectMapper;

    public DefaultReportFileService(AutoReportProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateReportFile(ReportTemplate template, ReportData reportData, ChartConfig chartConfig) throws Exception {
        String outputFormat = template.getOutputFormat().toUpperCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("%s_%s.%s",
                template.getName().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_"),
                timestamp,
                outputFormat.toLowerCase());

        Path outputPath = Paths.get(properties.getFile().getStoragePath(), fileName);

        // 确保目录存在
        Files.createDirectories(outputPath.getParent());

        switch (outputFormat) {
            case "EXCEL":
                generateExcelFile(outputPath, reportData, template);
                break;
            case "JSON":
                generateJsonFile(outputPath, reportData);
                break;
            case "HTML":
                generateHtmlFile(outputPath, reportData, template, chartConfig);
                break;
            default:
                throw new IllegalArgumentException("不支持的输出格式: " + outputFormat);
        }

        log.info("报表文件生成成功: {}", outputPath.toString());
        return outputPath.toString();
    }

    private void generateExcelFile(Path outputPath, ReportData reportData, ReportTemplate template) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(template.getName());

            List<Map<String, Object>> data = reportData.getData();
            if (data.isEmpty()) {
                return;
            }

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            List<String> columns = reportData.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));

                // 设置标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据行
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = data.get(rowIndex);

                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                    Cell cell = dataRow.createCell(colIndex);
                    Object value = rowData.get(columns.get(colIndex));

                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fileOut);
            }
        }
    }

    private void generateJsonFile(Path outputPath, ReportData reportData) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputPath.toFile(), reportData);
    }

    private void generateHtmlFile(Path outputPath, ReportData reportData, ReportTemplate template, ChartConfig chartConfig) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html>\n<head>\n")
                .append("<meta charset='UTF-8'>\n")
                .append("<title>").append(template.getName()).append("</title>\n")
                .append("<style>\n")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("table { border-collapse: collapse; width: 100%; }\n")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                .append("th { background-color: #f2f2f2; }\n")
                .append("</style>\n")
                .append("</head>\n<body>\n")
                .append("<h1>").append(template.getName()).append("</h1>\n");

        if (template.getDescription() != null) {
            html.append("<p>").append(template.getDescription()).append("</p>\n");
        }

        // 生成表格
        html.append("<table>\n<thead>\n<tr>\n");
        for (String column : reportData.getColumns()) {
            html.append("<th>").append(column).append("</th>\n");
        }
        html.append("</tr>\n</thead>\n<tbody>\n");

        for (Map<String, Object> row : reportData.getData()) {
            html.append("<tr>\n");
            for (String column : reportData.getColumns()) {
                Object value = row.get(column);
                html.append("<td>").append(value != null ? value.toString() : "").append("</td>\n");
            }
            html.append("</tr>\n");
        }

        html.append("</tbody>\n</table>\n")
                .append("<p>生成时间: ").append(LocalDateTime.now()).append("</p>\n")
                .append("</body>\n</html>");

        Files.write(outputPath, html.toString().getBytes("UTF-8"));
    }
}
