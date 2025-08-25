<template>
  <Teleport to="body">
    <Transition name="notification" appear>
      <div
        v-if="visible"
        class="base-notification"
        :class="[
          `base-notification--${variant}`,
          `base-notification--${position}`
        ]"
        @click="handleClick"
      >
        <!-- 消息内容 -->
        <div class="base-notification-content">
          <!-- 标题 -->
          <div v-if="title" class="base-notification-title">
            {{ title }}
          </div>
          
          <!-- 消息文本 -->
          <div class="base-notification-message">
            <slot>{{ message }}</slot>
          </div>
        </div>

        <!-- 关闭按钮 -->
        <button
          v-if="closable"
          class="base-notification-close"
          type="button"
          @click.stop="handleClose"
          aria-label="关闭通知"
        >
          ✕
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onUnmounted, watchEffect } from 'vue'

// Props定义
interface Props {
  visible?: boolean
  variant?: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message?: string
  duration?: number
  closable?: boolean
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center'
  persistent?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  variant: 'info',
  duration: 3000,
  closable: true,
  position: 'top-right',
  persistent: false
})

// 事件定义
const emit = defineEmits<{
  'update:visible': [visible: boolean]
  close: []
  click: [event: MouseEvent]
}>()

// 响应式数据
const timer = ref<number | null>(null)

// 方法
const handleClose = () => {
  clearAutoClose()
  emit('update:visible', false)
  emit('close')
}

const handleClick = (event: MouseEvent) => {
  emit('click', event)
}

const clearAutoClose = () => {
  if (timer.value) {
    clearTimeout(timer.value)
    timer.value = null
  }
}

const startAutoClose = () => {
  if (props.duration > 0 && !props.persistent) {
    timer.value = window.setTimeout(() => {
      handleClose()
    }, props.duration)
  }
}

// 监听visible变化
watchEffect(() => {
  if (props.visible) {
    startAutoClose()
  } else {
    clearAutoClose()
  }
})

// 生命周期清理
onUnmounted(() => {
  clearAutoClose()
})
</script>

<style scoped>
.base-notification {
  position: fixed;
  z-index: 2000;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  color: white;
  font-weight: 500;
  max-width: 400px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.base-notification:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* 位置变体 */
.base-notification--top-right {
  top: 140px;
  right: 20px;
}

.base-notification--top-left {
  top: 140px;
  left: 20px;
}

.base-notification--bottom-right {
  bottom: 20px;
  right: 20px;
}

.base-notification--bottom-left {
  bottom: 20px;
  left: 20px;
}

.base-notification--top-center {
  top: 140px;
  left: 50%;
  transform: translateX(-50%);
}

.base-notification--bottom-center {
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

/* 状态变体 - 参考原项目样式 */
.base-notification--success {
  background: linear-gradient(135deg, rgba(46, 204, 113, 0.9), rgba(39, 174, 96, 0.9));
}

.base-notification--error {
  background: linear-gradient(135deg, rgba(231, 76, 60, 0.9), rgba(192, 57, 43, 0.9));
}

.base-notification--warning {
  background: linear-gradient(135deg, rgba(243, 156, 18, 0.9), rgba(230, 126, 34, 0.9));
}

.base-notification--info {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));
}

/* 内容样式 */
.base-notification-content {
  flex: 1;
  min-width: 0;
}

.base-notification-title {
  font-weight: 600;
  font-size: 1rem;
  color: white;
  margin-bottom: 0.25rem;
  line-height: 1.3;
}

.base-notification-message {
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
  word-wrap: break-word;
}

/* 关闭按钮 */
.base-notification-close {
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 0.125rem;
}

.base-notification-close:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}

/* 动画效果 - 参考原项目slideInRight */
.notification-enter-active {
  animation: slideInRight 0.3s ease;
}

.notification-leave-active {
  animation: slideOutRight 0.3s ease;
}

@keyframes slideInRight {
  from { 
    transform: translateX(100%) scale(0.95); 
    opacity: 0; 
  }
  to { 
    transform: translateX(0) scale(1); 
    opacity: 1; 
  }
}

@keyframes slideOutRight {
  from { 
    transform: translateX(0) scale(1); 
    opacity: 1; 
  }
  to { 
    transform: translateX(100%) scale(0.95); 
    opacity: 0; 
  }
}

/* 左侧位置的动画 */
.base-notification--top-left.notification-enter-active,
.base-notification--bottom-left.notification-enter-active {
  animation: slideInLeft 0.3s ease;
}

.base-notification--top-left.notification-leave-active,
.base-notification--bottom-left.notification-leave-active {
  animation: slideOutLeft 0.3s ease;
}

@keyframes slideInLeft {
  from { 
    transform: translateX(-100%) scale(0.95); 
    opacity: 0; 
  }
  to { 
    transform: translateX(0) scale(1); 
    opacity: 1; 
  }
}

@keyframes slideOutLeft {
  from { 
    transform: translateX(0) scale(1); 
    opacity: 1; 
  }
  to { 
    transform: translateX(-100%) scale(0.95); 
    opacity: 0; 
  }
}

/* 中央位置的动画 */
.base-notification--top-center.notification-enter-active,
.base-notification--bottom-center.notification-enter-active {
  animation: slideInCenter 0.3s ease;
}

.base-notification--top-center.notification-leave-active,
.base-notification--bottom-center.notification-leave-active {
  animation: slideOutCenter 0.3s ease;
}

@keyframes slideInCenter {
  from { 
    transform: translateX(-50%) translateY(-20px) scale(0.95); 
    opacity: 0; 
  }
  to { 
    transform: translateX(-50%) translateY(0) scale(1); 
    opacity: 1; 
  }
}

@keyframes slideOutCenter {
  from { 
    transform: translateX(-50%) translateY(0) scale(1); 
    opacity: 1; 
  }
  to { 
    transform: translateX(-50%) translateY(-20px) scale(0.95); 
    opacity: 0; 
  }
}

/* 中央位置悬停效果 */
.base-notification--top-center:hover,
.base-notification--bottom-center:hover {
  transform: translateX(-50%) translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-notification {
    width: calc(100vw - 40px);
    max-width: 320px;
    margin: 0 20px;
  }
  
  .base-notification--top-right,
  .base-notification--bottom-right {
    right: 0;
  }
  
  .base-notification--top-left,
  .base-notification--bottom-left {
    left: 0;
  }
  
  .base-notification--top-center,
  .base-notification--bottom-center {
    left: 20px;
    right: 20px;
    transform: none;
    width: auto;
  }
}

@media (max-width: 480px) {
  .base-notification {
    padding: 0.875rem 1rem;
    border-radius: 8px;
    font-size: 0.875rem;
  }
  
  .base-notification-title {
    font-size: 0.9rem;
  }
  
  .base-notification-message {
    font-size: 0.8rem;
  }
  
  .base-notification-close {
    width: 1.25rem;
    height: 1.25rem;
    font-size: 0.7rem;
  }
}

/* 多个通知的堆叠效果 */
.base-notification + .base-notification {
  margin-top: 0.75rem;
}
</style> 