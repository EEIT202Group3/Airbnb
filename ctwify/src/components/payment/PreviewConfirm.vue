<template>
  <v-container class="py-6" max-width="1000">
    <h2 class="mb-4">確認訂單</h2>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card class="mb-4" color="orange-lighten-5" v-if="preview">
      <v-card-title class="text-h6">房源資訊</v-card-title>
      <v-card-text>
        <ul class="ma-0 pl-4">
          <li>房名：{{ preview.listing.houseName }}</li>
          <li>地址：{{ preview.listing.address || preview.listing.ads }}</li>
          <li>電話：{{ preview.listing.tel }}</li>
          <li>房型：{{ preview.listing.type }}{{ preview.listing.bed }}</li>
          <li>每晚價格：NT$ {{ preview.listing.price }}</li>
        </ul>
      </v-card-text>
    </v-card>

    <v-card class="mb-4" color="orange-lighten-5" v-if="preview">
      <v-card-title class="text-h6">入住資訊</v-card-title>
      <v-card-text>
        <ul class="ma-0 pl-4">
          <li>入住日：{{ formatDate(preview.checkindate) }}</li>
          <li>退房日：{{ formatDate(preview.checkoutdate) }}</li>
          <li>入住人數：{{ preview.people }}人</li>
          <li>入住天數：{{ preview.days }} 晚</li>
        </ul>
      </v-card-text>
    </v-card>

    <v-card class="mb-6" color="orange-lighten-5" v-if="preview">
      <v-card-title class="text-h6">金額</v-card-title>
      <v-card-text>
        <div class="text-h5 font-weight-bold">
          應付金額：NT$ {{ preview.total }}
        </div>
        <div class="text-caption text-grey mt-2"></div>
      </v-card-text>
    </v-card>

    <!-- 是否需要租車 -->
    <v-card class="mb-6" color="orange-lighten-5" v-if="preview">
      <v-card-title class="text-h6">是否需要租車</v-card-title>
      <v-card-text>
        <v-row dense>
          <v-col cols="6">
            <v-btn block size="large" @click="goCarRent">需要</v-btn>
          </v-col>
          <v-col cols="6">
            <v-btn block size="large" variant="outlined" @click="noCar"
              >不需要</v-btn
            >
          </v-col>
        </v-row>
        <div v-if="form.carId" class="text-caption mt-2">
          已選擇車輛：{{ form.carLabel || form.carId }}
          <v-btn size="small" variant="text" @click="clearCar">清除</v-btn>
        </div>
      </v-card-text>
    </v-card>

    <v-radio-group v-model="payMethod" inline class="mb-4">
      <v-radio label="信用卡（藍新）" value="CREDIT_NEWEBPAY" />
      <v-radio label="現金付款" value="CASH" />
    </v-radio-group>

    <v-btn
      color="primary"
      :loading="loading"
      :disabled="!preview"
      @click="submitOrder"
    >
      確認下單
    </v-btn>
    <v-btn class="ml-3" variant="text" @click="$router.back()">返回修改</v-btn>
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
const payMethod = ref("CREDIT_NEWEBPAY");

// 租車選擇暫存（carId 允許為 null）
const form = reactive({
  bookingId: "",
  carId: null,
  carLabel: "",
});

function formatDate(dateStr) {
  return dayjs(dateStr).format("YYYY-MM-DD");
}

onMounted(loadPreview);

async function loadPreview() {
  error.value = "";
  const { listId, checkInDate, checkOutDate, guests } = route.query;
  if (!listId || !checkInDate || !checkOutDate || !guests) {
    error.value = "參數不足，請重新選擇預訂資訊";
    return;
  }
  try {
    loading.value = true;
    const data = await previewOrder({
      listid: Number(listId),
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
    const { listId, checkInDate, checkOutDate, guests } = route.query;
    const { bookingId } = await finalizeOrderApi({
      listid: Number(listId),
      checkindate: checkInDate,
      checkoutdate: checkOutDate,
      people: Number(guests),
      // 可在此一併帶 carId（可能為 null）給後端
      carId: form.carId,
    });
    form.bookingId = bookingId;

    if (payMethod.value === "CREDIT_NEWEBPAY") {
      router.push({ name: "PayRedirect", query: { bookingId } });
    } else {
      router.push({ name: "PaymentDone", params: { bookingId } });
    }
  } catch (e) {
    error.value = e?.response?.data || e?.message || "建立訂單失敗";
  } finally {
    loading.value = false;
  }
}

//跳租車首頁
function goCarRent() {
  const { listId, checkInDate, checkOutDate, guests } = route.query;
  router.push({
    name: "CarRentFrontHomepage",
    query: {
      from: "booking",
      bookingId: form.bookingId || "",
      listId,
      checkin: checkInDate,
      checkout: checkOutDate,
      guests,
    },
  });
}

// 不租車：清空 carId（之後完成訂單api 一起送到後端存 null）
function noCar() {
  form.carId = null;
  form.carLabel = "";
}
function clearCar() {
  form.carId = null;
  form.carLabel = "";
}

//從租車頁回來帶 carId/carLabel 當 query
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