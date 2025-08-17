<script setup>
import {useBookingStore} from '@/stores/booking'
import {useRouter} from 'vue-router'

const booking = useBookingStore()
const router = useRouter()

// 假資料（對應 LisBean）
const list = {
  listId: 101,
  host_Id: '8d7cc70f-6369-4a41-9a58-05F97ABFB688',
  reviewCount: 5,
  house_Name: '台北101豪景套房',
  ads: '台北市信義區信義路五段7號',
  room: '套房',
  bed: '一大床',
  describe: '高樓景觀、近捷運，含基本備品與 Wi-Fi',
  tel: '0912-345-678',
  ppl: 2,
  price: 2800,
  photo1: 'house1.jpg',
  approved: null,
  published: true,
  equipments: [{name: 'Wi-Fi'}, {name: '冷氣'}],
}
const base = '/images/listings/'

function testBook() {
  booking.setListing(list)      // 第一次：存整包房屋
  booking.resetVehicleDraft()   // 清空上一筆車輛草稿
  router.push({name: 'BookingStart1'})
}
</script>

<template>
  <v-container class="py-6">
    <v-card elevation="2">
      <v-card-title class="text-h6">房源測試預覽（假資料）</v-card-title>
      <v-card-text>
        <div class="mb-4">
          <div><b>listId：</b>{{ list.listId }}</div>
          <div><b>house_Name：</b>{{ list.house_Name }}</div>
          <div><b>price：</b>NT${{ list.price.toLocaleString() }}</div>
          <div class="mt-2">
            <v-img :src="base + list.photo1" height="200" cover class="rounded"/>
          </div>
        </div>
        <v-btn class="mt-3" color="primary" variant="tonal" @click="testBook">
          測試預訂（寫入 booking 並前往 BookingStart）
        </v-btn>
      </v-card-text>
    </v-card>
  </v-container>
</template>

