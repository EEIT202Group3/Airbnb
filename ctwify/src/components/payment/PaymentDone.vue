<template>
  <v-container class="py-6" max-width="1000">
    <!-- Title -->
    <div class="header-row">
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-credit-card-check-outline</v-icon
      >
      <h2 class="page-title">付款結果</h2>
    </div>

    <!-- 載入中 -->
    <v-card v-if="loading" class="soft-card mb-4" elevation="2" rounded="xl">
      <v-card-text class="text-center py-8">
        <v-progress-circular
          indeterminate
          color="deep-orange-darken-1"
          size="64"
        />
        <p class="mt-4 loading-text">正在確認付款狀態...</p>
      </v-card-text>
    </v-card>

    <!-- 錯誤 -->
    <v-alert
      v-if="error && !loading"
      type="error"
      variant="tonal"
      border="start"
      color="red-darken-1"
      icon="mdi-alert-octagon-outline"
      class="mb-4"
    >
      <div class="font-weight-bold">處理過程中發生錯誤</div>
      <div class="mt-2">{{ error }}</div>
    </v-alert>

    <!-- 狀態條 -->
    <v-alert
      v-if="detail && !loading"
      :type="isPaid() ? 'success' : isFailed() ? 'error' : 'warning'"
      variant="tonal"
      border="start"
      class="mb-4"
    >
      <div class="d-flex align-center">
        <v-icon
          class="mr-2"
          :color="isPaid() ? 'green' : isFailed() ? 'red' : 'orange'"
        >
          {{
            isPaid()
              ? "mdi-check-decagram"
              : isFailed()
              ? "mdi-close-octagon"
              : "mdi-timer-sand"
          }}
        </v-icon>
        <div class="font-weight-bold mr-3">
          {{ getStatusText(detail.mentStatus || detail.mentstatus) }}
        </div>
        <v-chip
          size="small"
          v-if="detail.bookingMethod || detail.bookingmethod"
          class="ml-auto"
          color="deep-orange-accent-2"
        >
          {{ detail.bookingMethod || detail.bookingmethod }}
        </v-chip>
      </div>
      <div v-if="detail.paidTime || detail.paidtime" class="mt-1 text-caption">
        付款時間：{{ formatDateTime(detail.paidTime || detail.paidtime) }}
      </div>
    </v-alert>

    <!-- 訂單資訊 -->
    <v-card
      v-if="detail && !loading"
      class="soft-card mb-4"
      elevation="2"
      rounded="xl"
    >
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-receipt-text-check</v-icon
        >
        <span class="card-title">訂單資訊</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <v-row>
          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-identifier</v-icon></template
                >
                <v-list-item-title class="kv">
                  訂單編號：<span class="value">{{
                    detail.bookingId || detail.bookingid
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
                  入住日：<span class="value">{{
                    formatDate(detail.checkinDate || detail.checkindate)
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
                  退房日：<span class="value">{{
                    formatDate(detail.checkoutDate || detail.checkoutdate)
                  }}</span>
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-account-badge-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv">
                  訂購人：<span class="value">{{
                    detail.username || detail.userName
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
                  總金額：<span class="value"
                    >NT$
                    {{
                      formatAmount(detail.grandtotal || detail.grandTotal)
                    }}</span
                  >
                </v-list-item-title>
              </v-list-item>

              <v-list-item v-if="detail.paymentId">
                <template #prepend
                  ><v-icon color="deep-orange">mdi-pound</v-icon></template
                >
                <v-list-item-title class="kv">
                  付款訂單：<span class="value">{{ detail.paymentId }}</span>
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 操作 -->
    <div class="mt-6 d-flex align-center">
      <v-btn
        color="deep-orange-darken-1"
        class="px-6 mr-3"
        size="large"
        @click="goHome"
      >
        <v-icon start>mdi-home</v-icon> 回首頁
      </v-btn>

      <v-btn
        v-if="isFailed()"
        variant="outlined"
        color="deep-orange-darken-1"
        class="px-6"
        size="large"
        @click="retry"
      >
        <v-icon start>mdi-refresh</v-icon> 重新查詢
      </v-btn>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const bookingId = route.query.bookingId || route.params.bookingId;

const detail = ref(null);
const loading = ref(true);
const error = ref("");
let pollTimer = null;

/** API：查訂單明細（由後端更新狀態，前端只查 DB） */
async function getOrderDetail(bookingId) {
  const { data } = await axios.get("/api/orderconfirm/detail", {
    params: { bookingId },
    withCredentials: true,
  });
  return data;
}

/** helpers */
function isPaid() {
  const s = (detail.value?.mentStatus || detail.value?.mentstatus || "")
    .toString()
    .toUpperCase();
  return ["PAID", "SUCCESS", "COMPLETED", "已付款"].some((k) => s.includes(k));
}
function isFailed() {
  const s = (detail.value?.mentStatus || detail.value?.mentstatus || "")
    .toString()
    .toUpperCase();
  return ["FAILED", "ERROR", "付款失敗"].some((k) => s.includes(k));
}
function getStatusText(s) {
  if (!s) return "狀態未知";
  const u = s.toString().toUpperCase();
  if (["PAID", "SUCCESS", "COMPLETED"].includes(u) || s.includes("已付款"))
    return "付款成功";
  if (["FAILED", "ERROR"].includes(u) || s.includes("付款失敗"))
    return "付款失敗";
  if (
    ["PENDING", "PROCESSING"].includes(u) ||
    s.includes("處理中") ||
    s.includes("待付款")
  )
    return "待付款";
  return s;
}
function isPayPalMethod() {
  const m = (detail.value?.bookingMethod || detail.value?.bookingmethod || "")
    .toString()
    .toUpperCase();
  return m.includes("PAYPAL");
}
function formatDate(d) {
  return d ? new Date(d).toLocaleDateString("zh-TW") : "";
}
function formatDateTime(d) {
  return d ? new Date(d).toLocaleString("zh-TW") : "";
}
function formatAmount(n) {
  return new Intl.NumberFormat("zh-TW").format(n || 0);
}

/** 只查一次明細 */
async function fetchDetailOnce() {
  try {
    const data = await getOrderDetail(String(bookingId));
    detail.value = data;
    error.value = "";
  } catch (e) {
    error.value = e?.response?.data || e?.message || "查詢失敗";
  }
}

/** PayPal **/
function startPolling() {
  clearInterval(pollTimer);
  pollTimer = setInterval(async () => {
    await fetchDetailOnce();
    if (isPaid() || isFailed()) {
      clearInterval(pollTimer);
      pollTimer = null;
      loading.value = false;
    }
  }, 3000);
}

async function retry() {
  loading.value = true;
  error.value = "";
  await fetchDetailOnce();
  if (isPayPalMethod() && !isPaid() && !isFailed()) startPolling();
  else loading.value = false;
}

function goHome() {
  router.push("/");
}
onMounted(async () => {
  if (!bookingId) {
    error.value = "缺少訂單編號";
    loading.value = false;
    return;
  }
  await fetchDetailOnce();
  if (isPayPalMethod() && !isPaid() && !isFailed()) {
    startPolling();
  } else {
    loading.value = false;
  }
});
onBeforeUnmount(() => clearInterval(pollTimer));
</script>

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
.loading-text {
  font-size: 16px;
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
}
</style>
