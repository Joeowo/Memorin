<template>
  <BaseCard class="knowledge-base-list">
    <div class="section-header">
      <h3>📁 分类管理</h3>
      <div class="list-controls">
        <BaseSelect
          v-model="selectedLevel"
          :options="levelOptions"
          placeholder="选择层级"
          :disabled="store.loading"
          class="level-select"
        />
        <BaseInput
          v-model="searchQuery"
          placeholder="搜索分类..."
          @input="handleSearch"
          :disabled="store.loading"
          class="search-input"
        />
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-if="filteredCategories.length === 0" class="empty-state">
      <div class="empty-icon">📁</div>
      <h4>暂无分类</h4>
      <p v-if="searchQuery">
        没有找到包含 "{{ searchQuery }}" 的分类
      </p>
      <p v-else>
        还没有创建任何分类，点击上方按钮创建第一个吧！
      </p>
    </div>
    
    <!-- 分类网格 -->
    <div v-else class="categories-grid">
      <div
        v-for="category in filteredCategories"
        :key="category.id"
        @click="handleCategoryClick(category)"
        class="category-card"
        :class="{ 
          'selected': store.selectedCategoryIds.includes(category.id)
        }"
      >
        <!-- 选择框 -->
        <input
          type="checkbox"
          :checked="store.selectedCategoryIds.includes(category.id)"
          @change.stop="store.toggleCategorySelection(category.id)"
          class="category-checkbox"
        />
        
        <!-- 分类图标 -->
        <div class="category-icon" :style="{ backgroundColor: category.color }">
          {{ category.icon }}
        </div>
        
        <!-- 分类信息 -->
        <div class="category-info">
          <h4 class="category-name">{{ category.name }}</h4>
          <p class="category-description">{{ category.description || '暂无描述' }}</p>
          
          <!-- 统计信息 -->
          <div class="category-stats">
            <BaseBadge variant="info" size="small">
              Level {{ category.level }}
            </BaseBadge>
            <BaseBadge v-if="category.parentId" variant="secondary" size="small">
              子分类
            </BaseBadge>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="category-actions" @click.stop>
          <BaseButton
            @click="handleEdit(category)"
            variant="secondary"
            size="small"
            title="编辑分类"
          >
            ✏️
          </BaseButton>
          <BaseButton
            @click="handleDelete(category)"
            variant="danger"
            size="small"
            title="删除分类"
          >
            🗑️
          </BaseButton>
        </div>
      </div>
    </div>
    
    <!-- 知识点筛选 -->
    <div class="knowledge-filter-section">
      <BaseButton
        @click="showCategoryFilter = true"
        variant="secondary"
        class="filter-button"
      >
        🔍 按分类筛选
        <span v-if="selectedCategories.length > 0" class="filter-count">
          ({{ selectedCategories.length }})
        </span>
      </BaseButton>
      
      <div v-if="selectedCategories.length > 0" class="selected-categories">
        <span
          v-for="category in selectedCategories"
          :key="category.id"
          class="selected-tag"
        >
          {{ getCategoryPath(category) }}
          <button @click="removeCategory(category.id)" class="remove-btn">×</button>
        </span>
      </div>
    </div>

    <!-- 知识点列表 -->
    <div v-if="filteredKnowledge.length > 0" class="knowledge-section">
      <h4 class="section-title">💡 知识点列表 ({{ filteredKnowledge.length }})</h4>
      
      <!-- 层级化展示 -->
      <div class="knowledge-hierarchy">
        <div
          v-for="item in hierarchicalKnowledge"
          :key="item.key"
          class="knowledge-group"
        >
          <!-- 分类标题 -->
          <div
            :class="`category-title level-${item.level}`"
            :style="{ marginLeft: `${(item.level - 1) * 20}px` }"
          >
            {{ item.name }}
            <BaseBadge v-if="item.count > 0" variant="info" size="small">{{ item.count }}</BaseBadge>
          </div>
          
          <!-- 知识点卡片 -->
          <div
            v-for="knowledge in item.knowledge"
            :key="knowledge.id"
            class="knowledge-card"
            :class="{ 'selected': store.selectedKnowledgeIds.includes(knowledge.id) }"
            :style="{ marginLeft: `${item.level * 20}px` }"
          >
              <!-- 选择框 -->
            <input
              type="checkbox"
              :checked="store.selectedKnowledgeIds.includes(knowledge.id)"
              @change.stop="store.toggleKnowledgeSelection(knowledge.id)"
              class="knowledge-checkbox"
            />
          
            <!-- 知识点类型标识 -->
            <div class="knowledge-type">
              {{ getTypeIcon(knowledge.type) }}
            </div>
          
            <!-- 知识点内容 -->
            <div class="knowledge-content">
              <h5 class="knowledge-question">{{ knowledge.question }}</h5>
              <p class="knowledge-explanation">{{ knowledge.explanation }}</p>
              
              <!-- 元信息 -->
              <div class="knowledge-meta">
                <span class="difficulty">{{ getDifficultyLabel(knowledge.difficulty) }}</span>
                <span class="time">⏱️ {{ knowledge.estimatedTime }}分钟</span>
                <span class="type">{{ getTypeLabel(knowledge.type) }}</span>
              </div>
              
              <!-- 标签 -->
              <div v-if="knowledge.tags && knowledge.tags.length > 0" class="knowledge-tags">
                <span v-for="tag in knowledge.tags" :key="tag" class="knowledge-tag">
                  {{ tag }}
                </span>
              </div>
            </div>
          
            <!-- 操作按钮 -->
            <div class="knowledge-actions" @click.stop>
              <BaseButton
                @click="handleEditKnowledge(knowledge)"
                variant="secondary"
                size="small"
                title="编辑知识点"
              >
                ✏️
              </BaseButton>
              <BaseButton
                @click="handleDeleteKnowledge(knowledge)"
                variant="danger"
                size="small"
                title="删除知识点"
              >
                🗑️
              </BaseButton>
            </div>
        </div>
      </div>
    </div>
  </div>
    
    <!-- 统计信息 -->
    <div class="stats-footer">
      <p>
        共 {{ store.stats.totalCategories }} 个分类，
        {{ store.stats.totalKnowledge }} 个知识点，
        {{ store.stats.totalTextQuestions }} 个文本题
      </p>
    </div>
    
    <!-- 调试信息（开发环境可见） -->
    <div v-if="isDev" class="debug-info">
      <small>
        数据已连接到真实后端服务 (localhost:8082)
      </small>
    </div>
  </BaseCard>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledgeStore'
