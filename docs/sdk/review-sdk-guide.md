# Memorin Review SDK 使用指南

## 概述

Memorin Review SDK 是一个面向前端开发的 TypeScript/JavaScript 库，提供了对 Memorin 复习服务的完整封装。通过简洁的 API，开发者可以轻松实现SM-2算法复习、错题管理、复习会话等核心功能。

## 快速开始

### 1. 安装

```bash
# 直接复制使用（无需安装）
# SDK代码位于: frontend/sdk/review-sdk-demo.ts
```

### 2. 基本使用

```typescript
import { ReviewServiceAPI } from './review-sdk-demo.ts'

// 创建API实例
const api = new ReviewServiceAPI('http://localhost:8083')

// 检查服务状态
const isHealthy = await api.healthCheck()
if (isHealthy) {
  console.log('✅ 复习服务连接成功')
} else {
  console.error('❌ 复习服务不可用')
}
```

## 核心功能

### 1. 复习会话管理 (ReviewSessionManager)

#### 创建复习会话
```typescript
const session = await api.sessions.create({
  reviewMode: 'SM2',
  targetQuestionCount: 10,
  maxDurationMinutes: 30,
  notes: 'JavaScript基础复习'
}, 'user_001')
```

#### 获取用户会话
```typescript
// 获取所有会话
const sessions = await api.sessions.getAll('user_001')

// 获取活跃会话
const activeSessions = await api.sessions.getActive('user_001')

// 获取会话详情
const sessionDetail = await api.sessions.getById('RS_20250825_001')
```

#### 管理会话状态
```typescript
// 启动会话
await api.sessions.start('RS_20250825_001', 'user_001')

// 暂停会话
await api.sessions.pause('RS_20250825_001', 'user_001')

// 完成会话
await api.sessions.complete('RS_20250825_001', 'user_001')

// 取消会话
await api.sessions.cancel('RS_20250825_001', 'user_001')
```

#### 提交答案
```typescript
const result = await api.sessions.submitAnswer('RS_20250825_001', {
  knowledgePointId: 'KP_JS_001',
  qualityRating: 3, // 1=错误, 2=模糊, 3=正确
  timeSpentSeconds: 45,
  isSkipped: false,
  isCorrect: true,
  studyNotes: '掌握了闭包概念'
}, 'user_001')
```

### 2. 题目生成器 (QuestionGenerator)

#### 使用模板生成题目
```typescript
// 智能复习
const questions = await api.generator.generateSmartReview({
  userId: 'user_001',
  count: 20,
  onlyDue: true
})

// 错题复习
const mistakeQuestions = await api.generator.generateMistakeReview({
  baseId: 'JS_FOUNDATION',
  userId: 'user_001',
  limit: 10,
  random: false
})

// 弱项强化
const weaknessQuestions = await api.generator.generateWeaknessReview({
  userId: 'user_001',
  count: 15,
  baseId: 'JS_FOUNDATION'
})
```

#### 自定义生成题目
```typescript
const customQuestions = await api.generator.generate({
  userId: 'user_001',
  source: {
    type: 'knowledge-base',
    config: { baseId: 'JS_FOUNDATION' }
  },
  filters: [
    { type: 'by-difficulty', config: { min: 2, max: 4 } },
    { type: 'by-accuracy', config: { maxAccuracy: 0.8 } }
  ],
  sorter: { type: 'smart' },
  limiter: { type: 'fixed-count', config: { count: 10 } }
})
```

#### 获取可用策略
```typescript
const strategies = await api.generator.getStrategies()
console.log('可用数据源:', strategies.dataSources)
console.log('可用过滤器:', strategies.filters)
console.log('可用排序器:', strategies.sorters)
console.log('可用限制器:', strategies.limiters)
```

### 3. 错题管理 (MistakeManager)

#### 记录错题
```typescript
const mistake = await api.mistakes.add({
  userId: 'user_001',
  knowledgePointId: 'KP_JS_CLOSURE_001',
  mistakeReason: '闭包概念理解错误',
  questionType: 'choice',
  difficultyLevel: 'medium'
})
```

