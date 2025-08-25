<template>
  <BaseCard class="category-detail">
    <div class="section-header">
      <h3>💡 {{ store.currentCategory?.name }} - 知识点列表</h3>
    </div>
    
    <div class="placeholder-content">
      <p>分类详情视图 - 待实现</p>
      <p>当前分类: {{ store.currentCategory?.name }}</p>
      <p>知识点数量: {{ store.currentKnowledge.length }}</p>
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