<template>
  <v-container class="py-6" v-if="listing">
    <!-- 房源資訊區塊 -->
    <v-row>
      <v-col cols="12">
        <v-img
          :src="listing.image"
          aspect-ratio="2.1"
          class="rounded-lg mb-4"
          cover
        />
      </v-col>

      <v-col cols="12" md="8">
        <div class="text-h6 mb-1">{{ listing.title }}</div>
        <div class="text-body-2 text-medium-emphasis">
          地址：{{ listing.address }}
        </div>
        <div class="text-body-2 text-medium-emphasis">
          房型：{{ listing.type }}　床位：{{ listing.bed }}　入住人數：{{
            listing.capacity
          }}
        </div>
        <div class="text-body-2 text-medium-emphasis mb-4">
          聯絡電話：{{ listing.phone }}
        </div>
      </v-col>

      <!-- 預訂卡片 -->
      <v-col cols="12" md="4">
        <v-card class="pa-4">
          <!-- 使用者輸入入住資料 -->
          <v-text-field v-model="checkIn" label="入住日期" type="date" />
          <v-text-field v-model="checkOut" label="退房日期" type="date" />
          <v-text-field
            v-model.number="guests"
            label="入住人數"
            type="number"
            :max="listing.capacity"
          />

          <v-divider class="my-2" />

          <!-- 價格區塊 -->
          <div class="d-flex justify-space-between mb-2">
            <span>晚數</span><span>{{ nights }} 晚</span>
          </div>
          <div class="d-flex justify-space-between mb-4">
            <span>總金額</span><span>NT$ {{ total }}</span>
          </div>

          <!-- 預定按鈕 -->
          <v-btn
            block
            color="orange-darken-2"
            size="large"
            @click="goToBooking"
          >
            預定
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
axios.defaults.withCredentials = true;

// 假資料格式
const listing = ref({
  id: 7,
  title: "全木質、暖泥作 自帶庭園",
  address: "宜蘭縣礁溪鄉快樂村35號",
  type: "獨立套房",
  bed: "單人床",
  capacity: 1,
  phone: "0900000000",
  pricePerNight: 1542,
  image: "https://picsum.photos/seed/room1/1200/600",
});

// 表單欄位
const checkIn = ref("");
const checkOut = ref("");
const guests = ref(2);

const nights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0;
  const inD = new Date(checkIn.value);
  const outD = new Date(checkOut.value);
  const ms = outD.getTime() - inD.getTime();
  return ms > 0 ? Math.ceil(ms / (1000 * 60 * 60 * 24)) : 0;
});

const total = computed(() => {
  return nights.value * listing.value.pricePerNight;
});

function goToBooking() {
  router.push({
    name: "BookingStart",
    query: {
      listid: listing.value.id,
      houseName: listing.value.title,
      address: listing.value.address,
      bed: listing.value.bed,
      type: listing.value.type,
      tel: listing.value.phone,
      price: listing.value.pricePerNight,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guests: guests.value,
    },
  });
}
</script>
