<template>
  <div class="editor-page">
    <!-- 顶部操作栏 -->
    <div class="editor-topbar">
      <el-input
        v-model="form.title"
        placeholder="输入文章标题..."
        class="title-input"
        :bordered="false"
      />
      <div class="topbar-actions">
        <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="publish" :loading="saving">发布</el-button>
      </div>
    </div>

    <!-- 主体：编辑器 + 侧边栏 -->
    <div class="editor-body">
      <!-- 编辑区 -->
      <div class="editor-area">
        <div class="mode-switch">
          <el-radio-group v-model="form.contentType" size="small">
            <el-radio-button :value="1">Markdown</el-radio-button>
            <el-radio-button :value="2">富文本</el-radio-button>
          </el-radio-group>
        </div>

        <!-- Markdown 编辑器 -->
        <MarkdownEditor
          v-if="form.contentType === 1"
          v-model="form.content"
          class="editor-instance"
        />

        <!-- 富文本编辑器 -->
        <RichTextEditor
          v-else
          v-model="form.content"
          class="editor-instance"
        />
      </div>

      <!-- 侧边栏 -->
      <div class="editor-sidebar">
        <!-- 摘要 -->
        <div class="sidebar-section">
          <div class="section-title">摘要</div>
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="文章摘要（选填）"
          />
        </div>

        <!-- 封面图 -->
        <div class="sidebar-section">
          <div class="section-title">封面图</div>
          <div v-if="form.coverImage" class="cover-preview">
            <img :src="form.coverImage" alt="封面" />
            <el-button text icon="Delete" @click="form.coverImage = ''" class="cover-remove">移除</el-button>
          </div>
          <el-input v-model="form.coverImage" placeholder="图片 URL" size="small" />
        </div>

        <!-- 分类 -->
        <div class="sidebar-section">
          <div class="section-title">分类</div>
          <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width: 100%">
            <el-option
              v-for="c in flatCategories"
              :key="c.id"
              :label="c.label"
              :value="c.id"
            />
          </el-select>
        </div>

        <!-- 标签 -->
        <div class="sidebar-section">
          <div class="section-title">标签</div>
          <el-select
            v-model="form.tagIds"
            multiple
            placeholder="选择标签"
            style="width: 100%"
            collapse-tags
            collapse-tags-tooltip
          >
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </div>

        <!-- 系列 -->
        <div class="sidebar-section">
          <div class="section-title">系列</div>
          <el-select v-model="form.seriesId" placeholder="选择系列" clearable style="width: 100%">
            <el-option v-for="s in seriesList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
          <el-input-number
            v-if="form.seriesId"
            v-model="form.seriesSort"
            :min="1"
            placeholder="系列顺序"
            style="width: 100%; margin-top: 8px"
            size="small"
          />
        </div>

        <!-- 其他设置 -->
        <div class="sidebar-section">
          <div class="section-title">其他</div>
          <div class="setting-row">
            <span>精选文章</span>
            <el-switch v-model="form.isFeatured" />
          </div>
          <div class="setting-row" style="margin-top: 8px">
            <span>定时发布</span>
            <el-date-picker
              v-model="form.publishTime"
              type="datetime"
              placeholder="选择时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              style="width: 180px"
              size="small"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 草稿恢复提示 -->
    <el-alert
      v-if="showDraftAlert"
      title="检测到本地草稿，是否恢复？"
      type="info"
      show-icon
      :closable="false"
      class="draft-alert"
    >
      <template #default>
        <el-button size="small" type="primary" @click="restoreDraft">恢复草稿</el-button>
        <el-button size="small" @click="discardDraft">丢弃</el-button>
      </template>
    </el-alert>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getArticleApi, createArticleApi, updateArticleApi } from '../../api/article'
import { getCategoriesApi } from '../../api/category'
import { getTagsApi } from '../../api/tag'
import { getSeriesListApi } from '../../api/series'
import type { CategoryVO, TagVO, SeriesVO } from '../../types'
import MarkdownEditor from '../../components/editor/MarkdownEditor.vue'
import RichTextEditor from '../../components/editor/RichTextEditor.vue'

