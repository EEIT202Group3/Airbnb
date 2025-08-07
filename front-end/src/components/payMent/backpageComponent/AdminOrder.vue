<template>
  <v-layout>
    <v-main>
      <v-responsive class="mx-auto" max-width="1800">
        <h1 class="page-title">訂單查詢</h1>
        <!-- 查詢區塊 -->
        <div v-if="!searched" class="search-container">
          <v-text-field
            class="search-bar"
            v-model="customerId"
            hint="請輸入用戶ID"
            label="用戶ID"
            type="input"
          ></v-text-field>

          <v-btn class="search-btn" @click="fetchOrders()">查詢</v-btn>
        </div>

        <!-- 顯示訂單表格 -->
        <v-data-table
          v-if="orders.length > 0"
          :headers="headers"
          :items="orders"
          class="mt-800 order-table"
        >
          <template v-slot:item.totalamount="{ item }">
            ${{ item.totalamount }}
          </template>

          <template v-slot:item.checkindate="{ item }">
            {{ formatDate(item.checkindate) }}
          </template>

          <template v-slot:item.checkoutdate="{ item }">
            {{ formatDate(item.checkoutdate) }}
          </template>

          <!-- 明細按鈕 -->
          <template v-slot:item.actions="{ item }">
            <v-btn color="primary" @click="fetchOrderDetail(item.bookingId)"
              >明細</v-btn
            >
          </template>
        </v-data-table>

        <!-- 無資料提示 -->
        <div v-else-if="searched && orders.length === 0">查無資料</div>

        <!-- 返回按鈕 -->
        <v-btn color="#E0E0E0" v-if="searched" @click="clearOrders">返回</v-btn>

        <!-- 明細 Dialog -->
        <v-dialog v-model="detailDialog" max-width="600">
          <v-card>
            <v-card-title>訂單明細</v-card-title>
            <v-card-text>
              <p>訂單編號：{{ orderDetail?.bookingId }}</p>
              <p>租車訂單編號：{{ orderDetail?.reservationId }}</p>
              <p>用戶名稱：{{ orderDetail?.username }}</p>
              <p>房源名稱：{{ orderDetail?.houseName }}</p>
              <p>住房地址：{{ orderDetail?.address }}</p>
              <p>電話：{{ orderDetail?.tel }}</p>
              <p>床型：{{ orderDetail?.bed }}</p>
              <p>入住人數：{{ orderDetail?.people }}</p>
              <p>取車地址：{{ orderDetail?.locationid }}</p>
              <p>入住日期：{{ formatDate(orderDetail?.checkinDate) }}</p>
              <p>退房日期：{{ formatDate(orderDetail?.checkoutDate) }}</p>
              <p>房價：$ {{ orderDetail?.price }}</p>
              <p>租車金額：${{ orderDetail?.totalAmount }}</p>
              <p>總金額：${{ orderDetail?.total }}</p>
              <p>付款編號：{{ orderDetail?.paymentId }}</p>
              <p>付款時間：{{ orderDetail?.paidTime }}</p>
              <p>付款方式：{{ orderDetail?.bookingMethod }}</p>
              <p>
                訂單狀態：{{ orderDetail?.bookingStatus }}
                <v-select
                  v-model="newBookingStatus"
                  :items="bookingStatusOptions"
                  density="compact"
                  variant="outlined"
                  hide-details
                  style="width: 120px; display: inline-block; margin-left: 8px"
                />
                <v-btn
                  size="small"
                  color="primary"
                  style="margin-left: 5px"
                  @click="updateBookingStatus"
                >
                  確認更新
                </v-btn>
              </p>
              <!-- 付款狀態 -->
              <p>
                付款狀態：{{ orderDetail?.mentStatus }}
                <v-select
                  v-model="newMentStatus"
                  :items="mentStatusOptions"
                  density="compact"
                  variant="outlined"
                  hide-details
                  style="width: 120px; display: inline-block; margin-left: 8px"
                />
                <v-btn
                  size="small"
                  color="primary"
                  style="margin-left: 5px"
                  @click="updateMentStatus"
                >
                  確認更新
                </v-btn>
              </p>
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" @click="detailDialog = false">關閉</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-responsive>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import { ref } from "vue";
import api from "@/api";
import axios from "axios";
import layout from "@/layouts/layout.vue";
import { useRouter } from "vue-router";

