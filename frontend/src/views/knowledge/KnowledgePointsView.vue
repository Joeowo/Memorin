<template>
  <div class="knowledge-points-view">
    <h3>知识点管理</h3>
    <p>知识点: {{ areaId }}</p>
    <p>知识点列表功能正在开发中...</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  areaId: string
}

defineProps<Props>()
</script>

<style scoped>
.knowledge-points-view {
  color: #fff;
}
</style> 