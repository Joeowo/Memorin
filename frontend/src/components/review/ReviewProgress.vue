<template>
  <div class="review-progress">
    <!-- 进度头部 -->
    <div class="progress-header">
      <div class="session-info">
        <h3 class="session-title">{{ getModeLabel(mode) }}</h3>
        <div class="session-stats">
          <span class="question-counter">
            第 {{ currentIndex + 1 }} / {{ totalQuestions }} 题
          </span>
          <span class="accuracy-display">
            正确率: {{ accuracy }}%
          </span>
          <span v-if="timeRemaining" class="time-remaining">
            剩余: {{ formatTime(timeRemaining) }}
          </span>
        </div>
      </div>
      
      <div class="session-actions">
        <BaseButton
          @click="$emit('pause')"
          variant="secondary"
          size="small"
          class="pause-button"
        >
          ⏸️ 暂停
        </BaseButton>
        <BaseButton
          @click="$emit('quit')"
          variant="danger"
          size="small"
          class="quit-button"
        >
          🚪 退出
        </BaseButton>
      </div>
    </div>

    <!-- 主进度条 -->
    <div class="main-progress">
      <div class="progress-bar-container">
        <div 
          class="progress-bar"
          :style="{ width: `${progressPercentage}%` }"
        />
        <div class="progress-text">
          {{ progressPercentage }}% 完成
        </div>
      </div>
    </div>

    <!-- 详细进度信息 -->
    <div class="progress-details">
      <!-- 题目状态网格 -->
      <div class="question-grid">
        <div 
          v-for="(status, index) in questionStatuses"
          :key="index"
          class="question-dot"
          :class="{
            'current': index === currentIndex,
            'correct': status === 'correct',
            'incorrect': status === 'incorrect',
            'unanswered': status === 'unanswered'
          }"
          :title="getQuestionTooltip(index, status)"
          @click="handleQuestionJump(index)"
        >
          {{ index + 1 }}
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon correct">✅</div>
          <div class="stat-content">
            <div class="stat-value">{{ correctCount }}</div>
            <div class="stat-label">正确</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon incorrect">❌</div>
          <div class="stat-content">
            <div class="stat-value">{{ incorrectCount }}</div>
            <div class="stat-label">错误</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon remaining">⏳</div>
          <div class="stat-content">
            <div class="stat-value">{{ remainingCount }}</div>
            <div class="stat-label">剩余</div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon time">⏱️</div>
          <div class="stat-content">
            <div class="stat-value">{{ formatTime(elapsedTime) }}</div>
            <div class="stat-label">已用时间</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预估完成时间 -->
    <div v-if="estimatedTimeRemaining" class="time-estimation">
      <div class="estimation-content">
        <span class="estimation-icon">🎯</span>
        <span class="estimation-text">
          预计还需 {{ formatTime(estimatedTimeRemaining) }} 完成
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ReviewMode } from '@/types/review'
import BaseButton from '@/components/common/BaseButton.vue'

// Props
interface ProgressProps {
  mode: ReviewMode
  currentIndex: number
  totalQuestions: number
  correctCount: number
  incorrectCount: number
  elapsedTime: number // 已用时间(秒)
  timeRemaining?: number // 剩余时间(秒)
  questionStatuses: ('correct' | 'incorrect' | 'unanswered')[]
}

const props = defineProps<ProgressProps>()

// Emits
const emit = defineEmits<{
  'pause': []
  'quit': []
  'jump-to-question': [index: number]
}>()

// 计算属性
const progressPercentage = computed(() => {
  if (props.totalQuestions === 0) return 0
  return Math.round((props.currentIndex / props.totalQuestions) * 100)
})

const accuracy = computed(() => {
  const totalAnswered = props.correctCount + props.incorrectCount
  if (totalAnswered === 0) return 0
  return Math.round((props.correctCount / totalAnswered) * 100)
})

const remainingCount = computed(() => {
  return props.totalQuestions - props.currentIndex
})

const estimatedTimeRemaining = computed(() => {
  if (props.currentIndex === 0) return null
  
  const avgTimePerQuestion = props.elapsedTime / props.currentIndex
  const remainingQuestions = props.totalQuestions - props.currentIndex
  
  return Math.round(avgTimePerQuestion * remainingQuestions)
})

// 工具函数
function getModeLabel(mode: ReviewMode): string {
  const labels = {
    'daily': '📅 今日复习',
    'quick': '⚡ 快速复习',
    'weakness': '💪 弱项强化',
    'mistake': '🎯 错题回顾'
  }
  return labels[mode] || mode
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

function getQuestionTooltip(index: number, status: string): string {
  const questionNum = index + 1
  
  switch (status) {
    case 'correct':
      return `第${questionNum}题：回答正确 ✅`
    case 'incorrect':
      return `第${questionNum}题：回答错误 ❌`
    case 'unanswered':
      return `第${questionNum}题：未回答`
    default:
      return `第${questionNum}题`
  }
}

function handleQuestionJump(index: number) {
  emit('jump-to-question', index)
}
</script>

<style scoped>
.review-progress {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 2px solid #e9ecef;
}

/* 进度头部 */
.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}

.session-info {
  flex: 1;
}

.session-title {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.session-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  font-size: 0.9rem;
}

.question-counter {
  color: #007bff;
  font-weight: 600;
}

.accuracy-display {
  color: #28a745;
  font-weight: 600;
}

.time-remaining {
  color: #ffc107;
  font-weight: 600;
}

.session-actions {
  display: flex;
  gap: 0.5rem;
}

.pause-button,
.quit-button {
  min-width: 80px;
}

/* 主进度条 */
.main-progress {
  margin-bottom: 1.5rem;
}

.progress-bar-container {
  position: relative;
  height: 24px;
  background: #e9ecef;
  border-radius: 12px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #007bff 0%, #0056b3 100%);
  border-radius: 12px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.8rem;
  z-index: 1;
}

/* 详细进度 */
.progress-details {
  margin-bottom: 1rem;
}

/* 题目状态网格 */
.question-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  justify-content: center;
}

.question-dot {
  min-width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.question-dot.current {
  background: #007bff;
  color: white;
  border-color: #0056b3;
  transform: scale(1.1);
}

.question-dot.correct {
  background: #28a745;
  color: white;
}

.question-dot.incorrect {
  background: #dc3545;
  color: white;
}

.question-dot.unanswered {
  background: #e9ecef;
  color: #6c757d;
}

.question-dot:hover {
  transform: scale(1.05);
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.stat-icon {
  font-size: 1.5rem;
  min-width: 24px;
  text-align: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: #6c757d;
}

/* 时间预估 */
.time-estimation {
  padding: 1rem;
  background: linear-gradient(135deg, #e3f2fd 0%, #f8f9fa 100%);
  border-radius: 8px;
  border: 1px solid #bee5eb;
  text-align: center;
}

.estimation-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.estimation-icon {
  font-size: 1.2rem;
}

.estimation-text {
  color: #0c5460;
  font-weight: 600;
  font-size: 0.9rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .review-progress {
    padding: 1rem;
  }
  
  .progress-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .session-actions {
    justify-content: center;
  }
  
  .session-stats {
    justify-content: center;
    text-align: center;
  }
  
  .question-grid {
    gap: 0.25rem;
  }
  
  .question-dot {
    min-width: 28px;
    height: 28px;
    font-size: 0.7rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .estimation-content {
    flex-direction: column;
    gap: 0.25rem;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .question-grid {
    max-height: 120px;
    overflow-y: auto;
  }
}
</style> 