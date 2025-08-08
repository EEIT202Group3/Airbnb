<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>房源資訊</v-card-title>
          <v-card-text>
            <!-- 顯示使用者資訊 -->
            <p><strong>客戶名稱：</strong>{{ user.username }}</p>
            <p><strong>電子郵件：</strong>{{ user.customerId }}</p>
          </v-card-text>

          <!-- 顯示房源資訊 -->
          <p>
            <strong>{{ order.houseName }}</strong>
          </p>
          <p>{{ order.address }}</p>
          <p>每晚價格：{{ order.price }} 元</p>
          <p>房東電話：{{ order.tel }}</p>
          <p>房型：{{ order.bed }}</p>
          <p>入住人數：{{ order.people }}</p>

          <!-- 表單輸入日期與人數 -->
          <v-form @submit.prevent="nextPage">
            <v-text-field
              label="入住日期"
              v-model="checkindate"
              type="datetime-local"
              required
            ></v-text-field>
            <v-text-field
              label="退房日期"
              v-model="checkoutdate"
              type="datetime-local"
              required
            ></v-text-field>
            <v-text-field
              label="人數"
              v-model="people"
              type="number"
              min="1"
              required
            ></v-text-field>
            <v-btn type="submit" color="primary">下一步</v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      order: {}, // 訂單資料 (房源與使用者)
      user: {}, // 使用者資料
      checkindate: "",
      checkoutdate: "",
      people: 1,
    };
  },
  created() {
    this.getOrderData();
    this.getUserData();
  },
  methods: {
    async getOrderData() {
      // 模擬從後端 API 獲取訂單資料 (假資料)
      this.order = {
        bookingId: "12345",
        houseName: "美麗的海邊別墅",
        address: "台灣高雄市海邊路123號",
        tel: "0912345678",
        bed: "雙人床",
        price: 3500,
        people: 2,
      };
    },
    async getUserData() {
      // 模擬從後端獲取使用者資料 (假資料)
      this.user = {
        username: "測試用戶",
        customerId: "testuser@example.com",
      };
    },
    nextPage() {
      // 進入到下一頁 (計算金額)
      this.$router.push({
        name: "confirmation",
        query: {
          checkindate: this.checkindate,
          checkoutdate: this.checkoutdate,
          people: this.people,
          houseName: this.order.houseName,
          price: this.order.price,
        },
      });
    },
  },
};
</script>