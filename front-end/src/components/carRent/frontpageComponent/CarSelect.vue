<script setup lang="ts">
import {useRoute} from "vue-router";
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";
import {carList} from "@/components/carRent/frontpageComponent/carList.ts";

const route = useRoute();

const searchParams = {
  location: route.query.location || '',
  pickupDate: route.query.pickupDate || '',
  pickupTime: route.query.pickupTime || '',
  returnDate: route.query.returnDate || '',
  returnTime: route.query.returnTime || '',
  ageChecked: route.query.ageChecked === 'true'

}
</script>

<template>
  <NavigationBar></NavigationBar>
  <SearchBar></SearchBar>
  <!-- 車輛選擇 -->
  <div class="container mt-5">
    <div class="row g-4">

      <div class="col-12" v-for="car in carList" :key="car.id">
        <div class="card d-flex flex-row align-items-center p-3">
          <router-link
                  :to="{
            name: 'CarDetail',
            params: { id: car.id },
            query: {
              pickupDate: searchParams.pickupDate,
              pickupTime: searchParams.pickupTime,
              returnDate: searchParams.returnDate,
              returnTime: searchParams.returnTime
            }
          }"
                  class="text-decoration-none"
          >
            <img :src="car.img" :alt="car.brand + ' ' + car.model" style="width: 200px;" />
          </router-link>
          
          <div class="card-body">
            <h5 class="card-title">{{ car.brand + ' ' + car.model }}</h5>
            <p class="card-text">{{ car.color + " | " + car.seatCapacity + "人" + car.transmission + "座 | " + "油箱" + car.fuelCapacity + "公升" }}</p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>