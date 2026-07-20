<template>
  <div>
    <PageHeader title="按楼栋单元统计" description="按楼栋、单元汇总缴费与欠费情况" />

    <div class="page-card">
      <el-button type="primary" :loading="loading" style="margin-bottom: 16px" @click="runStats">
        刷新统计
      </el-button>

      <el-table :data="stats" stripe border>
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="unit" label="单元" width="100" />
        <el-table-column prop="residentName" label="住户" />
        <el-table-column prop="orderCount" label="订单数" width="90" />
        <el-table-column prop="totalAmount" label="累计应收(元)" width="120" />
        <el-table-column prop="unpaid" label="待缴金额(元)" width="120" />
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
    stats.value = await store.fetchReportByBuilding()
  } finally {
    loading.value = false
  }
}

onMounted(() => runStats())
</script>
