# 📚 知识库管理服务 API文档

> **服务端口**: 8082  
> **API版本**: v0  
> **生成时间**: 2025年08月18日 23:43:09

## 📋 服务概述

**服务名称**: OpenAPI definition  
**服务描述**:   
**联系方式**:  <>  
**许可证**: 
## 🌐 服务器地址

- **Generated server url**: `http://localhost:8082`

## 📡 API接口列表

### code-question-data-test-controller

#### 🟢 `GET` /api/test/codequestions/{id}

**功能**: 

**操作ID**: `getCodeQuestionDataById`

---

#### 🟡 `PUT` /api/test/codequestions/{id}

**功能**: 

**操作ID**: `updateCodeQuestionData`

---

#### 🔴 `DELETE` /api/test/codequestions/{id}

**功能**: 

**操作ID**: `deleteCodeQuestionData`

---

#### 🟢 `GET` /api/test/codequestions

**功能**: 

**操作ID**: `getAllCodeQuestionData`

---

#### 🔵 `POST` /api/test/codequestions

**功能**: 

**操作ID**: `createCodeQuestionData`

---

#### 🟢 `GET` /api/test/codequestions/statistics

**功能**: 

**操作ID**: `getCodeQuestionDataStatistics`

---

#### 🟢 `GET` /api/test/codequestions/search

**功能**: 

**操作ID**: `searchCodeQuestionData`

---

#### 🟢 `GET` /api/test/codequestions/search/keyword

**功能**: 

**操作ID**: `searchCodeQuestionDataByKeyword`

---

#### 🟢 `GET` /api/test/codequestions/search/hints

**功能**: 

**操作ID**: `searchCodeQuestionDataByHints`

---

#### 🟢 `GET` /api/test/codequestions/search/code

**功能**: 

**操作ID**: `searchCodeQuestionDataByCode`

---

#### 🟢 `GET` /api/test/codequestions/page

**功能**: 

**操作ID**: `getCodeQuestionDataWithPagination`

---

#### 🟢 `GET` /api/test/codequestions/max-testcases

**功能**: 

**操作ID**: `getCodeQuestionDataWithMaxTestCases`

---

#### 🟢 `GET` /api/test/codequestions/language/{language}

**功能**: 

**操作ID**: `getCodeQuestionDataByLanguage`

---

#### 🟢 `GET` /api/test/codequestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getCodeQuestionDataByKnowledgeId`

---

#### 🟢 `GET` /api/test/codequestions/info

**功能**: 

**操作ID**: `getTestInfo`

---

#### 🟢 `GET` /api/test/codequestions/difficulty/{level}

**功能**: 

**操作ID**: `getCodeQuestionDataByDifficulty`

---

### base-knowledge-test-controller

#### 🟢 `GET` /api/test/knowledge/{id}

**功能**: 

**操作ID**: `getTestKnowledgeById`

---

#### 🟡 `PUT` /api/test/knowledge/{id}

**功能**: 

**操作ID**: `updateTestKnowledge`

---

#### 🔴 `DELETE` /api/test/knowledge/{id}

**功能**: 

**操作ID**: `deleteTestKnowledge`

---

#### 🟢 `GET` /api/test/knowledge

**功能**: 

**操作ID**: `getAllTestKnowledge`

---

#### 🔵 `POST` /api/test/knowledge

**功能**: 

**操作ID**: `createTestKnowledge`

---

#### 🔵 `POST` /api/test/knowledge/demo

**功能**: 

**操作ID**: `createDemoKnowledge`

---

#### 🟢 `GET` /api/test/knowledge/statistics

**功能**: 

**操作ID**: `getTestKnowledgeStatistics`

---

#### 🟢 `GET` /api/test/knowledge/search

**功能**: 

**操作ID**: `searchTestKnowledge`

---

#### 🟢 `GET` /api/test/knowledge/category/{categoryId}

**功能**: 

**操作ID**: `getTestKnowledgeByCategory`

---

### category-controller

#### 🟢 `GET` /api/categories/{id}

**功能**: 

**操作ID**: `getCategoryById`

---

#### 🟡 `PUT` /api/categories/{id}

**功能**: 

**操作ID**: `updateCategory`

---

#### 🔴 `DELETE` /api/categories/{id}

**功能**: 

**操作ID**: `deleteCategory`

---

#### 🟢 `GET` /api/categories

**功能**: 

**操作ID**: `getAllCategories`

---

#### 🔵 `POST` /api/categories

**功能**: 

**操作ID**: `createCategory`

---

