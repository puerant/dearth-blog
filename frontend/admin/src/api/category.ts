import request from './request'
import type { CategoryVO, CategorySaveRequest } from '../types'

export const getCategoriesApi = () =>
  request.get<unknown, CategoryVO[]>('/api/admin/category')

export const createCategoryApi = (data: CategorySaveRequest) =>
  request.post<unknown, CategoryVO>('/api/admin/category', data)

export const updateCategoryApi = (id: number, data: CategorySaveRequest) =>
  request.put<unknown, CategoryVO>(`/api/admin/category/${id}`, data)

export const deleteCategoryApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/category/${id}`)
