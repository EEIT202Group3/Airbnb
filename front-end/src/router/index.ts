import { createRouter, createWebHistory } from 'vue-router'

import CarRentFrontHomepage from "@/components/carRent/frontpageComponent/CarRentFrontHomepage.vue";
import CarSelect from "@/components/carRent/frontpageComponent/CarSelect.vue";
import CarDetail from "@/components/carRent/frontpageComponent/CarDetail.vue";
import CarRentBackHomepage from "@/components/carRent/backpageComponent/CarRentBackHomepage.vue";
import CarRentReservation from "@/components/carRent/backpageComponent/CarRentReservation.vue";
import Vehicle from "@/components/carRent/backpageComponent/Vehicle.vue";


const routes = [
  {
    path: '/car-rent',
    name: 'CarRentFrontHomepage',
    component: CarRentFrontHomepage
  },
  {
    path: '/car-rent/car-select',
    name: 'CarSelect',
    component: CarSelect
  },
  {
    path: '/car-rent/car-detail',
    name: 'CarDetail',
    component: CarDetail,
    props: true
  },
  {
    path: '/car-rent/back-homepage',
    name: 'CarRentBackHomepage',
    component:CarRentBackHomepage
  },
  {
    path: "/car-rent/reservations/:id",
    name: "CarRentReservationDetail",
    component: CarRentReservation,
    props: true
  },
  {
    path: "/car-rent/vehicles/:id",
    name: "VehicleDetail",
    component: Vehicle,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
export default router;