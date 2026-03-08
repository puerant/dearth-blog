<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { portalApi } from '../src/api/portal'
import ArticleListItem from '../src/components/ArticleListItem.vue'
import type { CategoryVO, PortalArticleListVO } from '../src/types'

const categories = ref<CategoryVO[]>([])
const articles = ref<PortalArticleListVO[]>([])
const activeSlug = ref('')

// Flat list for sidebar
const flatCategories = computed(() => {
  const result: CategoryVO[] = []
  function walk(list: CategoryVO[]) {
    list.forEach(c => { result.push(c); if (c.children?.length) walk(c.children) })
  }
  walk(categories.value)
  return result
})

const activeCategory = computed(() => flatCategories.value.find(c => c.slug === activeSlug.value))

async function selectCategory(slug: string) {
  activeSlug.value = slug
  articles.value = []
  try {
    const page = await portalApi.listArticles({ size: 100, categorySlug: slug })
    articles.value = page.records
  } catch {}
}

onMounted(async () => {
  const qs = new URLSearchParams(window.location.search)
  const initSlug = qs.get('slug') || ''
  try {
    categories.value = await portalApi.listCategories()
    if (initSlug) {
      await selectCategory(initSlug)
    } else if (flatCategories.value.length) {
      await selectCategory(flatCategories.value[0].slug)
    }
  } catch {}
})
</script>

<template>
  <div class="page-body">
    <div class="container">
      <div class="page-hd">
        <span class="page-hd-eyebrow">// Categories</span>
        <h1 class="page-hd-title">分类</h1>
        <p class="page-hd-sub">按主题浏览所有文章</p>
      </div>

      <div class="cat-layout">
        <!-- Sidebar tree -->
        <aside class="cat-sidebar">
          <p class="cat-sidebar-label">所有分类</p>
          <ul class="cat-tree">
            <li v-for="cat in categories" :key="cat.id" class="cat-tree-item">
              <a class="cat-tree-link" :class="{ active: activeSlug === cat.slug }" @click.prevent="selectCategory(cat.slug)" href="#">
                {{ cat.name }}
                <span class="cat-count">{{ cat.articleCount }}</span>
              </a>
              <ul v-if="cat.children?.length" class="cat-children">
                <li v-for="child in cat.children" :key="child.id" class="cat-tree-item">
                  <a class="cat-tree-link" :class="{ active: activeSlug === child.slug }" @click.prevent="selectCategory(child.slug)" href="#">
                    {{ child.name }}
                    <span class="cat-count">{{ child.articleCount }}</span>
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </aside>

        <!-- Articles -->
        <div>
          <div v-if="activeCategory" style="margin-bottom: 28px;">
            <h2 style="font-family: var(--font-display); font-size: 20px; font-weight: 700; color: var(--text); letter-spacing: -0.02em;">
              {{ activeCategory.name }}
            </h2>
            <p style="font-size: 13px; color: var(--text-3); margin-top: 6px;">{{ articles.length }} 篇文章</p>
          </div>

          <div v-if="!articles.length" class="empty-state">
            <div class="empty-state-icon">📂</div>
            <div>暂无文章</div>
          </div>

          <ul v-else class="article-list">
            <ArticleListItem v-for="a in articles" :key="a.id" :article="a" />
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
