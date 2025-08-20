import { createRouter, createWebHistory } from "vue-router";
import CarRentFrontHomepage from "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue";
import CarSelect from "@/components/carRent/frontpageComponent/CarSelect.vue";
import CarDetail from "@/components/carRent/frontpageComponent/CarDetail.vue";


const routes = [
    //客戶首頁
    {
        path: '/',
        component: () => import("@/layouts/MainLayout.vue"),
        name: 'Homepage',
        redirect: { name: 'HomeCard' },
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

            //房東登入介面，接續之後的房東首頁
            {
                path: "hostLogin",
                component: () => import("@/components/user/host/HostLoginPage.vue"),
                name: "HostLogin",
            },
        ],
      },
      {
        path: '/login',
        component: () => import('@/components/user/customer/LoginSignup.vue'),
      },


      //首頁
      {
        path:'/homepage',
        component: () => import('@/components/listing/customer/ListingCard.vue'),
      },

      //租客房源詳細頁面
      {
        path: '/coustomerlistings/:id',
         component: () =>import('@/components/listing/customer/CustomerDetail.vue'),
      },

     // 新增房源
      {
        path: '/addlistings',
        component: () => import('@/components/listing/host/AddListing2.vue'),

      },

      {
         path: '/Edlistings/:id',
        component: () => import('@/components/listing/host/EditListing.vue'),


      },

      {
         path: '/search',
        component: () => import('@/components/listing/customer/SearchResults.vue'),


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
            //放房東頁面
            // {
            //     path: '/addlistings',
            //     component: () => import('@/components/listing/host/AddListing2.vue')

            // },
        ],
    },

    //租車首頁
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

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes, // 正確傳入已定義好的 routes
});

export default router;