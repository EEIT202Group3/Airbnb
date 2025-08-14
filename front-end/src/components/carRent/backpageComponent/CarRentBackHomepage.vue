<script setup lang="ts">
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import {ref, onMounted, onBeforeUnmount, nextTick} from "vue";
import router from "@/router";
import api from "@/api";

// Chart.js（圓餅圖）
import {Chart, DoughnutController, ArcElement, Tooltip, Legend, Title} from "chart.js";

Chart.register(DoughnutController, ArcElement, Tooltip, Legend, Title);

type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

type Reservation = {
  reservationId: number;
  license: string;
  driverPhone: string;
  pickupDate: string;
  returnDate: string;
  status?: string;
  createdAt?: string;
};

type Vehicle = {
  vehicleId: number;
  plateNo: string;
  brand?: string;
  model?: string;
  status?: string;
};

const showSidebar = ref(false);

const mode = ref<"created" | "period">("created");
const kw = ref("");
const fromDate = ref<string>("");
const toDate = ref<string>("");

const resPage = ref<Page<Reservation> | null>(null);
const resLoading = ref(false);
const resError = ref("");
const resPageSize = ref(10);
const resPageNumber = ref(1);
const showResDialog = ref(false);

function normalizeDateRange() {
  if (fromDate.value && toDate.value && fromDate.value > toDate.value) {
    const t = fromDate.value;
    fromDate.value = toDate.value;
    toDate.value = t;
  }
}

async function fetchReservations(page = 1) {
  resLoading.value = true;
  resError.value = "";
  try {
    const params: any = {
      keyword: kw.value?.trim() || "",
      mode: mode.value,
      page: page - 1,
      size: resPageSize.value,
    };
    if (mode.value === "period") {
      normalizeDateRange();
      if (fromDate.value) params.from = fromDate.value;
      if (toDate.value) params.to = toDate.value;
      params.sort = "pickupDate,desc";
    } else {
      params.sort = "createdAt,desc";
    }
    const {data} = await api.get("/reservations/search-eligible", {params});
    resPage.value = data;
    resPageNumber.value = (data?.number ?? 0) + 1;
  } catch (e) {
    resError.value = "查無資料或伺服器錯誤";
    resPage.value = {content: [], totalElements: 0, totalPages: 0, size: resPageSize.value, number: 0};
  } finally {
    resLoading.value = false;
  }
}

function onSearchReservations() {
  if (mode.value !== "period" && !kw.value.trim()) {
    resError.value = "請輸入查詢內容（駕照或電話）";
    resPage.value = null;
    showResDialog.value = true;
    return;
  }
  if (mode.value === "period" && !fromDate.value && !toDate.value && !kw.value.trim()) {
    resError.value = "請提供開始/結束日期";
    resPage.value = null;
    showResDialog.value = true;
    return;
  }
  fetchReservations(1).then(() => (showResDialog.value = true));
}

const plateKw = ref("");
const vehPage = ref<Page<Vehicle> | null>(null);
const vehLoading = ref(false);
const vehError = ref("");
const vehPageSize = ref(10);
const vehPageNumber = ref(1);
const showVehDialog = ref(false);

async function fetchVehicles(page = 1) {
  vehLoading.value = true;
  vehError.value = "";
  try {
    const params: any = {plateNo: plateKw.value?.trim() || "", page: page - 1, size: vehPageSize.value};
    const {data} = await api.get("/vehicles/search-eligible", {params});

    if (data && !("content" in data) && data.vehicleId) {
      vehPage.value = {content: [data], totalElements: 1, totalPages: 1, size: vehPageSize.value, number: 0};
    } else {
      vehPage.value = data;
    }
    vehPageNumber.value = (vehPage.value?.number ?? 0) + 1;
  } catch (e) {
    vehError.value = "查無資料";
    vehPage.value = {content: [], totalElements: 0, totalPages: 0, size: vehPageSize.value, number: 0};
  } finally {
    vehLoading.value = false;
  }
}

