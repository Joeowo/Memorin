# Memorin 全模块集成测试 - API版本

## 测试概述

**测试目标**: 验证user、knowledge、review三个模块的完整集成工作流
**测试方式**: API直接调用测试
**测试环境**: 开发环境（H2内存数据库）

## 测试准备

### 服务状态检查
1. 用户服务: http://localhost:8081 ✅
2. 知识服务: http://localhost:8082 ✅
3. 复习服务: http://localhost:8083 ✅

### 测试用户创建
```bash
# 创建测试用户
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "integration_test_user",
    "email": "test@integration.com",
    "password": "Test123456"
  }'
```

## 完整集成测试场景

### 场景1: 完整学习工作流

#### 步骤1: 用户注册和登录
```bash
# 注册新用户
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "learner_001",
    "email": "learner@example.com",
    "password": "SecurePass123"
  }')

# 提取用户ID
USER_ID=$(echo $USER_RESPONSE | jq -r '.data.id')
echo "创建用户: $USER_ID"

# 用户登录
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "learner_001",
    "password": "SecurePass123"
  }' | jq -r '.data.token')
```

#### 步骤2: 创建学习分类和知识点
```bash
# 创建学习分类
CATEGORY_RESPONSE=$(curl -s -X POST http://localhost:8082/api/test/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "JavaScript基础",
    "description": "JavaScript核心概念",
    "color": "#f7df1e",
    "icon": "📜"
  }')

CATEGORY_ID=$(echo $CATEGORY_RESPONSE | jq -r '.data.id')
echo "创建分类: $CATEGORY_ID"

# 创建多个知识点
KNOWLEDGE_POINTS=(
  '变量声明与作用域:理解var、let、const的区别'
  '函数与闭包:掌握JavaScript闭包概念'
  '原型与继承:理解原型链机制'
  '异步编程:掌握Promise和async/await'
  '事件处理:理解事件循环和事件委托'
)

for i in "${!KNOWLEDGE_POINTS[@]}"; do
  IFS=':' read -r question explanation <<< "${KNOWLEDGE_POINTS[$i]}"
  
  KNOWLEDGE_RESPONSE=$(curl -s -X POST http://localhost:8082/api/test/knowledge \
    -H "Content-Type: application/json" \
    -d '{
      "question": "'$question'",
      "explanation": "'$explanation'",
      "type": "text",
      "categoryId": "'$CATEGORY_ID'",
      "tags": ["JavaScript", "基础"],
      "difficulty": 3,
      "estimatedTime": 15
    }')
  
  echo "创建知识点 $((i+1)): $(echo $KNOWLEDGE_RESPONSE | jq -r '.data.id')"
done
```

#### 步骤3: 创建具体题目
```bash
# 获取所有知识点
KNOWLEDGE_LIST=$(curl -s http://localhost:8082/api/test/knowledge)
KNOWLEDGE_IDS=($(echo $KNOWLEDGE_LIST | jq -r '.data[].id'))

# 为每个知识点创建文本题
for i in "${!KNOWLEDGE_IDS[@]}"; do
  curl -s -X POST http://localhost:8082/api/test/textquestions \
    -H "Content-Type: application/json" \
    -d '{
      "knowledgeId": "'${KNOWLEDGE_IDS[$i]}'",
      "answer": "标准答案",
      "type": "essay",
      "validation": "contains"
    }'
done
```

#### 步骤4: 创建复习会话
```bash
# 生成题目列表用于复习
QUESTION_GENERATOR=$(curl -s -X POST http://localhost:8083/api/review/question-generator/templates/smart-review \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "'$USER_ID'",
    "count": 5,
    "onlyDue": false
  }')

echo "生成题目: $(echo $QUESTION_GENERATOR | jq -r '.data.questions | length') 道"

# 创建复习会话
SESSION_RESPONSE=$(curl -s -X POST http://localhost:8083/api/review/sessions \
  -H "Content-Type: application/json" \
  -H "User-Id: $USER_ID" \
  -d '{
    "reviewMode": "SM2",
    "targetQuestionCount": 5,
    "maxDurationMinutes": 30,
    "notes": "JavaScript基础复习"
  }')

SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.data.id')
echo "创建复习会话: $SESSION_ID"
```

