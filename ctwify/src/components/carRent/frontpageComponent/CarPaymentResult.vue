<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useBookingStore } from "@/stores/booking";

const store = useBookingStore();
const detail = computed(() => store.vehicleDraft || {});

const router = useRouter();
function goHome() {
  router.push("/");
}

function formatDateTime(d) {
  if (!d) return "—";
  const dt = new Date(d);
  return isNaN(dt) ? d : dt.toLocaleString("zh-TW");
}
function formatAmount(n) {
  return new Intl.NumberFormat("zh-TW").format(n || 0);
}

// 收放按鈕
const revealed = ref(false);
const toggle = () => {
  revealed.value = !revealed.value;
};
</script>

<template>
  <!-- 切換按鈕 -->
  <div class="text-center my-10">
    <v-btn
      :color="revealed ? 'grey-darken-1' : 'deep-orange-darken-1'"
      size="large"
      class="px-8"
      :prepend-icon="revealed ? 'mdi-eye-off' : 'mdi-eye'"
      @click="toggle"
    >
      {{ revealed ? "隱藏車輛資訊" : "顯示車輛資訊" }}
    </v-btn>
  </div>

  <!-- 收放內容 -->
  <v-expand-transition>
    <div v-show="revealed">
      <v-container class="py-6" max-width="1000">
        <v-card class="soft-card mb-4" elevation="2" rounded="xl">
          <v-card-title class="text-h6 d-flex align-center">
            <v-icon class="mr-2" color="deep-orange-accent-3">mdi-car</v-icon>
            <span class="card-title">車輛資訊</span>
            <v-spacer />
            <v-btn
              size="small"
              variant="text"
              prepend-icon="mdi-chevron-up"
              @click="toggle"
            >
              收合
            </v-btn>
          </v-card-title>

          <v-divider class="mx-4"></v-divider>

          <v-card-text class="py-4">
            <v-row>
              <v-col cols="12" md="6">
                <v-list density="comfortable" class="flat-list">
                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-identifier</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      租車訂單：<span class="value">{{
                        detail.reservationId || "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-calendar-start</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      取車時間：<span class="value">{{
                        formatDateTime(detail.pickupDateTime)
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-calendar-end</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      還車時間：<span class="value">{{
                        formatDateTime(detail.returnDateTime)
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-store-marker</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      取還車地點：<span class="value">{{
                        detail.locationName || "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-cash-multiple</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      車輛總金額：<span class="value"
                        >NT$ {{ formatAmount(detail.totalAmount) }}</span
                      >
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item v-if="detail.locationAddr">
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-map-marker</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      門市地址：<span class="value">{{
                        detail.locationAddr
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item v-if="detail.businessHours">
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-clock-outline</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      營業時間：<span class="value">{{
                        detail.businessHours
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-col>

              <v-col cols="12" md="6">
                <v-list density="comfortable" class="flat-list">
                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-account</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      駕駛人姓名：<span class="value">{{
                        detail.driverName ?? "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange">mdi-phone</v-icon></template
                    >
                    <v-list-item-title class="kv">
                      駕駛人電話：<span class="value">{{
                        detail.driverPhone ?? "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange">mdi-label</v-icon></template
                    >
                    <v-list-item-title class="kv">
                      車輛品牌：<span class="value">{{
                        detail.vehicleBrand ?? "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-car-info</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      車輛型號：<span class="value">{{
                        detail.vehicleModel ?? "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>

                  <v-list-item>
                    <template #prepend
                      ><v-icon color="deep-orange"
                        >mdi-palette</v-icon
                      ></template
                    >
                    <v-list-item-title class="kv">
                      車輛顏色：<span class="value">{{
                        detail.vehicleColor ?? "—"
                      }}</span>
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-container>
    </div>
  </v-expand-transition>
</template>

<style scoped>
.soft-card {
  background: #fff7ed;
}
.card-title {
  font-size: 25px;
  font-weight: 700;
  color: #7c2d12;
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
  word-break: break-all;
}
</style>