#### 🟢 `GET` /api/categories/tree

**功能**: 

**操作ID**: `getCategoryTree`

---

#### 🟢 `GET` /api/categories/search

**功能**: 

**操作ID**: `searchCategories`

---

#### 🟢 `GET` /api/categories/page

**功能**: 

**操作ID**: `getCategoriesWithPagination`

---

### category-test-controller

#### 🟢 `GET` /api/test/categories/{id}

**功能**: 

**操作ID**: `getTestCategoryById`

---

#### 🟡 `PUT` /api/test/categories/{id}

**功能**: 

**操作ID**: `updateTestCategory`

---

#### 🔴 `DELETE` /api/test/categories/{id}

**功能**: 

**操作ID**: `deleteTestCategory`

---

#### 🟢 `GET` /api/test/categories

**功能**: 

**操作ID**: `getAllTestCategories`

---

#### 🔵 `POST` /api/test/categories

**功能**: 

**操作ID**: `createTestCategory`

---

#### 🔵 `POST` /api/test/categories/demo

**功能**: 

**操作ID**: `createDemoData`

---

#### 🟢 `GET` /api/test/categories/tree

**功能**: 

**操作ID**: `getTestCategoryTree`

---

#### 🟢 `GET` /api/test/categories/search

**功能**: 

**操作ID**: `searchTestCategories`

---

### base-knowledge-controller

#### 🟢 `GET` /api/knowledge/{id}

**功能**: 

**操作ID**: `getKnowledgeById`

---

#### 🟡 `PUT` /api/knowledge/{id}

**功能**: 

**操作ID**: `updateKnowledge`

---

#### 🔴 `DELETE` /api/knowledge/{id}

**功能**: 

**操作ID**: `deleteKnowledge`

---

#### 🟢 `GET` /api/knowledge

**功能**: 

**操作ID**: `getAllKnowledge`

---

#### 🔵 `POST` /api/knowledge

**功能**: 

**操作ID**: `createKnowledge`

---

#### 🟢 `GET` /api/knowledge/type/{type}

**功能**: 

**操作ID**: `getKnowledgeByType`

---

#### 🟢 `GET` /api/knowledge/tag

**功能**: 

**操作ID**: `getKnowledgeByTag`

---

#### 🟢 `GET` /api/knowledge/status/{status}

**功能**: 

**操作ID**: `getKnowledgeByStatus`

---

#### 🟢 `GET` /api/knowledge/statistics

**功能**: 

**操作ID**: `getKnowledgeStatistics`

---

#### 🟢 `GET` /api/knowledge/search

**功能**: 

**操作ID**: `searchKnowledge`

---

#### 🟢 `GET` /api/knowledge/page

**功能**: 

**操作ID**: `getKnowledgeWithPagination`

---

#### 🟢 `GET` /api/knowledge/difficulty/{difficulty}

**功能**: 

**操作ID**: `getKnowledgeByDifficulty`

---

#### 🟢 `GET` /api/knowledge/category/{categoryId}

**功能**: 

**操作ID**: `getKnowledgeByCategory`

---

### choice-question-data-test-controller

#### 🟢 `GET` /api/test/choicequestions/{id}

**功能**: 

**操作ID**: `getChoiceQuestionDataById`

---

#### 🟡 `PUT` /api/test/choicequestions/{id}

**功能**: 

**操作ID**: `updateChoiceQuestionData`

---

#### 🔴 `DELETE` /api/test/choicequestions/{id}

**功能**: 

**操作ID**: `deleteChoiceQuestionData`

---

#### 🟢 `GET` /api/test/choicequestions

**功能**: 

**操作ID**: `getAllChoiceQuestionData`

---

#### 🔵 `POST` /api/test/choicequestions

**功能**: 

**操作ID**: `createChoiceQuestionData`

---

#### 🔵 `POST` /api/test/choicequestions/demo

**功能**: 

**操作ID**: `createDemoChoiceQuestionData`

---

#### 🟢 `GET` /api/test/choicequestions/type/{choiceType}

**功能**: 

**操作ID**: `getChoiceQuestionDataByType`

---

#### 🟢 `GET` /api/test/choicequestions/statistics

**功能**: 

**操作ID**: `getChoiceQuestionDataStatistics`

---

#### 🟢 `GET` /api/test/choicequestions/search/{keyword}

**功能**: 

**操作ID**: `searchChoiceQuestionDataByKeyword`

---

#### 🟢 `GET` /api/test/choicequestions/page

**功能**: 

**操作ID**: `getChoiceQuestionDataWithPagination`

