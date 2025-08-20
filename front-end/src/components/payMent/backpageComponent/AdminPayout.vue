<template>
  <v-container class="py-8" max-width="1100">
    <div class="d-flex align-center mb-6">
      <v-icon size="30" class="mr-2">mdi-invoice-text-clock-outline</v-icon>
      <h2 class="m-0">後台｜房東月結支付</h2>
    </div>

    <!-- 區塊一：單筆訂單拆帳預覽 -->
    <v-card class="mb-6" rounded="xl">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-receipt-text</v-icon>
        單筆訂單拆帳預覽
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="bookingId"
              label="Booking ID"
              density="comfortable"
              prepend-inner-icon="mdi-identifier"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-center">
            <v-btn
              color="lightgray"
              :loading="loadingOrder"
              @click="fetchOrderPreview"
            >
              取得拆帳
            </v-btn>
          </v-col>
        </v-row>

        <v-alert v-if="orderError" type="error" variant="tonal" class="mb-4">
          {{ orderError }}
        </v-alert>

        <v-table v-if="order">
          <tbody>
            <tr>
              <th class="text-no-wrap">房源</th>
              <td>{{ order.houseName }}</td>
            </tr>
            <tr>
              <th>地址</th>
              <td>{{ order.address }}</td>
            </tr>
            <tr>
              <th>床型</th>
              <td>{{ order.bed }}</td>
            </tr>
            <tr>
              <th>房屋定價</th>
              <td>NT$ {{ fmt(order.price) }}</td>
            </tr>
            <tr>
              <th>租車金額</th>
              <td>NT$ {{ fmt(order.cartotal) }}</td>
            </tr>
            <tr>
              <th>總金額</th>
              <td>NT$ {{ fmt(order.grandTotal) }}</td>
            </tr>
            <tr>
              <th>平台抽成(比)</th>
              <td>20%</td>
            </tr>
            <tr>
              <th>平台抽成(額)</th>
              <td>NT$ {{ fmt(order.platformFeeAmount) }}</td>
            </tr>
            <tr>
              <th>房東實拿</th>
              <td class="font-weight-bold">
                NT$ {{ fmt(order.hostNetAmount) }}
              </td>
            </tr>
            <tr>
              <th>結算月份</th>
              <td>{{ order.settlementMonth }}</td>
            </tr>
          </tbody>
        </v-table>
      </v-card-text>
    </v-card>

    <!-- 區塊二：月結預覽/產生/標記支付 -->
    <v-card rounded="xl">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-calendar-month</v-icon>
        月結預覽 / 產生 / 標記支付
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="month"
              label="結算月份 (YYYY-MM)"
              hint="例如：2025-08"
              density="comfortable"
              prepend-inner-icon="mdi-calendar"
            />
          </v-col>
          <v-col cols="12" md="8" class="d-flex align-center">
            <v-btn
              class="mr-3"
              color="lightgray"
              :loading="loadingMonthly"
              @click="fetchMonthlyPreview"
            >
              取得月結預覽
            </v-btn>
            <v-btn
              class="mr-3"
              color="lightgray"
              :loading="generating"
              @click="generateLock"
            >
              產生/鎖單
            </v-btn>
          </v-col>
        </v-row>

        <v-alert v-if="monthlyError" type="error" variant="tonal" class="mb-4">
          {{ monthlyError }}
        </v-alert>

        <v-table v-if="rows.length">
          <thead>
            <tr>
              <th class="text-left">Host ID</th>
              <th class="text-left">Payout 月份</th>
              <th class="text-right">租車收入</th>
              <th class="text-right">平台抽成</th>
              <th class="text-right">房東實拿</th>
              <th class="text-right">筆數</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.hostId">
              <td>{{ r.hostId }}</td>
              <td>{{ r.payoutMonth }}</td>
              <td class="text-right">NT$ {{ fmt(r.totalEarnings) }}</td>
              <td class="text-right">NT$ {{ fmt(r.totalPlatformFee) }}</td>
              <td class="text-right font-weight-bold">
                NT$ {{ fmt(r.totalNetPayout) }}
              </td>
              <td class="text-right">{{ r.orders }}</td>
            </tr>
          </tbody>
        </v-table>

        <v-divider class="my-6"></v-divider>

        <!-- 標記支付：這裡先提供手動輸入 payoutId（因為 /generate 目前不回傳 id） -->
        <div class="d-flex align-center">
          <v-text-field
            v-model="payoutId"
            label="payoutId（從 DB/host_payouts 複製）"
            density="comfortable"
            class="mr-4"
            style="max-width: 520px"
            prepend-inner-icon="mdi-identifier"
          />
          <v-btn color="success" :loading="marking" @click="markPaid">
            標記已支付
          </v-btn>
        </div>

        <v-alert
          v-if="markMsg"
          :type="markOk ? 'success' : 'error'"
          variant="tonal"
          class="mt-4"
        >
          {{ markMsg }}
        </v-alert>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import api from "@/api";

