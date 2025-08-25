<template>
  <div class="base-input-wrapper">
    <label v-if="label" :for="inputId" class="base-input-label">
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>
    
    <div class="base-input-container">
      <input
        :id="inputId"
        v-model="inputValue"
        :type="type"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :class="[
          'base-input',
          { 
            'base-input--error': hasError,
            'base-input--success': hasSuccess,
            'base-input--disabled': disabled
          }
        ]"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />
      
      <div v-if="hasIcon" class="base-input-icon">
        <slot name="icon">
          <span v-if="hasError">❌</span>
          <span v-else-if="hasSuccess">✅</span>
        </slot>
      </div>
    </div>
    
    <div v-if="errorMessage || helpText" class="base-input-message">
      <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
      <span v-else-if="helpText" class="help-message">{{ helpText }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, useId } from 'vue'

// Props定义
interface Props {
  modelValue?: string | number
  type?: 'text' | 'email' | 'password' | 'number' | 'search' | 'tel' | 'url'
  label?: string
  placeholder?: string
  helpText?: string
  errorMessage?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  type: 'text',
  disabled: false,
  readonly: false,
  required: false
})

// 事件定义
const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  input: [event: Event]
  blur: [event: FocusEvent]
  focus: [event: FocusEvent]
}>()

// 响应式数据
const inputId = useId()

const inputValue = computed({
  get: () => props.modelValue,
  set: (value: string | number) => emit('update:modelValue', value)
})

const hasError = computed(() => !!props.errorMessage)
const hasSuccess = computed(() => !hasError.value && !!inputValue.value && !props.disabled)
const hasIcon = computed(() => hasError.value || hasSuccess.value)

// 事件处理
const handleInput = (event: Event) => {
  emit('input', event)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}
</script>

<style scoped>
.base-input-wrapper {
  width: 100%;
}

.base-input-label {
  display: block;
  font-size: 1rem;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 0.5rem;
  cursor: pointer;
}

.required-mark {
  color: #e74c3c;
  margin-left: 0.25rem;
}

.base-input-container {
  position: relative;
  width: 100%;
}

.base-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 400;
  color: #2c3e50;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  box-sizing: border-box;
  outline: none;
}

.base-input::placeholder {
  color: #94a3b8;
  opacity: 1;
}

.base-input:focus {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transform: translateY(-1px);
}

.base-input:hover:not(:disabled):not(:focus) {
  border-color: rgba(102, 126, 234, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

/* Error state */
.base-input--error {
  border-color: rgba(231, 76, 60, 0.5);
  background: rgba(255, 255, 255, 0.9);
}

.base-input--error:focus {
  border-color: rgba(231, 76, 60, 0.7);
  box-shadow: 
    0 4px 20px rgba(231, 76, 60, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

/* Success state */
.base-input--success {
  border-color: rgba(39, 174, 96, 0.5);
}

.base-input--success:focus {
  border-color: rgba(39, 174, 96, 0.7);
  box-shadow: 
    0 4px 20px rgba(39, 174, 96, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

/* Disabled state */
.base-input--disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.5);
}

.base-input--disabled:hover {
  transform: none;
  border-color: rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.5);
}

/* Icon */
.base-input-icon {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  font-size: 1rem;
}

.base-input:has(+ .base-input-icon) {
  padding-right: 3rem;
}

/* Messages */
.base-input-message {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  line-height: 1.4;
}

.error-message {
  color: #e74c3c;
  font-weight: 500;
}

.help-message {
  color: #6c757d;
  font-weight: 400;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-input {
    padding: 1rem;
    font-size: 1rem;
  }
  
  .base-input-label {
    font-size: 0.95rem;
  }
  
  .base-input-message {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .base-input {
    padding: 0.875rem 0.875rem;
    font-size: 0.95rem;
  }
  
  .base-input-label {
    font-size: 0.9rem;
  }
}
</style> 