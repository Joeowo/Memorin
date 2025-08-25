<template>
  <div class="review-view">
    <div class="container">
      <!-- 页面头部 -->
      <div class="review-header">
        <h2>🎯 智能复习</h2>
        <p class="header-subtitle">基于SM-2算法的间隔重复学习系统</p>
      </div>
      
      <!-- 错误提示 -->
      <BaseNotification
        v-if="store.error"
        type="error"
        :message="store.error"
        @close="store.clearError"
        class="error-notification"
      />
      
      <!-- 加载指示器 -->
      <LoadingSpinner v-if="store.loading && !store.currentSession" />
      
      <!-- 主要内容区域 -->
      <div class="review-content" v-else>
        <!-- 复习会话进行中 -->
        <ReviewSession 
          v-if="store.sessionInProgress" 
          @session-complete="handleSessionComplete"
        />
        
        <!-- 复习主界面 -->
        <div v-else class="review-dashboard">
          <!-- 今日复习统计 -->
          <div class="stats-section">
            <h3>📊 今日学习</h3>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon">📝</div>
                <div class="stat-content">
                  <div class="stat-value">{{ store.todayStats.questions }}</div>
                  <div class="stat-label">已复习题目</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">✅</div>
                <div class="stat-content">
                  <div class="stat-value">{{ store.todayStats.accuracy }}%</div>
                  <div class="stat-label">正确率</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">⏱️</div>
                <div class="stat-content">
                  <div class="stat-value">{{ store.todayStats.studyTime }}</div>
                  <div class="stat-label">学习时间(分钟)</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">🔥</div>
                <div class="stat-content">
                  <div class="stat-value">{{ store.todayStats.sessions }}</div>
                  <div class="stat-label">复习会话</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 复习队列状态 -->
          <div class="queue-section" v-if="store.queueSummary">
            <h3>📋 复习队列</h3>
            <div class="queue-overview">
              <div class="queue-card">
                <div class="queue-header">
                  <span class="queue-title">待复习</span>
                  <span class="queue-count">{{ store.queueSummary.totalDue }}</span>
                </div>
                <div class="queue-details">
                  <div class="queue-item">
                    <span>已过期:</span>
                    <span class="overdue">{{ store.queueSummary.overdue }}</span>
                  </div>
                  <div class="queue-item">
                    <span>新内容:</span>
                    <span>{{ store.queueSummary.newItems }}</span>
                  </div>
                  <div class="queue-item">
                    <span>预计时间:</span>
                    <span>{{ store.queueSummary.estimatedTime }} 分钟</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 快速开始区域 -->
          <div class="quick-start-section">
            <h3>🚀 开始复习</h3>
            <div class="start-options">
              <BaseCard class="start-card" @click="handleQuickStart('daily')">
                <div class="start-icon">📅</div>
                <h4>今日复习</h4>
                <p>复习今天到期的知识点</p>
                <div class="start-stats">
                  <span>{{ store.queueSummary?.totalDue || 0 }} 个待复习</span>
                </div>
              </BaseCard>
              
              <BaseCard class="start-card" @click="handleQuickStart('quick')">
                <div class="start-icon">⚡</div>
                <h4>快速复习</h4>
                <p>10分钟快速复习模式</p>
                <div class="start-stats">
                  <span>约 10-15 个题目</span>
                </div>
              </BaseCard>
              
              <BaseCard class="start-card" @click="handleQuickStart('weakness')">
                <div class="start-icon">💪</div>
                <h4>弱项强化</h4>
                <p>针对薄弱知识点的复习</p>
                <div class="start-stats">
                  <span>智能推荐</span>
                </div>
              </BaseCard>
              
              <BaseCard class="start-card" @click="handleQuickStart('mistake')">
                <div class="start-icon">🎯</div>
                <h4>错题回顾</h4>
                <p>复习历史错题</p>
                <div class="start-stats">
                  <span>{{ store.mistakes?.unresolvedMistakes || 0 }} 个错题</span>
                </div>
              </BaseCard>
            </div>
          </div>
          
          <!-- 最近会话 -->
          <div class="recent-sessions-section" v-if="store.recentSessions.length > 0">
            <h3>📚 最近会话</h3>
            <div class="sessions-list">
              <div 
                v-for="session in store.recentSessions.slice(0, 5)" 
                :key="session.id"
                class="session-item"
              >
                <div class="session-info">
                  <div class="session-title">
                    {{ getSessionModeLabel(session.mode) }}
                  </div>
                  <div class="session-meta">
                    <span>{{ formatDate(session.startTime) }}</span>
                    <span>•</span>
                    <span>{{ session.totalQuestions }} 题</span>
                    <span>•</span>
                    <span>{{ Math.round((session.correctAnswers / session.totalQuestions) * 100) }}% 正确率</span>
                  </div>
                </div>
                <div class="session-status">
                  <BaseBadge 
                    :variant="getStatusVariant(session.status)"
                    size="small"
                  >
                    {{ getStatusLabel(session.status) }}
                  </BaseBadge>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useReviewStore } from '@/stores/reviewStore'
