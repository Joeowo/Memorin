<template>
  <Teleport to="body">
    <Transition name="modal" appear>
      <div
        v-if="visible"
        class="base-modal"
        :class="{ 'base-modal--confirm': variant === 'confirm' }"
        @click="handleBackdropClick"
      >
        <div
          class="base-modal-content"
          :class="[
            `base-modal-content--${size}`,
            `base-modal-content--${variant}`
          ]"
          @click.stop
        >
          <!-- Loading遮罩层 -->
          <div v-if="loading" class="base-modal-loading-overlay">
            <LoadingSpinner 
              size="large" 
              variant="primary" 
              :text="loadingText || '处理中...'" 
            />
          </div>
          
          <!-- 装饰性顶部线条 -->
          <div class="base-modal-decoration"></div>
          
          <!-- 模态框头部 -->
          <header v-if="!hideHeader" class="base-modal-header">
            <div class="base-modal-title">
              <slot name="title">
                <h3>{{ title }}</h3>
              </slot>
            </div>
            <button
              v-if="showCloseButton"
              class="base-modal-close"
              type="button"
              @click="handleClose"
              aria-label="关闭"
              :disabled="loading"
            >
              ✕
            </button>
          </header>

          <!-- 模态框主体 -->
          <main class="base-modal-body">
            <slot></slot>
          </main>

          <!-- 模态框底部 -->
          <footer v-if="!hideFooter || $slots.footer" class="base-modal-footer">
            <slot name="footer">
              <div class="base-modal-actions">
                <BaseButton
                  v-if="showCancelButton"
                  variant="secondary"
                  @click="handleCancel"
                  :disabled="loading"
                >
                  {{ cancelText }}
                </BaseButton>
                <BaseButton
                  v-if="showConfirmButton"
                  variant="primary"
                  @click="handleConfirm"
                  :loading="loading"
                >
                  {{ confirmText }}
                </BaseButton>
              </div>
            </slot>
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { watch, nextTick, onMounted, onUnmounted } from 'vue'
import BaseButton from './BaseButton.vue'
import LoadingSpinner from './LoadingSpinner.vue'

// Props定义
interface Props {
  visible?: boolean
  title?: string
  size?: 'small' | 'medium' | 'large' | 'full'
  variant?: 'default' | 'confirm'
  hideHeader?: boolean
  hideFooter?: boolean
  showCloseButton?: boolean
  showCancelButton?: boolean
  showConfirmButton?: boolean
  cancelText?: string
  confirmText?: string
  closeOnBackdrop?: boolean
  closeOnEscape?: boolean
  loading?: boolean
  loadingText?: string
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  size: 'medium',
  variant: 'default',
  hideHeader: false,
  hideFooter: false,
  showCloseButton: true,
  showCancelButton: true,
  showConfirmButton: true,
  cancelText: '取消',
  confirmText: '确认',
  closeOnBackdrop: true,
  closeOnEscape: true,
  loading: false,
  loadingText: ''
})

// 事件定义
const emit = defineEmits<{
  'update:visible': [visible: boolean]
  close: []
  cancel: []
  confirm: []
  opened: []
  closed: []
}>()

// 事件处理
const handleClose = () => {
  emit('update:visible', false)
  emit('close')
}

const handleCancel = () => {
  emit('cancel')
  if (!props.loading) {
    handleClose()
  }
}

const handleConfirm = () => {
  emit('confirm')
}

const handleBackdropClick = () => {
  if (props.closeOnBackdrop && !props.loading) {
    handleClose()
  }
}

const handleEscapeKey = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.closeOnEscape && props.visible && !props.loading) {
    handleClose()
  }
}

// 生命周期
watch(() => props.visible, async (newVisible) => {
  if (newVisible) {
    await nextTick()
    emit('opened')
    document.body.style.overflow = 'hidden'
  } else {
    emit('closed')
    document.body.style.overflow = ''
  }
})

onMounted(() => {
  document.addEventListener('keydown', handleEscapeKey)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscapeKey)
  document.body.style.overflow = ''
})
</script>

