<template>
  <v-container class="py-8" max-width="1100">
    <div class="d-flex align-center mb-6">
      <v-icon size="30" class="mr-2">mdi-invoice-text-clock-outline</v-icon>
      <h2 class="m-0">後台｜房東月結支付</h2>
    </div>

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

const month = ref("");
const rows = ref([]);
const loadingMonthly = ref(false);
const monthlyError = ref("");
const generating = ref(false);

const payoutId = ref("");
const marking = ref(false);
const markMsg = ref("");
const markOk = ref(false);

// 月結預覽
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

function normalizeMonthly(data, month) {
  if (data && Array.isArray(data.rows)) {
    return data.rows;
  }
  if (Array.isArray(data)) {
    const byHost = new Map();
    for (const o of data) {
      const hostKey = o.hostId || o.host_id || o.username || "UNKNOWN";
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
    await fetchMonthlyPreview();
  } catch (e) {
    monthlyError.value = e?.response?.data || e?.message || "產生失敗";
  } finally {
    generating.value = false;
  }
}

// 標記已支付
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
