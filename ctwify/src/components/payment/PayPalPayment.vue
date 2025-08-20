<template>
  <v-container class="py-10" max-width="900">
    <h2 class="mb-6">PayPal 信用卡付款</h2>

    <!-- 狀態 -->
    <div v-if="loading && !error" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" size="64" />
      <p class="mt-4">{{ loadingMessage }}</p>
    </div>

    <v-alert v-if="error" type="error" class="mb-4">
      <div class="font-weight-bold">付款過程發生錯誤</div>
      <div class="mt-2">{{ error }}</div>
      <div class="mt-3">
        <v-btn size="small" @click="retryPayment" :loading="retrying"
          >重新嘗試</v-btn
        >
        <v-btn size="small" variant="text" class="ml-2" @click="goBack"
          >返回訂單</v-btn
        >
      </div>
    </v-alert>

    <!-- 付款資訊 -->
    <v-card
      v-if="orderInfo && !loading"
      class="highlight-card mb-6"
      elevation="1"
      rounded="xl"
    >
      <v-card-title class="d-flex align-center py-3">
        <v-icon class="mr-2" color="deep-orange-darken-2"
          >mdi-credit-card-outline</v-icon
        >
        <span class="card-title">付款資訊</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <v-row class="pay-grid" align="start">
          <v-col cols="12" md="6">
            <div class="info-line">
              <v-icon size="18" class="mr-1" color="deep-orange"
                >mdi-identifier</v-icon
              >
              <span class="label">訂單編號：</span>
              <span class="value code">{{ orderInfo.bookingId }}</span>
            </div>
            <div class="info-line mt-4">
              <v-icon size="18" class="mr-1" color="deep-orange"
                >mdi-currency-usd</v-icon
              >
              <span class="label">付款金額：</span>
              <span class="amount-usd">${{ orderInfo.amount }} USD</span>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="info-line">
              <v-icon size="18" class="mr-1" color="deep-orange"
                >mdi-cash</v-icon
              >
              <span class="label">原始金額：</span>
              <span class="value"
                >NT$ {{ formatAmount(orderInfo.originalAmount) }}</span
              >
            </div>
            <div class="rate-note mt-2">* 匯率按 1 USD = 31 TWD 計算</div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- PayPal 付款按鈕區-->
    <v-card class="mb-4">
      <v-card-title class="text-h6">選擇付款方式</v-card-title>
      <v-card-text>
        <div class="mb-3">
          <v-icon color="info" class="mr-2">mdi-information</v-icon>
          <span class="text-body-2"
            >點擊下方 PayPal 按鈕進行付款，支援信用卡直接付款</span
          >
        </div>

        <div class="paypal-section">
          <div
            id="paypal-buttons-container"
            ref="paypalContainer"
            style="min-height: 60px"
          ></div>

          <!-- 載入狀態 -->
          <div v-if="initializingButton" class="text-center py-4">
            <v-progress-circular indeterminate size="40" color="primary" />
            <p class="mt-2">正在載入 PayPal 付款按鈕...</p>
          </div>

          <!-- 若初始化失敗一重試 -->
          <div
            v-if="!initializingButton && !buttonReady"
            class="text-center py-4"
          >
            <v-btn
              color="primary"
              @click="startPayPalInit"
              :loading="initializingButton"
            >
              載入 PayPal 付款按鈕
            </v-btn>
          </div>
        </div>
      </v-card-text>
    </v-card>

    <div class="mt-6">
      <v-btn variant="text" @click="goBack">
        <v-icon class="mr-2">mdi-arrow-left</v-icon> 返回訂單確認
      </v-btn>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { createPayPalOrder, capturePayPalPayment } from "../../sevice/paypal";

const route = useRoute();
const router = useRouter();

const bookingId = route.query.bookingId || route.params.bookingId;
const paypalContainer = ref(null);

const orderInfo = ref(null);
const error = ref("");
const loading = ref(true);
const retrying = ref(false);
const loadingMessage = ref("正在準備付款...");
const buttonReady = ref(false);
const initializingButton = ref(false);

const PAYPAL_CLIENT_ID =
  "AcEqMs9hMZ6okj-3IDrydnmdGcaX9lCWkIHuaLOqVFg8Xq_0an34uEOiSgH7buMWB1PI-r_pukLmnAX5";

let paypalInstance = null;
let sdkLoaded = false;

onMounted(async () => {
  if (!bookingId) {
    error.value = "缺少訂單編號，無法進行付款";
    loading.value = false;
    return;
  }
  await initializeOrder();
  // 自動初始化按鈕（也可手動按鈕再叫 startPayPalInit）
  await startPayPalInit();
});

onBeforeUnmount(() => {
  if (paypalInstance && paypalInstance.close) {
    try {
      paypalInstance.close();
    } catch {}
  }
});

