<template>
  <div class="base-table-wrapper">
    <!-- 表格头部工具栏 -->
    <div v-if="title || $slots.toolbar" class="base-table-header">
      <div class="base-table-title-section">
        <h3 v-if="title" class="base-table-title">{{ title }}</h3>
        <p v-if="description" class="base-table-description">{{ description }}</p>
      </div>
      <div v-if="$slots.toolbar" class="base-table-toolbar">
        <slot name="toolbar" :selected-rows="selectedRows" :selected-count="selectedRows.length"></slot>
      </div>
    </div>

    <!-- 表格容器 -->
    <div class="base-table-container" :class="{ 'base-table-container--loading': loading }">
      <!-- Loading遮罩层 -->
      <div v-if="loading" class="base-table-loading-overlay">
        <LoadingSpinner 
          size="large" 
          variant="primary" 
          :text="loadingText || '加载中...'" 
        />
      </div>

      <!-- 表格主体 -->
      <div class="base-table-scroll">
        <table class="base-table">
          <!-- 表头 -->
          <thead class="base-table-head">
            <tr class="base-table-head-row">
              <!-- 选择列 -->
              <th v-if="selectable" class="base-table-head-cell base-table-head-cell--select">
                <input
                  v-if="multiple"
                  type="checkbox"
                  class="base-table-checkbox"
                  :checked="isAllSelected"
                  :indeterminate="isIndeterminate"
                  @change="handleSelectAll"
                />
              </th>

              <!-- 数据列 -->
              <th 
                v-for="column in columns" 
                :key="column.key"
                class="base-table-head-cell"
                :class="[
                  `base-table-head-cell--${column.align || 'left'}`,
                  { 
                    'base-table-head-cell--sortable': column.sortable,
                    'base-table-head-cell--sorted': sortColumn === column.key
                  }
                ]"
                :style="{ width: column.width, minWidth: column.minWidth }"
                @click="column.sortable && handleSort(column.key)"
              >
                <div class="base-table-head-content">
                  <span class="base-table-head-title">{{ column.title }}</span>
                  <div v-if="column.sortable" class="base-table-sort-icon">
                    <span 
                      class="sort-arrow sort-arrow--asc"
                      :class="{ 'sort-arrow--active': sortColumn === column.key && sortOrder === 'asc' }"
                    >▲</span>
                    <span 
                      class="sort-arrow sort-arrow--desc"
                      :class="{ 'sort-arrow--active': sortColumn === column.key && sortOrder === 'desc' }"
                    >▼</span>
                  </div>
                </div>
              </th>

              <!-- 操作列 -->
              <th v-if="actions && actions.length > 0" class="base-table-head-cell base-table-head-cell--actions">
                操作
              </th>
            </tr>
          </thead>

          <!-- 表体 -->
          <tbody class="base-table-body">
            <!-- 数据行 -->
            <tr 
              v-for="(row, rowIndex) in displayData" 
              :key="getRowKey(row, rowIndex)"
              class="base-table-body-row"
              :class="{ 
                'base-table-body-row--selected': isRowSelected(row),
                'base-table-body-row--hover': hoverable
              }"
              @click="handleRowClick(row, rowIndex)"
            >
              <!-- 选择列 -->
              <td v-if="selectable" class="base-table-body-cell base-table-body-cell--select">
                <input
                  type="checkbox"
                  class="base-table-checkbox"
                  :checked="isRowSelected(row)"
                  @change="handleRowSelect(row, $event)"
                  @click.stop
                />
              </td>

              <!-- 数据列 -->
              <td 
                v-for="column in columns" 
                :key="column.key"
                class="base-table-body-cell"
                :class="[
                  `base-table-body-cell--${column.align || 'left'}`,
                  column.cellClass
                ]"
              >
                <!-- 自定义列渲染 -->
                <slot 
                  v-if="$slots[`column-${column.key}`]"
                  :name="`column-${column.key}`" 
                  :record="row" 
                  :value="getColumnValue(row, column.key)"
                  :index="rowIndex"
                  :column="column"
                ></slot>
                
                <!-- 默认列渲染 -->
                <span v-else class="base-table-cell-content">
                  {{ formatColumnValue(getColumnValue(row, column.key), column) }}
                </span>
              </td>

              <!-- 操作列 -->
              <td v-if="actions && actions.length > 0" class="base-table-body-cell base-table-body-cell--actions">
                <div class="base-table-actions">
                  <BaseButton
                    v-for="action in actions"
                    :key="action.key"
                    :variant="action.variant || 'secondary'"
                    :size="action.size || 'small'"
                    :disabled="action.disabled && action.disabled(row)"
                    @click.stop="action.handler(row, rowIndex)"
                  >
                    {{ action.title }}
                  </BaseButton>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 空状态 -->
        <div v-if="!loading && displayData.length === 0" class="base-table-empty">
          <slot name="empty">
            <div class="base-table-empty-content">
              <div class="base-table-empty-icon">📊</div>
              <h4 class="base-table-empty-title">{{ emptyTitle || '暂无数据' }}</h4>
              <p class="base-table-empty-description">{{ emptyDescription || '当前没有可显示的数据' }}</p>
            </div>
          </slot>
        </div>
      </div>
    </div>

    <!-- 表格底部 -->
    <div v-if="showPagination || $slots.footer" class="base-table-footer">
      <div class="base-table-info">
        <span v-if="selectable && selectedRows.length > 0" class="base-table-selected-info">
          已选择 {{ selectedRows.length }} 项
        </span>
        <span v-if="showTotal" class="base-table-total-info">
          共 {{ total }} 条数据
        </span>
      </div>

      <!-- 分页器 -->
      <div v-if="showPagination" class="base-table-pagination">
        <BaseButton
          variant="secondary"
          size="small"
          :disabled="currentPage <= 1"
          @click="handlePageChange(currentPage - 1)"
        >
          上一页
        </BaseButton>
        
        <div class="base-table-page-info">
          第 {{ currentPage }} / {{ totalPages }} 页
        </div>
        
        <BaseButton
          variant="secondary"
          size="small"
          :disabled="currentPage >= totalPages"
          @click="handlePageChange(currentPage + 1)"
        >
          下一页
        </BaseButton>
      </div>

      <!-- 自定义底部 -->
      <div v-if="$slots.footer" class="base-table-custom-footer">
        <slot name="footer" :selected-rows="selectedRows" :total="total"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import BaseButton from './BaseButton.vue'