import type { CategoryData, KnowledgeData } from '@/utils/memorin-sdk'

// 组件导入
import BaseCard from '@/components/common/BaseCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseBadge from '@/components/common/BaseBadge.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
// import CategoryFilterModal from './CategoryFilterModal.vue'

const store = useKnowledgeStore()

// 响应式数据
const searchQuery = ref('')
const selectedLevel = ref(1)
const showCategoryFilter = ref(false)
const selectedCategoryIds = ref<string[]>([])

// 计算属性
const levelOptions = computed(() => {
  const maxLevel = Math.max(...store.categories.map(cat => cat.level), 1)
  return Array.from({ length: maxLevel }, (_, i) => ({
    label: `Level ${i + 1}`,
    value: i + 1
  }))
})

const filteredCategories = computed(() => {
  let categories = store.categories
  
  // 按层级筛选
  if (selectedLevel.value) {
    categories = categories.filter(category => category.level === selectedLevel.value)
  }
  
  // 按搜索词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    categories = categories.filter(category => 
      category.name.toLowerCase().includes(query) ||
      (category.description && category.description.toLowerCase().includes(query))
    )
  }
  
  return categories
})

const selectedCategories = computed(() => {
  return store.categories.filter(cat => selectedCategoryIds.value.includes(cat.id))
})

const filteredKnowledge = computed(() => {
  let knowledge = store.knowledgePoints
  
  // 按分类筛选
  if (selectedCategoryIds.value.length > 0) {
    knowledge = knowledge.filter(k => 
      selectedCategoryIds.value.includes(k.categoryId)
    )
  }
  
  // 按搜索词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    knowledge = knowledge.filter(knowledge => 
      knowledge.question.toLowerCase().includes(query) ||
      knowledge.explanation.toLowerCase().includes(query) ||
      (knowledge.tags && knowledge.tags.some(tag => tag.toLowerCase().includes(query)))
    )
  }
  
  return knowledge
})

