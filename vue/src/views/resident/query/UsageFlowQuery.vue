<template>
  <div>
    <PageHeader title="用量流水查询" description="查询本人水、电用量及费用流水" />

    <div class="page-card">
      <div class="toolbar">
        <el-input v-model="period" placeholder="账期筛选" clearable style="width: 140px" />
        <el-button type="primary" @click="applyFilter">查询</el-button>
      </div>

      <el-table :data="list" stripe border>
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="usage" label="用量" />
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="amount" label="金额(元)" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const period = ref('')
const appliedPeriod = ref('')

const list = computed(() => {
  let data = store.usageFlows.filter((f) => f.residentId === store.currentResidentId)
  if (appliedPeriod.value) {
    data = data.filter((f) => f.period.includes(appliedPeriod.value))
  }
  return data
})

function applyFilter() {
  appliedPeriod.value = period.value
}
</script>
