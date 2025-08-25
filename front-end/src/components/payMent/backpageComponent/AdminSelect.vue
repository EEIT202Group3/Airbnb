<template>
  <v-container class="py-10" max-width="1600">
    <div class="d-flex align-center mb-8">
      <v-icon size="40" class="mr-3">mdi-cash-multiple</v-icon>
      <h2 class="page-title">後台｜房東月結清單</h2>
      <v-spacer />
      <v-btn
        color="primary"
        @click="fetchPayouts"
        :loading="loading"
        class="big-btn"
      >
        重新整理
      </v-btn>
      <!--匯出CSV -->
      <v-btn
        variant="outlined"
        color="primary"
        class="big-btn ml-3"
        :disabled="loading"
        @click="exportHostCsv"
      >
        匯出 應付款房東明細 CSV
      </v-btn>
      <v-btn
        variant="outlined"
        color="primary"
        class="big-btn ml-3"
        :disabled="loading"
        @click="exportOrdersCsv"
      >
        匯出 每筆付款明細涵蓋的訂單 CSV
      </v-btn>
    </div>

    <!-- 篩選列 -->
    <v-card class="mb-6" rounded="xl">
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="filters.month"
              label="結算月份 (YYYY-MM)"
              hint="例如：2025-08"
              prepend-inner-icon="mdi-calendar-month"
              density="comfortable"
              clearable
              class="big-input"
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-select
              v-model="filters.status"
              :items="statusItems"
              label="狀態"
              prepend-inner-icon="mdi-tag"
              density="comfortable"
              clearable
              class="big-input"
            />
          </v-col>

          <v-col cols="12" md="4">
            <v-text-field
              v-model="filters.hostId"
              label="Host ID (GUID)"
              prepend-inner-icon="mdi-account-badge"
              density="comfortable"
              clearable
              class="big-input"
            />
          </v-col>
        </v-row>

        <div class="d-flex mt-4">
          <v-btn
            color="deep-orange-darken-1"
            class="mr-4 big-btn"
            @click="fetchPayouts"
            :loading="loading"
          >
            依條件查詢
          </v-btn>
          <v-btn
            variant="outlined"
            color="grey"
            @click="resetFilters"
            :disabled="loading"
            class="big-btn"
          >
            清空條件
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- 錯誤 -->
    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <!-- 主清單 -->
    <v-card rounded="xl">
      <v-data-table
        :headers="headers"
        :items="items"
        :items-per-page="10"
        :loading="loading"
        class="elevation-0 big-table"
      >
        <!-- 狀態 -->
        <template #item.status="{ item }">
          <v-chip
            size="small"
            :color="statusColor(item.status)"
            variant="flat"
            class="text-capitalize"
          >
            {{ item.status }}
          </v-chip>
        </template>

        <template #item.payoutId="{ item }">
          <code class="mono">{{ shortId(item.payoutId) }}</code>
          <v-btn
            size="small"
            variant="text"
            icon="mdi-content-copy"
            @click.stop="copy(item.payoutId)"
          />
        </template>

        <template #item.totalEarnings="{ item }">
          NT$ {{ fmt(item.totalEarnings) }}
        </template>
        <template #item.totalPlatformFee="{ item }">
          NT$ {{ fmt(item.totalPlatformFee) }}
        </template>
        <template #item.totalNetPayout="{ item }"
          ><strong>NT$ {{ fmt(item.totalNetPayout) }}</strong></template
        >

        <template #item.createdAt="{ item }">{{ dt(item.createdAt) }}</template>
        <template #item.updatedAt="{ item }">{{ dt(item.updatedAt) }}</template>
        <template #item.payoutDate="{ item }">{{
          dt(item.payoutDate)
        }}</template>

        <template #item.actions="{ item }">
          <div class="action-bar">
            <v-btn
              color="primary"
              variant="tonal"
              class="soft-btn"
              prepend-icon="mdi-eye-outline"
              @click.stop="openOrders(item)"
              type="button"
            >
              明細
            </v-btn>

            <v-btn
              color="success"
              variant="tonal"
              class="soft-btn"
              prepend-icon="mdi-cash-check"
              :disabled="isFinalStatus(item)"
              :loading="markingId === item.payoutId && markingType === 'paid'"
              @click.stop="markPaid(item)"
              type="button"
            >
              標記已支付
            </v-btn>

            <v-btn
              color="error"
              variant="tonal"
              class="soft-btn"
              prepend-icon="mdi-cancel"
              :disabled="isFinalStatus(item)"
              :loading="markingId === item.payoutId && markingType === 'cancel'"
              @click.stop="confirmCancel(item)"
              type="button"
            >
              取消撥款
            </v-btn>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- 明細彈窗 -->
    <v-dialog v-model="ordersDialog" max-width="1000">
      <v-card rounded="xl">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2">mdi-receipt-text</v-icon>
          撥款明細
          <v-spacer />
          <v-chip
            size="small"
            color="deep-orange-accent-2"
            v-if="current?.payoutId"
          >
            {{ current.payoutId }}
          </v-chip>
        </v-card-title>

        <v-card-text>
          <v-alert v-if="ordersError" type="error" variant="tonal" class="mb-3">
            {{ ordersError }}
          </v-alert>

          <v-data-table
            :headers="orderHeaders"
            :items="orders"
            :loading="ordersLoading"
            class="elevation-0 big-table"
            :items-per-page="10"
          >
            <template #item.bookingId="{ item }"
              ><code class="mono">{{ item.bookingId }}</code></template
            >
            <template #item.grossAmount="{ item }"
              >NT$ {{ fmt(item.grossAmount) }}</template
            >
            <template #item.platformFee="{ item }"
              >NT$ {{ fmt(item.platformFee) }}</template
            >
            <template #item.netAmount="{ item }"
              ><b>NT$ {{ fmt(item.netAmount) }}</b></template
            >
            <template #item.createdAt="{ item }">{{
              dt(item.createdAt)
            }}</template>
          </v-data-table>

          <v-divider class="my-4" />

          <div class="d-flex justify-end">
            <div class="text-right">
              <div>
                明細筆數：<b>{{ orders.length }}</b>
              </div>
              <div>
                租車收入：<b>NT$ {{ fmt(sumBy(orders, "grossAmount")) }}</b>
              </div>
              <div>
                平台抽成：<b>NT$ {{ fmt(sumBy(orders, "platformFee")) }}</b>
              </div>
              <div>
                房東實拿：<b>NT$ {{ fmt(sumBy(orders, "netAmount")) }}</b>
              </div>
            </div>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="grey" variant="outlined" @click="ordersDialog = false"
            >關閉</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 取消確認 Dialog -->
    <v-dialog v-model="cancelDialog" max-width="560">
      <v-card rounded="xl">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2" color="error">mdi-alert-octagon</v-icon>
          確認取消撥款
        </v-card-title>
        <v-card-text>
          <div class="mb-3">
            你正要取消此撥款單，受影響的訂單會回到
            <b>pending</b> 狀態以便重新結算。<br />
            <span class="text-error"
              >此操作不會刪除撥款單紀錄，但會移除其明細。</span
            >
          </div>
          <v-textarea
            v-model="cancelReason"
            label="取消原因（選填）"
            rows="3"
            auto-grow
            variant="outlined"
            prepend-inner-icon="mdi-text"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" color="grey" @click="cancelDialog = false"
            >返回</v-btn
          >
          <v-btn
            color="error"
            class="soft-btn"
            prepend-icon="mdi-cancel"
            :loading="
              markingId === cancelTarget?.payoutId && markingType === 'cancel'
            "
            @click="doCancel"
          >
            確認取消
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from "@/api"; // baseURL: http://localhost:8080/api/admins/

