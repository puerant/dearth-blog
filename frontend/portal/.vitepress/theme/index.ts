import type { Theme } from 'vitepress'
import { createPinia } from 'pinia'
import Layout from './Layout.vue'
import './style.css'

export default {
  Layout,
  enhanceApp({ app }) {
    app.use(createPinia())
  }
} satisfies Theme
