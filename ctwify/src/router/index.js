import { createRouter, createWebHistory } from "vue-router";

const routes = [
  //客戶首頁
  {
    path: "/",
    component: () => import("@/layouts/MainLayout.vue"),
    name: "Homepage",
    redirect: { name: "HomeCard" },
    children: [
      //主畫面 + 修維的部分
      {
        path: "",
        component: () =>
          import("@/components/listing/customer/ListingCard.vue"),
        name: "HomeCard",
      },
      {
        path: "/coustomerlistings/:id",
        component: () =>
          import("@/components/listing/customer/CustomerDetail.vue"),
      },

      {
        path: "/search",
        component: () =>
          import("@/components/listing/customer/SearchResults.vue"),
      },

      {
        path: "/search",
        component: () =>
          import("@/components/listing/customer/SearchResults.vue"),
      },

      //修維的API串上之後可砍
      {
        path: "/listing/card",
        name: "ListingCard",
        component: () => import("@/components/payment/ListingCard.vue"),
      },
      //宜臻之後的部分，不能砍
      {
        path: "/booking/start",
        name: "BookingStart",
        component: () => import("@/components/payment/BookingStart.vue"),
      },
      {
        path: "/preview-confirm",
        name: "PreviewConfirm",
        component: () => import("@/components/payment/PreviewConfirm.vue"),
      },
      {
        path: "/payment/paypal",
        name: "PayPalPayment",
        component: () => import("@/components/payment/PayPalPayment.vue"),
        props: (route) => ({
          bookingId: route.query.bookingId,
        }),
      },
      {
        path: "/payment/redirect",
        name: "PayRedirect",
        component: () => import("@/components/payment/PayRedirect.vue"),
      },
      {
        path: "/payment/done",
        name: "PaymentDone",
        component: () => import("@/components/payment/PaymentDone.vue"),
        props: true,
      },
      {
        path: "/order/list",
        name: "OrderList",
        component: () => import("@/components/payment/OrderList.vue"),
      },

      //諺廷的部分
      {
        path: "/login",
        component: () => import("@/components/user/customer/LoginSignup.vue"),
      },
      {
        path: "/customer",
        component: () => import("@/components/user/customer/CustomerPage.vue"),
        children: [
            //主畫面 + 修維的部分
            {
                path: '',
                component: () => import("@/components/listing/customer/ListingCard.vue"),
                name: 'HomeCard',
            },
            {
                path: '/coustomerlistings/:id',
                component: () => import('@/components/listing/customer/CustomerDetail.vue'),
            },

            {
                path: '/search',
                component: () => import('@/components/listing/customer/SearchResults.vue'),

            },

            {
                path: '/search',
                component: () => import('@/components/listing/customer/SearchResults.vue'),
            },

            //修維的API串上之後可砍
            {
                path: "/listing/card",
                name: "ListingCard",
                component: () => import("@/components/payment/ListingCard.vue"),
            },
            //宜臻之後的部分，不能砍
            {
                path: "/booking/start",
                name: "BookingStart",
                component: () => import("@/components/payment/BookingStart.vue"),
            },
            {
                path: "/preview-confirm",
                name: "PreviewConfirm",
                component: () => import("@/components/payment/PreviewConfirm.vue"),
            },
            {
                path: '/payment/paypal',
                name: 'PayPalPayment',
                component: () => import("@/components/payment/PayPalPayment.vue"),
                props: route => ({
                    bookingId: route.query.bookingId
                })
            },
            {
                path: "/payment/redirect",
                name: "PayRedirect",
                component: () => import("@/components/payment/PayRedirect.vue"),
            },
            {
                path: "/payment/done",
                name: "PaymentDone",
                component: () => import("@/components/payment/PaymentDone.vue"),
                props: true,
            },
            {
                path: '/order/list',
                name: 'OrderList',
                component: () => import('@/components/payment/OrderList.vue'),
            },

            //諺廷的部分
            {

                path: '/login',
                component: () => import('@/components/user/customer/LoginSignup.vue'),
            },
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
                path: "customerprofile",
                component: () =>
                    import("@/components/user/customer/CustomerProfile.vue"),
                name: "CustomerProfile",

            },
            {
                path: "password",
                component: () =>
                    import("@/components/user/customer/ChangePassword.vue"),
                name: "ChangePassword",
            },

            //致融的部分
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
        ],
      },
      {
        path: "customerprofile",
        component: () =>
          import("@/components/user/customer/CustomerProfile.vue"),
        name: "CustomerProfile",
      },
      {
        path: "password",
        component: () =>
          import("@/components/user/customer/ChangePassword.vue"),
        name: "ChangePassword",
      },

      //致融的部分
      {
        path: "/reviews",
        component: () => import("@/components/reviews/ReviewView.vue"),
        name: "Reviews",
      },
      {
        path: "reviews/insert",
        name: "InsertReview",
        component: () =>
          import("@/components/reviews/customer/InsertReview.vue"),
      },

      //房東登入介面，接續之後的房東首頁
      {
        path: "hostLogin",
        component: () => import("@/components/user/host/HostLoginPage.vue"),
        name: "HostLogin",
      },
    ],
  },

  //房東首頁
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
          {
            path: "review",
            component: () => import("@/components/user/host/HostReview.vue"),
            name: "HostReview",
          },
        ],
    },

    //租車首頁
    {
        path: '/car-front-homepage',
        name: 'carFrontHomepage',
        component: () => import('@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue')
    },

    {
        path: '/car-select',
        name: 'carSelect',
        component: () => import('@/components/carRent/frontpageComponent/CarSelect.vue')
    },

    {
        path: '/car-detail/:id',
        name: 'carDetail',
        component: () => import('@/components/carRent/frontpageComponent/CarDetail.vue'),
        props: true
    },
    {
        path: '/car-payment-result',
        name: 'CarPaymentResult',
        component: () => import('@/components/carRent/frontpageComponent/CarPaymentResult.vue')
    }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes, // 正確傳入已定義好的 routes
});

export default router;
