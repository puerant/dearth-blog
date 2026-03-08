<script setup lang="ts">
import { computed } from 'vue'
import { useSiteStore } from '../stores/site'

const site = useSiteStore()
const year = new Date().getFullYear()
const mailtoHref = computed(() => site.email ? `mailto:${site.email}` : '#')
</script>

<template>
  <footer class="site-footer">
    <div class="container">
      <p class="footer-left">© {{ year }} {{ site.siteName }}</p>
      <div class="footer-links">
        <a v-if="site.githubUrl" :href="site.githubUrl" target="_blank" rel="noopener">GitHub</a>
        <a v-if="site.email" :href="mailtoHref">Email</a>
        <a href="/about">About</a>
      </div>
    </div>
  </footer>
</template>
