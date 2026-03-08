import request from './request'
import type { SiteConfig } from '../types'

export const getSiteConfigApi = () => request.get<unknown, SiteConfig>('/api/admin/config')

export const saveSiteConfigApi = (data: SiteConfig) =>
  request.put<unknown, SiteConfig>('/api/admin/config', data)
