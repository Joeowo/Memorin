# 🧠 智能复习服务 API文档

> **服务端口**: 8083  
> **API版本**: v0  
> **生成时间**: 2025年08月18日 23:43:09

## 📋 服务概述

**服务名称**: OpenAPI definition  
**服务描述**:   
**联系方式**:  <>  
**许可证**: 
## 🌐 服务器地址

- **Generated server url**: `http://localhost:8083`

## 📡 API接口列表

### mistake-controller

#### 🟡 `PUT` /api/review/mistakes/{userId}/{knowledgePointId}/unresolve

**功能**: 

**操作ID**: `unresolveMistake`

---

#### 🟡 `PUT` /api/review/mistakes/{userId}/{knowledgePointId}/resolve

**功能**: 

**操作ID**: `resolveMistake`

---

#### 🔵 `POST` /api/review/mistakes

**功能**: 

**操作ID**: `addMistake`

---

#### 🟢 `GET` /api/review/mistakes/{userId}/{knowledgePointId}

**功能**: 

**操作ID**: `getMistakeDetail`

---

#### 🔴 `DELETE` /api/review/mistakes/{userId}/{knowledgePointId}

**功能**: 

**操作ID**: `deleteMistake`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}

**功能**: 

**操作ID**: `getUserMistakes`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}/unresolved

**功能**: 

**操作ID**: `getUnresolvedMistakes`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}/statistics

**功能**: 

**操作ID**: `getMistakeStatistics`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}/resolved

**功能**: 

**操作ID**: `getResolvedMistakes`

---

#### 🔴 `DELETE` /api/review/mistakes/user/{userId}/resolved

**功能**: 

**操作ID**: `cleanupResolvedMistakes`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}/recent

**功能**: 

**操作ID**: `getRecentMistakes`

---

#### 🟢 `GET` /api/review/mistakes/user/{userId}/high-priority

**功能**: 

**操作ID**: `getHighPriorityMistakes`

---

#### 🟢 `GET` /api/review/mistakes/test

**功能**: 

**操作ID**: `testMistakeService`

---

### question-generator-controller

#### 🔵 `POST` /api/review/question-generator/templates/weakness-review

**功能**: 

**操作ID**: `generateWeaknessReview`

---

#### 🔵 `POST` /api/review/question-generator/templates/smart-review

**功能**: 

**操作ID**: `generateSmartReview`

---

#### 🔵 `POST` /api/review/question-generator/templates/mistake-review/{baseId}

**功能**: 

**操作ID**: `generateMistakeReview`

---

#### 🔵 `POST` /api/review/question-generator/templates/knowledge-base/{baseId}

**功能**: 

**操作ID**: `generateKnowledgeBaseReview`

---

#### 🔵 `POST` /api/review/question-generator/generate

**功能**: 

**操作ID**: `generateQuestionList`

---

#### 🟢 `GET` /api/review/question-generator/test

**功能**: 

**操作ID**: `test`

---

#### 🟢 `GET` /api/review/question-generator/strategies

**功能**: 

**操作ID**: `getAvailableStrategies`

---

### 复习会话管理

#### 🟡 `PUT` /api/review/sessions/{sessionId}/start

**功能**: 

**操作ID**: `startSession`

---

#### 🟡 `PUT` /api/review/sessions/{sessionId}/resume

**功能**: 

**操作ID**: `resumeSession`

---

#### 🟡 `PUT` /api/review/sessions/{sessionId}/pause

**功能**: 

**操作ID**: `pauseSession`

---

#### 🟡 `PUT` /api/review/sessions/{sessionId}/complete

**功能**: 

**操作ID**: `completeSession`

---

#### 🟡 `PUT` /api/review/sessions/{sessionId}/cancel

**功能**: 

**操作ID**: `cancelSession`

---

#### 🟢 `GET` /api/review/sessions

**功能**: 

**操作ID**: `getUserSessions`

---

#### 🔵 `POST` /api/review/sessions

**功能**: 创建复习会话

**描述**: 根据用户需求创建个性化的复习会话，支持多种复习模式

**操作ID**: `createSession`

---

#### 🔵 `POST` /api/review/sessions/{sessionId}/submit

