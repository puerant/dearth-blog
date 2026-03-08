import request from './request'
import type { MediaVO, PageResult } from '../types'

export const getMediaListApi = (params: { page?: number; size?: number }) =>
  request.get<unknown, PageResult<MediaVO>>('/api/admin/media', { params })

export const deleteMediaApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/media/${id}`)

// 上传使用 el-upload 的 action 属性直接 POST，这里仅提供 URL
export const MEDIA_UPLOAD_URL = '/api/admin/media/upload'
