import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录', guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: '居民注册', guest: true },
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '管理首页' },
      },
      {
        path: 'residents',
        name: 'AdminResidents',
        component: () => import('@/views/admin/residents/ResidentManage.vue'),
        meta: { title: '住户信息管理' },
      },
      {
        path: 'utilities',
        name: 'AdminUtilities',
        component: () => import('@/views/admin/utilities/UtilityManage.vue'),
        meta: { title: '水电费信息管理' },
      },
      {
        path: 'meter/entry',
        name: 'AdminMeterEntry',
        component: () => import('@/views/admin/meter/MeterEntry.vue'),
        meta: { title: '抄表数据录入' },
      },
      {
        path: 'meter/usage',
        name: 'AdminUsageFlow',
        component: () => import('@/views/admin/meter/UsageFlow.vue'),
        meta: { title: '用量流水查询' },
      },
      {
        path: 'meter/records',
        name: 'AdminMeterRecords',
        component: () => import('@/views/admin/meter/MeterRecords.vue'),
        meta: { title: '抄表记录查询' },
      },
      {
        path: 'reports/time',
        name: 'ReportByTime',
        component: () => import('@/views/admin/reports/ReportByTime.vue'),
        meta: { title: '按时间段统计' },
      },
      {
        path: 'reports/building',
        name: 'ReportByBuilding',
        component: () => import('@/views/admin/reports/ReportByBuilding.vue'),
        meta: { title: '按楼栋单元统计' },
      },
      {
        path: 'reports/type',
        name: 'ReportByType',
        component: () => import('@/views/admin/reports/ReportByType.vue'),
        meta: { title: '按住户类型统计' },
      },
      {
        path: 'reports/generate',
        name: 'ReportGenerate',
        component: () => import('@/views/admin/reports/ReportGenerate.vue'),
        meta: { title: '缴费报表生成' },
      },
    ],
  },
  {
    path: '/resident',
    component: () => import('@/layouts/ResidentLayout.vue'),
    redirect: '/resident/dashboard',
    meta: { requiresAuth: true, role: 'RESIDENT' },
    children: [
      {
        path: 'dashboard',
        name: 'ResidentDashboard',
        component: () => import('@/views/resident/ResidentDashboard.vue'),
        meta: { title: '居民首页' },
      },
      {
        path: 'orders',
        name: 'ResidentOrders',
        component: () => import('@/views/resident/orders/OrderManage.vue'),
        meta: { title: '缴费订单管理' },
      },
      {
        path: 'query/resident',
        name: 'QueryResident',
        component: () => import('@/views/resident/query/ResidentInfoQuery.vue'),
        meta: { title: '住户信息查询' },
      },
      {
        path: 'query/utility',
        name: 'QueryUtility',
        component: () => import('@/views/resident/query/UtilityInfoQuery.vue'),
        meta: { title: '水电费信息查询' },
      },
      {
        path: 'query/usage',
        name: 'QueryUsage',
        component: () => import('@/views/resident/query/UsageFlowQuery.vue'),
        meta: { title: '用量流水查询' },
      },
      {
        path: 'query/meter',
        name: 'QueryMeter',
        component: () => import('@/views/resident/query/MeterRecordQuery.vue'),
        meta: { title: '抄表记录查询' },
      },
      {
        path: 'query/orders',
        name: 'QueryOrders',
        component: () => import('@/views/resident/query/OrderQuery.vue'),
        meta: { title: '订单查询' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const title = to.meta.title
  if (title) {
    document.title = `${title} - 社区居民水电费记录管理系统`
  }

  const authStore = useAuthStore()
  const requiredRole = to.matched.find((r) => r.meta.role)?.meta.role

  if (to.meta.guest && authStore.isLoggedIn) {
    return authStore.isAdmin ? '/admin' : '/resident'
  }

  if (requiredRole) {
    if (!authStore.isLoggedIn) {
      return { path: '/login', query: { role: requiredRole, redirect: to.fullPath } }
    }
    if (authStore.role !== requiredRole) {
      return authStore.isAdmin ? '/admin' : '/resident'
    }
  }
})

export default router
