# Memorin SDK 使用指南

## 概述

Memorin SDK 是一个面向前端开发的 TypeScript/JavaScript 库，提供了对 Memorin 知识服务的完整封装。通过简洁的 API，开发者可以轻松实现分类管理、知识点创建、题目管理等核心功能。

## 快速开始

### 1. 安装

```bash
# 直接复制使用（无需安装）
# SDK代码位于: frontend/sdk/memorin-sdk-demo.ts
```

### 2. 基本使用

```typescript
import { MemorinaAPI } from './memorin-sdk-demo.ts'

// 创建API实例
const api = new MemorinaAPI('http://localhost:8082')

// 检查服务状态
const isHealthy = await api.healthCheck()
if (isHealthy) {
  console.log('✅ 服务连接成功')
} else {
  console.error('❌ 服务不可用')
}
```

## 核心功能

### 1. 分类管理 (CategoryManager)

#### 获取所有分类
```typescript
const categories = await api.categories.getAll()
console.log('分类列表:', categories)
```

#### 创建分类
```typescript
const newCategory = await api.categories.create({
  name: "前端开发",
  description: "前端技术相关知识点",
  // icon 和 color 会自动生成
})
```

#### 按名称查找分类
```typescript
const jsCategory = await api.categories.findByName("JavaScript")
if (jsCategory) {
  console.log('找到分类:', jsCategory)
}
```

#### 创建子分类
```typescript
const childCategory = await api.categories.create({
  name: "Vue3",
  parentName: "前端开发",  // 自动关联父分类
  description: "Vue3相关知识点"
})
```

### 2. 知识点管理 (KnowledgeManager)

#### 创建知识点
```typescript
const knowledge = await api.knowledge.create({
  question: "什么是闭包？",
  explanation: "闭包是指有权访问另一个函数作用域中变量的函数",
  type: "text",  // text | choice | code
  categoryName: "JavaScript",  // 直接使用分类名称
  tags: ["JavaScript", "函数式编程"],
  difficulty: 3,
  estimatedTime: 20,
  status: "published"  // draft | published
})
```

#### 获取特定类型的知识点
```typescript
// 获取所有文本题
const textKnowledge = await api.knowledge.getByType('text')

// 获取所有编程题
const codeKnowledge = await api.knowledge.getByType('code')

// 获取所有选择题
const choiceKnowledge = await api.knowledge.getByType('choice')
```

#### 获取所有知识点
```typescript
const allKnowledge = await api.knowledge.getAll()
```

### 3. 文本题管理 (TextQuestionManager)

#### 创建填空题（一行代码）
```typescript
const fillQuestion = await api.textQuestions.createFillQuestion(
  "闭包",  // 正确答案
  ["closure", "闭包函数"]  // 可选的替代答案
)
```

#### 创建问答题
```typescript
const essayQuestion = await api.textQuestions.createEssayQuestion(
  "JavaScript",  // 分类名称
  "解释JavaScript的事件循环机制",  // 问题
  "事件循环是JavaScript处理异步操作的机制..."  // 答案
)
```

#### 高级创建（自定义配置）
```typescript
const customQuestion = await api.textQuestions.create({
  categoryName: "前端开发",
  knowledgeQuestion: "什么是虚拟DOM？",
  answer: "虚拟DOM是真实DOM在内存中的轻量级表示",
  alternatives: ["Virtual DOM", "虚拟节点"],
  caseSensitive: false,
  type: "essay",
  validation: "contains"
})
```

## 实际应用示例

### 示例1：创建学习路径

```typescript
async function createLearningPath() {
  try {
    // 1. 创建学习分类
    const jsCategory = await api.categories.create({
      name: "JavaScript基础",
      description: "JavaScript核心概念学习"
    })

    // 2. 创建相关知识点
    const concepts = [
      "变量声明与作用域",
      "函数与闭包",
      "原型与继承",
      "异步编程",
      "事件处理"
    ]

    for (const concept of concepts) {
      await api.knowledge.create({
        question: `什么是${concept}？`,
        explanation: `详细解释${concept}的概念和应用`,
        type: "text",
        categoryName: "JavaScript基础",
        tags: ["JavaScript", concept],
        difficulty: Math.floor(Math.random() * 3) + 2, // 2-4难度
        estimatedTime: 15
      })
    }

    console.log('✅ 学习路径创建完成')

  } catch (error) {
    console.error('创建失败:', error.message)
  }
}
```

### 示例2：批量导入题目

