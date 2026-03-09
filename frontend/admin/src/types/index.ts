// ── 通用 ──────────────────────────────────────────────────────────────
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// ── 认证 ──────────────────────────────────────────────────────────────
export interface LoginRequest {
  username: string
  password: string
}

export interface TokenVO {
  token: string
  expiresIn: number
}

// ── 用户 ──────────────────────────────────────────────────────────────
export interface UserVO {
  id: number
  username: string
  nickname: string
  avatar: string
  bio: string
  email: string
  createdAt: string
}

export interface UserUpdateRequest {
  nickname?: string
  avatar?: string
  bio?: string
  email?: string
}

export interface PasswordUpdateRequest {
  oldPassword: string
  newPassword: string
}

// ── 分类 ──────────────────────────────────────────────────────────────
export interface CategoryVO {
  id: number
  name: string
  slug: string
  description: string
  parentId: number
  sortOrder: number
  articleCount: number
  children: CategoryVO[]
}

export interface CategorySaveRequest {
  name: string
  slug: string
  description?: string
  parentId?: number
  sortOrder?: number
}

// ── 标签 ──────────────────────────────────────────────────────────────
export interface TagVO {
  id: number
  name: string
  slug: string
  description: string
  articleCount: number
}

export interface TagSaveRequest {
  name: string
  slug: string
  description?: string
}

// ── 系列 ──────────────────────────────────────────────────────────────
export interface SeriesVO {
  id: number
  name: string
  slug: string
  description: string
  coverImage: string
  sortOrder: number
  articleCount: number
  createdAt: string
}

export interface SeriesSaveRequest {
  name: string
  slug: string
  description?: string
  coverImage?: string
  sortOrder?: number
}

// ── 文章 ──────────────────────────────────────────────────────────────
export interface ArticleListVO {
  id: number
  title: string
  slug: string
  summary: string
  coverImage: string
  categoryId: number
  categoryName: string
  seriesId: number
  isFeatured: boolean
  status: number
  viewCount: number
  publishTime: string
  createdAt: string
  updatedAt: string
  tags: TagVO[]
}

export interface ArticleDetailVO extends ArticleListVO {
  content: string
  contentType: number
  seriesSort: number
  tagIds: number[]
}

export interface ArticleSaveRequest {
  title: string
  slug?: string
  summary?: string
  content: string
  contentType: number
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  seriesId?: number
  seriesSort?: number
  isFeatured?: boolean
  status?: number
  publishTime?: string
}

// ── 媒体 ──────────────────────────────────────────────────────────────
export interface MediaVO {
  id: number
  originalName: string
  accessUrl: string
  mimeType: string
  fileSize: number
  createdAt: string
}

// ── 访问统计 ───────────────────────────────────────────────────────────
export interface VisitSummaryVO {
  totalPv: number
  totalUv: number
  todayPv: number
  todayUv: number
}

export interface VisitTrendVO {
  date: string
  pv: number
  uv: number
}

export interface ArticleRankVO {
  id: number
  title: string
  slug: string
  viewCount: number
}

// ── 项目展示 ──────────────────────────────────────────────────────────
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
  createdAt: string
}

export interface ProjectSaveRequest {
  projectNo: string
  name: string
  shortName: string
  description?: string
  techStack?: string[]
  highlights?: string[]
  startYear?: string
  status?: string
  hue?: number
  githubUrl?: string
  sortOrder?: number
}

// ── 站点配置 ──────────────────────────────────────────────────────────
export type SiteConfig = Record<string, string>
