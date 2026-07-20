<template>
  <div>
    <PageHeader title="按时间段统计" description="按账期或缴费时间统计缴费情况" />

    <div class="page-card">
      <div class="toolbar">
        <el-date-picker
          v-model="dateRange"
          type="monthrange"
          range-separator="至"
          start-placeholder="开始月份"
          end-placeholder="结束月份"
          value-format="YYYY-MM"
        />
        <el-button type="primary" :loading="loading" @click="runStats">统计</el-button>
      </div>

      <el-table :data="stats" stripe border show-summary>
        <el-table-column prop="period" label="账期" />
        <el-table-column prop="orderCount" label="订单数" />
        <el-table-column prop="paidCount" label="已缴笔数" />
        <el-table-column prop="totalAmount" label="应收合计(元)" />
        <el-table-column prop="paidAmount" label="实收合计(元)" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const dateRange = ref([])
const stats = ref([])
const loading = ref(false)

async function runStats() {
  loading.value = true
  try {
    await store.ensureInit()
    const start = dateRange.value?.[0]
    const end = dateRange.value?.[1]
    stats.value = await store.fetchReportByTime(start, end)
  } finally {
    loading.value = false
  }
}

onMounted(() => runStats())
</script>
