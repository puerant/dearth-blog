<template>
  <div class="page-container">
    <div class="page-toolbar">
      <el-button type="primary" icon="Plus" @click="openDialog()">新建系列</el-button>
    </div>

    <el-table :data="list" v-loading="loading">
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img v-if="row.coverImage" :src="row.coverImage" style="width: 48px; height: 32px; object-fit: cover; border-radius: 4px;" />
          <el-icon v-else :size="20" style="color: #c0c4cc"><Picture /></el-icon>
        </template>
      </el-table-column>
      <el-table-column label="系列名称" prop="name" min-width="180" />
      <el-table-column label="Slug" prop="slug" width="180" />
      <el-table-column label="文章数" prop="articleCount" width="100" align="center" />
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑系列' : '新建系列'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Slug" prop="slug">
          <el-input v-model="form.slug" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="图片 URL" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getSeriesListApi, createSeriesApi, updateSeriesApi, deleteSeriesApi } from '../api/series'
import type { SeriesVO } from '../types'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const list = ref<SeriesVO[]>([])
const formRef = ref<FormInstance>()

const form = reactive({ name: '', slug: '', description: '', coverImage: '', sortOrder: 0 })
const rules = {
  name: [{ required: true, message: '请输入系列名称', trigger: 'blur' }],
  slug: [{ required: true, message: '请输入 Slug', trigger: 'blur' }],
}

async function fetchList() {
  loading.value = true
  try { list.value = await getSeriesListApi() } finally { loading.value = false }
}

function openDialog(row?: SeriesVO) {
  editingId.value = row?.id ?? null
  Object.assign(form, {
    name: row?.name ?? '', slug: row?.slug ?? '',
    description: row?.description ?? '', coverImage: row?.coverImage ?? '',
    sortOrder: row?.sortOrder ?? 0,
  })
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editingId.value) { await updateSeriesApi(editingId.value, form) } else { await createSeriesApi(form) }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally { saving.value = false }
}

async function handleDelete(row: SeriesVO) {
  await ElMessageBox.confirm(`确定删除系列「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteSeriesApi(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-container {
  background: #fff; border-radius: 10px; padding: 20px;
  display: flex; flex-direction: column; gap: 16px;
}
.page-toolbar { display: flex; justify-content: flex-end; }
</style>