const customerId = ref("");
const orders = ref<any[]>([]);
const searched = ref(false);
const orderDetail = ref<any | null>(null);
const detailDialog = ref(false);
const router = useRouter();

//查詢訂單明細
//Service
async function fetchOrderDetail(bookingId: string) {
  try {
    const response = await api.get(
      "/api/admins/admingetorderdetail/admindetail",
      {
        params: { bookingId: bookingId },
        withCredentials: true,
      }
    );
    orderDetail.value = response.data;
    detailDialog.value = true;
  } catch (error) {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      alert("請先登入");
      return null;
    } else {
      console.error("取得資料失敗", error);
      throw error;
    }
  }
}
//查詢全部
//2
const headers = [
  { title: "用戶名稱", key: "username" },
  { title: "房源名稱", key: "housename" },
  { title: "地址", key: "address" },
  { title: "電話", key: "tel" },
  { title: "床型", key: "bed" },
  { title: "入住人數", key: "people" },
  { title: "預訂狀態", key: "bookingstatus" },
  { title: "入住日期", key: "checkindate" },
  { title: "退房日期", key: "checkoutdate" },
  { title: "總金額", key: "total" },
  { title: "明細", key: "actions", sortable: false },
];

//Service
async function fetchOrders() {
  if (!customerId.value) {
    alert("請輸入用戶 ID");
    return;
  }
  try {
    const res = await api.get(
      "/api/admins/admingetorderdetail/adminbyCustomer",
      {
        params: { customerId: customerId.value },
        withCredentials: true,
      }
    );
    console.log(res.data);
    orders.value = Array.isArray(res.data) ? res.data : [];
  } catch (error) {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      console.error("未登入或沒有權限", error);
      alert("請先登入");
      orders.value = [];
      router.push({name:'Homepage'});
    }else{
      console.error('取得資料失敗', error);
      throw error;
    }
  } finally {
    searched.value = true;
  }
}

//2
function formatDate(dateString: string) {
  if (!dateString) return "";
  return dateString.split("T")[0];
}

//1
function clearOrders() {
  orders.value = [];
  searched.value = false;
}

//2
const bookingStatusOptions = ["待入住", "已入住", "已完成", "已取消"];
const bookingMethodOptions = ["現金", "信用卡", "Line Pay"];
//2
const mentStatusOptions = ["待付款", "已付款"];

//2
const newBookingStatus = ref("");
const newBookingMethod = ref("");

//2
const newMentStatus = ref("");

// 更新訂單狀態
//Service
async function updateBookingStatus() {
  try {
    await api.post(
      "/api/admins/admingetorderdetail/updatebookingstatus",
      null,
      {
        params: {
          bookingId: orderDetail.value.bookingId,
          bookingStatus: newBookingStatus.value,
        },
        withCredentials: true,
      }
    );
    alert("訂單狀態更新成功");
    orderDetail.value.bookingStatus = newBookingStatus.value;
  } catch (error) {
    console.error(error);
    alert("訂單狀態更新失敗");
  }
}
// 更新付款狀態
//Service
async function updateMentStatus() {
  try {
    await api.post("/api/admins/admingetorderdetail/updatementstatus", null, {
      params: {
        bookingId: orderDetail.value.bookingId,
        mentStatus: newMentStatus.value,
      },
      withCredentials: true,
    });
    alert("付款狀態更新成功");
    orderDetail.value.mentStatus = newMentStatus.value;
  } catch (error) {
    console.error(error);
    alert("付款狀態更新失敗");
  }
}
</script>

<!--css-->
<style scoped>
.page-title {
  text-align: center;
  font-size: 40px;
  font-weight: bold;
  letter-spacing: 3px;
  margin: 20px 0;
  font-family: "Arial", sans-serif;
  color: #333;
}
.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar {
  max-width: 400px;
}

.search-btn {
  background-color: #e0e0e0;
}
.order-table {
  font-size: 18px;
}

.order-table th {
  font-size: 20px;
  padding-left: 8px !important;
  text-align: center;
}

.order-table td {
  font-size: 18px;
  padding-left: 8px !important;
  text-align: center;
}
</style>