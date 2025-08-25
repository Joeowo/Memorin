<template>
  <div class="memorin-demo">
    <h1>🚀 Memorin API SDK 使用演示</h1>
    
    <!-- 服务状态检查 -->
    <div class="status-section">
      <h2>📡 服务状态</h2>
      <button @click="checkHealth" :disabled="loading">
        {{ loading ? '检查中...' : '检查API健康状态' }}
      </button>
      <span v-if="healthStatus !== null" :class="healthStatus ? 'status-ok' : 'status-error'">
        {{ healthStatus ? '✅ 服务正常' : '❌ 服务异常' }}
      </span>
    </div>

    <!-- 快速创建分类 -->
    <div class="create-section">
      <h2>📋 创建分类（智能默认值）</h2>
      <div class="form-group">
        <input v-model="categoryName" placeholder="输入分类名称（如：前端开发）" />
        <button @click="createCategory" :disabled="!categoryName || loading">
          创建分类
        </button>
      </div>
      <div v-if="createdCategory" class="result">
        ✅ 创建成功: {{ createdCategory.name }} 
        <span class="category-preview">{{ createdCategory.icon }} {{ createdCategory.color }}</span>
      </div>
    </div>

    <!-- 一键创建文本题 -->
    <div class="create-section">
      <h2>📝 一键创建填空题</h2>
      <div class="form-group">
        <input v-model="fillAnswer" placeholder="答案（如：Vue3）" />
        <input v-model="fillAlternatives" placeholder="同义答案，用逗号分隔（如：vue3,vuejs）" />
        <button @click="createFillQuestion" :disabled="!fillAnswer || loading">
          创建填空题
        </button>
      </div>
      <div v-if="createdFillQuestion" class="result">
        ✅ 填空题创建成功: {{ createdFillQuestion.answer }}
      </div>
    </div>

    <!-- 创建完整的问答题 -->
    <div class="create-section">
      <h2>📖 创建完整问答题（自动处理依赖）</h2>
      <div class="form-group">
        <input v-model="essayCategory" placeholder="分类名称（如：编程语言）" />
        <input v-model="essayQuestion" placeholder="问题（如：什么是JavaScript？）" />
        <textarea v-model="essayAnswer" placeholder="答案..."></textarea>
        <button @click="createEssayQuestion" :disabled="!essayCategory || !essayQuestion || !essayAnswer || loading">
          创建问答题
        </button>
      </div>
      <div v-if="createdEssayQuestion" class="result">
        ✅ 问答题创建成功: {{ essayQuestion }}
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-section">
      <h2>📊 数据统计</h2>
      <button @click="loadStats" :disabled="loading">刷新统计</button>
      <div v-if="stats" class="stats-grid">
        <div class="stat-card">
          <h3>📋 分类总数</h3>
          <div class="stat-number">{{ stats.categories }}</div>
        </div>
        <div class="stat-card">
          <h3>📚 知识点总数</h3>
          <div class="stat-number">{{ stats.knowledge }}</div>
        </div>
        <div class="stat-card">
          <h3>📝 文本题总数</h3>
          <div class="stat-number">{{ stats.textQuestions }}</div>
        </div>
      </div>
    </div>

    <!-- 错误信息显示 -->
    <div v-if="error" class="error-message">
      ❌ {{ error }}
      <button @click="clearError">清除</button>
    </div>

    <!-- 操作日志 -->
    <div class="log-section">
      <h2>📋 操作日志</h2>
      <div class="log-container">
        <div v-for="(log, index) in logs" :key="index" :class="['log-item', log.type]">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { memorinAPI, handleAPIError, type CategoryData, type TextQuestionData } from './memorin-sdk-demo'

// 响应式数据
const loading = ref(false)
const healthStatus = ref<boolean | null>(null)
const error = ref('')

// 分类创建
const categoryName = ref('')
const createdCategory = ref<CategoryData | null>(null)

// 填空题创建
const fillAnswer = ref('')
const fillAlternatives = ref('')
const createdFillQuestion = ref<TextQuestionData | null>(null)

// 问答题创建
const essayCategory = ref('')
const essayQuestion = ref('')
const essayAnswer = ref('')
const createdEssayQuestion = ref<TextQuestionData | null>(null)

// 统计数据
const stats = ref<{
  categories: number
  knowledge: number
  textQuestions: number
} | null>(null)

// 操作日志
const logs = ref<Array<{
  time: string
  message: string
  type: 'info' | 'success' | 'error'
}>>([])

// 工具函数
function addLog(message: string, type: 'info' | 'success' | 'error' = 'info') {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message,
    type
  })
  // 限制日志数量
  if (logs.value.length > 20) {
    logs.value = logs.value.slice(0, 20)
  }
}

function clearError() {
  error.value = ''
}

async function withLoading<T>(fn: () => Promise<T>): Promise<T | null> {
  loading.value = true
  error.value = ''
  try {
    const result = await fn()
    return result
  } catch (err) {
    const errorMsg = handleAPIError(err)
    error.value = errorMsg
    addLog(`操作失败: ${errorMsg}`, 'error')
    return null
  } finally {
    loading.value = false
  }
}

