<template>
  <header class="app-header" ref="headerRef">
    <div class="header-content">
      <div class="logo">
        <h1>Memorin</h1>
        <span class="subtitle">知识复习系统</span>
      </div>
      <nav class="main-nav">
        <ul class="nav-list">
          <li>
            <router-link to="/" class="nav-link" active-class="active" exact-active-class="active">
              仪表板
            </router-link>
          </li>
          <li>
            <router-link to="/knowledge" class="nav-link" active-class="active">
              知识管理
            </router-link>
          </li>
          <li>
            <router-link to="/review" class="nav-link" active-class="active">
              开始复习
            </router-link>
          </li>
          <li>
            <router-link to="/mistakes" class="nav-link" active-class="active">
              错题本
            </router-link>
          </li>
          <li>
            <router-link to="/statistics" class="nav-link" active-class="active">
              统计分析
            </router-link>
          </li>
          <li>
            <router-link to="/notes" class="nav-link" active-class="active">
              笔记编辑器
            </router-link>
          </li>
          <li>
            <router-link to="/test" class="nav-link" active-class="active">
              组件测试
            </router-link>
          </li>
        </ul>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 全局导航栏组件
const headerRef = ref<HTMLElement>()

// 动态检测并设置导航栏高度
const updateHeaderHeight = () => {
  if (headerRef.value) {
    const height = headerRef.value.offsetHeight
    document.documentElement.style.setProperty('--header-height', `${height}px`)
    console.log('Header height updated:', height + 'px') // 调试信息
  }
}

// 响应式监听器
let resizeObserver: ResizeObserver | null = null

onMounted(() => {
  // 初始设置
  updateHeaderHeight()
  
  // 监听窗口大小变化
  window.addEventListener('resize', updateHeaderHeight)
  
  // 使用ResizeObserver监听导航栏自身高度变化
  if (headerRef.value && window.ResizeObserver) {
    resizeObserver = new ResizeObserver(() => {
      updateHeaderHeight()
    })
    resizeObserver.observe(headerRef.value)
  }
  
  // 延迟执行一次，确保DOM完全渲染
  setTimeout(updateHeaderHeight, 100)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateHeaderHeight)
  if (resizeObserver) {
    resizeObserver.disconnect()
  }
})
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 0 0 20px 20px;
  color: white;
  padding: 1rem 0;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  margin: 0;
  width: 100%;
}

.app-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  margin: 0 auto;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  box-sizing: border-box;
}

.logo h1 {
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  background: linear-gradient(135deg, #fff 0%, #e8e8ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.2rem;
}

.subtitle {
  font-size: 0.9rem;
  opacity: 0.9;
  color: rgba(255, 255, 255, 0.9);
}

.nav-list {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 1rem;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 0.8rem 1.5rem;
  border-radius: 15px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-weight: 500;
  position: relative;
  display: inline-block;
  z-index: 1;
}

.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  border-radius: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: -1;
}

.nav-link:hover::before,
.nav-link.active::before {
  opacity: 1;
}

.nav-link:hover,
.nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 1);
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow: 
    0 4px 20px rgba(255, 255, 255, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (min-width: 1400px) {
  .nav-list {
    gap: 1.2rem;
  }
  
  .nav-link {
    padding: 0.9rem 1.8rem;
    font-size: 1.05rem;
  }
}

@media (min-width: 1920px) {
  .header-content {
    padding-left: 40px;
    padding-right: 40px;
  }
  
  .logo h1 {
    font-size: 2.5rem;
  }
  
  .nav-link {
    padding: 1rem 2rem;
    font-size: 1.1rem;
  }
}

@media (max-width: 1024px) {
  .header-content {
    padding-left: 15px;
    padding-right: 15px;
  }
  
  .nav-list {
    gap: 0.8rem;
  }
  
  .nav-link {
    padding: 0.4rem 0.8rem;
    font-size: 0.95rem;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    padding: 0.8rem 20px;
  }
  
  .logo h1 {
    font-size: 1.8rem;
  }
  
  .subtitle {
    font-size: 0.8rem;
  }
  
  .nav-list {
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.5rem;
  }
  
  .nav-link {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding-left: 10px;
    padding-right: 10px;
    padding-top: 0.6rem;
    padding-bottom: 0.6rem;
  }
  
  .logo h1 {
    font-size: 1.5rem;
  }
  
  .nav-list {
    grid-template-columns: repeat(2, 1fr);
    display: grid;
    width: 100%;
    gap: 0.4rem;
  }
  
  .nav-link {
    text-align: center;
    padding: 0.5rem 0.4rem;
    font-size: 0.8rem;
  }
}
</style> 