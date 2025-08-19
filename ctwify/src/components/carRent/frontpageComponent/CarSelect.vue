<script setup lang="ts">
import {useRoute} from "vue-router";
import SearchBar from "@/components/carRent/frontpageComponent/SearchBar.vue";
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
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
  locationId: (route.query.locationId as string) || "",
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

const maxDailyRent = computed(() => {
  if (!originalCarList.value.length) return 0;
  return Math.max(...originalCarList.value.map((c) => Number(c.dailyRent) || 0));
});

const brandItems = computed(() => {
  const map = new Map<string, string>();
  for (const c of originalCarList.value) {
    const raw = String(c.brand ?? "").trim();
    const key = raw.toLowerCase();
    if (raw && !map.has(key)) map.set(key, raw);
  }
  return [{title: "全部", value: ""}, ...Array.from(map.values()).map((b) => ({title: b, value: b}))];
});

const seatItems = computed(() => {
  const set = new Set<number>();
  for (const c of originalCarList.value) {
    const n = Number((c as any).seatCapacity);
    if (!Number.isNaN(n)) set.add(n);
  }
  return [{title: "全部", value: null}, ...Array.from(set).sort((a, b) => a - b).map((n) => ({
    title: String(n),
    value: n
  }))];
});

const colorItems = computed(() => {
  const map = new Map<string, string>();
  for (const c of originalCarList.value) {
    const raw = String(c.color ?? "").trim();
    const key = raw.toLowerCase();
    if (raw && !map.has(key)) map.set(key, raw);
  }
  return [{title: "全部", value: ""}, ...Array.from(map.values()).map((col) => ({title: col, value: col}))];
});

// --- 請求資料 ---
onMounted(async () => {
  if (!searchParams.pickupDateTime || !searchParams.returnDateTime) {
    fetchError.value = "缺少時間參數";
    return;
  }
  try {
    loading.value = true;
    const res = await api.get("/reservations1/car-select", {
      params: {
        pickupDateTime: searchParams.pickupDateTime,
        returnDateTime: searchParams.returnDateTime,
      },
    });
    originalCarList.value = (res.data ?? []).map((c: any) => ({
      ...c,
      brand: String(c.brand ?? "").trim(),
      color: String(c.color ?? "").trim(),
      seatCapacity: Number(c.seatCapacity),
      dailyRent: Number(c.dailyRent),
    }));
    filters.value.dailyRentMax = maxDailyRent.value || 0;
  } catch (err) {
    fetchError.value = "無法載入車輛資料";
    console.error(err);
  } finally {
    loading.value = false;
  }
});

