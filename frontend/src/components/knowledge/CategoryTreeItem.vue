<template>
  <div class="tree-item" :style="{ marginLeft: `${level * 20}px` }">
    <div class="item-content" :class="{ selected: selectedIds.includes(category.id) }">
      <div class="item-checkbox">
        <input
          type="checkbox"
          :checked="selectedIds.includes(category.id)"
          @change="handleToggle"
        />
      </div>
      
      <div class="item-info" @click="handleToggle">
        <div class="item-icon" :style="{ backgroundColor: category.color }">
          {{ category.icon }}
        </div>
        <div class="item-text">
          <span class="item-name">{{ category.name }}</span>
          <span class="item-level">Level {{ category.level }}</span>
        </div>
      </div>
    </div>
    
    <!-- 子分类 -->
    <div v-if="childCategories.length > 0" class="child-categories">
      <CategoryTreeItem
        v-for="child in childCategories"
        :key="child.id"
        :category="child"
        :level="level + 1"
        :selectedIds="selectedIds"
        :categories="categories"
        @toggle="$emit('toggle', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CategoryData } from '@/utils/memorin-sdk'

interface Props {
  category: CategoryData
  level: number
  selectedIds: string[]
  categories: CategoryData[]
}

interface Emits {
  (e: 'toggle', categoryId: string): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

const childCategories = computed(() => {
  return props.categories
    .filter(cat => cat.parentId === props.category.id)
    .sort((a, b) => a.name.localeCompare(b.name))
})

function handleToggle() {
  // 选择/取消选择当前分类
  const isSelected = props.selectedIds.includes(props.category.id)
  
  // 获取所有相关ID（当前分类及其所有子分类）
  const relatedIds = [props.category.id]
  collectChildIds(props.category.id, relatedIds)
  
  // 根据是否已选择来决定添加还是移除
  if (isSelected) {
    // 移除所有相关ID
    relatedIds.forEach(id => {
      const index = props.selectedIds.indexOf(id)
      if (index > -1) {
        const newSelected = [...props.selectedIds]
        newSelected.splice(index, 1)
        // 这里需要重新触发，因为Vue的响应式
        // 实际实现中应该通过父组件处理
      }
    })
  } else {
    // 添加所有相关ID
    relatedIds.forEach(id => {
      if (!props.selectedIds.includes(id)) {
        const newSelected = [...props.selectedIds, id]
        // 同样通过父组件处理
      }
    })
  }
  
  // 直接触发切换事件
  emit('toggle', props.category.id)
}

function collectChildIds(parentId: string, ids: string[]) {
  const children = props.categories.filter(cat => cat.parentId === parentId)
  children.forEach(child => {
    if (!ids.includes(child.id)) {
      ids.push(child.id)
      collectChildIds(child.id, ids)
    }
  })
}
</script>

<style scoped>
.tree-item {
  margin-bottom: 0.25rem;
}

.item-content {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.item-content:hover {
  background-color: #f8f9fa;
}

.item-content.selected {
  background-color: #e3f2fd;
}

.item-checkbox {
  margin-right: 0.5rem;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.item-icon {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: white;
}

.item-text {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.item-name {
  font-weight: 500;
  color: #2c3e50;
  font-size: 0.9rem;
}

.item-level {
  font-size: 0.7rem;
  color: #6c757d;
}

.child-categories {
  margin-top: 0.25rem;
}
</style>