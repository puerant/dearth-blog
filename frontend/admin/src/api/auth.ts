import request from './request'
import type { LoginRequest, TokenVO } from '../types'

export const loginApi = (data: LoginRequest) =>
  request.post<unknown, TokenVO>('/api/auth/login', data)

export const logoutApi = () => request.post<unknown, void>('/api/auth/logout')
