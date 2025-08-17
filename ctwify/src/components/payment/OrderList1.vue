<!-- src/components/order/OrderList1.vue -->
<script setup>
import { useBookingStore } from "@/stores/booking"

const booking = useBookingStore()
</script>

<template>
  <v-container class="py-6" max-width="800">
    <h2 class="mb-4">目前本地訂單資料（Pinia）</h2>

    <v-alert v-if="!booking.selectedListing && !booking.vehicleDraft" type="info">
      尚未選擇任何房屋或車輛
    </v-alert>

    <!-- 房屋資料 -->
    <v-card v-if="booking.selectedListing" class="mb-4" color="grey-lighten-4">
      <v-card-title class="text-h6">房屋資訊</v-card-title>
      <v-card-text>
        <ul class="ma-0 pl-4">
          <li>房名：{{ booking.selectedListing.house_Name || booking.selectedListing.houseName }}</li>
          <li>地址：{{ booking.selectedListing.ads || booking.selectedListing.address }}</li>
          <li>房型：{{ booking.selectedListing.bed }}</li>
          <li>人數：{{ booking.selectedListing.ppl || booking.selectedListing.people }}</li>
          <li>入住日期：{{ booking.selectedListing.checkInDate || '-' }}</li>
          <li>退房日期：{{ booking.selectedListing.checkOutDate || '-' }}</li>
          <li>金額：NT$ {{ booking.selectedListing.price }}</li>
        </ul>
      </v-card-text>
    </v-card>

    <!-- 車輛資料 -->
    <v-card v-if="booking.vehicleDraft && booking.vehicleDraft.vehicleId" class="mb-4" color="blue-lighten-5">
      <v-card-title class="text-h6">車輛資訊</v-card-title>
      <v-card-text>
        <ul class="ma-0 pl-4">
          <li>車輛ID：{{ booking.vehicleDraft.vehicleId }}</li>
          <li>取車時間：{{ booking.vehicleDraft.pickupDateTime }}</li>
          <li>還車時間：{{ booking.vehicleDraft.returnDateTime }}</li>
          <li>地點：{{ booking.vehicleDraft.locationName }}</li>
          <li>租金：NT$ {{ booking.vehicleDraft.totalAmount }}</li>
        </ul>
      </v-card-text>
    </v-card>

    <!-- 清空 -->
    <v-btn v-if="booking.selectedListing || booking.vehicleDraft"
           color="grey"
           variant="tonal"
           @click="booking.clearAll()">
      清空本地資料
    </v-btn>
  </v-container>
</template>
