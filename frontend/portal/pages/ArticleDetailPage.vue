<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { portalApi } from '../src/api/portal'
import type { PortalArticleDetailVO } from '../src/types'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const props = defineProps<{ slug: string }>()

const article = ref<PortalArticleDetailVO | null>(null)
const loading = ref(true)
const error = ref(false)

// Markdown renderer
const md = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch {}
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

const renderedContent = computed(() => {
  if (!article.value) return ''
  if (article.value.contentType === 1) {
    return md.render(article.value.content)
  }
  return article.value.content // Rich text already HTML
})

// TOC
const toc = ref<{ id: string; text: string; level: number }[]>([])
const activeTocId = ref('')
const readingProgress = ref(0)

function buildToc() {
  const body = document.querySelector('.article-body')
  if (!body) return
  const headings = body.querySelectorAll('h2, h3')
  toc.value = Array.from(headings).map((h) => {
    if (!h.id) h.id = h.textContent!.trim().toLowerCase().replace(/\s+/g, '-').replace(/[^\w-]/g, '')
    return { id: h.id, text: h.textContent!.trim(), level: parseInt(h.tagName[1]) }
  })
}

function onScroll() {
  const scrollTop = window.scrollY
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  readingProgress.value = docHeight > 0 ? Math.min(100, (scrollTop / docHeight) * 100) : 0

  // Active TOC item
  const headings = document.querySelectorAll('.article-body h2, .article-body h3')
  let active = ''
  headings.forEach((h) => {
    if ((h as HTMLElement).offsetTop - 100 <= scrollTop) active = h.id
  })
  activeTocId.value = active
}

function formatDate(iso: string) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

async function load(slug: string) {
  loading.value = true
  error.value = false
  try {
    article.value = await portalApi.getArticle(slug)
    portalApi.recordVisit(`/articles/${slug}`, article.value.id)
    await nextTick()
    buildToc()
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.slug) load(props.slug)
  window.addEventListener('scroll', onScroll, { passive: true })
})

watch(() => props.slug, (newSlug) => {
  if (newSlug) load(newSlug)
})
</script>

<template>
  <div class="page-body">
    <div class="container">

      <!-- Loading -->
      <div v-if="loading" class="empty-state" style="padding-top: 120px;">
        <div class="empty-state-icon">○</div>
        <div>加载中…</div>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="empty-state" style="padding-top: 120px;">
        <div class="empty-state-icon">×</div>
        <div>文章不存在或加载失败</div>
      </div>

      <!-- Content -->
      <div v-else-if="article" class="article-page">
        <div>
          <!-- Breadcrumb -->
          <div class="breadcrumb">
            <a href="/">文章</a>
            <span class="breadcrumb-sep">/</span>
            <a :href="`/categories?slug=${article.categorySlug}`">{{ article.categoryName }}</a>
            <span class="breadcrumb-sep">/</span>
            <span>{{ article.title }}</span>
          </div>

          <!-- Article header -->
          <header class="article-header">
            <span class="a-cat">{{ article.categoryName }}</span>
            <h1>{{ article.title }}</h1>
            <div class="article-hd-meta">
              <span>{{ formatDate(article.publishTime) }}</span>
              <span>{{ article.readingTime }} min 阅读</span>
              <span>{{ article.viewCount }} 次浏览</span>
              <span v-if="article.seriesName">系列：{{ article.seriesName }}</span>
            </div>
          </header>

          <!-- Article body -->
          <article class="article-body" v-html="renderedContent"></article>

          <!-- Tags footer -->
          <div v-if="article.tags.length" style="margin-top: 40px; padding-top: 28px; border-top: 1px solid var(--border); display: flex; flex-wrap: wrap; gap: 8px;">
            <a v-for="tag in article.tags" :key="tag.id" :href="`/tags?tag=${tag.name}`" class="tag-pill" style="font-size: 12px; padding: 4px 12px;">
              # {{ tag.name }}
            </a>
          </div>

          <!-- Prev / Next -->
          <nav class="article-nav">
            <a v-if="article.prevArticle" class="article-nav-btn" :href="`/articles/${article.prevArticle.slug}`">
              <div class="anb-dir">← 上一篇</div>
              <div class="anb-title">{{ article.prevArticle.title }}</div>
            </a>
            <div v-else></div>
            <a v-if="article.nextArticle" class="article-nav-btn article-nav-btn--next" :href="`/articles/${article.nextArticle.slug}`">
              <div class="anb-dir">下一篇 →</div>
              <div class="anb-title">{{ article.nextArticle.title }}</div>
            </a>
          </nav>
        </div>

        <!-- Sidebar: TOC + Progress -->
        <aside class="article-sidebar">
          <p class="toc-label">目录</p>
          <ul v-if="toc.length" class="toc-list">
            <li v-for="item in toc" :key="item.id" :class="`toc-item--h${item.level}`">
              <a class="toc-link" :class="{ active: activeTocId === item.id }" :href="`#${item.id}`">
                {{ item.text }}
              </a>
            </li>
          </ul>

          <div class="reading-progress-wrap">
            <p class="toc-label">阅读进度</p>
            <div class="reading-progress-bar-bg">
              <div class="reading-progress-bar" :style="`width: ${readingProgress}%`"></div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>
