<template>
  <div class="page-container">
    <div class="page-toolbar">
      <el-button type="primary" icon="Plus" @click="openDialog()">新建标签</el-button>
    </div>

    <el-table :data="list" v-loading="loading">
      <el-table-column label="名称" prop="name" min-width="150" />
      <el-table-column label="Slug" prop="slug" width="200" />
      <el-table-column label="文章数" prop="articleCount" width="100" align="center" />
      <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑标签' : '新建标签'" width="440px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Slug" prop="slug">
          <el-input v-model="form.slug" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { getTagsApi, createTagApi, updateTagApi, deleteTagApi } from '../api/tag'
import type { TagVO } from '../types'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const list = ref<TagVO[]>([])
const formRef = ref<FormInstance>()

const form = reactive({ name: '', slug: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  slug: [{ required: true, message: '请输入 Slug', trigger: 'blur' }],
}

async function fetchList() {
  loading.value = true
  try {
    list.value = await getTagsApi()
  } finally {
    loading.value = false
  }
}

function openDialog(row?: TagVO) {
  editingId.value = row?.id ?? null
  Object.assign(form, { name: row?.name ?? '', slug: row?.slug ?? '', description: row?.description ?? '' })
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editingId.value) {
      await updateTagApi(editingId.value, form)
    } else {
      await createTagApi(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: TagVO) {
  await ElMessageBox.confirm(`确定删除标签「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteTagApi(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.page-toolbar {
  display: flex;
  justify-content: flex-end;
}
</style>
