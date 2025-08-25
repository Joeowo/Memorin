<template>
  <div class="review-session-view">
    <h3>复习会话</h3>
    <p>复习模式: {{ mode }}</p>
    <p>复习会话功能正在开发中...</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  mode: string
}

defineProps<Props>()
</script>

<style scoped>
.review-session-view {
  color: #fff;
}
</style> 