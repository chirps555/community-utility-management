<template>
  <div>
    <PageHeader title="按住户类型统计" description="按业主、租户等类型汇总缴费数据" />

    <div class="page-card">
      <el-button type="primary" :loading="loading" style="margin-bottom: 16px" @click="runStats">
        刷新统计
      </el-button>

      <el-table :data="stats" stripe border>
        <el-table-column prop="type" label="住户类型" />
        <el-table-column prop="residentCount" label="住户数量" />
        <el-table-column prop="orderCount" label="订单总数" />
        <el-table-column prop="totalAmount" label="累计金额(元)" />
        <el-table-column prop="paidRate" label="缴费率" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const stats = ref([])
const loading = ref(false)

async function runStats() {
  loading.value = true
  try {
    await store.ensureInit()
    stats.value = await store.fetchReportByType()
  } finally {
    loading.value = false
  }
}

onMounted(() => runStats())
</script>