import LoadingSpinner from './LoadingSpinner.vue'

// 表格数据类型
type TableRecordValue = string | number | boolean | null | undefined | Date | object
type TableRecord = Record<string, TableRecordValue>

// 列配置类型
interface TableColumn {
  key: string
  title: string
  width?: string
  minWidth?: string
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  cellClass?: string
  formatter?: (value: TableRecordValue, record: TableRecord) => string
}

// 操作配置类型
interface TableAction {
  key: string
  title: string
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger'
  size?: 'small' | 'medium' | 'large'
  disabled?: (record: TableRecord) => boolean
  handler: (record: TableRecord, index: number) => void
}

// Props定义
interface Props {
  title?: string
  description?: string
  columns: TableColumn[]
  data: TableRecord[]
  loading?: boolean
  loadingText?: string
  selectable?: boolean
  multiple?: boolean
  selectedKeys?: string[]
  rowKey?: string | ((record: TableRecord) => string)
  hoverable?: boolean
  actions?: TableAction[]
  // 分页相关
  showPagination?: boolean
  currentPage?: number
  pageSize?: number
  total?: number
  showTotal?: boolean
  // 排序相关
  defaultSortColumn?: string
  defaultSortOrder?: 'asc' | 'desc'
  // 空状态
  emptyTitle?: string
  emptyDescription?: string
}

const props = withDefaults(defineProps<Props>(), {
  data: () => [],
  loading: false,
  selectable: false,
  multiple: true,
  selectedKeys: () => [],
  rowKey: 'id',
  hoverable: true,
  actions: () => [],
  showPagination: false,
  currentPage: 1,
  pageSize: 10,
  total: 0,
  showTotal: true
})

// 事件定义
const emit = defineEmits<{
  'row-click': [record: TableRecord, index: number]
  'row-select': [selectedKeys: string[], selectedRows: TableRecord[]]
  'sort-change': [column: string, order: 'asc' | 'desc' | null]
  'page-change': [page: number]
}>()

// 响应式数据
const selectedRows = ref<TableRecord[]>([])
const sortColumn = ref<string>(props.defaultSortColumn || '')
const sortOrder = ref<'asc' | 'desc'>('asc')