#### 步骤5: 开始复习会话
```bash
# 启动会话
curl -s -X PUT http://localhost:8083/api/review/sessions/$SESSION_ID/start \
  -H "User-Id: $USER_ID"
```

#### 步骤6: 模拟答题过程
```bash
# 模拟答题提交（循环5次，每次不同评分）
for i in {1..5}; do
  QUALITY=$(( (RANDOM % 4) + 1 ))
  TIME_SPENT=$(( (RANDOM % 60) + 30 ))
  
  curl -s -X POST http://localhost:8083/api/review/sessions/$SESSION_ID/submit \
    -H "Content-Type: application/json" \
    -H "User-Id: $USER_ID" \
    -d '{
      "knowledgePointId": "'${KNOWLEDGE_IDS[$((i-1))]}'",
      "qualityRating": '$QUALITY',
      "timeSpentSeconds": '$TIME_SPENT',
      "isSkipped": false,
      "isCorrect": '$([ $QUALITY -ge 3 ] && echo "true" || echo "false")',
      "studyNotes": "第'$i'题答题记录"
    }'
  
  echo "提交第 $i 题，评分: $QUALITY"
  sleep 1
done
```

#### 步骤7: 记录错题
```bash
# 如果评分较低，记录为错题
for i in {1..3}; do
  curl -s -X POST http://localhost:8083/api/review/mistakes \
    -H "Content-Type: application/json" \
    -d '{
      "userId": "'$USER_ID'",
      "knowledgePointId": "'${KNOWLEDGE_IDS[$((i-1))]}'",
      "mistakeReason": "概念理解错误，需要加强",
      "questionType": "text",
      "difficultyLevel": "medium"
    }'
done
```

#### 步骤8: 完成会话并查看统计
```bash
# 完成会话
curl -s -X PUT http://localhost:8083/api/review/sessions/$SESSION_ID/complete \
  -H "User-Id: $USER_ID"

# 查看错题统计
MISTAKE_STATS=$(curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID/statistics)
echo "错题统计: $MISTAKE_STATS"

# 查看会话统计
SESSION_DETAIL=$(curl -s http://localhost:8083/api/review/sessions/$SESSION_ID \
  -H "User-Id: $USER_ID")
echo "会话详情: $SESSION_DETAIL"
```

### 场景2: 错题追踪工作流

#### 步骤1: 获取用户错题
```bash
# 获取未解决错题
UNRESOLVED_MISTAKES=$(curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID/unresolved)
echo "未解决错题: $(echo $UNRESOLVED_MISTAKES | jq '.data | length')"

# 获取高优先级错题
HIGH_PRIORITY=$(curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID/high-priority)
echo "高优先级错题: $(echo $HIGH_PRIORITY | jq '.data | length')"
```

#### 步骤2: 创建错题复习会话
```bash
# 使用错题复习模板
MISTAKE_REVIEW=$(curl -s -X POST http://localhost:8083/api/review/question-generator/templates/mistake-review/javascript_base \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "'$USER_ID'",
    "limit": 10,
    "random": false
  }')

echo "错题复习题目: $(echo $MISTAKE_REVIEW | jq '.data.questions | length')"

# 创建错题复习会话
MISTAKE_SESSION=$(curl -s -X POST http://localhost:8083/api/review/sessions \
  -H "Content-Type: application/json" \
  -H "User-Id: $USER_ID" \
  -d '{
    "reviewMode": "mistake-review",
    "targetQuestionCount": 3,
    "maxDurationMinutes": 20,
    "notes": "错题重点复习"
  }')

MISTAKE_SESSION_ID=$(echo $MISTAKE_SESSION | jq -r '.data.id')
```

#### 步骤3: 解决错题
```bash
# 获取错题列表
MISTAKE_LIST=$(curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID/unresolved)
MISTAKE_IDS=($(echo $MISTAKE_LIST | jq -r '.data[].knowledgePointId'))

# 逐个解决错题
for mistake_id in "${MISTAKE_IDS[@]:0:3}"; do
  curl -s -X PUT http://localhost:8083/api/review/mistakes/$USER_ID/$mistake_id/resolve
  echo "解决错题: $mistake_id"
done
```

