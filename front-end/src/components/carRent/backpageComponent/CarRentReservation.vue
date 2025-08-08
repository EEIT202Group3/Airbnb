<script setup lang="ts">
const showSidebar = ref(false);
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import api from "@/api";

const route = useRoute();
const router = useRouter();
const reservation = ref<any>({
  license: "",
  driverName: "",
  driverPhone: "",
  driverEmail: "",
  driverAge: "",
  pickupDate: "",
  returnDate: "",
  totalAmount: "",
  vehicleId: "",
  location: "",
  status: ""
});
onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await api.get(`/reservations/${id}`);
    reservation.value = res.data;
  } catch (err) {
    console.error("載入預約資料失敗：", err);
  }
});

// 刪除訂單
const isEditing = ref(false);
const deleteReservation = async () => {
  if (!confirm("確定要刪除此筆訂單嗎？")) return;
  try {
    const id = reservation.value.reservationId;
    await api.delete(`/reservations/delete/${id}`);
    alert("刪除完成!");
    await router.push("/car-rent/back-homepage");
  } catch (err) {
    console.log("刪除失敗", err);
    alert("刪除失敗，請稍後再試");
  }
}

// 修改訂單
const saveEditing = async () => {
  try {
    await api.put(`/reservations/update`, reservation.value);
    alert("修改成功");
    isEditing.value = false;
  } catch (err) {
    const msg = err?.response?.data?.message || "修改失敗，請稍後再試";
    if (msg.includes("該車牌已被登入")) {
      alert("該車牌已被登入，請再次檢查車牌資訊");
    } else {
      alert(msg);
    }
  }
}
// 新增模式
const startInsertReservation = () => {
  reservation.value = {
    license: "",
    driverName: "",
    driverPhone: "",
    driverEmail: "",
    driverAge: "",
    pickupDate: "",
    returnDate: "",
    totalAmount: "",
    vehicleId: "",
    location: "",
    status: ""
  };
  insertReservationMode.value = true;
  isEditing.value = true;
}

// 新增訂單
const insertReservationMode = ref(false);
const insertReservation = async () => {
  try {
    const response = await api.post(`/reservations/insert`, reservation.value);
    const newId = response.data.reservationId;
    alert("新增成功");
    insertReservationMode.value = false;
    isEditing.value = false;
    await router.push(`/car-rent/reservations/${newId}`);
  } catch (err) {
    const msg = err?.response?.data?.message || "更新失敗，請稍後再試";
    if (msg.includes("該車牌已被登入")) {
      alert("該車牌已被登入，請再次檢查車牌資訊");
    } else {
      alert(msg);
    }
  }
};

// 完成訂單
function finishedReservation() {
  reservation.value.status = "完成訂單";
  saveEditing();
  alert("訂單已標記為完成");
}
</script>

