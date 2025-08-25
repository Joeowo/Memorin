<template>
  <BaseCard class="knowledge-breadcrumb">
    <div class="breadcrumb-content">
      <!-- 面包屑路径 -->
      <nav class="breadcrumb-nav">
        <ol class="breadcrumb-list">
          <li 
            v-for="(item, index) in store.breadcrumb" 
            :key="item.id || 'root'"
            class="breadcrumb-item"
            :class="{ 'active': index === store.breadcrumb.length - 1 }"
          >
            <button
              v-if="index < store.breadcrumb.length - 1"
              @click="handleNavigate(item, index)"
              class="breadcrumb-link"
              :disabled="store.loading"
            >
              {{ item.name }}
            </button>
            <span v-else class="breadcrumb-current">
              {{ item.name }}
            </span>
            
            <!-- 分隔符 -->
            <span 
              v-if="index < store.breadcrumb.length - 1" 
              class="breadcrumb-separator"
            >
              >
            </span>
          </li>
        </ol>
      </nav>
      
      <!-- 当前层级统计信息 -->
      <div class="breadcrumb-stats" v-if="currentLevelStats">
        <BaseBadge 
          variant="info" 
          size="small"
          class="stats-badge"
        >
          {{ currentLevelStats.text }}
        </BaseBadge>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <LoadingSpinner 
      v-if="store.loading" 
      size="small" 
      class="breadcrumb-loading"
    />
  </BaseCard>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledgeStore'
import type { BreadcrumbItem } from '@/types/knowledge'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseBadge from '@/components/common/BaseBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

const store = useKnowledgeStore()

// 当前层级统计信息
const currentLevelStats = computed(() => {
  if (store.isKnowledgeBaseListView) {
    return {
      text: `${store.knowledgeBases.length} 个知识库`
    }
  } else if (store.isKnowledgeBaseDetailView) {
    return {
      text: `${store.currentCategories.length} 个知识区`
    }
  } else if (store.isCategoryDetailView) {
    const categoryCount = store.currentCategories.length
    const knowledgeCount = store.currentKnowledge.length
    return {
      text: `${categoryCount} 个子分类, ${knowledgeCount} 个知识点`
    }
  }
  return null
})

// 导航处理
const handleNavigate = async (item: BreadcrumbItem, index: number) => {
  if (store.loading) return
  
  // 根据层级和位置决定导航方式
  if (index === 0) {
    // 点击"知识管理"，回到知识库列表
    await store.navigateToKnowledgeBaseList()
  } else if (item.level === 1) {
    // 点击知识库名称，进入知识库详情
    await store.navigateToKnowledgeBase(item.id!)
  } else if (item.level >= 2) {
    // 点击知识区名称，进入分类详情
    await store.navigateToCategory(item.id!)
  }
}
</script>

<style scoped>
.knowledge-breadcrumb {
  margin-bottom: 1.5rem;
  position: relative;
}

.breadcrumb-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.breadcrumb-nav {
  flex: 1;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.breadcrumb-link {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  text-decoration: none;
}

.breadcrumb-link:hover:not(:disabled) {
  color: #0056b3;
  background: #f8f9fa;
}

.breadcrumb-link:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.breadcrumb-current {
  color: #2c3e50;
  font-weight: 500;
  font-size: 0.9rem;
}

.breadcrumb-separator {
  color: #6c757d;
  margin: 0 0.25rem;
  font-weight: 500;
}

.breadcrumb-stats {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.stats-badge {
  background: #e9ecef !important;
  color: #495057 !important;
  border: 1px solid #dee2e6 !important;
}

.breadcrumb-loading {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .breadcrumb-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .breadcrumb-stats {
    align-self: flex-end;
  }
  
  .breadcrumb-list {
    flex-wrap: wrap;
  }
}
</style> 