function onSearchVehicles() {
  if (!plateKw.value.trim()) {
    vehError.value = "請輸入車牌";
    vehPage.value = null;
    showVehDialog.value = true;
    return;
  }
  fetchVehicles(1).then(() => (showVehDialog.value = true));
}

const reservationSummary = ref<Record<string, number>>({});

onMounted(async () => {
  try {
    const res = await api.get("/reservations/dashboard");
    reservationSummary.value = res.data || {};
  } catch (err) {
    console.error("取得 dashboard 資料失敗：", err);
  }
  loadVehicleStatusChart();
});

const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chartInstance: Chart | null = null;

async function loadVehicleStatusChart() {
  if (!chartCanvas.value) return;
  try {
    const res = await api.get("/vehicles/status-summary");
    const summary = res.data || {};
    const labels = Object.keys(summary);
    const data = Object.values(summary);
    const allZero = !data.length || (data as number[]).every((n) => Number(n) === 0);
    if (chartInstance) chartInstance.destroy();
    chartInstance = new Chart(chartCanvas.value!, {
      type: "doughnut",
      data: allZero
          ? {labels: ["無資料"], datasets: [{data: [1], backgroundColor: ["#e0e0e0"]}]}
          : {labels, datasets: [{data, backgroundColor: ["#198754", "#0d6efd", "#dc3545"], borderWidth: 1}]},
      options: {responsive: true, plugins: {legend: {position: "bottom"}}},
    });
  } catch (err) {
    console.error("載入車輛統計失敗", err);
  }
}

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
});

// 修改日期格式
function formatDateTime(dt?: string) {
  if (!dt) return "—";
  return dt.replace("T", " ").slice(0, 16);
}
</script>

