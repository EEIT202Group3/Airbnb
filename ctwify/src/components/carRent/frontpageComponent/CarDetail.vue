<script setup lang="ts">
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import {useBookingStore} from '@/stores/booking'
import {ref, onMounted, computed} from "vue";
import {useRoute} from "vue-router";
import router from "@/router";
import api from "@/api";

const props = defineProps<{ id: string | number }>();

const route = useRoute();
const pickupDateTime = (route.query.pickupDateTime as string) || "";
const returnDateTime = (route.query.returnDateTime as string) || "";
const locationId = route.query.locationId ? Number(route.query.locationId) : null;

// 車輛
const vehicle = ref<any | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

// 門市
const locationName = ref<string>("");
const locationAddr = ref<string>("");
const businessHours = ref<string>("");

// 表單
const formRef = ref<any>(null);
const firstName = ref("");
const lastName = ref("");
const email = ref("");
const phone = ref("");
const license = ref("");
const driverAge = ref("25-60");

// 驗證規則
const required = (v: any) => (!!v || v === 0 ? true : "必填");
const emailRule = (v: string) => !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || "Email 格式不正確";
const phoneRule = (v: string) => !v || /^09\d{2}-?\d{3}-?\d{3}$/.test(v) || "手機格式 09xx-xxx-xxx";
const licenseRule = (v: string) => !v || /^[A-Z]\d{9}$/.test(v) || "駕照格式 A123456789";

onMounted(async () => {
  try {
    // 車輛
    const vres = await api.get(`/vehicles1/${props.id}`);
    vehicle.value = vres.data;

    if (locationId) {
      try {
        const lres = await api.get(`/locations/${locationId}`);
        const loc = lres.data;
        locationName.value = loc?.name ?? "";
        locationAddr.value = loc?.address ?? "";
        businessHours.value = loc?.businessHours ?? "";
      } catch (e) {
        console.warn("讀取門市失敗/未設定 API：", e);
      }
    }
  } catch (err) {
    console.error("載入車輛失敗", err);
    error.value = "查無此車輛資料";
  } finally {
    loading.value = false;
  }
});

const fmtDateTime = (s: string) => (s ? new Date(s).toLocaleString() : "");

// 金額計算（至少 1 天）
const rentalDays = computed(() => {
  if (!pickupDateTime || !returnDateTime) return 1;
  const start = new Date(pickupDateTime).getTime();
  const end = new Date(returnDateTime).getTime();
  const diff = end - start;
  const dayMs = 1000 * 60 * 60 * 24;
  if (Number.isNaN(diff) || diff <= 0) return 1;
  return Math.max(1, Math.ceil(diff / dayMs));
});

const totalAmount = computed(() => {
  if (!vehicle.value) return 0;
  return rentalDays.value * Number(vehicle.value.dailyRent ?? 0);
});

// 送出
async function submitReservation() {
  if (!vehicle.value) return
  const {valid} = await formRef.value.validate()
  if (!valid) return

  const payload = {
    pickupDate: pickupDateTime,
    returnDate: returnDateTime,
    license: license.value,
    driverName: lastName.value + firstName.value,
    driverEmail: email.value,
    driverPhone: phone.value,
    driverAge: driverAge.value,
    totalAmount: totalAmount.value,
    vehicleId: vehicle.value.vehicleId,
    status: "未付款",
    locationId: locationId ?? null,
  };

  try {
    const res = await api.post("/reservations1/insert", payload);
    console.log("新增成功", res.data);
    alert("預約成功！");

    // 取成新的ID
    const rid =
        res?.data?.reservationId ??
        res?.data?.id ??
        null;
    console.log(rid);

    // 將資料存到booking.js
    booking.setVehicleId(vehicle.value.vehicleId)
    booking.setDates({
      pickupDateTime,
      returnDateTime,
    })
    booking.setLocation({
      locationId: locationId ?? null,
      locationName: locationName.value,
      locationAddr: locationAddr.value,
      businessHours: businessHours.value,
    })
    booking.setTotalAmount(totalAmount.value)
    booking.setReservationId(rid)

    await router.push("/preview-confirm");
  } catch (err) {
    console.error("新增失敗", err);
    alert("預約失敗，請稍後再試");
  }
}

