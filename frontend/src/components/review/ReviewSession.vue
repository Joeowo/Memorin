<template>
  <div class="review-session">
    <!-- 复习进度 -->
    <ReviewProgress
      v-if="store.currentSession"
      :mode="store.currentSession.mode"
      :current-index="store.currentQuestionIndex"
      :total-questions="store.totalQuestions"
      :correct-count="store.currentSession.correctAnswers"
      :incorrect-count="incorrectCount"
      :elapsed-time="elapsedTime"
      :time-remaining="timeRemaining"
      :question-statuses="questionStatuses"
      @pause="handlePause"
      @quit="handleQuit"
      @jump-to-question="handleJumpToQuestion"
      class="session-progress"
    />

    <!-- 主要答题区域 -->
    <div class="session-content">
      <div v-if="store.currentQuestion" class="question-section">
        <!-- 统一答题卡片 -->
        <UnifiedQuestionCard
          ref="questionCardRef"
          :question="store.currentQuestion"
          :is-last-question="isLastQuestion"
          @submit-answer="handleSubmitAnswer"
          @next-question="handleNextQuestion"
          class="question-card"
        />
      </div>

      <!-- 无题目状态 -->
      <div v-else class="no-question">
        <div class="empty-state">
          <div class="empty-icon">📝</div>
          <h3>准备复习</h3>
          <p>正在加载题目，请稍候...</p>
          <LoadingSpinner />
        </div>
      </div>
    </div>

    <!-- 会话完成对话框 -->
    <div v-if="showCompletionDialog" class="completion-overlay">
      <div class="completion-dialog">
        <div class="completion-header">
          <h3>🎉 复习完成！</h3>
        </div>
        <div class="completion-content">
          <div class="completion-stats">
            <div class="stat-row">
              <span>总题数:</span>
              <span>{{ store.totalQuestions }}</span>
            </div>
            <div class="stat-row">
              <span>正确数:</span>
              <span class="correct">{{ store.currentSession?.correctAnswers || 0 }}</span>
            </div>
            <div class="stat-row">
              <span>错误数:</span>
              <span class="incorrect">{{ incorrectCount }}</span>
            </div>
            <div class="stat-row">
              <span>正确率:</span>
              <span class="accuracy">{{ finalAccuracy }}%</span>
            </div>
            <div class="stat-row">
              <span>用时:</span>
              <span>{{ formatTime(elapsedTime) }}</span>
            </div>
          </div>
        </div>
        <div class="completion-actions">
          <BaseButton
            @click="handleSessionComplete"
            variant="primary"
            size="large"
          >
            确认完成
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useReviewStore } from '@/stores/reviewStore'

// 组件导入
import UnifiedQuestionCard from './UnifiedQuestionCard.vue'
import ReviewProgress from './ReviewProgress.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

// Emits
const emit = defineEmits<{
  'session-complete': []
}>()

const store = useReviewStore()

// 本地状态
const startTime = ref(Date.now())
const questionStatuses = ref<('correct' | 'incorrect' | 'unanswered')[]>([])
const showCompletionDialog = ref(false)
const questionCardRef = ref<InstanceType<typeof UnifiedQuestionCard> | null>(null)

// 计算属性
const elapsedTime = computed(() => {
  return Math.floor((Date.now() - startTime.value) / 1000)
})

const timeRemaining = computed(() => {
  if (!store.currentSession?.settings.timeLimit) return undefined
  return Math.max(0, store.currentSession.settings.timeLimit - elapsedTime.value)
})

const incorrectCount = computed(() => {
  if (!store.currentSession) return 0
  return store.currentQuestionIndex - store.currentSession.correctAnswers
})

const isLastQuestion = computed(() => {
  return store.currentQuestionIndex >= store.totalQuestions - 1
})

const finalAccuracy = computed(() => {
  if (!store.currentSession || store.totalQuestions === 0) return 0
  return Math.round((store.currentSession.correctAnswers / store.totalQuestions) * 100)
})

// 生命周期
onMounted(() => {
  initializeSession()
  document.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
})

// 初始化会话
function initializeSession() {
  if (store.currentSession) {
    // 初始化题目状态
    questionStatuses.value = new Array(store.totalQuestions).fill('unanswered')
  }
}

// 事件处理
async function handleSubmitAnswer(answer: string) {
  if (!store.currentQuestion) return

  try {
    // 计算答题时间和质量
    const timeTaken = Math.floor((Date.now() - startTime.value) / 1000)
    const quality = calculateAnswerQuality(answer, store.currentQuestion.correctAnswer, timeTaken)
    
    // 提交答案到store
    const result = await store.submitAnswer(answer, timeTaken, quality)
    
    if (result && questionCardRef.value) {
      // 更新题目状态
      questionStatuses.value[store.currentQuestionIndex] = result.isCorrect ? 'correct' : 'incorrect'
      
      // 将结果传递给答题卡片
      questionCardRef.value.setResult(result)
    }
    
  } catch (error) {
    console.error('提交答案失败:', error)
  }
}

