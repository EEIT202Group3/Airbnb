<template>
  <v-layout>
    <v-main>
      <v-container class="py-8" fluid>
        <!-- 頁首 -->
        <div class="page-head">
          <div class="title-wrap">
            <v-avatar size="40" class="mr-3" color="grey-lighten-3">
              <v-icon>mdi-view-list</v-icon>
            </v-avatar>
            <div>
              <h1 class="title">訂單查詢</h1>
              <div class="subtitle">管理與檢視用戶的住宿訂單與狀態</div>
            </div>
          </div>
          <div class="legend">
            <v-chip size="small" color="info" variant="tonal" class="mr-2"
              >待入住</v-chip
            >
            <v-chip size="small" color="success" variant="tonal" class="mr-2"
              >已完成</v-chip
            >
            <v-chip size="small" color="error" variant="tonal" class="mr-2"
              >已取消</v-chip
            >
            <v-chip size="small" color="grey" variant="tonal">未設定</v-chip>
          </div>
        </div>

        <!-- 查詢 -->
        <v-card v-if="!searched" class="search-card">
          <v-card-text>
            <v-row align="center" no-gutters>
              <v-col cols="12" md="6" lg="5">
                <v-text-field
                  v-model="customerId"
                  label="用戶 Email"
                  hint="輸入 Email 後按查詢"
                  density="comfortable"
                  prepend-inner-icon="mdi-email-outline"
                  clearable
                />
              </v-col>
              <v-spacer />
              <v-col cols="12" md="auto">
                <v-btn
                  class="ml-md-4 w-100 w-md-auto"
                  color="primary"
                  variant="elevated"
                  :loading="listLoading"
                  @click="fetchOrders"
                >
                  <v-icon start>mdi-magnify</v-icon> 查詢
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- 清單 -->
        <v-card v-if="orders.length > 0" class="data-card mt-6">
          <v-data-table
            :headers="headers"
            :items="orders"
            :items-per-page="10"
            density="comfortable"
            class="order-table"
            fixed-header
            :loading="listLoading"
          >
            <template #top>
              <div class="table-top">
                <div class="table-title">
                  <v-icon size="20" class="mr-2">mdi-database-eye</v-icon>
                  共 {{ orders.length }} 筆結果
                </div>
                <div class="table-actions">
                  <v-btn variant="text" color="grey" @click="clearOrders">
                    <v-icon start>mdi-arrow-left</v-icon> 返回
                  </v-btn>
                </div>
              </div>
              <v-divider />
            </template>

            <!-- 狀態 -->
            <template #item.bookingStatus="{ item }">
              <v-chip
                :color="statusColor(item.bookingStatus)"
                size="small"
                variant="tonal"
              >
                {{ item.bookingStatus || "—" }}
              </v-chip>
            </template>

            <!-- 床型：只包一層 span，字不變、內容不變 -->
            <template #item.bed="{ item }">
              <span class="bed-cell">{{ item.bed }}</span>
            </template>

            <!-- 日期保持原本公式 -->
            <template #item.checkinDate="{ item }">
              <span class="date-nowrap">{{
                formatDate(item.checkinDate)
              }}</span>
            </template>
            <template #item.checkoutDate="{ item }">
              <span class="date-nowrap">{{
                formatDate(item.checkoutDate)
              }}</span>
            </template>

            <!-- 總金額 -->
            <template #item.grandtotal="{ item }">
              <div class="amt">
                NT$
                {{
                  new Intl.NumberFormat("zh-TW").format(item.grandtotal || 0)
                }}
              </div>
            </template>

            <!-- 明細 -->
            <template #item.actions="{ item }">
              <v-btn
                size="small"
                color="primary"
                variant="tonal"
                @click="fetchOrderDetail(item.bookingId)"
              >
                明細
              </v-btn>
            </template>
          </v-data-table>
        </v-card>

        <!-- 空狀態 -->
        <v-sheet
          v-else-if="searched"
          class="empty"
          color="grey-lighten-4"
          rounded="xl"
          border
        >
          <v-icon size="36" class="mb-2" color="grey">mdi-database-off</v-icon>
          查無資料
          <div class="mt-3">
            <v-btn variant="text" color="primary" @click="clearOrders">
              <v-icon start>mdi-arrow-left</v-icon> 返回
            </v-btn>
          </div>
        </v-sheet>

        <!-- 明細 -->
        <v-dialog v-model="detailDialog" max-width="720">
          <v-card class="detail-card">
            <v-toolbar flat density="comfortable">
              <v-toolbar-title class="text-h6">
                <v-icon class="mr-2">mdi-receipt-text</v-icon> 訂單明細
              </v-toolbar-title>
              <v-spacer />
              <v-chip
                v-if="orderDetail"
                :color="statusColor(orderDetail.bookingStatus)"
                size="small"
                variant="tonal"
              >
                {{ orderDetail?.bookingStatus || "—" }}
              </v-chip>
            </v-toolbar>
            <v-divider />

            <v-card-text class="pt-6">
              <v-row>
                <v-col cols="12" md="6">
                  <div class="kv">
                    <div class="k">訂單編號</div>
                    <div class="v mono">{{ orderDetail?.bookingId }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">用戶名稱</div>
                    <div class="v">{{ orderDetail?.username }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">房源名稱</div>
                    <div class="v">{{ orderDetail?.houseName }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">床型</div>
                    <div class="v">{{ orderDetail?.bed }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">電話</div>
                    <div class="v">{{ orderDetail?.tel }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">地址</div>
                    <div class="v">{{ orderDetail?.address }}</div>
                  </div>
                </v-col>

                <v-col cols="12" md="6">
                  <div class="kv">
                    <div class="k">入住日期</div>
                    <div class="v">
                      {{ formatDate(orderDetail?.checkinDate) }}
                    </div>
                  </div>
                  <div class="kv">
                    <div class="k">退房日期</div>
                    <div class="v">
                      {{ formatDate(orderDetail?.checkoutDate) }}
                    </div>
                  </div>
                  <div class="kv">
                    <div class="k">入住人數</div>
                    <div class="v">{{ orderDetail?.people }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">房價</div>
                    <div class="v">NT$ {{ fmt(orderDetail?.roomprice) }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">租車金額</div>
                    <div class="v">NT$ {{ fmt(orderDetail?.cartotal) }}</div>
                  </div>
                  <div class="kv highlight">
                    <div class="k">總金額</div>
                    <div class="v strong">
                      NT$ {{ fmt(orderDetail?.grandtotal) }}
                    </div>
                  </div>
                </v-col>
              </v-row>

              <v-divider class="my-4" />

              <v-row>
                <v-col cols="12" md="6">
                  <div class="kv">
                    <div class="k">付款編號</div>
                    <div class="v">{{ orderDetail?.paymentId }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">付款方式</div>
                    <div class="v">{{ orderDetail?.bookingMethod }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">付款時間</div>
                    <div class="v">{{ formatDate(orderDetail?.paidTime) }}</div>
                  </div>
                </v-col>

                <v-col cols="12" md="6">
                  <div class="kv compact">
                    <div class="k">訂單狀態</div>
                    <div class="v">
                      <v-select
                        v-model="newBookingStatus"
                        :items="bookingStatusOptions"
                        density="compact"
                        variant="outlined"
                        hide-details
                        class="mini-select"
                      />
                      <v-btn
                        size="small"
                        color="primary"
                        variant="elevated"
                        class="ml-2"
                        :loading="updating"
                        :disabled="
                          !newBookingStatus ||
                          newBookingStatus === orderDetail?.bookingStatus
                        "
                        @click="updateBookingStatus"
                        >確認更新</v-btn
                      >
                    </div>
                  </div>

                  <div class="kv compact">
                    <div class="k">付款狀態</div>
                    <div class="v">
                      <v-select
                        v-model="newMentStatus"
                        :items="mentStatusOptions"
                        density="compact"
                        variant="outlined"
                        hide-details
                        class="mini-select"
                      />
                      <v-btn
                        size="small"
                        color="primary"
                        variant="elevated"
                        class="ml-2"
                        :loading="updating"
                        :disabled="
                          !newMentStatus ||
                          newMentStatus === orderDetail?.mentStatus
                        "
                        @click="updateMentStatus"
                        >確認更新</v-btn
                      >
                    </div>
                  </div>
                </v-col>
              </v-row>
            </v-card-text>

            <v-divider />
            <v-card-actions class="justify-end">
              <v-btn variant="text" color="grey" @click="detailDialog = false"
                >關閉</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import { ref } from "vue";
import api from "@/api";
import { useRouter } from "vue-router";

const router = useRouter();

const customerId = ref("");
const lastEmail = ref("");
const orders = ref<any[]>([]);
const searched = ref(false);

const listLoading = ref(false);
const updating = ref(false);

const orderDetail = ref<any | null>(null);
const detailDialog = ref(false);

const bookingStatusOptions = ["待入住", "已完成", "已取消"];
const mentStatusOptions = ["待付款", "已付款", "已退款"];

const newBookingStatus = ref("");
const newMentStatus = ref("");

const headers = [
  { title: "用戶名稱", key: "username" },
  { title: "房源名稱", key: "houseName" },
  { title: "地址", key: "address" },
  { title: "電話", key: "tel" },
  { title: "床型", key: "bed", width: 120 },
  { title: "入住人數", key: "people", align: "end", width: 90 },
  { title: "預訂狀態", key: "bookingStatus", width: 110 },
  { title: "入住日期", key: "checkinDate", width: 120 },
  { title: "退房日期", key: "checkoutDate", width: 120 },
  { title: "總金額", key: "grandtotal", align: "end", width: 180 },
  {
    title: "明細",
    key: "actions",
    sortable: false,
    align: "center",
    width: 110,
  },
];

function statusColor(s?: string) {
  const k = (s || "").trim();
  if (k === "待入住") return "info";
  if (k === "已完成") return "success";
  if (k === "已取消") return "error";
  return "grey";
}

async function fetchOrders() {
  if (!customerId.value) {
    alert("請輸入用戶 Email");
    return;
  }
  listLoading.value = true;
  try {
    const res = await api.get("/admingetorderdetail/adminbyCustomer", {
      params: { email: customerId.value },
      withCredentials: true,
    });
    orders.value = Array.isArray(res.data) ? res.data : [];
    lastEmail.value = customerId.value;
  } catch (error: any) {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      alert("請先登入");
      orders.value = [];
      router.push({ name: "Homepage" });
    } else {
      console.error("取得資料失敗", error);
      throw error;
    }
  } finally {
    searched.value = true;
    listLoading.value = false;
  }
}

async function refreshList() {
  if (!lastEmail.value) return;
  listLoading.value = true;
  try {
    const res = await api.get("/admingetorderdetail/adminbyCustomer", {
      params: { email: lastEmail.value },
      withCredentials: true,
    });
    orders.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    console.error("刷新列表失敗", e);
  } finally {
    listLoading.value = false;
  }
}

async function fetchOrderDetail(bookingId: string) {
  try {
    const response = await api.get("/admingetorderdetail/admindetail", {
      params: { bookingId },
      withCredentials: true,
    });
    orderDetail.value = response.data;
    newBookingStatus.value = orderDetail.value?.bookingStatus ?? "";
    newMentStatus.value = orderDetail.value?.mentStatus ?? "";
    detailDialog.value = true;
  } catch (error: any) {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      alert("請先登入");
    } else {
      console.error("取得資料失敗", error);
      throw error;
    }
  }
}

function patchOrderRow(bookingId: string, patch: Partial<any>) {
  const i = orders.value.findIndex((o) => o.bookingId === bookingId);
  if (i !== -1) orders.value[i] = { ...orders.value[i], ...patch };
}

async function updateBookingStatus() {
  if (!orderDetail.value) return;
  updating.value = true;
  try {
    await api.post("/admingetorderdetail/updatebookingstatus", null, {
      params: {
        bookingId: orderDetail.value.bookingId,
        bookingStatus: newBookingStatus.value,
      },
      withCredentials: true,
    });
    orderDetail.value.bookingStatus = newBookingStatus.value;
    patchOrderRow(orderDetail.value.bookingId, {
      bookingStatus: newBookingStatus.value,
    });
    await refreshList();
    alert("訂單狀態更新成功");
  } catch (error) {
    console.error(error);
    alert("訂單狀態更新失敗");
  } finally {
    updating.value = false;
  }
}

async function updateMentStatus() {
  if (!orderDetail.value) return;
  updating.value = true;
  try {
    await api.post("/admingetorderdetail/updatementstatus", null, {
      params: {
        bookingId: orderDetail.value.bookingId,
        mentStatus: newMentStatus.value,
      },
      withCredentials: true,
    });
    orderDetail.value.mentStatus = newMentStatus.value;
    await refreshList();
    alert("付款狀態更新成功");
  } catch (error) {
    console.error(error);
    alert("付款狀態更新失敗");
  } finally {
    updating.value = false;
  }
}

function formatDate(v: any) {
  if (!v) return "—";
  const s = typeof v === "string" ? v : new Date(v).toISOString();
  return s.split("T")[0];
}
function fmt(n: any) {
  const num = Number(n ?? 0);
  return new Intl.NumberFormat("zh-TW").format(isNaN(num) ? 0 : num);
}
function clearOrders() {
  orders.value = [];
  searched.value = false;
}
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.title-wrap {
  display: flex;
  align-items: center;
}
.title {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  color: #222;
  letter-spacing: 0.5px;
}
.subtitle {
  color: #6b7280;
  font-size: 18px;
}

/* 卡片 */
.search-card {
  border: 1px solid #e5e7eb;
  background: linear-gradient(#fafafa, #f6f7f9);
  border-radius: 16px;
}
.data-card {
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  overflow: hidden;
}

/* 表格 */
.order-table :deep(th) {
  color: #6b7280;
  font-weight: 700 !important;
  font-size: 17px;
}
.order-table :deep(td) {
  font-size: 17px;
  line-height: 1.35;
  padding: 14px 12px;
}

.order-table :deep(thead) {
  background: #f8fafc;
}
.order-table :deep(tbody tr) {
  transition: background 0.15s ease;
}
.order-table :deep(tbody tr:nth-child(even)) {
  background: #fafafa;
}
.order-table :deep(tbody tr:hover) {
  background: #eef2ff;
}

.bed-cell {
  white-space: nowrap;
}

.amt {
  font-weight: 800;
  font-size: 20px;
  letter-spacing: 0.2px;
  text-align: right;
}

/* 明細卡 */
.detail-card {
  border-radius: 30px;
}
.kv {
  display: flex;
  margin: 6px 0;
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
}
.kv:last-child {
  border-bottom: none;
}
.kv.compact {
  align-items: center;
}
.k {
  width: 88px;
  color: #6b7280;
}
.v {
  flex: 1;
  font-weight: 600;
  color: #1f2937;
  word-break: break-word;
}
.highlight {
  background: #fafafa;
  padding: 8px 8px;
  border-radius: 12px;
}
.strong {
  font-weight: 800;
}
.mini-select {
  width: 140px;
  display: inline-block;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 220px;
  border-style: dashed !important;
}

/* 共用 */
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono",
    monospace;
}
.order-table :deep(td) {
  font-size: 17px;
  line-height: 1.5;
  padding: 18px 12px;
}
.order-table :deep(th) {
  color: #6b7280;
  font-weight: 700 !important;
  font-size: 17px;
  white-space: nowrap;
}
/*日期欄*/
.date-nowrap {
  white-space: nowrap;
}
</style>
