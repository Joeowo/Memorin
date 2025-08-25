<template>
  <BaseCard class="category-editor">
    <div class="editor-header">
      <h3>{{ isEditing ? '编辑分类' : '创建分类' }}</h3>
      <p class="editor-subtitle">
        {{ isEditing ? '修改分类信息' : '创建一个新的知识分类' }}
      </p>
    </div>
    
    <form @submit.prevent="handleSave" class="category-form">
      <!-- 分类名称 -->
      <div class="form-group">
        <label for="category-name" class="form-label">
          <span class="required">*</span> 分类名称
        </label>
        <BaseInput
          id="category-name"
          v-model="formData.name"
          placeholder="请输入分类名称，如：前端开发、数学基础..."
          :error="errors.name"
          required
          maxlength="50"
        />
        <div v-if="errors.name" class="error-text">{{ errors.name }}</div>
      </div>
      
      <!-- 分类描述 -->
      <div class="form-group">
        <label for="category-description" class="form-label">分类描述</label>
        <textarea
          id="category-description"
          v-model="formData.description"
          placeholder="简要描述这个分类的内容和用途..."
          class="form-textarea"
          rows="3"
          maxlength="200"
        />
        <div class="char-count">{{ formData.description?.length || 0 }}/200</div>
      </div>
      
      <!-- 图标选择 -->
      <div class="form-group">
        <label class="form-label">分类图标</label>
        <div class="icon-selector">
          <div class="icon-preview">
            <div 
              class="icon-display" 
              :style="{ backgroundColor: formData.color || '#667eea' }"
            >
              {{ formData.icon || '📁' }}
            </div>
            <div class="icon-info">
              <p>当前图标: {{ formData.icon || '📁' }}</p>
              <p>当前颜色: {{ formData.color || '#667eea' }}</p>
            </div>
          </div>
          
          <div class="icon-grid">
            <button
              v-for="icon in iconOptions"
              :key="icon"
              type="button"
              @click="formData.icon = icon"
              class="icon-option"
              :class="{ active: formData.icon === icon }"
            >
              {{ icon }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 颜色选择 -->
      <div class="form-group">
        <label class="form-label">分类颜色</label>
        <div class="color-selector">
          <button
            v-for="color in colorOptions"
            :key="color"
            type="button"
            @click="formData.color = color"
            class="color-option"
            :class="{ active: formData.color === color }"
            :style="{ backgroundColor: color }"
            :title="color"
          />
        </div>
      </div>
      
      <!-- 预览区域 -->
      <div class="form-group">
        <label class="form-label">预览效果</label>
        <div class="category-preview">
          <div class="preview-card">
            <div class="preview-icon" :style="{ backgroundColor: formData.color || '#667eea' }">
              {{ formData.icon || '📁' }}
            </div>
            <div class="preview-content">
              <h4>{{ formData.name || '分类名称' }}</h4>
              <p>{{ formData.description || '分类描述' }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="form-actions">
        <BaseButton 
          type="button"
          @click="handleCancel" 
          variant="secondary"
          :disabled="loading"
        >
          取消
        </BaseButton>
        <BaseButton 
          type="submit"
          variant="primary"
          :loading="loading"
          :disabled="!isFormValid"
        >
          {{ isEditing ? '保存修改' : '创建分类' }}
        </BaseButton>
      </div>
    </form>
  </BaseCard>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { CategoryData } from '@/utils/memorin-sdk'

// 组件导入
import BaseCard from '@/components/common/BaseCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'

// Props
interface Props {
  category: CategoryData | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  save: [data: Partial<CategoryData>]
  cancel: []
}>()

// 响应式数据
const loading = ref(false)
const formData = ref<Partial<CategoryData>>({
  name: '',
  description: '',
  icon: '📁',
  color: '#667eea'
})

const errors = ref<Record<string, string>>({})

// 图标选项
const iconOptions = [
  '📁', '📚', '💻', '🎨', '🔬', '📊', '🎵', '🏃‍♂️',
  '🍕', '🌱', '🔧', '💡', '🎯', '📝', '🌟', '🔥',
  '⚡', '🚀', '💎', '🎭', '🏆', '🎪', '🎨', '🎸'
]

// 颜色选项
const colorOptions = [
  '#667eea', '#764ba2', '#f093fb', '#f5576c', 
  '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
  '#ffecd2', '#fcb69f', '#a8edea', '#fed6e3',
  '#d299c2', '#fef9d7', '#667db6', '#0082c8'
]

// 计算属性
const isEditing = computed(() => !!props.category?.id)

const isFormValid = computed(() => {
  return formData.value.name && 
         formData.value.name.trim().length > 0 &&
         !errors.value.name
})

// 表单验证
function validateForm() {
  errors.value = {}
  
  if (!formData.value.name || formData.value.name.trim().length === 0) {
    errors.value.name = '分类名称不能为空'
  } else if (formData.value.name.trim().length > 50) {
    errors.value.name = '分类名称不能超过50个字符'
  }
  
  return Object.keys(errors.value).length === 0
}

// 事件处理
async function handleSave() {
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  try {
    emit('save', {
      ...formData.value,
      name: formData.value.name?.trim()
    })
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  emit('cancel')
}

// 监听输入变化进行实时验证
watch(() => formData.value.name, () => {
  if (errors.value.name) {
    validateForm()
  }
})

// 生命周期
onMounted(() => {
  if (props.category) {
    formData.value = {
      name: props.category.name,
      description: props.category.description,
      icon: props.category.icon,
      color: props.category.color
    }
  }
})
</script>

<style scoped>
.category-editor {
  max-width: 600px;
  margin: 0 auto;
}

.editor-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e9ecef;
}

.editor-header h3 {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.editor-subtitle {
  color: #6c757d;
  margin: 0;
  font-size: 0.9rem;
}

.category-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
}

.required {
  color: #dc3545;
  margin-right: 0.25rem;
}

.form-textarea {
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-family: inherit;
  font-size: 0.9rem;
  resize: vertical;
  transition: border-color 0.2s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #6c757d;
}

.error-text {
  color: #dc3545;
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.icon-selector {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.icon-preview {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.icon-display {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
}

.icon-info p {
  margin: 0.25rem 0;
  font-size: 0.8rem;
  color: #6c757d;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(50px, 1fr));
  gap: 0.5rem;
  max-height: 200px;
  overflow-y: auto;
  padding: 0.5rem;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.icon-option {
  width: 50px;
  height: 50px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  background: white;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-option:hover {
  border-color: #007bff;
  transform: scale(1.05);
}

.icon-option.active {
  border-color: #007bff;
  background: #f8f9ff;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2);
}

.color-selector {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  gap: 0.5rem;
  max-width: 400px;
}

.color-option {
  width: 40px;
  height: 40px;
  border: 3px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-option:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.color-option.active {
  border-color: #2c3e50;
  transform: scale(1.1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.category-preview {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
}

.preview-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.preview-content h4 {
  margin: 0 0 0.25rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.preview-content p {
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
  margin-top: 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-editor {
    max-width: 100%;
  }
  
  .icon-preview {
    flex-direction: column;
    text-align: center;
  }
  
  .icon-grid {
    grid-template-columns: repeat(6, 1fr);
  }
  
  .color-selector {
    grid-template-columns: repeat(6, 1fr);
  }
  
  .preview-card {
    flex-direction: column;
    text-align: center;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
}
</style> 