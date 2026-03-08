<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { portalApi } from '../src/api/portal'
import ArticleListItem from '../src/components/ArticleListItem.vue'
import type { TagVO, PortalArticleListVO } from '../src/types'

const tags = ref<TagVO[]>([])
const articles = ref<PortalArticleListVO[]>([])
const activeTag = ref('')

const resultLabel = computed(() =>
  activeTag.value ? `# ${activeTag.value}` : '全部文章'
)

async function selectTag(name: string) {
  activeTag.value = activeTag.value === name ? '' : name
  articles.value = []
  try {
    const page = await portalApi.listArticles({
      size: 100,
      tagSlug: activeTag.value ? tags.value.find(t => t.name === activeTag.value)?.slug : undefined
    })
    articles.value = page.records
  } catch {}
}

onMounted(async () => {
  const qs = new URLSearchParams(window.location.search)
  const initTag = qs.get('tag') || ''
  try {
    tags.value = await portalApi.listTags()
    const page = await portalApi.listArticles({ size: 100, tagSlug: initTag ? tags.value.find(t => t.name === initTag)?.slug : undefined })
    articles.value = page.records
    if (initTag) activeTag.value = initTag
  } catch {}
})
</script>

<template>
  <div class="page-body">
    <div class="container--narrow">
      <div class="page-hd">
        <span class="page-hd-eyebrow">// Tags</span>
        <h1 class="page-hd-title">标签</h1>
        <p class="page-hd-sub">共 <strong>{{ tags.length }}</strong> 个标签，点击标签筛选文章</p>
      </div>

      <!-- Tag cloud -->
      <div class="tag-cloud-wrap">
        <button
          v-for="tag in tags"
          :key="tag.id"
          class="tag-cloud-btn"
          :class="{ active: activeTag === tag.name }"
          @click="selectTag(tag.name)"
        >
          {{ tag.name }}
          <span class="tc-count">{{ tag.articleCount }}</span>
        </button>
      </div>

      <!-- Results -->
      <div>
        <div class="section-hd" style="padding-top: 0;">
          <span class="section-label">{{ resultLabel }}</span>
          <span style="font-size: 13px; color: var(--text-3);">{{ articles.length }} 篇</span>
        </div>

        <div v-if="!articles.length" class="empty-state">
          <div class="empty-state-icon">🏷️</div>
          <div>暂无文章</div>
        </div>

        <ul v-else class="article-list">
          <ArticleListItem v-for="a in articles" :key="a.id" :article="a" />
        </ul>
      </div>
    </div>
  </div>
</template>
