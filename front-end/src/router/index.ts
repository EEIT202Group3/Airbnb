import { createRouter, createWebHistory } from 'vue-router'
import { components } from 'vuetify/dist/vuetify.js';

const routes = [
    {
        path: '/',
        component: () => import('@/layouts/layout.vue'),
        children: [
            {
                path: '',
                name: 'Homepage',
                component: () => import('@/components/users/Homepage.vue')
            },
            {
                path: 'customers',
                name: 'CustomerList',
                component: () => import('@/components/users/CustomerList.vue')
            },
            {
                path: 'hosts',
                component: () => import('@/components/users/HostList.vue')
            },


            {
                path: 'addEquipment',
                component: () => import ('@/components/listing/addEquipment.vue')

            }

            {
                path: "reviews/list",
                component: () => import('@/components/reviews/views/ReviewView.vue')
            },
            {
                path: "reviews/insert",
                component: () => import('@/components/reviews/components/IReview.vue')
            },
              
        ]
    },
    {
        path: '/car-rent/front-homepage',
        component: () => import('@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue'),
        children: [
            {
                path: 'car-select',
                name: 'CarSelect',
                component: () => import('@/components/carRent/frontpageComponent/CarSelect.vue')
            },
            {
                path: 'car-detail/:id',
                name: 'CarDetail',
                component: () => import('@/components/carRent/frontpageComponent/CarDetail.vue'),
                props: true
            }
        ]
    },
    {
        path: '/car-rent/back-homepage',
        component: () => import('@/components/carRent/backpageComponent/CarRentBackHomepage.vue'),
        children: [
            {
                path: 'reservations/:id',
                name: 'CarRentReservationDetail',
                component: () => import('@/components/carRent/backpageComponent/CarRentReservation.vue'),
                props: true
            },
            {
                path: 'vehicles/:id',
                name: 'VehicleDetail',
                component: () => import('@/components/carRent/backpageComponent/Vehicle.vue'),
                props: true
            }
        ]
    },
    {
        path: '/AdminOrder',
        component: () => import('@/layouts/layout.vue'),
        children: [
            {
                path: '',
                name: 'AdminOrderAll',
                component: () => import('@/components/payMent/backpageComponent/AdminOrder.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router;