type PayoutRow = {
  payoutId: string;
  hostId: string;
  payoutMonth: string;
  totalEarnings: number | string;
  totalPlatformFee: number | string;
  totalNetPayout: number | string;
  status: string;
  orders: number;
  createdAt?: string | Date;
  updatedAt?: string | Date;
  payoutDate?: string | Date;
};

type PayoutOrderRow = {
  payoutOrderId: string;
  payoutId: string;
  bookingId: string;
  listId: number;
  grossAmount: number | string;
  platformFee: number | string;
  netAmount: number | string;
  createdAt?: string | Date;
};

const loading = ref(false);
const error = ref("");

const filters = ref({
  hostId: "",
  month: "",
  status: "",
});

const statusItems = ["generated", "scheduled", "paid", "cancelled"];

const headers = [
  { title: "PayoutId", value: "payoutId", width: 220 },
  { title: "Host ID", value: "hostId", width: 230 },
  { title: "月份", value: "payoutMonth", width: 100 },
  { title: "租車收入", value: "totalEarnings", align: "end", width: 130 },
  { title: "平台抽成", value: "totalPlatformFee", align: "end", width: 130 },
  { title: "房東實拿", value: "totalNetPayout", align: "end", width: 130 },
  { title: "狀態", value: "status", width: 120 },
  { title: "明細筆數", value: "orders", align: "end", width: 110 },
  { title: "建立時間", value: "createdAt", width: 160 },
  { title: "更新時間", value: "updatedAt", width: 160 },
  { title: "支付日", value: "payoutDate", width: 160 },
  { title: "", value: "actions", sortable: false, width: 320 },
];