#### 获取错题信息
```typescript
// 获取所有错题
const allMistakes = await api.mistakes.getAll('user_001')

// 获取未解决错题
const unresolvedMistakes = await api.mistakes.getUnresolved('user_001')

// 获取已解决错题
const resolvedMistakes = await api.mistakes.getResolved('user_001')

// 获取高优先级错题
const highPriorityMistakes = await api.mistakes.getHighPriority('user_001')

// 获取最近错题
const recentMistakes = await api.mistakes.getRecent('user_001', 7)
```

#### 管理错题状态
```typescript
// 标记为已解决
await api.mistakes.resolve('user_001', 'KP_JS_CLOSURE_001')

// 重新标记为未解决
await api.mistakes.unresolve('user_001', 'KP_JS_CLOSURE_001')

// 删除错题记录
await api.mistakes.delete('user_001', 'KP_JS_CLOSURE_001')

// 清理已解决错题
await api.mistakes.cleanupResolved('user_001')
```

#### 获取错题统计
```typescript
const stats = await api.mistakes.getStatistics('user_001')
console.log(`总错题数: ${stats.totalMistakes}`)
console.log(`未解决: ${stats.unresolvedMistakes}`)
console.log(`已解决: ${stats.resolvedMistakes}`)
console.log(`解决率: ${stats.resolutionRate}`)
```

### 4. SM-2算法 (ReviewAlgorithm)

#### 测试算法
```typescript
const algorithmTest = await api.algorithm.test()
console.log('算法测试:', algorithmTest)
```

#### 计算复习参数
```typescript
const reviewParams = await api.algorithm.calculate({
  easeFactor: 2.5,
  interval: 7,
  quality: 3,
  userId: 'user_001'
})
```

## 实际应用示例

### 示例1：创建每日复习计划

```typescript
async function createDailyReviewPlan(userId: string) {
  try {
    // 1. 获取用户错题统计
    const stats = await api.mistakes.getStatistics(userId)
    
    // 2. 根据错题情况生成题目
    let questions
    if (stats.unresolvedMistakes > 5) {
      // 优先复习错题
      questions = await api.generator.generateMistakeReview({
        userId,
        limit: 10,
        random: false
      })
    } else {
      // 智能复习新知识
      questions = await api.generator.generateSmartReview({
        userId,
        count: 15,
        onlyDue: true
      })
    }
    
    // 3. 创建复习会话
    const session = await api.sessions.create({
      reviewMode: 'SM2',
      targetQuestionCount: questions.questions.length,
      maxDurationMinutes: 30,
      notes: '每日复习计划'
    }, userId)
    
    console.log('✅ 每日复习计划创建完成', session)
    return session
    
  } catch (error) {
    console.error('创建失败:', error.message)
  }
}
```

### 示例2：错题追踪系统

```typescript
class MistakeTracker {
  constructor(private api: ReviewServiceAPI) {}
  
  async trackMistake(userId: string, knowledgePointId: string, reason: string) {
    const mistake = await this.api.mistakes.add({
      userId,
      knowledgePointId,
      mistakeReason: reason,
      questionType: 'text',
      difficultyLevel: 'medium'
    })
    
    // 自动生成复习计划
    const reviewQuestions = await this.api.generator.generateMistakeReview({
      userId,
      limit: 1,
      random: false
    })
    
    return { mistake, reviewQuestions }
  }
  
  async getWeeklyReport(userId: string) {
    const stats = await this.api.mistakes.getStatistics(userId)
    const recentMistakes = await this.api.mistakes.getRecent(userId, 7)
    
    return {
      totalMistakes: stats.totalMistakes,
      resolutionRate: stats.resolutionRate,
      recentMistakes: recentMistakes.length,
      improvement: stats.resolutionRate > 0.8 ? '优秀' : '需要努力'
    }
  }
}
```

### 示例3：智能复习推荐

```typescript
async function getSmartRecommendation(userId: string) {
  const stats = await api.mistakes.getStatistics(userId)
  
  // 根据用户表现调整复习策略
  let strategy
  if (stats.resolutionRate < 0.5) {
    // 错题较多，重点复习错题
    strategy = {
      source: { type: 'all-mistakes' },
      filters: [{ type: 'by-accuracy', config: { maxAccuracy: 0.7 } }],
      sorter: { type: 'by-accuracy', config: { order: 'asc' } },
      limiter: { type: 'fixed-count', config: { count: 10 } }
    }
  } else {
    // 表现良好，混合复习
    strategy = {
      source: { type: 'all-knowledge' },
      filters: [{ type: 'due-for-review' }],
      sorter: { type: 'smart' },
      limiter: { type: 'smart-limit', config: { baseCount: 15 } }
    }
  }
  
  return await api.generator.generate(strategy)
}
```

