<template>
  <v-container class="py-6" max-width="1000">
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
    <v-card
      v-if="booking.selectedListing"
      class="mb-4 soft-card"
      elevation="2"
      rounded="xl"
    >
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
                    booking.selectedListing.houseName
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-map-marker</v-icon></template
                >
                <v-list-item-title class="kv"
                  >地址：<span class="value">{{
                    booking.selectedListing.address
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-phone</v-icon></template
                >
                <v-list-item-title class="kv"
                  >電話：<span class="value">{{
                    booking.selectedListing.tel
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
                    >{{ booking.selectedListing.type
                    }}{{ booking.selectedListing.bed }}</span
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
                    >NT$ {{ formatAmount(booking.selectedListing.price) }}</span
                  ></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 入住資訊 -->
    <v-card
      v-if="booking.hasBookingData"
      class="mb-4 soft-card"
      elevation="2"
      rounded="xl"
    >
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
                    formatDate(booking.bookingParams.checkInDate)
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
                    formatDate(booking.bookingParams.checkOutDate)
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
                    >{{ booking.bookingParams.guests }} 人</span
                  ></v-list-item-title
                >
              </v-list-item>
              <v-list-item v-if="preview">
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
        <span class="card-title">金額明細</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <div class="mb-3">
          <div class="d-flex justify-space-between mb-2">
            <span class="text-body-1">房租小計</span>
            <span class="text-body-1"
              >NT$ {{ formatAmount(preview.roomTotal || 0) }}</span
            >
          </div>
          <div
            v-if="preview.carTotal > 0"
            class="d-flex justify-space-between mb-2"
          >
            <span class="text-body-1">租車小計</span>
            <span class="text-body-1"
              >NT$ {{ formatAmount(preview.carTotal) }}</span
            >
          </div>
        </div>

        <v-divider class="my-3" />

        <div class="total-row">
          <span class="total-label text-h5">應付金額</span>
          <span class="total-value"
            >NT$ {{ formatAmount(preview.grandtotal) }}</span
          >
        </div>
        <div
          v-if="payMethod === 'PAYPAL'"
          class="text-caption text-grey-darken-1 mt-2 d-flex align-center"
        >
          <v-icon size="16" class="mr-1">mdi-information-outline</v-icon>
          PayPal 付款金額約：${{ convertToUSD(preview.grandTotal) }}
          USD（示意匯率 1 USD = 31 TWD）
        </div>
      </v-card-text>
    </v-card>

    <!-- 租車 -->
    <v-card
      v-if="booking.hasBookingData"
      class="mb-6 soft-card"
      elevation="2"
      rounded="xl"
    >
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

        <div
          v-if="booking.hasCarData"
          class="text-caption mt-3 d-flex align-center"
        >
          <v-icon size="16" class="mr-1" color="deep-orange"
            >mdi-check-decagram</v-icon
          >
          已選擇車輛：租車訂單 {{ booking.vehicleDraft.reservationId }}｜門市
          {{ booking.vehicleDraft.locationId ?? "-" }}｜NT$
          {{ formatAmount(booking.vehicleDraft.totalAmount) }}
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
        :disabled="!preview || !booking.hasBookingData"
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
import { ref, onMounted, watch } from "vue";
import dayjs from "dayjs";
import {
  previewOrder,
  finalizeOrder as finalizeOrderApi,
} from "../../sevice/order";
import { useBookingStore } from "@/stores/booking";

const route = useRoute();
const router = useRouter();
const booking = useBookingStore();

const preview = ref(null);
const error = ref("");
const loading = ref(false);
const payMethod = ref("PAYPAL");

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

// 監聽租車資料變化，重新載入預覽
watch(
  () => booking.vehicleDraft,
  () => {
    if (booking.hasBookingData) {
      loadPreview();
    }
  },
  { deep: true }
);

async function loadPreview() {
  if (!booking.hasBookingData) {
    error.value = "缺少訂房資料，請重新選擇預訂資訊";
    return;
  }

  error.value = "";
  loading.value = true;

  try {
    const payload = {
      listid: booking.bookingParams.listid,
      checkindate: booking.bookingParams.checkInDate,
      checkoutdate: booking.bookingParams.checkOutDate,
      people: booking.bookingParams.guests,
      // 租車相關資料
      reservationId: booking.vehicleDraft.reservationId || null,
      carLocationId: booking.vehicleDraft.locationId || null,
      cartotal: booking.vehicleDraft.totalAmount || 0,
    };

    const data = await previewOrder(payload);
    preview.value = data;
  } catch (e) {
    error.value = e?.response?.data || e?.message || "取得預覽資料失敗";
  } finally {
    loading.value = false;
  }
}

async function submitOrder() {
  if (!booking.hasBookingData) {
    error.value = "缺少訂房資料";
    return;
  }

  error.value = "";
  loading.value = true;

  try {
    const orderData = {
      listid: booking.bookingParams.listid,
      checkindate: booking.bookingParams.checkInDate,
      checkoutdate: booking.bookingParams.checkOutDate,
      people: booking.bookingParams.guests,
      // 租車相關資料
      reservationId: booking.vehicleDraft.reservationId || null,
      carLocationId: booking.vehicleDraft.locationId || null,
      cartotal: booking.vehicleDraft.totalAmount || 0,
      bookingmethod: payMethod.value,
    };

    const response = await finalizeOrderApi(orderData);

    // 回傳可能是字串或物件 統一取 bookingId
    let bookingId;
    if (typeof response === "string") {
      const match = response.match(/訂單編號[:：]\s*([A-Za-z0-9-]+)/);
      bookingId = match ? match[1] : "";
    } else {
      bookingId = response.bookingId || response.data?.bookingId;
    }
    if (!bookingId) throw new Error("無法取得訂單編號");

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
  router.push({
    name: "CarRentFrontHomepage",
    query: {
      from: "booking",
      listid: booking.bookingParams.listid,
      checkin: booking.bookingParams.checkInDate,
      checkout: booking.bookingParams.checkOutDate,
      guests: booking.bookingParams.guests,
    },
  });
}

// 不租車：清空 carId
function noCar() {
  booking.resetVehicleDraft();
}

function clearCar() {
  booking.resetVehicleDraft();
}
</script>

<style scoped>
.soft-card {
  background: #fff7ed; /* orange-50 風格 */
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