// 回傳車輛資訊給booking.js
const booking = useBookingStore()
const payload = {
  pickupDate: booking.vehicleDraft.pickupDateTime,
  returnDate: booking.vehicleDraft.returnDateTime,
  license: license.value,
  driverName: lastName.value + firstName.value,
  driverEmail: email.value,
  driverPhone: phone.value,
  driverAge: driverAge.value,
  totalAmount: booking.vehicleDraft.totalAmount,
  vehicleId: booking.vehicleDraft.vehicleId,
  status: '未付款',
  locationId: booking.vehicleDraft.locationId ?? null,
}

</script>

<template>
  <!--  <NavigationBar />-->

  <v-container class="py-6">
    <div class="page-wrap">
      <!-- 載入中 -->
      <div v-if="loading" class="text-center my-10">
        <v-progress-circular indeterminate/>
        <div class="mt-2 text-medium-emphasis">載入中...</div>
      </div>

      <!-- 錯誤 -->
      <v-alert v-else-if="error" type="error" variant="tonal" class="my-4">
        {{ error }}
      </v-alert>

      <!-- 內容 -->
      <template v-else>
        <v-row v-if="vehicle" dense>
          <!-- 車輛資訊 -->
          <v-col cols="12">
            <v-card elevation="2" class="mb-4 overflow-hidden">
              <v-card-title class="card-title">
                車輛詳細資訊
              </v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="4">
                    <v-img
                        :src="`/carPicture/${vehicle.image}`"
                        :alt="`${vehicle.brand} ${vehicle.model}`"
                        height="260"
                        cover
                        class="rounded"
                    />
                  </v-col>
                  <v-col cols="12" md="8">
                    <div class="text-h5 font-weight-bold mb-3">
                      {{ vehicle.brand }} {{ vehicle.model }}
                    </div>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">車牌號碼：</v-col>
                      <v-col cols="7" md="9">{{ vehicle.plateNo }}</v-col>
                    </v-row>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">顏色 / 座位數：</v-col>
                      <v-col cols="7" md="9">{{ vehicle.color }} / {{ vehicle.seatCapacity }} 人座</v-col>
                    </v-row>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">變速系統 / 油種：</v-col>
                      <v-col cols="7" md="9">{{ vehicle.transmission }} / {{ vehicle.fuelType }}</v-col>
                    </v-row>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">油箱容量：</v-col>
                      <v-col cols="7" md="9">{{ vehicle.fuelCapacity }} 公升</v-col>
                    </v-row>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">里程數：</v-col>
                      <v-col cols="7" md="9">{{ vehicle.mileage?.toLocaleString?.() }} 公里</v-col>
                    </v-row>

                    <v-row class="mb-1">
                      <v-col cols="5" md="3" class="text-medium-emphasis">租金：</v-col>
                      <v-col cols="7" md="9" class="text-success font-weight-bold">
                        NT${{ vehicle.dailyRent?.toLocaleString?.() }} / 日
                      </v-col>
                    </v-row>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- 取還車資訊 -->
          <v-col cols="12">
            <v-card elevation="2" class="mb-4">
              <v-card-title class="card-title-flat">
                取車及還車
              </v-card-title>
              <v-card-text class="py-4">
                <div class="mb-1">
                  <v-chip size="small" class="mr-2" variant="elevated">取車</v-chip>
                  <span>{{ fmtDateTime(pickupDateTime) }}</span>
                </div>
                <div class="mb-1">
                  <v-chip size="small" class="mr-2" variant="elevated">還車</v-chip>
                  <span>{{ fmtDateTime(returnDateTime) }}</span>
                </div>
                <v-divider class="my-3"/>
                <div v-if="locationName">地點：<b>{{ locationName }}</b></div>
                <div v-if="locationAddr" class="text-medium-emphasis">地址：{{ locationAddr }}</div>
                <div v-if="businessHours" class="text-medium-emphasis">營業時間：{{ businessHours }}</div>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- 駕駛人資料 -->
          <v-col cols="12">
            <v-card elevation="2" class="mb-4">
              <v-card-title class="card-title-flat">
                駕駛人資料
              </v-card-title>
              <v-card-text>
                <v-form ref="formRef" @submit.prevent>
                  <v-row dense>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="lastName" label="姓氏" placeholder="請以中文輸入"
                                    :rules="[required]" variant="outlined" density="comfortable"/>
                    </v-col>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="firstName" label="名字" placeholder="請以中文輸入"
                                    :rules="[required]" variant="outlined" density="comfortable"/>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field v-model="email" label="電子郵件" placeholder="email@example.com"
                                    :rules="[required, emailRule]" variant="outlined" density="comfortable"/>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field v-model="phone" label="手機號碼" placeholder="09xx-xxx-xxx"
                                    :rules="[required, phoneRule]" variant="outlined" density="comfortable"/>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field v-model="license" label="駕照號碼" placeholder="A123456789"
                                    :rules="[required, licenseRule]" variant="outlined" density="comfortable"/>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-select v-model="driverAge" :items="['18-24','25-60','60+']" label="駕駛年齡"
                                :rules="[required]" variant="outlined" density="comfortable"/>
                    </v-col>

                    <!-- 隱藏欄位 -->
                    <input type="hidden" name="vehicleId" :value="vehicle?.vehicleId"/>
                    <input type="hidden" name="pickupDateTime" :value="pickupDateTime"/>
                    <input type="hidden" name="returnDateTime" :value="returnDateTime"/>
                  </v-row>
                </v-form>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- 價格明細 -->
          <v-col cols="4">
            <v-card elevation="2" class="mb-6">
              <v-card-title class="card-title-flat">
                價格明細
              </v-card-title>
              <v-card-text>
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>租用天數</v-list-item-title>
                    <v-list-item-subtitle>{{ rentalDays }} 天</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>每日租金</v-list-item-title>
                    <v-list-item-subtitle>
                      NT${{ vehicle.dailyRent?.toLocaleString?.() }}
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>

                <v-divider class="my-3"/>
                <div class="d-flex justify-space-between align-center">
                  <div class="text-subtitle-1 font-weight-bold">總計</div>
                  <div class="text-h6 font-weight-bold text-orange-darken-2">
                    NT${{ totalAmount.toLocaleString() }} 元
                  </div>
                </div>
              </v-card-text>

              <v-card-actions class="pa-4">
                <v-btn
                    class="cta-button"
                    size="large"
                    block
                    @click="submitReservation"
                    prepend-icon="mdi-cart"
                >
                  預定車輛
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </template>
    </div>
  </v-container>
</template>

<style scoped>
.page-wrap {
  width: 70%;
  margin-left: auto;
  margin-right: auto;
}

.card-title {
  color: #fff;
  background: linear-gradient(
      90deg,
      #ff9800,
      rgba(255, 152, 0, 0.15)
  );
}

.card-title-flat {
  background: #f6f6f6;
}

.cta-button {
  background: linear-gradient(
      90deg,
      #ff9800,
      #f57c00
  );
  color: #fff !important;
  font-weight: 700;
  letter-spacing: .5px;
  border-radius: 999px;
  box-shadow: 0 6px 16px rgba(255, 152, 0, 0.35);
}

.cta-button .v-btn__content {
  color: #fff !important;
}

.cta-button:hover {
  filter: brightness(1.03);
}

@media (max-width: 1024px) {
  .page-wrap {
    width: 100%;
  }
}

.font-weight-bold {
  font-weight: 700;
}

.position-sticky {
  position: sticky;
}
</style>
