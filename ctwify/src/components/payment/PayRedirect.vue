<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { payNewebpayCheckout } from "@/api/orders";

const route = useRoute();
const bookingId = route.query.bookingId;

const error = ref("");
const loading = ref(true);

onMounted(async () => {
  try {
    if (!bookingId) throw new Error("缺少 bookingId");
    const data = await payNewebpayCheckout(String(bookingId));

    const form = document.createElement("form");
    form.method = "POST";
    form.action = data.Gateway; // https://ccore.newebpay.com/MPG/mpg_gateway

    addHidden(form, "MerchantID", data.MerchantID);
    addHidden(form, "TradeInfo", data.TradeInfo);
    addHidden(form, "TradeSha", data.TradeSha);
    addHidden(form, "Version", data.Version);

    document.body.appendChild(form);
    form.submit();
  } catch (e) {
    error.value = e.message || "導向金流失敗";
  } finally {
    loading.value = false;
  }
});

function addHidden(form, name, value) {
  const input = document.createElement("input");
  input.type = "hidden";
  input.name = name;
  input.value = value;
  form.appendChild(input);
}
</script>

<template>
  <v-container class="py-10 text-center">
    <v-progress-circular v-if="loading" indeterminate size="56" class="mb-4" />
    <div v-if="loading">正在導向藍新金流頁面…</div>
    <v-alert v-else-if="error" type="error" class="mx-auto" max-width="600">
      {{ error }}
    </v-alert>
  </v-container>
</template>