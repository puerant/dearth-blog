// 构建时从 API 获取所有项目 projectNo 以生成静态页面
// API 不可用时返回占位路由，dev 模式下实际 projectNo 从 URL 读取
export default {
  async paths() {
    try {
      const res = await fetch('http://localhost:8080/api/portal/projects')
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      const records = data?.data ?? []
      if (records.length > 0) {
        return records.map((p) => ({ params: { projectNo: p.projectNo } }))
      }
    } catch (e) {
      console.warn('[portal] project paths() fetch failed:', e?.message ?? e)
    }
    return [{ params: { projectNo: '__init__' } }]
  }
}
