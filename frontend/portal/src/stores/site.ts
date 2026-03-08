import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { portalApi } from '../api/portal'
import type { PortalHomeVO } from '../types'

export const useSiteStore = defineStore('site', () => {
  const homeData = ref<PortalHomeVO | null>(null)
  const loading = ref(false)

  const config = computed(() => homeData.value?.siteConfig ?? {})
  const siteName = computed(() => config.value['site_name'] || 'dear·blog')
  const tagline = computed(() => config.value['site_tagline'] || '')
  const since = computed(() => config.value['site_since'] || '2024')
  const location = computed(() => config.value['site_location'] || '')
  const githubUrl = computed(() => config.value['github_url'] || '')
  const email = computed(() => config.value['email'] || '')

  async function fetchConfig() {
    if (homeData.value) return
    loading.value = true
    try {
      homeData.value = await portalApi.getHome()
    } catch (e) {
      console.warn('Failed to load site config:', e)
    } finally {
      loading.value = false
    }
  }

  return { homeData, loading, config, siteName, tagline, since, location, githubUrl, email, fetchConfig }
})
