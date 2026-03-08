import request from './request'
import type { ArticleListVO, ArticleDetailVO, ArticleSaveRequest, PageResult } from '../types'

export interface ArticleListParams {
  page?: number
  size?: number
  title?: string
  status?: number | null
  categoryId?: number | null
  tagSlug?: string
}

export const getArticlesApi = (params: ArticleListParams) =>
  request.get<unknown, PageResult<ArticleListVO>>('/api/admin/article', { params })

export const getArticleApi = (id: number) =>
  request.get<unknown, ArticleDetailVO>(`/api/admin/article/${id}`)

export const createArticleApi = (data: ArticleSaveRequest) =>
  request.post<unknown, ArticleDetailVO>('/api/admin/article', data)

export const updateArticleApi = (id: number, data: ArticleSaveRequest) =>
  request.put<unknown, ArticleDetailVO>(`/api/admin/article/${id}`, data)

export const deleteArticleApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/article/${id}`)

export const updateArticleStatusApi = (id: number, status: number) =>
  request.put<unknown, void>(`/api/admin/article/${id}/status`, { status })
