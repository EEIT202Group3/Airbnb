<template>
  <v-container class="py-10" max-width="900">
    <h2 class="mb-6">導向信用卡付款中…</h2>
    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <form
      v-if="form"
      ref="payForm"
      :action="form.action"
      method="POST"
      style="display: none"
    >
      <input name="MerchantID" :value="form.MerchantID" />
      <input name="TradeInfo" :value="form.TradeInfo" />
      <input name="TradeSha" :value="form.TradeSha" />
      <input name="Version" :value="form.Version" />
    </form>

    <v-progress-circular indeterminate v-if="!error" />
  </v-container>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { payNewebpayCheckout } from "./order";

const route = useRoute();
const payForm = ref(null);
const form = ref(null);
const error = ref("");

onMounted(async () => {
  try {
    const bookingId = route.query.bookingId || route.params.bookingId;
    if (!bookingId) {
      error.value = "缺少訂單編號，無法進入信用卡付款。";
      return;
    }
    form.value = await payNewebpayCheckout(String(bookingId));
    requestAnimationFrame(() => payForm.value?.submit());
  } catch (e) {
    error.value = e?.response?.data || e?.message || "建立信用卡交易失敗";
  }
});
</script>