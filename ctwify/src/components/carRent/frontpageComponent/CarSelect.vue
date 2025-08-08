<script setup lang="ts">
import {useRoute} from "vue-router";
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";
import {computed, onMounted, ref} from "vue";
import api from "@/api";

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

const route = useRoute();

const searchParams = {
  location: (route.query.location as string) || "",
  pickupDateTime: (route.query.pickupDateTime as string) || "",
  returnDateTime: (route.query.returnDateTime as string) || "",
  ageChecked: route.query.ageChecked === "true",
};

const originalCarList = ref<Vehicle[]>([]);
const loading = ref(false);
const fetchError = ref<null | string>(null);

const showFilter = ref(false);
const filters = ref({
  brand: "" as string,
  color: "" as string,
  seatCapacity: null as number | null,
  dailyRentMax: null as number | null,
});

const maxDailyRent = computed(() =>
    originalCarList.value.length
        ? Math.max(...originalCarList.value.map((c) => c.dailyRent)) : 0);

onMounted(async () => {
  if (!searchParams.pickupDateTime || !searchParams.returnDateTime) {
    fetchError.value = "缺少時間參數";
    return;
  }
  try {
    loading.value = true;
    const res = await api.get("/reservations/car-select", {
      params: {
        pickupDateTime: searchParams.pickupDateTime,
        returnDateTime: searchParams.returnDateTime,
      },
    });
    originalCarList.value = res.data;
    filters.value.dailyRentMax = maxDailyRent.value || 0;
  } catch (err) {
    fetchError.value = "無法載入車輛資料";
    console.error(err);
  } finally {
    loading.value = false;
  }
});

const uniqueBrands = computed(() => Array.from(new Set(originalCarList.value.map(c => c.brand))));
const uniqueSeats = computed(() => Array.from(new Set(originalCarList.value.map(c => c.seatCapacity))).sort((a, b) => a - b));
const uniqueColors = computed(() => Array.from(new Set(originalCarList.value.map(c => c.color))));

const filteredCars = computed(() =>
    originalCarList.value.filter((car) =>
        (!filters.value.brand || car.brand === filters.value.brand) &&
        (!filters.value.color || car.color === filters.value.color) &&
        (!filters.value.seatCapacity || car.seatCapacity === Number(filters.value.seatCapacity)) &&
        (!filters.value.dailyRentMax || car.dailyRent <= Number(filters.value.dailyRentMax))
    )
);

// 清除篩選結果
function clearFilters() {
  filters.value.brand = "";
  filters.value.color = "";
  filters.value.seatCapacity = null;
  filters.value.dailyRentMax = maxDailyRent.value || 0;
}
</script>

<template>
  <NavigationBar/>
  <SearchBar/>

  <!-- 篩選按鈕 -->
  <div class="container">
    <button class="btn btn-primary my-3" @click="showFilter = true">
      篩選條件
    </button>
  </div>

  <div class="filter-overlay" :class="{ active: showFilter }" @click="showFilter = false">
    <div class="filter-panel" :class="{ active: showFilter }" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">篩選條件</h5>
        <button class="btn btn-sm btn-outline-secondary" @click="showFilter = false">關閉</button>
      </div>

      <div class="mb-2">
        <label class="form-label">品牌</label>
        <select v-model="filters.brand" class="form-select">
          <option value="">全部</option>
          <option v-for="b in uniqueBrands" :key="b" :value="b">{{ b }}</option>
        </select>
      </div>

      <div class="mb-2">
        <label class="form-label">座位數</label>
        <select v-model.number="filters.seatCapacity" class="form-select">
          <option :value="null">全部</option>
          <option v-for="s in uniqueSeats" :key="s" :value="s">{{ s }}</option>
        </select>
      </div>

      <div class="mb-2">
        <label class="form-label">顏色</label>
        <select v-model="filters.color" class="form-select">
          <option value="">全部</option>
          <option v-for="c in uniqueColors" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <div class="mb-3">
        <label class="form-label">最高價格</label>
        <div class="d-flex align-items-center">
          <input
              type="number"
              class="form-control me-2"
              style="width: 140px"
              v-model.number="filters.dailyRentMax"
              :min="0"
              :max="maxDailyRent"
              step="50"
          />
          <span>元</span>
        </div>
        <input
            type="range"
            class="form-range mt-2"
            :min="0"
            :max="maxDailyRent"
            step="50"
            v-model.number="filters.dailyRentMax"
        />
        <small class="text-muted">目前上限：{{ filters.dailyRentMax?.toLocaleString?.() ?? 0 }} 元</small>
      </div>

      <button class="btn btn-outline-secondary w-100" @click="clearFilters">清除篩選條件</button>
    </div>
  </div>

  <!-- 清單區 -->
  <div class="container mt-3">
    <div v-if="loading" class="text-center text-muted py-5">載入中...</div>
    <div v-else-if="fetchError" class="alert alert-danger">{{ fetchError }}</div>
    <template v-else>
      <div v-if="!filteredCars.length" class="alert alert-info">沒有符合條件的車輛</div>

      <div class="row g-4">
        <div class="col-12" v-for="car in filteredCars" :key="car.vehicleId">
          <router-link
              :to="{
              name: 'carDetail',
              params: { id: car.vehicleId },
              query: {
                pickupDateTime: searchParams.pickupDateTime,
                returnDateTime: searchParams.returnDateTime
              }
            }"
              class="text-decoration-none car-card-link"
          >
            <div class="card d-flex flex-row align-items-center justify-content-between p-3 car-card"
                 style="min-height: 150px;">

              <!-- 圖片 -->
              <img
                  :src="`/carPicture/${car.image}`"
                  :alt="car.brand + ' ' + car.model"
                  class="img-fluid rounded"
                  style="width: 180px; height: 120px; object-fit: cover;"
              />
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

              <div class="text-end">
                <div class="text-primary fw-bold fs-5">NT${{ car.dailyRent.toLocaleString() }} /日起</div>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.filter-overlay {
  position: fixed;
  inset: 0;
  background-color: transparent;
  pointer-events: none;
  transition: background-color .25s ease;
  z-index: 1000;
}

.filter-overlay.active {
  background-color: rgba(0, 0, 0, .4);
  pointer-events: auto;
}

.filter-panel {
  background: #fff;
  width: 22rem;
  max-width: 90vw;
  height: 100%;
  transform: translateX(-100%);
  transition: transform .25s ease;
  padding: 20px;
}

.filter-panel.active {
  transform: translateX(0);
}

.car-card-link {
  color: inherit;
}

.car-card {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  transition: box-shadow .2s ease, transform .15s ease;
}

.car-card:hover {
  background-color: #f8f9fa;
  box-shadow: 0 0 10px rgba(0, 0, 0, .15);
  transform: translateY(-2px);
  cursor: pointer;
}
</style>
