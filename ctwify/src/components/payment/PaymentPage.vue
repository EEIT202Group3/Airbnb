<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>付款</v-card-title>
          <v-card-text>
            <v-btn @click="processPayment" color="primary">提交付款</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  methods: {
    async processPayment() {
      const paymentData = {
        // 假資料
      };

      const response = await fetch("/api/ecpay/goPay", {
        method: "POST",
        body: JSON.stringify(paymentData),
      });

      const result = await response.json();
      if (result.success) {
        this.$router.push({ name: "paymentSuccess", params: { result } });
      } else {
        this.$router.push({ name: "paymentFailed" });
      }
    },
  },
};
</script>