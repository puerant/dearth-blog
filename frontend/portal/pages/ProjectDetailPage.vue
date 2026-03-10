<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { portalApi } from '../src/api/portal'
import type { ProjectVO } from '../src/types'

const props = defineProps<{ projectNo: string }>()

const project = ref<ProjectVO | null>(null)
const loading = ref(true)
const error = ref(false)

async function load(no: string) {
  if (!no) return
  loading.value = true
  error.value = false
  try {
    project.value = await portalApi.getProject(no)
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

// onMounted: 首次挂载时加载（props 已有正确值时）
onMounted(() => { if (props.projectNo) load(props.projectNo) })
// watch: 处理 prop 延迟赋值（如 URL fallback 的 onMounted 异步写入）
watch(() => props.projectNo, (no) => { if (no) load(no) })

function getStatusStyle(status: string) {
  return status === 'Active'
    ? { color: '#6EC89A' }
    : status === 'WIP'
    ? { color: 'var(--accent)' }
    : { color: 'var(--text-3)' }
}

const projHue = computed(() => project.value?.hue ?? 38)
const heroStyle = computed(() => ({ '--proj-hue': projHue.value }))
</script>

<template>
  <!-- Loading -->
  <div v-if="loading" class="pj-fullscreen">
    <div class="empty-state">
      <div class="empty-state-icon">○</div>
      <div>加载中…</div>
    </div>
  </div>

  <!-- Error -->
  <div v-else-if="error || !project" class="pj-fullscreen">
    <div class="empty-state">
      <div class="empty-state-icon">×</div>
      <div>项目不存在或加载失败</div>
    </div>
  </div>

  <!-- Content -->
  <div v-else class="pj-page" :style="heroStyle">

    <!-- ── Hero ─────────────────────────────────────────── -->
    <section class="pj-hero">
      <div class="pj-hero-glow" aria-hidden="true"></div>

      <div class="pj-hero-top">
        <a href="/" class="pj-back">← 首页</a>
        <span class="pj-hero-no">{{ project.projectNo }}</span>
      </div>

      <div class="pj-ghost" aria-hidden="true">{{ project.shortName || project.name }}</div>

      <div class="pj-hero-body">
        <h1 class="pj-name">{{ project.name }}</h1>
        <p v-if="project.description" class="pj-desc">{{ project.description }}</p>
        <div class="pj-hero-actions">
          <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" rel="noopener" class="pj-gh-link">
            GitHub →
          </a>
        </div>
      </div>

      <div class="pj-hero-bottom">
        <div class="pj-meta-row">
          <span class="pj-meta-item">
            <span class="pj-meta-label">YEAR</span>
            <span class="pj-meta-val">{{ project.startYear }}</span>
          </span>
          <span class="pj-meta-sep" aria-hidden="true">·</span>
          <span class="pj-meta-item">
            <span class="pj-meta-label">STATUS</span>
            <span class="pj-meta-val" :style="getStatusStyle(project.status)">{{ project.status }}</span>
          </span>
          <template v-if="project.techStack?.length">
            <span class="pj-meta-sep" aria-hidden="true">·</span>
            <span class="pj-meta-item">
              <span class="pj-meta-label">STACK</span>
              <span class="pj-meta-val pj-stack-inline">
                <template v-for="(t, i) in project.techStack.slice(0, 4)" :key="t">
                  {{ t }}<span v-if="i < Math.min(project.techStack.length, 4) - 1" class="pj-stack-dot"> · </span>
                </template>
                <span v-if="project.techStack.length > 4" class="pj-stack-more"> +{{ project.techStack.length - 4 }}</span>
              </span>
            </span>
          </template>
        </div>
      </div>
    </section>

    <!-- ── Body ─────────────────────────────────────────── -->
    <div class="pj-body container--narrow">

      <section v-if="project.highlights?.length" class="pj-section">
        <header class="pj-section-hd">
          <span class="pj-section-label">// 项目亮点</span>
          <span class="pj-section-count">{{ project.highlights.length }}</span>
        </header>
        <ul class="pj-hl-list">
          <li v-for="(h, i) in project.highlights" :key="i" class="pj-hl-item">
            <span class="pj-hl-idx">{{ String(i + 1).padStart(2, '0') }}</span>
            <span class="pj-hl-text">{{ h }}</span>
          </li>
        </ul>
      </section>

      <section v-if="project.techStack?.length" class="pj-section">
        <header class="pj-section-hd">
          <span class="pj-section-label">// 技术栈</span>
          <span class="pj-section-count">{{ project.techStack.length }}</span>
        </header>
        <div class="pj-tags">
          <span v-for="t in project.techStack" :key="t" class="pj-tag">{{ t }}</span>
        </div>
      </section>

    </div>
  </div>
</template>

<style scoped>
.pj-page {
  --proj-accent:     hsl(var(--proj-hue), 55%, 58%);
  --proj-glow-start: hsl(var(--proj-hue), 60%, 20%);
}

.pj-fullscreen {
  min-height: 100svh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ── Hero ─────────────────────────────────────────────── */
.pj-hero {
  position: relative;
  min-height: 100svh;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  overflow: hidden;
  background-image: repeating-linear-gradient(
    to bottom,
    transparent 0, transparent 23px,
    rgba(255,255,255,0.012) 23px, rgba(255,255,255,0.012) 24px
  );
}
[data-theme="light"] .pj-hero {
  background-image: repeating-linear-gradient(
    to bottom,
    transparent 0, transparent 23px,
    rgba(0,0,0,0.022) 23px, rgba(0,0,0,0.022) 24px
  );
}

/* Hue-based ambient glow */
.pj-hero-glow {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(
    ellipse 70% 55% at 60% 30%,
    var(--proj-glow-start) 0%,
    transparent 70%
  );
  opacity: 0.5;
}
[data-theme="light"] .pj-hero-glow {
  background: radial-gradient(
    ellipse 70% 55% at 60% 30%,
    hsl(var(--proj-hue), 60%, 88%) 0%,
    transparent 70%
  );
  opacity: 0.6;
}

/* Ghost project short-name */
.pj-ghost {
  position: absolute;
  right: -2%;
  top: 14%;
  font-family: var(--font-logo);
  font-size: clamp(80px, 18vw, 260px);
  font-weight: 800;
  line-height: 0.88;
  letter-spacing: -0.05em;
  text-transform: uppercase;
  color: var(--proj-accent);
  opacity: 0.07;
  pointer-events: none;
  user-select: none;
  white-space: nowrap;
}

/* Top bar */
.pj-hero-top {
  position: absolute;
  top: 72px; left: 0; right: 0;
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 2;
}
.pj-back {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--text-3);
  text-decoration: none;
  transition: color var(--transition);
}
.pj-back:hover { color: var(--text); }
.pj-hero-no {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.16em;
  color: var(--text-3);
  text-transform: uppercase;
}

/* Hero main */
.pj-hero-body {
  padding: 0 40px;
  position: relative;
  z-index: 2;
  margin-bottom: 8px;
}
.pj-name {
  font-family: var(--font-logo);
  font-weight: 800;
  font-size: clamp(40px, 8vw, 100px);
  letter-spacing: -0.04em;
  line-height: 0.95;
  color: var(--text);
  margin-bottom: 20px;
}
.pj-desc {
  font-size: 16px;
  line-height: 1.75;
  color: var(--text-2);
  max-width: 560px;
  margin-bottom: 28px;
}
.pj-gh-link {
  display: inline-block;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--proj-accent);
  text-decoration: none;
  padding: 8px 16px;
  border: 1px solid color-mix(in srgb, var(--proj-accent) 35%, transparent);
  transition: border-color var(--transition), background var(--transition);
}
.pj-gh-link:hover {
  background: color-mix(in srgb, var(--proj-accent) 10%, transparent);
  border-color: var(--proj-accent);
}

/* Hero bottom meta bar */
.pj-hero-bottom {
  padding: 20px 40px 40px;
  border-top: 1px solid var(--border-2);
  position: relative;
  z-index: 2;
}
.pj-meta-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.pj-meta-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.pj-meta-label {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.14em;
  color: var(--text-3);
}
.pj-meta-val {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.06em;
  color: var(--text-2);
  font-weight: 600;
}
.pj-meta-sep { color: var(--text-3); font-size: 12px; }
.pj-stack-inline { font-weight: 500; }
.pj-stack-dot, .pj-stack-more { color: var(--text-3); }

/* ── Body ─────────────────────────────────────────────── */
.pj-body {
  padding-top: 72px;
  padding-bottom: 96px;
}
.pj-section { margin-bottom: 60px; }
.pj-section-hd {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}
.pj-section-label {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.12em;
  color: var(--text-2);
  font-weight: 600;
}
.pj-section-count {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--text-3);
  background: var(--bg-3);
  padding: 1px 7px;
  border-radius: 2px;
}