// --- 篩選 ---
const filteredCars = computed(() =>
    originalCarList.value.filter((car) => {
      const matchBrand = !filters.value.brand || car.brand === filters.value.brand;
      const matchColor = !filters.value.color || car.color === filters.value.color;
      const matchSeat =
          !filters.value.seatCapacity || Number(car.seatCapacity) === Number(filters.value.seatCapacity);
      const matchPrice =
          !filters.value.dailyRentMax || Number(car.dailyRent) <= Number(filters.value.dailyRentMax);
      return matchBrand && matchColor && matchSeat && matchPrice;
    })
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
  <div class="page-wrap">
    <SearchBar />

    <!-- 篩選按鈕 -->
    <v-container>
      <v-btn color="orange-darken-2" class="my-3 text-white" @click="showFilter = true" prepend-icon="mdi-filter-variant">
        篩選條件
      </v-btn>
    </v-container>

    <!-- 左側篩選抽屜 -->
    <v-navigation-drawer v-model="showFilter" location="left" temporary width="320">
      <v-toolbar flat color="orange-darken-2" title="篩選條件" class="text-white">
        <v-spacer />
        <v-btn icon="mdi-close" variant="text" @click="showFilter = false" color="white"/>
      </v-toolbar>

      <v-divider />

      <v-container>
        <!-- 品牌 -->
        <v-select
            v-model="filters.brand"
            :items="brandItems"
            item-title="title"
            item-value="value"
            label="品牌"
            density="comfortable"
            variant="outlined"
            clearable
            color="orange-darken-2"
        />

        <!-- 座位數 -->
        <v-select
            v-model.number="filters.seatCapacity"
            :items="seatItems"
            item-title="title"
            item-value="value"
            label="座位數"
            density="comfortable"
            variant="outlined"
            clearable
            color="orange-darken-2"
        />

        <!-- 顏色 -->
        <v-select
            v-model="filters.color"
            :items="colorItems"
            item-title="title"
            item-value="value"
            label="顏色"
            density="comfortable"
            variant="outlined"
            clearable
            color="orange-darken-2"
        />

        <!-- 最高價格 -->
        <div class="mt-4">
          <div class="text-subtitle-2 mb-2">最高價格（元）</div>
          <div class="d-flex align-center mb-2">
            <v-text-field
                v-model.number="filters.dailyRentMax"
                type="number"
                hide-details
                density="comfortable"
                class="mr-2"
                style="max-width: 160px"
                :min="0"
                :max="maxDailyRent"
                step="50"
                variant="outlined"
                color="orange-darken-2"
            />
            <span>元</span>
          </div>
          <v-slider
              v-model.number="filters.dailyRentMax"
              :min="0"
              :max="maxDailyRent"
              step="50"
              hide-details
              color="orange-darken-2"
          />
          <div class="text-caption text-medium-emphasis mt-1">
            目前上限：{{ filters.dailyRentMax?.toLocaleString?.() ?? 0 }} 元
          </div>
        </div>

        <v-btn class="mt-4" variant="outlined" block color="orange-darken-2" @click="clearFilters">
          清除篩選條件
        </v-btn>
      </v-container>
    </v-navigation-drawer>

    <!-- 清單區 -->
    <v-container class="mt-3">
      <div v-if="loading" class="text-center py-10">
        <v-progress-circular indeterminate color="orange-darken-2"/>
        <div class="mt-2 text-medium-emphasis">載入中...</div>
      </div>

      <v-alert v-else-if="fetchError" type="error" variant="tonal" class="mb-4">
        {{ fetchError }}
      </v-alert>

      <template v-else>
        <v-alert v-if="!filteredCars.length" type="info" variant="tonal" color="orange-darken-2" class="mb-4">
          沒有符合條件的車輛
        </v-alert>

        <v-row dense>
          <v-col v-for="car in filteredCars" :key="car.vehicleId" cols="12" class="mb-3">
            <RouterLink
                :to="{
                name: 'carDetail',
                params: { id: car.vehicleId },
                query: {
                  locationId: searchParams.locationId,
                  pickupDateTime: searchParams.pickupDateTime,
                  returnDateTime: searchParams.returnDateTime
                }
              }"
                class="text-decoration-none"
            >
              <v-card class="pa-3" elevation="2" rounded="lg" hover>
                <div class="d-flex align-center justify-space-between" style="min-height: 150px;">
                  <!-- 圖片 -->
                  <v-img
                      :src="`http://localhost:8080/carPicture/${car.image}`"
                      :alt="car.brand + ' ' + car.model"
                      width="180"
                      height="120"
                      cover
                      class="rounded-lg"
                  />

                  <!-- 文字資訊 -->
                  <div class="mx-4 flex-grow-1">
                    <div class="text-h6 font-weight-bold mb-2">
                      {{ car.brand.toUpperCase() }} - {{ car.model }}
                    </div>

                    <div class="mb-2">
                      <v-chip size="small" variant="outlined" class="mr-2">小型車</v-chip>
                      <v-chip size="small" color="orange-darken-2" class="mr-2 text-white">{{ car.seatCapacity }}人座</v-chip>
                      <v-chip size="small" variant="tonal" color="orange-lighten-3">{{ car.brand }}</v-chip>
                    </div>

                    <div class="text-medium-emphasis">
                      <v-icon size="18" class="mr-1">mdi-account</v-icon>
                      {{ car.seatCapacity }}人座　
                      <v-icon size="18" class="mr-1">mdi-automatic</v-icon>
                      {{ car.transmission }}　
                      <v-icon size="18" class="mr-1">mdi-gas-station</v-icon>
                      {{ car.fuelCapacity }} 公升
                    </div>
                  </div>

                  <!-- 價格 -->
                  <div class="text-right">
                    <div class="text-orange-darken-2 text-h6 font-weight-bold">
                      NT${{ car.dailyRent.toLocaleString() }} /日起
                    </div>
                  </div>
                </div>
              </v-card>
            </RouterLink>
          </v-col>
        </v-row>
      </template>
    </v-container>
  </div>
</template>

<style scoped>
.page-wrap {
  width: 70%;
  margin: 0 auto;
}
.text-right {
  text-align: right;
}
</style>