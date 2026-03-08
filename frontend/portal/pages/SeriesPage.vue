<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { portalApi } from '../src/api/portal'
import type { SeriesVO, SeriesDetailVO } from '../src/types'

const series = ref<SeriesVO[]>([])
const expanded = ref<Record<number, SeriesDetailVO | null>>({})
const loading = ref(true)

function formatDate(iso: string) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

async function toggleSeries(s: SeriesVO) {
  if (expanded.value[s.id] !== undefined) {
    delete expanded.value[s.id]
    return
  }
  expanded.value[s.id] = null
  try {
    expanded.value[s.id] = await portalApi.getSeries(s.slug)
  } catch {}
}

onMounted(async () => {
  try {
    series.value = await portalApi.listSeries()
  } catch {}
  loading.value = false
})
</script>

<template>
  <div class="page-body">
    <div class="container--narrow">
      <div class="page-hd">
        <span class="page-hd-eyebrow">// Series</span>
        <h1 class="page-hd-title">系列</h1>
        <p class="page-hd-sub">成系列的文章集合，系统性学习利器</p>
      </div>

      <div v-if="loading" class="empty-state">
        <div class="empty-state-icon">○</div>
        <div>加载中…</div>
      </div>

      <div v-else-if="!series.length" class="empty-state">
        <div class="empty-state-icon">📚</div>
        <div>暂无系列</div>
      </div>

      <div v-else class="series-grid">
        <div v-for="s in series" :key="s.id" class="series-card" style="cursor: pointer;" @click="toggleSeries(s)">
          <div class="series-card-hd">
            <div class="series-name">{{ s.name }}</div>
            <span class="series-badge">{{ s.articleCount }} 篇</span>
          </div>
          <p class="series-desc">{{ s.description }}</p>

          <!-- Expanded article list -->
          <div v-if="expanded[s.id] !== undefined">
            <div v-if="expanded[s.id] === null" style="font-size: 12px; color: var(--text-3);">加载中…</div>
            <ul v-else-if="expanded[s.id]?.articles.length" class="series-articles-list">
              <li v-for="(a, i) in expanded[s.id]!.articles" :key="a.id" class="series-article-row">
                <span class="sar-num">{{ String(i + 1).padStart(2, '0') }}</span>
                <a class="sar-title" :href="`/articles/${a.slug}`" @click.stop>{{ a.title }}</a>
                <span class="sar-date">{{ formatDate(a.publishTime) }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
