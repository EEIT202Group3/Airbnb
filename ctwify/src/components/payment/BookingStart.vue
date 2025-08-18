<template>
  <v-container class="py-6" max-width="1000">
    <div class="header-row">
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-calendar-edit</v-icon
      >
      <h2 class="page-title">預約房源</h2>
    </div>

    <!-- 房源資訊 -->
    <v-card class="mb-4 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3">mdi-home-city</v-icon>
        <span class="card-title">房源資訊</span>
        <v-spacer />
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-3">
        <v-row>
          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-home-city-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >房名：<span class="value">{{
                    listing.houseName
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-map-marker</v-icon></template
                >
                <v-list-item-title class="kv"
                  >地址：<span class="value">{{
                    listing.address
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange">mdi-phone</v-icon></template
                >
                <v-list-item-title class="kv"
                  >電話：<span class="value">{{
                    listing.tel
                  }}</span></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" md="6">
            <v-list density="comfortable" class="flat-list">
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-bed-queen-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >床位：<span class="value">{{
                    listing.bed
                  }}</span></v-list-item-title
                >
              </v-list-item>

              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-home-outline</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >房型：<span class="value">{{
                    listing.type
                  }}</span></v-list-item-title
                >
              </v-list-item>
              <v-list-item>
                <template #prepend
                  ><v-icon color="deep-orange"
                    >mdi-currency-usd</v-icon
                  ></template
                >
                <v-list-item-title class="kv"
                  >房價：<span class="value"
                    >NT$ {{ formatAmount(listing.price) }}/晚</span
                  ></v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 輸入欄位 -->
    <v-card class="mb-4 soft-card" elevation="2" rounded="xl">
      <v-card-title class="text-h6 d-flex align-center">
        <v-icon class="mr-2" color="deep-orange-accent-3"
          >mdi-form-textbox</v-icon
        >
        <span class="card-title">請填寫預訂資訊</span>
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <v-card-text class="py-4">
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              label="入住日期"
              v-model="form.checkin"
              type="date"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-calendar-start"
              :min="today"
              class="mb-2"
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              label="退房日期"
              v-model="form.checkout"
              type="date"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-calendar-end"
              :min="form.checkin || today"
              class="mb-2"
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              label="入住人數"
              v-model.number="form.people"
              type="number"
              min="1"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-account-multiple"
              class="mb-2"
            />
          </v-col>
        </v-row>

        <div class="text-caption text-grey-darken-1 mb-4 d-flex align-center">
          <v-icon size="16" class="mr-1">mdi-information-outline</v-icon>
          退房日期需晚於入住日期；入住人數至少 1 人。
        </div>

        <v-btn
          color="deep-orange-darken-1"
          size="large"
          class="px-6"
          :disabled="submitting"
          :loading="submitting"
          @click="goToPreview"
        >
          <v-icon start>mdi-arrow-right-bold</v-icon>
          下一步：確認訂單
        </v-btn>
      </v-card-text>
    </v-card>

    <!-- 錯誤訊息 -->
    <v-alert
      type="error"
      v-if="error"
      class="mt-4"
      variant="tonal"
      border="start"
      color="red-darken-1"
      icon="mdi-alert-octagon-outline"
    >
      {{ error }}
    </v-alert>
  </v-container>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useBookingStore } from "@/stores/booking";

const route = useRoute();
const router = useRouter();
const booking = useBookingStore();
const error = ref("");
const submitting = ref(false);

// 從上一頁取得房源資訊
const listing = {
  listid: route.query.listid,
  houseName: route.query.houseName,
  address: route.query.address,
  tel: route.query.tel,
  bed: route.query.bed,
  type: route.query.type,
  price: parseInt(route.query.price),
};

const form = ref({
  checkin: "",
  checkout: "",
  people: 1,
});

const today = computed(() => new Date().toISOString().slice(0, 10));

async function goToPreview() {
  error.value = "";

  // 前端驗證
  if (!form.value.checkin || !form.value.checkout || form.value.people < 1) {
    error.value = "請填寫完整的預訂資訊";
    return;
  }
  if (new Date(form.value.checkin) >= new Date(form.value.checkout)) {
    error.value = "退房日期必須晚於入住日期";
    return;
  }

  try {
    submitting.value = true;

    booking.setListing(listing);
    booking.setBookingParams({
      listid: Number(listing.listid),
      checkInDate: form.value.checkin,
      checkOutDate: form.value.checkout,
      guests: Number(form.value.people),
    });
    router.push({ name: "PreviewConfirm" });
  } catch (err) {
    error.value = err?.message || "預覽訂單失敗";
  } finally {
    submitting.value = false;
  }
}

function formatAmount(n) {
  return new Intl.NumberFormat("zh-TW").format(n || 0);
}
</script>

<style scoped>
.soft-card {
  background: #fff7ed;
}
.header-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #c2410c;
  margin: 0;
}
.card-title {
  font-size: 25px;
  font-weight: 700;
}
.flat-list {
  --v-list-padding-start: 0;
  --v-list-padding-end: 0;
}
.kv {
  font-size: 20px;
}
.kv .value {
  font-weight: 700;
  color: #7c2d12;
}
</style>