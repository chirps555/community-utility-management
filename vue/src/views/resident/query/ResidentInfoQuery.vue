<template>
  <div>
    <PageHeader title="住户信息查询" description="查询当前登录住户的基本信息" />

    <div class="page-card" v-if="info">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ info.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ info.name }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ info.phone }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ info.building }}</el-descriptions-item>
        <el-descriptions-item label="单元">{{ info.unit }}</el-descriptions-item>
        <el-descriptions-item label="住户类型">{{ info.type }}</el-descriptions-item>
        <el-descriptions-item label="入住日期" :span="2">{{ info.moveInDate }}</el-descriptions-item>
      </el-descriptions>
    </div>
    <el-empty v-else description="未找到住户信息" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const info = computed(() => store.currentResident)
</script>