<template>
  <v-container fluid class="py-4 main-content">
    <v-btn variant="outlined" class="ma-2" prepend-icon="mdi-menu" @click="showSidebar = true">功能選單</v-btn>
    <Sidebar :visible="showSidebar" :close="() => (showSidebar = false)"/>

    <v-row class="mt-2" dense>
      <v-col v-for="(value, key, index) in reservationSummary" :key="index" cols="12" sm="6" md="2">
        <v-card elevation="1" class="h-100">
          <v-card-text class="text-center">
            <div class="text-body-2">{{ key }}</div>
            <div class="text-h5 font-weight-medium">{{ value }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4 mb-4" dense>
<!--    預約查詢-->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-text>
            <div class="d-flex align-center justify-space-between mb-2">
              <div class="text-subtitle-2">查詢預約</div>
            </div>

            <v-row dense class="mb-2">
              <v-col cols="12" md="4">
                <v-select
                    :items="[{ title: '依關鍵字', value: 'created' }, { title: '依日期', value: 'period' }]"
                    v-model="mode" density="comfortable" variant="outlined" hide-details label="模式"
                />
              </v-col>

              <template v-if="mode === 'period'">
                <v-col cols="12" md="4">
                  <v-text-field type="date" v-model="fromDate" label="開始日期"
                                density="comfortable" variant="outlined" hide-details/>
                </v-col>
                <v-col cols="12" md="4">
                  <v-text-field type="date" v-model="toDate" label="結束日期"
                                density="comfortable" variant="outlined" hide-details/>
                </v-col>
              </template>

              <v-col :cols="mode === 'period' ? 12 : 6" md="6">
                <v-text-field v-model="kw" density="comfortable" variant="outlined"
                              hide-details label="駕照 / 電話"
                              @keyup.enter="onSearchReservations"/>
              </v-col>

              <v-col cols="12" md="2" class="d-flex">
                <v-btn block color="success" class="text-white" :loading="resLoading" @click="onSearchReservations">
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
            <div class="d-flex justify-space-between align-center mb-2">
              <div class="text-subtitle-2">查詢車輛（車牌）</div>
            </div>

            <v-row dense class="mb-2">
              <v-col cols="12" md="8">
                <v-text-field v-model="plateKw" label="車牌" density="comfortable" variant="outlined"
                              hide-details @keyup.enter="onSearchVehicles"/>
              </v-col>
              <v-col cols="12" md="4" class="d-flex">
                <v-btn block color="primary" :loading="vehLoading" @click="onSearchVehicles">查詢</v-btn>
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
                <td>大型轎車</td>
                <td>100</td>
                <td>80</td>
              </tr>
              <tr>
                <td>中型車</td>
                <td>100</td>
                <td>70</td>
              </tr>
              <tr>
                <td>休旅車</td>
                <td>100</td>
                <td>150</td>
              </tr>
              <tr>
                <td>豪華車</td>
                <td>0</td>
                <td>60</td>
              </tr>
              <tr>
                <td>普通轎車</td>
                <td>0</td>
                <td>130</td>
              </tr>
              <tr>
                <td>小型車</td>
                <td>0</td>
                <td>10</td>
              </tr>
              </tbody>
            </v-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 預約結果視窗 -->
    <v-dialog v-model="showResDialog" max-width="980" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1">預約查詢結果</v-card-title>
        <v-card-text>
          <v-alert v-if="resError" type="error" variant="tonal" density="comfortable" class="mb-2">{{
              resError
            }}
          </v-alert>
          <div v-if="resLoading" class="py-6 text-center">載入中…</div>

          <v-table v-else-if="resPage && resPage.content && resPage.content.length" density="compact"
                   class="text-no-wrap">
            <thead>
            <tr>
              <th>訂單ID</th>
              <th>取還車時間</th>
              <th>駕照</th>
              <th>電話</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="r in resPage.content" :key="r.reservationId">
              <td>{{ r.reservationId }}</td>
              <td>{{ formatDateTime(r.pickupDate) }} ～ {{ formatDateTime(r.returnDate) }}</td>
              <td>{{ r.license }}</td>
              <td>{{ r.driverPhone }}</td>
              <td>{{ r.status }}</td>
              <td>
                <v-btn
                    size="small"
                    color="primary"
                    :to="{ name: 'ReservationDetail', params: { id: String(r.reservationId) } }"
                    @click="showResDialog = false"
                >
                  查看
                </v-btn>
              </td>
            </tr>
            </tbody>
          </v-table>
          <div v-else class="text-grey text-caption">尚無資料</div>

          <div class="d-flex justify-end mt-2" v-if="resPage && resPage.totalPages > 1">
            <v-pagination v-model="resPageNumber" :length="resPage.totalPages" density="comfortable"
                          @update:modelValue="(p:number) => fetchReservations(p)"/>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer/>
          <v-btn variant="text" @click="showResDialog = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 車輛結果彈窗 -->
    <v-dialog v-model="showVehDialog" max-width="900" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1">車牌查詢結果</v-card-title>
        <v-card-text>
          <v-alert v-if="vehError" type="error" variant="tonal" density="comfortable" class="mb-2">{{
              vehError
            }}
          </v-alert>
          <div v-if="vehLoading" class="py-6 text-center">載入中…</div>

          <v-table v-else-if="vehPage && vehPage.content && vehPage.content.length" density="compact"
                   class="text-no-wrap">
            <thead>
            <tr>
              <th>車輛ID</th>
              <th>車牌</th>
              <th>車型</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="v in vehPage.content" :key="v.vehicleId">
              <td>{{ v.vehicleId }}</td>
              <td>{{ v.plateNo }}</td>
              <td>{{ v.brand }} {{ v.model }}</td>
              <td>{{ v.status }}</td>
              <td>
                <v-btn
                    size="small"
                    color="primary"
                    :to="{ name: 'VehicleDetail', params: { id: String(v.vehicleId) } }"
                    @click="showVehDialog = false"
                >
                  查看
                </v-btn>
              </td>
            </tr>
            </tbody>
          </v-table>
          <div v-else class="text-grey text-caption">尚無資料</div>

          <div class="d-flex justify-end mt-2" v-if="vehPage && vehPage.totalPages > 1">
            <v-pagination v-model="vehPageNumber" :length="vehPage.totalPages" density="comfortable"
                          @update:modelValue="(p:number) => fetchVehicles(p)"/>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer/>
          <v-btn variant="text" @click="showVehDialog = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
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