// 计算属性
const displayData = computed(() => {
  const result = [...props.data]
  
  // 排序
  if (sortColumn.value && sortOrder.value) {
    const column = props.columns.find(col => col.key === sortColumn.value)
    if (column && column.sortable) {
      result.sort((a, b) => {
        const aValue = getColumnValue(a, sortColumn.value)
        const bValue = getColumnValue(b, sortColumn.value)
        
        // 处理null/undefined值
        if (aValue === null || aValue === undefined) return 1
        if (bValue === null || bValue === undefined) return -1
        if (aValue === bValue) return 0
        
        const comparison = aValue > bValue ? 1 : -1
        return sortOrder.value === 'asc' ? comparison : -comparison
      })
    }
  }
  
  return result
})

const totalPages = computed(() => {
  return Math.ceil(props.total / props.pageSize)
})

const isAllSelected = computed(() => {
  return props.data.length > 0 && selectedRows.value.length === props.data.length
})

const isIndeterminate = computed(() => {
  return selectedRows.value.length > 0 && selectedRows.value.length < props.data.length
})

// 工具函数
const getRowKey = (record: TableRecord, index: number): string => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(record)
  }
  return (record[props.rowKey] as string) || `row-${index}`
}

const getColumnValue = (record: TableRecord, key: string): TableRecordValue => {
  return key.split('.').reduce((obj: TableRecordValue, k: string) => {
    if (obj && typeof obj === 'object' && !Array.isArray(obj) && !(obj instanceof Date)) {
      return (obj as Record<string, TableRecordValue>)[k]
    }
    return undefined
  }, record)
}

const formatColumnValue = (value: TableRecordValue, column: TableColumn): string => {
  if (column.formatter) {
    return column.formatter(value, {} as TableRecord)
  }
  
  if (value === null || value === undefined) {
    return '-'
  }
  
  return String(value)
}

const isRowSelected = (record: TableRecord): boolean => {
  const key = getRowKey(record, -1)
  return selectedRows.value.some(row => getRowKey(row, -1) === key)
}

// 事件处理
const handleRowClick = (record: TableRecord, index: number) => {
  emit('row-click', record, index)
  
  if (props.selectable && !props.multiple) {
    const mockEvent = {
      target: { checked: !isRowSelected(record) }
    } as unknown as Event
    handleRowSelect(record, mockEvent)
  }
}

const handleRowSelect = (record: TableRecord, event: Event) => {
  const target = event.target as HTMLInputElement
  const key = getRowKey(record, -1)
  
  if (target.checked) {
    if (props.multiple) {
      if (!isRowSelected(record)) {
        selectedRows.value.push(record)
      }
    } else {
      selectedRows.value = [record]
    }
  } else {
    selectedRows.value = selectedRows.value.filter(row => getRowKey(row, -1) !== key)
  }
  
  const selectedKeys = selectedRows.value.map(row => getRowKey(row, -1))
  emit('row-select', selectedKeys, [...selectedRows.value])
}

const handleSelectAll = (event: Event) => {
  const target = event.target as HTMLInputElement
  
  if (target.checked) {
    selectedRows.value = [...props.data]
  } else {
    selectedRows.value = []
  }
  
  const selectedKeys = selectedRows.value.map(row => getRowKey(row, -1))
  emit('row-select', selectedKeys, [...selectedRows.value])
}

const handleSort = (columnKey: string) => {
  if (sortColumn.value === columnKey) {
    // 切换排序顺序：asc -> desc -> null -> asc
    if (sortOrder.value === 'asc') {
      sortOrder.value = 'desc'
    } else {
      sortColumn.value = ''
      sortOrder.value = 'asc'
    }
  } else {
    sortColumn.value = columnKey
    sortOrder.value = 'asc'
  }
  
  emit('sort-change', sortColumn.value, sortColumn.value ? sortOrder.value : null)
}

const handlePageChange = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    emit('page-change', page)
  }
}

// 监听外部selectedKeys变化
watch(() => props.selectedKeys, (newKeys) => {
  selectedRows.value = props.data.filter(record => {
    const key = getRowKey(record, -1)
    return newKeys.includes(key)
  })
}, { immediate: true })

// 初始化排序
if (props.defaultSortColumn && props.defaultSortOrder) {
  sortColumn.value = props.defaultSortColumn
  sortOrder.value = props.defaultSortOrder
}
</script>

