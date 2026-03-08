<template>
  <div class="page-container">
    <div class="page-toolbar">
      <el-button type="primary" icon="Plus" @click="openDialog()">新建分类</el-button>
    </div>

    <el-table :data="treeData" row-key="id" v-loading="loading" :tree-props="{ children: 'children' }">
      <el-table-column label="名称" prop="name" min-width="180" />
      <el-table-column label="Slug" prop="slug" width="180" />
      <el-table-column label="文章数" prop="articleCount" width="100" align="center" />
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button text size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
          <el-button text size="small" icon="Plus" @click="openDialog(null, row.id)">子分类</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新建分类'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Slug" prop="slug">
          <el-input v-model="form.slug" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="顶级分类" clearable style="width: 100%">
            <el-option v-for="c in flatAll" :key="c.id" :label="c.label" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
import { getCategoriesApi, createCategoryApi, updateCategoryApi, deleteCategoryApi } from '../api/category'
import type { CategoryVO } from '../types'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const treeData = ref<CategoryVO[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  slug: '',
  description: '',
  parentId: undefined as number | undefined,
  sortOrder: 0,
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  slug: [{ required: true, message: '请输入 Slug', trigger: 'blur' }],
}

function flattenCategories(list: CategoryVO[], prefix = ''): { id: number; label: string }[] {
  const result: { id: number; label: string }[] = []
  for (const c of list) {
    result.push({ id: c.id, label: prefix + c.name })
    if (c.children?.length) result.push(...flattenCategories(c.children, prefix + '  '))
  }
  return result
}

const flatAll = computed(() => flattenCategories(treeData.value))

async function fetchList() {
  loading.value = true
  try {
    treeData.value = await getCategoriesApi()
  } finally {
    loading.value = false
  }
}

function openDialog(row?: CategoryVO | null, parentId?: number) {
  editingId.value = row?.id ?? null
  Object.assign(form, {
    name: row?.name ?? '',
    slug: row?.slug ?? '',
    description: row?.description ?? '',
    parentId: row?.parentId || parentId || undefined,
    sortOrder: row?.sortOrder ?? 0,
  })
  dialogVisible.value = true
}

async function handleSave() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const payload = { ...form, parentId: form.parentId ?? 0 }
    if (editingId.value) {
      await updateCategoryApi(editingId.value, payload)
    } else {
      await createCategoryApi(payload)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: CategoryVO) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteCategoryApi(row.id)
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
