// ── Portal API 类型定义 ──

export interface SiteConfig {
  site_name: string
  site_tagline: string
  site_since: string
  site_location: string
  github_url: string
  email: string
  about_content: string
  skills: string[]
}

export interface CategoryVO {
  id: number
  name: string
  slug: string
  description: string
  parentId: number
  articleCount: number
  children?: CategoryVO[]
}

export interface TagVO {
  id: number
  name: string
  slug: string
  articleCount: number
}

export interface SeriesVO {
  id: number
  name: string
  slug: string
  description: string
  coverImage: string
  articleCount: number
  sortOrder: number
}

export interface PortalArticleListVO {
  id: number
  title: string
  slug: string
  summary: string
  coverImage: string
  contentType: number      // 1=Markdown 2=RichText
  categoryId: number
  categoryName: string
  categorySlug: string
  seriesId: number
  seriesName: string
  seriesSort: number
  isFeatured: boolean
  status: number
  publishTime: string
  viewCount: number
  readingTime: number
  tags: TagVO[]
}

export interface PortalArticleDetailVO extends PortalArticleListVO {
  content: string
  prevArticle: { id: number; title: string; slug: string } | null
  nextArticle: { id: number; title: string; slug: string } | null
}

export interface PortalHomeVO {
  featuredArticles: PortalArticleListVO[]
  recentArticles: PortalArticleListVO[]
  siteConfig: Record<string, string>
  totalArticles: number
  totalCategories: number
  totalTags: number
  totalSeries: number
  projects: ProjectVO[]
}

export interface ProjectVO {
  id: number
  projectNo: string
  name: string
  shortName: string
  description: string
  techStack: string[]
  highlights: string[]
  startYear: string
  status: string
  hue: number
  githubUrl: string
  sortOrder: number
}

export interface ArchiveItemVO {
  year: number
  months: {
    month: number
    articles: {
      id: number
      title: string
      slug: string
      publishTime: string
      categoryName: string
    }[]
  }[]
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface SeriesDetailVO extends SeriesVO {
  articles: PortalArticleListVO[]
}