/* Highlights */
.pj-hl-list { display: flex; flex-direction: column; }
.pj-hl-item {
  display: grid;
  grid-template-columns: 36px 1fr;
  gap: 16px;
  align-items: baseline;
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
}
.pj-hl-idx {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.1em;
  color: var(--proj-accent);
  font-weight: 700;
  opacity: 0.75;
}
.pj-hl-text {
  font-size: 15px;
  line-height: 1.7;
  color: var(--text-2);
}

/* Tech tags */
.pj-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.pj-tag {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.06em;
  color: var(--tag-text);
  background: var(--tag-bg);
  padding: 5px 12px;
  border-radius: 2px;
  transition: background var(--transition), color var(--transition);
}
.pj-tag:hover {
  background: color-mix(in srgb, var(--proj-accent) 12%, var(--tag-bg));
  color: var(--proj-accent);
}

/* ── Responsive ────────────────────────────────────────── */
@media (max-width: 768px) {
  .pj-hero-top { padding: 0 20px; top: 56px; }
  .pj-hero-body { padding: 0 20px; }
  .pj-hero-bottom { padding: 16px 20px 36px; }
  .pj-body { padding-top: 48px; padding-bottom: 64px; }
  .pj-ghost { font-size: clamp(64px, 22vw, 160px); top: 10%; right: -4%; }
}
@media (max-width: 480px) {
  .pj-name { font-size: clamp(32px, 11vw, 56px); }
  .pj-meta-row { gap: 10px; }
  .pj-hl-item { grid-template-columns: 28px 1fr; gap: 12px; }
}
</style>
