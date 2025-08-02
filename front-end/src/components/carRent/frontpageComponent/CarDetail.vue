<script setup lang="ts">
import NavigationBar from "@/components/carRent/frontpageComponent/NavigationBar.vue";
import {useRoute} from "vue-router";
import {ref} from "vue";
import {carList} from "@/components/carRent/frontpageComponent/carList.ts";

const route = useRoute();
const pickupDateTime = `${route.query.pickupDate}T${route.query.pickupTime}`;
const returnDateTime = `${route.query.returnDate}T${route.query.returnTime}`;
const id = Number(route.query.id) || 1;
const firstName = ref('');
const lastName = ref('');
const email = ref('');
const phone = ref('');
const license = ref('');
const driverAge = ref('25-60');

const vehicle = carList.find(function (c) {
  return c.id === id;
});
if (!vehicle) {
  console.warn("查無此車輛 ID：", id);
}

function submitReservation() {
  const formData = {
    firstName: firstName.value,
    lastName: lastName.value,
    email: email.value,
    phone: phone.value,
    license: license.value,
    driverAge: driverAge.value,
    vehicleId: vehicle?.id,
    pickupDateTime,
    returnDateTime
  }
  console.log("送出表單");
}
</script>

<template>
  <NavigationBar></NavigationBar>

  <!--車輛資訊-->
  <div class="card mb-4">
    <div class="card-header fw-bold">車輛詳細資訊</div>
    <div class="row g-0">
      <div class="col-md-4">
        <img :src="vehicle.img" class="card-img-top"
             :alt="`${vehicle.brand} ${vehicle.model}`">
      </div>
      <div class="col-md-8">
        <div class="card-body">
          <h5 class="card-title">{{ vehicle.brand }} {{ vehicle.model }}</h5>
          <p class="card-text">
            顏色：{{ vehicle.color }}｜
            座位數：{{ vehicle.seatCapacity }}｜
            變速系統：{{ vehicle.transmission }}｜
            油箱容量：{{ vehicle.fuelCapacity }} 公升｜
            油種：{{ vehicle.fuelType }}
          </p>
        </div>
      </div>
    </div>
  </div>

  <!--取還車資訊-->
  <div class="card mb-4">
    <div class="card-header">取車及還車</div>
    <div class="card-body">
      <p>取車時間：{{ pickupDateTime }}</p>
      <p>還車時間：{{ returnDateTime }}</p>
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
          <li>基本方案：NT$5350</li>
          <li class="text-success">套用 優惠：-NT$290</li>
          <hr>
          <li><strong>總額：NT$5060</strong></li>
        </ul>
        <button type="submit" class="btn btn-primary w-100">預定車輛</button>
      </div>
    </div>
  </form>
</template>

<style scoped>

</style>