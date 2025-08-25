<template>
  <div class="question-renderer">
    <!-- 题目头部信息 -->
    <div class="question-header">
      <div class="question-meta">
        <span class="question-type">{{ getTypeLabel(question.questionType) }}</span>
        <span class="question-difficulty">{{ getDifficultyLabel(question.difficulty) }}</span>
        <span class="question-time">预计 {{ question.estimatedTime }} 分钟</span>
      </div>
      <div class="question-tags" v-if="question.tags && question.tags.length > 0">
        <span 
          v-for="tag in question.tags" 
          :key="tag" 
          class="question-tag"
        >
          {{ tag }}
        </span>
      </div>
    </div>

    <!-- 题目内容区域 -->
    <div class="question-content">
      <div class="question-text">
        {{ question.question }}
      </div>

      <!-- 选择题选项 -->
      <div v-if="question.questionType === 'choice' && question.options" class="choice-options">
        <div 
          v-for="(option, index) in question.options" 
          :key="index"
          class="choice-option"
          :class="{ 'correct': showAnswer && option === question.correctAnswer }"
        >
          <span class="option-label">{{ getOptionLabel(index) }}</span>
          <span class="option-text">{{ option }}</span>
        </div>
      </div>

      <!-- 填空题提示 -->
      <div v-if="question.questionType === 'text'" class="text-hint">
        <p class="hint-text">请在下方输入框中填写您的答案</p>
      </div>

      <!-- 编程题提示 -->
      <div v-if="question.questionType === 'code'" class="code-hint">
        <p class="hint-text">请编写代码解决以下问题</p>
      </div>
    </div>

    <!-- 答案显示区域 -->
    <div v-if="showAnswer" class="answer-section">
      <div class="correct-answer">
        <h4>🎯 正确答案</h4>
        <div class="answer-content">
          {{ question.correctAnswer }}
        </div>
      </div>

      <div v-if="question.explanation" class="explanation">
        <h4>💡 详细解释</h4>
        <div class="explanation-content">
          {{ question.explanation }}
        </div>
      </div>
    </div>

    <!-- 题目分类信息 -->
    <div class="question-footer">
      <div class="category-info">
        <span class="category-label">分类:</span>
        <span class="category-name">{{ question.category }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { QuestionRendererProps, QuestionType } from '@/types/review'

// Props
const { question, showAnswer } = defineProps<QuestionRendererProps>()

// 工具函数
function getTypeLabel(type: QuestionType): string {
  const labels = {
    'text': '📝 填空题',
    'choice': '📋 选择题',
    'code': '💻 编程题'
  }
  return labels[type] || type
}

function getDifficultyLabel(difficulty: number): string {
  const labels = ['', '⭐ 简单', '⭐⭐ 容易', '⭐⭐⭐ 中等', '⭐⭐⭐⭐ 困难', '⭐⭐⭐⭐⭐ 极难']
  return labels[difficulty] || `难度 ${difficulty}`
}

function getOptionLabel(index: number): string {
  return String.fromCharCode(65 + index) // A, B, C, D...
}
</script>

<style scoped>
.question-renderer {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

/* 题目头部 */
.question-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}

.question-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.question-type {
  background: #007bff;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.question-difficulty {
  background: #ffc107;
  color: #212529;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.question-time {
  background: #6c757d;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

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
  margin-bottom: 2rem;
}

.question-text {
  font-size: 1.1rem;
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #007bff;
}

/* 选择题选项 */
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
  transition: all 0.2s ease;
  cursor: pointer;
}

.choice-option:hover {
  border-color: #007bff;
  background: #f8f9ff;
}

.choice-option.correct {
  border-color: #28a745;
  background: #f8fff9;
}

.option-label {
  min-width: 32px;
  height: 32px;
  background: #007bff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
}

.choice-option.correct .option-label {
  background: #28a745;
}

.option-text {
  flex: 1;
  font-size: 1rem;
  line-height: 1.4;
}

/* 提示信息 */
.text-hint,
.code-hint {
  padding: 1rem;
  background: #e3f2fd;
  border-radius: 8px;
  border-left: 4px solid #2196f3;
}

.hint-text {
  margin: 0;
  color: #1565c0;
  font-size: 0.9rem;
}

/* 答案显示 */
.answer-section {
  margin: 2rem 0;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px solid #e9ecef;
}

.correct-answer {
  margin-bottom: 1.5rem;
}

.correct-answer h4 {
  color: #28a745;
  margin: 0 0 0.75rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.answer-content {
  padding: 1rem;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 8px;
  color: #155724;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 1rem;
  line-height: 1.4;
}

.explanation h4 {
  color: #007bff;
  margin: 0 0 0.75rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.explanation-content {
  padding: 1rem;
  background: #d1ecf1;
  border: 1px solid #bee5eb;
  border-radius: 8px;
  color: #0c5460;
  line-height: 1.5;
}

/* 题目底部 */
.question-footer {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.category-label {
  font-weight: 600;
}

.category-name {
  color: #007bff;
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
  
  .option-label {
    align-self: flex-start;
  }
  
  .answer-section {
    padding: 1rem;
  }
}
</style> 