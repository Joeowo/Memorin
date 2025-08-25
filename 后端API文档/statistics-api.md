# 📊 统计分析服务 API文档

> **服务端口**: 8084  
> **API版本**: v0  
> **生成时间**: 2025年08月18日 23:43:09

## 📋 服务概述

**服务名称**: OpenAPI definition  
**服务描述**:   
**联系方式**:  <>  
**许可证**: 
## 🌐 服务器地址

- **Generated server url**: `http://localhost:8084`

## 📡 API接口列表

### 学习统计分析

#### 🟢 `GET` /api/statistics/overview/{userId}

**功能**: 获取用户学习概览

**描述**: 获取指定用户在指定时间范围内的学习统计概览数据，包括学习时长、复习次数、正确率等关键指标

**操作ID**: `getUserOverviewStatistics`

---

## 🔗 相关链接

- **Swagger UI**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs)
- **健康检查**: [http://localhost:8084/actuator/health](http://localhost:8084/actuator/health)

## 📝 使用示例

### 认证方式
`ash
# 如果需要认证，请在请求头中添加JWT Token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     http://localhost:8084/api/endpoint
`

### 通用响应格式
`json
{
  "success": true,
  "data": {},
  "message": "操作成功",
  "timestamp": "2025-01-17T10:30:00"
}
`

---

> **💡 提示**: 建议使用Swagger UI进行API测试，界面更加友好和直观。

