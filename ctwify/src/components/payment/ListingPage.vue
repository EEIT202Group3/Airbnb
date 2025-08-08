<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>房源資訊</v-card-title>
          <v-card-text>
            <p>
              <strong>{{ listing.houseName }}</strong>
            </p>
            <p>{{ listing.description }}</p>
            <p>
              <strong>{{ listing.price }} 元/晚</strong>
            </p>
            <p>可容納: {{ listing.capacity }} 人</p>
          </v-card-text>
          <v-btn @click="goToBooking" color="primary">我要下訂</v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      listing: {}, // 房源資料
    };
  },
  created() {
    this.getListingData();
  },
  methods: {
    async getListingData() {
      // 模擬從後端獲取房源資料
      this.listing = {
        houseName: "美麗的海邊別墅",
        description: "這是一個風景如畫的海邊別墅，適合家庭或朋友度假。",
        price: 3500, // 每晚價格
        capacity: 6, // 可容納人數
      };
    },
    goToBooking() {
      // 當用戶點擊「我要下訂」時，進入結帳流程頁面
      this.$router.push({
        name: "booking",
        query: {
          listingId: "12345", // 房源 ID (假設)
        },
      });
    },
  },
};
</script>