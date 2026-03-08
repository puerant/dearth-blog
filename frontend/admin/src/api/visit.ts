import request from './request'
import type { VisitSummaryVO, VisitTrendVO, ArticleRankVO } from '../types'

export const getVisitSummaryApi = () =>
  request.get<unknown, VisitSummaryVO>('/api/admin/visit/summary')

export const getVisitTrendApi = (days = 30) =>
  request.get<unknown, VisitTrendVO[]>('/api/admin/visit/trend', { params: { days } })

export const getArticleRankApi = (limit = 10) =>
  request.get<unknown, ArticleRankVO[]>('/api/admin/visit/article-rank', { params: { limit } })
