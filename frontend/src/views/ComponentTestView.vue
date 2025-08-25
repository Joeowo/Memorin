<template>
  <div class="component-test-view">
    <div class="container">
      <header class="test-header">
        <h1>🧪 组件测试中心</h1>
        <p>测试和展示所有基础UI组件以及API SDK的功能</p>
    </header>

      <!-- Tab切换 -->
      <div class="tab-container">
        <div class="tab-nav">
          <button 
            :class="['tab-button', { active: activeTab === 'components' }]"
            @click="activeTab = 'components'"
          >
            🔧 基础组件
          </button>
          <button 
            :class="['tab-button', { active: activeTab === 'sdk' }]"
            @click="activeTab = 'sdk'"
          >
            🚀 SDK 测试
          </button>
        </div>
      </div>

      <!-- 基础组件测试内容 -->
      <div v-show="activeTab === 'components'" class="tab-content">
    <!-- BaseButton 测试 -->
    <section class="test-section">
      <h2>🔘 BaseButton 按钮组件</h2>
      
      <div class="test-group">
        <h3>按钮变体</h3>
        <div class="button-group">
          <BaseButton variant="primary">主要按钮</BaseButton>
          <BaseButton variant="secondary">次要按钮</BaseButton>
          <BaseButton variant="info">信息按钮</BaseButton>
        </div>
      </div>

      <div class="test-group">
        <h3>按钮状态</h3>
        <div class="button-group">
          <BaseButton :loading="buttonLoading" @click="toggleLoading">
            {{ buttonLoading ? '加载中...' : '点击加载' }}
          </BaseButton>
          <BaseButton disabled>禁用按钮</BaseButton>
          <BaseButton variant="primary" @click="showSuccess">
            触发成功通知
          </BaseButton>
        </div>
      </div>
    </section>

    <!-- BaseCard 测试 -->
    <section class="test-section">
      <h2>🗃️ BaseCard 卡片组件</h2>
      
              <div class="test-grid">
        <BaseCard variant="default">
          <h4>默认卡片</h4>
          <p>这是一个默认样式的卡片组件，展示基础的毛玻璃效果。</p>
        </BaseCard>

        <BaseCard variant="stat" clickable @click="handleCardClick">
          <h4>统计卡片</h4>
          <div class="stat-content">
            <div class="stat-number">128</div>
            <div class="stat-label">已学知识点</div>
          </div>
        </BaseCard>

        <BaseCard variant="action" clickable @click="handleCardClick">
          <h4>操作卡片</h4>
          <p>点击进行操作</p>
        </BaseCard>
      </div>
    </section>

    <!-- BaseInput 测试 -->
    <section class="test-section">
      <h2>📝 BaseInput 输入框组件</h2>
      
      <div class="input-grid">
        <BaseInput
          v-model="textInput"
          label="文本输入"
          placeholder="请输入文本"
          help-text="这是帮助文本"
        />

        <BaseInput
          v-model="emailInput"
          type="email"
          label="邮箱地址"
          placeholder="请输入邮箱"
          required
        />

        <BaseInput
          v-model="passwordInput"
          type="password"
          label="密码"
          placeholder="请输入密码"
        />

        <BaseInput
          v-model="numberInput"
          type="number"
          label="数字输入"
          placeholder="请输入数字"
          disabled
        />
      </div>

      <div class="input-preview">
        <h4>输入值预览：</h4>
        <div class="preview-content">
          <p><strong>文本：</strong>{{ textInput || '空' }}</p>
          <p><strong>邮箱：</strong>{{ emailInput || '空' }}</p>
          <p><strong>密码：</strong>{{ passwordInput || '空' }}</p>
          <p><strong>数字：</strong>{{ numberInput || '空' }}</p>
        </div>
      </div>
    </section>

    <!-- BaseBadge 测试 -->
    <section class="test-section">
      <h2>🏷️ BaseBadge 徽章组件</h2>
      
      <div class="test-group">
        <h3>基础变体</h3>
        <div class="badge-container">
          <BaseBadge variant="default" text="默认" />
          <BaseBadge variant="primary" text="主要" />
          <BaseBadge variant="secondary" text="次要" />
          <BaseBadge variant="success" text="成功" />
          <BaseBadge variant="warning" text="警告" />
          <BaseBadge variant="danger" text="危险" />
          <BaseBadge variant="info" text="信息" />
        </div>
      </div>

      <div class="test-group">
        <h3>难度等级徽章</h3>
        <div class="badge-container">
          <BaseBadge variant="difficulty" :difficulty="1" text="难度1" clickable @click="handleBadgeClick" />
          <BaseBadge variant="difficulty" :difficulty="2" text="难度2" clickable @click="handleBadgeClick" />
          <BaseBadge variant="difficulty" :difficulty="3" text="难度3" clickable @click="handleBadgeClick" />
          <BaseBadge variant="difficulty" :difficulty="4" text="难度4" clickable @click="handleBadgeClick" />
          <BaseBadge variant="difficulty" :difficulty="5" text="难度5" clickable @click="handleBadgeClick" />
        </div>
      </div>

      <div class="test-group">
        <h3>标签和尺寸</h3>
        <div class="badge-container">
          <BaseBadge variant="tag" text="数学" removable @remove="handleRemove" />
          <BaseBadge variant="tag" text="物理" removable @remove="handleRemove" />
          <BaseBadge variant="count" text="12" />
          <BaseBadge size="small" text="小" />
          <BaseBadge size="medium" text="中" />
          <BaseBadge size="large" text="大" />
        </div>
      </div>
    </section>

    <!-- BaseModal 测试 -->
    <section class="test-section">
      <h2>🎭 BaseModal 模态框组件</h2>
      
      <div class="button-group">
        <BaseButton @click="showModal = true">基础模态框</BaseButton>
        <BaseButton @click="showConfirmModal = true">确认对话框</BaseButton>
        <BaseButton @click="showLargeModal = true">大型模态框</BaseButton>
      </div>

      <!-- 基础模态框 -->
      <BaseModal
        v-model:visible="showModal"
        title="基础模态框"
        @confirm="handleModalConfirm"
        @cancel="showModal = false"
      >
        <p>这是一个基础的模态框组件，展示了标准的头部、主体和底部布局。</p>
        <BaseInput v-model="modalInput" label="在模态框中输入" placeholder="测试输入" />
      </BaseModal>

      <!-- 确认对话框 -->
      <BaseModal
        v-model:visible="showConfirmModal"
        title="确认删除"
        variant="confirm"
        size="small"
        confirm-text="删除"
        @confirm="handleDelete"
        @cancel="showConfirmModal = false"
      >
        <p>确定要删除这个知识点吗？此操作不可撤销。</p>
      </BaseModal>

      <!-- 大型模态框 -->
      <BaseModal
        v-model:visible="showLargeModal"
        title="大型模态框"
        size="large"
        @confirm="showLargeModal = false"
        @cancel="showLargeModal = false"
      >
        <div class="large-modal-content">
          <h4>复杂表单示例</h4>
          <div class="form-grid">
            <BaseInput v-model="formTitle" label="标题" />
            <BaseInput v-model="formCategory" label="分类" />
            <BaseInput v-model="formDifficulty" label="难度" />
            <BaseInput v-model="formDescription" label="描述" />
          </div>
        </div>
      </BaseModal>
    </section>

    <!-- BaseNotification 测试 -->
    <section class="test-section">
      <h2>🔔 BaseNotification 通知组件</h2>
      
      <div class="button-group">
        <BaseButton variant="primary" @click="showSuccessNotification">
          成功通知
        </BaseButton>
        <BaseButton variant="secondary" @click="showErrorNotification">
          错误通知
        </BaseButton>
        <BaseButton @click="showWarningNotification">
          警告通知
        </BaseButton>
        <BaseButton variant="info" @click="showInfoNotification">
          信息通知
        </BaseButton>
      </div>
      
      <div class="button-group">
        <BaseButton @click="showSimpleNotification">
          简单通知
        </BaseButton>
        <BaseButton @click="showPersistentNotification">
          持久通知
        </BaseButton>
        <BaseButton @click="showMultipleNotifications">
          多个通知
        </BaseButton>
        <BaseButton variant="secondary" @click="closeAllNotifications">
          关闭所有
        </BaseButton>
      </div>
    </section>
      </div>

      <!-- SDK测试内容 -->
      <div v-show="activeTab === 'sdk'" class="tab-content">
        <SDKTestComponent />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseBadge from '@/components/common/BaseBadge.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import SDKTestComponent from '@/components/test/SDKTestComponent.vue'
