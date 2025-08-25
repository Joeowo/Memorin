# Phase 2 - API接口完整文档

## 📋 **文档信息**

| 项目信息 | 详情 |
|---------|------|
| **文档标题** | Phase 2 API接口完整文档 |
| **服务地址** | http://localhost:8082 |
| **API版本** | v1.0 |
| **认证方式** | JWT Bearer Token |
| **内容类型** | application/json |

## 🌐 **API接口总览**

### **接口数量统计**
- **TextQuestionData**: 26个接口
- **ChoiceQuestionData**: 24个接口  
- **CodeQuestionData**: 28个接口
- **总计**: 78个RESTful API接口

### **接口分类**
- 🔧 **CRUD操作**: 15个接口
- 🔍 **查询功能**: 35个接口
- 🔎 **搜索功能**: 18个接口
- 📊 **统计分析**: 10个接口

---

## 🔤 **TextQuestionData API接口**

### **基础路径**: `/api/textquestions`

#### **1. CRUD操作**

##### **1.1 创建文本题**
```http
POST /api/textquestions
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体示例**:
```json
{
  "knowledgeId": "BK_20250815_001",
  "textType": "fill",
  "answer": "Java",
  "validationMode": "exact",
  "alternativeAnswers": ["JAVA", "java"],
  "caseSensitive": false,
  "regexPattern": null,
  "maxWordCount": null,
  "minWordCount": null,
  "scoringPoints": []
}
```

**响应示例**:
```json
{
  "id": "TQD_20250815_001",
  "knowledgeId": "BK_20250815_001",
  "textType": "fill",
  "answer": "Java",
  "validationMode": "exact",
  "alternativeAnswers": ["JAVA", "java"],
  "caseSensitive": false,
  "createdAt": 1723652800000,
  "updatedAt": 1723652800000,
  "hasAlternativeAnswers": true,
  "isEssayQuestion": false,
  "isFillQuestion": true,
  "scoringPointsCount": 0,
  "totalScoringPoints": 0
}
```

##### **1.2 获取文本题**
```http
GET /api/textquestions/{id}
Authorization: Bearer {jwt_token}
```

##### **1.3 更新文本题**
```http
PUT /api/textquestions/{id}
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

##### **1.4 删除文本题**
```http
DELETE /api/textquestions/{id}
Authorization: Bearer {jwt_token}
```

**响应示例**:
```json
{
  "message": "文本题数据删除成功",
  "deletedId": "TQD_20250815_001"
}
```

#### **2. 查询操作**

##### **2.1 获取所有文本题**
```http
GET /api/textquestions
Authorization: Bearer {jwt_token}
```

##### **2.2 根据知识点获取**
```http
GET /api/textquestions/knowledge/{knowledgeId}
Authorization: Bearer {jwt_token}
```

##### **2.3 根据题型获取**
```http
GET /api/textquestions/type/{textType}
Authorization: Bearer {jwt_token}
```
**参数**: `textType` = `fill` | `essay`

##### **2.4 根据验证模式获取**
```http
GET /api/textquestions/validation/{validationMode}
Authorization: Bearer {jwt_token}
```
**参数**: `validationMode` = `exact` | `contains` | `regex` | `manual`

##### **2.5 分页查询**
```http
GET /api/textquestions/page?page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer {jwt_token}
```

