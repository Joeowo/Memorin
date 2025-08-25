<template>
  <BaseCard class="knowledge-base-detail">
    <div class="section-header">
      <h3>📁 {{ store.currentKnowledgeBase?.name }} - 知识区列表</h3>
    </div>
    
    <div class="placeholder-content">
      <p>知识库详情视图 - 待实现</p>
      <p>当前知识库: {{ store.currentKnowledgeBase?.name }}</p>
      <p>子分类数量: {{ store.currentCategories.length }}</p>
    </div>
  </BaseCard>
</template>

<script setup lang="ts">
import { useKnowledgeStore } from '@/stores/knowledgeStore'
import BaseCard from '@/components/common/BaseCard.vue'

const store = useKnowledgeStore()
</script>

<style scoped>
.section-header h3 {
  color: #2c3e50;
  margin: 0 0 1rem 0;
}

.placeholder-content {
  padding: 2rem;
  text-align: center;
  color: #6c757d;
}
</style> 