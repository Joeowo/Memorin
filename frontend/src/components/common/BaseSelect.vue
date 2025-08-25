<template>
  <select
    :value="modelValue"
    @change="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
    :disabled="disabled"
    :class="['base-select', variant]"
  >
    <option v-if="placeholder" disabled value="">{{ placeholder }}</option>
    <option
      v-for="option in options"
      :key="getOptionValue(option)"
      :value="getOptionValue(option)"
    >
      {{ getOptionLabel(option) }}
    </option>
  </select>
</template>

<script setup lang="ts">
interface Props {
  modelValue: string | number
  options: any[]
  placeholder?: string
  disabled?: boolean
  variant?: 'default' | 'primary' | 'secondary'
  valueKey?: string
  labelKey?: string
}

interface Emits {
  (e: 'update:modelValue', value: string | number): void
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  variant: 'default',
  valueKey: 'value',
  labelKey: 'label'
})

defineEmits<Emits>()

function getOptionValue(option: any): string | number {
  if (typeof option === 'string' || typeof option === 'number') {
    return option
  }
  return option[props.valueKey]
}

function getOptionLabel(option: any): string {
  if (typeof option === 'string' || typeof option === 'number') {
    return String(option)
  }
  return option[props.labelKey] || String(option)
}
</script>

<style scoped>
.base-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  font-size: 0.9rem;
  background-color: white;
  transition: all 0.2s ease;
  cursor: pointer;
}

.base-select:hover:not(:disabled) {
  border-color: #007bff;
}

.base-select:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.base-select:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
  opacity: 0.6;
}

.base-select.primary {
  border-color: #007bff;
  color: #007bff;
}

.base-select.secondary {
  border-color: #6c757d;
  color: #6c757d;
}
</style>