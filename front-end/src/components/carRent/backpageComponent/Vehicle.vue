<script setup lang="ts">
import Sidebar from "@/components/carRent/backpageComponent/Sidebar.vue";
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import api from "@/api";

const showSidebar = ref(false);
const route = useRoute();
const vehicle = ref<any>(null);
const isEditing = ref(false);
const insertVehicleMode = ref(false);
const router = useRouter();
const selectedFile = ref<File | null>(null);

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await api.get(`/vehicles/${id}`);
    vehicle.value = res.data;
    console.log(vehicle.value);
  } catch (err) {
    console.error("載入車輛資料失敗：", err);
  }
});
const emptyVehicle = {
  vehicleId: "",
  plateNo: "",
  brand: "",
  model: "",
  color: "",
  fuelType: "",
  transmission: "",
  seatCapacity: null,
  fuelCapacity: null,
  vehicleTax: null,
  dailyRent: null,
  mileage: null,
  latitude: null,
  longitude: null,
  status: "",
  image: ""
};

// 新增模式
const startInsertVehicle = () => {
  vehicle.value = {...emptyVehicle};
  insertVehicleMode.value = true;
  isEditing.value = true;
};

// 新增車輛
const insertVehicle = async () => {
  try {
    const res = await api.post(`/vehicles/insert`, vehicle.value);
    alert("新增成功！");
    const newId = res.data.vehicleId;
    insertVehicleMode.value = false;
    isEditing.value = false;
    await router.push(`/car-rent/back-homepage/vehicles/${newId}`);
  } catch (err) {
    const msg = err?.response?.data?.message || "更新失敗，請稍後再試";
    if (msg.includes("車輛在所選時間已被預約")) {
      alert("此車輛在所選時間已被預約，請選擇其他時間或車輛");
    } else {
      alert(msg);
    }
  }
};

// 修改車輛
const saveVehicle = async () => {
  try {
    console.log("送出的資料：", JSON.stringify(vehicle.value, null, 2));
    await api.put(`/vehicles/update`, vehicle.value);
    alert("修改成功！");
    isEditing.value = false;
  } catch (err) {
    const msg = err?.response?.data?.message || "更新失敗，請稍後再試";
    if (msg.includes("車輛在所選時間已被預約")) {
      alert("此車輛在所選時間已被預約，請選擇其他時間或車輛");
    } else {
      alert(msg);
    }
  }
};

// 刪除車輛
const deleteVehicle = async () => {
  if (!confirm("確定要刪除此車輛嗎？")) return;
  try {
    await api.delete(`/vehicles/delete/${vehicle.value.vehicleId}`);
    alert("刪除成功！");
    await router.push("/car-rent/back-homepage");
  } catch (err) {
    alert("刪除失敗：" + err);
  }
};

// 圖片上傳
  const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      selectedFile.value = target.files[0];
    }
  };
  const submitForm = async () => {
    if (!selectedFile.value) {
      alert("請先選擇圖片！");
      return;
    }

    const formData = new FormData();
    formData.append("image", selectedFile.value);

    try {
      const res = await api.post("/vehicles/upload-image", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      const uploadedFileName = res.data.filename;
      vehicle.value.image = uploadedFileName;

      alert("圖片上傳成功！");
    } catch (err) {
      alert("圖片上傳失敗：" + err);
    }
  };

</script>

<template>
  <div class="container-fluid" v-if="vehicle">
    <main class="main-content">
      <button class="btn btn-outline-dark m-2" @click="showSidebar = true">
        <i class="fa-solid fa-bars"></i> 功能選單
      </button>
      <div class="d-flex justify-content-end mb-3">
        <button class="btn btn-outline-info btn-sm me-2" v-if="isEditing && !insertVehicleMode" @click="saveVehicle">
          儲存
        </button>
        <button class="btn btn-outline-success btn-sm me-2" v-if="!insertVehicleMode && !isEditing"
                @click="startInsertVehicle">
          新增車輛
        </button>
        <button class="btn btn-primary btn-sm me-2" v-if="insertVehicleMode" @click="insertVehicle">
          確定新增
        </button>
        <button class="btn btn-outline-secondary btn-sm me-2" v-if="insertVehicleMode"
                @click="insertVehicleMode = false; isEditing = false">
          取消新增
        </button>
        <button class="btn btn-outline-primary btn-sm me-2" v-if="!insertVehicleMode" @click="isEditing = !isEditing">
          {{ isEditing ? "取消編輯" : "編輯" }}
        </button>
        <button class="btn btn-danger btn-sm" v-if="!insertVehicleMode" @click="deleteVehicle">
          刪除車輛
        </button>
      </div>
      <Sidebar :visible="showSidebar" :close="() => showSidebar = false"/>

      <div class="bg-white p-4 rounded shadow-sm">
        <h4 class="fw-bold mb-4">車輛資訊</h4>
        <div class="row">

          <!-- 車輛圖片 -->
          <div class="col-md-3 me-4">
            <img
                :src="`/carPicture/${vehicle.image}`"
                class="img-fluid rounded-circle"
                alt="車輛圖片"
            />

            <div v-if="isEditing" class="mt-2">
              <form @submit.prevent="submitForm">
                <input type="file" class="form-control mb-2" @change="handleFileChange" />
                <button type="submit" class="btn btn-sm btn-primary">上傳圖片</button>
              </form>
            </div>
          </div>

          <!-- 車輛詳細資料 -->
          <div class="col-md-8">
            <table class="table table-bordered table-sm">
              <tbody>
              <tr>
                <td>車牌</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.plateNo }}</span>
                  <input v-else v-model="vehicle.plateNo" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>品牌</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.brand }}</span>
                  <input v-else v-model="vehicle.brand" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>型號</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.model }}</span>
                  <input v-else v-model="vehicle.model" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>顏色</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.color }}</span>
                  <input v-else v-model="vehicle.color" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>燃料類型</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.fuelType }}</span>
                  <input v-else v-model="vehicle.fuelType" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>傳動系統</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.transmission }}</span>
                  <input v-else v-model="vehicle.transmission" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>座位數</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.seatCapacity }}</span>
                  <input v-else type="number" v-model="vehicle.seatCapacity" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>油量容量</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.fuelCapacity }}</span>
                  <input v-else type="number" step="0.1" v-model="vehicle.fuelCapacity"
                         class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>租金（日）</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.dailyRent }}</span>
                  <input v-else type="number" v-model="vehicle.dailyRent" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>稅金</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.vehicleTax }}</span>
                  <input v-else type="number" v-model="vehicle.vehicleTax" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>里程</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.mileage }}</span>
                  <input v-else type="number" v-model="vehicle.mileage" class="form-control form-control-sm"/>
                </td>
              </tr>
              <tr>
                <td>狀態</td>
                <td>
                  <span v-if="!isEditing">{{ vehicle.status }}</span>
                  <select v-else class="form-select input-short editable" v-model="vehicle.status">
                  <option value="可租用">可用</option>
                  <option value="已預約">維修中</option>
                  <option value="維修中">下架</option>
                  </select>
                </td>
              </tr>
              </tbody>

            </table>
          </div>
        </div>
      </div>
    </main>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary" role="status"></div>
    <p class="mt-3">載入中...</p>
  </div>
</template>

<style scoped>
.main-content {
  margin: auto;
  padding: 20px;
}

.table td {
  white-space: nowrap;
  vertical-align: middle;
}

.main-content {
  margin: auto;
  padding: 20px;
  width: calc(100% - 200px);
}
</style>
