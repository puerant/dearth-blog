// 构建时从 API 获取所有文章 slug 以生成静态页面
// API 不可用时返回占位路由，dev 模式下实际 slug 从 URL 读取
export default {
  async paths() {
    try {
      const res = await fetch('http://localhost:8080/api/portal/articles?size=1000&page=1')
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      const records = data?.data?.records ?? []
      if (records.length > 0) {
        return records.map((a) => ({ params: { slug: a.slug } }))
      }
    } catch (e) {
      console.warn('[portal] paths() fetch failed:', e?.message ?? e)
    }
    // 占位符：确保 VitePress 始终注册 /articles/[slug] 路由模式
    // dev 模式下 Vite middleware 会将任意 /articles/<slug> 重写到此路径
    return [{ params: { slug: '__init__' } }]
  }
}
