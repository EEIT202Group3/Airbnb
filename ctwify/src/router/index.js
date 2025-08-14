import { createRouter, createWebHistory } from "vue-router";
import CarRentFrontHomepage from "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue";
import CarSelect from "@/components/carRent/frontpageComponent/CarSelect.vue";
import CarDetail from "@/components/carRent/frontpageComponent/CarDetail.vue";
// import List from "../components/listing/List.vue";
// import Add from "../components/listing/AddListing.vue";
// import Detail from "../components/listing/Detail.vue";
// import Edit from "../components/listing/EditListing.vue";

//import getList from "@/layouts/ListLayout.vue";

import ListingPage from '../components/payment/ListingPage.vue';
import BookingPage from '../components/payment/BookingPage.vue';
import ConfirmationPage from '../components/payment/ConfirmationPage.vue';
import Ecpay from '../components/payment/Ecpay.vue';
import PaymentPage from '../components/payment/PaymentPage.vue';
import PaymentSuccessPage from '../components/payment/PaymentSuccessPage.vue';

const routes = [
  {
    path: '/',
    component: () => import("@/layouts/MainLayout.vue"),
    children: [
      {
        path: '/customer',
        component: () => import("@/components/user/customer/CustomerPage.vue"),
        children: [
          {
            path: '',
            component: () => import('@/components/user/customer/CustomerInfo.vue'),
            name: 'CustomerInfo',
          },
          {
            path: '/history',
            component: () => import('@/components/user/customer/TripHistory.vue'),
            name: 'TripHistory',
          },
        ]
      },
      {
        path: '/login',
        component: () => import('@/components/user/LoginSignup.vue')
      },
      {
        path: '/car-front-homepage',
        name: 'carFrontHomepage',
        component: CarRentFrontHomepage
      },
      {
        path: '/car-select',
        name: 'carSelect',
        component: CarSelect
      },
      {
        path: '/car-detail/:id',
        name: 'carDetail',
        component: CarDetail,
        props: true
      }
    ]
  }

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
