<template>
  <div class="unified-question-card">
    <!-- 题目头部信息 -->
    <div class="question-header">
      <div class="question-meta">
        <span class="question-type">{{ getTypeLabel(question.questionType) }}</span>
        <span class="question-difficulty">{{ getDifficultyLabel(question.difficulty) }}</span>
        <span class="question-time">预计 {{ question.estimatedTime }} 分钟</span>
      </div>
      <div class="question-tags" v-if="question.tags && question.tags.length > 0">
        <span v-for="tag in question.tags" :key="tag" class="question-tag">{{ tag }}</span>
      </div>
    </div>

    <!-- 题目内容 -->
    <div class="question-content">
      <div class="question-text">{{ question.question }}</div>
    </div>

    <!-- 答题区域 -->
    <div class="answer-section">
      <!-- 填空题答题 -->
      <div v-if="question.questionType === 'text'" class="text-answer">
        <div class="answer-label">您的答案：</div>
        <textarea
          v-model="userAnswer"
          :disabled="showResult"
          placeholder="请输入您的答案..."
          class="text-input"
          rows="4"
          @keydown.ctrl.enter="handleSubmit"
        />
        <div class="input-hint">💡 提示：Ctrl + Enter 快速提交</div>
      </div>

      <!-- 选择题答题 -->
      <div v-else-if="question.questionType === 'choice'" class="choice-answer">
        <div class="answer-label">请选择正确答案：</div>
        <div class="choice-options">
          <label 
            v-for="(option, index) in question.options" 
            :key="index"
            class="choice-option"
            :class="{ 
              'selected': userAnswer === option,
              'correct': showResult && option === question.correctAnswer,
              'incorrect': showResult && userAnswer === option && option !== question.correctAnswer,
              'disabled': showResult
            }"
            @click="handleChoiceSelect(option)"
          >
            <span class="option-indicator">{{ getOptionLabel(index) }}</span>
            <span class="option-text">{{ option }}</span>
            <span v-if="showResult && option === question.correctAnswer" class="correct-mark">✓</span>
          </label>
        </div>
      </div>

      <!-- 编程题答题 -->
      <div v-else-if="question.questionType === 'code'" class="code-answer">
        <div class="answer-label">请编写代码：</div>
        <textarea
          v-model="userAnswer"
          :disabled="showResult"
          placeholder="// 请在此编写您的代码&#10;function solution() {&#10;  // Your code here&#10;}"
          class="code-input"
          rows="12"
        />
        <div class="input-hint">💡 提示：使用适当的缩进和注释</div>
      </div>
    </div>

    <!-- 答题操作 -->
    <div class="answer-actions" v-if="!showResult">
      <div class="answer-status">
        <span v-if="userAnswer.trim()" class="status-text">
          📝 已输入 {{ getAnswerLength() }}
        </span>
        <span v-else class="status-prompt">等待输入答案...</span>
      </div>
      <div class="action-buttons">
        <BaseButton
          @click="handleSubmit"
          :disabled="!canSubmit"
          variant="primary"
          size="large"
          :loading="submitting"
        >
          {{ submitting ? '提交中...' : '提交答案' }}
        </BaseButton>
        <BaseButton
          v-if="userAnswer.trim()"
          @click="handleClear"
          variant="secondary"
          size="large"
        >
          清空
        </BaseButton>
      </div>
    </div>

    <!-- 答题结果 -->
    <div v-if="showResult" class="result-section">
      <div class="result-header">
        <div class="result-status" :class="{ 'correct': isCorrect, 'incorrect': !isCorrect }">
          <span class="result-icon">{{ isCorrect ? '✅' : '❌' }}</span>
          <span class="result-text">{{ isCorrect ? '回答正确！' : '回答错误' }}</span>
        </div>
        <div class="result-meta">
          <span>用时: {{ formatTime(timeTaken) }}</span>
          <span>质量: {{ getQualityLabel(answerQuality) }}</span>
        </div>
      </div>

      <!-- 正确答案 -->
      <div class="correct-answer">
        <h4>🎯 正确答案</h4>
        <div class="answer-content">{{ question.correctAnswer }}</div>
      </div>

      <!-- 详细解释 -->
      <div v-if="question.explanation" class="explanation">
        <h4>💡 详细解释</h4>
        <div class="explanation-content">{{ question.explanation }}</div>
      </div>

      <!-- SM-2算法结果 -->
      <div v-if="sm2Result" class="sm2-result">
        <h4>📊 学习效果</h4>
        <div class="sm2-info">
          <div class="sm2-item">
            <span>难度系数:</span>
            <span>{{ sm2Result.newEaseFactor.toFixed(2) }}</span>
          </div>
          <div class="sm2-item">
            <span>复习间隔:</span>
            <span>{{ sm2Result.newInterval }} 天</span>
          </div>
          <div class="sm2-item">
            <span>下次复习:</span>
            <span>{{ formatDate(sm2Result.nextReviewDate) }}</span>
          </div>
        </div>
      </div>

      <!-- 继续按钮 -->
      <div class="continue-section">
        <BaseButton
          @click="$emit('next-question')"
          variant="primary"
          size="large"
          class="continue-button"
        >
          <span v-if="isLastQuestion">完成复习</span>
          <span v-else>下一题 →</span>
        </BaseButton>
      </div>
    </div>

    <!-- 题目分类信息 -->
    <div class="question-footer">
      <span class="category">分类: {{ question.category }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { ReviewQuestion, AnswerResult } from '@/types/review'
import BaseButton from '@/components/common/BaseButton.vue'

// Props
interface UnifiedQuestionProps {
  question: ReviewQuestion
  isLastQuestion?: boolean
}

const props = defineProps<UnifiedQuestionProps>()

// Emits
const emit = defineEmits<{
  'submit-answer': [answer: string]
  'next-question': []
}>()

// 状态
const userAnswer = ref('')
const submitting = ref(false)
const showResult = ref(false)
const isCorrect = ref(false)
const timeTaken = ref(0)
const answerQuality = ref(0)
const sm2Result = ref<AnswerResult | null>(null)
const answerStartTime = ref(Date.now())

// 计算属性
const canSubmit = computed(() => {
  return userAnswer.value.trim().length > 0 && !submitting.value
})

// 事件处理
function handleChoiceSelect(option: string) {
  if (showResult.value) return
  userAnswer.value = option
  // 移除自动提交，只选择选项
}

async function handleSubmit() {
  if (!canSubmit.value) return
  
  submitting.value = true
  timeTaken.value = Math.floor((Date.now() - answerStartTime.value) / 1000)
  
  try {
    // 发出提交事件，让父组件处理
    emit('submit-answer', userAnswer.value.trim())
    
    // 模拟答题结果（实际应该从父组件传入）
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 判断答案正确性
    isCorrect.value = userAnswer.value.trim().toLowerCase() === props.question.correctAnswer.trim().toLowerCase()
    answerQuality.value = calculateQuality()
    
    showResult.value = true
    
  } catch (error) {
    console.error('提交答案失败:', error)
  } finally {
    submitting.value = false
  }
}

function handleClear() {
  userAnswer.value = ''
}

// 工具函数
function calculateQuality(): number {
  if (!isCorrect.value) return 0
  if (timeTaken.value <= 10) return 5
  if (timeTaken.value <= 30) return 4  
  if (timeTaken.value <= 60) return 3
  if (timeTaken.value <= 120) return 2
  return 1
}

function getTypeLabel(type: string): string {
  const labels = { 'text': '📝 填空题', 'choice': '📋 选择题', 'code': '💻 编程题' }
  return labels[type as keyof typeof labels] || type
}

function getDifficultyLabel(difficulty: number): string {
  const labels = ['', '⭐ 简单', '⭐⭐ 容易', '⭐⭐⭐ 中等', '⭐⭐⭐⭐ 困难', '⭐⭐⭐⭐⭐ 极难']
  return labels[difficulty] || `难度 ${difficulty}`
}

function getOptionLabel(index: number): string {
  return String.fromCharCode(65 + index)
}

function getAnswerLength(): string {
  const trimmed = userAnswer.value.trim()
  if (props.question.questionType === 'choice') return '1 个选项'
  if (props.question.questionType === 'code') return `${trimmed.split('\n').length} 行代码`
  return trimmed.length < 10 ? `${trimmed.length} 个字符` : `${trimmed.split(/\s+/).length} 个词`
}

function getQualityLabel(quality: number): string {
  const labels = ['很差', '较差', '一般', '良好', '优秀', '完美']
  return labels[quality] || '未知'
}

function formatTime(seconds: number): string {
  return seconds < 60 ? `${seconds}秒` : `${Math.floor(seconds / 60)}分${seconds % 60}秒`
}

function formatDate(date: Date): string {
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

// 暴露方法给父组件
defineExpose({
  setResult: (result: AnswerResult) => {
    isCorrect.value = result.isCorrect
    timeTaken.value = result.timeTaken
    answerQuality.value = result.quality
    sm2Result.value = result
    showResult.value = true
  },
  reset: () => {
    userAnswer.value = ''
    showResult.value = false
    submitting.value = false
    answerStartTime.value = Date.now()
  }
})
</script>

<style scoped>
.unified-question-card {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 题目头部 */
.question-header {
  padding: 1.5rem;
  border-bottom: 2px solid #e9ecef;
}

.question-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.question-type, .question-difficulty, .question-time {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.question-type { background: #007bff; color: white; }
.question-difficulty { background: #ffc107; color: #212529; }
.question-time { background: #6c757d; color: white; }

.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.question-tag {
  background: #f8f9fa;
  color: #495057;
  padding: 0.2rem 0.5rem;
  border-radius: 8px;
  font-size: 0.75rem;
  border: 1px solid #dee2e6;
}

/* 题目内容 */
.question-content {
  padding: 1.5rem;
}

.question-text {
  font-size: 1.1rem;
  line-height: 1.6;
  color: #2c3e50;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #007bff;
}

/* 答题区域 */
.answer-section {
  padding: 1.5rem;
  border-bottom: 2px solid #e9ecef;
}

.answer-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
  display: block;
}

/* 填空题样式 */
.text-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  line-height: 1.5;
  resize: vertical;
  transition: border-color 0.2s ease;
}

.text-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

/* 选择题样式 */
.choice-options {
  display: grid;
  gap: 0.75rem;
}

.choice-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.choice-option:hover:not(.disabled) {
  border-color: #007bff;
  background: #f8f9ff;
}

.choice-option.selected {
  border-color: #007bff;
  background: #e3f2fd;
}

.choice-option.correct {
  border-color: #28a745;
  background: #f8fff9;
}

.choice-option.incorrect {
  border-color: #dc3545;
  background: #fff5f5;
}

.choice-option.disabled {
  cursor: not-allowed;
}

.option-indicator {
  min-width: 32px;
  height: 32px;
  background: #e9ecef;
  color: #495057;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.choice-option.selected .option-indicator {
  background: #007bff;
  color: white;
}

.choice-option.correct .option-indicator {
  background: #28a745;
  color: white;
}

.option-text {
  flex: 1;
  font-size: 1rem;
  line-height: 1.4;
}

.correct-mark {
  color: #28a745;
  font-size: 1.2rem;
  font-weight: bold;
}

/* 编程题样式 */
.code-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.9rem;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f8f9fa;
  resize: vertical;
  transition: border-color 0.2s ease;
}

.code-input:focus {
  outline: none;
  border-color: #007bff;
  background: white;
}

.input-hint {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #6c757d;
}

/* 答题操作 */
.answer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: #f8f9fa;
}

.answer-status .status-text {
  color: #007bff;
  font-weight: 600;
}

.answer-status .status-prompt {
  color: #6c757d;
  font-style: italic;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

/* 结果区域 */
.result-section {
  padding: 1.5rem;
}

.result-header {
  text-align: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}

.result-status {
  margin-bottom: 1rem;
}

.result-status.correct { color: #28a745; }
.result-status.incorrect { color: #dc3545; }

.result-icon {
  font-size: 2rem;
  margin-right: 0.5rem;
}

.result-text {
  font-size: 1.3rem;
  font-weight: 600;
}

.result-meta {
  display: flex;
  justify-content: center;
  gap: 2rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.correct-answer, .explanation, .sm2-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  border-radius: 8px;
}

.correct-answer {
  background: #f8fff9;
  border: 1px solid #c3e6cb;
}

.explanation {
  background: #e3f2fd;
  border: 1px solid #bee5eb;
}

.sm2-result {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.correct-answer h4, .explanation h4, .sm2-result h4 {
  margin: 0 0 0.75rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.correct-answer h4 { color: #28a745; }
.explanation h4 { color: #007bff; }
.sm2-result h4 { color: #2c3e50; }

.answer-content, .explanation-content {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  line-height: 1.4;
}

.sm2-info {
  display: grid;
  gap: 0.5rem;
}

.sm2-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e9ecef;
}

.sm2-item:last-child {
  border-bottom: none;
}

.continue-section {
  text-align: center;
}

.continue-button {
  min-width: 150px;
}

/* 题目底部 */
.question-footer {
  padding: 1rem 1.5rem;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  font-size: 0.9rem;
  color: #6c757d;
}

.category {
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .choice-option {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .option-indicator {
    align-self: flex-start;
  }
  
  .answer-actions {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .result-meta {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style> 