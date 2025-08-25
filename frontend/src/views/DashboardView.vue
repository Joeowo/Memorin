<template>
  <div class="dashboard-view">
    <div class="container">
      <div class="dashboard-header">
      <h2>学习概览</h2>
      <p class="welcome-text">欢迎回到Memorin，继续您的学习之旅！</p>
    </div>
    
    <div class="stats-grid">
      <BaseCard variant="stat">
        <div class="stat-icon">📚</div>
        <div class="stat-content">
          <h3>{{ stats.totalKnowledge }}</h3>
          <p>总知识点</p>
        </div>
      </BaseCard>
      <BaseCard variant="stat">
        <div class="stat-icon">🎯</div>
        <div class="stat-content">
          <h3>{{ stats.todayReview }}</h3>
          <p>今日待复习</p>
        </div>
      </BaseCard>
      <BaseCard variant="stat">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <h3>{{ stats.masteredCount }}</h3>
          <p>已掌握</p>
        </div>
      </BaseCard>
      <BaseCard variant="stat">
        <div class="stat-icon">❌</div>
        <div class="stat-content">
          <h3>{{ stats.mistakesCount }}</h3>
          <p>错题数量</p>
        </div>
      </BaseCard>
    </div>

    <BaseCard variant="action">
      <h3>快速操作</h3>
      <div class="action-grid">
        <BaseButton variant="primary" @click="startReview">开始复习</BaseButton>
        <BaseButton variant="secondary" @click="reviewMistakes">复习错题</BaseButton>
        <BaseButton variant="info" @click="openNotes">📝 笔记编辑器</BaseButton>
      </div>
    </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import { notifySuccess, notifyInfo, notifyWarning } from '@/utils/notification'

const router = useRouter()

// 统计数据
const stats = reactive({
  totalKnowledge: 0,
  todayReview: 0,
  masteredCount: 0,
  mistakesCount: 0
})

// 快速操作
const startReview = () => {
  if (stats.todayReview === 0) {
    notifyInfo('今日没有待复习的知识点', '复习提醒')
    return
  }
  
  notifySuccess('开始复习，加油！', '复习开始')
  router.push('/review')
}

const reviewMistakes = () => {
  if (stats.mistakesCount === 0) {
    notifyInfo('恭喜！暂无错题需要复习', '错题提醒')
    return
  }
  
  notifyInfo(`准备复习 ${stats.mistakesCount} 道错题`, '错题复习')
  router.push('/mistakes')
}

const openNotes = () => {
  notifyInfo('正在打开笔记编辑器...', '笔记编辑器')
  router.push('/notes')
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 这里后续会从store中获取数据
    // 暂时使用模拟数据
    stats.totalKnowledge = 150
    stats.todayReview = 12
    stats.masteredCount = 89
    stats.mistakesCount = 8
    
    // 数据加载完成通知
    notifySuccess('数据加载完成', '欢迎回来')
    
    // 如果有待复习的内容，提醒用户
    if (stats.todayReview > 0) {
      setTimeout(() => {
        notifyWarning(`您有 ${stats.todayReview} 个知识点待复习`, '复习提醒', {
          duration: 5000
        })
      }, 2000)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard-view {
  width: 100%;
  padding: 2rem 0;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 3rem;
}

.dashboard-header h2 {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #2c3e50;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-text {
  font-size: 1.3rem;
  color: #6c757d;
  font-weight: 400;
}

/* stats-grid 样式在全局 layout.css 中定义 */

/* 统计卡片样式已移至BaseCard组件 */

.stat-icon {
  font-size: 3rem;
  line-height: 1;
}

.stat-content h3 {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.stat-content p {
  color: #6c757d;
  font-size: 1rem;
  font-weight: 500;
}

/* 快速操作卡片样式已移至BaseCard组件 */

.quick-actions h3 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #2c3e50;
  text-align: center;
  font-weight: 600;
}

/* action-grid 样式在全局 layout.css 中定义 */

/* 按钮样式已移至BaseButton组件 */

/* 响应式设计已移至全局 layout.css 文件中统一管理 */

/* 保留仅针对此页面的响应式样式 */
@media (max-width: 1024px) {
  .dashboard-header h2 {
    font-size: 2.2rem;
  }
  
  .stat-content h3 {
    font-size: 2.2rem;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    margin-bottom: 2rem;
  }
  
  .dashboard-header h2 {
    font-size: 2rem;
  }
  
  .welcome-text {
    font-size: 1rem;
  }
  
  .stat-icon {
    font-size: 2.5rem;
  }
  
  .stat-content h3 {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .dashboard-header h2 {
    font-size: 1.8rem;
  }
  
  .welcome-text {
    font-size: 0.9rem;
  }
  
  .stat-icon {
    font-size: 2rem;
  }
  
  .stat-content h3 {
    font-size: 1.8rem;
  }
  
  .stat-content p {
    font-size: 1rem;
  }
  
  .quick-actions h3 {
    font-size: 1.3rem;
  }
}
</style> 