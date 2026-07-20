<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="brand">
        <el-icon :size="22"><House /></el-icon>
        <span>居民端 · 社区居民水电费记录管理系统</span>
      </div>
      <el-button type="primary" link @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </el-header>

    <el-container>
      <el-aside width="220px" class="aside">
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/resident/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>居民首页</span>
          </el-menu-item>

          <el-sub-menu index="orders">
            <template #title>
              <el-icon><Wallet /></el-icon>
              <span>缴费订单管理</span>
            </template>
            <el-menu-item index="/resident/orders">订单生成 / 查询 / 修改 / 删除</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="query">
            <template #title>
              <el-icon><Search /></el-icon>
              <span>信息查询类</span>
            </template>
            <el-menu-item index="/resident/query/resident">住户信息查询</el-menu-item>
            <el-menu-item index="/resident/query/utility">水电费信息查询</el-menu-item>
            <el-menu-item index="/resident/query/usage">用量流水查询</el-menu-item>
            <el-menu-item index="/resident/query/meter">抄表记录查询</el-menu-item>
            <el-menu-item index="/resident/query/orders">订单查询</el-menu-item>
          </el-sub-menu>
        </el-menu>

        <div class="resident-info">
          <p>当前登录</p>
          <div class="resident-name">{{ authStore.name }}</div>
          <div v-if="store.currentResident" class="resident-detail">
            {{ store.currentResident.building }}{{ store.currentResident.unit }}
          </div>
        </div>
      </el-aside>

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

const route = useRoute()
const router = useRouter()
const store = useDataStore()
const authStore = useAuthStore()
const activeMenu = computed(() => route.path)

onMounted(() => {
  if (authStore.residentId) {
    store.currentResidentId = authStore.residentId
  }
  store.init()
})

function handleLogout() {
  authStore.logout()
  store.inited = false
  router.push('/')
}
</script>

<style scoped>
.layout {
  height: 100vh;
  flex-direction: column;
}

.header {
  background: linear-gradient(90deg, #409eff, #66b1ff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.aside {
  background: #fff;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.main {
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}

.resident-info {
  margin-top: auto;
  padding: 16px;
  border-top: 1px solid #ebeef5;
}

.resident-info p {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.resident-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.resident-detail {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}
</style>
