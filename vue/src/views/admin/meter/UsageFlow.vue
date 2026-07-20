<template>
  <div>
    <PageHeader title="用量流水查询" description="查询各住户水、电用量及费用流水" />

    <div class="page-card">
      <div class="toolbar">
        <el-select v-model="filter.residentId" placeholder="住户" clearable style="width: 160px">
          <el-option v-for="r in store.residents" :key="r.id" :label="r.name" :value="r.id" />
        </el-select>
        <el-select v-model="filter.type" placeholder="类型" clearable style="width: 100px">
          <el-option label="水" value="水" />
          <el-option label="电" value="电" />
        </el-select>
        <el-input v-model="filter.period" placeholder="账期" clearable style="width: 120px" />
        <el-button type="primary" @click="applyFilter">查询</el-button>
      </div>

      <el-table :data="list" stripe border>
        <el-table-column prop="id" label="流水号" width="120" />
        <el-table-column prop="residentName" label="住户" width="100" />
        <el-table-column prop="type" label="类型" width="70" />
        <el-table-column prop="usage" label="用量" width="90" />
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="amount" label="金额(元)" width="100" />
        <el-table-column prop="createTime" label="生成时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const filter = ref({ residentId: '', type: '', period: '' })
const applied = ref({ residentId: '', type: '', period: '' })

const list = computed(() =>
  store.usageFlows.filter((f) => {
    if (applied.value.residentId && f.residentId !== applied.value.residentId) return false
    if (applied.value.type && f.type !== applied.value.type) return false
    if (applied.value.period && !f.period.includes(applied.value.period)) return false
    return true
  }),
)

function applyFilter() {
  applied.value = { ...filter.value }
}
</script>