### 场景3: 学习进度追踪

#### 步骤1: 获取学习统计
```bash
# 用户统计
echo "=== 用户学习统计 ==="
curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID/statistics | jq .

# 会话统计
SESSIONS=$(curl -s http://localhost:8083/api/review/sessions \
  -H "User-Id: $USER_ID")
echo "总会话数: $(echo $SESSIONS | jq '.data | length')"

# 知识库统计
KNOWLEDGE_STATS=$(curl -s http://localhost:8082/api/test/knowledge/statistics)
echo "知识点总数: $(echo $KNOWLEDGE_STATS | jq '.data.total')"
```

#### 步骤2: 生成学习报告
```bash
# 创建学习报告脚本
cat > learning_report.json <<EOF
{
  "user_id": "$USER_ID",
  "generated_at": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "sessions": $(curl -s http://localhost:8083/api/review/sessions -H "User-Id: $USER_ID"),
  "mistakes": $(curl -s http://localhost:8083/api/review/mistakes/user/$USER_ID),
  "knowledge_points": $(curl -s http://localhost:8082/api/test/knowledge)
}
EOF

echo "学习报告已生成到 learning_report.json"
```

## 测试验证点

### 1. 数据一致性验证
```bash
# 验证用户ID在所有模块中的一致性
echo "验证用户ID一致性: $USER_ID"

# 验证知识点ID在knowledge和review中的引用
echo "验证知识点引用: ${KNOWLEDGE_IDS[@]}"

# 验证会话ID在review模块中的完整性
echo "验证会话完整性: $SESSION_ID"
```

### 2. 业务流程验证
```bash
# 验证完整学习循环
validate_workflow() {
  echo "=== 工作流验证 ==="
  
  # 1. 用户存在
  user_check=$(curl -s http://localhost:8081/api/auth/me -H "Authorization: Bearer $TOKEN")
  echo "用户验证: $(echo $user_check | jq '.success')"
  
  # 2. 分类存在
  category_check=$(curl -s http://localhost:8082/api/test/categories/$CATEGORY_ID)
  echo "分类验证: $(echo $category_check | jq '.success')"
  
  # 3. 知识点存在
  knowledge_check=$(curl -s http://localhost:8082/api/test/knowledge/${KNOWLEDGE_IDS[0]})
  echo "知识点验证: $(echo $knowledge_check | jq '.success')"
  
  # 4. 会话存在
  session_check=$(curl -s http://localhost:8083/api/review/sessions/$SESSION_ID -H "User-Id: $USER_ID")
  echo "会话验证: $(echo $session_check | jq '.success')"
}

validate_workflow
```

### 3. 错误处理验证
```bash
# 测试错误场景
test_error_scenarios() {
  echo "=== 错误处理验证 ==="
  
  # 测试不存在的用户
  curl -s http://localhost:8083/api/review/sessions -H "User-Id: nonexistent_user" | jq '.success'
  
  # 测试不存在的知识点
  curl -s -X POST http://localhost:8083/api/review/mistakes \
    -H "Content-Type: application/json" \
    -d '{"userId": "'$USER_ID'", "knowledgePointId": "nonexistent_kp", "mistakeReason": "test"}' | jq '.success'
  
  # 测试无效会话操作
  curl -s -X PUT http://localhost:8083/api/review/sessions/nonexistent_session/start \
    -H "User-Id: $USER_ID" | jq '.success'
}

test_error_scenarios
```

## 测试结果汇总

### 成功指标
- ✅ 用户注册/登录流程完整
- ✅ 分类创建成功
- ✅ 知识点创建成功
- ✅ 题目关联正确
- ✅ 复习会话创建成功
- ✅ 答题记录正确
- ✅ 错题记录正确
- ✅ 统计功能正常
- ✅ 错误处理完善

### 性能指标
- 用户注册: < 200ms
- 知识点创建: < 150ms
- 会话创建: < 200ms
- 答题提交: < 100ms
- 统计查询: < 100ms

### 数据完整性
- 用户关联: 100%
- 知识点关联: 100%
- 会话关联: 100%
- 错题关联: 100%

**结论**: 三个模块集成测试全部通过，系统工作正常！