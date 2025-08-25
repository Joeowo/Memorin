# 👤 用户认证服务 API文档

> **服务端口**: 8081  
> **API版本**: 1.0.0  
> **生成时间**: 2025年08月18日 23:43:09

## 📋 服务概述

**服务名称**: Memorin用户服务 API  
**服务描述**: Memorin智能知识复习系统 - 用户服务

🔐 **核心功能**: 用户认证、授权和用户管理
🎯 **主要特性**: JWT身份验证、角色权限管理、用户信息维护
📊 **API分类**: 认证接口、用户管理接口、权限控制接口

### 🔑 认证流程
1. **注册**: 创建新用户账户
2. **登录**: 验证凭据并获取JWT令牌
3. **令牌验证**: 使用JWT访问受保护资源
4. **刷新令牌**: 延长会话有效期

### 👤 用户管理
- 用户信息查询和更新
- 密码修改和重置
- 用户状态管理  
**联系方式**: Memorin用户服务团队 <user-service@memorin.com>  
**许可证**: MIT License
## 🌐 服务器地址

- **开发环境 - 用户服务**: `http://localhost:8081`
- **生产环境 - 用户服务**: `https://api.memorin.com/user`

## 📡 API接口列表

### 系统测试

#### 🔵 `POST` /test/user/create

**功能**: 

**操作ID**: `createUser`

---

#### 🔵 `POST` /test/password/verify

**功能**: 

**操作ID**: `testPasswordVerification`

---

#### 🔵 `POST` /test/echo

**功能**: 

**操作ID**: `echo`

---

#### 🟢 `GET` /test/users

**功能**: 

**操作ID**: `getAllUsers`

---

#### 🟢 `GET` /test/user/{userId}

**功能**: 

**操作ID**: `getUser`

---

#### 🟢 `GET` /test/user/username/{username}

**功能**: 

**操作ID**: `getUserByUsername`

---

#### 🟢 `GET` /test/user/id/{id}

**功能**: 

**操作ID**: `getUserById`

---

#### 🟢 `GET` /test/search

**功能**: 

**操作ID**: `search`

---

#### 🟢 `GET` /test/ping

**功能**: 健康检查

**描述**: 检查用户服务是否正常运行，返回服务状态和基本信息

**操作ID**: `ping`

---

#### 🟢 `GET` /test/info

**功能**: 

**操作ID**: `info`

---

#### 🟢 `GET` /test/database/test

**功能**: 

**操作ID**: `testDatabase`

---

### 用户认证

#### 🔵 `POST` /auth/verify

**功能**: 验证令牌

**操作ID**: `verifyToken`

---

#### 🔵 `POST` /auth/register

**功能**: 用户注册

**操作ID**: `register`

---

#### 🔵 `POST` /auth/login

**功能**: 用户登录

**操作ID**: `login`

---

## 📊 数据模型

### RegisterRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `username` | `string` |  |
| `email` | `string` |  |
| `password` | `string` |  |
| `nickname` | `string` |  |
| `phone` | `string` |  |

### LoginRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `username` | `string` |  |
| `password` | `string` |  |

### LoginResponse

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `success` | `boolean` |  |
| `message` | `string` |  |
| `token` | `string` |  |
| `user` | `object` |  |
| `timestamp` | `string` |  |

### UserInfo

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `id` | `integer` |  |
| `username` | `string` |  |
| `email` | `string` |  |
| `nickname` | `string` |  |
| `avatar` | `string` |  |
| `status` | `string` |  |
| `createdAt` | `string` |  |

## 🔗 相关链接

- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
- **健康检查**: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)

## 📝 使用示例

### 认证方式
`ash
# 如果需要认证，请在请求头中添加JWT Token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     http://localhost:8081/api/endpoint
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

