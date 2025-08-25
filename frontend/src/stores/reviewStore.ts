import { defineStore } from 'pinia'
import type { 
  ReviewState, 
  CreateSessionRequest,
  SubmitAnswerRequest,
  AnswerResult,
  SessionSettings,
  ReviewMode,
  QuestionType,
  AnswerQuality
} from '@/types/review'
import { 
  createMockSession, 
  createMockAnswerResult, 
  delay, 
  shouldMockError, 
  mockSuccessRate,
  getRandomDelay
} from '@/utils/mockReviewData'
import { ReviewService } from '@/services/reviewService'

// 默认配置
const DEFAULT_SETTINGS: SessionSettings = {
  maxQuestions: 20,
  timeLimit: undefined,
  showCorrectAnswer: true,
  autoNext: false,
  categories: [],
  difficulty: undefined
}

// 测试用户ID (与后端保持一致)
const TEST_USER_ID = 'USER_001'

// 是否使用模拟数据 (当后端不可用时自动切换)
const USE_MOCK_DATA = import.meta.env.DEV || false

export const useReviewStore = defineStore('review', {
  state: (): ReviewState => ({
    // 当前会话
    currentSession: null,
    currentQuestion: null,
    currentAnswer: '',
    
    // 历史会话
    recentSessions: [],
    
    // 复习队列
    reviewQueue: null,
    queueStatistics: null,
    
    // 统计数据
    statistics: null,
    mistakes: null,
    
    // UI状态
    loading: false,
    error: null,
    showAnswer: false,
    sessionInProgress: false,
    
    // 设置
    defaultSettings: { ...DEFAULT_SETTINGS }
  }),
  
  getters: {
    // 当前题目索引
    currentQuestionIndex(): number {
      return this.currentSession?.currentQuestionIndex || 0
    },
    
    // 总题目数
    totalQuestions(): number {
      return this.currentSession?.totalQuestions || 0
    },
    
    // 当前进度百分比
    progressPercentage(): number {
      if (!this.currentSession || this.totalQuestions === 0) return 0
      return Math.round((this.currentQuestionIndex / this.totalQuestions) * 100)
    },
    
    // 当前准确率
    currentAccuracy(): number {
      if (!this.currentSession || this.currentQuestionIndex === 0) return 0
      return Math.round((this.currentSession.correctAnswers / this.currentQuestionIndex) * 100)
    },
    
    // 是否有活跃会话
    hasActiveSession(): boolean {
      return this.currentSession?.status === 'active'
    },
    
    // 今日复习统计
    todayStats(): {
      sessions: number
      questions: number
      correctAnswers: number
      accuracy: number
      studyTime: number
    } {
      return {
        sessions: this.statistics?.todaySessions || 0,
        questions: this.statistics?.todayQuestions || 0,
        correctAnswers: this.statistics?.todayCorrectAnswers || 0,
        accuracy: this.statistics?.todayQuestions 
          ? Math.round((this.statistics.todayCorrectAnswers / this.statistics.todayQuestions) * 100)
          : 0,
        studyTime: this.statistics?.todayStudyTime || 0
      }
    },
    
    // 复习队列摘要
    queueSummary(): {
      totalDue: number
      overdue: number
      newItems: number
      estimatedTime: number
    } | null {
      if (!this.reviewQueue) return null
      
      return {
        totalDue: this.reviewQueue.dueToday.length + this.reviewQueue.overdue.length,
        overdue: this.reviewQueue.overdue.length,
        newItems: this.reviewQueue.newItems.length,
        estimatedTime: this.queueStatistics?.estimatedTime || 0
      }
    }
  },
  
  actions: {
    // ========== 通用方法 ==========
    
    setLoading(loading: boolean) {
      this.loading = loading
    },
    
    setError(error: string | null) {
      this.error = error
    },
    
    clearError() {
      this.error = null
    },

    // ========== 模拟数据方法 ==========
    
    async createMockSession(request: Omit<CreateSessionRequest, 'userId'>) {
      await delay(getRandomDelay())
      
      if (shouldMockError(mockSuccessRate.createSession)) {
        throw new Error('模拟：创建会话失败')
      }
      
      const mockSession = createMockSession(request.mode, request.maxQuestions || 5)
      this.currentSession = mockSession
      this.sessionInProgress = true
      
      // 加载第一个问题
      if (this.currentSession?.questions && this.currentSession.questions.length > 0) {
        this.currentQuestion = this.currentSession.questions[0]
      }
      
      return this.currentSession
    },

    async submitMockAnswer(userAnswer: string, timeTaken: number, quality: number) {
      await delay(getRandomDelay())
      
      if (shouldMockError(mockSuccessRate.submitAnswer)) {
        throw new Error('模拟：提交答案失败')
      }
      
      if (!this.currentQuestion) return null
      
      const isCorrect = userAnswer.trim().toLowerCase() === this.currentQuestion.correctAnswer.trim().toLowerCase()
      const result = createMockAnswerResult(isCorrect, timeTaken, quality)
      
      // 更新会话状态
      if (isCorrect && this.currentSession) {
        this.currentSession.correctAnswers++
      }
      
      // 显示答案
      this.showAnswer = true
      
      return result
    },
    
    // ========== 复习会话管理 ==========
    
    async createSession(request: Omit<CreateSessionRequest, 'userId'>) {
      this.setLoading(true)
      this.clearError()
      
      try {
        // 如果使用模拟数据或后端不可用，使用模拟数据
        if (USE_MOCK_DATA) {
          console.log('🔄 使用模拟数据创建复习会话')
          return await this.createMockSession(request)
        }
        
        // 转换前端请求格式到后端API格式
        const backendRequest = {
          reviewMode: request.mode || 'daily',
          knowledgeBaseId: 'TEST_BASE_001',
          targetQuestionCount: request.maxQuestions || 5,
          onlyDueQuestions: false,
          autoStart: false
        }
        
        // 调用后端API创建会话
        const response = await ReviewService.createSession(backendRequest)
        
        // 将后端响应转换为前端格式
        this.currentSession = {
          id: response.id,
          userId: response.userId,
          mode: request.mode || 'daily',
          status: response.status as 'active' | 'paused' | 'completed',
          totalQuestions: response.totalQuestions,
          currentQuestionIndex: response.currentIndex,
          correctAnswers: response.correctCount,
          startTime: response.startTime ? new Date(response.startTime) : new Date(),
          questions: response.questionList.map((q, index) => ({
            id: q.knowledgePointId,
            knowledgeId: q.knowledgePointId,
            questionType: q.questionType as QuestionType,
            question: `模拟问题 ${index + 1}`,
            correctAnswer: '模拟答案',
            explanation: '模拟解释',
            difficulty: 3,
            estimatedTime: 30,
            category: '默认分类',
            tags: [],
            options: q.questionType === 'choice' ? ['选项A', '选项B', '选项C', '选项D'] : undefined
          })),
          settings: {
            maxQuestions: request.maxQuestions || 20,
            timeLimit: request.timeLimit,
            showCorrectAnswer: true,
            autoNext: false,
            categories: request.categories || [],
            difficulty: request.difficulty
          }
        }
        this.sessionInProgress = true
        
        // 加载第一个问题
        if (this.currentSession?.questions && this.currentSession.questions.length > 0) {
          this.currentQuestion = this.currentSession.questions[0]
        }
        
        return this.currentSession
      } catch (error) {
        // 网络错误，使用模拟数据
        console.log('🔄 网络错误，使用模拟数据:', error)
        return await this.createMockSession(request)
      } finally {
        this.setLoading(false)
      }
    },
    
    async startSession(sessionId: string) {
      this.setLoading(true)
      this.clearError()
      
      try {
        if (USE_MOCK_DATA || !this.currentSession) {
          // 模拟数据模式，直接标记为active
          if (this.currentSession) {
            this.currentSession.status = 'active'
          }
          return this.currentSession
        }

        const response = await fetch(`/api/review/sessions/${sessionId}/start`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (!response.ok) {
          // 后端不可用，使用本地状态
          if (this.currentSession) {
            this.currentSession.status = 'active'
          }
          return this.currentSession
        }
        
        const data = await response.json()
        this.currentSession = data.data || data
        this.sessionInProgress = true
        
        return this.currentSession
      } catch {
        // 网络错误，使用本地状态
        if (this.currentSession) {
          this.currentSession.status = 'active'
        }
        return this.currentSession
      } finally {
        this.setLoading(false)
      }
    },
    
    async pauseSession() {
      if (!this.currentSession) return
      
      try {
        if (USE_MOCK_DATA) {
          // 模拟数据模式
          this.currentSession.status = 'paused'
          return
        }

        const response = await fetch(`/api/review/sessions/${this.currentSession.id}/pause`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' }
        })
        
        if (!response.ok) {
          throw new Error(`暂停会话失败: ${response.status}`)
        }
        
        if (this.currentSession) {
          this.currentSession.status = 'paused'
        }
      } catch (error) {
        // 网络错误，使用本地状态
        if (this.currentSession) {
          this.currentSession.status = 'paused'
        }
        this.setError(`暂停会话失败: ${(error as Error).message}`)
      }
    },
    
    async completeSession() {
      if (!this.currentSession) return
      
      try {
        if (!USE_MOCK_DATA) {
          const response = await fetch(`/api/review/sessions/${this.currentSession.id}/complete`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
          })
          
          if (!response.ok) {
            console.log('完成会话API调用失败，使用本地处理')
          }
        }
        
        // 添加到历史记录
        if (this.currentSession) {
          this.recentSessions.unshift({ ...this.currentSession })
          if (this.recentSessions.length > 10) {
            this.recentSessions = this.recentSessions.slice(0, 10)
          }
        }
        
        // 清理当前会话
        this.currentSession = null
        this.currentQuestion = null
        this.currentAnswer = ''
        this.showAnswer = false
        this.sessionInProgress = false
        
        // 刷新统计数据
        await this.loadStatistics()
        
      } catch (error) {
        console.log('完成会话时出错，使用本地处理:', error)
        // 即使API失败，也要清理本地状态
        this.currentSession = null
        this.currentQuestion = null
        this.currentAnswer = ''
        this.showAnswer = false
        this.sessionInProgress = false
      }
    },
    
    // ========== 答题相关 ==========
    
    async submitAnswer(userAnswer: string, timeTaken: number, quality: number) {
      if (!this.currentSession || !this.currentQuestion) return null
      
      this.setLoading(true)
      this.clearError()
      
      try {
        // 如果使用模拟数据，返回模拟结果
        if (USE_MOCK_DATA) {
          return await this.submitMockAnswer(userAnswer, timeTaken, quality)
        }

        const request: SubmitAnswerRequest = {
          sessionId: this.currentSession.id,
          questionId: this.currentQuestion.id,
          userAnswer,
          timeTaken,
          quality: quality as AnswerQuality
        }
        
        const response = await fetch(`/api/review/sessions/${this.currentSession.id}/submit`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(request)
        })
        
        if (!response.ok) {
          // 后端不可用，使用模拟数据
          return await this.submitMockAnswer(userAnswer, timeTaken, quality)
        }
        
        const result: AnswerResult = await response.json()
        
        // 更新会话状态
        if (result.isCorrect) {
          this.currentSession.correctAnswers++
        }
        
        // 显示答案
        this.showAnswer = true
        
        return result
      } catch (error) {
        // 网络错误，使用模拟数据
        console.log('提交答案失败，使用模拟数据:', error)
        return await this.submitMockAnswer(userAnswer, timeTaken, quality)
      } finally {
        this.setLoading(false)
      }
    },
    
    nextQuestion() {
      if (!this.currentSession) return
      
      const nextIndex = this.currentSession.currentQuestionIndex + 1
      
      if (nextIndex >= this.currentSession.questions.length) {
        // 会话完成
        this.completeSession()
        return
      }
      
      // 移到下一题
      this.currentSession.currentQuestionIndex = nextIndex
      this.currentQuestion = this.currentSession.questions[nextIndex]
      this.currentAnswer = ''
      this.showAnswer = false
    },
    
    setCurrentAnswer(answer: string) {
      this.currentAnswer = answer
    },
    
    toggleShowAnswer() {
      this.showAnswer = !this.showAnswer
    },
    
    // ========== 数据加载 ==========
    
    async loadReviewQueue() {
      this.setLoading(true)
      this.clearError()
      
      try {
        // TODO: 实现复习队列API调用
        // 暂时使用模拟数据
        this.reviewQueue = {
          dueToday: [],
          overdue: [],
          newItems: [],
          upcoming: []
        }
        
        this.queueStatistics = {
          totalDue: 0,
          totalOverdue: 0,
          totalNew: 0,
          estimatedTime: 0,
          recommendedSessions: 0
        }
      } catch (error) {
        this.setError(`加载复习队列失败: ${(error as Error).message}`)
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadStatistics() {
      this.setLoading(true)
      this.clearError()
      
      try {
        // TODO: 实现统计数据API调用
        // 暂时使用模拟数据
        this.statistics = {
          totalSessions: this.recentSessions.length,
          totalQuestions: this.recentSessions.reduce((sum, session) => sum + session.totalQuestions, 0),
          correctAnswers: this.recentSessions.reduce((sum, session) => sum + session.correctAnswers, 0),
          averageAccuracy: this.recentSessions.length > 0 
            ? Math.round((this.recentSessions.reduce((sum, session) => sum + session.correctAnswers, 0) / 
                this.recentSessions.reduce((sum, session) => sum + session.totalQuestions, 0)) * 100)
            : 0,
          totalStudyTime: 0,
          streakDays: 0,
          todaySessions: this.recentSessions.filter(session => 
            new Date(session.startTime).toDateString() === new Date().toDateString()
          ).length,
          todayQuestions: 0,
          todayCorrectAnswers: 0,
          todayStudyTime: 0,
          weeklyProgress: [],
          monthlyProgress: []
        }
      } catch (error) {
        this.setError(`加载统计数据失败: ${(error as Error).message}`)
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadMistakes() {
      this.setLoading(true)
      this.clearError()
      
      try {
        if (USE_MOCK_DATA) {
          // 使用模拟数据
          this.mistakes = {
            totalMistakes: 0,
            unresolvedMistakes: 0,
            resolvedMistakes: 0,
            mostMistakeCategory: '',
            recentMistakes: [],
            highPriorityMistakes: []
          }
          return
        }

        const response = await fetch(`/api/review/mistakes/user/${TEST_USER_ID}/statistics`)
        
        if (!response.ok) {
          throw new Error(`获取错题统计失败: ${response.status}`)
        }
        
        const data = await response.json()
        this.mistakes = data.data || data
      } catch {
        // 使用模拟数据
        this.mistakes = {
          totalMistakes: 0,
          unresolvedMistakes: 0,
          resolvedMistakes: 0,
          mostMistakeCategory: '',
          recentMistakes: [],
          highPriorityMistakes: []
        }
      } finally {
        this.setLoading(false)
      }
    },
    
    async loadAllData() {
      await Promise.all([
        this.loadReviewQueue(),
        this.loadStatistics(),
        this.loadMistakes()
      ])
    },
    
    // ========== 快速开始 ==========
    
    async quickStart(mode: ReviewMode = 'daily') {
      const request: Omit<CreateSessionRequest, 'userId'> = {
        mode,
        maxQuestions: this.defaultSettings.maxQuestions,
        categories: this.defaultSettings.categories,
        difficulty: this.defaultSettings.difficulty
      }
      
      const session = await this.createSession(request)
      
      if (session) {
        await this.startSession(session.id)
      }
      
      return session
    },
    
    // ========== 设置管理 ==========
    
    updateDefaultSettings(settings: Partial<SessionSettings>) {
      this.defaultSettings = { ...this.defaultSettings, ...settings }
    },
    
    resetDefaultSettings() {
      this.defaultSettings = { ...DEFAULT_SETTINGS }
    }
  }
}) 