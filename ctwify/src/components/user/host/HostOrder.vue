<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";

axios.defaults.withCredentials = true; // 一定要帶 cookie

// 後端 DTO
type HostOrder = {
  bookingId: string;
  username: string;
  housename: string;
  address: string;
  tel: string;
  bed: string;
  people: number;
  bookingstatus: string;
  checkindate: string | Date;
  checkoutdate: string | Date;
  grandtotal: number | string;
  hostnetamount?: number | string;
};

// 卡片要顯示的精簡欄位
type OrderCard = { id: string; amount: number; date: string | Date };

const orders = ref<OrderCard[]>([]);
const fullMap = ref<Record<string, HostOrder>>({}); // 以 bookingId 快速找到完整資料
const hovered = ref<string | null>(null);

const showDetail = ref(false);
const selectedOrder = ref<HostOrder | null>(null);
const error = ref<string | null>(null);

onMounted(loadOrders);

async function loadOrders() {
  try {
    const { data } = await axios.get<HostOrder[]>("/api/orderconfirm/byhost");
    const list = Array.isArray(data) ? data : [];

    // 建立快取 + 轉成卡片資料
    fullMap.value = Object.fromEntries(list.map((o) => [o.bookingId, o]));
    orders.value = list.map((o) => ({
      id: o.bookingId,
      amount: Number((o as any).grandtotal ?? 0),
      date: o.checkindate,
    }));
  } catch (e: any) {
    error.value = e?.response?.data || "無法取得訂單資料";
  }
}

function openDetail(id: string) {
  const full = fullMap.value[id];
  if (full) {
    selectedOrder.value = full;
    showDetail.value = true;
  }
}

function formatAmount(n?: number) {
  return new Intl.NumberFormat("zh-TW").format(n || 0);
}
function formatDate(v?: string | Date) {
  if (!v) return "-";
  return new Date(v).toLocaleString("zh-TW");
}
</script>

<template>
  <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
    <h1 style="font-weight: bolder">訂單總覽</h1>
    <v-divider class="my-6" />

    <!-- 錯誤提示（不影響版面樣式） -->
    <v-alert v-if="error" type="error" density="comfortable" class="mb-4">{{
      error
    }}</v-alert>

    <v-item-group mandatory>
      <v-container>
        <v-row>
          <v-col v-for="o in orders" :key="o.id" cols="12" md="4">
            <v-item>
              <v-card
                class="d-flex flex-column justify-center align-center"
                height="200"
                dark
                @click="openDetail(o.id)"
                @mouseover="hovered = o.id"
                @mouseleave="hovered = null"
                :style="
                  hovered === o.id
                    ? 'background-color:#F2AB27;color:white;'
                    : 'background-color: #FFFAF4;'
                "
              >
                <div>{{ o.id }}</div>
                <div>{{ formatAmount(o.amount) }}</div>
                <div>{{ formatDate(o.date) }}</div>
              </v-card>
            </v-item>
          </v-col>
        </v-row>
      </v-container>
    </v-item-group>
  </v-card>

  <!-- 詳細資訊 Dialog（不改動卡片樣式） -->
  <v-dialog v-model="showDetail" max-width="640">
    <v-card v-if="selectedOrder">
      <v-card-title class="text-h6">訂單明細</v-card-title>
      <v-card-text>
        <div><b>訂單編號：</b>{{ selectedOrder.bookingId }}</div>
        <div><b>住客姓名：</b>{{ selectedOrder.username }}</div>
        <div><b>房源名稱：</b>{{ selectedOrder.housename }}</div>
        <div><b>地址：</b>{{ selectedOrder.address }}</div>
        <div><b>電話：</b>{{ selectedOrder.tel }}</div>
        <div><b>房型：</b>{{ selectedOrder.bed }}</div>
        <div><b>人數：</b>{{ selectedOrder.people }}</div>
        <div><b>訂單狀態：</b>{{ selectedOrder.bookingstatus }}</div>
        <div><b>入住日期：</b>{{ formatDate(selectedOrder.checkindate) }}</div>
        <div><b>退房日期：</b>{{ formatDate(selectedOrder.checkoutdate) }}</div>
        <div>
          <b>總金額：</b
          >{{ formatAmount(Number(selectedOrder.grandtotal as any)) }}
        </div>
        <div v-if="selectedOrder.hostnetamount != null">
          <b>房東淨收：</b
          >{{ formatAmount(Number(selectedOrder.hostnetamount as any)) }}
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="showDetail = false">關閉</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.h5 {
  font-weight: bolder;
}
</style>
