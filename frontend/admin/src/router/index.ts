import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('../layouts/AuthLayout.vue'),
      children: [
        {
          path: '',
          name: 'Login',
          component: () => import('../views/LoginView.vue'),
        },
      ],
    },
    {
      path: '/',
      component: () => import('../layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/DashboardView.vue'),
        },
        {
          path: 'articles',
          name: 'ArticleList',
          component: () => import('../views/article/ArticleListView.vue'),
        },
        {
          path: 'articles/new',
          name: 'ArticleNew',
          component: () => import('../views/article/ArticleEditView.vue'),
        },
        {
          path: 'articles/:id/edit',
          name: 'ArticleEdit',
          component: () => import('../views/article/ArticleEditView.vue'),
        },
        {
          path: 'categories',
          name: 'Category',
          component: () => import('../views/CategoryView.vue'),
        },
        {
          path: 'tags',
          name: 'Tag',
          component: () => import('../views/TagView.vue'),
        },
        {
          path: 'series',
          name: 'Series',
          component: () => import('../views/SeriesView.vue'),
        },
        {
          path: 'media',
          name: 'Media',
          component: () => import('../views/MediaView.vue'),
        },
        {
          path: 'visit',
          name: 'Visit',
          component: () => import('../views/VisitView.vue'),
        },
        {
          path: 'projects',
          name: 'Project',
          component: () => import('../views/ProjectView.vue'),
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('../views/SettingsView.vue'),
        },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

// 路由守卫：未登录跳转到 /login
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'Login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'Login' && token) {
    return { path: '/dashboard' }
  }
})

export default router
