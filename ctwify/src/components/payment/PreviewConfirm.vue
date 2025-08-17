<template>
  <v-container class="py-6" max-width="1000">
    <!-- Title -->
    <div class="header-row">
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-file-document-check-outline</v-icon
      >
      <h2 class="page-title">確認訂單</h2>
    </div>

    <!-- 錯誤訊息 -->
    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      border="start"
      color="red-darken-1"
      icon="mdi-alert-octagon-outline"
      class="mb-4"
    >
      {{ error }}
    </v-alert>

    <!-- 房源資訊 -->
    <v-card v-if="preview" class="mb-4 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3">mdi-home-city</v-icon>
        <span class="card-title">房源資訊</span>
        <v-spacer />
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-3">
        <v-row>
          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-home-city-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >房名：<span class="value">{{
                    preview.listing.houseName
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-map-marker</v-icon></template
                >
                <v-list-item-title class="kv"
                  >地址：<span class="value">{{
                    preview.listing.address || preview.listing.ads
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-phone</v-icon></template
                >
                <v-list-item-title class="kv"
                  >電話：<span class="value">{{
                    preview.listing.tel
                  }}</span></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-bed-queen-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >房型：<span class="value"
                    >{{ preview.listing.type }}{{ preview.listing.bed }}</span
                  ></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-currency-usd</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >每晚價格：<span class="value"
                    >NT$ {{ formatAmount(preview.listing.price) }}</span
                  ></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 入住資訊 -->
    <v-card v-if="preview" class="mb-4 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-calendar-range</v-icon
        >
        <span class="card-title">入住資訊</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-3">
        <v-row>
          <v-col cols="12" md="4">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-calendar-start</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >入住日：<span class="value">{{
                    formatDate(preview.checkindate)
                  }}</span></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="4">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-calendar-end</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >退房日：<span class="value">{{
                    formatDate(preview.checkoutdate)
                  }}</span></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="4">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-account-multiple</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >入住人數：<span class="value"
                    >{{ preview.people }} 人</span
                  ></v-list-item-title
                >
              </v-list-item>
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-moon-waning-crescent</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >入住天數：<span class="value"
                    >{{ preview.days }} 晚</span
                  ></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 金額 -->
    <v-card v-if="preview" class="mb-6 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-receipt-text-check</v-icon
        >
        <span class="card-title">金額</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <div class="total-row">
          <span class="total-label text-h5">應付金額</span>
          <span class="total-value">NT$ {{ formatAmount(preview.total) }}</span>
        </div>
        <div
          v-if="payMethod === 'PAYPAL'"
          class="text-caption text-grey-darken-1 mt-2 d-flex align-center"
        >
          <v-icon size="16" class="mr-1">mdi-information-outline</v-icon>
          PayPal 付款金額約：${{ convertToUSD(preview.total) }} USD（示意匯率 1
          USD = 31 TWD）
        </div>
      </v-card-text>
    </v-card>

    <!-- 租車 -->
    <v-card v-if="preview" class="mb-6 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3">mdi-car</v-icon>
        <span class="card-title">是否需要租車</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <v-row dense>
          <v-col cols="12" md="6">
            <v-btn
              block
              size="large"
              color="deep-orange-darken-1"
              @click="goCarRent"
            >
              <v-icon start>mdi-car-arrow-right</v-icon> 需要
            </v-btn>
          </v-col>
          <v-col cols="12" md="6">
            <v-btn
              block
              size="large"
              variant="outlined"
              color="deep-orange-darken-1"
              @click="noCar"
            >
              <v-icon start>mdi-car-off</v-icon> 不需要
            </v-btn>
          </v-col>
        </v-row>

        <div v-if="form.carId" class="text-caption mt-3 d-flex align-center">
          <v-icon size="16" class="mr-1" color="deep-orange"
            >mdi-check-decagram</v-icon
          >
          已選擇車輛：{{ form.carLabel || form.carId }}
          <v-btn
            size="small"
            variant="text"
            color="deep-orange-darken-1"
            class="ml-2"
            @click="clearCar"
          >
            清除
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- 付款方式 -->
    <v-card class="mb-4 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-credit-card-outline</v-icon
        >
        <span class="card-title">付款方式</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <v-radio-group v-model="payMethod" inline>
          <v-radio label="PayPal / 信用卡" value="PAYPAL" />
          <v-radio label="現金付款" value="CASH" />
        </v-radio-group>

        <v-alert
          v-if="payMethod === 'PAYPAL'"
          type="info"
          variant="tonal"
          border="start"
          color="deep-orange"
          class="mt-2"
          density="compact"
        >
          <v-icon class="mr-2">mdi-information-outline</v-icon>
          使用 PayPal 可以直接刷信用卡，無需註冊 PayPal 帳號
        </v-alert>
      </v-card-text>
    </v-card>

    <!-- 動作 -->
    <div class="mt-4 d-flex align-center">
      <v-btn
        color="deep-orange-darken-1"
        size="large"
        class="px-6"
        :loading="loading"
        :disabled="!preview"
        @click="submitOrder"
      >
        <v-icon start>mdi-check-circle-outline</v-icon>
        {{ getButtonText() }}
      </v-btn>

      <v-btn
        class="ml-3"
        variant="text"
        color="deep-orange-darken-1"
        @click="$router.back()"
      >
        <v-icon start>mdi-arrow-left</v-icon> 返回修改
      </v-btn>
    </div>
  </v-container>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { ref, onMounted, reactive, watch } from "vue";
import dayjs from "dayjs";
import { previewOrder, finalizeOrder as finalizeOrderApi } from "./order";

const route = useRoute();
const router = useRouter();

const preview = ref(null);
const error = ref("");
const loading = ref(false);
const payMethod = ref("PAYPAL"); // 預設使用 PayPal

// 租車選擇暫存
const form = reactive({
  bookingId: "",
  carId: null,
  carLabel: "",
});

function getButtonText() {
  switch (payMethod.value) {
    case "PAYPAL":
      return "前往 PayPal 付款";
    case "CREDIT_NEWEBPAY":
      return "前往信用卡付款";
    default:
      return "確認下單";
  }
}

function convertToUSD(twdAmount) {
  if (!twdAmount) return "0.00";
  const usdAmount = parseFloat(twdAmount) / 31;
  return usdAmount.toFixed(2);
}

function formatDate(dateStr) {
  return dayjs(dateStr).format("YYYY-MM-DD");
}
function formatAmount(n) {
  return new Intl.NumberFormat("zh-TW").format(n || 0);
}

onMounted(loadPreview);

async function loadPreview() {
  error.value = "";
  const { listid, checkInDate, checkOutDate, guests } = route.query;
  if (!listid || !checkInDate || !checkOutDate || !guests) {
    error.value = "參數不足，請重新選擇預訂資訊";
    return;
  }
  try {
    loading.value = true;
    const data = await previewOrder({
      listid: Number(listid),
      checkindate: checkInDate,
      checkoutdate: checkOutDate,
      people: Number(guests),
    });
    preview.value = data;
  } catch (e) {
    error.value = e?.response?.data || e?.message || "取得預覽資料失敗";
  } finally {
    loading.value = false;
  }
}

async function submitOrder() {
  error.value = "";
  loading.value = true;

  try {
    const { listid, checkInDate, checkOutDate, guests } = route.query;

    const orderData = {
      listid: Number(listid),
      checkindate: checkInDate,
      checkoutdate: checkOutDate,
      people: Number(guests),
      cartotal: form.carId ? 1000 : 0, // 有租車加 1000，否則 0（示意）
      bookingmethod: payMethod.value,
    };

    const response = await finalizeOrderApi(orderData);

    // 回傳可能是字串或物件 → 統一取 bookingId
    let bookingId;
    if (typeof response === "string") {
      const match = response.match(/訂單編號[:：]\s*([A-Za-z0-9-]+)/);
      bookingId = match ? match[1] : "";
    } else {
      bookingId = response.bookingId || response.data?.bookingId;
    }
    if (!bookingId) throw new Error("無法取得訂單編號");

    form.bookingId = bookingId;

    if (payMethod.value === "PAYPAL") {
      router.push({ name: "PayPalPayment", query: { bookingId } });
    } else if (payMethod.value === "CREDIT_NEWEBPAY") {
      router.push({ name: "PayRedirect", query: { bookingId } });
    } else {
      router.push({ name: "PaymentDone", query: { bookingId } });
    }
  } catch (e) {
    error.value = e?.response?.data || e?.message || "建立訂單失敗";
  } finally {
    loading.value = false;
  }
}

// 租車：導向租車首頁
function goCarRent() {
  const { listid, checkInDate, checkOutDate, guests } = route.query;
  router.push({
    name: "CarRentFrontHomepage",
    query: {
      from: "booking",
      bookingId: form.bookingId || "",
      listid,
      checkin: checkInDate,
      checkout: checkOutDate,
      guests,
    },
  });
}

// 不租車：清空 carId
function noCar() {
  form.carId = null;
  form.carLabel = "";
}
function clearCar() {
  form.carId = null;
  form.carLabel = "";
}

// 從租車頁回來帶 carId/carLabel 當 query
watch(
  () => ({ id: route.query.carId, label: route.query.carLabel }),
  ({ id, label }) => {
    if (id) {
      form.carId = String(id);
      form.carLabel = String(label || "");
    }
  },
  { immediate: true }
);
</script>

<style scoped>
/* 柔和暖橘卡片 */
.soft-card {
  background: #fff7ed; /* orange-50 風格 */
}

/* 標題區 */
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
}

/* Key-Value 列表 */
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

/* 金額 */
.total-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.total-label {
  font-size: 16px;
  color: #78350f;
  font-weight: 600;
}
.total-value {
  font-size: 24px;
  font-weight: 800;
  color: #9a3412;
}
</style>