import { 
  notifySuccess, 
  notifyError, 
  notifyWarning, 
  notifyInfo, 
  closeAllNotifications as closeAll 
} from '@/utils/notification'

// Tab状态
const activeTab = ref<'components' | 'sdk'>('components')

// 响应式数据
const buttonLoading = ref(false)

// 输入框测试数据
const textInput = ref('')
const emailInput = ref('')
const passwordInput = ref('')
const numberInput = ref('42')

// 模态框相关
const showModal = ref(false)
const showConfirmModal = ref(false)
const showLargeModal = ref(false)
const modalInput = ref('')

// 表单数据
const formTitle = ref('线性代数基础')
const formCategory = ref('数学')
const formDifficulty = ref('3')
const formDescription = ref('向量空间与线性变换')

// 方法
const toggleLoading = () => {
  buttonLoading.value = true
  setTimeout(() => {
    buttonLoading.value = false
  }, 2000)
}

const showSuccess = () => {
  notifySuccess('操作成功完成')
}

const handleCardClick = () => {
  console.log('卡片被点击')
  notifyInfo('卡片被点击')
}

const handleBadgeClick = () => {
  console.log('徽章被点击')
  notifyInfo('徽章被点击')
}

const handleRemove = () => {
  console.log('标签被移除')
  notifySuccess('标签已移除')
}

