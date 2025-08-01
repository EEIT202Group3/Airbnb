import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    
    // 主布局:
    {
      path:"/main",
      component: () => import('@/layouts/MainLayout.vue')
    }

    
  ],
})

export default router
