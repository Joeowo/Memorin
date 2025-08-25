<template>
  <div class="knowledge-area-view">
    <h3>知识区管理</h3>
    <p>知识区: {{ baseId }}</p>
    <p>知识区列表功能正在开发中...</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  baseId: string
}

defineProps<Props>()
</script>

<style scoped>
.knowledge-area-view {
  color: #fff;
}
</style> 