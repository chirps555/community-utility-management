<template>
  <div>
    <PageHeader title="抄表记录查询" description="查询历史抄表数据记录" />

    <div class="page-card">
      <div class="toolbar">
        <el-select v-model="filterResidentId" placeholder="住户" clearable style="width: 160px">
          <el-option v-for="r in store.residents" :key="r.id" :label="r.name" :value="r.id" />
        </el-select>
        <el-input v-model="filterPeriod" placeholder="账期" clearable style="width: 120px" />
        <el-button type="primary" @click="applyFilter">查询</el-button>
      </div>

      <el-table :data="list" stripe border>
        <el-table-column prop="id" label="记录号" width="110" />
        <el-table-column prop="residentName" label="住户" width="100" />
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="waterReading" label="水表读数" width="100" />
        <el-table-column prop="electricReading" label="电表读数" width="100" />
        <el-table-column prop="readDate" label="抄表日期" width="120" />
        <el-table-column prop="operator" label="操作员" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const filterResidentId = ref('')
const filterPeriod = ref('')
const appliedResident = ref('')
const appliedPeriod = ref('')

const list = computed(() =>
  store.meterReadings.filter((m) => {
    if (appliedResident.value && m.residentId !== appliedResident.value) return false
    if (appliedPeriod.value && !m.period.includes(appliedPeriod.value)) return false
    return true
  }),
)

function applyFilter() {
  appliedResident.value = filterResidentId.value
  appliedPeriod.value = filterPeriod.value
}
</script>