const route = useRoute()
const router = useRouter()
const saving = ref(false)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  contentType: 1 as 1 | 2,
  coverImage: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  seriesId: undefined as number | undefined,
  seriesSort: 1,
  isFeatured: false,
  status: 1,
  publishTime: undefined as string | undefined,
})

const articleId = computed(() => {
  const id = route.params.id
  return id ? Number(id) : null
})

const draftKey = computed(() => `draft_${articleId.value ?? 'new'}`)
const showDraftAlert = ref(false)

// 扁平分类、标签、系列
const flatCategories = ref<{ id: number; label: string }[]>([])
const tags = ref<TagVO[]>([])
const seriesList = ref<SeriesVO[]>([])

function flattenCategories(list: CategoryVO[], prefix = '') {
  const result: { id: number; label: string }[] = []
  for (const c of list) {
    result.push({ id: c.id, label: prefix + c.name })
    if (c.children?.length) result.push(...flattenCategories(c.children, prefix + '  '))
  }
  return result
}

// 自动保存草稿定时器
let autoSaveTimer: ReturnType<typeof setInterval> | null = null

function startAutoSave() {
  autoSaveTimer = setInterval(() => {
    localStorage.setItem(draftKey.value, JSON.stringify(form))
  }, 30000)
}

function restoreDraft() {
  const raw = localStorage.getItem(draftKey.value)
  if (raw) Object.assign(form, JSON.parse(raw))
  showDraftAlert.value = false
}

function discardDraft() {
  localStorage.removeItem(draftKey.value)
  showDraftAlert.value = false
}

async function saveDraft() {
  if (!form.title.trim()) { ElMessage.warning('请填写标题'); return }
  await save(1)
}

async function publish() {
  if (!form.title.trim()) { ElMessage.warning('请填写标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请填写内容'); return }
  await save(2)
}

async function save(status: number) {
  saving.value = true
  try {
    const payload = { ...form, status }
    if (articleId.value) {
      await updateArticleApi(articleId.value, payload)
    } else {
      const res = await createArticleApi(payload)
      localStorage.removeItem(draftKey.value)
      router.replace(`/articles/${res.id}/edit`)
    }
    localStorage.removeItem(draftKey.value)
    ElMessage.success(status === 2 ? '发布成功' : '草稿已保存')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  // 加载基础数据
  const [cats, tgs, series] = await Promise.allSettled([
    getCategoriesApi(),
    getTagsApi(),
    getSeriesListApi(),
  ])
  if (cats.status === 'fulfilled') flatCategories.value = flattenCategories(cats.value)
  if (tgs.status === 'fulfilled') tags.value = tgs.value
  if (series.status === 'fulfilled') seriesList.value = series.value

  // 如果是编辑模式，加载文章
  if (articleId.value) {
    const article = await getArticleApi(articleId.value)
    Object.assign(form, {
      title: article.title,
      summary: article.summary,
      content: article.content,
      contentType: article.contentType,
      coverImage: article.coverImage,
      categoryId: article.categoryId,
      tagIds: article.tagIds,
      seriesId: article.seriesId,
      seriesSort: article.seriesSort || 1,
      isFeatured: article.isFeatured,
      status: article.status,
      publishTime: article.publishTime,
    })
  } else {
    // 检测本地草稿
    const draft = localStorage.getItem(draftKey.value)
    if (draft) showDraftAlert.value = true
  }

  startAutoSave()
})

onUnmounted(() => {
  if (autoSaveTimer) clearInterval(autoSaveTimer)
})
</script>

<style scoped>
.editor-page {
  height: calc(100vh - 60px - 48px);
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.editor-topbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.title-input {
  flex: 1;
}

:deep(.title-input .el-input__inner) {
  font-size: 18px;
  font-weight: 600;
  border: none;
  box-shadow: none !important;
}

.topbar-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.editor-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.editor-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid #f0f0f0;
}

.mode-switch {
  padding: 8px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.editor-instance {
  flex: 1;
  overflow: hidden;
}

.editor-sidebar {
  width: 260px;
  flex-shrink: 0;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cover-preview {
  position: relative;
}

.cover-preview img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.cover-remove {
  position: absolute;
  top: 4px;
  right: 4px;
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #333;
}

.draft-alert {
  position: absolute;
  bottom: 16px;
  right: 16px;
  width: 320px;
  z-index: 100;
}
</style>
