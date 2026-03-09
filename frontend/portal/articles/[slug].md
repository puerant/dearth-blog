---
layout: false
---

<script setup>
import { useData } from 'vitepress'
import { ref, onMounted } from 'vue'
import ArticleDetailPage from '../pages/ArticleDetailPage.vue'

const { params } = useData()

// SSG 构建时：params.slug 是真实 slug，直接使用
// Dev 模式：Vite middleware 将所有 /articles/* 重写到 /articles/__init__，
//           params.slug = '__init__'，需要从浏览器 URL 读取真实 slug
const slug = ref(
  params.value?.slug && params.value.slug !== '__init__'
    ? params.value.slug
    : ''
)

onMounted(() => {
  if (!slug.value) {
    // 从实际 URL 中提取 slug（浏览器端始终可用）
    slug.value = window.location.pathname
      .replace(/^\/articles\//, '')
      .replace(/\/$/, '')
  }
})
</script>

<ArticleDetailPage :slug="slug" />
