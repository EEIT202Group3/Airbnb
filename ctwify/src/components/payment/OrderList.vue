<template>
  <v-container class="py-6" max-width="1000">
    <h2 class="mb-4">我的訂單</h2>

    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <!-- 訂單卡片 -->
    <v-card
      v-for="order in orders"
      :key="order.bookingId"
      class="mb-4"
      color="orange-lighten-5"
      @click="openDetail(order.bookingId)"
      style="cursor: pointer"
    >
      <v-card-title class="text-h6">
        訂單編號：{{ order.bookingId }}
      </v-card-title>
      <v-card-text>
        <ul class="ma-0 pl-4">
          <li>房名：{{ order.housename }}</li>
          <li>地址：{{ order.address }}</li>
          <li>電話：{{ order.tel }}</li>
          <li>房型：{{ order.bed }}</li>
          <li>人數：{{ order.people }}</li>
          <li>入住日期：{{ formatDate(order.checkindate) }}</li>
          <li>退房日期：{{ formatDate(order.checkoutdate) }}</li>
          <li>總金額：$ {{ order.grandtotal }}</li>
          <li>付款狀態：{{ order.bookingstatus }}</li>
        </ul>
      </v-card-text>
    </v-card>

    <div v-if="orders.length === 0 && !error">目前沒有訂單紀錄。</div>

    <!-- 明細 Dialog -->
    <v-dialog v-model="showDetail" max-width="600">
      <v-card>
        <v-card-title class="text-h6">訂單明細</v-card-title>

        <!-- 載入中 -->
        <v-card-text v-if="detailLoading">
          <v-skeleton-loader type="article, actions" />
        </v-card-text>

        <!-- 真的有資料再渲染內容，避免 undefined -->
        <v-card-text v-else-if="selectedOrder">
          <ul class="ma-0 pl-4">
            <li>房名：{{ selectedOrder.housename }}</li>
            <li>租車明細：{{ selectedOrder.reservationId }}</li>
            <li>地址：{{ selectedOrder.address }}</li>
            <li>電話：{{ selectedOrder.tel }}</li>
            <li>房型：{{ selectedOrder.bed }}</li>
            <li>人數：{{ selectedOrder.people }}</li>
            <li>入住日期：{{ formatDate(selectedOrder.checkindate) }}</li>
            <li>退房日期：{{ formatDate(selectedOrder.checkoutdate) }}</li>
            <li>取車地點：{{ selectedOrder.locationId }}</li>
            <li>付款訂單：{{ selectedOrder.paymentId }}</li>
            <li>租房金額：$ {{ selectedOrder.roomprice || 0 }}</li>
            <li>
              租車金額：$
              {{ selectedOrder.cartotal || selectedOrder.carTotal || 0 }}
            </li>
            <li>
              總金額：$
              {{ selectedOrder.grandtotal || selectedOrder.grandTotal || 0 }}
            </li>
            <li>付款時間：{{ formatDateTime(selectedOrder.paidTime) }}</li>
            <li>訂單狀態：{{ selectedOrder.bookingStatus }}</li>
            <li>付款方式：{{ selectedOrder.bookingMethod }}</li>
            <li>付款狀態：{{ selectedOrder.mentStatus }}</li>
          </ul>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="orange" text @click="showDetail = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "OrderList",
  data() {
    return {
      orders: [],
      error: "",
      showDetail: false,
      detailLoading: false,
      selectedOrder: null,
    };
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      try {
        const res = await axios.get("/api/orderconfirm/byCustomer", {
          withCredentials: true,
        });
        console.log("byCustomer =>", res.data);
        this.orders = res.data; // 確保後端欄位是 camelCase
      } catch (err) {
        this.error = err.response?.data?.error || "無法取得訂單資料";
      }
    },

    // 先開 dialog + loading，再打 API；回來後再塞資料
    async openDetail(bookingId) {
      this.showDetail = true;
      this.detailLoading = true;
      this.selectedOrder = null;
      try {
        const res = await axios.get("/api/orderconfirm/detail", {
          params: { bookingId: bookingId }, // 改為 bookingId（與後端一致）
          withCredentials: true,
        });
        console.log("detail =>", res.data);
        this.selectedOrder = res.data;
      } catch (err) {
        this.showDetail = false;
        alert("取得明細失敗：" + (err.response?.data?.error || "未知錯誤"));
      } finally {
        this.detailLoading = false;
      }
    },

    formatDate(v) {
      if (!v) return "-";
      return new Date(v).toLocaleDateString("zh-TW");
    },
    formatDateTime(v) {
      if (!v) return "-";
      return new Date(v).toLocaleString("zh-TW");
    },
  },
};
</script>