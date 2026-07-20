<template>
  <div>
    <PageHeader title="抄表数据录入" description="录入本期水表、电表读数，自动生成用量流水" />

    <div class="page-card" style="max-width: 560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="住户" prop="residentId">
          <el-select v-model="form.residentId" style="width: 100%" @change="onResidentChange">
            <el-option
              v-for="r in store.residents"
              :key="r.id"
              :label="`${r.name} (${r.building}${r.unit})`"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账期" prop="period">
          <el-input v-model="form.period" placeholder="如 2026-05" />
        </el-form-item>
        <el-form-item label="上期水表读数">
          <el-input-number v-model="form.prevWater" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="本期水表读数" prop="waterReading">
          <el-input-number v-model="form.waterReading" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上期电表读数">
          <el-input-number v-model="form.prevElectric" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="本期电表读数" prop="electricReading">
          <el-input-number v-model="form.electricReading" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="抄表日期" prop="readDate">
          <el-date-picker
            v-model="form.readDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交录入</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const formRef = ref()

const form = ref({
  residentId: '',
  residentName: '',
  period: '',
  prevWater: 0,
  waterReading: 0,
  prevElectric: 0,
  electricReading: 0,
  readDate: '',
  operator: '管理员',
})

const rules = {
  residentId: [{ required: true, message: '请选择住户', trigger: 'change' }],
  period: [{ required: true, message: '请输入账期', trigger: 'blur' }],
  waterReading: [{ required: true, message: '请输入水表读数', trigger: 'blur' }],
  electricReading: [{ required: true, message: '请输入电表读数', trigger: 'blur' }],
  readDate: [{ required: true, message: '请选择抄表日期', trigger: 'change' }],
}

function onResidentChange(id) {
  const r = store.residents.find((x) => x.id === id)
  form.value.residentName = r?.name || ''
  const last = [...store.meterReadings]
    .filter((m) => m.residentId === id)
    .sort((a, b) => b.period.localeCompare(a.period))[0]
  if (last) {
    form.value.prevWater = last.waterReading
    form.value.prevElectric = last.electricReading
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  try {
    await store.addMeterReading({ ...form.value })
    ElMessage.success('抄表数据录入成功，已生成用量流水')
    resetForm()
  } catch {
    /* 错误已由 request 拦截器提示 */
  }
}

function resetForm() {
  form.value = {
    residentId: '',
    residentName: '',
    period: '',
    prevWater: 0,
    waterReading: 0,
    prevElectric: 0,
    electricReading: 0,
    readDate: '',
    operator: '管理员',
  }
}
</script>
