<template>
  <div>
    <PageHeader title="缴费报表生成" description="生成并预览缴费汇总报表，支持导出 CSV" />

    <div class="page-card">
      <div class="toolbar">
        <el-input v-model="reportTitle" placeholder="报表标题" style="width: 280px" />
        <el-select v-model="reportPeriod" placeholder="账期" clearable style="width: 140px">
          <el-option
            v-for="p in periods"
            :key="p"
            :label="p"
            :value="p"
          />
        </el-select>
        <el-button type="primary" :loading="loading" @click="generateReport">生成报表</el-button>
        <el-button type="success" :disabled="!reportData.length" @click="exportCsv">
          导出 CSV
        </el-button>
      </div>

      <el-alert
        v-if="generated"
        :title="`报表已生成：共 ${reportData.length} 条记录`"
        type="success"
        show-icon
        style="margin-bottom: 16px"
      />

      <el-table :data="reportData" stripe border>
        <el-table-column prop="residentName" label="住户" />
        <el-table-column prop="building" label="楼栋" width="80" />
        <el-table-column prop="unit" label="单元" width="80" />
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
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const reportTitle = ref('社区居民水电费缴费报表')
const reportPeriod = ref('')
const reportData = ref([])
const generated = ref(false)
const loading = ref(false)

const periods = computed(() => [...new Set(store.orders.map((o) => o.period))])

async function generateReport() {
  loading.value = true
  try {
    await store.ensureInit()
    reportData.value = await store.fetchReportPayment(reportPeriod.value || undefined)
    generated.value = true
    ElMessage.success('缴费报表生成成功')
  } finally {
    loading.value = false
  }
}

function exportCsv() {
  const headers = ['住户', '楼栋', '单元', '账期', '水费', '电费', '合计', '状态']
  const rows = reportData.value.map((r) =>
    [r.residentName, r.building, r.unit, r.period, r.waterFee, r.electricFee, r.total, r.status].join(','),
  )
  const csv = '\uFEFF' + [headers.join(','), ...rows].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${reportTitle.value || '缴费报表'}.csv`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('CSV 已导出')
}
</script>
