<script setup lang="ts">
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import {ref, onMounted, computed} from "vue";
import {useRoute} from "vue-router";
import axios from "axios";
import router from "@/router";
import api from "@/api";

const props = defineProps<{
  id: string | number
}>();

const route = useRoute();
const pickupDateTime = route.query.pickupDateTime as string || '';
const returnDateTime = route.query.returnDateTime as string || '';

const vehicle = ref<any | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const firstName = ref('');
const lastName = ref('');
const email = ref('');
const phone = ref('');
const license = ref('');
const driverAge = ref('25-60');

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
// 寫入資料
async function submitReservation() {
  if (!vehicle.value) return;

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
    await router.push("/car-rent/front-homepage");
  } catch (error) {
    console.error("新增失敗", error);
    alert("預約失敗，請稍後再試");
  }
}
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
</script>

<template>
  <NavigationBar></NavigationBar>
  <div v-if="loading" class="text-center my-5">
    載入中...
  </div>

  <div v-else-if="error" class="alert alert-danger my-4">
    {{ error }}
  </div>
  <!-- 車輛詳細資訊 -->
  <template v-else>
    <div class="container">
      <div v-if="vehicle" class="card mb-4 shadow-sm">
        <div class="card-header fw-bold bg-primary text-white">
          車輛詳細資訊
        </div>
        <div class="row g-0">
          <div class="col-md-4">
            <img :src="`/carPicture/${vehicle.image}`" class="img-fluid rounded-start"
                 :alt="`${vehicle.brand} ${vehicle.model}`"/>
          </div>
          <div class="col-md-8">
            <div class="card-body">
              <h4 class="card-title mb-3">{{ vehicle.brand }} {{ vehicle.model }}</h4>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>車牌號碼：</strong></div>
                <div class="col-sm-8">{{ vehicle.plateNo }}</div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>顏色 / 座位數：</strong></div>
                <div class="col-sm-8">{{ vehicle.color }} / {{ vehicle.seatCapacity }} 人座</div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>變速系統 / 油種：</strong></div>
                <div class="col-sm-8">{{ vehicle.transmission }} / {{ vehicle.fuelType }}</div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>油箱容量：</strong></div>
                <div class="col-sm-8">{{ vehicle.fuelCapacity }} 公升</div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>里程數：</strong></div>
                <div class="col-sm-8">{{ vehicle.mileage.toLocaleString() }} 公里</div>
              </div>
              <div class="row mb-2">
                <div class="col-sm-4"><strong>租金：</strong></div>
                <div class="col-sm-8 text-success fw-bold">NT${{ vehicle.dailyRent }} / 日</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 取還車資訊 -->
      <div class="card mb-4">
        <div class="card-header ">取車及還車</div>
        <div class="card-body">
          <p>取車時間：{{ pickupDateTime.replace('T', ' ') }}</p>
          <p>還車時間：{{ returnDateTime.replace('T', ' ') }}</p>
          <p>營業時間：07:30 - 22:00</p>
        </div>
      </div>

      <!--駕駛人資料-->
      <form @submit.prevent="submitReservation">
        <div class="card mb-4">
          <div class="card-header">駕駛人資料</div>
          <div class="card-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="firstname" class="form-label">姓氏</label>
                <input type="text" class="form-control" placeholder="請以中文輸入" name="firstname"
                       id="firstname" v-model="lastName">
              </div>
              <div class="col-md-6">
                <label for="lastname" class="form-label">名字</label>
                <input type="text" class="form-control" placeholder="請以中文輸入" name="lastname"
                       id="lastname" v-model="firstName">
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="email" class="form-label">電子郵件</label>
                <input type="email" class="form-control" placeholder="email@example.com" name="email"
                       id="email" v-model="email">
              </div>
              <div class="col-md-6">
                <label for="phone" class="form-label">手機號碼</label>
                <input type="tel" class="form-control" placeholder="09xx-xxx-xxx" name="phone"
                       id="phone" v-model="phone">
              </div>
              <div class="col-md-6">
                <label for="license" class="form-label">駕照號碼</label>
                <input type="tel" class="form-control" placeholder="A123456789" name="license"
                       id="license" v-model="license">
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">駕駛年齡</label>
              <select class="form-select" name="driverAge" v-model="driverAge">
                <option>18-24</option>
                <option>25-60</option>
                <option>60+</option>
              </select>
            </div>
          </div>
        </div>

        <!--隱藏欄位-->
        <input type="hidden" name="vehicleId" :value="vehicle.id">
        <input type="hidden" name="pickupDateTime" :value="pickupDateTime">
        <input type="hidden" name="returnDateTime" :value="returnDateTime">
        <!--總價與按鈕-->
        <div class="col-lg-4">
          <div class="card sticky-sidebar p-3">
            <h5 class="mb-3">價格明細</h5>
            <ul class="list-unstyled">
              <li>租用天數：{{ rentalDays }} 天</li>
              <li>每日租金：NT${{ vehicle.dailyRent.toLocaleString() }}</li>
              <hr />
              <li><strong>共：NT${{ totalAmount.toLocaleString() }} 元</strong></li>
            </ul>
            <button type="submit" class="btn btn-primary w-100">預定車輛</button>
          </div>
        </div>
      </form>
    </div>
  </template>
</template>

<style scoped>

</style>