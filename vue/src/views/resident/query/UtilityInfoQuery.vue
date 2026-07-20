<template>
  <div>
    <PageHeader title="水电费信息查询" description="查询当前住户的水电价及表底配置" />

    <div class="page-card">
      <el-table :data="list" stripe border>
        <el-table-column prop="waterPrice" label="水价(元/吨)" />
        <el-table-column prop="electricPrice" label="电价(元/度)" />
        <el-table-column prop="waterBase" label="水表底数" />
        <el-table-column prop="electricBase" label="电表底数" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <el-empty v-if="!list.length" description="暂无水电费配置，请联系物业" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const list = computed(() =>
  store.utilities.filter((u) => u.residentId === store.currentResidentId),
)
</script>
