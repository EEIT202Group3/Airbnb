import { createRouter, createWebHistory } from 'vue-router'
import List from '../components/listing/List.vue'
import Add from '../components/listing/AddListing.vue'
import Detail from '../components/listing/Detail.vue'
import Edit from '../components/listing/EditListing.vue'

const routes = [
  { path: '/', component: List }, // 建議加這個作為首頁
  { path: '/listing/addListing', component: Add },
  { path: '/listing/detail/:id', component: Detail },
  { path: '/listing/edit/:id', component: Edit },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // 正確傳入已定義好的 routes
})

export default router

