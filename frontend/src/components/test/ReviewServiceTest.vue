<template>
  <div class="review-service-test">
    <div class="container">
      <h2>🧪 复习服务API测试</h2>
      
      <div class="test-section">
        <h3>1. 连接测试</h3>
        <BaseButton @click="testConnection" :loading="testing.connection" variant="primary">
          测试连接
        </BaseButton>
        <div v-if="results.connection !== null" class="test-result">
          {{ results.connection ? '✅ 连接成功' : '❌ 连接失败' }}
        </div>
      </div>

      <div class="test-section">
        <h3>2. 获取会话列表</h3>
        <BaseButton @click="testGetSessions" :loading="testing.sessions" variant="primary">
          获取会话
        </BaseButton>
        <div v-if="results.sessions" class="test-result">
          <pre>{{ JSON.stringify(results.sessions, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h3>3. 创建会话</h3>
        <BaseButton @click="testCreateSession" :loading="testing.createSession" variant="primary">
          创建会话
        </BaseButton>
        <div v-if="results.createSession" class="test-result">
          <pre>{{ JSON.stringify(results.createSession, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h3>4. 错题统计</h3>
        <BaseButton @click="testMistakeStats" :loading="testing.mistakes" variant="primary">
          获取错题统计
        </BaseButton>
        <div v-if="results.mistakes !== null" class="test-result">
          <pre>{{ JSON.stringify(results.mistakes, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h3>5. 综合测试日志</h3>
        <div class="log-container">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ReviewService } from '@/services/reviewService'
import BaseButton from '@/components/common/BaseButton.vue'

// 测试状态
const testing = reactive({
  connection: false,
  sessions: false,
  createSession: false,
  mistakes: false
})

// 测试结果
const results = reactive({
  connection: null as boolean | null,
  sessions: null as unknown,
  createSession: null as unknown,
  mistakes: null as unknown
})

// 日志
const logs = ref<Array<{ time: string, message: string }>>([])

function addLog(message: string) {
  const now = new Date().toLocaleTimeString()
  logs.value.unshift({ time: now, message })
  console.log(`[${now}] ${message}`)
}

// 测试连接
async function testConnection() {
  testing.connection = true
  addLog('开始测试复习服务连接...')
  
  try {
    const isConnected = await ReviewService.testConnection()
    results.connection = isConnected
    addLog(isConnected ? '✅ 复习服务连接成功' : '❌ 复习服务连接失败')
  } catch (error) {
    results.connection = false
    addLog(`❌ 连接测试异常: ${error}`)
  } finally {
    testing.connection = false
  }
}

// 测试获取会话
async function testGetSessions() {
  testing.sessions = true
  addLog('开始获取会话列表...')
  
  try {
    const sessions = await ReviewService.getUserSessions()
    results.sessions = sessions
    addLog(`✅ 获取到 ${sessions.length} 个会话`)
  } catch (error) {
    results.sessions = null
    addLog(`❌ 获取会话失败: ${error}`)
  } finally {
    testing.sessions = false
  }
}

// 测试创建会话
async function testCreateSession() {
  testing.createSession = true
  addLog('开始创建测试会话...')
  
  try {
    const session = await ReviewService.createSession({
      reviewMode: 'daily',
      knowledgeBaseId: 'TEST_BASE_001',
      targetQuestionCount: 5,
      onlyDueQuestions: true,
      autoStart: false
    })
    results.createSession = session
    addLog(`✅ 创建会话成功: ${session.id}`)
  } catch (error) {
    results.createSession = null
    addLog(`❌ 创建会话失败: ${error}`)
  } finally {
    testing.createSession = false
  }
}

// 测试错题统计
async function testMistakeStats() {
  testing.mistakes = true
  addLog('开始获取错题统计...')
  
  try {
    const stats = await ReviewService.getMistakeStatistics()
    results.mistakes = stats
    addLog('✅ 获取错题统计成功')
  } catch (error) {
    results.mistakes = null
    addLog(`❌ 获取错题统计失败: ${error}`)
  } finally {
    testing.mistakes = false
  }
}
</script>

<style scoped>
.review-service-test {
  width: 100%;
  min-height: 100vh;
  background: #f8f9fa;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

h2 {
  color: #2c3e50;
  margin-bottom: 2rem;
  text-align: center;
}

.test-section {
  background: white;
  padding: 2rem;
  margin-bottom: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h3 {
  color: #2c3e50;
  margin: 0 0 1rem 0;
  font-size: 1.2rem;
}

.test-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.test-result pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 0.9rem;
  line-height: 1.4;
}

.log-container {
  max-height: 400px;
  overflow-y: auto;
  background: #2c3e50;
  color: #ecf0f1;
  padding: 1rem;
  border-radius: 8px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.log-item {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.log-time {
  color: #95a5a6;
  min-width: 80px;
}

.log-message {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-section {
    padding: 1rem;
  }
  
  .log-item {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .log-time {
    min-width: auto;
    font-size: 0.8rem;
  }
}
</style> 