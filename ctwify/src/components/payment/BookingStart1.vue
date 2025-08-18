<template>
  <v-container class="py-6" max-width="1000">
    <h2 class="mb-4">預約房源</h2>

    <!-- 房源資訊 -->
    <v-card class="mb-4" color="orange-lighten-5">
      <v-card-title class="text-h6">房源資訊</v-card-title>
      <v-card-text>
        <div>房名：{{ listing.houseName }}</div>
        <div>地址：{{ listing.address }}</div>
        <div>床位：{{ listing.bed }}</div>
        <div>房型：{{ listing.type }}</div>
        <div>聯絡電話：{{ listing.tel }}</div>
      </v-card-text>
    </v-card>

    <!-- 輸入欄位 -->
    <v-card class="mb-4">
      <v-card-title class="text-h6">請填寫預訂資訊</v-card-title>
      <v-card-text>
        <v-text-field
            label="入住日期"
            v-model="form.checkin"
            type="date"
            variant="outlined"
            class="mb-2"
        />

        <v-text-field
            label="退房日期"
            v-model="form.checkout"
            type="date"
            variant="outlined"
            class="mb-2"
        />

        <v-text-field
            label="入住人數"
            v-model.number="form.people"
            type="number"
            min="1"
            variant="outlined"
            class="mb-2"
        />

        <v-btn color="orange-darken-2" @click="goToPreview">
          下一步：確認訂單
        </v-btn>

<!--==========================================-->
<!--測試用按鈕-->
        <v-btn
            color="blue-darken-2"
            class="ml-2"
            @click="$router.push({ name: 'CarRentFrontHomepage' })"
        >
          去租車首頁
        </v-btn>
<!--==========================================-->

      </v-card-text>
    </v-card>

    <!-- 錯誤訊息 -->
    <v-alert type="error" v-if="error" class="mt-4">
      {{ error }}
    </v-alert>
  </v-container>
</template>

<script setup>
import {ref} from "vue";
import {useRouter, useRoute} from "vue-router";
import axios from "axios";

// ==========================================
// 導入booking.js
import {useBookingStore} from "@/stores/booking";

const store = useBookingStore();
// ==========================================

const route = useRoute();
const router = useRouter();
const error = ref("");

// 從上一頁取得房源資訊
// const listing = {
//   listId: route.query.listId,
//   houseName: route.query.houseName,
//   address: route.query.address,
//   tel: route.query.tel,
//   bed: route.query.bed,
//   type: route.query.type,
//   price: parseInt(route.query.price),
// };

// ==========================================
// 從 Pinia 優先帶入，缺的再用 query 補
const s = store.selectedListing || {};
const q = route.query;

const listing = {
  listId: s.listId ?? s.list_id ?? q.listId,
  houseName: s.houseName ?? s.house_Name ?? q.houseName,
  address: s.address ?? s.ads ?? q.address,
  tel: s.tel ?? q.tel,
  bed: s.bed ?? q.bed,
  type: s.type ?? s.room ?? q.type,
  price: Number(s.price ?? q.price ?? 0),
};
// ==========================================

// 表單輸入欄位
const form = ref({
  checkin: "",
  checkout: "",
  // people: 1,

  // ==========================================
  // 優先用store的資料
  people: Number(s.people ?? q.people ?? 1)
  // ==========================================

});

// 傳送預覽訂單請求並導向下一頁
async function goToPreview() {
  error.value = "";
  // 前端驗證
  if (!form.value.checkin || !form.value.checkout || form.value.people < 1) {
    error.value = "請填寫完整的預訂資訊";
    return;
  }

  if (new Date(form.value.checkin) >= new Date(form.value.checkout)) {
    error.value = "退房日期必須晚於入住日期";
    return;
  }

  try {
    const payload = {
      listid: parseInt(listing.listId),
      checkindate: form.value.checkin,
      checkoutdate: form.value.checkout,
      people: form.value.people,
    };

    console.log("發送預覽請求:", payload); // 調試用

    const {data} = await axios.post("/api/orderconfirm/preview", payload);

    router.push({
      name: "PreviewConfirm",
      query: {
        listId: listing.listId,
        houseName: listing.houseName,
        address: listing.address,
        tel: listing.tel,
        bed: listing.bed,
        type: listing.type,
        price: listing.price,
        checkInDate: form.value.checkin,
        checkOutDate: form.value.checkout,
        guests: form.value.people,
        days: data.days,
        total: data.total,
        username: data.customer?.name || data.customer?.username,
      },
    });
  } catch (err) {
    console.error("預覽訂單錯誤:", err); // 調試用
    error.value = err.response?.data || err.message || "預覽訂單失敗";
  }
}
</script>