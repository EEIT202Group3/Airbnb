<script setup lang="ts">
import {useRoute} from "vue-router";
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";
import axios from "axios";
import {onMounted, ref} from "vue";
import api from "@/api";

const route = useRoute();

interface Vehicle {
  vehicleId: number;
  plateNo: string;
  brand: string;
  model: string;
  color: string;
  fuelType: string;
  transmission: string;
  seatCapacity: number;
  fuelCapacity: number;
  vehicleTax: number;
  dailyRent: number;
  mileage: number;
  latitude: number;
  longitude: number;
  status: string;
  image: string;
}

const carList = ref<Vehicle[]>([]);
const searchParams = {
  location: route.query.location || '',
  pickupDateTime: route.query.pickupDateTime || '',
  returnDateTime: route.query.returnDateTime || '',
  ageChecked: route.query.ageChecked === 'true'
};
onMounted(async () => {
  if (!searchParams.pickupDateTime || !searchParams.returnDateTime) {
    console.error("缺少時間參數");
    return;
  }

  try {
    const res = await api.get("/reservations/car-select", {
      params: {
        pickupDateTime: searchParams.pickupDateTime,
        returnDateTime: searchParams.returnDateTime
      }
    });
    carList.value = res.data;
    console.log(res.data);
  } catch (err) {
    console.error("無法載入車輛資料", err);
  }
});
</script>

<template>
  <NavigationBar></NavigationBar>
  <SearchBar></SearchBar>

  <div class="container mt-4">
    <div class="row g-4">
      <div class="col-9 mx-auto" v-for="car in carList" :key="car.vehicleId">
        <router-link
            :to="{
            name: 'CarDetail',
            params: { id: car.vehicleId },
            query: {
              pickupDateTime: searchParams.pickupDateTime,
              returnDateTime: searchParams.returnDateTime,
            }
          }"
            class="text-decoration-none car-card-link"
        >
          <div
              class="card d-flex flex-row align-items-center justify-content-between p-3 car-card"
              style="min-height: 150px;"
          >
            <!-- 車輛圖片 -->
            <img
                :src="`/carPicture/${car.image}`"
                :alt="car.brand + ' ' + car.model"
                class="img-fluid rounded"
                style="width: 180px; height: 120px; object-fit: cover;"
            />

            <!-- 車輛文字資訊 -->
            <div class="flex-grow-1 ms-4">
              <h5 class="fw-bold text-dark mb-2">
                {{ car.brand.toUpperCase() }} - {{ car.model }}
              </h5>

              <div class="mb-2">
                <span class="badge bg-light text-dark me-2">小型車</span>
                <span class="badge bg-primary me-2">{{ car.seatCapacity }}人座</span>
                <span class="badge bg-info text-dark">{{ car.brand }}</span>
              </div>

              <div class="text-muted">
                <i class="bi bi-person-fill"></i> {{ car.seatCapacity }}人座　
                <i class="bi bi-gear-fill"></i> {{ car.transmission }}　
                <i class="bi bi-droplet-fill"></i> {{ car.fuelCapacity }} 公升
              </div>
            </div>

            <!-- 價格 -->
            <div class="text-end">
              <div class="text-primary fw-bold fs-5">NT${{ car.dailyRent.toLocaleString() }} /日起</div>
              <!-- 如果需要原價可加上這行 -->
              <!-- <div class="text-muted text-decoration-line-through">NT$2700</div> -->
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.car-card-link {
  color: inherit;
}

.car-card {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  transition: box-shadow 0.3s ease, transform 0.2s ease;
}

.car-card:hover {
  background-color: #f8f9fa;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  cursor: pointer;
}
</style>