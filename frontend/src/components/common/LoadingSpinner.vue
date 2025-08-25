<template>
  <div 
    :class="[
      'loading-spinner', 
      `loading-spinner--${size}`, 
      `loading-spinner--${variant}`
    ]"
    :style="{ width: spinnerSize, height: spinnerSize }"
  >
    <div class="spinner-circle"></div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// Props定义
interface Props {
  size?: 'small' | 'medium' | 'large' | 'extra-large'
  variant?: 'primary' | 'secondary' | 'white' | 'success' | 'warning' | 'danger'
  text?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 'medium',
  variant: 'primary',
  text: ''
})

// 计算spinner尺寸
const spinnerSize = computed(() => {
  const sizes = {
    'small': '16px',
    'medium': '24px', 
    'large': '32px',
    'extra-large': '48px'
  }
  return sizes[props.size]
})
</script>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.spinner-circle {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  animation: spin 1s linear infinite;
  position: relative;
}

.spinner-circle::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Primary variant - 蓝紫色渐变主题 */
.loading-spinner--primary .spinner-circle {
  color: #667eea;
  border-top-color: #667eea;
  filter: drop-shadow(0 0 8px rgba(102, 126, 234, 0.3));
}

.loading-spinner--primary .spinner-circle::before {
  border-color: rgba(102, 126, 234, 0.1);
}

/* Secondary variant */
.loading-spinner--secondary .spinner-circle {
  color: #6c757d;
  border-top-color: #6c757d;
  filter: drop-shadow(0 0 8px rgba(108, 117, 125, 0.3));
}

.loading-spinner--secondary .spinner-circle::before {
  border-color: rgba(108, 117, 125, 0.1);
}

/* White variant - 适用于深色背景 */
.loading-spinner--white .spinner-circle {
  color: #ffffff;
  border-top-color: #ffffff;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.3));
}

.loading-spinner--white .spinner-circle::before {
  border-color: rgba(255, 255, 255, 0.2);
}

.loading-spinner--white .loading-text {
  color: #ffffff;
}

/* Success variant */
.loading-spinner--success .spinner-circle {
  color: #28a745;
  border-top-color: #28a745;
  filter: drop-shadow(0 0 8px rgba(40, 167, 69, 0.3));
}

.loading-spinner--success .spinner-circle::before {
  border-color: rgba(40, 167, 69, 0.1);
}

/* Warning variant */
.loading-spinner--warning .spinner-circle {
  color: #ffc107;
  border-top-color: #ffc107;
  filter: drop-shadow(0 0 8px rgba(255, 193, 7, 0.3));
}

.loading-spinner--warning .spinner-circle::before {
  border-color: rgba(255, 193, 7, 0.1);
}

/* Danger variant */
.loading-spinner--danger .spinner-circle {
  color: #dc3545;
  border-top-color: #dc3545;
  filter: drop-shadow(0 0 8px rgba(220, 53, 69, 0.3));
}

.loading-spinner--danger .spinner-circle::before {
  border-color: rgba(220, 53, 69, 0.1);
}

/* 尺寸相关样式 */
.loading-spinner--small .spinner-circle {
  border-width: 1.5px;
}

.loading-spinner--small .spinner-circle::before {
  border-width: 1.5px;
}

.loading-spinner--medium .spinner-circle {
  border-width: 2px;
}

.loading-spinner--medium .spinner-circle::before {
  border-width: 2px;
}

.loading-spinner--large .spinner-circle {
  border-width: 3px;
}

.loading-spinner--large .spinner-circle::before {
  border-width: 3px;
}

.loading-spinner--extra-large .spinner-circle {
  border-width: 4px;
}

.loading-spinner--extra-large .spinner-circle::before {
  border-width: 4px;
}

/* 加载文本样式 */
.loading-text {
  font-size: 0.875rem;
  color: #6c757d;
  margin: 0;
  font-weight: 500;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.loading-spinner--small .loading-text {
  font-size: 0.75rem;
  padding: 0.2rem 0.4rem;
}

.loading-spinner--large .loading-text,
.loading-spinner--extra-large .loading-text {
  font-size: 1rem;
  padding: 0.3rem 0.6rem;
}

/* 旋转动画 */
@keyframes spin {
  0% { 
    transform: rotate(0deg); 
  }
  100% { 
    transform: rotate(360deg); 
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-text {
    font-size: 0.8rem;
  }
  
  .loading-spinner--small .loading-text {
    font-size: 0.7rem;
  }
}

@media (max-width: 480px) {
  .loading-text {
    font-size: 0.75rem;
  }
  
  .loading-spinner--small .loading-text {
    font-size: 0.65rem;
  }
}

/* 无障碍访问 */
@media (prefers-reduced-motion: reduce) {
  .spinner-circle {
    animation-duration: 2s;
  }
}
</style> 