```typescript
async function importQuestions(questions: Array<{
  category: string
  question: string
  answer: string
  type: 'fill' | 'essay'
}>) {
  const results = []
  
  for (const q of questions) {
    try {
      const result = await api.textQuestions.create({
        categoryName: q.category,
        knowledgeQuestion: q.question,
        answer: q.answer,
        type: q.type
      })
      results.push({ success: true, data: result })
    } catch (error) {
      results.push({ success: false, error: error.message })
    }
  }
  
  console.log(`导入完成: ${results.filter(r => r.success).length}/${results.length} 成功`)
  return results
}

// 使用示例
const questions = [
  { category: "HTML", question: "什么是语义化HTML？", answer: "使用具有明确含义的HTML标签", type: "essay" },
  { category: "CSS", question: "Flexbox的主要属性有哪些？", answer: "display, flex-direction, justify-content, align-items", type: "fill" }
]

await importQuestions(questions)
```

### 示例3：智能分类生成

```typescript
async function autoGenerateCategories(topics: string[]) {
  const generated = []
  
  for (const topic of topics) {
    try {
      const category = await api.categories.create({
        name: topic,
        description: `${topic}相关技术知识点`
      })
      generated.push(category)
    } catch (error) {
      if (error.message.includes('已存在')) {
        console.log(`${topic} 分类已存在，跳过`)
      } else {
        throw error
      }
    }
  }
  
  return generated
}

// 使用示例
const topics = ["React", "Vue", "Angular", "TypeScript", "Node.js"]
const categories = await autoGenerateCategories(topics)
```

## 错误处理

### 基础错误处理

```typescript
try {
  const result = await api.knowledge.create({
    // ... 创建参数
  })
} catch (error) {
  if (error instanceof MemorinaAPIError) {
    switch (error.status) {
      case 400:
        console.error('参数错误:', error.message)
        break
      case 404:
        console.error('资源不存在:', error.message)
        break
      case 500:
        console.error('服务器错误，请稍后重试')
        break
      default:
        console.error('未知错误:', error.message)
    }
  } else {
    console.error('网络错误:', error.message)
  }
}
```

### 使用错误处理工具

```typescript
import { handleAPIError } from './memorin-sdk-demo.ts'

try {
  await api.knowledge.create(invalidData)
} catch (error) {
  const message = handleAPIError(error)
  console.error('处理后的错误信息:', message)
}
```

## 高级功能

### 1. 缓存管理

SDK内置了1分钟的分类缓存，减少重复请求：

```typescript
// 第一次调用会请求API
const categories1 = await api.categories.getAll()

// 1分钟内再次调用会使用缓存
const categories2 = await api.categories.getAll()  // 使用缓存
```

### 2. 智能默认值

```typescript
// 所有可选参数都有智能默认值
const minimalKnowledge = await api.knowledge.create({
  question: "最小配置知识点",
  explanation: "使用默认值的示例",
  type: "text",
  categoryName: "测试分类"
  // difficulty: 3 (默认)
  // estimatedTime: 15 (默认)
  // tags: [] (默认)
  // status: "published" (默认)
})
```

### 3. 批量操作

```typescript
async function batchCreateKnowledge(items: Array<{
  question: string
  explanation: string
  categoryName: string
}>) {
  const results = []
  
  // 串行处理，避免并发问题
  for (const item of items) {
    try {
      const result = await api.knowledge.create({
        ...item,
        type: "text"
      })
      results.push({ success: true, data: result })
    } catch (error) {
      results.push({ success: false, error: error.message, item })
    }
  }
  
  return results
}
```

## 测试与验证

### 1. 在线测试

访问测试页面：
```
http://localhost:8082 的前端测试页面
文件位置: frontend/sdk/test-sdk.html
```

### 2. 命令行测试

```bash
# 测试服务状态
curl http://localhost:8082/api/test/health

# 测试分类获取
curl http://localhost:8082/api/test/categories

# 测试知识点获取
curl http://localhost:8082/api/test/knowledge
```

### 3. 代码测试

```typescript
// 快速验证
async function validateSDK() {
  const api = new MemorinaAPI()
  
  // 基本连接测试
  const healthy = await api.healthCheck()
  console.log('服务状态:', healthy ? '正常' : '异常')
  
  if (healthy) {
    // 数据获取测试
    const categories = await api.categories.getAll()
    console.log('分类数量:', categories.length)
    
    const knowledge = await api.knowledge.getAll()
    console.log('知识点数量:', knowledge.length)
    
    return { healthy, categoryCount: categories.length, knowledgeCount: knowledge.length }
  }
  
  return { healthy, categoryCount: 0, knowledgeCount: 0 }
}

validateSDK().then(console.log)
```

