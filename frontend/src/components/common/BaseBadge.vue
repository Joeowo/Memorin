<template>
  <span
    class="base-badge"
    :class="[
      `base-badge--${variant}`,
      `base-badge--${size}`,
      {
        'base-badge--clickable': clickable,
        'base-badge--removable': removable
      }
    ]"
    :style="customStyle"
    @click="handleClick"
  >
    <!-- 图标 -->
    <span v-if="icon" class="base-badge-icon">{{ icon }}</span>
    
    <!-- 内容 -->
    <slot>{{ text }}</slot>
    
    <!-- 删除按钮 -->
    <button
      v-if="removable"
      class="base-badge-remove"
      type="button"
      @click.stop="handleRemove"
      aria-label="移除"
    >
      ✕
    </button>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// Props定义
interface Props {
  text?: string | number
  variant?: 'default' | 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'info' | 'difficulty' | 'tag' | 'count'
  size?: 'small' | 'medium' | 'large'
  color?: string
  backgroundColor?: string
  icon?: string
  clickable?: boolean
  removable?: boolean
  difficulty?: 1 | 2 | 3 | 4 | 5
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'medium',
  clickable: false,
  removable: false
})

// 事件定义
const emit = defineEmits<{
  click: [event: MouseEvent]
  remove: []
}>()

// 计算属性
const customStyle = computed(() => {
  const style: Record<string, string> = {}
  
  // 难度等级样式
  if (props.variant === 'difficulty' && props.difficulty) {
    const difficultyColors = {
      1: { bg: 'rgba(46, 204, 113, 0.15)', color: '#27ae60', border: 'rgba(46, 204, 113, 0.3)' },
      2: { bg: 'rgba(52, 152, 219, 0.15)', color: '#3498db', border: 'rgba(52, 152, 219, 0.3)' },
      3: { bg: 'rgba(241, 196, 15, 0.15)', color: '#f39c12', border: 'rgba(241, 196, 15, 0.3)' },
      4: { bg: 'rgba(230, 126, 34, 0.15)', color: '#e67e22', border: 'rgba(230, 126, 34, 0.3)' },
      5: { bg: 'rgba(231, 76, 60, 0.15)', color: '#e74c3c', border: 'rgba(231, 76, 60, 0.3)' }
    }
    const colors = difficultyColors[props.difficulty]
    style.backgroundColor = colors.bg
    style.color = colors.color
    style.borderColor = colors.border
  }
  
  // 自定义颜色
  if (props.color) {
    style.color = props.color
  }
  if (props.backgroundColor) {
    style.backgroundColor = props.backgroundColor
  }
  
  return style
})

// 事件处理
const handleClick = (event: MouseEvent) => {
  if (props.clickable) {
    emit('click', event)
  }
}

const handleRemove = () => {
  emit('remove')
}
</script>

<style scoped>
.base-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-weight: 500;
  border-radius: 6px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
  white-space: nowrap;
  vertical-align: middle;
  line-height: 1;
  user-select: none;
}

/* 尺寸变体 */
.base-badge--small {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.base-badge--medium {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
}

.base-badge--large {
  padding: 0.5rem 1rem;
  font-size: 1rem;
}

/* 颜色变体 */
.base-badge--default {
  background: rgba(108, 117, 125, 0.15);
  color: #6c757d;
  border-color: rgba(108, 117, 125, 0.2);
}

.base-badge--primary {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
  border-color: rgba(102, 126, 234, 0.3);
}

.base-badge--secondary {
  background: rgba(118, 75, 162, 0.15);
  color: #764ba2;
  border-color: rgba(118, 75, 162, 0.3);
}

.base-badge--success {
  background: rgba(46, 204, 113, 0.15);
  color: #27ae60;
  border-color: rgba(46, 204, 113, 0.3);
}

.base-badge--warning {
  background: rgba(241, 196, 15, 0.15);
  color: #f39c12;
  border-color: rgba(241, 196, 15, 0.3);
}

.base-badge--danger {
  background: rgba(231, 76, 60, 0.15);
  color: #e74c3c;
  border-color: rgba(231, 76, 60, 0.3);
}

.base-badge--info {
  background: rgba(52, 152, 219, 0.15);
  color: #3498db;
  border-color: rgba(52, 152, 219, 0.3);
}

/* 特殊变体 */
.base-badge--difficulty {
  background: rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.base-badge--tag {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.1);
}

.base-badge--count {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50%;
  min-width: 1.5rem;
  height: 1.5rem;
  padding: 0;
  justify-content: center;
  font-weight: 600;
  font-size: 0.75rem;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

/* 可点击状态 */
.base-badge--clickable {
  cursor: pointer;
}

.base-badge--clickable:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.base-badge--tag.base-badge--clickable:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.base-badge--difficulty.base-badge--clickable:hover {
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

/* 可移除状态 */
.base-badge--removable {
  padding-right: 0.25rem;
}

.base-badge--removable.base-badge--small {
  padding-right: 0.125rem;
}

.base-badge--removable.base-badge--large {
  padding-right: 0.375rem;
}

/* 图标样式 */
.base-badge-icon {
  flex-shrink: 0;
  font-size: 0.875em;
  opacity: 0.8;
}

/* 删除按钮样式 */
.base-badge-remove {
  background: none;
  border: none;
  color: currentColor;
  cursor: pointer;
  padding: 0;
  margin-left: 0.25rem;
  font-size: 0.75em;
  opacity: 0.6;
  transition: all 0.2s ease;
  border-radius: 50%;
  width: 1em;
  height: 1em;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.base-badge-remove:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* 特定变体的删除按钮 */
.base-badge--tag .base-badge-remove:hover {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.base-badge--difficulty .base-badge-remove:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 活跃状态 */
.base-badge--clickable:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(102, 126, 234, 0.15);
}

/* 禁用状态 */
.base-badge[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.base-badge--count {
  animation: pulse 2s infinite;
}

.base-badge--count:hover {
  animation: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-badge--large {
    padding: 0.375rem 0.75rem;
    font-size: 0.875rem;
  }
  
  .base-badge--medium {
    padding: 0.25rem 0.5rem;
    font-size: 0.8rem;
  }
  
  .base-badge--small {
    padding: 0.125rem 0.375rem;
    font-size: 0.7rem;
  }
  
  .base-badge--count {
    min-width: 1.25rem;
    height: 1.25rem;
    font-size: 0.7rem;
  }
}

@media (max-width: 480px) {
  .base-badge {
    gap: 0.125rem;
  }
  
  .base-badge-remove {
    margin-left: 0.125rem;
  }
}

/* 组合使用样式 */
.base-badge + .base-badge {
  margin-left: 0.375rem;
}

/* 在容器中的对齐 */
.badge-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.375rem;
  align-items: center;
}

.badge-container .base-badge {
  margin: 0;
}

/* 特殊场景样式 */
.base-badge--tag.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  border-color: rgba(102, 126, 234, 0.5);
  color: #5a67d8;
}

.base-badge--difficulty.mastered {
  background: rgba(46, 204, 113, 0.2);
  color: #27ae60;
  border-color: rgba(46, 204, 113, 0.4);
}
</style> 