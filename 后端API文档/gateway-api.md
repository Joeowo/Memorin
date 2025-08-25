# 🌐 API网关服务 API文档

- `/api/auth/**` 
- `/api/user/**` 
- `/api/knowledge/**` 
- `/api/review/**` 
- `/api/statistics/**` 
**联系方式**: Memorinå¼åå¢é <team@memorin.com>  
**许可证**: MIT License
## 🌐 服务器地址

- **å¼åç¯å¢ - APIç½å³**: `http://localhost:8080`
- **çäº§ç¯å¢ - APIç½å³**: `https://api.memorin.com`

## 📡 API接口列表

### ç½å³ç®¡ç

#### 🟢 `GET` /gateway/routes

**操作ID**: `getRoutes`

---

#### 🟢 `GET` /gateway/info

**操作ID**: `getGatewayInfo`

---

#### 🟢 `GET` /gateway/health

**操作ID**: `health`

---

### API

#### 🟢 `GET` /api-docs


**操作ID**: `getApiDocsList`

---

#### 🟢 `GET` /api-docs/{service}

**操作ID**: `getServiceApiDocs`

---

#### 🟢 `GET` /api-docs/test

**操作ID**: `test`

---

#### 🟢 `GET` /api-docs/overview

**操作ID**: `getApiOverview`

---

#### 🟢 `GET` /api-docs/health

**操作ID**: `getServicesHealth`

---

## 🔗 相关链接

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **健康检查**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## 📝 使用示例

### 认证方式
`ash
# 如果需要认证，请在请求头中添加JWT Token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     http://localhost:8080/api/endpoint
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