<template>
  <div class="container-fluid" v-if="reservation">
    <div class="container-fluid">
      <div class="row">
        <main class="main-content">
          <button class="btn btn-outline-dark m-2" @click="showSidebar = true">
            <i class="fa-solid fa-bars"></i> 功能選單
          </button>
          <Sidebar :visible="showSidebar" :close="() => showSidebar = false"/>

          <div class="bg-white p-4 rounded shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">

              <h4 class="fw-bold">訂單一覽</h4>
              <div id="action-buttons">
                <button class="btn btn-outline-primary btn-sm"
                        v-if="!insertReservationMode && !isEditing"
                        @click="finishedReservation">
                  完成訂單
                </button>
                <button class="btn btn-primary btn-sm" v-if="isEditing && !insertReservationMode"
                        @click="saveEditing">確定儲存
                </button>
                <button class="btn btn-outline-success btn-sm"
                        v-if="!insertReservationMode && !isEditing"
                        @click="startInsertReservation">
                  新增訂單
                </button>
                <button class="btn btn-primary btn-sm"
                        v-if="insertReservationMode"
                        @click="insertReservation">
                  確定新增
                </button>
                <button class="btn btn-outline-secondary btn-sm"
                        v-if="insertReservationMode"
                        @click="insertReservationMode = false; isEditing = false">
                  取消
                </button>
                <button class="btn btn-outline-secondary btn-sm" v-if="!insertReservationMode && !insertReservationMode"
                        @click="isEditing = !isEditing">
                  {{ isEditing ? "取消 " : "編輯" }}
                </button>
                <button class="btn btn-danger btn-sm" @click="deleteReservation">刪除訂單</button>
              </div>
            </div>

            <!-- 分頁按鈕 -->
            <ul class="nav nav-tabs mb-3" id="infoTabs" role="tablist">
              <li class="nav-item" role="presentation">
                <button class="nav-link active" id="summary-tab" data-bs-toggle="tab" data-bs-target="#summary"
                        type="button" role="tab">摘要
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button class="nav-link" id="note-tab" data-bs-toggle="tab" data-bs-target="#note"
                        type="button" role="tab">備註
                </button>
              </li>
            </ul>

            <!-- 分頁內容 -->
            <div class="tab-content" id="infoTabsContent">
              <!-- 摘要 -->
              <div class="tab-pane fade show active" id="summary" role="tabpanel">
                <div class="row">
                  <!-- 個人資料 -->
                  <div class="col-md-6 mb-3">
                    <div class="card h-100">
                      <div class="card-body">
                        <h5 class="card-title">個人資料</h5>
                        <table class="table table-bordered table-sm">
                          <tbody>
                          <tr>
                            <td>駕照號碼</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.license }}</span>
                              <input v-else class="form-control input-short editable" type="text"
                                     v-model="reservation.license"/>
                            </td>
                          </tr>
                          <tr>
                            <td>姓名</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.driverName }}</span>
                              <input v-else class="form-control input-short editable" type="text"
                                     v-model="reservation.driverName"/>
                            </td>
                          </tr>
                          <tr>
                            <td>電話</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.driverPhone }}</span>
                              <input v-else class="form-control input-short editable" type="text"
                                     v-model="reservation.driverPhone"/>
                            </td>
                          </tr>
                          <tr>
                            <td>電子信箱</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.driverEmail }}</span>
                              <input v-else class="form-control input-short editable" type="text"
                                     v-model="reservation.driverEmail"/>
                            </td>
                          </tr>
                          <tr>
                            <td>年齡</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.driverAge }}</span>
                              <input v-else class="form-control input-short editable" type="number"
                                     v-model="reservation.driverAge"/>
                            </td>
                          </tr>
                          <tr>
                            <td>租屋訂單編號</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.bookingId }}</span>
                              <input v-else class="form-control input-short editable" type="text"
                                     v-model="reservation.bookingId"/>
                            </td>
                          </tr>
                          </tbody>

                        </table>
                      </div>
                    </div>
                  </div>

                  <!-- 訂單摘要 -->
                  <div class="col-md-6 mb-3">
                    <div class="card h-100">
                      <div class="card-body">
                        <h5 class="card-title">本次訂單摘要</h5>
                        <table class="table table-bordered table-sm">
                          <tbody>
                          <tr>
                            <td>訂單編號</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.reservationId }}</span>
                              <input v-else type="text" class="form-control input-short"
                                     v-model="reservation.reservationId" disabled placeholder="不可自行輸入">
                            </td>
                          </tr>
                          <tr>
                            <td>訂單建立時間</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.createdAt }}</span>
                              <input v-else type="text" class="form-control input-short editable"
                                     v-model="reservation.createdAt" disabled placeholder="不可自行輸入">
                            </td>
                          </tr>
                          <tr>
                            <td>訂單總金額</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.totalAmount }}</span>
                              <input v-else type="number" class="form-control input-short editable"
                                     v-model="reservation.totalAmount">
                            </td>
                          </tr>
                          <tr>
                            <td>預約時間</td>
                            <td>
                              <div class="datetime-group" v-if="!isEditing">
                                {{ reservation.pickupDate }} ～ {{ reservation.returnDate }}
                              </div>
                              <div class="datetime-group" v-else>
                                <input type="datetime-local" class="form-control input-short editable"
                                       v-model="reservation.pickupDate">
                                <span>～</span>
                                <input type="datetime-local" class="form-control input-short editable"
                                       v-model="reservation.returnDate">
                              </div>
                            </td>
                          </tr>
                          <tr>
                            <td>租用車輛</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.vehicleId }}</span>
                              <input v-else type="text" class="form-control input-short editable"
                                     v-model="reservation.vehicleId">
                            </td>
                          </tr>
                          <tr>
                            <td>租車地點</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.locationId }}</span>
                              <input v-else type="text" class="form-control input-short editable"
                                     v-model="reservation.locationId">
                            </td>
                          </tr>
                          <tr>
                            <td>付款狀態</td>
                            <td>
                              <span v-if="!isEditing">{{ reservation.status }}</span>
                              <select v-else class="form-select input-short editable" v-model="reservation.status">
                                <option value="未付款">未付款</option>
                                <option value="已付款">已付款</option>
                                <option value="取消訂單">取消訂單</option>
                                <option value="完成訂單" disabled class="text-info">完成訂單</option>
                              </select>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 備註 -->
              <div class="tab-pane fade" id="note" role="tabpanel">
                <div class="card">
                  <div class="card-body">
                    <h5 class="card-title">備註</h5>
                    <textarea class="form-control" rows="6"
                              placeholder="輸入關於此使用者的備註..."></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary" role="status"></div>
    <p class="mt-3">載入中...</p>
  </div>

</template>

<style scoped>
body {
  background-color: #f8f9fa;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}

.sidebar a {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
}

.sidebar a:hover {
  background-color: #f0f0f0;
}

.sidebar a i {
  margin-right: 6px;
}

.main-content {
  margin: auto;
  padding: 20px;
  width: calc(100% - 200px);

}

.form-control[readonly] {
  background-color: #f9f9f9;
}

.input-short {
  max-width: 100%;
}

.datetime-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.table td {
  white-space: nowrap;
  width: 1%;
  vertical-align: middle;
}

.table input.form-control {
  width: 100%;
  min-width: 0;
  box-sizing: border-box;
}

#action-buttons button {
  white-space: nowrap;
  margin-left: 0.5rem;
}
</style>