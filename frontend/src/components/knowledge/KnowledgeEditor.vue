<template>
  <BaseCard class="knowledge-editor">
    <div class="editor-header">
      <h3>{{ isEditing ? '编辑知识点' : '创建知识点' }}</h3>
      <p class="editor-subtitle">
        {{ isEditing ? '修改知识点信息' : '创建一个新的知识点' }}
      </p>
    </div>
    
    <form @submit.prevent="handleSave" class="knowledge-form">
      <!-- 知识点问题 -->
      <div class="form-group">
        <label for="knowledge-question" class="form-label">
          <span class="required">*</span> 知识点问题
        </label>
        <BaseInput
          id="knowledge-question"
          v-model="formData.question"
          placeholder="请输入知识点问题，如：什么是Vue3的响应式原理？"
          :error="errors.question"
          required
          maxlength="200"
        />
        <div v-if="errors.question" class="error-text">{{ errors.question }}</div>
      </div>
      
      <!-- 知识点解释 -->
      <div class="form-group">
        <label for="knowledge-explanation" class="form-label">
          <span class="required">*</span> 知识点解释
        </label>
        <textarea
          id="knowledge-explanation"
          v-model="formData.explanation"
          placeholder="详细解释这个知识点..."
          class="form-textarea"
          rows="4"
          maxlength="500"
          required
        />
        <div class="char-count">{{ formData.explanation?.length || 0 }}/500</div>
        <div v-if="errors.explanation" class="error-text">{{ errors.explanation }}</div>
      </div>
      
      <!-- 知识点类型 -->
      <div class="form-group">
        <label class="form-label">
          <span class="required">*</span> 知识点类型
        </label>
        <div class="type-selector">
          <label 
            v-for="type in typeOptions" 
            :key="type.value"
            class="type-option"
            :class="{ active: formData.type === type.value }"
          >
            <input
              type="radio"
              :value="type.value"
              v-model="formData.type"
              class="type-radio"
            />
            <div class="type-card">
              <div class="type-icon">{{ type.icon }}</div>
              <div class="type-info">
                <h4>{{ type.label }}</h4>
                <p>{{ type.description }}</p>
              </div>
            </div>
          </label>
        </div>
      </div>
      
      <!-- 所属分类 -->
      <div class="form-group">
        <label class="form-label">
          <span class="required">*</span> 所属分类
        </label>
        <select v-model="formData.categoryName" class="form-select" required>
          <option value="">请选择分类</option>
          <option 
            v-for="category in availableCategories" 
            :key="category.id"
            :value="category.name"
          >
            {{ category.icon }} {{ category.name }}
          </option>
        </select>
        <div v-if="errors.categoryName" class="error-text">{{ errors.categoryName }}</div>
      </div>
      
      <!-- 难度和时间 -->
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">难度等级</label>
          <div class="difficulty-slider">
            <input
              type="range"
              v-model.number="formData.difficulty"
              min="1"
              max="5"
              step="1"
              class="slider"
            />
            <div class="difficulty-labels">
              <span>简单</span>
              <span>中等</span>
              <span>困难</span>
            </div>
            <div class="difficulty-value">
              {{ getDifficultyLabel(formData.difficulty || 3) }}
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">预计学习时间（分钟）</label>
          <BaseInput
            v-model.number="formData.estimatedTime"
            type="number"
            min="1"
            max="120"
            placeholder="15"
          />
        </div>
      </div>
      
      <!-- 标签 -->
      <div class="form-group">
        <label class="form-label">标签</label>
        <div class="tags-input">
          <div class="tags-display">
            <span 
              v-for="(tag, index) in formData.tags" 
              :key="index"
              class="tag"
            >
              {{ tag }}
              <button 
                type="button" 
                @click="removeTag(index)"
                class="tag-remove"
              >
                ×
              </button>
            </span>
          </div>
          <BaseInput
            v-model="newTag"
            placeholder="输入标签后按回车添加"
            @keyup.enter="addTag"
            class="tag-input"
          />
        </div>
      </div>
      
      <!-- 预览区域 -->
      <div class="form-group">
        <label class="form-label">预览效果</label>
        <div class="knowledge-preview">
          <div class="preview-card">
            <div class="preview-header">
              <div class="preview-type">
                {{ typeOptions.find(t => t.value === formData.type)?.icon }} 
                {{ typeOptions.find(t => t.value === formData.type)?.label }}
              </div>
              <div class="preview-difficulty">
                {{ getDifficultyLabel(formData.difficulty || 3) }}
              </div>
            </div>
            <h4>{{ formData.question || '知识点问题' }}</h4>
            <p>{{ formData.explanation || '知识点解释' }}</p>
            <div class="preview-meta">
              <span class="category">📁 {{ formData.categoryName || '未选择分类' }}</span>
              <span class="time">⏱️ {{ formData.estimatedTime }}分钟</span>
            </div>
            <div v-if="(formData.tags || []).length > 0" class="preview-tags">
              <span v-for="tag in (formData.tags || [])" :key="tag" class="preview-tag">
                {{ tag }}
              </span>
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
          {{ isEditing ? '保存修改' : '创建知识点' }}
        </BaseButton>
      </div>
    </form>
  </BaseCard>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledgeStore'
import type { KnowledgeData } from '@/utils/memorin-sdk'

// 组件导入
import BaseCard from '@/components/common/BaseCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'

// Props
interface Props {
  knowledge: KnowledgeData | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  save: [data: Partial<KnowledgeData>]
  cancel: []
}>()

const store = useKnowledgeStore()

// 响应式数据
const loading = ref(false)
const newTag = ref('')
const formData = ref<Partial<KnowledgeData> & { categoryName?: string }>({
  question: '',
  explanation: '',
  type: 'text',
  categoryName: '',
  tags: [],
  difficulty: 3,
  estimatedTime: 15
})

