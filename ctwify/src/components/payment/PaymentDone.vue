
<template>
  <v-container class="py-10" max-width="900">
    <h2 class="mb-6">付款結果</h2>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <v-card v-if="detail" class="mb-4" color="orange-lighten-5">
      <v-card-text>
        <div>
          訂單編號：<b>{{ detail.bookingId || detail.bookingid }}</b>
        </div>
        <div>
          訂單狀態：<b>{{ detail.mentStatus || detail.mentstatus }}</b>
        </div>
        <div>房型：{{ detail.houseName || detail.housename }}</div>
        <div>入住日：{{ detail.checkinDate || detail.checkindate }}</div>
        <div>退房日：{{ detail.checkoutDate || detail.checkoutdate }}</div>
        <div>總金額：NT$ {{ detail.grandtotal || detail.grandTotal }}</div>
        <div v-if="detail.paidTime || detail.paidtime">
          付款時間：{{ detail.paidTime || detail.paidtime }}
        </div>
      </v-card-text>
    </v-card>

    <v-alert v-else type="info">讀取中…</v-alert>

    <v-btn color="primary" @click="$router.push('/')">回首頁</v-btn>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRoute } from "vue-router";
import { getOrderDetail } from "./order";

const route = useRoute();
const bookingId = route.params.bookingId;

const detail = ref(null);
const error = ref("");
let timer = null;

async function fetchDetailOnce() {
  try {
    const data = await getOrderDetail(String(bookingId));
    detail.value = data;
  } catch (e) {
    error.value = e.message || "查詢訂單失敗";
  }
}

// 簡單輪詢：每 3 秒查一次，直到狀態為 PAID 或 FAILED
function startPolling() {
  timer = setInterval(async () => {
    await fetchDetailOnce();
    const status = detail.value?.mentStatus || detail.value?.mentstatus;
    if (status === "PAID" || status === "FAILED") {
      clearInterval(timer);
      timer = null;
    }
  }, 3000);
}

onMounted(async () => {
  await fetchDetailOnce();
  startPolling();
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});
</script>