// API操作方法
async function checkHealth() {
  await withLoading(async () => {
    const isHealthy = await memorinAPI.healthCheck()
    healthStatus.value = isHealthy
    addLog(`健康检查完成: ${isHealthy ? '服务正常' : '服务异常'}`, isHealthy ? 'success' : 'error')
  })
}

async function createCategory() {
  const result = await withLoading(async () => {
    // 🎯 使用SDK创建分类 - 自动生成icon和color
    const category = await memorinAPI.categories.create({
      name: categoryName.value,
      description: `${categoryName.value}相关知识点`
    })
    
    createdCategory.value = category
    categoryName.value = '' // 清空输入
    addLog(`分类创建成功: ${category.name} ${category.icon}`, 'success')
    
    return category
  })
  
  if (result) {
    // 自动刷新统计
    await loadStats()
  }
}

async function createFillQuestion() {
  const result = await withLoading(async () => {
    // 🎯 解析同义答案
    const alternatives = fillAlternatives.value
      ? fillAlternatives.value.split(',').map(s => s.trim()).filter(s => s)
      : []
    
    // 🎯 一行代码创建填空题
    const question = await memorinAPI.textQuestions.createFillQuestion(
      fillAnswer.value,
      alternatives
    )
    
    createdFillQuestion.value = question
    fillAnswer.value = ''
    fillAlternatives.value = ''
    addLog(`填空题创建成功: 答案="${question.answer}"`, 'success')
    
    return question
  })
  
  if (result) {
    await loadStats()
  }
}

async function createEssayQuestion() {
  const result = await withLoading(async () => {
    // 🎯 自动处理所有依赖的问答题创建
    const question = await memorinAPI.textQuestions.createEssayQuestion(
      essayCategory.value,   // 分类名称
      essayQuestion.value,   // 问题
      essayAnswer.value      // 答案
    )
    
    createdEssayQuestion.value = question
    essayCategory.value = ''
    essayQuestion.value = ''
    essayAnswer.value = ''
    addLog(`问答题创建成功: ${essayQuestion.value}`, 'success')
    
    return question
  })
  
  if (result) {
    await loadStats()
  }
}

async function loadStats() {
  await withLoading(async () => {
    // 🎯 并行获取统计数据
    const [categories, knowledge, textQuestions] = await Promise.all([
      memorinAPI.categories.getAll(),
      memorinAPI.knowledge.getAll(),
      memorinAPI.textQuestions.getAll()
    ])
    
    stats.value = {
      categories: categories.length,
      knowledge: knowledge.length,
      textQuestions: textQuestions.length
    }
    
    addLog('统计数据已更新', 'success')
  })
}

// 组件挂载时初始化
onMounted(async () => {
  addLog('组件已加载，开始初始化...', 'info')
  
  // 检查服务健康状态
  await checkHealth()
  
  if (healthStatus.value) {
    // 初始化演示数据
    await withLoading(async () => {
      await memorinAPI.initDemoData()
      addLog('演示数据初始化完成', 'success')
    })
    
    // 加载统计数据
    await loadStats()
  }
})
</script>

<style scoped>
.memorin-demo {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
}

h1 {
  text-align: center;
  color: #667eea;
  margin-bottom: 2rem;
}

h2 {
  color: #4a5568;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 0.5rem;
  margin: 2rem 0 1rem 0;
}

.status-section, .create-section {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  border-left: 4px solid #667eea;
}

.form-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.form-group input, .form-group textarea {
  flex: 1;
  min-width: 200px;
  padding: 0.75rem;
  border: 1px solid #cbd5e0;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.form-group button {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}

.form-group button:hover:not(:disabled) {
  background: #5a67d8;
}

.form-group button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.result {
  background: #f0fff4;
  border: 1px solid #9ae6b4;
  color: #22543d;
  padding: 1rem;
  border-radius: 4px;
  margin-top: 1rem;
}

.category-preview {
  font-weight: bold;
  margin-left: 0.5rem;
}

.status-ok {
  color: #22c55e;
  font-weight: bold;
  margin-left: 1rem;
}

.status-error {
  color: #ef4444;
  font-weight: bold;
  margin-left: 1rem;
}

.stats-section {
  background: #f8fafc;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 14px;
  color: #6b7280;
  border: none;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
}

.error-message {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 1rem;
  border-radius: 4px;
  margin: 1rem 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-message button {
  background: #dc2626;
  color: white;
  border: none;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.log-section {
  margin-top: 2rem;
}

.log-container {
  background: #1a202c;
  border-radius: 8px;
  padding: 1rem;
  max-height: 300px;
  overflow-y: auto;
  font-family: 'Consolas', monospace;
  font-size: 13px;
}

.log-item {
  display: flex;
  margin-bottom: 0.5rem;
  padding: 0.25rem 0;
}

.log-time {
  color: #718096;
  margin-right: 1rem;
  min-width: 80px;
}

.log-message {
  flex: 1;
}

.log-item.info .log-message {
  color: #e2e8f0;
}

.log-item.success .log-message {
  color: #68d391;
}

.log-item.error .log-message {
  color: #fc8181;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-group {
    flex-direction: column;
  }
  
  .form-group input, .form-group textarea, .form-group button {
    min-width: auto;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style> 