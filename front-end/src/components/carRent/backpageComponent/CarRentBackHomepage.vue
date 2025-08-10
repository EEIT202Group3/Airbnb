<script setup lang="ts">
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import router from "@/router";
import api from "@/api";

import {
  Chart,
  DoughnutController,
  ArcElement,
  Tooltip,
  Legend,
  Title
} from "chart.js";
Chart.register(DoughnutController, ArcElement, Tooltip, Legend, Title);

const route = useRoute();
const showSidebar = ref(false);
const type = ref("license");
const query = ref("");
const errorMess = ref("");
const errorMess2 = ref("");
const reservation = ref(null);
const plateNo = ref("");
const vehicle = ref(null);

const searchReservation = async () => {
  errorMess.value = "";
  reservation.value = null;
  if (!query.value.trim()) {
    errorMess.value = "請輸入查詢內容";
    return;
  }
  try {
    const res = await api.get("reservations/search", {
      params: { type: type.value, query: query.value.trim() }
    });
    reservation.value = res.data;
    await router.push(`/car-rent/reservations/${res.data.reservationId}`);
  } catch {
    errorMess.value = "查無資料";
  }
};

const searchVehicle = async () => {
  errorMess2.value = "";
  vehicle.value = null;
  if (!plateNo.value) {
    errorMess2.value = "請輸入查詢內容";
    return;
  }
  try {
    const res = await api.get("/vehicles/search", { params: { plateNo: plateNo.value } });
    await router.push(`/car-rent/vehicles/${res.data.vehicleId}`);
  } catch {
    errorMess2.value = "查無資料";
  }
};

// 預約表儀錶板
const reservationSummary = ref<Record<string, number>>({});
onMounted(async () => {
  try {
    const res = await api.get("/reservations/dashboard");
    reservationSummary.value = res.data;
  } catch (err) {
    console.error("取得 dashboard 資料失敗：", err);
  }
});

// 車輛圓餅圖
const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chartInstance: Chart | null = null;

const loadVehicleStatusChart = async () => {
  try {
    const res = await api.get("/vehicles/status-summary");
    const summary = res.data;

    const labels = Object.keys(summary);
    const data = Object.values(summary);

    if (chartInstance) chartInstance.destroy();

    chartInstance = new Chart(chartCanvas.value!, {
      type: "doughnut",
      data: {
        labels,
        datasets: [
          {
            label: "車輛狀態",
            data,
            backgroundColor: ["#198754", "#0d6efd", "#dc3545"],
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        plugins: { legend: { position: "bottom" } }
      }
    });
  } catch (err) {
    console.error("載入車輛統計失敗", err);
  }
};

onMounted(() => { loadVehicleStatusChart(); });

watch(() => route.path, (newPath) => {
  if (newPath === "/car-rent/back-homepage") loadVehicleStatusChart();
});
</script>

<template>
  <div v-if="route.path === '/car-rent/back-homepage'">
    <v-container fluid class="px-6 py-4 main-content">
      <v-btn
          variant="outlined"
          class="ma-2"
          prepend-icon="mdi-menu"
          @click="showSidebar = true"
      >
        功能選單
      </v-btn>
      <Sidebar :visible="showSidebar" :close="() => (showSidebar = false)" />

      <v-row class="mt-2" dense>
        <v-col
            v-for="(value, key, index) in reservationSummary"
            :key="index"
            cols="12" sm="6" md="2"
        >
          <v-card elevation="1" class="h-100">
            <v-card-text class="text-center">
              <div class="text-body-2">{{ key }}</div>
              <div class="text-h5 font-weight-medium">{{ value }}</div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-4 mb-4" dense>
        <v-col cols="12" md="6">
          <v-card>
            <v-card-text>
              <v-alert
                  v-if="errorMess"
                  type="error"
                  variant="tonal"
                  density="compact"
                  class="mb-2"
              >
                {{ errorMess }}
              </v-alert>

              <div class="d-flex align-center justify-space-between mb-2">
                <div class="text-subtitle-2">查詢預約</div>
              </div>

              <v-row dense>
                <v-col cols="12" md="4">
                  <v-select
                      :items="[
                      { title: '駕照號碼', value: 'license' },
                      { title: '電話號碼', value: 'phone' }
                    ]"
                      v-model="type"
                      density="comfortable"
                      variant="outlined"
                      hide-details
                      label="查詢類型"
                  />
                </v-col>
                <v-col cols="12" md="5">
                  <v-text-field
                      v-model="query"
                      density="comfortable"
                      variant="outlined"
                      hide-details
                      label="請輸入對應內容"
                      @keyup.enter="searchReservation"
                  />
                </v-col>
                <v-col cols="12" md="3" class="d-flex">
                  <v-btn
                      block
                      color="success"
                      class="text-white"
                      @click="searchReservation"
                  >
                    查詢
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card>
            <v-card-text>
              <v-alert
                  v-if="errorMess2"
                  type="error"
                  variant="tonal"
                  density="compact"
                  class="mb-2"
              >
                {{ errorMess2 }}
              </v-alert>

              <div class="text-subtitle-2 mb-2">查詢車輛</div>

              <v-row dense>
                <v-col cols="12" md="9">
                  <v-text-field
                      v-model="plateNo"
                      density="comfortable"
                      variant="outlined"
                      hide-details
                      label="車牌號碼"
                      @keyup.enter="searchVehicle"
                  />
                </v-col>
                <v-col cols="12" md="3" class="d-flex">
                  <v-btn block color="success" @click="searchVehicle">
                    查詢
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row dense>
        <v-col cols="12" md="6">
          <v-card>
            <v-card-text>
              <div class="d-flex justify-space-between align-center mb-2">
                <div class="text-subtitle-2">車輛資訊</div>
              </div>

              <div class="d-flex justify-center">
                <v-responsive max-width="260">
                  <canvas ref="chartCanvas"></canvas>
                </v-responsive>
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card>
            <v-card-text>
              <div class="text-subtitle-2 mb-2">車型租金一覽表</div>

              <v-table density="compact" class="text-no-wrap">
                <thead>
                <tr>
                  <th>車型</th>
                  <th>免費里程數</th>
                  <th>日租金</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td>大型轎車</td><td>100</td><td>80</td>
                </tr>
                <tr>
                  <td>中型車</td><td>100</td><td>70</td>
                </tr>
                <tr>
                  <td>休旅車</td><td>100</td><td>150</td>
                </tr>
                <tr>
                  <td>豪華車</td><td>0</td><td>60</td>
                </tr>
                <tr>
                  <td>普通轎車</td><td>0</td><td>130</td>
                </tr>
                <tr>
                  <td>小型車</td><td>0</td><td>10</td>
                </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>

  <div v-else>
    <router-view />
  </div>
</template>

<style scoped>
.main-content {
  margin: auto;
  width: calc(100% - 200px);
}
canvas {
  width: 100% !important;
  height: auto !important;
}
</style>
