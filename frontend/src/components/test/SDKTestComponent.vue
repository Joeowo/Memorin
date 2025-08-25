<template>
  <div class="sdk-test-container">
    <div class="test-header">
      <h1>🧪 Memorin SDK 功能测试</h1>
      <div class="status-indicator" :class="{ 'online': healthStatus, 'offline': healthStatus === false }">
        <span v-if="healthStatus === null">⏳ 检查中...</span>
        <span v-else-if="healthStatus">✅ 服务在线</span>
        <span v-else>❌ 服务离线</span>
      </div>
    </div>

    <!-- 基础功能测试 -->
    <div class="test-section">
      <h2>🔍 基础功能测试</h2>
      <div class="test-actions">
        <button @click="testHealthCheck" :disabled="loading" class="test-btn">
          健康检查
        </button>
        <button @click="testInitDemo" :disabled="loading" class="test-btn">
          初始化演示数据
        </button>
        <button @click="loadAllStats" :disabled="loading" class="test-btn">
          加载统计数据
        </button>
      </div>
    </div>

    <!-- 分类管理测试 -->
    <div class="test-section">
      <h2>📋 分类管理测试</h2>
      <div class="form-group">
        <input 
          v-model="newCategoryName" 
          placeholder="输入分类名称（如：Vue开发）"
          class="test-input"
        />
        <button @click="testCreateCategory" :disabled="!newCategoryName || loading" class="test-btn">
          创建分类
        </button>
      </div>
      
      <div v-if="testResults.lastCategory" class="test-result success">
        ✅ 最新分类: {{ testResults.lastCategory.name }} 
        <span class="category-preview">{{ testResults.lastCategory.icon }} {{ testResults.lastCategory.color }}</span>
      </div>
    </div>

    <!-- 文本题测试 -->
    <div class="test-section">
      <h2>📝 文本题管理测试</h2>
      <div class="form-group">
        <input 
          v-model="fillAnswer" 
          placeholder="填空题答案（如：Vue3）"
          class="test-input"
        />
        <input 
          v-model="fillAlternatives" 
          placeholder="同义答案，逗号分隔（如：vue3,vuejs）"
          class="test-input"
        />
        <button @click="testCreateFillQuestion" :disabled="!fillAnswer || loading" class="test-btn">
          创建填空题
        </button>
      </div>

      <div v-if="testResults.lastFillQuestion" class="test-result success">
        ✅ 最新填空题: 答案为 "{{ testResults.lastFillQuestion.answer }}"
      </div>
    </div>

    <!-- 完整问答题测试 -->
    <div class="test-section">
      <h2>📖 完整问答题测试</h2>
      <div class="form-group">
        <input 
          v-model="essayCategory" 
          placeholder="分类名称（如：前端开发）"
          class="test-input"
        />
        <input 
          v-model="essayQuestion" 
          placeholder="问题（如：什么是Vue组件？）"
          class="test-input"
        />
        <textarea 
          v-model="essayAnswer" 
          placeholder="答案..."
          class="test-textarea"
        ></textarea>
        <button @click="testCreateEssayQuestion" :disabled="!essayCategory || !essayQuestion || !essayAnswer || loading" class="test-btn">
          创建问答题（自动处理依赖）
        </button>
      </div>

      <div v-if="testResults.lastEssayQuestion" class="test-result success">
        ✅ 最新问答题创建成功
      </div>
    </div>

    <!-- 统计数据显示 -->
    <div class="test-section" v-if="stats">
      <h2>📊 当前数据统计</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <h3>📋 分类数量</h3>
          <div class="stat-number">{{ stats.categories }}</div>
        </div>
        <div class="stat-card">
          <h3>📚 知识点数量</h3>
          <div class="stat-number">{{ stats.knowledge }}</div>
        </div>
        <div class="stat-card">
          <h3>📝 文本题数量</h3>
          <div class="stat-number">{{ stats.textQuestions }}</div>
        </div>
      </div>
    </div>

    <!-- 错误信息显示 -->
    <div v-if="error" class="test-section error-section">
      <h2>❌ 错误信息</h2>
      <div class="error-message">
        {{ error }}
        <button @click="clearError" class="clear-btn">清除</button>
      </div>
    </div>

    <!-- 操作日志 -->
    <div class="test-section">
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
import { MemorinaAPI, handleAPIError, type CategoryData, type TextQuestionData } from '@/utils/memorin-sdk'

// 响应式数据
const loading = ref(false)
const healthStatus = ref<boolean | null>(null)
const error = ref('')

// 表单数据
const newCategoryName = ref('')
const fillAnswer = ref('')
const fillAlternatives = ref('')
const essayCategory = ref('')
const essayQuestion = ref('')
const essayAnswer = ref('')

