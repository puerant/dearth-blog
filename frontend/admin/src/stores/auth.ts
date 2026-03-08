import { defineStore } from 'pinia'
import { loginApi, logoutApi } from '../api/auth'
import { getUserMeApi } from '../api/user'
import type { UserVO, LoginRequest } from '../types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userInfo = ref<UserVO | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(req: LoginRequest) {
    const data = await loginApi(req)
    token.value = data.token
    localStorage.setItem('token', data.token)
    await fetchUserInfo()
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = null
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  async function fetchUserInfo() {
    try {
      userInfo.value = await getUserMeApi()
    } catch {
      // ignore
    }
  }

  return { token, userInfo, isAuthenticated, login, logout, fetchUserInfo }
})
