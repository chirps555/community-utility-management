<template>
  <el-container class="layout">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon :size="22"><OfficeBuilding /></el-icon>
        <span>管理员端</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="rgba(255,255,255,0.75)"
        active-text-color="#fff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>管理首页</span>
        </el-menu-item>

        <el-sub-menu index="residents">
          <template #title>
            <el-icon><User /></el-icon>
            <span>住户信息管理</span>
          </template>
          <el-menu-item index="/admin/residents">住户添加 / 查询 / 修改 / 删除</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="utilities">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>水电费信息管理</span>
          </template>
          <el-menu-item index="/admin/utilities">水电费信息增删改查</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="meter">
          <template #title>
            <el-icon><Odometer /></el-icon>
            <span>抄表与用量管理</span>
          </template>
          <el-menu-item index="/admin/meter/entry">抄表数据录入</el-menu-item>
          <el-menu-item index="/admin/meter/usage">用量流水查询</el-menu-item>
          <el-menu-item index="/admin/meter/records">抄表记录查询</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="reports">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>缴费统计与报表</span>
          </template>
          <el-menu-item index="/admin/reports/time">按时间段统计</el-menu-item>
          <el-menu-item index="/admin/reports/building">按楼栋单元统计</el-menu-item>
          <el-menu-item index="/admin/reports/type">按住户类型统计</el-menu-item>
          <el-menu-item index="/admin/reports/generate">缴费报表生成</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="breadcrumb">{{ currentTitle }}</span>
        <div class="header-actions">
          <span class="user-name">{{ authStore.name }}</span>
          <el-button type="primary" link @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </el-header>
      <el-main v-loading="store.loading" class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDataStore } from '@/stores/dataStore'
import { useAuthStore } from '@/stores/authStore'

const store = useDataStore()
const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

onMounted(() => {
  store.init()
})

function handleLogout() {
  authStore.logout()
  store.inited = false
  router.push('/')
}

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '管理员端')
</script>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background: #001529;
  overflow-x: hidden;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  height: 56px;
}

.breadcrumb {
  font-size: 15px;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #606266;
}

.main {
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}

:deep(.el-menu) {
  border-right: none;
}
</style>
