# 📚 Memorin智能知识复习系统 - API文档集合

> **生成时间**: 2025年08月18日 23:43:09  
> **系统版本**: v1.0.0  
> **架构**: Spring Cloud微服务架构

## 🏗️ 系统架构概览

Memorin采用现代化的微服务架构，每个服务独立部署、独立扩展：

| 服务 | 端口 | 状态 | 功能描述 | API文档 |
|------|------|------|----------|---------|| 📊 统计分析服务 | 8084 | 🟢 运行中 | 负责数据统计、学习报告、性能分析等功能 | [📖 查看文档](./statistics-api.md) |
| 👤 用户认证服务 | 8081 | 🟢 运行中 | 负责用户注册、登录、认证授权等功能 | [📖 查看文档](./user-api.md) |
| 🌐 API网关服务 | 8080 | 🟢 运行中 | 负责路由分发、负载均衡、统一入口等功能 | [📖 查看文档](./gateway-api.md) |
| 📚 知识库管理服务 | 8082 | 🟢 运行中 | 负责知识点管理、分类组织、内容存储等功能 | [📖 查看文档](./knowledge-api.md) |
| 🧠 智能复习服务 | 8083 | 🟢 运行中 | 负责SM-2算法、复习计划、学习进度等功能 | [📖 查看文档](./review-api.md) |

## 🔗 快速访问链接

### 📋 在线API文档 (Swagger UI)- **📊 统计分析服务**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- **👤 用户认证服务**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **🌐 API网关服务**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **📚 知识库管理服务**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **🧠 智能复习服务**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

### 🌐 API网关聚合
- **🎯 API聚合中心**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **🎨 统一Swagger界面**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **🏥 Nacos控制台**: [http://localhost:8848/nacos](http://localhost:8848/nacos)

## 📖 使用说明

1. **开发环境**: 所有服务运行在本地，使用H2内存数据库
2. **认证方式**: 部分接口需要JWT Token认证
3. **数据格式**: 统一使用JSON格式进行数据交换
4. **错误处理**: 遵循RESTful API设计规范

## 🛠️ 开发工具

- **API测试**: 推荐使用Postman或直接使用Swagger UI
- **文档更新**: 修改代码后API文档会自动更新
- **服务监控**: 通过Nacos控制台监控服务状态

---

> **💡 提示**: 点击上方表格中的"查看文档"链接可以查看各服务的详细API文档。

