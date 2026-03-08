<template>
  <div class="page-container">
    <!-- 操作栏 -->
    <div class="page-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="filters.title"
          placeholder="搜索文章标题"
          clearable
          style="width: 220px"
          prefix-icon="Search"
          @change="fetchList"
        />
        <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 130px" @change="fetchList">
          <el-option label="草稿" :value="1" />
          <el-option label="已发布" :value="2" />
          <el-option label="已归档" :value="3" />
        </el-select>
        <el-select v-model="filters.categoryId" placeholder="全部分类" clearable style="width: 150px" @change="fetchList">
          <el-option
            v-for="c in flatCategories"
            :key="c.id"
            :label="c.label"
            :value="c.id"
          />
        </el-select>
      </div>
      <el-button type="primary" icon="Plus" @click="router.push('/articles/new')">
        写文章
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="articles" v-loading="loading" class="article-table">
      <el-table-column label="标题" min-width="280">
        <template #default="{ row }">
          <div class="article-title">{{ row.title }}</div>
          <div class="article-meta">
            <span v-if="row.categoryName" class="meta-tag">{{ row.categoryName }}</span>
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              type="info"
              style="margin-right: 4px"
            >{{ tag.name }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="90" align="center" />
      <el-table-column label="发布时间" width="160">
        <template #default="{ row }">
          <span>{{ row.publishTime ? row.publishTime.replace('T', ' ').slice(0, 16) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text size="small" icon="Edit" @click="router.push(`/articles/${row.id}/edit`)">编辑</el-button>
          <el-button
            v-if="row.status === 1"
            text
            size="small"
            type="success"
            @click="changeStatus(row, 2)"
          >发布</el-button>
          <el-button
            v-if="row.status === 2"
            text
            size="small"
            @click="changeStatus(row, 1)"
          >撤稿</el-button>
          <el-button
            v-if="row.status === 2"
            text
            size="small"
            type="warning"
            @click="changeStatus(row, 3)"
          >归档</el-button>
          <el-button text size="small" type="danger" @click="deleteArticle(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      background
      class="pagination"
      @change="fetchList"
    />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getArticlesApi, deleteArticleApi, updateArticleStatusApi } from '../../api/article'
import { getCategoriesApi } from '../../api/category'
import type { ArticleListVO, CategoryVO } from '../../types'

const router = useRouter()
const loading = ref(false)
const articles = ref<ArticleListVO[]>([])

const filters = reactive({ title: '', status: null as number | null, categoryId: null as number | null })
const pagination = reactive({ page: 1, size: 15, total: 0 })

// 扁平化分类（用于下拉选择）
const flatCategories = ref<{ id: number; label: string }[]>([])

function flattenCategories(list: CategoryVO[], prefix = '') {
  const result: { id: number; label: string }[] = []
  for (const c of list) {
    result.push({ id: c.id, label: prefix + c.name })
    if (c.children?.length) {
      result.push(...flattenCategories(c.children, prefix + '  '))
    }
  }
  return result
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getArticlesApi({
      page: pagination.page,
      size: pagination.size,
      title: filters.title || undefined,
      status: filters.status,
      categoryId: filters.categoryId,
    })
    articles.value = res.records
    pagination.total = Number(res.total)
  } finally {
    loading.value = false
  }
}

async function changeStatus(row: ArticleListVO, status: number) {
  await updateArticleStatusApi(row.id, status)
  ElMessage.success('状态已更新')
  fetchList()
}

async function deleteArticle(row: ArticleListVO) {
  await ElMessageBox.confirm(`确定删除《${row.title}》吗？`, '提示', { type: 'warning' })
  await deleteArticleApi(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

const statusLabel = (s: number) => ({ 1: '草稿', 2: '已发布', 3: '已归档' }[s] || '-')
const statusType = (s: number) =>
  ({ 1: 'info', 2: 'success', 3: 'warning' }[s] as any || 'info')

onMounted(async () => {
  const categories = await getCategoriesApi().catch(() => [])
  flatCategories.value = flattenCategories(categories)
  fetchList()
})
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
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.article-table {
  width: 100%;
}

.article-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.article-meta {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.meta-tag {
  font-size: 12px;
  color: #909399;
}

.pagination {
  justify-content: flex-end;
}
</style>
