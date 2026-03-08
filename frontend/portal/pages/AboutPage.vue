<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useSiteStore } from '../src/stores/site'
import MarkdownIt from 'markdown-it'

const site = useSiteStore()
const md = new MarkdownIt({ html: true, linkify: true })

const renderedAbout = computed(() => {
  const content = site.config['about_content']
  return content ? md.render(content) : ''
})

const skillsList = computed<string[]>(() => {
  try {
    const raw = site.config['skills']
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
})

const authorInitial = computed(() => {
  const name = site.config['site_name'] || 'D'
  return name.charAt(0).toUpperCase()
})

onMounted(() => site.fetchConfig())
</script>

<template>
  <div class="page-body">
    <div class="container">
      <div class="page-hd">
        <span class="page-hd-eyebrow">// About</span>
        <h1 class="page-hd-title">关于我</h1>
      </div>

      <div class="about-layout">
        <!-- Main content -->
        <div>
          <div class="about-avatar-wrap">
            <span v-if="!site.config['avatar']">{{ authorInitial }}</span>
            <img v-else :src="site.config['avatar']" alt="avatar" />
          </div>

          <h2 class="about-name">{{ site.siteName }}</h2>
          <p class="about-tagline">{{ site.tagline }}</p>

          <!-- Bio content from Markdown -->
          <div v-if="renderedAbout" class="about-section">
            <div class="about-sec-title">关于</div>
            <div class="about-body" v-html="renderedAbout"></div>
          </div>

          <!-- Skills -->
          <div v-if="skillsList.length" class="about-section">
            <div class="about-sec-title">技能 & 工具</div>
            <div class="skills-chips">
              <span v-for="skill in skillsList" :key="skill" class="skill-chip">{{ skill }}</span>
            </div>
          </div>
        </div>

        <!-- Sidebar: fact card -->
        <div>
          <div class="about-fact-card">
            <div class="afc-title">联系 & 信息</div>
            <div class="afc-rows">
              <div v-if="site.location" class="afc-row">
                <span class="afc-row-label">城市</span>
                <span class="afc-row-val">{{ site.location }}</span>
              </div>
              <div v-if="site.since" class="afc-row">
                <span class="afc-row-label">建站</span>
                <span class="afc-row-val">{{ site.since }}</span>
              </div>
              <div v-if="site.githubUrl" class="afc-row">
                <span class="afc-row-label">GitHub</span>
                <span class="afc-row-val">
                  <a :href="site.githubUrl" target="_blank" rel="noopener">查看主页 →</a>
                </span>
              </div>
              <div v-if="site.email" class="afc-row">
                <span class="afc-row-label">Email</span>
                <span class="afc-row-val">
                  <a :href="`mailto:${site.email}`">{{ site.email }}</a>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
