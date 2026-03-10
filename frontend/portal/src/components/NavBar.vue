<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vitepress'
import { useSiteStore } from '../stores/site'

const route = useRoute()
const site = useSiteStore()
const mobileOpen = ref(false)
const isDark = ref(true)

const navLinks = [
  { label: 'Articles', href: '/' },
  { label: 'Categories', href: '/categories' },
  { label: 'Tags', href: '/tags' },
  { label: 'Archives', href: '/archives' },
  { label: 'Series', href: '/series' },
  { label: 'About', href: '/about' },
]

function isActive(href: string) {
  if (href === '/') return route.path === '/'
  return route.path.startsWith(href)
}

function toggleTheme() {
  isDark.value = !isDark.value
  const theme = isDark.value ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('dear-theme', theme)
}

onMounted(() => {
  const saved = localStorage.getItem('dear-theme')
  if (saved === 'light') {
    isDark.value = false
    document.documentElement.setAttribute('data-theme', 'light')
  }
})
</script>

<template>
  <nav class="site-nav">
    <div class="container">
      <a href="/" class="site-title">
        <span class="site-logo">
          <span class="site-logo-text">DEAR</span>
          <sup class="site-logo-sup">th</sup>
        </span>
      </a>

      <ul class="nav-links" :class="{ open: mobileOpen }">
        <li v-for="link in navLinks" :key="link.href">
          <a :href="link.href" :class="{ active: isActive(link.href) }" @click="mobileOpen = false">{{ link.label }}</a>
        </li>
      </ul>

      <button class="theme-btn" :title="isDark ? '切换至亮色模式' : '切换至暗色模式'" @click="toggleTheme">
        <!-- Sun icon (shown in dark mode, click to go light) -->
        <svg v-if="isDark" class="theme-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="4"/>
          <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/>
        </svg>
        <!-- Moon icon (shown in light mode, click to go dark) -->
        <svg v-else class="theme-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>

      <div class="nav-toggle" :class="{ open: mobileOpen }" @click="mobileOpen = !mobileOpen">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </nav>
</template>
