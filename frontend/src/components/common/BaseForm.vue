<template>
  <form 
    class="base-form" 
    :class="{ 'base-form--loading': loading }"
    @submit.prevent="handleSubmit"
  >
    <!-- Loading遮罩层 -->
    <div v-if="loading" class="base-form-loading-overlay">
      <LoadingSpinner 
        size="large" 
        variant="primary" 
        :text="loadingText || '提交中...'" 
      />
    </div>

    <!-- 表单头部 -->
    <header v-if="title || $slots.header" class="base-form-header">
      <slot name="header">
        <h3 v-if="title" class="base-form-title">{{ title }}</h3>
        <p v-if="description" class="base-form-description">{{ description }}</p>
      </slot>
    </header>

    <!-- 表单字段区域 -->
    <div class="base-form-body">
      <div v-if="fields && fields.length > 0" class="base-form-fields">
        <div 
          v-for="field in fields" 
          :key="field.name"
          class="base-form-field"
          :class="{ 'base-form-field--full-width': field.fullWidth }"
        >
          <!-- 文本输入字段 -->
          <BaseInput
            v-if="field.type === 'input'"
            v-model="formData[field.name]"
            :type="field.inputType || 'text'"
            :label="field.label"
            :placeholder="field.placeholder"
            :help-text="field.helpText"
            :error-message="getFieldError(field.name)"
            :required="field.required"
            :disabled="loading || field.disabled"
            @blur="validateField(field.name)"
          />

          <!-- 文本域字段 -->
          <div v-else-if="field.type === 'textarea'" class="base-form-textarea-wrapper">
            <label v-if="field.label" :for="`${field.name}-textarea`" class="base-form-label">
              {{ field.label }}
              <span v-if="field.required" class="required-mark">*</span>
            </label>
            <textarea
              :id="`${field.name}-textarea`"
              v-model="formData[field.name]"
              :placeholder="field.placeholder"
              :rows="field.rows || 4"
              :disabled="loading || field.disabled"
              :class="[
                'base-form-textarea',
                { 'base-form-textarea--error': getFieldError(field.name) }
              ]"
              @blur="validateField(field.name)"
            ></textarea>
            <div v-if="getFieldError(field.name) || field.helpText" class="base-form-message">
              <span v-if="getFieldError(field.name)" class="error-message">
                {{ getFieldError(field.name) }}
              </span>
              <span v-else-if="field.helpText" class="help-message">
                {{ field.helpText }}
              </span>
            </div>
          </div>

          <!-- 选择框字段 -->
          <div v-else-if="field.type === 'select'" class="base-form-select-wrapper">
            <label v-if="field.label" :for="`${field.name}-select`" class="base-form-label">
              {{ field.label }}
              <span v-if="field.required" class="required-mark">*</span>
            </label>
            <select
              :id="`${field.name}-select`"
              v-model="formData[field.name]"
              :disabled="loading || field.disabled"
              :class="[
                'base-form-select',
                { 'base-form-select--error': getFieldError(field.name) }
              ]"
              @change="validateField(field.name)"
            >
              <option value="" disabled>{{ field.placeholder || '请选择' }}</option>
              <option 
                v-for="option in field.options" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
            <div v-if="getFieldError(field.name) || field.helpText" class="base-form-message">
              <span v-if="getFieldError(field.name)" class="error-message">
                {{ getFieldError(field.name) }}
              </span>
              <span v-else-if="field.helpText" class="help-message">
                {{ field.helpText }}
              </span>
            </div>
          </div>

          <!-- 自定义字段 -->
          <div v-else-if="field.type === 'custom'" class="base-form-custom-field">
            <slot :name="`field-${field.name}`" :field="field" :value="formData[field.name]" :error="getFieldError(field.name)">
            </slot>
          </div>
        </div>
      </div>

      <!-- 自定义内容插槽 -->
      <div v-if="$slots.default" class="base-form-custom-content">
        <slot 
          :form-data="formData" 
          :errors="errors" 
          :loading="loading"
          :validate="validateField"
          :validate-all="validateForm"
        ></slot>
      </div>
    </div>

    <!-- 表单底部/操作区域 -->
    <footer v-if="!hideFooter || $slots.footer" class="base-form-footer">
      <slot name="footer" :loading="loading" :valid="isValid" :submit="handleSubmit">
        <div class="base-form-actions">
          <BaseButton
            v-if="showCancelButton"
            type="button"
            variant="secondary"
            :disabled="loading"
            @click="handleCancel"
          >
            {{ cancelText }}
          </BaseButton>
          <BaseButton
            type="submit"
            variant="primary"
            :loading="loading"
            :disabled="!isValid"
          >
            {{ submitText }}
          </BaseButton>
        </div>
      </slot>
    </footer>
  </form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import BaseInput from './BaseInput.vue'
