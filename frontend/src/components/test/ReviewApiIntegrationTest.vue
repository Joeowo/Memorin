<template>
  <div class="api-integration-test">
    <div class="container">
      <h2>🔧 复习API集成测试</h2>
      <p class="subtitle">测试完整的复习流程：创建→开始→答题→完成</p>
      
      <div class="test-section">
        <h3>完整流程测试</h3>
        <BaseButton 
          @click="runFullTest" 
          :loading="testing" 
          variant="primary" 
          size="large"
        >
          {{ testing ? '测试进行中...' : '开始完整流程测试' }}
        </BaseButton>
        
        <div v-if="currentStep" class="current-step">
          <div class="step-indicator">
            <span class="step-icon">⚡</span>
            <span class="step-text">{{ currentStep }}</span>
          </div>
        </div>
      </div>

      <div v-if="testResults.length > 0" class="results-section">
        <h3>测试结果</h3>
        <div class="results-list">
          <div 
            v-for="(result, index) in testResults" 
            :key="index"
            class="result-item"
            :class="{ 'success': result.success, 'error': !result.success }"
          >
            <div class="result-header">
              <span class="result-icon">{{ result.success ? '✅' : '❌' }}</span>
              <span class="result-title">{{ result.step }}</span>
              <span class="result-time">{{ result.duration }}ms</span>
            </div>
            <div v-if="result.data" class="result-data">
              <details>
                <summary>查看数据</summary>
                <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
              </details>
            </div>
            <div v-if="result.error" class="result-error">
              {{ result.error }}
            </div>
          </div>
        </div>
      </div>

      <div class="comprehensive-log">
        <h3>详细日志</h3>
        <div class="log-container">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-level" :class="log.level">{{ log.level.toUpperCase() }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ReviewService } from '@/services/reviewService'
import type { BackendSession } from '@/services/reviewService'
import BaseButton from '@/components/common/BaseButton.vue'

interface TestResult {
  step: string
  success: boolean
  duration: number
  data?: unknown
  error?: string
}

interface LogEntry {
  time: string
  level: 'info' | 'success' | 'error' | 'warn'
  message: string
}

const testing = ref(false)
const currentStep = ref('')
const testResults = ref<TestResult[]>([])
const logs = ref<LogEntry[]>([])

function addLog(message: string, level: 'info' | 'success' | 'error' | 'warn' = 'info') {
  const now = new Date().toLocaleTimeString()
  logs.value.unshift({ time: now, level, message })
  console.log(`[${now}] [${level.toUpperCase()}] ${message}`)
}

async function runFullTest() {
  testing.value = true
  currentStep.value = '开始测试...'
  testResults.value = []
  logs.value = []
  
  addLog('开始复习API完整流程测试', 'info')
  
  try {
    // 第1步：创建会话
    const createResult = await testStep('创建复习会话', async () => {
      return await ReviewService.createSession({
        reviewMode: 'daily',
        knowledgeBaseId: 'TEST_BASE_001',
        targetQuestionCount: 3,
        onlyDueQuestions: false,
        autoStart: false
      })
    })
    
    if (!createResult.success) {
      addLog('创建会话失败，终止测试', 'error')
      return
    }
    
    const session = createResult.data as BackendSession
    addLog(`会话创建成功: ${session.id}`, 'success')
    
    // 第2步：开始会话
    const startResult = await testStep('开始复习会话', async () => {
      return await ReviewService.startSession(session.id)
    })
    
    if (!startResult.success) {
      addLog('开始会话失败，终止测试', 'error')
      return
    }
    
    const activeSession = startResult.data as BackendSession
    addLog(`会话已开始，状态: ${activeSession.status}`, 'success')
    
    // 第3步：模拟答题
    const submitResult = await testStep('提交答案', async () => {
      return await ReviewService.submitAnswer({
        sessionId: session.id,
        knowledgePointId: 'KP_TEXT_20250819_002', // 使用真实的知识点ID
        questionIndex: 0,
        userAnswer: 'test answer',
        isCorrect: true,
        qualityRating: 4,
        timeSpentSeconds: 30,
        mistakeReason: '',
        studyNotes: '',
        perceivedDifficulty: 3,
        isSkipped: false,
        submissionType: 'manual',
        questionType: 'text'
      })
    })
    
    if (submitResult.success) {
      addLog('答案提交成功，SM-2算法已处理', 'success')
    } else {
      addLog('答案提交失败', 'error')
    }
    
    // 第4步：获取错题统计
    await testStep('获取错题统计', async () => {
      return await ReviewService.getMistakeStatistics()
    })
    
    addLog('完整流程测试完成！', 'success')
    
  } catch (error) {
    addLog(`测试过程中出现异常: ${error}`, 'error')
  } finally {
    testing.value = false
    currentStep.value = ''
  }
}

