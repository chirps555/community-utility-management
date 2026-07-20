<template>
  <el-dialog
    v-model="visible"
    :title="stepTitle"
    width="520px"
    destroy-on-close
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <!-- 步骤1：确认订单 -->
    <div v-if="step === 0" class="step-body">
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="订单号">{{ order?.id }}</el-descriptions-item>
        <el-descriptions-item label="账期">{{ order?.period }}</el-descriptions-item>
        <el-descriptions-item label="水费">¥ {{ order?.waterFee }}</el-descriptions-item>
        <el-descriptions-item label="电费">¥ {{ order?.electricFee }}</el-descriptions-item>
        <el-descriptions-item label="应缴合计">
          <span class="amount">¥ {{ order?.total }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 步骤2：选择支付方式 -->
    <div v-else-if="step === 1" class="step-body">
      <p class="step-tip">请选择支付方式</p>
      <div class="pay-methods">
        <div
          v-for="m in payMethods"
          :key="m.value"
          :class="['pay-method', { active: payMethod === m.value }]"
          @click="payMethod = m.value"
        >
          <span class="icon">{{ m.icon }}</span>
          <span>{{ m.label }}</span>
        </div>
      </div>
    </div>

    <!-- 步骤3：输入密码 -->
    <div v-else-if="step === 2" class="step-body">
      <p class="step-tip">支付密码</p>
      <el-input
        v-model="payPassword"
        type="password"
        maxlength="6"
        show-password
        placeholder="请输入 6 位支付密码"
        @keyup.enter="nextStep"
      />
    </div>

    <!-- 步骤4：处理中 -->
    <div v-else-if="step === 3" class="step-body processing">
      <el-progress :percentage="progress" :stroke-width="10" />
      <p class="process-text">{{ processText }}</p>
    </div>

    <!-- 步骤5：成功 -->
    <div v-else-if="step === 4" class="step-body success">
      <el-result icon="success" title="缴费成功" :sub-title="`已支付 ¥${payResult?.amount}`">
        <template #extra>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="交易单号">{{ payResult?.transactionNo }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ payResult?.payMethodLabel }}</el-descriptions-item>
            <el-descriptions-item label="校验码">{{ payResult?.verifyCode }}</el-descriptions-item>
            <el-descriptions-item label="缴费时间">{{ payResult?.payTime }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-result>
    </div>

    <template #footer>
      <template v-if="step < 3">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="nextStep">{{ step === 2 ? '确认支付' : '下一步' }}</el-button>
      </template>
      <el-button v-else-if="step === 4" type="primary" @click="visible = false">完成</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useDataStore } from '@/stores/dataStore'

const props = defineProps({
  modelValue: Boolean,
  order: Object,
})

const emit = defineEmits(['update:modelValue', 'success'])

const store = useDataStore()
const step = ref(0)
const payMethod = ref('wechat')
const payPassword = ref('')
const progress = ref(0)
const processText = ref('')
const payResult = ref(null)

const payMethods = [
  { value: 'wechat', label: '微信支付', icon: '💬' },
  { value: 'alipay', label: '支付宝', icon: '💳' },
  { value: 'bank', label: '银行卡', icon: '🏦' },
]

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const stepTitle = computed(() => {
  const titles = ['确认账单', '选择支付方式', '输入支付密码', '支付处理中', '缴费成功']
  return titles[step.value] || '缴费'
})

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      step.value = 0
      payMethod.value = 'wechat'
      payPassword.value = ''
      payResult.value = null
      progress.value = 0
    }
  },
)

function nextStep() {
  if (step.value === 0) {
    step.value = 1
    return
  }
  if (step.value === 1) {
    if (!payMethod.value) {
      ElMessage.warning('请选择支付方式')
      return
    }
    step.value = 2
    return
  }
  if (step.value === 2) {
    if (!/^\d{6}$/.test(payPassword.value)) {
      ElMessage.warning('请输入 6 位数字支付密码')
      return
    }
    runPayment()
  }
}

async function runPayment() {
  step.value = 3
  progress.value = 10
  processText.value = '正在连接支付网关...'

  const timer = setInterval(() => {
    if (progress.value < 90) progress.value += 15
  }, 400)

  try {
    await delay(800)
    processText.value = '正在验证订单与金额...'
    progress.value = 40
    await delay(600)
    processText.value = '正在执行扣款...'
    progress.value = 70

    const result = await store.payOrder(props.order.id, {
      payMethod: payMethod.value,
      payPassword: payPassword.value,
    })

    progress.value = 100
    processText.value = '支付完成'
    payResult.value = result
    await delay(400)
    step.value = 4
    emit('success', result)
  } catch {
    step.value = 2
    progress.value = 0
  } finally {
    clearInterval(timer)
  }
}

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

function handleClosed() {
  step.value = 0
  payPassword.value = ''
}
</script>

<style scoped>
.step-body {
  min-height: 200px;
}

.step-tip {
  margin-bottom: 16px;
  color: #606266;
  font-size: 14px;
}

.amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.pay-methods {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.pay-method {
  flex: 1;
  min-width: 120px;
  padding: 16px;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.pay-method.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.pay-method .icon {
  display: block;
  font-size: 28px;
  margin-bottom: 8px;
}

.processing {
  padding: 40px 20px;
  text-align: center;
}

.process-text {
  margin-top: 16px;
  color: #606266;
}

.steps-log {
  margin-top: 16px;
  text-align: left;
  width: 100%;
}

.steps-log h4 {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.steps-log p {
  font-size: 12px;
  color: #606266;
  line-height: 1.8;
  margin: 0;
}
</style>
