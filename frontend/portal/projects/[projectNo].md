---
layout: false
---

<script setup>
import { useData } from 'vitepress'
import { ref, computed, onMounted } from 'vue'
import ProjectDetailPage from '../pages/ProjectDetailPage.vue'

const { params } = useData()

// urlNo 在 onMounted 中从浏览器 URL 提取（SSG 水合后 / dev 直接访问均可用）
const urlNo = ref('')

// computed 保证 SPA 导航时 params 更新能实时传入子组件
const projectNo = computed(() => {
  const p = params.value?.projectNo
  if (p && p !== '__init__') return p
  return urlNo.value
})

onMounted(() => {
  const m = window.location.pathname.match(/\/projects\/([^/?#]+)/)
  if (m && m[1] !== '__init__') urlNo.value = m[1]
})
</script>

<ProjectDetailPage :projectNo="projectNo" />
