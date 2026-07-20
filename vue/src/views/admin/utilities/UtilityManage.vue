<template>
  <div>
    <PageHeader
      title="水电费信息管理"
      description="水电费信息添加、查询、修改、删除"
    />

    <div class="page-card">
      <div class="toolbar">
        <el-select
          v-model="filterResidentId"
          placeholder="按住户筛选"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="r in store.residents"
            :key="r.id"
            :label="r.name"
            :value="r.id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="openDialog()">水电费信息添加</el-button>
      </div>

      <el-table :data="filteredList" stripe border>
        <el-table-column prop="id" label="编号" width="100" />
        <el-table-column prop="residentName" label="住户" width="100" />
        <el-table-column prop="waterPrice" label="水价(元/吨)" width="110" />
        <el-table-column prop="electricPrice" label="电价(元/度)" width="110" />
        <el-table-column prop="waterBase" label="水表底数" width="90" />
        <el-table-column prop="electricBase" label="电表底数" width="90" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">修改</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '水电费信息修改' : '水电费信息添加'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="住户" prop="residentId">
          <el-select
            v-model="form.residentId"
            style="width: 100%"
            :disabled="isEdit"
            @change="onResidentChange"
          >
            <el-option
              v-for="r in store.residents"
              :key="r.id"
              :label="`${r.name} (${r.building}${r.unit})`"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="水价" prop="waterPrice">
          <el-input-number v-model="form.waterPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="电价" prop="electricPrice">
          <el-input-number v-model="form.electricPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="水表底数">
          <el-input-number v-model="form.waterBase" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="电表底数">
          <el-input-number v-model="form.electricBase" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDataStore } from '@/stores/dataStore'
import PageHeader from '@/components/common/PageHeader.vue'

const store = useDataStore()
const filterResidentId = ref('')
const appliedFilter = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref('')
const formRef = ref()

const form = ref({
  residentId: '',
  residentName: '',
  waterPrice: 3.5,
  electricPrice: 0.62,
  waterBase: 0,
  electricBase: 0,
  remark: '',
})

const rules = {
  residentId: [{ required: true, message: '请选择住户', trigger: 'change' }],
  waterPrice: [{ required: true, message: '请输入水价', trigger: 'blur' }],
  electricPrice: [{ required: true, message: '请输入电价', trigger: 'blur' }],
}

const filteredList = computed(() => store.utilities)

async function handleSearch() {
  appliedFilter.value = filterResidentId.value
  await store.fetchUtilities(appliedFilter.value || undefined)
}

function onResidentChange(id) {
  const r = store.residents.find((x) => x.id === id)
  form.value.residentName = r?.name || ''
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
  } else {
    isEdit.value = false
    editId.value = ''
    form.value = {
      residentId: '',
      residentName: '',
      waterPrice: 3.5,
      electricPrice: 0.62,
      waterBase: 0,
      electricBase: 0,
      remark: '',
    }
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await store.updateUtility(editId.value, form.value)
      ElMessage.success('水电费信息修改成功')
    } else {
      await store.addUtility(form.value)
      ElMessage.success('水电费信息添加成功')
    }
    dialogVisible.value = false
  } catch {
    /* 错误已由 request 拦截器提示 */
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除「${row.residentName}」的水电费配置？`, '删除', {
    type: 'warning',
  }).then(async () => {
    await store.deleteUtility(row.id)
    ElMessage.success('删除成功')
  })
}
</script>
