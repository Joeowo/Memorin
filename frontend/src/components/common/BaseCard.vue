<template>
  <div 
    :class="[
      'base-card', 
      `base-card--${variant}`,
      { 'base-card--clickable': clickable, 'base-card--hover': hoverEffect }
    ]"
    @click="handleClick"
  >
    <slot />
  </div>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'

// Props定义
interface Props {
  variant?: 'default' | 'stat' | 'action'
  clickable?: boolean
  hoverEffect?: boolean
}

withDefaults(defineProps<Props>(), {
  variant: 'default',
  clickable: false,
  hoverEffect: true
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
.base-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  box-shadow: 
    0 8px 32px rgba(102, 126, 234, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease;
  width: 100%;
  box-sizing: border-box;
}

.base-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.2), transparent);
}

.base-card::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.05) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

/* Hover效果 */
.base-card--hover:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 
    0 20px 60px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(102, 126, 234, 0.2);
}

.base-card--hover:hover::after {
  opacity: 1;
}

/* 可点击状态 */
.base-card--clickable {
  cursor: pointer;
}

/* Default variant */
.base-card--default {
  padding: 2rem;
}

/* Stat variant - 用于统计卡片 */
.base-card--stat {
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  min-height: 120px;
}

/* Action variant - 用于操作面板 */
.base-card--action {
  padding: 2rem;
  margin-bottom: 2rem;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .base-card--stat {
    padding: 1.5rem;
  }
}

@media (max-width: 768px) {
  .base-card--stat {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .base-card--stat {
    padding: 1.2rem;
  }
  
  .base-card--action {
    padding: 1.5rem;
  }
  
  .base-card--default {
    padding: 1.5rem;
  }
}
</style> 