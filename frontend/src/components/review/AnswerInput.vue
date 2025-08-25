<template>
  <div class="answer-input">
    <!-- 填空题输入 -->
    <div v-if="questionType === 'text'" class="text-input-container">
      <div class="input-label">
        <span>请输入您的答案：</span>
        <span v-if="disabled" class="submitted-indicator">✅ 已提交</span>
      </div>
      <textarea
        v-model="localAnswer"
        :disabled="disabled"
        :placeholder="placeholder || '请在此输入您的答案...'"
        class="text-input"
        rows="4"
        @input="handleInput"
        @keydown.ctrl.enter="handleSubmit"
      />
      <div class="input-hint">
        <span>💡 提示：Ctrl + Enter 快速提交</span>
      </div>
    </div>

    <!-- 选择题选项 -->
    <div v-else-if="questionType === 'choice'" class="choice-input-container">
      <div class="input-label">
        <span>请选择正确答案：</span>
        <span v-if="disabled" class="submitted-indicator">✅ 已提交</span>
      </div>
      <div class="choice-options">
        <label 
          v-for="(option, index) in options" 
          :key="index"
          class="choice-option"
          :class="{ 
            'selected': localAnswer === option,
            'disabled': disabled
          }"
        >
          <input
            type="radio"
            :value="option"
            v-model="localAnswer"
            :disabled="disabled"
            @change="handleInput"
            class="choice-radio"
          />
          <span class="option-indicator">{{ getOptionLabel(index) }}</span>
          <span class="option-text">{{ option }}</span>
        </label>
      </div>
    </div>

    <!-- 编程题输入 -->
    <div v-else-if="questionType === 'code'" class="code-input-container">
      <div class="input-label">
        <span>请编写代码：</span>
        <span v-if="disabled" class="submitted-indicator">✅ 已提交</span>
      </div>
      <div class="code-editor">
        <textarea
          v-model="localAnswer"
          :disabled="disabled"
          :placeholder="placeholder || '// 请在此编写您的代码\nfunction solution() {\n  // Your code here\n}'"
          class="code-textarea"
          rows="12"
          @input="handleInput"
        />
      </div>
      <div class="code-actions">
        <span class="input-hint">💡 提示：使用适当的缩进和注释</span>
      </div>
    </div>

    <!-- 答题操作区域 -->
    <div class="answer-actions">
      <div class="answer-info">
        <span v-if="localAnswer.trim()" class="answer-status">
          📝 已输入 {{ getAnswerLength(localAnswer) }}
        </span>
        <span v-else class="answer-prompt">
          等待输入答案...
        </span>
      </div>
      
      <div class="action-buttons">
        <!-- 提交答案按钮 -->
        <BaseButton
          v-if="!disabled"
          @click="handleSubmit"
          :disabled="!canSubmit"
          variant="primary"
          size="large"
          class="submit-button"
        >
          <span v-if="!submitting">提交答案</span>
          <span v-else>提交中...</span>
        </BaseButton>

        <!-- 清空答案按钮 -->
        <BaseButton
          v-if="!disabled && localAnswer.trim()"
          @click="handleClear"
          variant="secondary"
          size="large"
          class="clear-button"
        >
          清空
        </BaseButton>

        <!-- 已提交状态 -->
        <div v-else-if="disabled" class="submitted-status">
          <span class="submitted-text">答案已提交</span>
          <span class="submitted-answer">{{ localAnswer }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { AnswerInputProps } from '@/types/review'
import BaseButton from '@/components/common/BaseButton.vue'

// Props
const props = withDefaults(defineProps<AnswerInputProps>(), {
  disabled: false,
  placeholder: ''
})

// Emits
const emit = defineEmits<{
  'update:answer': [answer: string]
  'submit': [answer: string]
  'clear': []
}>()

// 本地状态
const localAnswer = ref('')
const submitting = ref(false)

// 计算属性
const canSubmit = computed(() => {
  return localAnswer.value.trim().length > 0 && !submitting.value
})

// 监听本地答案变化
watch(localAnswer, (newAnswer) => {
  emit('update:answer', newAnswer)
})

// 事件处理
function handleInput() {
  // 自动触发更新
}

function handleSubmit() {
  if (!canSubmit.value) return
  
  submitting.value = true
  emit('submit', localAnswer.value.trim())
  
  // 模拟提交延迟
  setTimeout(() => {
    submitting.value = false
  }, 500)
}

function handleClear() {
  localAnswer.value = ''
  emit('clear')
}

// 工具函数
function getOptionLabel(index: number): string {
  return String.fromCharCode(65 + index) // A, B, C, D...
}

function getAnswerLength(answer: string): string {
  const trimmed = answer.trim()
  
  if (props.questionType === 'choice') {
    return '1 个选项'
  } else if (props.questionType === 'code') {
    const lines = trimmed.split('\n').length
    return `${lines} 行代码`
  } else {
    const chars = trimmed.length
    if (chars < 10) {
      return `${chars} 个字符`
    } else {
      const words = trimmed.split(/\s+/).length
      return `${words} 个词`
    }
  }
}
</script>

<style scoped>
.answer-input {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

/* 输入标签 */
.input-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.submitted-indicator {
  color: #28a745;
  font-size: 0.9rem;
  font-weight: 600;
}

/* 填空题输入 */
.text-input-container {
  margin-bottom: 1.5rem;
}

.text-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  line-height: 1.5;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  transition: border-color 0.2s ease;
}

.text-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.text-input:disabled {
  background: #f8f9fa;
  color: #6c757d;
}

/* 选择题输入 */
.choice-input-container {
  margin-bottom: 1.5rem;
}

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
}

.choice-option:hover:not(.disabled) {
  border-color: #007bff;
  background: #f8f9ff;
}

.choice-option.selected {
  border-color: #007bff;
  background: #e3f2fd;
}

.choice-option.disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.choice-radio {
  display: none;
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

.option-text {
  flex: 1;
  font-size: 1rem;
  line-height: 1.4;
}

/* 编程题输入 */
.code-input-container {
  margin-bottom: 1.5rem;
}

.code-editor {
  position: relative;
}

.code-textarea {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.9rem;
  line-height: 1.4;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f8f9fa;
  resize: vertical;
  min-height: 200px;
  transition: border-color 0.2s ease;
}

.code-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
  background: white;
}

.code-textarea:disabled {
  background: #f8f9fa;
  color: #6c757d;
}

.code-actions {
  margin-top: 0.5rem;
}

/* 提示信息 */
.input-hint {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #6c757d;
}

/* 操作区域 */
.answer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px solid #e9ecef;
  margin-top: 1.5rem;
}

.answer-info {
  flex: 1;
}

.answer-status {
  color: #007bff;
  font-weight: 600;
  font-size: 0.9rem;
}

.answer-prompt {
  color: #6c757d;
  font-style: italic;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.submit-button {
  min-width: 120px;
}

.clear-button {
  min-width: 80px;
}

/* 已提交状态 */
.submitted-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.submitted-text {
  color: #28a745;
  font-weight: 600;
  font-size: 0.9rem;
}

.submitted-answer {
  color: #6c757d;
  font-size: 0.8rem;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .answer-actions {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .choice-option {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .option-indicator {
    align-self: flex-start;
  }
  
  .submitted-status {
    align-items: center;
    text-align: center;
  }
  
  .submitted-answer {
    max-width: none;
    white-space: normal;
  }
}
</style> 