**功能**: 

**操作ID**: `submitAnswer`

---

#### 🟢 `GET` /api/review/sessions/{sessionId}

**功能**: 

**操作ID**: `getSession`

---

#### 🔴 `DELETE` /api/review/sessions/{sessionId}

**功能**: 

**操作ID**: `deleteSession`

---

#### 🟢 `GET` /api/review/sessions/test

**功能**: 

**操作ID**: `createTestSession`

---

#### 🟢 `GET` /api/review/sessions/active

**功能**: 

**操作ID**: `getActiveSessions`

---

### review-algorithm-controller

#### 🔵 `POST` /api/review/algorithm/calculate

**功能**: 

**操作ID**: `calculateNextReview`

---

#### 🔵 `POST` /api/review/algorithm/batch-calculate

**功能**: 

**操作ID**: `batchCalculateNextReview`

---

#### 🟢 `GET` /api/review/algorithm/test

**功能**: 

**操作ID**: `testAlgorithm`

---

#### 🟢 `GET` /api/review/algorithm/mastery-level

**功能**: 

**操作ID**: `calculateMasteryLevel`

---

#### 🟢 `GET` /api/review/algorithm/default-parameters

**功能**: 

**操作ID**: `getDefaultParameters`

---

## 📊 数据模型

### CreateReviewSessionRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `reviewMode` | `string` |  |
| `knowledgeBaseId` | `string` |  |
| `knowledgeAreaId` | `string` |  |
| `targetQuestionCount` | `integer` |  |
| `difficultyLevels` | `array` |  |
| `questionTypes` | `array` |  |
| `tags` | `array` |  |
| `onlyDueQuestions` | `boolean` |  |
| `sortOrder` | `string` |  |
| `maxDurationMinutes` | `integer` |  |
| `autoStart` | `boolean` |  |
| `notes` | `string` |  |
| `customKnowledgePointIds` | `array` |  |
| `mistakeReviewDays` | `integer` |  |
| `sessionConfig` | `string` |  |

### SubmitAnswerRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `sessionId` | `string` |  |
| `knowledgePointId` | `string` |  |
| `questionIndex` | `integer` |  |
| `userAnswer` | `string` |  |
| `isCorrect` | `boolean` |  |
| `qualityRating` | `integer` |  |
| `timeSpentSeconds` | `integer` |  |
| `mistakeReason` | `string` |  |
| `studyNotes` | `string` |  |
| `perceivedDifficulty` | `integer` |  |
| `isSkipped` | `boolean` |  |
| `submissionType` | `string` |  |
| `questionType` | `string` |  |

### FilterConfig

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `type` | `string` |  |
| `params` | `object` |  |

### FinalProcessingOptions

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `enableChoiceShuffle` | `boolean` |  |
| `addMetadata` | `boolean` |  |
| `logGeneration` | `boolean` |  |

### LimiterConfig

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `type` | `string` |  |
| `params` | `object` |  |

### QuestionGeneratorRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `source` | `object` |  |
| `filters` | `array` |  |
| `sorter` | `object` |  |
| `limiter` | `object` |  |
| `finalProcessing` | `object` |  |
| `userId` | `string` |  |
| `generationId` | `string` |  |

### SorterConfig

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `type` | `string` |  |
| `params` | `object` |  |

### SourceConfig

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `type` | `string` |  |
| `params` | `object` |  |

### MistakeRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `userId` | `string` |  |
| `knowledgePointId` | `string` |  |
| `mistakeReason` | `string` |  |
| `questionType` | `string` |  |
| `difficultyLevel` | `string` |  |
| `mistakeTags` | `string` |  |
| `userNotes` | `string` |  |

### ReviewAlgorithmRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `easeFactor` | `number` |  |
| `interval` | `integer` |  |
| `quality` | `integer` |  |
| `knowledgePointId` | `string` |  |
| `userId` | `string` |  |

## 🔗 相关链接

- **Swagger UI**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)
- **健康检查**: [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health)

## 📝 使用示例

### 认证方式
`ash
# 如果需要认证，请在请求头中添加JWT Token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     http://localhost:8083/api/endpoint
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

