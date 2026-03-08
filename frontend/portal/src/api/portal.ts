import axios from 'axios'
import type {
  PortalHomeVO,
  PortalArticleListVO,
  PortalArticleDetailVO,
  CategoryVO,
  TagVO,
  SeriesVO,
  SeriesDetailVO,
  ArchiveItemVO,
  PageResult
} from '../types'

const request = axios.create({
  baseURL: '/api/portal',
  timeout: 10000
})

// 统一解包 Result<T>
request.interceptors.response.use(
  (res) => res.data?.data ?? res.data,
  (err) => Promise.reject(err)
)

export const portalApi = {
  /** 首页聚合数据 */
  getHome(): Promise<PortalHomeVO> {
    return request.get('/home')
  },

  /** 文章列表（分页） */
  listArticles(params?: {
    page?: number
    size?: number
    categorySlug?: string
    tagSlug?: string
    keyword?: string
  }): Promise<PageResult<PortalArticleListVO>> {
    return request.get('/articles', { params })
  },

  /** 文章详情（按 slug） */
  getArticle(slug: string): Promise<PortalArticleDetailVO> {
    return request.get(`/articles/${slug}`)
  },

  /** 分类列表（树形） */
  listCategories(): Promise<CategoryVO[]> {
    return request.get('/categories')
  },

  /** 标签列表 */
  listTags(): Promise<TagVO[]> {
    return request.get('/tags')
  },

  /** 系列列表 */
  listSeries(): Promise<SeriesVO[]> {
    return request.get('/series')
  },

  /** 系列详情（含文章列表） */
  getSeries(slug: string): Promise<SeriesDetailVO> {
    return request.get(`/series/${slug}`)
  },

  /** 时间归档 */
  getArchive(): Promise<ArchiveItemVO[]> {
    return request.get('/archive')
  },

  /** 记录访问（静默） */
  recordVisit(path: string, articleId?: number) {
    return request.post('/visit', { path, articleId }).catch(() => {})
  }
}
