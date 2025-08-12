<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { finalizeOrder } from "@/api/orders";
import ListingCard from "@/components/ListingCard.vue";

const router = useRouter();
const route = useRoute();

// 從上一頁帶來的資料（透過 router state）
const previewPayload = route.state?.previewPayload;
const previewResult = route.state?.previewResult;

if (!previewPayload || !previewResult) {
  // 若使用者 F5 造成 state 消失，建議導回起始頁或重新打 preview
  router.replace({ name: "BookingStart" });
}

const payMethod = ref("CREDIT_NEWEBPAY"); // 'CREDIT_NEWEBPAY' | 'CASH'
const loading = ref(false);
const error = ref("");

const summary = computed(() => ({
  days: previewResult?.days,
  total: previewResult?.total,
  checkin: previewResult?.checkindate,
  checkout: previewResult?.checkoutdate,
  people: previewResult?.people,
}));

async function confirmOrder() {
  error.value = "";
  loading.value = true;
  try {
    // finalize 需要的 DTO：沿用你後端的 OrderRequestDto 欄位
    const payload = {
      ...previewPayload,
      // 你後端目前 DTO 沒有 bookingMethod 欄位；如果有，請後端加上並存DB
      // 這裡先不帶，僅在前端分流流程
    };
    const { bookingId } = await finalizeOrder(payload);

    if (payMethod.value === "CREDIT_NEWEBPAY") {
      router.push({ name: "PayRedirect", query: { bookingId } });
    } else {
      router.push({ name: "PaymentDone", params: { bookingId } });
    }
  } catch (e) {
    error.value = e.message || "建立訂單失敗";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <v-container class="py-6" max-width="1000">
    <h2 class="mb-4">確認訂單</h2>
    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

    <ListingCard
      v-if="previewResult?.listing"
      :listing="previewResult.listing"
      :username="
        previewResult.customer?.username || previewResult.customer?.name
      "
    />

    <v-card class="mb-4">
      <v-card-title class="text-h6">入住資訊</v-card-title>
      <v-card-text>
        <div>入住日：{{ summary.checkin }}</div>
        <div>退房日：{{ summary.checkout }}</div>
        <div>入住人數：{{ summary.people }}</div>
        <div>入住天數：{{ summary.days }} 晚</div>
      </v-card-text>
    </v-card>

    <v-card class="mb-6">
      <v-card-title class="text-h6">金額</v-card-title>
      <v-card-text>
        <div class="text-h5 font-weight-bold">
          應付金額：NT$ {{ summary.total }}
        </div>
        <div class="text-caption text-grey mt-2">
          實際金額以後端計算為準，已防止前端竄改
        </div>
      </v-card-text>
    </v-card>

    <v-card class="mb-6">
      <v-card-title class="text-h6">付款方式</v-card-title>
      <v-card-text>
        <v-radio-group v-model="payMethod" inline>
          <v-radio label="信用卡（藍新）" value="CREDIT_NEWEBPAY" />
          <v-radio label="現金付款" value="CASH" />
        </v-radio-group>
      </v-card-text>
    </v-card>

    <v-btn color="primary" :loading="loading" @click="confirmOrder">
      確認下單
    </v-btn>
    <v-btn class="ml-3" variant="text" @click="$router.back()">返回修改</v-btn>
  </v-container>
</template>