### 示例4：批量操作优化

```typescript
async function batchProcessMistakes(mistakes: Array<{
  userId: string
  knowledgePointId: string
  reason: string
  type: string
}>) {
  const results = []
  
  // 串行处理，避免并发问题
  for (const mistake of mistakes) {
    try {
      const result = await api.mistakes.add(mistake)
      results.push({ success: true, data: result })
    } catch (error) {
      results.push({ success: false, error: error.message, mistake })
    }
  }
  
  return results
}

// 使用示例
const mistakes = [
  { userId: 'user_001', knowledgePointId: 'KP_JS_001', reason: '语法错误', type: 'choice' },
  { userId: 'user_001', knowledgePointId: 'KP_JS_002', reason: '逻辑错误', type: 'code' }
]

const results = await batchProcessMistakes(mistakes)
```

## 高级功能

### 1. 缓存管理

SDK内置了1分钟的策略缓存，减少重复请求：

```typescript
// 第一次调用会请求API
const strategies1 = await api.generator.getStrategies()

// 1分钟内再次调用会使用缓存
const strategies2 = await api.generator.getStrategies()  // 使用缓存
```

### 2. 智能默认值

```typescript
// 所有可选参数都有智能默认值
const minimalSession = await api.sessions.create({
  reviewMode: 'SM2',
  targetQuestionCount: 10
}, 'user_001')
// maxDurationMinutes: 30 (默认)
// notes: '' (默认)
```

### 3. 批量操作

```typescript
async function batchCreateSessions(sessions: Array<{
  mode: string
  count: number
  userId: string
}>) {
  const results = []
  
  for (const session of sessions) {
    try {
      const result = await api.sessions.create({
        reviewMode: session.mode,
        targetQuestionCount: session.count
      }, session.userId)
      results.push({ success: true, data: result })
    } catch (error) {
      results.push({ success: false, error: error.message, session })
    }
  }
  
  return results
}
```

## 测试与验证

### 1. 在线测试

访问测试页面：
```
http://localhost:8083 的前端测试页面
文件位置: frontend/sdk/test-review-sdk.html
```

### 2. 命令行测试

```bash
# 测试服务状态
curl http://localhost:8083/api/review/algorithm/test

# 测试会话创建
curl -X POST http://localhost:8083/api/review/sessions \
  -H "Content-Type: application/json" \
  -H "User-Id: test_user_001" \
  -d '{"reviewMode":"SM2","targetQuestionCount":5}'

# 测试错题管理
curl http://localhost:8083/api/review/mistakes/user/test_user_001

# 测试题目生成
curl http://localhost:8083/api/review/question-generator/strategies
```

### 3. 代码测试

```typescript
// 快速验证
async function validateReviewSDK() {
  const api = new ReviewServiceAPI()
  
  // 基本连接测试
  const healthy = await api.healthCheck()
  console.log('服务状态:', healthy ? '正常' : '异常')
  
  if (healthy) {
    // 数据获取测试
    const strategies = await api.generator.getStrategies()
    console.log('策略数量:', Object.keys(strategies).length)
    
    const testSession = await api.sessions.create({
      reviewMode: 'SM2',
      targetQuestionCount: 5
    }, 'USER_001')
    console.log('测试会话:', testSession.id)
    
    return { 
      healthy, 
      strategies: strategies.dataSources.length, 
      sessionId: testSession.id 
    }
  }
  
  return { healthy, strategies: 0, sessionId: null }
}

validateReviewSDK().then(console.log)
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
const session = await retryOperation(() => 
  api.sessions.create({
    reviewMode: 'SM2',
    targetQuestionCount: 10
  }, 'user_001')
)
```

### 2. 数据验证

```typescript
function validateSessionData(data) {
  const errors = []
  
  if (!data.reviewMode || !['SM2', 'smart-review', 'mistake-review'].includes(data.reviewMode)) {
    errors.push('复习模式必须是SM2、smart-review或mistake-review')
  }
  
  if (!data.targetQuestionCount || data.targetQuestionCount < 1 || data.targetQuestionCount > 100) {
    errors.push('题目数量必须在1-100之间')
  }
  
  return errors
}
```

