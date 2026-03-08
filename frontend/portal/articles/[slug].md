---
layout: false
---

<script setup>
import { useData } from 'vitepress'
import ArticleDetailPage from '../pages/ArticleDetailPage.vue'

const { params } = useData()
</script>

<ArticleDetailPage :slug="params.slug" />
