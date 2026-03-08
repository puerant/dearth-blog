<template>
  <div class="media-page">
    <!-- 操作栏 -->
    <div class="media-toolbar">
      <el-upload
        :action="uploadUrl"
        :headers="uploadHeaders"
        :show-file-list="false"
        accept="image/*"
        :on-success="onUploadSuccess"
        :on-error="onUploadError"
      >
        <el-button type="primary" icon="Upload">上传图片</el-button>
      </el-upload>
      <div class="toolbar-right">
        <span class="total-text">共 {{ total }} 个文件</span>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="grid"><el-icon><Grid /></el-icon></el-radio-button>
          <el-radio-button value="list"><el-icon><List /></el-icon></el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 网格视图 -->
    <div v-if="viewMode === 'grid'" v-loading="loading" class="media-grid">
      <div v-for="item in list" :key="item.id" class="media-item">
        <img :src="item.accessUrl" :alt="item.originalName" class="media-img" />
        <div class="media-overlay">
          <div class="media-name" :title="item.originalName">{{ item.originalName }}</div>
          <div class="media-actions">
            <el-tooltip content="复制链接">
              <el-button circle size="small" icon="CopyDocument" @click="copyUrl(item.accessUrl)" />
            </el-tooltip>
            <el-tooltip content="删除">
              <el-button circle size="small" icon="Delete" type="danger" @click="handleDelete(item)" />
            </el-tooltip>
          </div>
        </div>
      </div>
      <el-empty v-if="!list.length && !loading" description="暂无媒体文件" />
    </div>

    <!-- 列表视图 -->
    <el-table v-else :data="list" v-loading="loading">
      <el-table-column label="预览" width="80">
        <template #default="{ row }">
          <img :src="row.accessUrl" style="width: 48px; height: 32px; object-fit: cover; border-radius: 4px;" />
        </template>
      </el-table-column>
      <el-table-column label="文件名" prop="originalName" min-width="200" show-overflow-tooltip />
      <el-table-column label="类型" prop="mimeType" width="130" />
      <el-table-column label="大小" width="100">
        <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
      </el-table-column>
      <el-table-column label="上传时间" width="160">
        <template #default="{ row }">{{ row.createdAt?.slice(0, 16).replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text size="small" icon="CopyDocument" @click="copyUrl(row.accessUrl)">复制链接</el-button>
          <el-button text size="small" type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="page"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      background
      class="pagination"
      @current-change="fetchList"
    />
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMediaListApi, deleteMediaApi, MEDIA_UPLOAD_URL } from '../api/media'
import type { MediaVO } from '../types'

const loading = ref(false)
const list = ref<MediaVO[]>([])
const page = ref(1)
const pageSize = 24
const total = ref(0)
const viewMode = ref<'grid' | 'list'>('grid')

const uploadUrl = MEDIA_UPLOAD_URL
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
}))

async function fetchList() {
  loading.value = true
  try {
    const res = await getMediaListApi({ page: page.value, size: pageSize })
    list.value = res.records
    total.value = Number(res.total)
  } finally {
    loading.value = false
  }
}

function onUploadSuccess() {
  ElMessage.success('上传成功')
  fetchList()
}

function onUploadError() {
  ElMessage.error('上传失败，请检查文件类型或大小')
}

async function handleDelete(row: MediaVO) {
  await ElMessageBox.confirm(`确定删除「${row.originalName}」吗？`, '提示', { type: 'warning' })
  await deleteMediaApi(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(() => ElMessage.success('链接已复制'))
}

function formatSize(bytes: number) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

onMounted(fetchList)
</script>

<style scoped>
.media-page {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.media-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-text {
  font-size: 13px;
  color: #909399;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
  min-height: 200px;
}

.media-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 4/3;
  background: #f5f7fa;
  cursor: pointer;
}

.media-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.media-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.media-item:hover .media-overlay {
  opacity: 1;
}

.media-name {
  font-size: 11px;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}

.media-actions {
  display: flex;
  gap: 6px;
}

.pagination {
  justify-content: flex-end;
}
</style>
