<template>
  <div>
    <PageHeader
      title="住户信息管理"
      description="住户添加、查询、修改、删除"
    />

    <div class="page-card">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="姓名 / 电话 / 楼栋"
          clearable
          style="width: 220px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="openDialog()">住户添加</el-button>
      </div>

      <el-table :data="filteredList" stripe border>
        <el-table-column prop="id" label="编号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="building" label="楼栋" width="80" />
        <el-table-column prop="unit" label="单元" width="80" />
        <el-table-column prop="type" label="住户类型" width="100" />
        <el-table-column prop="moveInDate" label="入住日期" />
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
      :title="isEdit ? '住户修改' : '住户添加'"
      width="480px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-input v-model="form.building" />
        </el-form-item>
        <el-form-item label="单元" prop="unit">
          <el-input v-model="form.unit" />
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
            style="width: 100%"
          />
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
const keyword = ref('')
const searchKey = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref('')
const formRef = ref()

const form = ref({
  name: '',
  phone: '',
  building: '',
  unit: '',
  type: '业主',
  moveInDate: '',
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单元', trigger: 'blur' }],
}

const filteredList = computed(() => store.residents)

async function handleSearch() {
  searchKey.value = keyword.value.trim()
  await store.fetchResidents(searchKey.value || undefined)
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
  } else {
    isEdit.value = false
    editId.value = ''
    form.value = { name: '', phone: '', building: '', unit: '', type: '业主', moveInDate: '' }
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await store.updateResident(editId.value, form.value)
      ElMessage.success('住户修改成功')
    } else {
      await store.addResident(form.value)
      ElMessage.success('住户添加成功')
    }
    dialogVisible.value = false
  } catch {
    /* 错误已由 request 拦截器提示 */
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除住户「${row.name}」？`, '住户删除', {
    type: 'warning',
  }).then(async () => {
    await store.deleteResident(row.id)
    ElMessage.success('删除成功')
  })
}
</script>
