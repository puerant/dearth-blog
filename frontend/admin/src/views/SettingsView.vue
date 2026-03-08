<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab">
      <!-- 账号信息 -->
      <el-tab-pane label="账号信息" name="profile">
        <div class="tab-form">
          <el-form ref="profileFormRef" :model="profileForm" label-width="90px" style="max-width: 480px">
            <el-form-item label="用户名">
              <el-input :value="authStore.userInfo?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="头像 URL">
              <el-input v-model="profileForm.avatar" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <div class="tab-form">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px" style="max-width: 400px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingPwd" @click="savePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 站点配置 -->
      <el-tab-pane label="站点配置" name="site">
        <div class="tab-form">
          <el-form label-width="120px" style="max-width: 560px">
            <el-form-item v-for="(val, key) in siteConfig" :key="key" :label="key">
              <el-input v-model="siteConfig[key]" />
            </el-form-item>
            <el-form-item v-if="Object.keys(siteConfig).length">
              <el-button type="primary" :loading="savingSite" @click="saveSite">保存配置</el-button>
            </el-form-item>
            <el-empty v-if="!Object.keys(siteConfig).length && !loadingSite" description="暂无配置项" />
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { updateUserMeApi, updatePasswordApi } from '../api/user'
import { getSiteConfigApi, saveSiteConfigApi } from '../api/config'

const authStore = useAuthStore()
const activeTab = ref('profile')

// ── 账号信息 ──────────────────────────────────────────────────────────
const profileFormRef = ref<FormInstance>()
const savingProfile = ref(false)
const profileForm = reactive({
  nickname: authStore.userInfo?.nickname ?? '',
  email: authStore.userInfo?.email ?? '',
  avatar: authStore.userInfo?.avatar ?? '',
  bio: authStore.userInfo?.bio ?? '',
})

watch(() => authStore.userInfo, (info) => {
  if (info) Object.assign(profileForm, { nickname: info.nickname, email: info.email, avatar: info.avatar, bio: info.bio })
}, { immediate: true })

async function saveProfile() {
  savingProfile.value = true
  try {
    const updated = await updateUserMeApi(profileForm)
    authStore.userInfo = updated
    ElMessage.success('保存成功')
  } finally {
    savingProfile.value = false
  }
}

// ── 修改密码 ──────────────────────────────────────────────────────────
const pwdFormRef = ref<FormInstance>()
const savingPwd = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, val, cb) => {
        if (val !== pwdForm.newPassword) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur',
    },
  ],
}

async function savePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingPwd.value = true
  try {
    await updatePasswordApi({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } finally {
    savingPwd.value = false
  }
}

// ── 站点配置 ──────────────────────────────────────────────────────────
const siteConfig = ref<Record<string, string>>({})
const savingSite = ref(false)
const loadingSite = ref(false)

async function loadSite() {
  loadingSite.value = true
  try { siteConfig.value = await getSiteConfigApi() } finally { loadingSite.value = false }
}

async function saveSite() {
  savingSite.value = true
  try {
    siteConfig.value = await saveSiteConfigApi(siteConfig.value)
    ElMessage.success('配置已保存')
  } finally {
    savingSite.value = false
  }
}

onMounted(loadSite)
</script>

<style scoped>
.settings-page {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
}

.tab-form {
  padding: 20px 0;
}
</style>