async function initializeOrder() {
  try {
    loading.value = true;
    error.value = "";
    loadingMessage.value = "正在建立 PayPal 訂單...";

    // 後端建立 PayPal 訂單
    const result = await createPayPalOrder(bookingId);
    orderInfo.value = result;

    loadingMessage.value = "正在載入 PayPal SDK...";
    await loadPayPalSDK();
  } catch (e) {
    error.value = e?.response?.data || e?.message || "初始化失敗";
  } finally {
    loading.value = false;
  }
}

function loadPayPalSDK() {
  return new Promise((resolve, reject) => {
    if (window.paypal) {
      sdkLoaded = true;
      resolve();
      return;
    }
    const s = document.createElement("script");
    // 加上 enable-funding=card 與 components=buttons，較容易顯示信用卡
    s.src = `https://www.paypal.com/sdk/js?client-id=${PAYPAL_CLIENT_ID}&currency=USD&intent=capture&components=buttons&enable-funding=card&locale=zh_TW`;
    s.async = true;
    s.onload = () => {
      sdkLoaded = true;
      resolve();
    };
    s.onerror = () => reject(new Error("載入 PayPal SDK 失敗"));
    document.head.appendChild(s);
  });
}

async function startPayPalInit() {
  if (!sdkLoaded) {
    error.value = "PayPal SDK 尚未就緒";
    return;
  }
  if (!orderInfo.value?.paypalOrderId) {
    error.value = "缺少 PayPal 訂單編號";
    return;
  }

  initializingButton.value = true;
  error.value = "";

  try {
    // 確保容器節點已經掛上 DOM
    await nextTick();
    const containerEl = paypalContainer.value;
    if (!containerEl) throw new Error("找不到 PayPal 按鈕容器");
    containerEl.innerHTML = "";

    paypalInstance = window.paypal.Buttons({
      style: {
        layout: "vertical",
        color: "blue",
        shape: "rect",
        label: "paypal",
        height: 55,
      },

      // 直接回傳後端建立好的 Order ID（不要在前端重建）
      createOrder: () => orderInfo.value.paypalOrderId,

      onApprove: async (data) => {
        try {
          loading.value = true;
          loadingMessage.value = "正在處理付款...";
          const result = await capturePayPalPayment(data.orderID, bookingId);
          if (result?.success) {
            router.push({
              name: "PaymentDone",
              query: { bookingId, status: "success", paymentMethod: "paypal" },
            });
          } else {
            throw new Error(result?.message || "付款處理失敗");
          }
        } catch (e) {
          error.value = e?.response?.data || e?.message || "付款處理失敗";
          loading.value = false;
        }
      },

      onCancel: () => {
        router.push({
          name: "PaymentDone",
          query: { bookingId, status: "cancelled", paymentMethod: "paypal" },
        });
      },

      onError: (err) => {
        error.value = "PayPal 付款錯誤：" + (err?.message || "未知錯誤");
      },
    });

    await paypalInstance.render(containerEl);
    buttonReady.value = true;
  } catch (e) {
    error.value = "PayPal 按鈕載入失敗：" + e.message;
    buttonReady.value = false;
  } finally {
    initializingButton.value = false;
  }
}

async function retryPayment() {
  retrying.value = true;
  buttonReady.value = false;
  if (paypalInstance && paypalInstance.close) {
    try {
      paypalInstance.close();
    } catch {}
    paypalInstance = null;
  }
  await initializeOrder();
  await startPayPalInit();
  retrying.value = false;
}

function goBack() {
  router.back();
}

function formatAmount(amount) {
  if (!amount) return "0";
  return new Intl.NumberFormat("zh-TW").format(amount);
}
</script>

<style scoped>
.paypal-section {
  min-height: 100px;
  padding: 20px;
  border: 2px solid #007cba;
  border-radius: 8px;
  background-color: #f8f9fa;
}
.highlight-card {
  background: #fffdf9;
  border: 4px solid #fb923c;
  border-radius: 16px;
}
/* 標題字 */
.card-title {
  font-size: 25px;
  font-weight: 700;
  color: #c2410c;
}
/* 資訊行 */
.info-line {
  display: flex;
  align-items: baseline;
  line-height: 1.6;
  margin-bottom: 4px;
  min-height: 28px;
  font-size: 20px;
}
/* 標籤寬度*/
.label {
  width: 120px;
  color: #7c2d12;
  font-weight: 600;
  flex: 0 0 auto;
}
/*可換行 */
.value {
  flex: 1 1 auto;
  min-width: 0;
  word-break: break-all;
  color: #4a342e;
}
.value.code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", "Courier New", monospace;
  letter-spacing: 0.2px;
}
.amount-usd {
  font-size: 22px;
  font-weight: 800;
  color: #0d47a1;
}
.rate-note {
  font-size: 12px;
  color: #8b5e34;
  opacity: 0.85;
}
@media (max-width: 600px) {
  .label {
    width: 84px;
  }
  .amount-usd {
    font-size: 20px;
  }
}
</style>
