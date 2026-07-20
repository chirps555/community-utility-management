<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>用户登录</h1>
        <p>{{ roleLabel }} · 社区居民水电费记录管理系统</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent>
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            size="large"
            :placeholder="loginRole === 'RESIDENT' ? '请输入手机号' : '请输入用户名'"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            size="large"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="login-footer">
        <el-button link @click="$router.push('/')">返回首页</el-button>
        <el-button
          v-if="loginRole === 'RESIDENT'"
          link
          type="primary"
          @click="$router.push('/register')"
        >
          没有账号？去注册
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/authStore'
import { useDataStore } from '@/stores/dataStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const dataStore = useDataStore()

const formRef = ref()
const loading = ref(false)

const loginRole = computed(() =>
  String(route.query.role || 'ADMIN').toUpperCase() === 'RESIDENT' ? 'RESIDENT' : 'ADMIN',
)

const roleLabel = computed(() => (loginRole.value === 'ADMIN' ? '管理员端' : '居民端'))

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.username, form.password, loginRole.value)
    dataStore.inited = false
    if (authStore.isResident && authStore.residentId) {
      dataStore.currentResidentId = authStore.residentId
    }
    ElMessage.success(`欢迎，${authStore.name}`)
    const redirect = route.query.redirect
    const defaultPath = loginRole.value === 'ADMIN' ? '/admin' : '/resident'
    await router.replace(typeof redirect === 'string' ? redirect : defaultPath)
  } catch {
    // 错误提示由 request 拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(160deg, #e8f4ff 0%, #f5f7fa 50%, #fff 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}

.login-footer {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
</style>
