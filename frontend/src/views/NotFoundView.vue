<template>
  <div class="not-found-view">
    <div class="error-content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在。</p>
      <router-link to="/" class="btn btn-primary">返回首页</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// 404页面逻辑
</script>

<style scoped>
.not-found-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  text-align: center;
}

.error-content {
  color: #fff;
}

.error-content h1 {
  font-size: 6rem;
  margin-bottom: 1rem;
}

.error-content h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.error-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: rgba(255, 255, 255, 0.8);
}

.btn {
  padding: 0.8rem 1.5rem;
  background: #007bff;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.btn:hover {
  background: #0056b3;
}
</style> 