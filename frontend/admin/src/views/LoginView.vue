<template>
  <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
    <el-form-item prop="username">
      <el-input
        v-model="form.username"
        placeholder="用户名"
        size="large"
        prefix-icon="User"
        autocomplete="username"
      />
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        v-model="form.password"
        type="password"
        placeholder="密码"
        size="large"
        prefix-icon="Lock"
        show-password
        autocomplete="current-password"
        @keyup.enter="handleLogin"
      />
    </el-form-item>
    <el-form-item>
      <el-button
        type="primary"
        size="large"
        style="width: 100%"
        :loading="loading"
        native-type="submit"
        @click="handleLogin"
      >
        登录
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { useRouter, useRoute } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form)
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } finally {
    loading.value = false
  }
}
</script>
