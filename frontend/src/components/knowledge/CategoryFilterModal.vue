<template>
  <BaseModal
    :visible="visible"
    @close="$emit('close')"
    title="选择分类"
    size="large"
  >
    <div class="category-filter-modal">
      <!-- 分类树 -->
      <div class="category-tree">
        <CategoryTree
          :categories="categories"
          :selectedIds="selectedIds"
          @toggle="toggleCategory"
        />
      </div>
      
      <!-- 已选分类 -->
      <div v-if="selectedCategories.length > 0" class="selected-categories">
        <h4>已选择 ({{ selectedCategories.length }})</h4>
        <div class="selected-list">
          <span
            v-for="category in selectedCategories"
            :key="category.id"
            class="selected-tag"
          >
            {{ getCategoryPath(category) }}
            <button @click="toggleCategory(category.id)" class="remove-btn">×</button>
          </span>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="modal-actions">
        <BaseButton variant="secondary" @click="clearSelection">清空</BaseButton>
        <BaseButton variant="primary" @click="confirmSelection">确定</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CategoryData } from '@/utils/memorin-sdk'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import CategoryTree from './CategoryTree.vue'

interface Props {
  visible: boolean
  categories: CategoryData[]
  selectedIds: string[]
}

interface Emits {
  (e: 'close'): void
  (e: 'update:selectedIds', ids: string[]): void
  (e: 'confirm', ids: string[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const selectedCategories = computed(() => {
  return props.categories.filter(cat => props.selectedIds.includes(cat.id))
})

function toggleCategory(categoryId: string) {
  const index = props.selectedIds.indexOf(categoryId)
  const newSelected = [...props.selectedIds]
  
  if (index > -1) {
    newSelected.splice(index, 1)
  } else {
    newSelected.push(categoryId)
  }
  
  emit('update:selectedIds', newSelected)
}

function clearSelection() {
  emit('update:selectedIds', [])
}

function confirmSelection() {
  emit('confirm', props.selectedIds)
  emit('close')
}

function getCategoryPath(category: CategoryData): string {
  const path = [category.name]
  let parent = props.categories.find(c => c.id === category.parentId)
  while (parent) {
    path.unshift(parent.name)
    parent = props.categories.find(c => c.id === parent.parentId)
  }
  return path.join(' > ')
}
</script>

<style scoped>
.category-filter-modal {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.category-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1rem;
}

.selected-categories {
  border-top: 1px solid #e9ecef;
  padding-top: 1rem;
}

.selected-categories h4 {
  margin: 0 0 0.5rem 0;
  color: #495057;
  font-size: 0.9rem;
}

.selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background: #007bff;
  color: white;
  border-radius: 12px;
  font-size: 0.8rem;
}

.remove-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}
</style>