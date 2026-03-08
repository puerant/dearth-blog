<script setup lang="ts">
import { useRoute } from 'vitepress'
import NavBar from '../../src/components/NavBar.vue'
import SiteFooter from '../../src/components/SiteFooter.vue'
import { useSiteStore } from '../../src/stores/site'
import { onMounted } from 'vue'

const route = useRoute()
const siteStore = useSiteStore()

onMounted(() => {
  siteStore.fetchConfig()
})
</script>

<template>
  <NavBar />
  <Content />
  <SiteFooter />
</template>
