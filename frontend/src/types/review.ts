/**
 * 复习系统类型定义
 * 基于后端API和SM-2算法设计
 */

// ===== 基础类型 =====

export type ReviewMode = 'daily' | 'quick' | 'weakness' | 'mistake'

export type QuestionType = 'text' | 'choice' | 'code'

export type SessionStatus = 'created' | 'active' | 'paused' | 'completed' | 'cancelled'

export type AnswerQuality = 0 | 1 | 2 | 3 | 4 | 5

// ===== 复习会话相关 =====

export interface CreateSessionRequest {
  userId: string
  mode: ReviewMode
  maxQuestions?: number
  categories?: string[]
  difficulty?: number
  timeLimit?: number
}

export interface ReviewSession {
  id: string
  userId: string
  mode: ReviewMode
  status: SessionStatus
  totalQuestions: number
  currentQuestionIndex: number
  correctAnswers: number
  startTime: Date
  endTime?: Date
  questions: ReviewQuestion[]
  settings: SessionSettings
}

export interface SessionSettings {
  maxQuestions: number
  timeLimit?: number
  showCorrectAnswer: boolean
  autoNext: boolean
  categories: string[]
  difficulty?: number
}

// ===== 复习题目相关 =====

export interface ReviewQuestion {
  id: string
  knowledgeId: string
  questionType: QuestionType
  question: string
  options?: string[]           // 选择题选项
  correctAnswer: string
  explanation: string
  difficulty: number
  estimatedTime: number
  category: string
  tags: string[]
  
  // 复习状态
  reviewData?: ReviewData
}

export interface ReviewData {
  easeFactor: number
  interval: number
  repetitions: number
  lastReviewDate: Date
  nextReviewDate: Date
  totalReviews: number
  correctReviews: number
}

// ===== 答题相关 =====

export interface SubmitAnswerRequest {
  sessionId: string
  questionId: string
  userAnswer: string
  timeTaken: number           // 答题耗时(秒)
  quality: AnswerQuality      // 答题质量评分
}

export interface AnswerResult {
  isCorrect: boolean
  correctAnswer: string
  explanation: string
  quality: AnswerQuality
  timeTaken: number
  
  // SM-2算法结果
  newEaseFactor: number
  newInterval: number
  nextReviewDate: Date
}

// ===== SM-2算法相关 =====

export interface SM2CalculationRequest {
  knowledgeId: string
  quality: AnswerQuality
  easeFactor: number
  interval: number
  repetitions: number
}

export interface SM2CalculationResult {
  easeFactor: number
  interval: number
  repetitions: number
  nextReviewDate: Date
}

// ===== 复习统计相关 =====

export interface ReviewStatistics {
  totalSessions: number
  totalQuestions: number
  correctAnswers: number
  averageAccuracy: number
  totalStudyTime: number      // 总学习时间(分钟)
  streakDays: number          // 连续学习天数
  
  // 今日数据
  todaySessions: number
  todayQuestions: number
  todayCorrectAnswers: number
  todayStudyTime: number
  
  // 历史数据
  weeklyProgress: DailyProgress[]
  monthlyProgress: MonthlyProgress[]
}

export interface DailyProgress {
  date: string
  sessions: number
  questions: number
  correctAnswers: number
  studyTime: number
  accuracy: number
}

export interface MonthlyProgress {
  month: string
  totalSessions: number
  totalQuestions: number
  averageAccuracy: number
  totalStudyTime: number
}

// ===== 错题本相关 =====

export interface Mistake {
  id: string
  userId: string
  knowledgeId: string
  questionId: string
  userAnswer: string
  correctAnswer: string
  mistakeDate: Date
  reviewCount: number
  isResolved: boolean
  lastReviewDate?: Date
  
  // 关联数据
  question?: ReviewQuestion
  category?: string
}

export interface MistakeStatistics {
  totalMistakes: number
  unresolvedMistakes: number
  resolvedMistakes: number
  mostMistakeCategory: string
  recentMistakes: Mistake[]
  highPriorityMistakes: Mistake[]
}

// ===== 复习队列相关 =====

export interface ReviewQueue {
  dueToday: ReviewQuestion[]      // 今日到期
  overdue: ReviewQuestion[]       // 已过期
  newItems: ReviewQuestion[]      // 新学习
  upcoming: ReviewQuestion[]      // 即将到期
}

export interface QueueStatistics {
  totalDue: number
  totalOverdue: number
  totalNew: number
  estimatedTime: number           // 预计完成时间(分钟)
  recommendedSessions: number     // 建议会话数
}

// ===== API响应包装 =====

export interface ReviewApiResponse<T> {
  success: boolean
  data: T
  message?: string
  timestamp: Date
}

// ===== 组件Props相关 =====

export interface ReviewSessionProps {
  sessionId?: string
  mode: ReviewMode
  autoStart?: boolean
}

export interface QuestionRendererProps {
  question: ReviewQuestion
  showAnswer?: boolean
  disabled?: boolean
}

export interface AnswerInputProps {
  questionType: QuestionType
  options?: string[]
  disabled?: boolean
  placeholder?: string
}

// ===== Store状态相关 =====

export interface ReviewState {
  // 当前会话
  currentSession: ReviewSession | null
  currentQuestion: ReviewQuestion | null
  currentAnswer: string
  
  // 历史会话
  recentSessions: ReviewSession[]
  
  // 复习队列
  reviewQueue: ReviewQueue | null
  queueStatistics: QueueStatistics | null
  
  // 统计数据
  statistics: ReviewStatistics | null
  mistakes: MistakeStatistics | null
  
  // UI状态
  loading: boolean
  error: string | null
  showAnswer: boolean
  sessionInProgress: boolean
  
  // 设置
  defaultSettings: SessionSettings
} 