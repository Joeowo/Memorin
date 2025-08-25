<template>
  <button 
    :class="['base-btn', `base-btn--${variant}`, `base-btn--${size}`, { 'base-btn--loading': loading }]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner"></span>
    <slot v-else />
  </button>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'

// Props定义
interface Props {
  variant?: 'primary' | 'secondary' | 'info' | 'success' | 'warning' | 'danger'
  size?: 'small' | 'medium' | 'large'
  disabled?: boolean
  loading?: boolean
}

withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'medium',
  disabled: false,
  loading: false
})

// 事件定义
const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const handleClick = (event: MouseEvent) => {
  emit('click', event)
}
</script>

<style scoped>
.base-btn {
  border: none;
  border-radius: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  text-align: center;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  white-space: nowrap;
}

.base-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.base-btn:hover::before {
  left: 100%;
}

.base-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* 尺寸变体 */
.base-btn--small {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  min-width: 80px;
}

.base-btn--medium {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  min-width: 120px;
}

.base-btn--large {
  padding: 1rem 2rem;
  font-size: 1.125rem;
  min-width: 160px;
}

/* Primary variant */
.base-btn--primary {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.base-btn--primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);
}

/* Secondary variant */
.base-btn--secondary {
  background: rgba(108, 117, 125, 0.8);
  color: white;
  box-shadow: 0 4px 15px rgba(108, 117, 125, 0.3);
}

.base-btn--secondary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(108, 117, 125, 0.4);
  background: rgba(108, 117, 125, 0.9);
}

/* Info variant */
.base-btn--info {
  background: rgba(23, 162, 184, 0.8);
  color: white;
  box-shadow: 0 4px 15px rgba(23, 162, 184, 0.3);
}

.base-btn--info:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(23, 162, 184, 0.4);
  background: rgba(23, 162, 184, 0.9);
}

/* Success variant */
.base-btn--success {
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.8) 0%, rgba(56, 189, 89, 0.8) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
}

.base-btn--success:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(40, 167, 69, 0.4);
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.9) 0%, rgba(56, 189, 89, 0.9) 100%);
}

/* Warning variant */
.base-btn--warning {
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.8) 0%, rgba(255, 206, 58, 0.8) 100%);
  color: #212529;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
}

.base-btn--warning:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 193, 7, 0.4);
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.9) 0%, rgba(255, 206, 58, 0.9) 100%);
}

/* Danger variant */
.base-btn--danger {
  background: linear-gradient(135deg, rgba(220, 53, 69, 0.8) 0%, rgba(233, 84, 99, 0.8) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
}

.base-btn--danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(220, 53, 69, 0.4);
  background: linear-gradient(135deg, rgba(220, 53, 69, 0.9) 0%, rgba(233, 84, 99, 0.9) 100%);
}

/* Loading state */
.base-btn--loading {
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-btn {
    width: 100%;
  }
  
  .base-btn--small {
    padding: 0.75rem 1.2rem;
  }
  
  .base-btn--medium {
    padding: 1rem 1.5rem;
  }
  
  .base-btn--large {
    padding: 1.2rem 2rem;
  }
}

@media (max-width: 480px) {
  .base-btn--small {
    font-size: 0.8rem;
    padding: 0.7rem 1rem;
  }
  
  .base-btn--medium {
    font-size: 0.9rem;
    padding: 0.9rem 1.2rem;
  }
  
  .base-btn--large {
    font-size: 1rem;
    padding: 1rem 1.5rem;
  }
}
</style> 