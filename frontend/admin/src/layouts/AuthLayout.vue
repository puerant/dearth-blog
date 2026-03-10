<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo">
        <h1>dear-blog</h1>
        <p>管理后台</p>
      </div>
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.auth-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 48px 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.auth-logo {
  text-align: center;
  margin-bottom: 36px;
}

.auth-logo h1 {
  font-family: var(--app-font-logo);
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: -0.5px;
  margin: 0 0 4px;
}

.auth-logo p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}
</style>