const hierarchicalKnowledge = computed(() => {
  if (selectedCategoryIds.value.length === 0) {
    // 如果没有选择分类，按原始平铺方式展示
    return [{
      key: 'all',
      name: '所有知识点',
      level: 1,
      count: filteredKnowledge.value.length,
      knowledge: filteredKnowledge.value
    }]
  }
  
  // 构建层级化的展示结构
  const result: any[] = []
  const processedCategories = new Set<string>()
  
  // 获取所有选中的分类及其祖先
  const categoriesWithAncestors = new Set<string>()
  selectedCategoryIds.value.forEach(id => {
    collectAncestors(id, categoriesWithAncestors)
    categoriesWithAncestors.add(id)
  })
  
  // 按层级排序展示
  const sortedCategories = Array.from(categoriesWithAncestors)
    .map(id => store.categories.find(cat => cat.id === id))
    .filter(Boolean)
    .sort((a, b) => {
      if (a.level !== b.level) return a.level - b.level
      if (a.parentId !== b.parentId) {
        return (a.parentId || '').localeCompare(b.parentId || '')
      }
      return a.name.localeCompare(b.name)
    })
  
  // 构建层级结构
  sortedCategories.forEach(category => {
    const knowledgeInCategory = filteredKnowledge.value.filter(
      k => k.categoryId === category.id
    )
    
    if (knowledgeInCategory.length > 0 || selectedCategoryIds.value.includes(category.id)) {
      result.push({
        key: category.id,
        name: category.name,
        level: category.level,
        count: knowledgeInCategory.length,
        knowledge: knowledgeInCategory,
        category
      })
    }
  })
  
  return result
})

function collectAncestors(categoryId: string, result: Set<string>) {
  const category = store.categories.find(cat => cat.id === categoryId)
  if (category?.parentId) {
    result.add(category.parentId)
    collectAncestors(category.parentId, result)
  }
}

const isDev = computed(() => {
  return import.meta.env.DEV
})

// 工具函数
function getTypeIcon(type: string): string {
  const icons = {
    'text': '📝',
    'choice': '✅', 
    'code': '💻'
  }
  return icons[type as keyof typeof icons] || '📄'
}

function getTypeLabel(type: string): string {
  const labels = {
    'text': '文本题',
    'choice': '选择题',
    'code': '代码题'
  }
  return labels[type as keyof typeof labels] || '未知类型'
}

function getDifficultyLabel(difficulty: number): string {
  const labels = ['', '⭐ 入门', '⭐⭐ 简单', '⭐⭐⭐ 中等', '⭐⭐⭐⭐ 困难', '⭐⭐⭐⭐⭐ 专家']
  return labels[difficulty] || '⭐⭐⭐ 中等'
}

// 事件处理方法
function handleSearch() {
  // 搜索在计算属性中实时处理
}

function handleCategoryClick(category: CategoryData) {
  // 可以导航到分类详情或其他操作
  console.log('Category clicked:', category)
}

function getCategoryPath(category: CategoryData): string {
  const path = [category.name]
  let parent = store.categories.find(c => c.id === category.parentId)
  while (parent) {
    path.unshift(parent.name)
    parent = store.categories.find(c => c.id === parent.parentId)
  }
  return path.join(' > ')
}

function removeCategory(categoryId: string) {
  const index = selectedCategoryIds.value.indexOf(categoryId)
  if (index > -1) {
    selectedCategoryIds.value.splice(index, 1)
  }
}

function handleCategoryFilterConfirm(selectedIds: string[]) {
  selectedCategoryIds.value = selectedIds
  showCategoryFilter.value = false
}

function handleEdit(category: CategoryData) {
  store.showEditCategory(category)
}

async function handleDelete(category: CategoryData) {
  if (!confirm(`确定要删除分类 "${category.name}" 吗？`)) {
    return
  }
  
  try {
    await store.deleteCategory(category.id)
  } catch (error) {
    console.error('Delete category failed:', error)
  }
}

