<script setup lang="ts">
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import router from "@/router";
import api from "@/api";

const props = defineProps<{
  id: string | number;
}>();

const route = useRoute();
const pickupDateTime = (route.query.pickupDateTime as string) || "";
const returnDateTime = (route.query.returnDateTime as string) || "";

const vehicle = ref<any | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

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
const emailRule = (v: string) =>
    !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || "Email 格式不正確";
const phoneRule = (v: string) =>
    !v || /^09\d{2}-?\d{3}-?\d{3}$/.test(v) || "手機格式 09xx-xxx-xxx";
const licenseRule = (v: string) =>
    !v || /^[A-Z]\d{9}$/.test(v) || "駕照格式 A123456789";

onMounted(async () => {
  try {
    const res = await api.get(`/vehicles/${props.id}`);
    vehicle.value = res.data;
  } catch (err) {
    console.error("載入車輛失敗", err);
    error.value = "查無此車輛資料";
  } finally {
    loading.value = false;
  }
});

// 金額計算
const rentalDays = computed(() => {
  if (!pickupDateTime || !returnDateTime) return 1;
  const pickup = new Date(pickupDateTime);
  const returnD = new Date(returnDateTime);
  const msPerDay = 1000 * 60 * 60 * 24;
  const diffInMs = returnD.getTime() - pickup.getTime();
  return Math.max(1, Math.ceil(diffInMs / msPerDay));
});

const totalAmount = computed(() => {
  if (!vehicle.value) return 0;
  return rentalDays.value * vehicle.value.dailyRent;
});

// 送出
async function submitReservation() {
  if (!vehicle.value) return;

  const { valid } = await formRef.value.validate();
  if (!valid) return;

  const formData = {
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
    locationId: 1,
  };

  try {
    const res = await api.post("/reservations/insert", formData);
    console.log("新增成功", res.data);
    alert("預約成功！");
    await router.push("/car-front-homepage");
  } catch (err) {
    console.error("新增失敗", err);
    alert("預約失敗，請稍後再試");
  }
}
</script>

<template>
  <NavigationBar />

  <v-container class="py-6">
    <!-- 載入中 -->
    <div v-if="loading" class="text-center my-10">
      <v-progress-circular indeterminate />
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
          <v-card elevation="2" class="mb-4">
            <v-card-title class="text-white" style="background: var(--v-theme-primary);">
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
            <v-card-title>取車及還車</v-card-title>
            <v-card-text>
              <div>取車時間：{{ pickupDateTime.replace('T', ' ') }}</div>
              <div>還車時間：{{ returnDateTime.replace('T', ' ') }}</div>
              <div>營業時間：07:30 - 22:00</div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 表單 + 價格側欄 -->
        <v-col cols="12" md="8">
          <v-card elevation="2" class="mb-4">
            <v-card-title>駕駛人資料</v-card-title>
            <v-card-text>
              <v-form ref="formRef" @submit.prevent="submitReservation">
                <v-row dense>
                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model="lastName"
                        label="姓氏"
                        placeholder="請以中文輸入"
                        :rules="[required]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model="firstName"
                        label="名字"
                        placeholder="請以中文輸入"
                        :rules="[required]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model="email"
                        label="電子郵件"
                        placeholder="email@example.com"
                        :rules="[required, emailRule]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model="phone"
                        label="手機號碼"
                        placeholder="09xx-xxx-xxx"
                        :rules="[required, phoneRule]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                        v-model="license"
                        label="駕照號碼"
                        placeholder="A123456789"
                        :rules="[required, licenseRule]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-select
                        v-model="driverAge"
                        :items="['18-24','25-60','60+']"
                        label="駕駛年齡"
                        :rules="[required]"
                        variant="outlined"
                        density="comfortable"
                    />
                  </v-col>

                  <!-- 隱藏欄位 -->
                  <input type="hidden" name="vehicleId" :value="vehicle?.vehicleId" />
                  <input type="hidden" name="pickupDateTime" :value="pickupDateTime" />
                  <input type="hidden" name="returnDateTime" :value="returnDateTime" />
                </v-row>

                <v-divider class="my-4" />

                <v-btn color="primary" type="submit">預定車輛</v-btn>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" md="4">
          <v-card elevation="2" class="position-sticky" style="top: 16px;">
            <v-card-title>價格明細</v-card-title>
            <v-card-text>
              <v-list density="compact">
                <v-list-item>
                  <v-list-item-title>租用天數</v-list-item-title>
                  <v-list-item-subtitle>{{ rentalDays }} 天</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>每日租金</v-list-item-title>
                  <v-list-item-subtitle>NT${{ vehicle.dailyRent?.toLocaleString?.() }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
              <v-divider class="my-3" />
              <div class="text-subtitle-1 font-weight-bold">
                共：NT${{ totalAmount.toLocaleString() }} 元
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn color="primary" block @click="submitReservation">預定車輛</v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>

<style scoped>
.font-weight-bold { font-weight: 700; }
.position-sticky { position: sticky; }
</style>
