<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useSiteStore } from '../src/stores/site'
import FeaturedCard from '../src/components/FeaturedCard.vue'
import ArticleListItem from '../src/components/ArticleListItem.vue'
import type { ProjectVO } from '../src/types'

const site = useSiteStore()

const home = computed(() => site.homeData)
const projects = computed(() => home.value?.projects ?? [])
const featuredProject = computed(() => projects.value[0] ?? null)
const projectList = computed(() => projects.value.slice(0, 6))
const hasMoreProjects = computed(() => projects.value.length > 6)

onMounted(async () => {
  await site.fetchConfig()
})

function getStatusColor(project: ProjectVO) {
  const s = project.status
  return s === 'Active'
    ? { borderColor: 'rgba(110,200,154,0.35)', color: '#6EC89A' }
    : s === 'WIP'
    ? { borderColor: 'rgba(200,169,110,0.35)', color: 'var(--accent)' }
    : { borderColor: 'var(--border-2)', color: 'var(--text-3)' }
}
</script>

<template>
  <div v-if="home" class="page-body">

    <!-- Hero -->
    <section class="hero">
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

      <div class="hero-display-wrap">
        <h1 class="hero-display">DEAR</h1>
<!--        <p class="hero-display-sub">{{ site.tagline }}</p>-->
      </div>

      <div v-if="projects.length" class="hero-projects">
        <a
          v-for="p in projects.slice(0, 4)"
          :key="p.id"
          class="hero-project-entry"
          :href="`/projects/${p.projectNo}`"
        >
          <span class="hpe-no">{{ p.projectNo }}</span>
          <span class="hpe-name">{{ p.name }}</span>
          <span class="hpe-year">{{ p.startYear }}</span>
          <span class="hpe-status" :style="getStatusColor(p)">{{ p.status }}</span>
        </a>
      </div>

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

    <!-- Featured -->
    <section v-if="home.featuredArticles.length" class="home-section">
      <div class="container--narrow">
        <div class="section-hd">
          <span class="section-label">// 精选文章</span>
        </div>
        <div class="featured-grid">
          <FeaturedCard v-for="a in home.featuredArticles" :key="a.id" :article="a" />
        </div>
      </div>
    </section>

    <!-- Recent Articles -->
    <section class="home-section">
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

    <!-- Projects -->
<!--    <section v-if="projects.length" class="project-info container&#45;&#45;narrow">-->
<!--      <div class="section-hd">-->
<!--        <span class="section-label">// 项目</span>-->
<!--        <a v-if="hasMoreProjects" href="/projects" class="section-more">查看全部项目</a>-->
<!--      </div>-->

<!--      <div class="project-showcase">-->
<!--        <article v-if="featuredProject" class="project-spotlight">-->
<!--          <div class="pi-id">{{ featuredProject.projectNo }}</div>-->
<!--          <h3 class="pi-name">{{ featuredProject.name }}</h3>-->
<!--          <p class="pi-desc">{{ featuredProject.description }}</p>-->
<!--          <div class="pi-tech">-->
<!--            <span v-for="t in featuredProject.techStack.slice(0, 3)" :key="`${featuredProject.id}-${t}`" class="pi-tag">{{ t }}</span>-->
<!--          </div>-->
<!--          <div class="pi-meta">-->
<!--            <span class="pi-year">{{ featuredProject.startYear }}</span>-->
<!--            <span class="pi-status" :style="getStatusColor(featuredProject)">{{ featuredProject.status }}</span>-->
<!--            <a :href="`/projects/${featuredProject.projectNo}`" class="pi-detail-link">查看项目 →</a>-->
<!--          </div>-->
<!--        </article>-->

<!--        <div class="project-list-minimal">-->
<!--          <a-->
<!--            v-for="project in projectList"-->
<!--            :key="project.id"-->
<!--            class="project-row"-->
<!--            :href="`/projects/${project.projectNo}`"-->
<!--          >-->
<!--            <span class="project-row-year">{{ project.startYear }}</span>-->
<!--            <span class="project-row-name">{{ project.name }}</span>-->
<!--            <span class="project-row-status" :style="getStatusColor(project)">{{ project.status }}</span>-->
<!--          </a>-->
<!--          <a v-if="hasMoreProjects" href="/projects" class="project-row project-row-more">-->
<!--            <span class="project-row-year">...</span>-->
<!--            <span class="project-row-name">查看更多项目</span>-->
<!--            <span class="project-row-status">MORE</span>-->
<!--          </a>-->
<!--        </div>-->
<!--      </div>-->
<!--    </section>-->

  </div>

  <div v-else style="min-height: 100svh; display: flex; align-items: center; justify-content: center;">
    <div class="empty-state">
      <div class="empty-state-icon">○</div>
      <div>加载中…</div>
    </div>
  </div>
</template>

<style scoped>
.home-section { margin-top: 80px; }

@media (max-width: 768px) {
  .home-section { margin-top: 52px; }
  .project-info { margin-top: 40px; }
}

@media (max-width: 480px) {
  .home-section { margin-top: 36px; }
}
</style>
