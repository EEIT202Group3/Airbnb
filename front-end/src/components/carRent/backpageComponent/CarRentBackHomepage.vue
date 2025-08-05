<script setup lang="ts">
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import {ref, onMounted} from "vue";
import {useRoute} from "vue-router";
import router from "@/router";

import api from "@/api";

const route = useRoute();
const showSidebar = ref(false);
const type = ref("license");
const query = ref("");
const errorMess = ref("");
const errorMess2 = ref("");
const reservation = ref(null);
const plateNo = ref("");
const vehicle = ref(null);

const searchReservation = async () => {
  errorMess.value = "";
  reservation.value = null;
  if (!query.value.trim()) {
    errorMess.value = "請輸入查詢內容";
    return;
  }
  errorMess.value = "";
  try {
    const res = await api.get("reservations/search", {
      params: {
        type: type.value,
        query: query.value.trim()
      }
    });
    console.log(res.data);
    reservation.value = res.data;
    await router.push(`/car-rent/back-homepage/reservations/${res.data.reservationId}`)
  } catch (err) {
    errorMess.value = "查無資料";
  }
};

const searchVehicle = async () => {
  errorMess2.value = "";
  vehicle.value = null;
  if (!plateNo.value) {
    errorMess2.value = "請輸入查詢內容";
    return;
  }
  errorMess2.value = "";
  try {
    const res = await api.get("/vehicles/search", {
      params: {
        plateNo: plateNo.value,
      }
    });
    console.log(res.data);
    await router.push(`/car-rent/back-homepage/vehicles/${res.data.vehicleId}`)
  } catch (err) {
    errorMess2.value = "查無資料";
  }
}
// 訂單儀錶板
const reservationSummary = ref({});

onMounted(async () => {
  try {
    const res = await api.get("/reservations/dashboard");
    reservationSummary.value = res.data;
  } catch (err) {
    console.error("取得 dashboard 資料失敗：", err);
  }
});
</script>

<template>
  <div v-if="route.path === '/car-rent/back-homepage'">
    <div class="main-content">
      <button class="btn btn-outline-dark m-2" @click="showSidebar = true">
        <i class="fa-solid fa-bars"></i> 功能選單
      </button>
      <Sidebar :visible="showSidebar" :close="() => showSidebar = false"/>


      <!-- 訂單狀態 -->
      <div class="row row-cols-1 row-cols-md-6 g-4">
        <div class="col" v-for="(value, key, index) in reservationSummary" :key="index">
          <div class="card h-10">
            <div class="card-body">
              <p class="card-text text-center">{{ key }}</p>
              <h4 class="text-center">{{ value }}</h4>
            </div>
          </div>
        </div>
      </div>

      <!-- 下方功能區 -->
      <div class="row mt-4 mb-4">
        <div class="col-md-6">
          <div class="card pt-2">
            <div class="card-body">
              <!--錯誤訊息-->
              <p class="text-danger" v-if="errorMess">{{ errorMess }}</p>

              <h6 class="card-title">查詢訂單</h6>
              <div class="input-group">
                <select class="form-select" v-model="type">
                  <option value="license">駕照號碼</option>
                  <option value="phone">電話號碼</option>
                </select>
                <input type="text" v-model="query" class="form-control" placeholder="請輸入對應內容">
                <button class="btn btn-success text-white" @click="searchReservation">查詢</button>
              </div>

            </div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="card pt-2">
            <div class="card-body">
              <!--錯誤訊息-->
              <p class="text-danger" v-if="errorMess2">{{ errorMess2 }}</p>
              <h6 class="card-title">查詢車輛</h6>
              <div class="input-group">
                <input type="text" v-model="plateNo" class="form-control" placeholder="車牌號碼">
                <button class="btn btn-success" @click="searchVehicle">查詢</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <h6 class="card-title">車輛資訊</h6>
                <select class="form-select form-select-sm w-auto">
                  <option>All Vehicle Types</option>
                </select>
              </div>
              <div class="my-3 text-center">
                <div
                    style="width: 160px; height: 160px; margin: 0 auto; background-color: #eee; border-radius: 50%; line-height: 160px;">
                  **串Chart.js
                </div>
              </div>
              <ul class="list-unstyled small">
                <li><span class="badge bg-success me-2">&nbsp;</span>可租用</li>
                <li><span class="badge bg-primary me-2">&nbsp;</span>維修中</li>
                <li><span class="badge bg-info me-2">&nbsp;</span>停用</li>
                <li><span class="badge bg-warning me-2">&nbsp;</span>已出租</li>
                <li><span class="badge bg-danger me-2">&nbsp;</span>下架</li>
              </ul>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <h6 class="card-title">車型租金一覽表</h6>

              <table class="table table-bordered table-sm">
                <thead class="table-dark">
                <tr>
                  <th>車型</th>
                  <th>免費里程數</th>
                  <th>日租金</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td>大型轎車</td>
                  <td>100</td>
                  <td>80</td>
                </tr>
                <tr>
                  <td>中型車</td>
                  <td>100</td>
                  <td>70</td>
                </tr>
                <tr>
                  <td>休旅車</td>
                  <td>100</td>
                  <td>150</td>
                </tr>
                <tr>
                  <td>豪華車</td>
                  <td>0</td>
                  <td>60</td>
                </tr>
                <tr>
                  <td>普通轎車</td>
                  <td>0</td>
                  <td>130</td>
                </tr>
                <tr>
                  <td>小型車</td>
                  <td>0</td>
                  <td>10</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <router-view></router-view>
  </div>
</template>

<style scoped>
.main-content {
  margin: auto;
  padding: 20px;
  width: calc(100% - 200px);
}
</style>