const bookingId = ref("");
const order = ref(null);
const loadingOrder = ref(false);
const orderError = ref("");

const month = ref("");
const rows = ref([]);
const loadingMonthly = ref(false);
const monthlyError = ref("");
const generating = ref(false);

const payoutId = ref("");
const marking = ref(false);
const markMsg = ref("");
const markOk = ref(false);

// 單筆訂單拆帳預覽
async function fetchOrderPreview() {
  order.value = null;
  orderError.value = "";
  if (!bookingId.value) {
    orderError.value = "請輸入 bookingId";
    return;
  }
  loadingOrder.value = true;
  try {
    const { data } = await api.get("/payouts/preview", {
      params: { bookingId: bookingId.value },
      withCredentials: true,
    });
    order.value = data;
  } catch (e) {
    orderError.value = e?.response?.data || e?.message || "取得失敗";
  } finally {
    loadingOrder.value = false;
  }
}

// 月結預覽（需要後端補上 /monthly-preview）
async function fetchMonthlyPreview() {
  rows.value = [];
  monthlyError.value = "";
  if (!month.value) {
    monthlyError.value = "請輸入月份 (YYYY-MM)";
    return;
  }
  loadingMonthly.value = true;
  try {
    const { data } = await api.get("/payouts/monthly-preview", {
      params: { month: month.value },
      withCredentials: true,
    });

    rows.value = normalizeMonthly(data, month.value);

    if (!rows.value.length) {
      monthlyError.value = "此月份沒有可顯示的月結資料。";
    }
  } catch (e) {
    monthlyError.value = e?.response?.data || e?.message || "取得失敗";
  } finally {
    loadingMonthly.value = false;
  }
}

/** 把後端回來的資料標準化成你的表格要的 PayoutRow 陣列 */
function normalizeMonthly(data, month) {
  // 後端若已回 { rows: [...] }，直接用
  if (data && Array.isArray(data.rows)) {
    return data.rows;
  }
  // 後端若回的是「每筆訂單」的陣列，這裡把它 group by host 後匯總成 PayoutRow
  if (Array.isArray(data)) {
    const byHost = new Map();
    for (const o of data) {
      // 後端不同欄位名時做容錯（請依你的實際欄位調整）
      const hostKey = o.hostId || o.host_id || o.username || "UNKNOWN";

      // 金額欄位容錯（依你後端實際欄位名調整）
      const gross = Number(
        o.total_amount ?? o.gross_amount ?? o.grandTotal ?? 0
      );
      const fee = Number(o.platform_fee_amount ?? o.platform_fee ?? 0);
      const net = Number(o.host_net_amount ?? o.net_amount ?? gross - fee);

      const curr = byHost.get(hostKey) || {
        hostId: hostKey,
        payoutMonth: month,
        totalEarnings: 0,
        totalPlatformFee: 0,
        totalNetPayout: 0,
        orders: 0,
      };

      curr.totalEarnings += isNaN(gross) ? 0 : gross;
      curr.totalPlatformFee += isNaN(fee) ? 0 : fee;
      curr.totalNetPayout += isNaN(net) ? 0 : net;
      curr.orders += 1;

      byHost.set(hostKey, curr);
    }
    return Array.from(byHost.values());
  }
  return [];
}

// 產生/鎖單
async function generateLock() {
  if (!month.value) {
    monthlyError.value = "請輸入月份 (YYYY-MM)";
    return;
  }
  generating.value = true;
  monthlyError.value = "";
  try {
    await api.post("/payouts/generate", null, {
      params: { month: month.value },
      withCredentials: true,
    });
    await fetchMonthlyPreview(); // 產生後再抓一次預覽
  } catch (e) {
    monthlyError.value = e?.response?.data || e?.message || "產生失敗";
  } finally {
    generating.value = false;
  }
}

// 標記已支付（Body: { payoutId }）
async function markPaid() {
  markMsg.value = "";
  if (!payoutId.value) {
    markMsg.value = "請輸入 payoutId";
    markOk.value = false;
    return;
  }
  marking.value = true;
  try {
    await api.post(
      "/payouts/markpaid",
      { payoutId: payoutId.value },
      { withCredentials: true }
    );
    markOk.value = true;
    markMsg.value = "已標記支付";
  } catch (e) {
    markOk.value = false;
    markMsg.value = e?.response?.data || e?.message || "標記失敗";
  } finally {
    marking.value = false;
  }
}

function fmt(n) {
  if (n == null) return "0";
  const num =
    typeof n === "object" && "toString" in n ? Number(n.toString()) : Number(n);
  return new Intl.NumberFormat("zh-TW").format(isNaN(num) ? 0 : num);
}
</script>