**响应示例**:
```json
{
  "textQuestions": [...],
  "currentPage": 0,
  "totalPages": 5,
  "totalElements": 45,
  "size": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

#### **3. 搜索功能**

##### **3.1 关键词搜索**
```http
GET /api/textquestions/search/keyword?keyword=Java
Authorization: Bearer {jwt_token}
```

##### **3.2 按答案内容搜索**
```http
GET /api/textquestions/search/answer?keyword=编程
Authorization: Bearer {jwt_token}
```

#### **4. 统计分析**

##### **4.1 获取统计信息**
```http
GET /api/textquestions/statistics
Authorization: Bearer {jwt_token}
```

**响应示例**:
```json
{
  "totalCount": 25,
  "textTypeStats": {
    "fill": 15,
    "essay": 10
  },
  "validationModeStats": {
    "exact": 12,
    "contains": 8,
    "regex": 3,
    "manual": 2
  },
  "averageWordCount": 45.5,
  "withAlternativeAnswersCount": 18,
  "withScoringPointsCount": 10
}
```

---

## ☑️ **ChoiceQuestionData API接口**

### **基础路径**: `/api/choicequestions`

#### **1. CRUD操作**

##### **1.1 创建选择题**
```http
POST /api/choicequestions
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体示例**:
```json
{
  "knowledgeId": "BK_20250815_002",
  "choiceType": "single",
  "points": 10,
  "partialCredit": false,
  "randomOrder": false,
  "explanation": "这是一道单选题的解析",
  "options": [
    {
      "optionKey": "A",
      "optionText": "选项A的内容",
      "isCorrect": true,
      "explanation": "选项A的解析",
      "sortOrder": 0
    },
    {
      "optionKey": "B",
      "optionText": "选项B的内容",
      "isCorrect": false,
      "explanation": "选项B的解析",
      "sortOrder": 1
    }
  ]
}
```

**响应示例**:
```json
{
  "id": "CQD_20250815_001",
  "knowledgeId": "BK_20250815_002",
  "choiceType": "single",
  "points": 10,
  "partialCredit": false,
  "randomOrder": false,
  "explanation": "这是一道单选题的解析",
  "options": [...],
  "createdAt": 1723652800000,
  "updatedAt": 1723652800000,
  "totalOptions": 2,
  "correctOptionsCount": 1,
  "hasExplanation": true,
  "isSingleChoice": true,
  "isMultipleChoice": false,
  "supportsPartialCredit": false
}
```

#### **2. 查询操作**

##### **2.1 根据题型获取**
```http
GET /api/choicequestions/type/{choiceType}
Authorization: Bearer {jwt_token}
```
**参数**: `choiceType` = `single` | `multiple`

##### **2.2 根据分值范围获取**
```http
GET /api/choicequestions/points?minPoints=5&maxPoints=15
Authorization: Bearer {jwt_token}
```

##### **2.3 获取支持部分得分的题目**
```http
GET /api/choicequestions/partial-credit
Authorization: Bearer {jwt_token}
```

#### **3. 统计分析**

##### **3.1 选择题统计**
```http
GET /api/choicequestions/statistics
Authorization: Bearer {jwt_token}
```

**响应示例**:
```json
{
  "totalCount": 18,
  "choiceTypeStats": {
    "single": 12,
    "multiple": 6
  },
  "pointsRangeStats": {
    "low": 5,
    "medium": 8,
    "high": 5
  },
  "averagePoints": 12.5,
  "withPartialCreditCount": 6,
  "withRandomOrderCount": 4,
  "averageOptionsCount": 4.2
}
```

---

## 💻 **CodeQuestionData API接口**

### **基础路径**: `/api/codequestions`

#### **1. CRUD操作**

