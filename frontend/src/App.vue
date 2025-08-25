<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppHeader from './components/common/AppHeader.vue'
</script>

<template>
  <div id="app">
    <AppHeader />
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<style>
/* 全局基础样式 - 参考原项目的毛玻璃蓝紫色主题 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  background: linear-gradient(135deg, #ffffff 0%, #f0f4ff 40%, #e6edff 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  margin: 0;
  padding: 0;
  width: 100%;
}

/* 背景装饰元素 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.10) 0%, transparent 50%),
    radial-gradient(circle at 40% 60%, rgba(102, 126, 234, 0.08) 0%, transparent 50%);
  z-index: -1;
  animation: backgroundFloat 20s ease-in-out infinite;
}

@keyframes backgroundFloat {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  33% { transform: translateY(-10px) rotate(1deg); }
  66% { transform: translateY(5px) rotate(-1deg); }
}

#app {
  min-height: 100vh;
  width: 100%;
}

.app-main {
  padding-top: var(--header-height, 120px); /* 动态导航栏高度，默认120px */
  min-height: calc(100vh - var(--header-height, 120px));
  width: 100%;
}

/* 确保所有页面内容都不被导航栏遮挡 */
.app-main > * {
  position: relative;
  z-index: 1;
}

/* 为所有视图组件添加安全的上边距 */
[class*="-view"] {
  padding-top: 1rem !important;
  min-height: auto !important;
}

/* 重置可能影响布局的样式 */
.main-layout {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 通用动画 */
* {
  transition: all 0.3s ease;
}
</style>