import BaseButton from './BaseButton.vue'
import LoadingSpinner from './LoadingSpinner.vue'

// 表单字段值类型
type FormFieldValue = string | number | boolean | null | undefined

// 表单字段类型定义
interface FormField {
  name: string
  type: 'input' | 'textarea' | 'select' | 'custom'
  label?: string
  placeholder?: string
  helpText?: string
  required?: boolean
  disabled?: boolean
  fullWidth?: boolean
  // input字段特有属性
  inputType?: 'text' | 'email' | 'password' | 'number' | 'search' | 'tel' | 'url'
  // textarea字段特有属性
  rows?: number
  // select字段特有属性
  options?: Array<{ label: string; value: string | number }>
  // 验证规则
  rules?: ValidationRule[]
}

interface ValidationRule {
  type: 'required' | 'email' | 'minLength' | 'maxLength' | 'pattern' | 'custom'
  value?: string | number | RegExp
  message?: string
  validator?: (value: FormFieldValue) => boolean | string
}

// Props定义
interface Props {
  title?: string
  description?: string
  fields?: FormField[]
  modelValue?: Record<string, FormFieldValue>
  loading?: boolean
  loadingText?: string
  hideFooter?: boolean
  showCancelButton?: boolean
  cancelText?: string
  submitText?: string
  validateOnBlur?: boolean
  validateOnSubmit?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  fields: () => [],
  modelValue: () => ({}),
  loading: false,
  hideFooter: false,
  showCancelButton: true,
  cancelText: '取消',
  submitText: '提交',
  validateOnBlur: true,
  validateOnSubmit: true
})

// 事件定义
const emit = defineEmits<{
  'update:modelValue': [data: Record<string, FormFieldValue>]
  submit: [data: Record<string, FormFieldValue>]
  cancel: []
  'field-change': [fieldName: string, value: FormFieldValue]
  'validation-change': [valid: boolean, errors: Record<string, string>]
}>()

// 表单数据
const formData = reactive<Record<string, FormFieldValue>>({ ...props.modelValue })
const errors = ref<Record<string, string>>({})

// 计算属性
const isValid = computed(() => {
  return Object.keys(errors.value).length === 0
})

// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(formData, newValue)
}, { deep: true })

// 监听formData变化
watch(formData, (newData) => {
  emit('update:modelValue', { ...newData })
}, { deep: true })

// 验证单个字段
const validateField = (fieldName: string): boolean => {
  const field = props.fields.find(f => f.name === fieldName)
  if (!field || !field.rules) {
    delete errors.value[fieldName]
    return true
  }

  const value = formData[fieldName]
  
  for (const rule of field.rules) {
    const result = validateRule(value, rule)
    if (result !== true) {
      errors.value[fieldName] = result
      return false
    }
  }

  delete errors.value[fieldName]
  return true
}

// 验证整个表单
const validateForm = (): boolean => {
  let valid = true
  
  for (const field of props.fields) {
    if (!validateField(field.name)) {
      valid = false
    }
  }
  
  emit('validation-change', valid, { ...errors.value })
  return valid
}

