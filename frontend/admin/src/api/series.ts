import request from './request'
import type { SeriesVO, SeriesSaveRequest } from '../types'

export const getSeriesListApi = () =>
  request.get<unknown, SeriesVO[]>('/api/admin/series')

export const createSeriesApi = (data: SeriesSaveRequest) =>
  request.post<unknown, SeriesVO>('/api/admin/series', data)

export const updateSeriesApi = (id: number, data: SeriesSaveRequest) =>
  request.put<unknown, SeriesVO>(`/api/admin/series/${id}`, data)

export const deleteSeriesApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/series/${id}`)
