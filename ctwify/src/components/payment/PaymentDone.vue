<template>
  <v-container class="py-10" max-width="900">
    <h2 class="mb-6">付款結果</h2>

    <!-- 載入中狀態 -->
    <div v-if="loading" class="text-center py-8">
      <v-progress-circular
        indeterminate
        color="primary"
        size="64"
      ></v-progress-circular>
      <p class="mt-4">正在確認付款狀態...</p>
    </div>

    <!-- 錯誤訊息 -->
    <v-alert v-if="error && !loading" type="error" class="mb-4">
      <div class="font-weight-bold">處理過程中發生錯誤</div>
      <div class="mt-2">{{ error }}</div>
      <div class="mt-3">
        <v-btn size="small" @click="retryFetch" :loading="retrying"
          >重新查詢</v-btn
        >
        <v-btn size="small" variant="text" class="ml-2" @click="contactSupport"
          >聯絡客服</v-btn
        >
      </div>
    </v-alert>

    <!-- 付款狀態顯示 -->
    <v-alert
      v-if="detail && !loading"
      :type="getStatusType(detail.mentStatus || detail.mentstatus)"
      class="mb-4"
    >
      <div class="font-weight-bold">
        {{ getStatusText(detail.mentStatus || detail.mentstatus) }}
      </div>
      <div v-if="detail.paidTime || detail.paidtime" class="mt-1">
        付款時間：{{ formatDateTime(detail.paidTime || detail.paidtime) }}
      </div>
    </v-alert>

    <!-- 訂單詳情 -->
    <v-card v-if="detail && !loading" class="mb-4" color="orange-lighten-5">
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <div class="mb-2">
              <strong>訂單編號：</strong
              >{{ detail.bookingId || detail.bookingid }}
            </div>
            <div class="mb-2">
              <strong>房型：</strong>{{ detail.houseName || detail.housename }}
            </div>
            <div class="mb-2">
              <strong>入住日：</strong
              >{{ formatDate(detail.checkinDate || detail.checkindate) }}
            </div>
            <div class="mb-2">
              <strong>退房日：</strong
              >{{ formatDate(detail.checkoutDate || detail.checkoutdate) }}
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-2">
              <strong>訂單狀態：</strong>
              <v-chip
                :color="getStatusColor(detail.mentStatus || detail.mentstatus)"
                size="small"
                class="ml-1"
              >
                {{ getStatusText(detail.mentStatus || detail.mentstatus) }}
              </v-chip>
            </div>
            <div class="mb-2">
              <strong>總金額：</strong>NT$
              {{ formatAmount(detail.grandtotal || detail.grandTotal) }}
            </div>
            <div
              v-if="detail.bookingMethod || detail.bookingmethod"
              class="mb-2"
            >
              <strong>付款方式：</strong
              >{{
                getPaymentMethodText(
                  detail.bookingMethod || detail.bookingmethod
                )
              }}
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 操作按鈕 -->
    <div class="mt-6">
      <v-btn color="primary" @click="goHome" class="mr-3">回首頁</v-btn>
      <v-btn
        v-if="
          detail &&
          (detail.mentStatus === 'PAID' || detail.mentstatus === 'PAID')
        "
        variant="outlined"
        @click="viewOrderDetails"
      >
        查看訂單詳情
      </v-btn>
      <v-btn
        v-if="
          detail &&
          (detail.mentStatus === 'FAILED' || detail.mentstatus === 'FAILED')
        "
        color="warning"
        variant="outlined"
        @click="retryPayment"
      >
        重新付款
      </v-btn>
    </div>

    <!-- 調試信息（開發環境） -->
    <v-card v-if="showDebugInfo && detail" class="mt-4" color="grey-lighten-4">
      <v-card-title class="text-subtitle-2">調試信息</v-card-title>
      <v-card-text>
        <pre class="text-caption">{{ JSON.stringify(detail, null, 2) }}</pre>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getOrderDetail } from "./order";

const route = useRoute();
const router = useRouter();
const bookingId = route.params.bookingId;

const detail = ref(null);
const error = ref("");
const loading = ref(true);
const retrying = ref(false);
const showDebugInfo = ref(false); // 在開發環境中可設為 true

let pollTimer = null;
let pollCount = 0;
const maxPolls = 30; // 增加到 30 次 (1.5 分鐘)

// 從 URL 參數獲取付款狀態提示
const urlStatus = route.query.status;

