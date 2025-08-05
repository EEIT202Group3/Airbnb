import {createRouter, createWebHistory} from 'vue-router'

const routes = [
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
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router;