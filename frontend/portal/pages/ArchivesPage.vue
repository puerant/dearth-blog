<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { portalApi } from '../src/api/portal'
import type { ArchiveItemVO } from '../src/types'

const archive = ref<ArchiveItemVO[]>([])
const loading = ref(true)

function dayOf(iso: string) {
  return iso ? new Date(iso).getDate().toString().padStart(2, '0') : ''
}

const MONTH_NAMES = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']

onMounted(async () => {
  try {
    archive.value = await portalApi.getArchive()
  } catch {}
  loading.value = false
})
</script>

<template>
  <div class="page-body">
    <div class="container--narrow">
      <div class="page-hd">
        <span class="page-hd-eyebrow">// Archives</span>
        <h1 class="page-hd-title">归档</h1>
        <p class="page-hd-sub">按时间浏览所有文章</p>
      </div>

      <div v-if="loading" class="empty-state">
        <div class="empty-state-icon">○</div>
        <div>加载中…</div>
      </div>

      <template v-else>
        <div v-for="yearGroup in archive" :key="yearGroup.year" class="archive-section">
          <div class="archive-year-hd">
            <span class="archive-year-num">{{ yearGroup.year }}</span>
            <span class="archive-year-count">
              {{ yearGroup.months.reduce((s, m) => s + m.articles.length, 0) }} 篇
            </span>
          </div>

          <div v-for="monthGroup in yearGroup.months" :key="monthGroup.month" class="archive-month-group">
            <div class="archive-month-label">
              {{ MONTH_NAMES[monthGroup.month - 1] || monthGroup.month }}
            </div>
            <div v-for="a in monthGroup.articles" :key="a.id" class="archive-entry">
              <span class="ae-day">{{ dayOf(a.publishTime) }}</span>
              <a class="ae-title" :href="`/articles/${a.slug}`">{{ a.title }}</a>
              <span class="ae-cat">{{ a.categoryName }}</span>
            </div>
          </div>
        </div>

        <div v-if="!archive.length" class="empty-state">
          <div class="empty-state-icon">📅</div>
          <div>暂无归档</div>
        </div>
      </template>
    </div>
  </div>
</template>
