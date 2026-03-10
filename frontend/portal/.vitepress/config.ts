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
    plugins: [
      {
        // Dev 模式下：将 /projects/<any-projectNo> 的 HTML 请求重写到占位路由
        name: 'portal-project-dev-fallback',
        configureServer(server) {
          server.middlewares.use((req, _res, next) => {
            if (req.url && /^\/projects\/[^/?#.]+(?:\/)?(?:\?.*)?$/.test(req.url)) {
              req.url = '/projects/__init__'
            }
            next()
          })
        }
      },
      {
        // Dev 模式下：VitePress 客户端 SPA 导航到 /projects/<real-no> 时
        // 返回重导出 [projectNo].md 组件并注入正确 params 的虚拟模块
        name: 'portal-project-virtual-no',
        apply: 'serve',
        resolveId(source: string) {
          const path = source.split('?')[0]
          const m = path.match(/^(?:\/)?projects\/([^/\[?#]+)\.md$/)
          if (!m || m[1] === '__init__') return
          return `\0vp-proj:${m[1]}`
        },
        load(id: string) {
          if (!id.startsWith('\0vp-proj:')) return
          const projectNo = id.slice('\0vp-proj:'.length)
          const pageData = JSON.stringify({
            title: '',
            description: '',
            frontmatter: { layout: false },
            headers: [],
            relativePath: `projects/${projectNo}.md`,
            filePath: `projects/${projectNo}.md`,
            params: { projectNo }
          })
          return [
            `import Comp from '/projects/[projectNo].md'`,
            `export default Comp`,
            `export const __pageData = ${pageData}`,
          ].join('\n')
        }
      },
      {
        // 处理直接访问 / 刷新页面的场景
        name: 'portal-article-dev-fallback',
        configureServer(server) {
          server.middlewares.use((req, _res, next) => {
            if (req.url && /^\/articles\/[^/?#.]+(?:\/)?(?:\?.*)?$/.test(req.url)) {
              req.url = '/articles/__init__'
            }
            next()
          })
        }
      },
      {
        // Dev 模式下：处理 VitePress 客户端 SPA 导航的 404 问题
        // 当 paths() 在后端未就绪时只注册了 __init__，VitePress 客户端路由导航到
        // /articles/<real-slug> 时会尝试 import articles/<real-slug>.md（不存在）
        // 此插件拦截这些模块请求，返回重导出 [slug].md 组件并注入正确 params 的虚拟模块
        name: 'portal-article-virtual-slug',
        apply: 'serve',
        resolveId(source: string) {
          const path = source.split('?')[0]
          const m = path.match(/^(?:\/)?articles\/([^/\[?#]+)\.md$/)
          if (!m || m[1] === '__init__') return
          return `\0vp-art:${m[1]}`
        },
        load(id: string) {
          if (!id.startsWith('\0vp-art:')) return
          const slug = id.slice('\0vp-art:'.length)
          const pageData = JSON.stringify({
            title: '',
            description: '',
            frontmatter: { layout: false },
            headers: [],
            relativePath: `articles/${slug}.md`,
            filePath: `articles/${slug}.md`,
            params: { slug }
          })
          return [
            `import Comp from '/articles/[slug].md'`,
            `export default Comp`,
            `export const __pageData = ${pageData}`,
          ].join('\n')
        }
      }
    ],
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
