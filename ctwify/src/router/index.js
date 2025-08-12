import { createRouter, createWebHistory } from "vue-router";
// import List from "../components/listing/List.vue";
// import Add from "../components/listing/AddListing.vue";
// import Detail from "../components/listing/Detail.vue";
// import Edit from "../components/listing/EditListing.vue";

//import getList from "@/layouts/ListLayout.vue";

import ListingPage from "../components/payment/ListingPage.vue";
import BookingPage from "../components/payment/BookingPage.vue";
import ConfirmationPage from "../components/payment/ConfirmationPage.vue";
// import Ecpay from '../components/payment/Ecpay.vue';
import PaymentPage from "../components/payment/PaymentPage.vue";
import PaymentSuccessPage from "../components/payment/PaymentSuccessPage.vue";
import { compile } from "vue";

const routes = [
  {
    path: "/",
    component: () => import("@/layouts/MainLayout.vue"),
    name: "Homepage",
    children: [
      {
        path: "customer",
        component: () => import("@/components/user/customer/CustomerPage.vue"),
        children: [
          {
            path: "",
            component: () =>
              import("@/components/user/customer/CustomerInfo.vue"),
            name: "CustomerInfo",
          },
          {
            path: "history",
            component: () =>
              import("@/components/user/customer/TripHistory.vue"),
            name: "TripHistory",
          },
        ],
      },
      {
        path: "host",
        component: () => import("@/components/user/host/HostPage.vue"),
        children: [
          {
            path: "",
            component: () => import("@/components/user/host/HostInfo.vue"),
            name: "HostInfo",
          },
          {
            path: "listing",
            component: () => import("@/components/user/host/HostListing.vue"),
            name: "HostListing",
          },
          {
            path: "order",
            component: () => import("@/components/user/host/HostOrder.vue"),
            name: "HostOrder",
          },
        ],
      },
      {
        path: "customerprofile",
        component: () =>
          import("@/components/user/customer/CustomerProfile.vue"),
        name: "CustomerProfile",
      },
      {
        path: "hostprofile",
        component: () => import("@/components/user/host/HostProfile.vue"),
        name: "HostProfile",
      },
      {
        path: "login",
        component: () => import("@/components/user/LoginSignup.vue"),
      },
      {
        path: "/reviews",
        component: () => import("@/components/reviews/ReviewView.vue"),
        name: "Reviews",
      },
      {
        path: "reviews/insert",
        component: () =>
          import("@/components/reviews/customer/InsertReview.vue"),
      },
      {
        path: "password",
        component: () => import("@/components/user/ChangePassword.vue"),
        name: "ChangePassword",
      },
    ],
  },

  // {
  //   path: "/main",
  //   component: () => import("@/layouts/MainLayout.vue"),
  //   children: [
  //     {
  //       path: "list",
  //       component: () => import("@/views/MainView.vue"),
  //     },
  //     {
  //       path: "getList/:id?",
  //       component: (id) => import(`@/views/ListView.vue`),
  //     },
  //   ],
  // },
  // {
  //   path: "/getList",
  //   component: () => import("@/layouts/ListLayout.vue"),
  //   children: [
  //     {
  //       path: "",
  //       component: () => import("@/views/ListView.vue"),
  //     },
  //   ],
  // },
  // { path: "/", component: List }, // 建議加這個作為首頁
  // { path: "/listing/addListing", component: Add },
  // { path: "/listing/detail/:id", component: Detail },
  // { path: "/listing/edit/:id", component: Edit },

  // { path: '/', component: ListingPage, name: 'listing' },
  // { path: '/booking', component: BookingPage, name: 'booking' },
  // { path: '/confirmation', component: ConfirmationPage, name: 'confirmation' },
  // { path: '/ecpay', component: Ecpay, name: 'ecpay' },
  // { path: '/payment', component: PaymentPage, name: 'payment' },
  // { path: '/payment-success', component: PaymentSuccessPage, name: 'paymentSuccess' },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // 正確傳入已定義好的 routes
});

export default router;
