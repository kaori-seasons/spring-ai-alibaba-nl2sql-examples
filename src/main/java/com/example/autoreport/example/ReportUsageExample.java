package com.example.autoreport.example;

@Component
public class ReportUsageExample {

    @Autowired
    private AutoReportService autoReportService;

    public void createSalesReport() {
        CreateReportTemplateRequest request = new CreateReportTemplateRequest();
        request.setName("每日销售报表");
        request.setDescription("统计每日销售数据，包括销售额、订单量等");
        request.setNaturalQuery("查询昨天的销售总额、订单数量和热销商品前10名");
        request.setCronExpression("0 0 9 * * ?"); // 每天上午9点执行
        request.setOutputFormat("EXCEL");
        request.setChartConfig("""
            {
                "charts": [
                    {
                        "type": "line",
                        "title": "销售趋势",
                        "xField": "date",
                        "yField": "sales_amount"
                    },
                    {
                        "type": "bar",
                        "title": "热销商品",
                        "xField": "product_name",
                        "yField": "quantity"
                    }
                ]
            }
            """);
        request.setNotificationConfig("""
            {
                "email": {
                    "recipients": ["manager@example.com", "sales@example.com"],
                    "subject": "每日销售报表 - {{date}}"
                },
                "dingtalk": {
                    "enabled": true,
                    "message": "📊 每日销售报表已生成，请查收！"
                }
            }
            """);

        ReportTemplate template = autoReportService.createTemplate(request);
        System.out.println("报表模板创建成功: " + template.getId());
    }
}