const handleModalConfirm = () => {
  showModal.value = false
  notifySuccess('操作确认成功')
}

const handleDelete = () => {
  showConfirmModal.value = false
  notifySuccess('删除操作完成')
}

const showSuccessNotification = () => {
  notifySuccess('数据已成功保存到数据库', '操作成功')
}

const showErrorNotification = () => {
  notifyError('网络连接异常，请稍后重试', '操作失败')
}

const showWarningNotification = () => {
  notifyWarning('您有5个知识点还未复习', '注意')
}

const showInfoNotification = () => {
  notifyInfo('系统将在5分钟后进行维护')
}

const showSimpleNotification = () => {
  notifyInfo('这是一个简单的通知消息')
}

const showPersistentNotification = () => {
  notifyWarning('这是一个持久通知，需要手动关闭', '重要提醒', { persistent: true })
}

const showMultipleNotifications = () => {
  notifySuccess('第一个通知')
  setTimeout(() => notifyInfo('第二个通知'), 500)
  setTimeout(() => notifyWarning('第三个通知'), 1000)
}

const closeAllNotifications = () => {
  closeAll()
}
</script>

<style scoped>
.component-test-view {
  width: 100%;
  padding: 2rem 0;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.test-header {
  text-align: center;
  margin-bottom: 3rem;
  padding: 2rem;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.test-header h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.test-header p {
  color: #5a6c7d;
  font-size: 1.125rem;
  margin: 0;
}

.tab-container {
  margin-bottom: 2rem;
}

.tab-nav {
  display: flex;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem;
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.tab-button {
  flex: 1;
  padding: 1rem 2rem;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  color: #5a6c7d;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-button:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.tab-button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

.tab-content {
  min-height: 400px;
}

.test-section {
  margin-bottom: 3rem;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.08);
}

.test-section h2 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
  padding-bottom: 0.5rem;
}

.test-group {
  margin-bottom: 2rem;
}

.test-group h3 {
  color: #2c3e50;
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.stat-content {
  text-align: center;
  padding: 1rem 0;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #5a6c7d;
  font-size: 0.875rem;
  font-weight: 500;
}

.input-preview {
  background: rgba(102, 126, 234, 0.05);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  padding: 1rem;
}

.input-preview h4 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.preview-content p {
  margin: 0.25rem 0;
  color: #2c3e50;
  font-size: 0.875rem;
}

.badge-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
  margin-bottom: 1rem;
}

.large-modal-content {
  max-height: 60vh;
  overflow-y: auto;
}

/* 响应式设计 */
@media (min-width: 1920px) {
  .component-test-view {
    padding: 0 2rem;
  }
}

@media (max-width: 768px) {
  .component-test-view {
    padding: 1rem 0;
    margin-top: 1rem;
  }
  
  .test-header {
    padding: 1.5rem;
    margin-bottom: 2rem;
  }
  
  .test-header h1 {
    font-size: 2rem;
  }
  
  .test-section {
    padding: 1.5rem;
    margin-bottom: 2rem;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .tab-nav {
    flex-direction: column;
  }
  
  .tab-button {
    padding: 0.75rem 1rem;
  }
}

@media (max-width: 480px) {
  .test-header h1 {
    font-size: 1.75rem;
  }
  
  .test-section h2 {
    font-size: 1.5rem;
  }
  
  .badge-container {
    gap: 0.5rem;
  }
}
</style> 