##### **1.1 创建编程题**
```http
POST /api/codequestions
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体示例**:
```json
{
  "knowledgeId": "BK_20250815_003",
  "programmingLanguage": "java",
  "title": "冒泡排序算法实现",
  "description": "请实现一个冒泡排序算法",
  "difficultyLevel": 3,
  "points": 30,
  "initialCode": "public class BubbleSort {\n    // 请实现\n}",
  "hints": "使用双重循环实现冒泡排序",
  "standardAnswer": "// 标准答案代码",
  "testCases": [
    {
      "caseName": "基本测试用例",
      "inputData": "[3,1,4,1,5]",
      "expectedOutput": "[1,1,3,4,5]",
      "isHidden": false,
      "casePoints": 15,
      "sortOrder": 0,
      "description": "测试基本排序功能"
    },
    {
      "caseName": "边界测试用例",
      "inputData": "[]",
      "expectedOutput": "[]",
      "isHidden": true,
      "casePoints": 15,
      "sortOrder": 1,
      "description": "测试空数组情况"
    }
  ]
}
```

**响应示例**:
```json
{
  "id": "CQDC_20250815_001",
  "knowledgeId": "BK_20250815_003",
  "programmingLanguage": "java",
  "title": "冒泡排序算法实现",
  "description": "请实现一个冒泡排序算法",
  "difficultyLevel": 3,
  "points": 30,
  "initialCode": "public class BubbleSort {\n    // 请实现\n}",
  "hints": "使用双重循环实现冒泡排序",
  "standardAnswer": "// 标准答案代码",
  "testCases": [...],
  "createdAt": 1723652800000,
  "updatedAt": 1723652800000,
  "totalTestCasePoints": 30,
  "publicTestCaseCount": 1,
  "hiddenTestCaseCount": 1,
  "totalTestCases": 2,
  "hasInitialCode": true,
  "hasHints": true,
  "hasStandardAnswer": true,
  "isTestCasePointsValid": true,
  "programmingLanguageDisplayName": "Java",
  "difficultyLevelDescription": "中等",
  "complexProblem": false,
  "simpleProblem": false
}
```

#### **2. 查询操作**

##### **2.1 根据编程语言获取**
```http
GET /api/codequestions/language/{language}
Authorization: Bearer {jwt_token}
```
**参数**: `language` = `java` | `python` | `javascript` | `cpp` | `c` | `sql` | `html` | `css`

##### **2.2 根据难度等级获取**
```http
GET /api/codequestions/difficulty/{level}
Authorization: Bearer {jwt_token}
```
**参数**: `level` = 1-5

##### **2.3 根据分值范围获取**
```http
GET /api/codequestions/points?minPoints=20&maxPoints=50
Authorization: Bearer {jwt_token}
```

##### **2.4 复合条件查询**
```http
GET /api/codequestions/search?categoryId=CAT_001&programmingLanguage=java&difficultyLevel=3&minPoints=20&maxPoints=50&page=0&size=10
Authorization: Bearer {jwt_token}
```

#### **3. 搜索功能**

##### **3.1 关键词搜索**
```http
GET /api/codequestions/search/keyword?keyword=排序
Authorization: Bearer {jwt_token}
```

##### **3.2 代码内容搜索**
```http
GET /api/codequestions/search/code?keyword=bubble
Authorization: Bearer {jwt_token}
```

##### **3.3 提示内容搜索**
```http
GET /api/codequestions/search/hints?keyword=循环
Authorization: Bearer {jwt_token}
```

#### **4. 统计分析**

##### **4.1 编程题统计**
```http
GET /api/codequestions/statistics
Authorization: Bearer {jwt_token}
```

**响应示例**:
```json
{
  "totalCount": 32,
  "programmingLanguageStats": {
    "java": 12,
    "python": 8,
    "javascript": 6,
    "cpp": 4,
    "sql": 2
  },
  "difficultyLevelStats": {
    "level1": 8,
    "level2": 10,
    "level3": 8,
    "level4": 4,
    "level5": 2
  },
  "pointsRangeStats": {
    "low": 12,
    "medium": 15,
    "high": 5
  },
  "categoryStats": {
    "CAT_20250815_001": 32
  },
  "withInitialCodeCount": 28,
  "withHintsCount": 25,
  "withStandardAnswerCount": 20,
  "averageDifficulty": 2.8,
  "averagePoints": 25.5
}
```

##### **4.2 获取测试用例最多的编程题**
```http
GET /api/codequestions/max-testcases
Authorization: Bearer {jwt_token}
```

---

## 🧪 **测试接口**

每个模块都提供了测试接口，用于开发调试，无需JWT认证：

### **测试接口路径**
- TextQuestionData: `/api/test/textquestions`
- ChoiceQuestionData: `/api/test/choicequestions`  
- CodeQuestionData: `/api/test/codequestions`

### **测试用户ID**: `test_user_001`

### **测试信息接口**
```http
GET /api/test/{module}/info
```

**响应示例**:
```json
{
  "testUserId": "test_user_001",
  "description": "编程题数据测试控制器",
  "warning": "⚠️ 仅用于开发测试，生产环境禁用",
  "endpoints": {
    "create": "POST /api/test/codequestions",
    "getAll": "GET /api/test/codequestions",
    "getById": "GET /api/test/codequestions/{id}",
    "update": "PUT /api/test/codequestions/{id}",
    "delete": "DELETE /api/test/codequestions/{id}",
    "statistics": "GET /api/test/codequestions/statistics"
  }
}
```

---

## 🔧 **通用响应格式**

### **成功响应**
- **状态码**: 200 OK
- **内容类型**: application/json

### **错误响应**
```json
{
  "error": "错误类型描述",
  "message": "详细错误信息",
  "timestamp": 1723652800000
}
```

### **常见错误状态码**
- **400 Bad Request**: 请求参数错误、数据验证失败
- **401 Unauthorized**: JWT令牌无效或过期
- **403 Forbidden**: 权限不足
- **404 Not Found**: 资源不存在
- **500 Internal Server Error**: 服务器内部错误

---

## 🔐 **认证与授权**

### **JWT令牌格式**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### **令牌包含信息**
- 用户ID (subject)
- 用户名 (username)
- 角色信息 (roles)
- 过期时间 (exp)

### **权限控制**
- 所有API都会验证用户权限
- 用户只能访问自己创建的数据
- 通过BaseKnowledge关联控制访问权限

---

## 📊 **分页查询参数**

### **通用分页参数**
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | int | 0 | 页码（从0开始） |
| `size` | int | 10 | 每页数量 |
| `sortBy` | string | createdAt | 排序字段 |
| `sortDir` | string | desc | 排序方向（asc/desc） |

### **分页响应格式**
```json
{
  "content": [...],
  "currentPage": 0,
  "totalPages": 5,
  "totalElements": 45,
  "size": 10,
  "hasNext": true,
  "hasPrevious": false,
  "first": true,
  "last": false
}
```

---

## 🔍 **搜索查询参数**

### **通用搜索参数**
| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| `keyword` | string | 是 | 搜索关键词 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |

### **复合条件查询参数（编程题）**
| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| `categoryId` | string | 否 | 分类ID |
| `programmingLanguage` | string | 否 | 编程语言 |
| `difficultyLevel` | int | 否 | 难度等级 |
| `minPoints` | int | 否 | 最小分值 |
| `maxPoints` | int | 否 | 最大分值 |

---

## 📝 **数据验证规则**

### **通用验证规则**
- ID字段：50字符限制，不能为空
- 文本字段：根据业务需求限制长度
- 数值字段：合理范围验证
- 集合字段：数量限制（通常1-20个）

### **业务验证规则**
- **排序顺序**：必须从0开始且连续
- **分值配置**：子项分值总和必须等于总分值
- **关联验证**：知识点必须存在且类型匹配
- **权限验证**：用户只能操作自己的数据

---

## 🚀 **性能优化建议**

### **查询优化**
1. 使用分页查询避免大数据量加载
2. 合理使用搜索接口而非获取全部数据
3. 统计接口适合缓存处理

### **请求优化**
1. 批量操作优于单个操作
2. 避免频繁的小请求
3. 合理使用HTTP缓存机制

### **响应优化**
1. 只请求需要的字段
2. 使用压缩传输
3. 合理设置超时时间

---

## 📖 **使用示例**

### **完整的编程题创建流程**
```javascript
// 1. 创建编程题
const createResponse = await fetch('/api/codequestions', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    knowledgeId: 'BK_20250815_001',
    programmingLanguage: 'java',
    title: '简单排序',
    description: '实现冒泡排序',
    difficultyLevel: 2,
    points: 20,
    testCases: [
      {
        caseName: '基础测试',
        inputData: '[3,1,2]',
        expectedOutput: '[1,2,3]',
        isHidden: false,
        casePoints: 20,
        sortOrder: 0,
        description: '基础排序测试'
      }
    ]
  })
});

const createdQuestion = await createResponse.json();
console.log('创建的编程题ID:', createdQuestion.id);

// 2. 获取编程题详情
const detailResponse = await fetch(`/api/codequestions/${createdQuestion.id}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const questionDetail = await detailResponse.json();
console.log('编程题详情:', questionDetail);

// 3. 搜索相关编程题
const searchResponse = await fetch('/api/codequestions/search/keyword?keyword=排序', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const searchResults = await searchResponse.json();
console.log('搜索结果:', searchResults);
```

---

*文档版本：v1.0*  
*最后更新：2025年8月15日*  
*下一版本：计划添加批量操作和导入导出接口* 