import type { ReviewMode, SessionStatus } from '@/types/review'

// 组件导入
import BaseCard from '@/components/common/BaseCard.vue'
import BaseNotification from '@/components/common/BaseNotification.vue'
import BaseBadge from '@/components/common/BaseBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import ReviewSession from '@/components/review/ReviewSession.vue'

const store = useReviewStore()

// 生命周期钩子
onMounted(async () => {
  await store.loadAllData()
})

// 事件处理
async function handleQuickStart(mode: ReviewMode) {
  try {
    await store.quickStart(mode)
  } catch (error) {
    console.error('快速开始失败:', error)
  }
}

function handleSessionComplete() {
  // 复习会话完成后的处理
  console.log('复习会话已完成')
  
  // 清理会话状态，返回到复习主界面
  store.sessionInProgress = false
  store.currentSession = null
  store.currentQuestion = null
  store.currentAnswer = ''
  store.showAnswer = false
  
  // 重新加载数据以更新统计信息
  store.loadAllData()
}

// 工具函数
function getSessionModeLabel(mode: ReviewMode): string {
  const labels = {
    'daily': '今日复习',
    'quick': '快速复习',
    'weakness': '弱项强化',
    'mistake': '错题回顾'
  }
  return labels[mode] || mode
}

function getStatusLabel(status: SessionStatus): string {
  const labels = {
    'created': '已创建',
    'active': '进行中',
    'paused': '已暂停',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return labels[status] || status
}

function getStatusVariant(status: SessionStatus): 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'info' {
  const variants = {
    'created': 'secondary' as const,
    'active': 'primary' as const,
    'paused': 'warning' as const,
    'completed': 'success' as const,
    'cancelled': 'danger' as const
  }
  return variants[status] || 'secondary'
}

function formatDate(date: Date): string {
  return new Date(date).toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.review-view {
  width: 100%;
  min-height: 100vh;
  background: #f8f9fa;
  padding: 2rem 0;
}

.review-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
}

.review-header h2 {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  font-weight: 600;
}

.header-subtitle {
  color: #6c757d;
  margin: 0;
  font-size: 1rem;
}

.error-notification {
  margin-bottom: 1.5rem;
}

.review-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.review-dashboard {
  padding: 2rem;
}

/* 统计区域 */
.stats-section {
  margin-bottom: 3rem;
}

.stats-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px solid #e9ecef;
  transition: all 0.2s ease;
}

.stat-card:hover {
  border-color: #007bff;
  background: #f8f9ff;
}

.stat-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 0.25rem;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

/* 队列区域 */
.queue-section {
  margin-bottom: 3rem;
}

.queue-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.queue-overview {
  display: grid;
  gap: 1.5rem;
}

.queue-card {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 1.5rem;
}

.queue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.queue-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.queue-count {
  font-size: 1.5rem;
  font-weight: 700;
  color: #007bff;
}

.queue-details {
  display: grid;
  gap: 0.5rem;
}

.queue-item {
  display: flex;
  justify-content: space-between;
  color: #6c757d;
  font-size: 0.9rem;
}

.overdue {
  color: #dc3545;
  font-weight: 600;
}

/* 快速开始区域 */
.quick-start-section {
  margin-bottom: 3rem;
}

.quick-start-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.start-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.start-card {
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid #e9ecef;
}

.start-card:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
  transform: translateY(-2px);
}

.start-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.start-card h4 {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.start-card p {
  color: #6c757d;
  margin: 0 0 1rem 0;
  font-size: 0.9rem;
  line-height: 1.4;
}

.start-stats {
  color: #007bff;
  font-size: 0.8rem;
  font-weight: 600;
}

/* 最近会话区域 */
.recent-sessions-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.sessions-list {
  display: grid;
  gap: 1rem;
}

.session-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.session-info {
  flex: 1;
}

.session-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.25rem;
}

.session-meta {
  color: #6c757d;
  font-size: 0.8rem;
}

.session-meta span {
  margin: 0 0.5rem;
}

.session-meta span:first-child {
  margin-left: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .start-options {
    grid-template-columns: 1fr;
  }
  
  .session-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .review-dashboard {
    padding: 1rem;
  }
}
</style> 