---

#### 🟢 `GET` /api/test/choicequestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getChoiceQuestionDataByKnowledgeId`

---

### choice-question-data-controller

#### 🟢 `GET` /api/choicequestions/{id}

**功能**: 

**操作ID**: `getChoiceQuestionDataById_1`

---

#### 🟡 `PUT` /api/choicequestions/{id}

**功能**: 

**操作ID**: `updateChoiceQuestionData_1`

---

#### 🔴 `DELETE` /api/choicequestions/{id}

**功能**: 

**操作ID**: `deleteChoiceQuestionData_1`

---

#### 🟢 `GET` /api/choicequestions

**功能**: 

**操作ID**: `getAllChoiceQuestionData_1`

---

#### 🔵 `POST` /api/choicequestions

**功能**: 

**操作ID**: `createChoiceQuestionData_1`

---

#### 🟢 `GET` /api/choicequestions/type/{choiceType}

**功能**: 

**操作ID**: `getChoiceQuestionDataByType_1`

---

#### 🟢 `GET` /api/choicequestions/statistics

**功能**: 

**操作ID**: `getChoiceQuestionDataStatistics_1`

---

#### 🟢 `GET` /api/choicequestions/search

**功能**: 

**操作ID**: `searchChoiceQuestionDataByComplexConditions`

---

#### 🟢 `GET` /api/choicequestions/search/{keyword}

**功能**: 

**操作ID**: `searchChoiceQuestionDataByKeyword_1`

---

#### 🟢 `GET` /api/choicequestions/search/option/{keyword}

**功能**: 

**操作ID**: `searchChoiceQuestionDataByOptionContent`

---

#### 🟢 `GET` /api/choicequestions/page

**功能**: 

**操作ID**: `getChoiceQuestionDataWithPagination_1`

---

#### 🟢 `GET` /api/choicequestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getChoiceQuestionDataByKnowledgeId_1`

---

#### 🟢 `GET` /api/choicequestions/category/{categoryId}

**功能**: 

**操作ID**: `getChoiceQuestionDataByCategory`

---

### text-question-data-controller

#### 🟢 `GET` /api/textquestions/{id}

**功能**: 

**操作ID**: `getTextQuestionDataById`

---

#### 🟡 `PUT` /api/textquestions/{id}

**功能**: 

**操作ID**: `updateTextQuestionData`

---

#### 🔴 `DELETE` /api/textquestions/{id}

**功能**: 

**操作ID**: `deleteTextQuestionData`

---

#### 🟢 `GET` /api/textquestions

**功能**: 

**操作ID**: `getAllTextQuestionData`

---

#### 🔵 `POST` /api/textquestions

**功能**: 

**操作ID**: `createTextQuestionData`

---

#### 🟢 `GET` /api/textquestions/type/{textType}

**功能**: 

**操作ID**: `getTextQuestionDataByType`

---

#### 🟢 `GET` /api/textquestions/statistics

**功能**: 

**操作ID**: `getTextQuestionDataStatistics`

---

#### 🟢 `GET` /api/textquestions/search

**功能**: 

**操作ID**: `searchTextQuestionDataByComplexConditions`

---

#### 🟢 `GET` /api/textquestions/search/{keyword}

**功能**: 

**操作ID**: `searchTextQuestionDataByKeyword`

---

#### 🟢 `GET` /api/textquestions/page

**功能**: 

**操作ID**: `getTextQuestionDataWithPagination`

---

#### 🟢 `GET` /api/textquestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getTextQuestionDataByKnowledgeId`

---

#### 🟢 `GET` /api/textquestions/category/{categoryId}

**功能**: 

**操作ID**: `getTextQuestionDataByCategory`

---

### 知识库测试

#### 🟢 `GET` /api/test/ready

**功能**: 就绪检查

**描述**: 检查知识库服务是否准备好接收请求，包括数据库、缓存等组件状态

**操作ID**: `readinessCheck`

---

#### 🟢 `GET` /api/test/info

**功能**: 获取服务信息

**描述**: 获取知识库服务的基本信息，包括服务名称、版本、端口等

**操作ID**: `getServiceInfo`

---

#### 🟢 `GET` /api/test/health

**功能**: 健康检查

**描述**: 检查知识库服务是否正常运行

**操作ID**: `healthCheck`

---

### code-question-data-controller

#### 🟢 `GET` /api/codequestions/{id}

**功能**: 

**操作ID**: `getCodeQuestionDataById_1`

---

#### 🟡 `PUT` /api/codequestions/{id}

