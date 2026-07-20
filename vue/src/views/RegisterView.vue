<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h1>居民注册</h1>
        <p>填写手机号、密码并绑定住户信息</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" @submit.prevent>
        <el-divider content-position="left">账号信息</el-divider>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="11 位手机号" maxlength="11" clearable />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 位" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="再次输入密码"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-divider content-position="left">住户信息</el-divider>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="与物业登记一致" clearable />
        </el-form-item>

        <el-form-item label="楼栋" prop="building">
          <el-input v-model="form.building" placeholder="如：1栋" clearable />
        </el-form-item>

        <el-form-item label="单元" prop="unit">
          <el-input v-model="form.unit" placeholder="如：101" clearable />
        </el-form-item>

        <el-form-item label="住户类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="业主" value="业主" />
            <el-option label="租户" value="租户" />
          </el-select>
        </el-form-item>

        <el-form-item label="入住日期" prop="moveInDate">
          <el-date-picker
            v-model="form.moveInDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择入住日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-button type="success" size="large" class="register-btn" :loading="loading" @click="handleRegister">
          注册并登录
        </el-button>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <el-button link type="primary" @click="$router.push({ path: '/login', query: { role: 'RESIDENT' } })">
          去登录
        </el-button>
        <el-button link @click="$router.push('/')">返回首页</el-button>
      </div>

      <p class="tip">
        若管理员已录入您的住户信息，请填写相同的姓名、楼栋、单元完成绑定；否则将自动创建新住户档案。
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/authStore'
import { useDataStore } from '@/stores/dataStore'

const router = useRouter()
const authStore = useAuthStore()
const dataStore = useDataStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  name: '',
  building: '',
  unit: '',
  type: '业主',
  moveInDate: '',
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单元', trigger: 'blur' }],
  type: [{ required: true, message: '请选择住户类型', trigger: 'change' }],
  moveInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.register({
      phone: form.phone,
      password: form.password,
      name: form.name,
      building: form.building,
      unit: form.unit,
      type: form.type,
      moveInDate: form.moveInDate,
    })
    dataStore.inited = false
    dataStore.currentResidentId = authStore.residentId
    ElMessage.success(`注册成功，欢迎 ${authStore.name}`)
    await router.replace('/resident')
  } catch {
    // 错误提示由 request 拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(160deg, #e8f4ff 0%, #f5f7fa 50%, #fff 100%);
}

.register-card {
  width: 100%;
  max-width: 480px;
  padding: 36px 32px 28px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.register-header {
  text-align: center;
  margin-bottom: 8px;
}

.register-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.register-btn {
  width: 100%;
  margin-top: 8px;
}

.register-footer {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.tip {
  margin-top: 16px;
  font-size: 12px;
  line-height: 1.6;
  color: #909399;
  text-align: center;
}

:deep(.el-divider__text) {
  font-size: 13px;
  color: #909399;
}
</style>
