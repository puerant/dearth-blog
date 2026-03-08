import request from './request'
import type { UserVO, UserUpdateRequest, PasswordUpdateRequest } from '../types'

export const getUserMeApi = () => request.get<unknown, UserVO>('/api/admin/user/me')

export const updateUserMeApi = (data: UserUpdateRequest) =>
  request.put<unknown, UserVO>('/api/admin/user/me', data)

export const updatePasswordApi = (data: PasswordUpdateRequest) =>
  request.put<unknown, void>('/api/admin/user/password', data)
