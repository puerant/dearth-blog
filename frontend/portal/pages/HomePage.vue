<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useSiteStore } from '../src/stores/site'
import FeaturedCard from '../src/components/FeaturedCard.vue'
import ArticleListItem from '../src/components/ArticleListItem.vue'
import ProjectClock from '../src/components/ProjectClock.vue'
import type { ProjectVO } from '../src/types'

const site = useSiteStore()

const home = computed(() => site.homeData)
const projects = computed(() => home.value?.projects ?? [])

const activeProject = ref<ProjectVO | null>(null)

onMounted(async () => {
  await site.fetchConfig()
  if (projects.value.length) activeProject.value = projects.value[0]
})

function onProjectSelect(p: ProjectVO) {
  activeProject.value = p
}

function formatDate(iso: string) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const statusColor = computed(() => {
  if (!activeProject.value) return {}
  const s = activeProject.value.status
  return s === 'Active'
    ? { borderColor: 'rgba(110,200,154,0.35)', color: '#6EC89A' }
    : s === 'WIP'
    ? { borderColor: 'rgba(200,169,110,0.35)', color: 'var(--accent)' }
    : { borderColor: 'var(--border-2)', color: 'var(--text-3)' }
})
</script>

<template>
  <div v-if="home" class="page-body">

    <!-- ── Hero ── -->
    <section class="hero">
      <!-- Corner metadata -->
      <div class="hero-corners">
        <div class="hero-corner-label">
          个人博客
          <span>Since {{ site.since }}</span>
        </div>
        <div class="hero-corner-label" style="text-align:right;">
          <span>{{ home.totalArticles }} 篇文章</span>
          <span>{{ site.location }}</span>
        </div>
      </div>

      <!-- Projects Clock -->
      <ProjectClock v-if="projects.length" :projects="projects" @select="onProjectSelect" />

      <!-- Display name -->
      <div class="hero-display-wrap">
        <p class="hero-display-sub">{{ site.tagline }}</p>
        <h1 class="hero-display">{{ site.siteName.replace('·', '') }}</h1>
      </div>

      <!-- Bottom bar -->
      <div class="hero-bottom">
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-val">{{ home.totalArticles }}</span>
            <span class="hero-stat-label">文章</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-val">{{ home.totalCategories }}</span>
            <span class="hero-stat-label">分类</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-val">{{ home.totalTags }}</span>
            <span class="hero-stat-label">标签</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-val">{{ home.totalSeries }}</span>
            <span class="hero-stat-label">系列</span>
          </div>
        </div>
        <span class="hero-scroll">向下探索</span>
      </div>
    </section>

    <!-- ── Project Info Panel ── -->
    <div v-if="activeProject" class="project-info container--narrow">
      <div class="pi-inner pi-enter">
        <div class="pi-left">
          <div class="pi-id">{{ activeProject.projectNo }}</div>
          <div class="pi-name">{{ activeProject.name }}</div>
          <p class="pi-desc">{{ activeProject.description }}</p>
        </div>
        <div class="pi-right">
          <div class="pi-tech">
            <span v-for="t in activeProject.techStack" :key="t" class="pi-tag">{{ t }}</span>
          </div>
          <div class="pi-meta">
            <span class="pi-year">{{ activeProject.startYear }}</span>
            <span class="pi-status" :style="statusColor">{{ activeProject.status }}</span>
            <a :href="`/projects/${activeProject.projectNo}`" class="pi-detail-link">详情 →</a>
          </div>
        </div>
      </div>
    </div>

    <!-- ── Featured ── -->
    <section v-if="home.featuredArticles.length" style="margin-top: 80px;">
      <div class="container--narrow">
        <div class="section-hd">
          <span class="section-label">// 精选文章</span>
        </div>
        <div class="featured-grid">
          <FeaturedCard v-for="a in home.featuredArticles" :key="a.id" :article="a" />
        </div>
      </div>
    </section>

    <!-- ── Recent Articles ── -->
    <section style="margin-top: 80px;">
      <div class="container--narrow">
        <div class="section-hd">
          <span class="section-label">// 最新文章</span>
          <a href="/archives" class="section-more">全部归档</a>
        </div>
        <ul class="article-list article-list--indexed">
          <ArticleListItem
            v-for="(a, i) in home.recentArticles"
            :key="a.id"
            :article="a"
            :index="i"
          />
        </ul>
      </div>
    </section>

  </div>

  <!-- Loading state -->
  <div v-else style="min-height: 100svh; display: flex; align-items: center; justify-content: center;">
    <div class="empty-state">
      <div class="empty-state-icon">○</div>
      <div>加载中…</div>
    </div>
  </div>
</template>