function handleEditKnowledge(knowledge: KnowledgeData) {
  store.showEditKnowledge(knowledge)
}

async function handleDeleteKnowledge(knowledge: KnowledgeData) {
  if (!confirm(`确定要删除知识点 "${knowledge.question}" 吗？`)) {
    return
  }
  
  try {
    await store.deleteKnowledge(knowledge.id)
  } catch (error) {
    console.error('Delete knowledge failed:', error)
  }
}
</script>

<style scoped>
.knowledge-base-list {
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h3 {
  color: #2c3e50;
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.list-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.level-select {
  min-width: 120px;
}

.search-input {
  min-width: 200px;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #6c757d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h4 {
  color: #495057;
  margin-bottom: 0.5rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.category-card {
  position: relative;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-card:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
  transform: translateY(-2px);
}

.category-card.selected {
  border-color: #007bff;
  background: #f8f9ff;
}

.category-checkbox {
  position: absolute;
  top: 12px;
  right: 12px;
  transform: scale(1.2);
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin-bottom: 1rem;
  color: white;
}

.category-info {
  flex: 1;
}

.category-name {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  font-weight: 600;
  line-height: 1.3;
}

.category-description {
  color: #6c757d;
  margin: 0 0 1rem 0;
  font-size: 0.9rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-stats {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.category-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.stats-footer {
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
  text-align: center;
  color: #6c757d;
  font-size: 0.9rem;
}

.stats-footer p {
  margin: 0;
}

/* 知识点列表样式 */
.knowledge-filter-section {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.filter-button {
  align-self: flex-start;
}

.filter-count {
  margin-left: 0.25rem;
  font-size: 0.8rem;
}

.selected-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background: #007bff;
  color: white;
  border-radius: 12px;
  font-size: 0.8rem;
}

.remove-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.knowledge-section {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.knowledge-hierarchy {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.knowledge-group {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.category-title {
  padding: 0.75rem 1rem;
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #e9ecef;
}

.category-title.level-1 {
  font-size: 1.2rem;
  background: #e3f2fd;
}

.category-title.level-2 {
  font-size: 1.1rem;
  background: #f5f5f5;
}

.category-title.level-3 {
  font-size: 1rem;
  background: #fafafa;
}

.category-title.level-4,
.category-title.level-5 {
  font-size: 0.9rem;
  background: #ffffff;
}

.empty-group {
  padding: 1rem;
  color: #6c757d;
  font-style: italic;
  text-align: center;
}

.text-muted {
  color: #6c757d;
}

.knowledge-section {
  margin-top: 3rem;
  padding-top: 2rem;
  border-top: 2px solid #e9ecef;
}

.section-title {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.knowledge-card {
  position: relative;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.knowledge-card:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
  transform: translateY(-2px);
}

.knowledge-card.selected {
  border-color: #007bff;
  background: #f8f9ff;
}

.knowledge-checkbox {
  position: absolute;
  top: 12px;
  right: 12px;
  transform: scale(1.2);
}

.knowledge-type {
  position: absolute;
  top: 12px;
  left: 12px;
  font-size: 1.5rem;
}

.knowledge-content {
  margin-top: 1rem;
}

.knowledge-question {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
  line-height: 1.3;
}

.knowledge-explanation {
  color: #6c757d;
  margin: 0 0 1rem 0;
  font-size: 0.9rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.knowledge-meta span {
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.difficulty {
  background: #fff3cd;
  color: #856404;
}

.time {
  background: #d1ecf1;
  color: #0c5460;
}

.type {
  background: #e2e3e5;
  color: #383d41;
}

.knowledge-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
  margin-bottom: 1rem;
}

.knowledge-tag {
  padding: 0.125rem 0.5rem;
  background: #f8f9fa;
  color: #6c757d;
  border-radius: 10px;
  font-size: 0.7rem;
}

.knowledge-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }
  
  .search-input {
    min-width: auto;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
  
  .knowledge-grid {
    grid-template-columns: 1fr;
  }
}

.debug-info {
  margin-top: 1rem;
  padding: 0.5rem;
  background: #f0f8ff;
  border-left: 3px solid #007bff;
  color: #0066cc;
  text-align: center;
  font-size: 0.8rem;
}
</style> 