import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'dear·blog',
  description: '记录技术与生活的交汇处',
  lang: 'zh-CN',
  cleanUrls: true,
  head: [
    ['link', { rel: 'preconnect', href: 'https://fonts.googleapis.com' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' }],
    ['link', {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap'
    }]
  ],
  themeConfig: {},
  vite: {
    server: {
      proxy: {
        '/api': { target: 'http://localhost:8080', changeOrigin: true }
      }
    },
    css: {
      postcss: {
        plugins: [
          // postcss.config.js is auto-detected by Vite
        ]
      }
    }
  }
})