**功能**: 

**操作ID**: `updateCodeQuestionData_1`

---

#### 🔴 `DELETE` /api/codequestions/{id}

**功能**: 

**操作ID**: `deleteCodeQuestionData_1`

---

#### 🟢 `GET` /api/codequestions

**功能**: 

**操作ID**: `getAllCodeQuestionData_1`

---

#### 🔵 `POST` /api/codequestions

**功能**: 

**操作ID**: `createCodeQuestionData_1`

---

#### 🟢 `GET` /api/codequestions/statistics

**功能**: 

**操作ID**: `getCodeQuestionDataStatistics_1`

---

#### 🟢 `GET` /api/codequestions/search

**功能**: 

**操作ID**: `searchCodeQuestionData_1`

---

#### 🟢 `GET` /api/codequestions/search/keyword

**功能**: 

**操作ID**: `searchCodeQuestionDataByKeyword_1`

---

#### 🟢 `GET` /api/codequestions/search/hints

**功能**: 

**操作ID**: `searchCodeQuestionDataByHints_1`

---

#### 🟢 `GET` /api/codequestions/search/code

**功能**: 

**操作ID**: `searchCodeQuestionDataByCode_1`

---

#### 🟢 `GET` /api/codequestions/page

**功能**: 

**操作ID**: `getCodeQuestionDataWithPagination_1`

---

#### 🟢 `GET` /api/codequestions/max-testcases

**功能**: 

**操作ID**: `getCodeQuestionDataWithMaxTestCases_1`

---

#### 🟢 `GET` /api/codequestions/language/{language}

**功能**: 

**操作ID**: `getCodeQuestionDataByLanguage_1`

---

#### 🟢 `GET` /api/codequestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getCodeQuestionDataByKnowledgeId_1`

---

#### 🟢 `GET` /api/codequestions/difficulty/{level}

**功能**: 

**操作ID**: `getCodeQuestionDataByDifficulty_1`

---

#### 🟢 `GET` /api/codequestions/category/{categoryId}

**功能**: 

**操作ID**: `getCodeQuestionDataByCategory`

---

### text-question-data-test-controller

#### 🟢 `GET` /api/test/textquestions/{id}

**功能**: 

**操作ID**: `getTextQuestionDataById_1`

---

#### 🟡 `PUT` /api/test/textquestions/{id}

**功能**: 

**操作ID**: `updateTextQuestionData_1`

---

#### 🔴 `DELETE` /api/test/textquestions/{id}

**功能**: 

**操作ID**: `deleteTextQuestionData_1`

---

#### 🟢 `GET` /api/test/textquestions

**功能**: 

**操作ID**: `getAllTextQuestionData_1`

---

#### 🔵 `POST` /api/test/textquestions

**功能**: 

**操作ID**: `createTextQuestionData_1`

---

#### 🔵 `POST` /api/test/textquestions/demo

**功能**: 

**操作ID**: `createDemoTextQuestionData`

---

#### 🟢 `GET` /api/test/textquestions/type/{textType}

**功能**: 

**操作ID**: `getTextQuestionDataByType_1`

---

#### 🟢 `GET` /api/test/textquestions/statistics

**功能**: 

**操作ID**: `getTextQuestionDataStatistics_1`

---

#### 🟢 `GET` /api/test/textquestions/search/{keyword}

**功能**: 

**操作ID**: `searchTextQuestionDataByKeyword_1`

---

#### 🟢 `GET` /api/test/textquestions/page

**功能**: 

**操作ID**: `getTextQuestionDataWithPagination_1`

---

#### 🟢 `GET` /api/test/textquestions/knowledge/{knowledgeId}

**功能**: 

**操作ID**: `getTextQuestionDataByKnowledgeId_1`

---

## 📊 数据模型

### ScoringPointRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `description` | `string` |  |
| `points` | `integer` |  |
| `keywords` | `array` |  |

### UpdateTextQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `textType` | `string` |  |
| `answer` | `string` |  |
| `validationMode` | `string` |  |
| `alternativeAnswers` | `array` |  |
| `caseSensitive` | `boolean` |  |
| `regexPattern` | `string` |  |
| `scoringPoints` | `array` |  |
| `maxWordCount` | `integer` |  |
| `minWordCount` | `integer` |  |
| `essayQuestion` | `boolean` |  |
| `fillQuestion` | `boolean` |  |

### UpdateBaseKnowledgeRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `question` | `string` |  |
| `explanation` | `string` |  |
| `categoryId` | `string` |  |
| `tags` | `array` |  |
| `difficulty` | `integer` |  |
| `estimatedTime` | `integer` |  |
| `status` | `string` |  |

### TestCaseRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `caseName` | `string` |  |
| `inputData` | `string` |  |
| `expectedOutput` | `string` |  |
| `isHidden` | `boolean` |  |
| `casePoints` | `integer` |  |
| `sortOrder` | `integer` |  |
| `description` | `string` |  |

### UpdateCodeQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `programmingLanguage` | `string` |  |
| `title` | `string` |  |
| `description` | `string` |  |
| `difficultyLevel` | `integer` |  |
| `points` | `integer` |  |
| `initialCode` | `string` |  |
| `hints` | `string` |  |
| `testCases` | `array` |  |
| `standardAnswer` | `string` |  |
| `hiddenTestCaseCount` | `integer` |  |
| `publicTestCaseCount` | `integer` |  |
| `validTestCasePointsConfiguration` | `boolean` |  |
| `validSortOrderConfiguration` | `boolean` |  |
| `validTestCasePoints` | `boolean` |  |
| `validTestCaseConfiguration` | `boolean` |  |

### ChoiceOptionRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `optionKey` | `string` |  |
| `optionText` | `string` |  |
| `isCorrect` | `boolean` |  |
| `explanation` | `string` |  |
| `sortOrder` | `integer` |  |

### UpdateChoiceQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `choiceType` | `string` |  |
| `points` | `integer` |  |
| `partialCredit` | `boolean` |  |
| `randomOrder` | `boolean` |  |
| `options` | `array` |  |
| `explanation` | `string` |  |
| `validPartialCreditConfiguration` | `boolean` |  |
| `validSortOrderConfiguration` | `boolean` |  |
| `validOptionConfiguration` | `boolean` |  |

### UpdateCategoryRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `name` | `string` |  |
| `description` | `string` |  |
| `color` | `string` |  |
| `icon` | `string` |  |
| `sortOrder` | `integer` |  |

### CreateTextQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `knowledgeId` | `string` |  |
| `textType` | `string` |  |
| `answer` | `string` |  |
| `validationMode` | `string` |  |
| `alternativeAnswers` | `array` |  |
| `caseSensitive` | `boolean` |  |
| `regexPattern` | `string` |  |
| `scoringPoints` | `array` |  |
| `maxWordCount` | `integer` |  |
| `minWordCount` | `integer` |  |
| `essayQuestion` | `boolean` |  |
| `fillQuestion` | `boolean` |  |

### CreateBaseKnowledgeRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `question` | `string` |  |
| `explanation` | `string` |  |
| `categoryId` | `string` |  |
| `type` | `string` |  |
| `tags` | `array` |  |
| `difficulty` | `integer` |  |
| `estimatedTime` | `integer` |  |
| `status` | `string` |  |

### CreateCodeQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `knowledgeId` | `string` |  |
| `programmingLanguage` | `string` |  |
| `title` | `string` |  |
| `description` | `string` |  |
| `difficultyLevel` | `integer` |  |
| `points` | `integer` |  |
| `initialCode` | `string` |  |
| `hints` | `string` |  |
| `testCases` | `array` |  |
| `standardAnswer` | `string` |  |
| `hiddenTestCaseCount` | `integer` |  |
| `publicTestCaseCount` | `integer` |  |
| `validTestCasePointsConfiguration` | `boolean` |  |
| `validTestCasePoints` | `boolean` |  |
| `validTestCaseConfiguration` | `boolean` |  |

### CreateChoiceQuestionDataRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `knowledgeId` | `string` |  |
| `choiceType` | `string` |  |
| `points` | `integer` |  |
| `partialCredit` | `boolean` |  |
| `randomOrder` | `boolean` |  |
| `options` | `array` |  |
| `explanation` | `string` |  |
| `validPartialCreditConfiguration` | `boolean` |  |
| `validOptionConfiguration` | `boolean` |  |

### CreateCategoryRequest

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `name` | `string` |  |
| `parentId` | `string` |  |
| `description` | `string` |  |
| `color` | `string` |  |
| `icon` | `string` |  |
| `sortOrder` | `integer` |  |

### Pageable

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `page` | `integer` |  |
| `size` | `integer` |  |
| `sort` | `array` |  |

## 🔗 相关链接

- **Swagger UI**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)
- **健康检查**: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)

## 📝 使用示例

### 认证方式
`ash
# 如果需要认证，请在请求头中添加JWT Token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     http://localhost:8082/api/endpoint
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

