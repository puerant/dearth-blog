<template>
  <div class="page-container">
    <div class="page-toolbar">
      <el-button type="primary" icon="Plus" @click="openDialog()">新建项目</el-button>
    </div>

    <el-table :data="list" v-loading="loading">
      <el-table-column label="项目编号" prop="projectNo" width="120" />
      <el-table-column label="项目名称" prop="name" min-width="160" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="技术栈" min-width="200">
        <template #default="{ row }">
          <el-tag v-for="t in row.techStack?.slice(0,4)" :key="t" size="small" type="info" style="margin-right:4px">{{ t }}</el-tag>
          <span v-if="row.techStack?.length > 4" style="font-size:12px;color:#909399">+{{ row.techStack.length - 4 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑项目' : '新建项目'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目编号" prop="projectNo"><el-input v-model="form.projectNo" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name"><el-input v-model="form.name" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目简称" prop="shortName"><el-input v-model="form.shortName" /></el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="Active" value="Active" />
                <el-option label="Archive" value="Archive" />
                <el-option label="WIP" value="WIP" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技术栈">
          <el-select v-model="form.techStack" multiple allow-create filterable placeholder="输入后回车添加" style="width:100%" />
        </el-form-item>
        <el-form-item label="项目亮点">
          <el-select v-model="form.highlights" multiple allow-create filterable placeholder="输入后回车添加" style="width:100%" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="起始年份"><el-input-number v-model="form.startYear" :min="2000" :max="2100" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主题色 Hue"><el-slider v-model="form.hue" :min="0" :max="360" show-input /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="GitHub 链接"><el-input v-model="form.githubUrl" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
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
import { getProjectsApi, createProjectApi, updateProjectApi, deleteProjectApi } from '../api/project'
import type { ProjectVO } from '../types'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const list = ref<ProjectVO[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  projectNo: '', name: '', shortName: '', description: '',
  techStack: [] as string[], highlights: [] as string[],
  startYear: new Date().getFullYear(), status: 'WIP', hue: 200,
  githubUrl: '', sortOrder: 0,
})

const rules = {
  projectNo: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  shortName: [{ required: true, message: '请输入项目简称', trigger: 'blur' }],
}

const statusType = (s: string) => ({ Active: 'success', Archive: 'info', WIP: 'warning' }[s] as any || 'info')

async function fetchList() {
  loading.value = true
  try { list.value = await getProjectsApi() } finally { loading.value = false }
}

function openDialog(row?: ProjectVO) {
  editingId.value = row?.id ?? null
  Object.assign(form, {
    projectNo: row?.projectNo ?? '', name: row?.name ?? '', shortName: row?.shortName ?? '',
    description: row?.description ?? '', techStack: [...(row?.techStack ?? [])],
    highlights: [...(row?.highlights ?? [])], startYear: row?.startYear ? Number(row.startYear) : new Date().getFullYear(),
    status: row?.status ?? 'WIP', hue: row?.hue ?? 200, githubUrl: row?.githubUrl ?? '', sortOrder: row?.sortOrder ?? 0,
  })
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editingId.value) { await updateProjectApi(editingId.value, { ...form, startYear: String(form.startYear) }) } else { await createProjectApi({ ...form, startYear: String(form.startYear) }) }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally { saving.value = false }
}

async function handleDelete(row: ProjectVO) {
  await ElMessageBox.confirm(`确定删除项目「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteProjectApi(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-container { background: #fff; border-radius: 10px; padding: 20px; display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: flex-end; }
</style>
