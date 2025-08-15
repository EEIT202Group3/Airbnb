import { createRouter, createWebHistory } from "vue-router";
import CarRentFrontHomepage from "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue";
import CarSelect from "@/components/carRent/frontpageComponent/CarSelect.vue";
import CarDetail from "@/components/carRent/frontpageComponent/CarDetail.vue";
// import List from "../components/listing/List.vue";
// import Add from "../components/listing/AddListing.vue";
// import Detail from "../components/listing/Detail.vue";
// import Edit from "../components/listing/EditListing.vue";
//import getList from "@/layouts/ListLayout.vue";
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
        ],
      },
      {
        path: "hostLogin",
        component: () => import("@/components/user/host/HostLoginPage.vue"),
        name: "HostLogin",
      },
      {
        path: "customerprofile",
        component: () =>
          import("@/components/user/customer/CustomerProfile.vue"),
        name: "CustomerProfile",
      },
      {
        path: "login",
        component: () => import("@/components/user/customer/LoginSignup.vue"),
      },

      {
        path: "/listing/card",
        name: "ListingCard",
        component: () => import("@/components/payment/ListingCard.vue"),
      },
      {
        path: "/booking/start",
        name: "BookingStart",
        component: () => import("@/components/payment/BookingStart.vue"),
      },
      {
        path: "/booking/preview",
        name: "PreviewConfirm",
        component: () => import("@/components/payment/PreviewConfirm.vue"),
      },
      {
        path: "/booking/pay",
        name: "PayRedirect",
        component: () => import("@/components/payment/PayRedirect.vue"),
      },
      {
        path: "/booking/done/:bookingId",
        name: "PaymentDone",
        component: () => import("@/components/payment/PaymentDone.vue"),
        props: true,
      },
      {
        path: "/carrent",
        name: "CarRentFrontHomepage",
        component: () =>
          import(
            "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue"
          ),
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
        component: () =>
          import("@/components/user/customer/ChangePassword.vue"),
        name: "ChangePassword",
      },
    ],
  },
  {
    path: "/host",
    component: () => import("@/layouts/host/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("@/components/user/host/HostPage.vue"),
        name: "HostHomepage",
        redirect: { name: "HostInfo" },
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
        path: "hostprofile",
        component: () => import("@/components/user/host/HostProfile.vue"),
        name: "HostProfile",
      },
      {
        path: "password",
        component: () => import("@/components/user/host/ChangePassword.vue"),
        name: "HostPassword",
      },
    ],
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