const errors = ref<Record<string, string>>({})

// 知识点类型选项
const typeOptions = [
  {
    value: 'text',
    label: '文本题',
    icon: '📝',
    description: '填空题、问答题等文本类型'
  },
  {
    value: 'choice',
    label: '选择题',
    icon: '✅',
    description: '单选题、多选题等选择类型'
  },
  {
    value: 'code',
    label: '代码题',
    icon: '💻',
    description: '编程题、代码片段等'
  }
]

// 计算属性
const isEditing = computed(() => !!props.knowledge?.id)

const availableCategories = computed(() => store.categories)

const isFormValid = computed(() => {
  return formData.value.question && 
         formData.value.question.trim().length > 0 &&
         formData.value.explanation &&
         formData.value.explanation.trim().length > 0 &&
         formData.value.categoryName &&
         !errors.value.question &&
         !errors.value.explanation &&
         !errors.value.categoryName
})

// 表单验证
function validateForm() {
  errors.value = {}
  
  if (!formData.value.question || formData.value.question.trim().length === 0) {
    errors.value.question = '知识点问题不能为空'
  } else if (formData.value.question.trim().length > 200) {
    errors.value.question = '知识点问题不能超过200个字符'
  }
  
  if (!formData.value.explanation || formData.value.explanation.trim().length === 0) {
    errors.value.explanation = '知识点解释不能为空'
  } else if (formData.value.explanation.trim().length > 500) {
    errors.value.explanation = '知识点解释不能超过500个字符'
  }
  
  if (!formData.value.categoryName) {
    errors.value.categoryName = '请选择所属分类'
  }
  
  return Object.keys(errors.value).length === 0
}

// 工具函数
function getDifficultyLabel(difficulty: number): string {
  const labels = ['', '⭐ 入门', '⭐⭐ 简单', '⭐⭐⭐ 中等', '⭐⭐⭐⭐ 困难', '⭐⭐⭐⭐⭐ 专家']
  return labels[difficulty] || '⭐⭐⭐ 中等'
}

function addTag() {
  if (newTag.value.trim() && !formData.value.tags?.includes(newTag.value.trim())) {
    formData.value.tags = [...(formData.value.tags || []), newTag.value.trim()]
    newTag.value = ''
  }
}

function removeTag(index: number) {
  formData.value.tags?.splice(index, 1)
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
      question: formData.value.question?.trim(),
      explanation: formData.value.explanation?.trim()
    })
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  emit('cancel')
}

// 监听输入变化进行实时验证
watch(() => formData.value.question, () => {
  if (errors.value.question) validateForm()
})

watch(() => formData.value.explanation, () => {
  if (errors.value.explanation) validateForm()
})

watch(() => formData.value.categoryName, () => {
  if (errors.value.categoryName) validateForm()
})

// 生命周期
onMounted(() => {
  if (props.knowledge) {
    const category = store.categories.find(cat => cat.id === props.knowledge?.categoryId)
    formData.value = {
      question: props.knowledge.question,
      explanation: props.knowledge.explanation,
      type: props.knowledge.type,
      categoryName: category?.name || '',
      tags: [...(props.knowledge.tags || [])],
      difficulty: props.knowledge.difficulty,
      estimatedTime: props.knowledge.estimatedTime
    }
  } else {
    // 创建模式：设置默认分类
    if (store.categories.length > 0) {
      formData.value.categoryName = store.categories[0].name
    }
  }
})
</script>

<style scoped>
.knowledge-editor {
  max-width: 700px;
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

.knowledge-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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

.form-select {
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.9rem;
  background: white;
  transition: border-color 0.2s ease;
}

.form-select:focus {
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
}

.type-selector {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.type-option {
  cursor: pointer;
}

.type-radio {
  display: none;
}

.type-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.type-option.active .type-card {
  border-color: #007bff;
  background: #f8f9ff;
}

.type-card:hover {
  border-color: #007bff;
}

.type-icon {
  font-size: 2rem;
}

.type-info h4 {
  margin: 0 0 0.25rem 0;
  color: #2c3e50;
  font-size: 1rem;
}

.type-info p {
  margin: 0;
  color: #6c757d;
  font-size: 0.8rem;
}

.difficulty-slider {
  position: relative;
}

.slider {
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: #e9ecef;
  outline: none;
  appearance: none;
}

.slider::-webkit-slider-thumb {
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #007bff;
  cursor: pointer;
}

.difficulty-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #6c757d;
}

.difficulty-value {
  text-align: center;
  margin-top: 0.5rem;
  font-weight: 600;
  color: #007bff;
}

.tags-input {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 0.5rem;
  min-height: 60px;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.5rem;
  background: #007bff;
  color: white;
  border-radius: 4px;
  font-size: 0.8rem;
}

.tag-remove {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
}

.tag-input {
  border: none !important;
  box-shadow: none !important;
}

.knowledge-preview {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-card {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 1.5rem;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.preview-type {
  background: #e3f2fd;
  color: #1976d2;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.preview-difficulty {
  color: #ff9800;
  font-size: 0.9rem;
  font-weight: 600;
}

.preview-card h4 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.preview-card p {
  margin: 0 0 1rem 0;
  color: #6c757d;
  line-height: 1.4;
}

.preview-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.5rem;
  font-size: 0.8rem;
  color: #6c757d;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.preview-tag {
  padding: 0.125rem 0.5rem;
  background: #f8f9fa;
  color: #6c757d;
  border-radius: 12px;
  font-size: 0.7rem;
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
  .knowledge-editor {
    max-width: 100%;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .type-selector {
    grid-template-columns: 1fr;
  }
  
  .preview-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
}
</style> 