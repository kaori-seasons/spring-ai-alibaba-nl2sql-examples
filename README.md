## 基于Spring AI Alibaba NL2SQL的自动化报表系统设计 

基于spring-ai-alibaba的nl2sql模块，设计一个完整的自动化报表系统。

### 系统架构 

系统采用分层架构，核心基于StateGraph工作流引擎。 NL2SQL模块通过多个节点处理自然语言查询：查询重写、关键词提取、Schema召回、表关系分析、SQL生成、SQL验证和语义一致性检查。

### 核心实现要点 

**1. 图表可视化支持**  
系统支持生成PlantUML和Mermaid格式的工作流图表。 您可以通过`getGraph()`方法获取流程图的markdown代码。 

**2. 向量存储配置**  
支持AnalyticDB（生产环境）和SimpleVector（开发测试）两种存储方式。

**3. 数据库连接**  
支持MySQL和PostgreSQL等主流数据库。

### 实际应用示例  

在Spring Ai Alibaba 的BigToolController中可以看到StateGraph的实际使用：系统会自动生成Mermaid格式的工作流图并输出到控制台。

### 图表生成功能  

系统内置了图表表示测试，展示了如何生成不同类型的流程图： 包括并行分支、条件路由等复杂场景的可视化。

这个设计充分利用了Spring AI Alibaba的Graph工作流引擎和NL2SQL能力，为企业提供了一个智能化的报表自动化解决方案，同时支持完整的流程图可视化功能。  
