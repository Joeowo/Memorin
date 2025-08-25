<template>
  <div class="knowledge-view">
    <div class="container">
      <!-- 页面头部 -->
      <div class="knowledge-header">
      <h2>📚 知识管理</h2>
        <div class="header-actions">
          <!-- 创建按钮 -->
          <BaseButton 
            @click="handleCreateCategory"
            variant="primary"
            :disabled="store.loading"
          >
            📁 创建分类
          </BaseButton>
          
          <BaseButton 
            @click="handleCreateKnowledge"
            variant="primary"
            :disabled="store.loading"
          >
            💡 创建知识点
          </BaseButton>
          
          <!-- 批量删除按钮 -->
          <BaseButton 
            v-if="store.hasSelection"
            @click="handleBatchDelete"
            variant="danger"
            :disabled="store.loading"
          >
            🗑️ 批量删除 ({{ store.totalSelected }})
          </BaseButton>
        </div>
      </div>
      
      <!-- 错误提示 -->
      <BaseNotification
        v-if="store.error"
        type="error"
        :message="store.error"
        @close="store.clearError"
        class="error-notification"
      />
      
      <!-- 加载指示器 -->
      <LoadingSpinner v-if="store.loading" />
      
      <!-- 主要内容区域 -->
      <div class="knowledge-content" v-else>
        <!-- 分类列表视图 -->
        <KnowledgeBaseListView 
          v-if="store.currentView === 'categories'"
        />
        
        <!-- 创建分类视图 -->
        <CategoryEditor 
          v-if="store.currentView === 'create-category'"
          :category="null"
          @save="handleSaveCategory"
          @cancel="store.showCategories"
        />
        
        <!-- 编辑分类视图 -->
        <CategoryEditor 
          v-if="store.currentView === 'edit-category'"
          :category="store.currentCategory"
          @save="handleSaveCategory"
          @cancel="store.showCategories"
        />
        
        <!-- 创建知识点视图 -->
        <KnowledgeEditor 
          v-if="store.currentView === 'create-knowledge'"
          :knowledge="null"
          @save="handleSaveKnowledge"
          @cancel="store.showCategories"
        />
        
        <!-- 编辑知识点视图 -->
        <KnowledgeEditor 
          v-if="store.currentView === 'edit-knowledge'"
          :knowledge="store.currentKnowledge"
          @save="handleSaveKnowledge"
          @cancel="store.showCategories"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledgeStore'
import type { CategoryData, KnowledgeData } from '@/utils/memorin-sdk'

// 组件导入
import BaseButton from '@/components/common/BaseButton.vue'
import BaseNotification from '@/components/common/BaseNotification.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import KnowledgeBaseListView from '@/components/knowledge/KnowledgeBaseListView.vue'
import CategoryEditor from '@/components/knowledge/CategoryEditor.vue'
import KnowledgeEditor from '@/components/knowledge/KnowledgeEditor.vue'

const store = useKnowledgeStore()

// 生命周期钩子
onMounted(async () => {
  await store.loadAllData()
})

// 事件处理方法
function handleCreateCategory() {
  store.showCreateCategory()
}

function handleCreateKnowledge() {
  store.showCreateKnowledge()
}

async function handleSaveCategory(categoryData: Partial<CategoryData>) {
  try {
    if (store.currentView === 'create-category') {
      await store.createCategory({
        name: categoryData.name!,
        description: categoryData.description,
        icon: categoryData.icon,
        color: categoryData.color
      })
    } else if (store.currentView === 'edit-category' && store.currentCategory?.id) {
      await store.updateCategory(store.currentCategory.id, {
        name: categoryData.name!,
        description: categoryData.description,
        icon: categoryData.icon,
        color: categoryData.color
      })
    }
    store.showCategories()
  } catch (error) {
    console.error('Save category failed:', error)
  }
}

async function handleSaveKnowledge(knowledgeData: Partial<KnowledgeData> & { categoryName?: string }) {
  try {
    if (store.currentView === 'create-knowledge') {
      await store.createKnowledge({
        question: knowledgeData.question!,
        explanation: knowledgeData.explanation!,
        categoryName: knowledgeData.categoryName || store.categories[0]?.name || 'Default',
        difficulty: knowledgeData.difficulty || 3,
        estimatedTime: knowledgeData.estimatedTime || 15
      })
    } else if (store.currentView === 'edit-knowledge' && store.currentKnowledge?.id) {
      await store.updateKnowledge(store.currentKnowledge.id, {
        question: knowledgeData.question!,
        explanation: knowledgeData.explanation!,
        type: knowledgeData.type,
        categoryName: knowledgeData.categoryName,
        tags: knowledgeData.tags,
        difficulty: knowledgeData.difficulty,
        estimatedTime: knowledgeData.estimatedTime
      })
    }
    store.showCategories()
  } catch (error) {
    console.error('Save knowledge failed:', error)
  }
}

async function handleBatchDelete() {
  if (!confirm(`确定要删除选中的 ${store.totalSelected} 项吗？`)) {
    return
  }
  
  try {
    // 批量删除分类和知识点
    const deletePromises = []
    
    for (const categoryId of store.selectedCategoryIds) {
      deletePromises.push(store.deleteCategory(categoryId))
    }
    
    for (const knowledgeId of store.selectedKnowledgeIds) {
      deletePromises.push(store.deleteKnowledge(knowledgeId))
    }
    
    await Promise.all(deletePromises)
    store.clearSelection()
  } catch (error) {
    console.error('Batch delete failed:', error)
  }
}
</script>

<style scoped>
.knowledge-view {
  width: 100%;
  min-height: 100vh;
  background: #f8f9fa;
  padding: 2rem 0;
}

.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 0 1rem;
}

.knowledge-header h2 {
  color: #2c3e50;
  margin: 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.error-notification {
  margin-bottom: 1.5rem;
}

.knowledge-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}
</style> 