// 验证规则执行
const validateRule = (value: FormFieldValue, rule: ValidationRule): boolean | string => {
  switch (rule.type) {
    case 'required':
      if (!value || (typeof value === 'string' && !value.trim())) {
        return rule.message || '此字段为必填项'
      }
      break
      
    case 'email':
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (value && typeof value === 'string' && !emailRegex.test(value)) {
        return rule.message || '请输入有效的邮箱地址'
      }
      break
      
    case 'minLength':
      if (value && typeof value === 'string' && typeof rule.value === 'number' && value.length < rule.value) {
        return rule.message || `最少需要${rule.value}个字符`
      }
      break
      
    case 'maxLength':
      if (value && typeof value === 'string' && typeof rule.value === 'number' && value.length > rule.value) {
        return rule.message || `最多允许${rule.value}个字符`
      }
      break
      
    case 'pattern':
      if (value && typeof value === 'string' && rule.value instanceof RegExp && !rule.value.test(value)) {
        return rule.message || '格式不正确'
      }
      break
      
    case 'custom':
      if (rule.validator) {
        const result = rule.validator(value)
        if (result !== true) {
          return typeof result === 'string' ? result : (rule.message || '验证失败')
        }
      }
      break
  }
  
  return true
}

// 获取字段错误信息
const getFieldError = (fieldName: string): string => {
  return errors.value[fieldName] || ''
}

// 事件处理
const handleSubmit = () => {
  if (props.validateOnSubmit) {
    if (!validateForm()) {
      return
    }
  }
  
  emit('submit', { ...formData })
}

const handleCancel = () => {
  emit('cancel')
}

// 监听字段变化
watch(formData, (newData, oldData) => {
  for (const [key, value] of Object.entries(newData)) {
    if (oldData && oldData[key] !== value) {
      emit('field-change', key, value)
      
      // 如果启用了blur验证，在字段变化时也进行验证
      if (props.validateOnBlur) {
        validateField(key)
      }
    }
  }
}, { deep: true })
</script>

<style scoped>
.base-form {
  width: 100%;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
}

/* Loading遮罩层 */
.base-form-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

/* 表单头部 */
.base-form-header {
  padding: 2rem 2rem 1rem;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 20px 20px 0 0;
}

.base-form-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.base-form-description {
  color: #6c757d;
  font-size: 1rem;
  margin: 0;
  line-height: 1.5;
}

/* 表单主体 */
.base-form-body {
  padding: 2rem;
}

.base-form-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.base-form-field--full-width {
  grid-column: 1 / -1;
}

/* 标签样式 */
.base-form-label {
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

/* 文本域样式 */
.base-form-textarea {
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
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.5;
}

.base-form-textarea::placeholder {
  color: #94a3b8;
  opacity: 1;
}

.base-form-textarea:focus {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.base-form-textarea--error {
  border-color: rgba(231, 76, 60, 0.5);
  background: rgba(255, 255, 255, 0.9);
}

/* 选择框样式 */
.base-form-select {
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
  cursor: pointer;
}

.base-form-select:focus {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.base-form-select--error {
  border-color: rgba(231, 76, 60, 0.5);
  background: rgba(255, 255, 255, 0.9);
}

/* 消息样式 */
.base-form-message {
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

/* 表单底部 */
.base-form-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  background: rgba(102, 126, 234, 0.02);
  border-radius: 0 0 20px 20px;
}

.base-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-form-header {
    padding: 1.5rem 1.5rem 1rem;
  }
  
  .base-form-body {
    padding: 1.5rem;
  }
  
  .base-form-fields {
    grid-template-columns: 1fr;
    gap: 1.25rem;
  }
  
  .base-form-footer {
    padding: 1rem 1.5rem;
  }
  
  .base-form-actions {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .base-form-title {
    font-size: 1.25rem;
  }
  
  .base-form-description {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .base-form-header {
    padding: 1rem 1rem 0.75rem;
  }
  
  .base-form-body {
    padding: 1rem;
  }
  
  .base-form-footer {
    padding: 0.75rem 1rem;
  }
  
  .base-form-title {
    font-size: 1.125rem;
  }
  
  .base-form-description {
    font-size: 0.875rem;
  }
  
  .base-form-textarea,
  .base-form-select {
    padding: 0.75rem;
    font-size: 0.95rem;
  }
}
</style> 