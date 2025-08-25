# Memorin 全模块集成测试执行指南

## 测试概述

**测试目标**: 验证user、knowledge、review三个模块的完整集成工作流  
**测试方式**: 分为API直接调用测试和TypeScript SDK测试  
**测试环境**: 开发环境（H2内存数据库）  
**服务端口**: 
- 用户服务: 8081
- 知识服务: 8082  
- 复习服务: 8083

## 前置条件

### 1. 服务启动检查
确保所有服务都已启动：

```bash
# 检查用户服务
curl http://localhost:8081/actuator/health

# 检查知识服务  
curl http://localhost:8082/actuator/health

# 检查复习服务
curl http://localhost:8083/actuator/health
```

### 2. 依赖安装

```bash
# 安装Node.js测试依赖（用于SDK测试）
npm install axios typescript ts-node

# 或者使用系统curl和jq（用于API测试）
# Windows: 需要安装Git Bash或使用WSL
```

## 测试执行步骤

### 方法1: 使用Bash脚本（API测试）

1. **运行集成测试脚本**:
```bash
cd D:\CODE\Memorin-rebuild\docs\tests
./run-integration-tests.sh
```

2. **手动执行关键测试**:
```bash
# 测试用户注册
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user_001",
    "email": "test@example.com",
    "password": "TestPass123"
  }'

# 创建学习分类
curl -X POST http://localhost:8082/api/test/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "JavaScript基础",
    "description": "JavaScript核心概念",
    "color": "#f7df1e",
    "icon": "📜"
  }'

# 创建复习会话
curl -X POST http://localhost:8083/api/review/sessions \
  -H "Content-Type: application/json" \
  -H "User-Id: test_user_001" \
  -d '{
    "reviewMode": "SM2",
    "targetQuestionCount": 5,
    "maxDurationMinutes": 30,
    "notes": "集成测试会话"
  }'
```

### 方法2: 使用TypeScript SDK测试

1. **安装依赖**:
```bash
cd D:\CODE\Memorin-rebuild\docs\tests
npm install axios typescript ts-node
```

2. **运行SDK测试**:
```bash
npx ts-node integration-test-runner.ts
```

3. **观察输出**:
- ✅ 服务连接检查
- 👤 用户注册和认证
- 📚 创建学习体系
- 🎯 创建复习会话
- 📖 模拟学习过程
- 🔍 错题分析与复习
- 📈 学习进度分析

## 测试验证点

### 1. 服务健康状态
- [ ] 所有服务成功启动
- [ ] 数据库连接正常（H2内存数据库）
- [ ] API端点可访问
- [ ] 健康检查通过

### 2. 用户模块验证
- [ ] 用户注册功能
- [ ] 用户登录认证
- [ ] 用户信息获取
- [ ] Token验证

### 3. 知识模块验证
- [ ] 分类创建和管理
- [ ] 知识点创建（文本/选择/代码类型）
- [ ] 标签和难度设置
- [ ] 知识点查询和搜索

### 4. 复习模块验证
- [ ] 复习会话创建
- [ ] 会话状态管理（启动/暂停/完成）
- [ ] 答案提交和评分
- [ ] 错题记录和追踪
- [ ] SM-2算法计算
- [ ] 统计和报告生成

### 5. 数据一致性验证
- [ ] 用户ID在所有模块中一致
- [ ] 知识点引用关系正确
- [ ] 错题与知识点关联正确
- [ ] 会话与答题记录完整

## 预期测试结果

### 成功指标
- ✅ 用户注册/登录流程完整
- ✅ 分类创建成功
- ✅ 知识点创建成功
- ✅ 复习会话创建成功
- ✅ 答题记录正确
- ✅ 错题记录正确
- ✅ 进度统计准确
- ✅ 个性化推荐有效

### 性能指标
- 用户注册: < 500ms
- 知识点创建: < 300ms
- 复习会话创建: < 400ms
- 答题提交: < 200ms
- 统计查询: < 150ms

## 测试报告生成

### 1. 运行测试后生成报告
```bash
# 执行测试并保存结果
./run-integration-tests.sh > test-results.log 2>&1

# 或者
npx ts-node integration-test-runner.ts > sdk-test-results.log 2>&1
```

### 2. 验证测试结果
检查日志文件中的：
- 测试通过率统计
- 失败测试详情
- 性能指标
- 错误信息

### 3. 数据库验证
访问H2控制台验证数据：
- 用户服务: http://localhost:8081/h2-console
- 知识服务: http://localhost:8082/h2-console
- 复习服务: http://localhost:8083/h2-console

## 故障排查

### 常见问题和解决方案

1. **服务连接失败**:
   - 检查端口是否被占用
   - 确认服务是否启动
   - 检查防火墙设置

2. **测试失败**:
   - 查看具体错误信息
   - 检查数据格式
   - 验证依赖服务状态

3. **性能问题**:
   - 检查内存使用
   - 查看日志中的响应时间
   - 确认数据库连接池配置

### 调试工具

1. **日志查看**:
```bash
# 查看服务日志
tail -f ../backend/*/logs/*.log
```

2. **API测试工具**:
- 使用Postman测试API
- 使用curl进行快速验证
- 查看Swagger文档: http://localhost:808*/swagger-ui.html

3. **数据库检查**:
```bash
# 使用H2控制台查看数据
# JDBC URL: jdbc:h2:mem:memorin_knowledge
# 用户名: memorin
# 密码: memorin_knowledge_2025
```

## 测试结论

成功完成所有测试后，系统应该：

1. **功能完整性**: 所有核心功能正常工作
2. **数据一致性**: 跨模块数据保持一致
3. **用户体验**: 学习流程顺畅
4. **性能达标**: 响应时间在预期范围内
5. **错误处理**: 异常情况处理得当

**最终验证**: 运行测试脚本后，如果所有测试通过，系统可以投入开发使用。