// 测试结果
const testResults = ref<{
  lastCategory: CategoryData | null
  lastFillQuestion: TextQuestionData | null
  lastEssayQuestion: TextQuestionData | null
}>({
  lastCategory: null,
  lastFillQuestion: null,
  lastEssayQuestion: null
})

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

// SDK实例
const api = new MemorinaAPI()

// 工具函数
function addLog(message: string, type: 'info' | 'success' | 'error' = 'info') {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message,
    type
  })
  if (logs.value.length > 15) {
    logs.value = logs.value.slice(0, 15)
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

// 测试方法
async function testHealthCheck() {
  await withLoading(async () => {
    const isHealthy = await api.healthCheck()
    healthStatus.value = isHealthy
    addLog(`健康检查: ${isHealthy ? '服务正常' : '服务异常'}`, isHealthy ? 'success' : 'error')
    return isHealthy
  })
}

async function testInitDemo() {
  await withLoading(async () => {
    await api.initDemoData()
    addLog('演示数据初始化完成', 'success')
    await loadAllStats()
  })
}

async function testCreateCategory() {
  const result = await withLoading(async () => {
    const category = await api.categories.create({
      name: newCategoryName.value,
      description: `${newCategoryName.value}相关知识点`
    })
    
    testResults.value.lastCategory = category
    newCategoryName.value = ''
    addLog(`分类创建成功: ${category.name} ${category.icon} ${category.color}`, 'success')
    
    return category
  })
  
  if (result) {
    await loadAllStats()
  }
}

async function testCreateFillQuestion() {
  const result = await withLoading(async () => {
    const alternatives = fillAlternatives.value
      ? fillAlternatives.value.split(',').map(s => s.trim()).filter(s => s)
      : []
    
    const question = await api.textQuestions.createFillQuestion(
      fillAnswer.value,
      alternatives
    )
    
    testResults.value.lastFillQuestion = question
    fillAnswer.value = ''
    fillAlternatives.value = ''
    addLog(`填空题创建成功: 答案="${question.answer}"`, 'success')
    
    return question
  })
  
  if (result) {
    await loadAllStats()
  }
}

async function testCreateEssayQuestion() {
  const result = await withLoading(async () => {
    const question = await api.textQuestions.createEssayQuestion(
      essayCategory.value,
      essayQuestion.value,
      essayAnswer.value
    )
    
    testResults.value.lastEssayQuestion = question
    const questionText = essayQuestion.value
    essayCategory.value = ''
    essayQuestion.value = ''
    essayAnswer.value = ''
    addLog(`问答题创建成功: "${questionText}"`, 'success')
    
    return question
  })
  
  if (result) {
    await loadAllStats()
  }
}

async function loadAllStats() {
  await withLoading(async () => {
    const [categories, knowledge, textQuestions] = await Promise.all([
      api.categories.getAll(),
      api.knowledge.getAll(),
      api.textQuestions.getAll()
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
  addLog('SDK测试组件已加载', 'info')
  await testHealthCheck()
  
  if (healthStatus.value) {
    await loadAllStats()
  }
})
</script>

<style scoped>
.sdk-test-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #f8f9fa;
  min-height: 100vh;
}

.test-header {
  text-align: center;
  margin-bottom: 3rem;
}

.test-header h1 {
  color: #333;
  margin-bottom: 1rem;
}

.status-indicator {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 500;
  display: inline-block;
}

.status-indicator.online {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-indicator.offline {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.test-section {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #667eea;
  margin-bottom: 1.5rem;
  font-size: 1.4rem;
}

.test-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.form-group {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.test-input, .test-textarea {
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.test-input {
  flex: 1;
  min-width: 200px;
}

.test-textarea {
  width: 100%;
  min-height: 80px;
  resize: vertical;
}

.test-input:focus, .test-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.test-btn {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.test-btn:hover:not(:disabled) {
  background: #5a6fd8;
}

.test-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.test-result {
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.test-result.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.category-preview {
  font-weight: bold;
  margin-left: 0.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #e9ecef;
}

.stat-card h3 {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
}

.error-section {
  border-left: 4px solid #dc3545;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #f5c6cb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.clear-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.log-container {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: #f8f9fa;
}

.log-item {
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
}

.log-item:last-child {
  border-bottom: none;
}

.log-item.success {
  background: rgba(40, 167, 69, 0.1);
  color: #155724;
}

.log-item.error {
  background: rgba(220, 53, 69, 0.1);
  color: #721c24;
}

.log-item.info {
  color: #6c757d;
}

.log-time {
  font-family: monospace;
  color: #6c757d;
  margin-right: 1rem;
  min-width: 80px;
}

.log-message {
  flex: 1;
}

@media (max-width: 768px) {
  .test-actions, .form-group {
    flex-direction: column;
  }
  
  .test-input {
    min-width: unset;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style> 