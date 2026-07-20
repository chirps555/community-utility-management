<template>
  <div>
    <PageHeader
      title="居民首页"
      :description="`欢迎，${store.currentResident?.name || '住户'}（${store.currentResident?.building || ''}${store.currentResident?.unit || ''}）`"
    />

    <div class="stat-grid">
      <div class="stat-card">
        <div class="label">待缴订单</div>
        <div class="value">{{ myPending }}</div>
      </div>
      <div class="stat-card orange">
        <div class="label">待缴金额(元)</div>
        <div class="value">{{ myUnpaidAmount }}</div>
      </div>
      <div class="stat-card green">
        <div class="label">已缴订单</div>
        <div class="value">{{ myPaid }}</div>
      </div>
    </div>

    <div class="page-card">
      <h3>快捷入口</h3>
      <el-space wrap style="margin-top: 12px">
        <el-button type="primary" @click="$router.push('/resident/orders')">在线缴费</el-button>
        <el-button @click="$router.push('/resident/query/usage')">用量流水查询</el-button>
        <el-button @click="$router.push('/resident/query/orders')">订单查询</el-button>
      </el-space>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()

const myOrders = computed(() =>
  store.orders.filter((o) => o.residentId === store.currentResidentId),
)

const myPending = computed(
  () => myOrders.value.filter((o) => o.status === '待缴费').length,
)

const myPaid = computed(
  () => myOrders.value.filter((o) => o.status === '已缴费').length,
)

const myUnpaidAmount = computed(() =>
  myOrders.value
    .filter((o) => o.status === '待缴费')
    .reduce((s, o) => s + Number(o.total), 0)
    .toFixed(2),
)
</script>

<style scoped>
h3 {
  font-size: 15px;
  color: #303133;
}
</style>
