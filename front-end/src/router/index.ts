import { createRouter, createWebHistory } from "vue-router";
import { components } from "vuetify/dist/vuetify.js";

const routes = [
  {
    path: "/",
    component: () => import("@/layouts/layout.vue"),
    children: [
      {
        path: "chat",
        component: () => import("@/components/chat/Chat.vue"),
      },
      {
        path: "",
        name: "Homepage",
        component: () => import("@/components/users/Homepage.vue"),
      },
      {
        path: "customers",
        name: "CustomerList",
        component: () => import("@/components/users/CustomerList.vue"),
      },
      {
        path: "hosts",
        component: () => import("@/components/users/HostList.vue"),
      },

      {
        path: "addEquipment",
        component: () => import("@/components/listing/addEquipment.vue"),
      },
      {
        path: "approveListing",
        component: () => import("@/components/listing/ApproveListing.vue"),
      },

      {
        path: "reviews/list",
        component: () => import("@/components/reviews/views/ReviewView.vue"),
      },
      {
        path: "reviews/insert",
        component: () => import("@/components/reviews/components/IReview.vue"),
      },
      {
        path: "car-rent/back-homepage",
        component: () =>
          import(
            "@/components/carRent/backpageComponent/CarRentBackHomepage.vue"
          ),
      },
      {
        path: "car-rent/reservations/:id",
        component: () =>
          import(
            "@/components/carRent/backpageComponent/CarRentReservation.vue"
          ),
        props: true,
      },
      {
        path: "car-rent/vehicles/:id",
        component: () =>
          import("@/components/carRent/backpageComponent/Vehicle.vue"),
        props: true,
      },
    ],
  },
  {
    path: "/AdminOrder",
    component: () => import("@/layouts/layout.vue"),
    children: [
      {
        path: "",
        name: "AdminOrderAll",
        component: () =>
          import("@/components/payMent/backpageComponent/AdminOrder.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});
export default router;