const items = ref<PayoutRow[]>([]);

// 明細相關
const ordersDialog = ref(false);
const current = ref<PayoutRow | null>(null);
const orders = ref<PayoutOrderRow[]>([]);
const ordersLoading = ref(false);
const ordersError = ref("");
const orderHeaders = [
  { title: "PayoutOrderId", value: "payoutOrderId", width: 220 },
  { title: "BookingId", value: "bookingId", width: 260 },
  { title: "房源ID", value: "listId", width: 100, align: "end" },
  { title: "租車收入", value: "grossAmount", align: "end", width: 120 },
  { title: "平台抽成", value: "platformFee", align: "end", width: 120 },
  { title: "房東實拿", value: "netAmount", align: "end", width: 120 },
  { title: "建立時間", value: "createdAt", width: 160 },
];

// 標記 loading 類型
const markingId = ref<string | null>(null);
const markingType = ref<"paid" | "cancel" | null>(null);

// 取消彈窗
const cancelDialog = ref(false);
const cancelTarget = ref<PayoutRow | null>(null);
const cancelReason = ref("");

//匯出CSV
async function downloadCsv(url: string, filename: string) {
  try {
    const { data } = await api.get(url, {
      responseType: "blob",
      withCredentials: true,
    });
    const blob = new Blob([data], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    const objectUrl = URL.createObjectURL(blob);
    link.href = objectUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(objectUrl);
  } catch (e: any) {
    error.value = e?.response?.data || e?.message || "匯出失敗";
  }
}

function exportHostCsv() {
  const qs = new URLSearchParams();
  if (filters.value.hostId) qs.append("hostId", filters.value.hostId);
  if (filters.value.month) qs.append("month", filters.value.month);
  if (filters.value.status) qs.append("status", filters.value.status);

  const url = `payouts/export/host-payouts${qs.toString() ? `?${qs}` : ""}`;
  downloadCsv(url, "host_payouts.csv");
}

function exportOrdersCsv() {
  const url = `payouts/export/payout-orders`;
  downloadCsv(url, "payout_orders.csv");
}

// 狀態顏色
function statusColor(s = "") {
  switch (s.toLowerCase()) {
    case "generated":
      return "deep-orange";
    case "scheduled":
      return "amber";
    case "paid":
      return "green";
    case "cancelled":
      return "grey";
    default:
      return "blue-grey";
  }
}
function isFinalStatus(row?: PayoutRow | null) {
  const s = String(row?.status || "").toLowerCase();
  return s === "paid" || s === "cancelled";
}

async function fetchPayouts() {
  loading.value = true;
  error.value = "";
  try {
    const { data } = await api.get("/payouts/host", {
      params: {
        hostId: filters.value.hostId || undefined,
        month: filters.value.month || undefined,
        status: filters.value.status || undefined,
      },
      withCredentials: true,
    });
    items.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    error.value = e?.response?.data || e?.message || "載入失敗";
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.value = { hostId: "", month: "", status: "" };
  fetchPayouts();
}

async function openOrders(row: PayoutRow) {
  current.value = row;
  ordersDialog.value = true;
  ordersLoading.value = true;
  ordersError.value = "";
  orders.value = [];
  try {
    const { data } = await api.get(`/payouts/${row.payoutId}/orders`, {
      withCredentials: true,
    });
    orders.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    ordersError.value = e?.response?.data || e?.message || "明細取得失敗";
  } finally {
    ordersLoading.value = false;
  }
}

async function markPaid(row: PayoutRow) {
  markingId.value = row.payoutId;
  markingType.value = "paid";
  try {
    await api.post(
      "/payouts/markpaid",
      { payoutId: row.payoutId },
      { withCredentials: true }
    );
    await fetchPayouts();
  } catch (e: any) {
    error.value = e?.response?.data || e?.message || "標記支付失敗";
  } finally {
    markingId.value = null;
    markingType.value = null;
  }
}

function confirmCancel(row: PayoutRow) {
  cancelTarget.value = row;
  cancelReason.value = "";
  cancelDialog.value = true;
}

async function doCancel() {
  if (!cancelTarget.value) return;
  markingId.value = cancelTarget.value.payoutId;
  markingType.value = "cancel";
  try {
    await api.post(
      "/payouts/cancel",
      { payoutId: cancelTarget.value.payoutId, reason: cancelReason.value },
      { withCredentials: true }
    );
    cancelDialog.value = false;
    await fetchPayouts();
  } catch (e: any) {
    error.value = e?.response?.data || e?.message || "取消撥款失敗";
  } finally {
    markingId.value = null;
    markingType.value = null;
  }
}

function fmt(n: unknown) {
  if (n == null) return "0";
  const num =
    typeof n === "object" && n !== null && "toString" in (n as any)
      ? Number((n as any).toString())
      : Number(n);
  return new Intl.NumberFormat("zh-TW").format(isNaN(num) ? 0 : num);
}
function dt(s?: string | Date) {
  if (!s) return "";
  const d = typeof s === "string" ? new Date(s) : s;
  return new Date(d).toLocaleString("zh-TW");
}
function shortId(id?: string) {
  if (!id) return "";
  const s = String(id);
  return s.length > 12 ? `${s.slice(0, 8)}…${s.slice(-4)}` : s;
}
function copy(text: string) {
  navigator.clipboard?.writeText(String(text));
}
function sumBy<T extends Record<string, any>>(arr: T[], key: keyof T) {
  return arr.reduce((acc, it) => acc + Number(it?.[key] ?? 0), 0);
}

onMounted(fetchPayouts);
</script>

<style scoped>
.page-title {
  font-size: 1.8rem;
  font-weight: bold;
}

.big-btn {
  font-size: 1.1rem;
  padding: 12px 20px;
}

.big-input :deep(input),
.big-input :deep(.v-field__input) {
  font-size: 1.1rem !important;
}

.big-table td,
.big-table th {
  font-size: 1.1rem !important;
  padding: 14px 12px !important;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono",
    monospace;
}
.action-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}
.soft-btn {
  border-radius: 999px;
  text-transform: none;
  letter-spacing: 0.2px;
  font-weight: 600;
  padding-inline: 14px !important;
}
</style>