<style scoped>
.base-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  box-sizing: border-box;
}

.base-modal-content {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  max-height: 90vh;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

/* Loading遮罩层样式 */
.base-modal-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.base-modal-loading-overlay::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg, 
    rgba(102, 126, 234, 0.05) 0%, 
    rgba(118, 75, 162, 0.05) 50%,
    rgba(102, 126, 234, 0.05) 100%
  );
  border-radius: 20px;
  animation: shimmer 2s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}

.base-modal-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
}

/* 尺寸变体 */
.base-modal-content--small {
  width: 100%;
  max-width: 400px;
}

.base-modal-content--medium {
  width: 100%;
  max-width: 600px;
}

.base-modal-content--large {
  width: 100%;
  max-width: 800px;
}

.base-modal-content--full {
  width: 95%;
  max-width: 1200px;
  height: 90vh;
}

/* 确认对话框变体 */
.base-modal-content--confirm {
  max-width: 450px;
}

.base-modal--confirm .base-modal-header {
  text-align: center;
  border-bottom: none;
  padding-bottom: 1rem;
}

.base-modal--confirm .base-modal-body {
  text-align: center;
  padding: 1rem 2rem;
}

/* 头部样式 */
.base-modal-header {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 20px 20px 0 0;
  flex-shrink: 0;
}

.base-modal-title h3 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.base-modal-close {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  font-size: 1.2rem;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.base-modal-close:hover:not(:disabled) {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.2);
}

.base-modal-close:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

/* 主体样式 */
.base-modal-body {
  padding: 2rem;
  flex: 1;
  overflow-y: auto;
  color: #2c3e50;
  line-height: 1.6;
}

/* 底部样式 */
.base-modal-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  background: rgba(102, 126, 234, 0.02);
  border-radius: 0 0 20px 20px;
  flex-shrink: 0;
}

.base-modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

/* 动画效果 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .base-modal-content,
.modal-leave-active .base-modal-content {
  transition: all 0.3s ease;
}

.modal-enter-from .base-modal-content,
.modal-leave-to .base-modal-content {
  transform: translateY(-50px) scale(0.95);
  opacity: 0;
}

.modal-enter-to .base-modal-content,
.modal-leave-from .base-modal-content {
  transform: translateY(0) scale(1);
  opacity: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-modal {
    padding: 0.5rem;
  }
  
  .base-modal-content {
    border-radius: 16px;
    max-height: 95vh;
  }
  
  .base-modal-loading-overlay {
    border-radius: 16px;
  }
  
  .base-modal-loading-overlay::before {
    border-radius: 16px;
  }
  
  .base-modal-header {
    padding: 1rem 1.5rem;
    border-radius: 16px 16px 0 0;
  }
  
  .base-modal-title h3 {
    font-size: 1.25rem;
  }
  
  .base-modal-body {
    padding: 1.5rem;
  }
  
  .base-modal-footer {
    padding: 1rem 1.5rem;
    border-radius: 0 0 16px 16px;
  }
  
  .base-modal-actions {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .base-modal-content--full {
    width: 100%;
    height: 95vh;
    border-radius: 0;
  }
  
  .base-modal-content--full .base-modal-header,
  .base-modal-content--full .base-modal-footer {
    border-radius: 0;
  }
  
  .base-modal-content--full .base-modal-loading-overlay,
  .base-modal-content--full .base-modal-loading-overlay::before {
    border-radius: 0;
  }
}

@media (max-width: 480px) {
  .base-modal {
    padding: 0;
  }
  
  .base-modal-content--small,
  .base-modal-content--medium,
  .base-modal-content--large {
    width: 100%;
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
  }
  
  .base-modal-header,
  .base-modal-footer {
    border-radius: 0;
  }
  
  .base-modal-loading-overlay,
  .base-modal-loading-overlay::before {
    border-radius: 0;
  }
}

/* 滚动条样式 */
.base-modal-body::-webkit-scrollbar {
  width: 6px;
}

.base-modal-body::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.1);
  border-radius: 3px;
}

.base-modal-body::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

.base-modal-body::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}
</style> 