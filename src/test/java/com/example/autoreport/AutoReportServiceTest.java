package com.example.autoreport;

import com.example.autoreport.dto.CreateReportTemplateRequest;
import com.example.autoreport.entity.ReportTemplate;
import com.example.autoreport.service.AutoReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AutoReportServiceTest {

    @Autowired
    private AutoReportService autoReportService;

    @Test
    public void testCreateReportTemplate() {
        CreateReportTemplateRequest request = new CreateReportTemplateRequest();
        request.setName("测试报表");
        request.setDescription("这是一个测试报表");
        request.setNaturalQuery("查询今天的销售数据");
        request.setOutputFormat("JSON");

        ReportTemplate template = autoReportService.createTemplate(request);

        assertNotNull(template.getId());
        assertEquals("测试报表", template.getName());
        assertEquals("JSON", template.getOutputFormat());
    }
}
