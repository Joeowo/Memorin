<template>
  <div class="main-layout">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 主布局组件逻辑
</script>

<style scoped>
.main-layout {
  width: 100%;
  padding: 2rem 0;
  min-height: calc(100vh - 140px);
  position: relative;
  z-index: 1;
}
</style> 