<template>
  <div class="category-tree">
    <div
      v-for="category in rootCategories"
      :key="category.id"
      class="tree-item"
    >
      <CategoryTreeItem
        :category="category"
        :level="0"
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
import CategoryTreeItem from './CategoryTreeItem.vue'

interface Props {
  categories: CategoryData[]
  selectedIds: string[]
}

interface Emits {
  (e: 'toggle', categoryId: string): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

const rootCategories = computed(() => {
  return props.categories
    .filter(cat => !cat.parentId)
    .sort((a, b) => a.name.localeCompare(b.name))
})
</script>

<style scoped>
.category-tree {
  padding: 0.5rem 0;
}

.tree-item {
  margin-bottom: 0.25rem;
}
</style>