### 3. 性能优化

```typescript
// 批量操作优化
async function optimizedBatchOperations(items) {
  const batchSize = 5
  const results = []
  
  for (let i = 0; i < items.length; i += batchSize) {
    const batch = items.slice(i, i + batchSize)
    const batchResults = await Promise.allSettled(
      batch.map(item => api.mistakes.add(item))
    )
    
    results.push(...batchResults)
    
    // 批次间小延迟
    if (i + batchSize < items.length) {
      await new Promise(resolve => setTimeout(resolve, 100))
    }
  }
  
  return results
}
```

## 常见问题

### Q1: 如何处理复习会话超时？

```typescript
try {
  await api.sessions.submitAnswer(sessionId, answer, userId)
} catch (error) {
  if (error.message.includes('会话已过期')) {
    // 创建新会话
    const newSession = await api.sessions.create({
      reviewMode: 'SM2',
      targetQuestionCount: remainingQuestions
    }, userId)
  }
}
```

### Q2: 如何获取特定复习模式的数据？

```typescript
// 获取SM2模式的复习数据
const sm2Data = await api.sessions.getAll('user_001', 'SM2')

// 获取智能复习数据
const smartData = await api.sessions.getAll('user_001', 'smart-review')
```

### Q3: 如何处理大量错题？

```typescript
// 使用分页获取
async function getAllMistakes(userId) {
  let allMistakes = []
  let page = 0
  
  while (true) {
    const result = await api.mistakes.getAll(userId)
    if (result.length === 0) break
    
    allMistakes.push(...result)
    page++
  }
  
  return allMistakes
}
```

## 完整示例项目

```typescript
// 创建一个智能复习管理系统
class SmartReviewSystem {
  constructor(private api: ReviewServiceAPI) {}
  
  async createDailyPlan(userId: string, preferences: any) {
    // 1. 分析用户数据
    const stats = await this.api.mistakes.getStatistics(userId)
    
    // 2. 生成个性化题目
    const questions = await this.generatePersonalizedQuestions(userId, stats, preferences)
    
    // 3. 创建复习会话
    const session = await this.api.sessions.create({
      reviewMode: 'SM2',
      targetQuestionCount: questions.length,
      maxDurationMinutes: preferences.dailyDuration || 30,
      notes: `每日复习 - ${new Date().toLocaleDateString()}`
    }, userId)
    
    return { session, questions }
  }
  
  private async generatePersonalizedQuestions(userId: string, stats: any, preferences: any) {
    if (stats.unresolvedMistakes > 10) {
      return await this.api.generator.generateMistakeReview({
        userId,
        limit: Math.min(stats.unresolvedMistakes, 15),
        random: false
      })
    }
    
    return await this.api.generator.generateSmartReview({
      userId,
      count: preferences.dailyQuestions || 15,
      onlyDue: true
    })
  }
  
  async trackProgress(userId: string, days: number = 7) {
    const stats = await this.api.mistakes.getStatistics(userId)
    const recentSessions = await this.api.sessions.getRecent(userId, days)
    
    return {
      totalMistakes: stats.totalMistakes,
      resolutionRate: stats.resolutionRate,
      completedSessions: recentSessions.length,
      averageAccuracy: this.calculateAverageAccuracy(recentSessions)
    }
  }
}

// 使用示例
const reviewSystem = new SmartReviewSystem(api)
await reviewSystem.createDailyPlan('user_001', {
  dailyDuration: 30,
  dailyQuestions: 20
})
```

## 总结

Memorin Review SDK 提供了：

1. **完整的SM-2算法** - 专业的间隔重复学习
2. **智能题目生成** - 23种策略组合
3. **完整的错题管理** - 生命周期追踪
4. **会话状态管理** - 暂停/恢复/完成
5. **实时统计分析** - 学习效果可视化
6. **批量操作支持** - 高效数据处理
7. **类型安全** - TypeScript完整支持
8. **错误处理** - 友好的错误信息

通过这套SDK，开发者可以专注于用户体验，无需关心底层算法和API细节。