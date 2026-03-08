import request from './request'
import type { TagVO, TagSaveRequest } from '../types'

export const getTagsApi = () => request.get<unknown, TagVO[]>('/api/admin/tag')

export const createTagApi = (data: TagSaveRequest) =>
  request.post<unknown, TagVO>('/api/admin/tag', data)

export const updateTagApi = (id: number, data: TagSaveRequest) =>
  request.put<unknown, TagVO>(`/api/admin/tag/${id}`, data)

export const deleteTagApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/tag/${id}`)
