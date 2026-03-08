import request from './request'
import type { ProjectVO, ProjectSaveRequest } from '../types'

export const getProjectsApi = () =>
  request.get<unknown, ProjectVO[]>('/api/admin/project')

export const createProjectApi = (data: ProjectSaveRequest) =>
  request.post<unknown, ProjectVO>('/api/admin/project', data)

export const updateProjectApi = (id: number, data: ProjectSaveRequest) =>
  request.put<unknown, ProjectVO>(`/api/admin/project/${id}`, data)

export const deleteProjectApi = (id: number) =>
  request.delete<unknown, void>(`/api/admin/project/${id}`)
