<script setup lang="ts">
import type { PortalArticleListVO } from '../types'

defineProps<{
  article: PortalArticleListVO
  index?: number       // numbered list
  showExcerpt?: boolean
}>()

function formatDate(iso: string) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<template>
  <li class="article-item">
    <a class="article-item-link" :href="`/articles/${article.slug}`">
      <div v-if="index !== undefined" class="ai-num">{{ String(index + 1).padStart(2, '0') }}</div>

      <div class="ai-main">
        <div class="a-meta">
          <span class="a-date">{{ formatDate(article.publishTime) }}</span>
          <span class="a-sep"></span>
          <span class="a-cat">{{ article.categoryName }}</span>
        </div>
        <h2 class="a-title">{{ article.title }}</h2>
        <p v-if="showExcerpt !== false" class="a-excerpt">{{ article.summary }}</p>
      </div>

      <div v-if="index !== undefined" class="ai-right">
        <div class="a-tags">
          <span v-for="tag in article.tags.slice(0, 2)" :key="tag.id" class="tag-pill">
            {{ tag.name }}
          </span>
        </div>
        <span class="a-read-time">{{ article.readingTime }} min</span>
      </div>
    </a>
  </li>
</template>
