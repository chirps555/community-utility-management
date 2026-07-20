<template>
  <div>
    <PageHeader title="订单查询" description="查询本人全部缴费订单" />

    <div class="page-card">
      <div class="toolbar">
        <el-input v-model="period" placeholder="账期" clearable style="width: 120px" />
        <el-select v-model="status" placeholder="状态" clearable style="width: 120px">
          <el-option label="待缴费" value="待缴费" />
          <el-option label="已缴费" value="已缴费" />
        </el-select>
        <el-button type="primary" @click="applyFilter">查询</el-button>
      </div>

      <el-table :data="list" stripe border>
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="waterFee" label="水费" width="90" />
        <el-table-column prop="electricFee" label="电费" width="90" />
        <el-table-column prop="total" label="合计" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已缴费' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" />
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
const status = ref('')
const applied = ref({ period: '', status: '' })

const list = computed(() => {
  let data = store.orders.filter((o) => o.residentId === store.currentResidentId)
  if (applied.value.period) {
    data = data.filter((o) => o.period.includes(applied.value.period))
  }
  if (applied.value.status) {
    data = data.filter((o) => o.status === applied.value.status)
  }
  return data
})

function applyFilter() {
  applied.value = { period: period.value, status: status.value }
}
</script>