<style scoped>
.base-table-wrapper {
  width: 100%;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(102, 126, 234, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

/* 表格头部 */
.base-table-header {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.base-table-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.base-table-description {
  color: #6c757d;
  font-size: 1rem;
  margin: 0;
  line-height: 1.5;
}

.base-table-toolbar {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

/* 表格容器 */
.base-table-container {
  position: relative;
  min-height: 200px;
}

.base-table-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.base-table-scroll {
  overflow-x: auto;
  overflow-y: hidden;
}

/* 表格样式 */
.base-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

/* 表头样式 */
.base-table-head {
  background: rgba(102, 126, 234, 0.05);
}

.base-table-head-row {
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
}

.base-table-head-cell {
  padding: 1rem 1.5rem;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  white-space: nowrap;
  user-select: none;
}

.base-table-head-cell--center {
  text-align: center;
}

.base-table-head-cell--right {
  text-align: right;
}

.base-table-head-cell--sortable {
  cursor: pointer;
  transition: all 0.3s ease;
}

.base-table-head-cell--sortable:hover {
  background: rgba(102, 126, 234, 0.1);
}

.base-table-head-cell--select,
.base-table-head-cell--actions {
  width: 80px;
  text-align: center;
}

.base-table-head-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.base-table-sort-icon {
  display: flex;
  flex-direction: column;
  font-size: 0.6rem;
  line-height: 1;
}

.sort-arrow {
  color: #94a3b8;
  transition: all 0.3s ease;
}

.sort-arrow--active {
  color: #667eea;
}

/* 表体样式 */
.base-table-body-row {
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.3s ease;
}

.base-table-body-row--hover:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: scale(1.005);
}

.base-table-body-row--selected {
  background: rgba(102, 126, 234, 0.1);
}

.base-table-body-cell {
  padding: 1rem 1.5rem;
  color: #2c3e50;
  border-bottom: 1px solid rgba(102, 126, 234, 0.05);
  vertical-align: middle;
}

.base-table-body-cell--center {
  text-align: center;
}

.base-table-body-cell--right {
  text-align: right;
}

.base-table-body-cell--select,
.base-table-body-cell--actions {
  text-align: center;
}

.base-table-cell-content {
  display: block;
  word-break: break-word;
}

/* 操作按钮 */
.base-table-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

/* 复选框样式 */
.base-table-checkbox {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #667eea;
}

/* 空状态 */
.base-table-empty {
  padding: 3rem 2rem;
  text-align: center;
}

.base-table-empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.base-table-empty-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #6c757d;
  margin: 0 0 0.5rem 0;
}

.base-table-empty-description {
  color: #94a3b8;
  margin: 0;
}

/* 表格底部 */
.base-table-footer {
  padding: 1rem 2rem;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  background: rgba(102, 126, 234, 0.02);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.base-table-info {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #6c757d;
}

.base-table-selected-info {
  color: #667eea;
  font-weight: 500;
}

.base-table-pagination {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.base-table-page-info {
  font-size: 0.875rem;
  color: #6c757d;
  white-space: nowrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-table-header {
    padding: 1rem 1.5rem;
    flex-direction: column;
    align-items: stretch;
  }
  
  .base-table-title {
    font-size: 1.25rem;
  }
  
  .base-table-head-cell,
  .base-table-body-cell {
    padding: 0.75rem 1rem;
  }
  
  .base-table-footer {
    padding: 0.75rem 1.5rem;
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }
  
  .base-table-info {
    justify-content: center;
  }
  
  .base-table-pagination {
    justify-content: center;
  }
  
  .base-table-actions {
    flex-direction: column;
    gap: 0.25rem;
  }
}

@media (max-width: 480px) {
  .base-table-header {
    padding: 0.75rem 1rem;
  }
  
  .base-table-title {
    font-size: 1.125rem;
  }
  
  .base-table-head-cell,
  .base-table-body-cell {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }
  
  .base-table-footer {
    padding: 0.5rem 1rem;
  }
  
  /* 移动端隐藏部分列 */
  .base-table-head-cell:nth-child(n+4),
  .base-table-body-cell:nth-child(n+4) {
    display: none;
  }
  
  .base-table-head-cell--actions,
  .base-table-body-cell--actions {
    display: table-cell;
  }
}

/* 滚动条样式 */
.base-table-scroll::-webkit-scrollbar {
  height: 6px;
}

.base-table-scroll::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.1);
  border-radius: 3px;
}

.base-table-scroll::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

.base-table-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}
</style> 