async function fetchDetailOnce() {
  try {
    console.log("🔍 查詢訂單:", bookingId);
    const data = await getOrderDetail(String(bookingId));
    detail.value = data;
    console.log("📄 訂單詳情:", data);
    error.value = ""; // 清除錯誤
  } catch (e) {
    console.error("❌ 查詢失敗:", e);
    error.value = e.response?.data || e.message || "查詢訂單失敗";
  }
}

function startPolling() {
  pollCount = 0;

  pollTimer = setInterval(async () => {
    pollCount++;
    console.log(`輪詢第 ${pollCount} 次`);

    await fetchDetailOnce();

    if (detail.value) {
      const status = detail.value.mentStatus || detail.value.mentstatus;
      console.log("當前狀態:", status);

      // 如果狀態已確定或達到最大輪詢次數，停止輪詢
      if (status === "PAID" || status === "FAILED" || pollCount >= maxPolls) {
        clearInterval(pollTimer);
        pollTimer = null;
        loading.value = false;

        if (pollCount >= maxPolls && status === "PENDING") {
          error.value = "付款狀態確認超時，請重新整理頁面或聯絡客服";
        }
      }
    } else if (pollCount >= maxPolls) {
      clearInterval(pollTimer);
      pollTimer = null;
      loading.value = false;
      error.value = "無法獲取訂單信息，請聯絡客服";
    }
  }, 3000);
}

async function retryFetch() {
  retrying.value = true;
  error.value = "";
  loading.value = true;

  try {
    await fetchDetailOnce();
    if (
      !detail.value ||
      (detail.value.mentStatus || detail.value.mentstatus) === "PENDING"
    ) {
      startPolling();
    } else {
      loading.value = false;
    }
  } catch (e) {
    loading.value = false;
  } finally {
    retrying.value = false;
  }
}

function getStatusType(status) {
  switch (status?.toUpperCase()) {
    case "PAID":
      return "success";
    case "FAILED":
      return "error";
    case "PENDING":
      return "warning";
    default:
      return "info";
  }
}

function getStatusColor(status) {
  switch (status?.toUpperCase()) {
    case "PAID":
      return "green";
    case "FAILED":
      return "red";
    case "PENDING":
      return "orange";
    default:
      return "grey";
  }
}

function getStatusText(status) {
  switch (status?.toUpperCase()) {
    case "PAID":
      return "付款成功";
    case "FAILED":
      return "付款失敗";
    case "PENDING":
      return "付款處理中";
    default:
      return "狀態未知";
  }
}

function getPaymentMethodText(method) {
  switch (method?.toUpperCase()) {
    case "CREDIT_NEWEBPAY":
      return "信用卡付款（藍新金流）";
    case "CASH":
      return "現金付款";
    default:
      return method || "未指定";
  }
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("zh-TW");
}

function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return "";
  return new Date(dateTimeStr).toLocaleString("zh-TW");
}

function formatAmount(amount) {
  if (!amount) return "0";
  return new Intl.NumberFormat("zh-TW").format(amount);
}

function goHome() {
  router.push("/");
}

function viewOrderDetails() {
  // 假設有訂單詳情頁面
  router.push(`/orders/${bookingId}`);
}

function retryPayment() {
  // 重新導向到付款頁面
  router.push({
    name: "PayRedirect",
    query: { bookingId },
  });
}

function contactSupport() {
  // 可以導向客服頁面或開啟郵件客戶端
  alert("請聯絡客服：support@example.com 或撥打客服專線：(02) 1234-5678");
}

// 開發環境快捷鍵
function toggleDebugInfo() {
  showDebugInfo.value = !showDebugInfo.value;
}

onMounted(async () => {
  console.log("PaymentDone mounted, bookingId:", bookingId);
  console.log("URL status:", urlStatus);

  // 開發環境：按 Ctrl+D 顯示調試信息
  if (process.env.NODE_ENV === "development") {
    window.addEventListener("keydown", (e) => {
      if (e.ctrlKey && e.key === "d") {
        e.preventDefault();
        toggleDebugInfo();
      }
    });
  }

  await fetchDetailOnce();

  // 如果訂單狀態還是 PENDING，開始輪詢
  if (
    detail.value &&
    (detail.value.mentStatus || detail.value.mentstatus) === "PENDING"
  ) {
    startPolling();
  } else {
    loading.value = false;
  }
});

onBeforeUnmount(() => {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
});
</script>

<style scoped>
pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 300px;
  overflow-y: auto;
}
</style>