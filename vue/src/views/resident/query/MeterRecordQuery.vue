<template>
  <div>
    <PageHeader title="抄表记录查询" description="查询本人历史抄表记录" />

    <div class="page-card">
      <el-table :data="list" stripe border>
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="waterReading" label="水表读数" />
        <el-table-column prop="electricReading" label="电表读数" />
        <el-table-column prop="readDate" label="抄表日期" />
        <el-table-column prop="operator" label="抄表员" />
      </el-table>
      <el-empty v-if="!list.length" description="暂无抄表记录" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const list = computed(() =>
  store.meterReadings.filter((m) => m.residentId === store.currentResidentId),
)
</script>
