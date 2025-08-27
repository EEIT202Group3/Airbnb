<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

axios.defaults.baseURL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const route = useRoute();
const router = useRouter();

const detail = ref({});
const loading = ref(false);
const error = ref("");

function toTWNumber(n) {
  const num = typeof n === "string" ? Number(n) : n;
  return new Intl.NumberFormat("zh-TW").format(num || 0);
}
function formatAmount(n) {
  return toTWNumber(n);
}
function formatDateTime(d) {
  if (!d) return "—";
  const dt = new Date(d);
  return isNaN(dt) ? d : dt.toLocaleString("zh-TW");
}

async function fetchAll() {
  loading.value = true;
  error.value = "";

  try {
    const rid = route.query.rid; // ← 用 query
    if (!rid) throw new Error("缺少查詢參數 rid");

    const { data: r } = await axios.get(`/reservations1/${rid}`);

    const vhPromise = r?.vehicleId
      ? axios.get(`/vehicles1/${r.vehicleId}`)
      : null;
    const locPromise = r?.locationId
      ? axios.get(`/locations/${r.locationId}`)
      : null;

    const [vhRes, locRes] = await Promise.all(
      [vhPromise, locPromise].map((p) => p ?? Promise.resolve(null))
    );

    const v = vhRes?.data ?? null;
    const loc = locRes?.data ?? null;

    detail.value = {
      reservationId: r.reservationId,
      pickupDateTime: r.pickupDate,
      returnDateTime: r.returnDate,
      totalAmount: r.totalAmount,

      driverName: r.driverName,
      driverPhone: r.driverPhone,

      vehicleBrand: v?.brand ?? null,
      vehicleModel: v?.model ?? null,
      vehicleColor: v?.color ?? null,

      locationName: loc?.name ?? null,
      locationAddr: loc?.address ?? null,
      businessHours: loc?.businessHours ?? null,
    };
  } catch (e) {
    console.error(e);
    error.value = e?.response?.data?.message || e.message || "載入失敗";
  } finally {
    loading.value = false;
  }
}
function goBack() {
  if (window.history.length > 1) router.back();
  else router.push({ name: "OrderList" });
}

onMounted(fetchAll);
</script>

<template>
  <!-- 切換按鈕 -->
  <v-container class="py-6" max-width="1000">
    <!-- 頁首：返回鍵 + 標題 -->
    <div class="header-row">
      <v-btn
        variant="text"
        color="deep-orange-darken-1"
        prepend-icon="mdi-arrow-left"
        class="mr-2"
        @click="goBack"
      >
        返回
      </v-btn>
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-car</v-icon
      >
      <h2 class="page-title">車輛資訊</h2>
    </div>
    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
      border="start"
      color="red-darken-1"
      icon="mdi-alert-octagon-outline"
    >
      {{ error }}
    </v-alert>

    <v-card class="soft-card mb-4" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3">mdi-car</v-icon>
        <span class="card-title">車輛資訊</span>
      </v-card-title>

      <v-divider class="mx-4" />

      <v-card-text v-if="loading" class="py-6">
        <v-skeleton-loader type="article, actions" />
      </v-card-text>

      <v-card-text v-else class="py-4">
        <v-row>
          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-identifier</v-icon></template
                >
                <v-list-item-title class="kv">
                  租車訂單：<span class="value">{{
                    detail.reservationId || "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-calendar-start</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  取車時間：<span class="value">{{
                    formatDateTime(detail.pickupDateTime)
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-calendar-end</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  還車時間：<span class="value">{{
                    formatDateTime(detail.returnDateTime)
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-store-marker</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  取還車地點：<span class="value">{{
                    detail.locationName || "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item v-if="detail.locationAddr">
                <template #prepend
                  ><v-icon color="deep-orange">mdi-map-marker</v-icon></template
                >
                <v-list-item-title class="kv">
                  門市地址：<span class="value">{{ detail.locationAddr }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item v-if="detail.businessHours">
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-clock-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  營業時間：<span class="value">{{
                    detail.businessHours
                  }}</span>
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-account</v-icon></template
                >
                <v-list-item-title class="kv">
                  駕駛人姓名：<span class="value">{{
                    detail.driverName ?? "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-phone</v-icon></template
                >
                <v-list-item-title class="kv">
                  駕駛人電話：<span class="value">{{
                    detail.driverPhone ?? "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-label</v-icon></template
                >
                <v-list-item-title class="kv">
                  車輛品牌：<span class="value">{{
                    detail.vehicleBrand ?? "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-car-info</v-icon></template
                >
                <v-list-item-title class="kv">
                  車輛型號：<span class="value">{{
                    detail.vehicleModel ?? "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-palette</v-icon></template
                >
                <v-list-item-title class="kv">
                  車輛顏色：<span class="value">{{
                    detail.vehicleColor ?? "—"
                  }}</span>
                </v-list-item-title>
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-cash-multiple</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  車輛總金額：<span class="value"
                    >NT$ {{ formatAmount(detail.totalAmount) }}</span
                  >
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style scoped>
.soft-card {
  background: #fff7ed;
}
.header-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #c2410c;
  margin: 0;
}
.card-title {
  font-size: 25px;
  font-weight: 700;
  color: #7c2d12;
}
.flat-list {
  --v-list-padding-start: 0;
  --v-list-padding-end: 0;
}
.kv {
  font-size: 20px;
}
.kv .value {
  font-weight: 700;
  color: #7c2d12;
  word-break: break-all;
}
</style>