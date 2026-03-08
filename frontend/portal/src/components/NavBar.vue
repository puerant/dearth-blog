<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vitepress'
import { useSiteStore } from '../stores/site'

const route = useRoute()
const site = useSiteStore()
const mobileOpen = ref(false)

const navLinks = [
  { label: '文章', href: '/' },
  { label: '分类', href: '/categories' },
  { label: '标签', href: '/tags' },
  { label: '归档', href: '/archives' },
  { label: '系列', href: '/series' },
  { label: '关于', href: '/about' },
]

function isActive(href: string) {
  if (href === '/') return route.path === '/'
  return route.path.startsWith(href)
}
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
          <a :href="link.href" :class="{ active: isActive(link.href) }">{{ link.label }}</a>
        </li>
      </ul>

      <div class="nav-toggle" @click="mobileOpen = !mobileOpen">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </nav>
</template>