async function testStep(stepName: string, testFunction: () => Promise<unknown>): Promise<TestResult> {
  currentStep.value = stepName
  addLog(`开始执行: ${stepName}`, 'info')
  
  const startTime = Date.now()
  
  try {
    const result = await testFunction()
    const duration = Date.now() - startTime
    
    const testResult: TestResult = {
      step: stepName,
      success: true,
      duration,
      data: result
    }
    
    testResults.value.push(testResult)
    addLog(`✅ ${stepName} - 成功 (${duration}ms)`, 'success')
    
    return testResult
    
  } catch (error) {
    const duration = Date.now() - startTime
    const errorMessage = error instanceof Error ? error.message : String(error)
    
    const testResult: TestResult = {
      step: stepName,
      success: false,
      duration,
      error: errorMessage
    }
    
    testResults.value.push(testResult)
    addLog(`❌ ${stepName} - 失败: ${errorMessage} (${duration}ms)`, 'error')
    
    return testResult
  }
}
</script>

<style scoped>
.api-integration-test {
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
  margin-bottom: 0.5rem;
  text-align: center;
}

.subtitle {
  text-align: center;
  color: #6c757d;
  margin-bottom: 2rem;
}

.test-section {
  background: white;
  padding: 2rem;
  margin-bottom: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.test-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
}

.current-step {
  margin-top: 2rem;
  padding: 1rem;
  background: #e3f2fd;
  border-radius: 8px;
  border-left: 4px solid #2196f3;
}

.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.step-icon {
  font-size: 1.2rem;
}

.step-text {
  font-weight: 600;
  color: #1565c0;
}

.results-section {
  background: white;
  padding: 2rem;
  margin-bottom: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.results-section h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
}

.results-list {
  display: grid;
  gap: 1rem;
}

.result-item {
  padding: 1rem;
  border-radius: 8px;
  border-left: 4px solid #e9ecef;
}

.result-item.success {
  background: #f8fff9;
  border-left-color: #28a745;
}

.result-item.error {
  background: #fff5f5;
  border-left-color: #dc3545;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.result-icon {
  font-size: 1.2rem;
}

.result-title {
  flex: 1;
  font-weight: 600;
  color: #2c3e50;
}

.result-time {
  color: #6c757d;
  font-size: 0.9rem;
}

.result-data {
  margin-top: 0.5rem;
}

.result-data details {
  cursor: pointer;
}

.result-data summary {
  color: #007bff;
  font-size: 0.9rem;
  padding: 0.5rem 0;
}

.result-data pre {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  font-size: 0.8rem;
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
}

.result-error {
  color: #dc3545;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.comprehensive-log {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comprehensive-log h3 {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
}

.log-container {
  max-height: 500px;
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
  align-items: center;
}

.log-time {
  color: #95a5a6;
  min-width: 80px;
}

.log-level {
  min-width: 60px;
  font-weight: bold;
  font-size: 0.8rem;
}

.log-level.info {
  color: #3498db;
}

.log-level.success {
  color: #2ecc71;
}

.log-level.error {
  color: #e74c3c;
}

.log-level.warn {
  color: #f39c12;
}

.log-message {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-section,
  .results-section,
  .comprehensive-log {
    padding: 1rem;
  }
  
  .result-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .log-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
  
  .log-time,
  .log-level {
    min-width: auto;
    font-size: 0.8rem;
  }
}
</style> 