function handleNextQuestion() {
  if (questionCardRef.value) {
    questionCardRef.value.reset()
  }
  
  const nextIndex = store.currentQuestionIndex + 1
  
  if (nextIndex >= store.totalQuestions) {
    // 会话完成
    showCompletionDialog.value = true
    return
  }
  
  // 检查是否是最后一题的情况
  if (nextIndex === store.totalQuestions - 1) {
    // 这是最后一题，正常跳转
    jumpToQuestion(nextIndex)
  } else {
    // 正常跳转到下一题
    jumpToQuestion(nextIndex)
  }
}

function handlePause() {
  store.pauseSession()
}

function handleQuit() {
  if (confirm('确定要退出当前复习会话吗？进度将不会保存。')) {
    // 清理当前复习状态
    store.sessionInProgress = false
    store.currentSession = null
    store.currentQuestion = null
    store.currentAnswer = ''
    store.showAnswer = false
    
    // 通知父组件会话已结束
    emit('session-complete')
  }
}

function handleSessionComplete() {
  showCompletionDialog.value = false
  store.completeSession()
  emit('session-complete')
}

// 工具函数
function calculateAnswerQuality(userAnswer: string, correctAnswer: string, timeTaken: number): number {
  // 简单的质量评分算法
  const isCorrect = userAnswer.trim().toLowerCase() === correctAnswer.trim().toLowerCase()
  
  if (!isCorrect) return 0 // 错误答案
  
  // 根据答题时间计算质量分数 (1-5)
  if (timeTaken <= 10) return 5      // 很快
  if (timeTaken <= 30) return 4      // 较快
  if (timeTaken <= 60) return 3      // 正常
  if (timeTaken <= 120) return 2     // 较慢
  return 1                           // 很慢
}

function formatTime(seconds: number): string {
  if (seconds < 60) {
    return `${seconds}秒`
  } else if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    return remainingSeconds > 0 ? `${minutes}分${remainingSeconds}秒` : `${minutes}分`
  } else {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}小时${minutes}分`
  }
}

// 键盘事件监听
function handleKeyDown(event: KeyboardEvent) {
  // 检查是否按下Ctrl键
  if (event.ctrlKey) {
    if (event.key === 'ArrowLeft') {
      event.preventDefault()
      handlePreviousQuestion()
    } else if (event.key === 'ArrowRight') {
      event.preventDefault()
      handleNextQuestionKeyboard()
    }
  }
}

// 题目导航
function handlePreviousQuestion() {
  if (store.currentQuestionIndex <= 0) {
    alert('已经是第一题了！')
    return
  }
  jumpToQuestion(store.currentQuestionIndex - 1)
}

function handleNextQuestionKeyboard() {
  if (store.currentQuestionIndex >= store.totalQuestions - 1) {
    alert('已经是最后一题了！')
    return
  }
  jumpToQuestion(store.currentQuestionIndex + 1)
}

function handleJumpToQuestion(index: number) {
  jumpToQuestion(index)
}

function jumpToQuestion(index: number) {
  if (index < 0 || index >= store.totalQuestions) return
  
  // 重置当前题目卡片
  if (questionCardRef.value) {
    questionCardRef.value.reset()
  }
  
  // 更新store中的当前题目索引
  store.currentSession!.currentQuestionIndex = index
  store.currentQuestion = store.currentSession!.questions[index]
}
</script>

<style scoped>
.review-session {
  width: 100%;
  min-height: 100vh;
  background: #f8f9fa;
  padding: 1rem 0;
}

.session-progress {
  margin-bottom: 2rem;
}

.session-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 1rem;
}

.question-section {
  margin-bottom: 2rem;
}

.question-card {
  /* 统一答题卡片样式已在组件内部定义 */
}

/* 无题目状态 */
.no-question {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-state {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.empty-state p {
  color: #6c757d;
  margin-bottom: 2rem;
}

/* 完成对话框 */
.completion-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.completion-dialog {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.completion-header {
  padding: 1.5rem;
  border-bottom: 2px solid #e9ecef;
  text-align: center;
}

.completion-header h3 {
  color: #2c3e50;
  margin: 0;
  font-size: 1.5rem;
}

.completion-content {
  padding: 1.5rem;
}

.completion-stats {
  display: grid;
  gap: 1rem;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 1rem;
}

.stat-row .correct {
  color: #28a745;
  font-weight: 600;
}

.stat-row .incorrect {
  color: #dc3545;
  font-weight: 600;
}

.stat-row .accuracy {
  color: #007bff;
  font-weight: 600;
}

.completion-actions {
  padding: 1.5rem;
  border-top: 2px solid #e9ecef;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .session-content {
    padding: 0 0.5rem;
  }
  
  .completion-dialog {
    margin: 1rem;
    width: auto;
  }
  
  .completion-header,
  .completion-content,
  .completion-actions {
    padding: 1rem;
  }
}
</style> 