<template>
  <div>
    <PageHeader
      title="缴费订单管理"
      description="按阶梯算法计算账单，模拟在线缴费全流程"
    />

    <div class="page-card bill-panel">
      <h3>账单计算（阶梯计价算法）</h3>
      <div class="toolbar">
        <el-input v-model="billPeriod" placeholder="账期，如 2026-04" style="width: 160px" clearable />
        <el-button type="primary" :loading="calcLoading" @click="handleCalculate">计算账单</el-button>
        <el-button type="success" :disabled="!billResult" :loading="genLoading" @click="handleGenerate">
          生成待缴订单
        </el-button>
        
      </div>
      <p v-if="billResult" class="algo-tip">{{ billResult.algorithm }}</p>
      <el-descriptions v-if="billResult" :column="3" border size="small" class="bill-summary">
        <el-descriptions-item label="用水量">{{ billResult.waterUsage }} 吨</el-descriptions-item>
        <el-descriptions-item label="用电量">{{ billResult.electricUsage }} 度</el-descriptions-item>
        <el-descriptions-item label="应缴合计">
          <span class="total-amount">¥ {{ billResult.total }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="水费">¥ {{ billResult.waterFee }}</el-descriptions-item>
        <el-descriptions-item label="电费">¥ {{ billResult.electricFee }}</el-descriptions-item>
        <el-descriptions-item label="账期">{{ billResult.period }}</el-descriptions-item>
      </el-descriptions>
      <el-row v-if="billResult" :gutter="16" class="tier-row">
        <el-col :span="12">
          <h4>水费阶梯明细</h4>
          <el-table :data="billResult.waterTiers || []" size="small" border stripe>
            <el-table-column prop="tier" label="档位" />
            <el-table-column prop="usage" label="用量(吨)" width="90" />
            <el-table-column prop="unitPrice" label="单价(元)" width="90" />
            <el-table-column prop="fee" label="小计(元)" width="90" />
          </el-table>
        </el-col>
        <el-col :span="12">
          <h4>电费阶梯明细</h4>
          <el-table :data="billResult.electricTiers || []" size="small" border stripe>
            <el-table-column prop="tier" label="档位" />
            <el-table-column prop="usage" label="用量(度)" width="90" />
            <el-table-column prop="unitPrice" label="单价(元)" width="90" />
            <el-table-column prop="fee" label="小计(元)" width="90" />
          </el-table>
        </el-col>
      </el-row>
    </div>

    <div class="page-card">
      <div class="toolbar">
        <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
          <el-option label="待缴费" value="待缴费" />
          <el-option label="已缴费" value="已缴费" />
        </el-select>
        <el-button type="primary" @click="applyFilter">订单查询</el-button>
      </div>

      <el-table :data="list" stripe border>
        <el-table-column prop="id" label="订单号" min-width="140" />
        <el-table-column prop="period" label="账期" width="100" />
        <el-table-column prop="waterFee" label="水费" width="90" />
        <el-table-column prop="electricFee" label="电费" width="90" />
        <el-table-column prop="total" label="合计" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已缴费' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '待缴费'"
              type="success"
              link
              @click="openPay(row)"
            >
              缴费
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <PaymentSimulator v-model="payVisible" :order="payOrder" @success="onPaySuccess" />

    
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'
import PaymentSimulator from '@/components/payment/PaymentSimulator.vue'

const store = useDataStore()
const filterStatus = ref('')
const appliedStatus = ref('')
const billPeriod = ref('')
const billResult = ref(null)
const calcLoading = ref(false)
const genLoading = ref(false)
const payVisible = ref(false)
const payOrder = ref(null)

const list = computed(() => {
  let data = store.orders.filter((o) => o.residentId === store.currentResidentId)
  if (appliedStatus.value) data = data.filter((o) => o.status === appliedStatus.value)
  return data
})

onMounted(() => {
  const periods = store.usageFlows
    .filter((f) => f.residentId === store.currentResidentId && f.period)
    .map((f) => f.period)
  billPeriod.value = periods[0] || ''
})

function applyFilter() {
  appliedStatus.value = filterStatus.value
}

async function handleCalculate() {
  if (!billPeriod.value.trim()) {
    ElMessage.warning('请输入账期')
    return
  }
  calcLoading.value = true
  try {
    billResult.value = await store.calculateBill(billPeriod.value.trim())
  } catch {
    billResult.value = null
  } finally {
    calcLoading.value = false
  }
}

async function handleGenerate() {
  if (!billResult.value) return
  genLoading.value = true
  try {
    await store.generateBill(billResult.value.period)
    ElMessage.success('待缴订单已生成')
  } finally {
    genLoading.value = false
  }
}

function openPay(row) {
  payOrder.value = row
  payVisible.value = true
}

function onPaySuccess() {
  ElMessage.success('缴费成功')
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该订单？', '订单删除', { type: 'warning' }).then(async () => {
    await store.deleteOrder(row.id)
    ElMessage.success('删除成功')
  })
}
</script>

<style scoped>
.bill-panel h3,
.bill-panel h4 {
  font-size: 15px;
  color: #303133;
  margin-bottom: 12px;
}

.algo-tip {
  margin: 12px 0;
  font-size: 13px;
  color: #909399;
}

.bill-summary {
  margin-bottom: 16px;
}

.total-amount {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.tier-row {
  margin-top: 8px;
}

.demo-tip {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.steps-log {
  margin-top: 16px;
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
