import { createRouter, createWebHistory } from "vue-router";
import CarRentFrontHomepage from "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue";
import CarSelect from "@/components/carRent/frontpageComponent/CarSelect.vue";
import CarDetail from "@/components/carRent/frontpageComponent/CarDetail.vue";


const routes = [
  {
    path: '/',
    component: () => import("@/layouts/MainLayout.vue"),
    name: 'Homepage',
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
        path: '/login',
        component: () => import('@/components/user/customer/LoginSignup.vue'),
      },

      {
        path:'/homepage',
        component: () => import('@/components/listing/customer/ListingCard.vue'),
      },

      {
        path: '/coustomerlistings/:id',
         component: () =>import('@/components/listing/customer/CustomerDetail.vue'),
      },

    
      {
        path: '/addlistings',
        component: () => import('@/components/listing/host/AddListing2.vue')

      },


      {
        path:'/customerdatil/:id',
        component: () => import('@/components/user/customer/LoginSignup.vue')
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
        // 測試從房源抓資料用
        path: "/booking/start1",
        name: "BookingStart1",
        component: () => import("@/components/payment/BookingStart1.vue"),
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
        // 測試從房源抓資料用
        path: "/orders1",
        name: "OrderList1",
        component: () => import("@/components/payment/OrderList1.vue"),
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
        // 測試從房源抓資料用
          path: '/test/listing-preview', component: () => import('@/components/payment/TestListingPreview.vue')
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

  {
    path: '/homepage',
    component: () => import('@/components/listing/customer/ListingCard.vue'),
  },

  {
    path: '/customerdatil/:id',
    component: () => import('@/components/listing/customer/CustomerDetail.vue'),
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
  }]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // 正確傳入已定義好的 routes
});

export default router;
