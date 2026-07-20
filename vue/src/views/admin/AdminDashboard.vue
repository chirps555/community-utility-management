<template>
  <div>
    <PageHeader
      title="管理首页"
      description="社区居民水电费记录管理系统 — 管理员端总览"
    />

    <div class="stat-grid">
      <div class="stat-card">
        <div class="label">住户总数</div>
        <div class="value">{{ dashboard.residentCount ?? store.residents.length }}</div>
      </div>
      <div class="stat-card green">
        <div class="label">待缴费订单</div>
        <div class="value">{{ dashboard.pendingOrders ?? pendingOrders }}</div>
      </div>
      <div class="stat-card orange">
        <div class="label">抄表记录数</div>
        <div class="value">{{ dashboard.meterReadingCount ?? store.meterReadings.length }}</div>
      </div>
      <div class="stat-card">
        <div class="label">累计缴费金额（元）</div>
        <div class="value">{{ dashboard.totalPaid ?? totalPaid }}</div>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :span="12">
        <div class="page-card">
          <h3>功能模块</h3>
          <el-descriptions :column="1" border size="small" style="margin-top: 12px">
            <el-descriptions-item label="住户信息管理">添加、查询、修改、删除住户</el-descriptions-item>
            <el-descriptions-item label="水电费信息管理">维护水价、电价及计费基准</el-descriptions-item>
            <el-descriptions-item label="抄表与用量管理">录入抄表、查询用量流水与抄表记录</el-descriptions-item>
            <el-descriptions-item label="缴费统计与报表">多维度统计与报表导出</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card">
          <h3>最近订单</h3>
          <el-table :data="recentOrders" size="small" style="margin-top: 12px">
            <el-table-column prop="residentName" label="住户" />
            <el-table-column prop="period" label="账期" width="90" />
            <el-table-column prop="total" label="金额" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === '已缴费' ? 'success' : 'warning'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const dashboard = ref({})

const pendingOrders = computed(
  () => store.orders.filter((o) => o.status === '待缴费').length,
)

const totalPaid = computed(() =>
  store.orders
    .filter((o) => o.status === '已缴费')
    .reduce((sum, o) => sum + Number(o.total || 0), 0)
    .toFixed(2),
)

const recentOrders = computed(() => {
  if (dashboard.value.recentOrders?.length) return dashboard.value.recentOrders
  return store.orders.slice(0, 5)
})

onMounted(async () => {
  await store.ensureInit()
  try {
    dashboard.value = await store.fetchAdminDashboard()
  } catch {
    dashboard.value = {}
  }
})
</script>

<style scoped>
h3 {
  font-size: 15px;
  color: #303133;
}
</style>
