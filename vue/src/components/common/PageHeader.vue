<template>
  <div class="page-header">
    <div>
      <h2>{{ title }}</h2>
      <p v-if="description" class="desc">{{ description }}</p>
    </div>
    <slot name="extra" />
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.desc {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
