<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>付款成功</v-card-title>
          <v-card-text>
            <p>訂單編號: {{ result.orderId }}</p>
            <p>付款金額: {{ result.total }} 元</p>
            <p>付款方式: {{ result.paymentMethod }}</p>
            <v-btn @click="goToOrderDetail" color="primary">查看訂單詳情</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      result: this.$route.params.result,
    };
  },
  methods: {
    goToOrderDetail() {
      this.$router.push({
        name: "orderDetail",
        params: { orderId: this.result.orderId },
      });
    },
  },
};
</script>