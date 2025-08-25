// 复习服务API接口
// 第一阶段：基础API测试

const BASE_URL = '/api/review'
const TEST_USER_ID = 'USER_001'  // 改为与后端数据一致

// API响应基础类型
interface ApiResponse<T> {
  data: T
  success: boolean
  message: string
  timestamp: number
  count?: number
}

// 后端会话数据结构（原始）
interface BackendSessionRaw {
  id: string
  userId: string
  reviewMode: string
  sessionConfig: string
  totalQuestions: number
  currentIndex: number
  completedCount: number
  correctCount: number
  status: string
  startTime: string | null
  endTime: string | null
  questionList: string // JSON字符串，需要解析
  notes: string | null
  isActive: boolean
  createdAt: number
  updatedAt: number
  progressPercentage: number
  accuracyRate: number
  pauseTime?: string | null
  totalPauseDuration?: number
  estimatedDurationMinutes?: number
  actualDurationMinutes?: number
  remainingQuestions?: number
  wrongCount?: number
  estimatedRemainingMinutes?: number
  currentQuestion?: unknown
  statistics?: unknown
}

// 处理后的会话数据结构
interface BackendSession {
  id: string
  userId: string
  reviewMode: string
  sessionConfig: string
  totalQuestions: number
  currentIndex: number
  completedCount: number
  correctCount: number
  status: string
  startTime: string | null
  endTime: string | null
  questionList: Array<{
    knowledgePointId: string
    questionType: string
  }>
  notes: string | null
  isActive: boolean
  createdAt: number
  updatedAt: number
  progressPercentage: number
  accuracyRate: number
}

// 创建会话请求
interface CreateSessionRequest {
  reviewMode: string
  knowledgeBaseId?: string
  targetQuestionCount: number
  onlyDueQuestions?: boolean
  autoStart?: boolean
}

// 提交答案请求
interface SubmitAnswerRequest {
  sessionId: string
  knowledgePointId: string
  questionIndex: number
  userAnswer: string
  isCorrect: boolean
  qualityRating: number
  timeSpentSeconds: number
  mistakeReason?: string
  studyNotes?: string
  perceivedDifficulty?: number
  isSkipped?: boolean
  submissionType?: string
  questionType?: string
}

export class ReviewService {
  // 数据转换：将后端原始数据转换为前端使用的格式
  private static transformSession(rawSession: BackendSessionRaw): BackendSession {
    let questionList: Array<{ knowledgePointId: string; questionType: string }> = []
    
    try {
      if (rawSession.questionList) {
        questionList = JSON.parse(rawSession.questionList)
      }
    } catch (error) {
      console.warn('解析questionList失败:', error)
      questionList = []
    }

    return {
      id: rawSession.id,
      userId: rawSession.userId,
      reviewMode: rawSession.reviewMode,
      sessionConfig: rawSession.sessionConfig,
      totalQuestions: rawSession.totalQuestions,
      currentIndex: rawSession.currentIndex,
      completedCount: rawSession.completedCount,
      correctCount: rawSession.correctCount,
      status: rawSession.status,
      startTime: rawSession.startTime,
      endTime: rawSession.endTime,
      questionList,
      notes: rawSession.notes,
      isActive: rawSession.isActive,
      createdAt: rawSession.createdAt,
      updatedAt: rawSession.updatedAt,
      progressPercentage: rawSession.progressPercentage,
      accuracyRate: rawSession.accuracyRate
    }
  }

  // 测试API连接
  static async testConnection(): Promise<boolean> {
    try {
      const response = await fetch(`${BASE_URL}/algorithm/test`)
      const result = await response.json()
      return result.success === true
    } catch (error) {
      console.error('复习服务连接测试失败:', error)
      return false
    }
  }

  // 获取用户会话列表
  static async getUserSessions(): Promise<BackendSession[]> {
    try {
      const response = await fetch(`${BASE_URL}/sessions`)
      const result: ApiResponse<BackendSessionRaw[]> = await response.json()
      
      if (result.success) {
        // 转换数据格式
        return result.data.map(rawSession => this.transformSession(rawSession))
      } else {
        throw new Error(result.message || '获取会话列表失败')
      }
    } catch (error) {
      console.error('获取会话列表失败:', error)
      throw error
    }
  }

  // 创建复习会话
  static async createSession(request: CreateSessionRequest): Promise<BackendSession> {
    try {
      const response = await fetch(`${BASE_URL}/sessions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
      })

      const result: ApiResponse<BackendSessionRaw> = await response.json()
      
      if (result.success) {
        // 转换数据格式
        return this.transformSession(result.data)
      } else {
        throw new Error(result.message || '创建会话失败')
      }
    } catch (error) {
      console.error('创建会话失败:', error)
      throw error
    }
  }

  // 开始会话
  static async startSession(sessionId: string): Promise<BackendSession> {
    try {
      const response = await fetch(`${BASE_URL}/sessions/${sessionId}/start`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        }
      })

      const result: ApiResponse<BackendSessionRaw> = await response.json()
      
      if (result.success) {
        // 转换数据格式
        return this.transformSession(result.data)
      } else {
        throw new Error(result.message || '开始会话失败')
      }
    } catch (error) {
      console.error('开始会话失败:', error)
      throw error
    }
  }

  // 提交答案
  static async submitAnswer(request: SubmitAnswerRequest): Promise<ApiResponse<unknown>> {
    try {
      const response = await fetch(`${BASE_URL}/sessions/${request.sessionId}/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
      })

      const result = await response.json()
      
      if (response.ok) {
        return result
      } else {
        throw new Error(result.message || '提交答案失败')
      }
    } catch (error) {
      console.error('提交答案失败:', error)
      throw error
    }
  }

  // 获取错题统计
  static async getMistakeStatistics(userId: string = TEST_USER_ID): Promise<unknown> {
    try {
      const response = await fetch(`${BASE_URL}/mistakes/user/${userId}/statistics`)
      const result = await response.json()
      
      if (response.ok) {
        return result.data
      } else {
        throw new Error(result.message || '获取错题统计失败')
      }
    } catch (error) {
      console.error('获取错题统计失败:', error)
      throw error
    }
  }
}

export type {
  BackendSession,
  CreateSessionRequest,
  SubmitAnswerRequest,
  ApiResponse
} 