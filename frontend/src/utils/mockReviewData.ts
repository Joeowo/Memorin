import type { ReviewQuestion, ReviewSession, AnswerResult } from '@/types/review'

// 模拟题目数据
export const mockQuestions: ReviewQuestion[] = [
  {
    id: 'q1',
    knowledgeId: 'k1',
    questionType: 'text',
    question: 'Vue 3 中用于创建响应式数据的函数是什么？',
    correctAnswer: 'ref',
    explanation: 'Vue 3 中使用 ref() 函数来创建响应式的基本类型数据，使用 reactive() 来创建响应式的对象。',
    difficulty: 2,
    estimatedTime: 30,
    category: '前端开发',
    tags: ['Vue', 'Composition API', '响应式'],
    reviewData: {
      easeFactor: 2.5,
      interval: 1,
      repetitions: 0,
      lastReviewDate: new Date(),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      totalReviews: 0,
      correctReviews: 0
    }
  },
  {
    id: 'q2',
    knowledgeId: 'k2',
    questionType: 'choice',
    question: '以下哪个选项是 TypeScript 的基本数据类型？',
    options: ['string', 'array', 'object', 'function'],
    correctAnswer: 'string',
    explanation: 'TypeScript 的基本数据类型包括：string、number、boolean、null、undefined、symbol、bigint。array、object、function 都是复合类型。',
    difficulty: 1,
    estimatedTime: 20,
    category: '前端开发',
    tags: ['TypeScript', '数据类型'],
    reviewData: {
      easeFactor: 2.5,
      interval: 1,
      repetitions: 0,
      lastReviewDate: new Date(),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      totalReviews: 0,
      correctReviews: 0
    }
  },
  {
    id: 'q3',
    knowledgeId: 'k3',
    questionType: 'code',
    question: '请写一个 JavaScript 函数，计算数组中所有数字的和。',
    correctAnswer: 'function sum(arr) { return arr.reduce((a, b) => a + b, 0); }',
    explanation: '使用 reduce 方法是计算数组元素和的最优雅方式。也可以使用 for 循环或者其他方法。',
    difficulty: 3,
    estimatedTime: 120,
    category: 'JavaScript',
    tags: ['JavaScript', '数组', '函数'],
    reviewData: {
      easeFactor: 2.5,
      interval: 1,
      repetitions: 0,
      lastReviewDate: new Date(),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      totalReviews: 0,
      correctReviews: 0
    }
  },
  {
    id: 'q4',
    knowledgeId: 'k4',
    questionType: 'choice',
    question: 'CSS Flexbox 中，justify-content 属性的默认值是什么？',
    options: ['flex-start', 'center', 'space-between', 'space-around'],
    correctAnswer: 'flex-start',
    explanation: 'justify-content 属性控制主轴上项目的对齐方式，默认值是 flex-start，表示项目对齐到主轴的起始位置。',
    difficulty: 2,
    estimatedTime: 25,
    category: 'CSS',
    tags: ['CSS', 'Flexbox', '布局'],
    reviewData: {
      easeFactor: 2.5,
      interval: 1,
      repetitions: 0,
      lastReviewDate: new Date(),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      totalReviews: 0,
      correctReviews: 0
    }
  },
  {
    id: 'q5',
    knowledgeId: 'k5',
    questionType: 'text',
    question: 'HTTP 状态码 404 表示什么？',
    correctAnswer: 'Not Found',
    explanation: 'HTTP 404 状态码表示服务器无法找到请求的资源。这是一个客户端错误状态码。',
    difficulty: 1,
    estimatedTime: 15,
    category: 'Web开发',
    tags: ['HTTP', '状态码', 'Web'],
    reviewData: {
      easeFactor: 2.5,
      interval: 1,
      repetitions: 0,
      lastReviewDate: new Date(),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      totalReviews: 0,
      correctReviews: 0
    }
  }
]

// 生成模拟复习会话
export function createMockSession(mode: 'daily' | 'quick' | 'weakness' | 'mistake', questionCount: number = 5): ReviewSession {
  const selectedQuestions = mockQuestions.slice(0, Math.min(questionCount, mockQuestions.length))
  
  return {
    id: `session_${Date.now()}`,
    userId: 'USER_001',
    mode,
    status: 'created',
    totalQuestions: selectedQuestions.length,
    currentQuestionIndex: 0,
    correctAnswers: 0,
    startTime: new Date(),
    questions: selectedQuestions,
    settings: {
      maxQuestions: selectedQuestions.length,
      timeLimit: mode === 'quick' ? 600 : undefined, // 快速模式10分钟限制
      showCorrectAnswer: true,
      autoNext: false,
      categories: ['前端开发', 'JavaScript', 'CSS', 'Web开发'],
      difficulty: mode === 'weakness' ? 4 : undefined
    }
  }
}

// 模拟答题结果
export function createMockAnswerResult(isCorrect: boolean, timeTaken: number, quality: number): AnswerResult {
  const baseResult = {
    isCorrect,
    correctAnswer: 'Mock Answer',
    explanation: '这是一个模拟的答案解释，用于测试复习系统功能。',
    quality: quality as 0 | 1 | 2 | 3 | 4 | 5,
    timeTaken
  }

  // 模拟 SM-2 算法结果
  if (isCorrect) {
    return {
      ...baseResult,
      newEaseFactor: 2.5 + (quality - 3) * 0.1,
      newInterval: Math.max(1, Math.floor(1 * (2.5 + (quality - 3) * 0.1))),
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000) // 明天
    }
  } else {
    return {
      ...baseResult,
      newEaseFactor: Math.max(1.3, 2.5 - 0.8),
      newInterval: 1,
      nextReviewDate: new Date(Date.now() + 24 * 60 * 60 * 1000) // 明天重复
    }
  }
}

// 模拟数据延迟
export function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// 模拟API错误
export class MockAPIError extends Error {
  constructor(message: string, public status: number = 500) {
    super(message)
    this.name = 'MockAPIError'
  }
}

// 模拟成功率（用于测试不同场景）
export const mockSuccessRate = {
  createSession: 0.95,      // 95% 成功率
  submitAnswer: 0.98,       // 98% 成功率
  loadData: 0.90            // 90% 成功率
}

// 模拟网络延迟
export const mockNetworkDelay = {
  fast: () => Math.random() * 100 + 50,       // 50-150ms
  normal: () => Math.random() * 300 + 200,    // 200-500ms
  slow: () => Math.random() * 1000 + 500      // 500-1500ms
}

// 检查是否应该模拟错误
export function shouldMockError(successRate: number): boolean {
  return Math.random() > successRate
}

// 获取随机模拟延迟
export function getRandomDelay(): number {
  const delays = Object.values(mockNetworkDelay)
  const randomDelay = delays[Math.floor(Math.random() * delays.length)]
  return randomDelay()
} 