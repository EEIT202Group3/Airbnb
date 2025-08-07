import { createRouter, createWebHistory } from "vue-router";
import List from "../components/listing/List.vue";
import Add from "../components/listing/AddListing.vue";
import Detail from "../components/listing/Detail.vue";
import Edit from "../components/listing/EditListing.vue";

import getList from "@/layouts/ListLayout.vue";

const routes = [
  {
    path: "/main",
    component: () => import("@/layouts/MainLayout.vue"),
    children: [
      {
        path: "list",
        component: () => import("@/views/MainView.vue"),
      },
      {
        path: "getList/:id?",
        component: (id) => import(`@/views/ListView.vue`),
      },
    ],
  },
  {
    path: "/getList",
    component: () => import("@/layouts/ListLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/ListView.vue"),
      },
    ],
  },
  { path: "/", component: List }, // 建議加這個作為首頁
  { path: "/listing/addListing", component: Add },
  { path: "/listing/detail/:id", component: Detail },
  { path: "/listing/edit/:id", component: Edit },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // 正確傳入已定義好的 routes
});

export default router;
