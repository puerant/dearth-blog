<script setup lang="ts">
import type { PortalArticleListVO } from '../types'

defineProps<{ article: PortalArticleListVO }>()

function formatDate(iso: string) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<template>
  <a class="featured-card" :href="`/articles/${article.slug}`">
    <span class="featured-badge">FEATURED</span>
    <div class="fc-meta">{{ formatDate(article.publishTime) }} · {{ article.categoryName }}</div>
    <div class="fc-title">{{ article.title }}</div>
    <div class="fc-excerpt">{{ article.summary }}</div>
  </a>
</template>
