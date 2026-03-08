// 构建时尝试从 API 获取所有文章 slug 以生成静态页面
// API 不可用时返回空数组（运行时 CSR 仍可正常访问）
export default {
  async paths() {
    try {
      const res = await fetch('http://localhost:8080/api/portal/articles?size=1000&page=1')
      const data = await res.json()
      const records = data?.data?.records ?? []
      return records.map((a) => ({ params: { slug: a.slug } }))
    } catch {
      return []
    }
  }
}