## 最佳实践

### 1. 错误重试机制

```typescript
async function retryOperation(operation, maxRetries = 3) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await operation()
    } catch (error) {
      if (i === maxRetries - 1) throw error
      console.log(`重试 ${i + 1}/${maxRetries}...`)
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)))
    }
  }
}

// 使用示例
const knowledge = await retryOperation(() => 
  api.knowledge.create({
    question: "重试机制测试",
    explanation: "测试重试功能",
    type: "text",
    categoryName: "测试"
  })
)
```

### 2. 数据验证

```typescript
function validateKnowledgeData(data) {
  const errors = []
  
  if (!data.question || data.question.length > 2000) {
    errors.push('问题内容不能为空且不能超过2000字符')
  }
  
  if (!data.categoryName) {
    errors.push('分类名称不能为空')
  }
  
  if (data.difficulty && (data.difficulty < 1 || data.difficulty > 5)) {
    errors.push('难度必须在1-5之间')
  }
  
  return errors
}

// 使用验证
const errors = validateKnowledgeData(newKnowledge)
if (errors.length > 0) {
  console.error('验证失败:', errors)
}
```

### 3. 性能优化

```typescript
// 批量操作优化
async function optimizedBatchCreate(items) {
  const batchSize = 10
  const results = []
  
  for (let i = 0; i < items.length; i += batchSize) {
    const batch = items.slice(i, i + batchSize)
    const batchResults = await Promise.allSettled(
      batch.map(item => api.knowledge.create(item))
    )
    
    results.push(...batchResults)
    
    // 批次间小延迟，避免服务器压力
    if (i + batchSize < items.length) {
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  }
  
  return results
}
```

## 常见问题

### Q1: 如何处理分类不存在的问题？

```typescript
try {
  await api.knowledge.create({
    categoryName: "不存在的分类"
  })
} catch (error) {
  if (error.message.includes('不存在')) {
    // 自动创建分类
    await api.categories.create({ name: "不存在的分类" })
    // 重试创建知识点
  }
}
```

### Q2: 如何获取特定分类的所有知识点？

```typescript
const category = await api.categories.findByName("JavaScript")
if (category) {
  const knowledge = await api.knowledge.getAll()
  const categoryKnowledge = knowledge.filter(k => k.categoryId === category.id)
}
```

### Q3: 如何处理大量数据？

```typescript
// 使用分页获取
async function getAllKnowledge() {
  let allKnowledge = []
  let page = 0
  
  while (true) {
    const result = await api.client.get(`/api/test/knowledge?page=${page}&size=100`)
    if (result.data.length === 0) break
    
    allKnowledge.push(...result.data)
    page++
  }
  
  return allKnowledge
}
```

## 完整示例项目

```typescript
// 创建一个完整的学习管理系统
class LearningManagementSystem {
  constructor(private api: MemorinaAPI) {}
  
  async createCourse(courseData) {
    // 创建课程分类
    const courseCategory = await this.api.categories.create({
      name: courseData.title,
      description: courseData.description
    })
    
    // 创建课程知识点
    for (const chapter of courseData.chapters) {
      for (const concept of chapter.concepts) {
        await this.api.knowledge.create({
          question: concept.question,
          explanation: concept.explanation,
          type: concept.type,
          categoryName: courseData.title,
          tags: [courseData.title, chapter.title],
          difficulty: concept.difficulty
        })
      }
    }
  }
}

// 使用示例
const lms = new LearningManagementSystem(api)
await lms.createCourse({
  title: "JavaScript高级编程",
  description: "深入理解JavaScript核心概念",
  chapters: [
    {
      title: "闭包与作用域",
      concepts: [
        { question: "什么是闭包？", explanation: "...", type: "text", difficulty: 3 }
      ]
    }
  ]
})
```

## 总结

Memorin SDK 提供了：

1. **简洁的API** - 一行代码完成复杂操作
2. **智能默认值** - 减少配置工作
3. **完整的错误处理** - 提供友好的错误信息
4. **类型安全** - TypeScript完整支持
5. **缓存优化** - 自动管理缓存
6. **批量操作** - 支持高效批量处理

通过这套SDK，开发者可以专注于业务逻辑，无需关心底层API细节。