<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "axios";
axios.defaults.withCredentials = true;

// 後端 DTO
type HostOrder = {
  bookingId: string;
  username: string;
  houseName: string;
  address: string;
  tel: string;
  bed: string;
  people: number;
  bookingStatus: string;
  checkinDate: string | Date;
  checkoutDate: string | Date;
  grandtotal: number | string;
  hostNetAmount: number | string;
  cartotal: number | string;
  platformFeeAmount: number | string;
};

// 卡片資料
type OrderCard = {
  id: string;
  amount: number;
  date: string | Date;
  status: string;
};

const orders = ref<OrderCard[]>([]);
const fullMap = ref<Record<string, HostOrder>>({});
const showDetail = ref(false);
const selectedOrder = ref<HostOrder | null>(null);
const error = ref<string | null>(null);
const loading = ref(true);

// 取資料
onMounted(loadOrders);
async function loadOrders() {
  loading.value = true;
  try {
    const { data } = await axios.get<HostOrder[]>("/api/hosts/orderconfirm/byhost");
    const list = Array.isArray(data) ? data : [];
    fullMap.value = Object.fromEntries(list.map((o) => [o.bookingId, o]));
    orders.value = list.map((o) => ({
      id: o.bookingId,
      amount: Number((o as any).grandtotal ?? 0),
      date: o.checkinDate,
      status: o.bookingStatus,
    }));
  } catch (e: any) {
    error.value = e?.response?.data || "無法取得訂單資料";
  } finally {
    loading.value = false;
  }
}

function openDetail(bookingId: string) {
  const full = fullMap.value[bookingId];
  if (full) {
    selectedOrder.value = full;
    showDetail.value = true;
  }
}

// ====== 顯示工具 ======
function pad2(n: number) {
  return n.toString().padStart(2, "0");
}

function fmtDate(v?: string | Date) {
  if (!v) return "-";
  const d = new Date(v);
  const y = d.getFullYear();
  const M = pad2(d.getMonth() + 1);
  const D = pad2(d.getDate());
  const h = pad2(d.getHours());
  const m = pad2(d.getMinutes());
  return `${y}-${M}-${D} ${h}:${m}`;
}

function fmtAmount(n?: number) {
  return new Intl.NumberFormat("zh-TW", { maximumFractionDigits: 0 }).format(
    n || 0
  );
}

function statusColor(status?: string) {
  switch ((status || "").trim()) {
    case "待入住":
      return "amber";
    case "已入住":
      return "blue";
    case "完成":
      return "green";
    case "已取消":
      return "red";
    default:
      return "grey";
  }
}

const totalOrders = computed(() => orders.value.length);
</script>

<template>
  <v-card
    class="pa-6 elevation-1 rounded-xl mx-auto order-wrap"
    max-width="1200"
  >
    <div class="d-flex align-center justify-space-between mb-4">
      <div class="d-flex align-center ga-2">
        <v-icon size="28">mdi-clipboard-text-clock</v-icon>
        <h2 class="m-0 fw-700">訂單總覽</h2>
        <v-chip size="small" variant="tonal" color="primary"
          >{{ totalOrders }} 筆</v-chip
        >
      </div>
      <div class="text-disabled text-caption"></div>
    </div>

    <v-divider class="mb-6" />

    <!-- 錯誤 -->
    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
      {{ error }}
    </v-alert>

    <!-- Loading skeleton -->
    <template v-if="loading">
      <v-row>
        <v-col v-for="i in 6" :key="i" cols="12" md="4">
          <v-skeleton-loader type="card" class="rounded-lg" />
        </v-col>
      </v-row>
    </template>

    <!-- 空狀態 -->
    <template v-else-if="orders.length === 0">
      <v-sheet class="py-12 text-center rounded-lg" color="grey-lighten-4">
        <v-icon size="48" color="grey">mdi-inbox-outline</v-icon>
        <div class="text-h6 mt-2 mb-1">目前沒有訂單</div>
        <div class="text-medium-emphasis">當有新的訂單時會顯示在這裡</div>
      </v-sheet>
    </template>

    <!-- 清單 -->
    <template v-else>
      <v-row>
        <v-col v-for="o in orders" :key="o.id" cols="12" md="4">
          <v-card
            class="order-card rounded-xl pa-4"
            :elevation="2"
            :ripple="true"
            @click="openDetail(o.id)"
          >
            <div class="d-flex align-center justify-space-between mb-3">
              <div class="text-body-2 text-medium-emphasis">訂單編號</div>
              <v-chip
                :color="statusColor(o.status)"
                size="small"
                variant="flat"
              >
                {{ o.status || "狀態未知" }}
              </v-chip>
            </div>

            <div class="text-truncate fw-700 mb-2">{{ o.id }}</div>

            <div class="d-flex align-center mb-2 ga-2 text-medium-emphasis">
              <v-icon size="18">mdi-calendar-start</v-icon>
              <span>入住 {{ fmtDate(o.date) }}</span>
            </div>

            <div class="d-flex align-center mb-4 ga-2 text-medium-emphasis">
              <v-icon size="18">mdi-cash</v-icon>
              <span
                >金額 NT$
                <span class="fw-700 text-high-emphasis">{{
                  fmtAmount(o.amount)
                }}</span></span
              >
            </div>

            <div class="d-flex justify-end">
              <v-btn color="orange" variant="elevated" density="comfortable">
                查看明細
              </v-btn>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-card>

  <!-- 詳細資訊 Dialog -->
  <v-dialog
    v-model="showDetail"
    max-width="760"
    transition="dialog-bottom-transition"
  >
    <v-card v-if="selectedOrder" class="rounded-xl">
      <v-toolbar density="comfortable" flat>
        <v-toolbar-title>訂單明細</v-toolbar-title>
        <v-spacer />
        <v-chip
          :color="statusColor(selectedOrder.bookingStatus)"
          size="small"
          variant="flat"
        >
          {{ selectedOrder.bookingStatus || "—" }}
        </v-chip>
      </v-toolbar>

      <v-divider />

      <v-card-text class="pt-6">
        <!-- 基本資料 -->
        <div class="section-title">基本資料</div>
        <div class="kv-list">
          <div class="kv-row">
            <div class="kv-label">訂單編號</div>
            <div class="kv-value mono">{{ selectedOrder.bookingId }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">住客姓名</div>
            <div class="kv-value">{{ selectedOrder.username }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">房源名稱</div>
            <div class="kv-value">{{ selectedOrder.houseName }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">房型</div>
            <div class="kv-value">{{ selectedOrder.bed }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">電話</div>
            <div class="kv-value">{{ selectedOrder.tel }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">地址</div>
            <div class="kv-value">{{ selectedOrder.address }}</div>
          </div>
        </div>

        <!-- 時間 -->
        <div class="section-title mt-6">時間</div>
        <div class="kv-list">
          <div class="kv-row">
            <div class="kv-label">入住日期</div>
            <div class="kv-value">
              {{ fmtDate(selectedOrder.checkinDate as any) }}
            </div>
          </div>
          <div class="kv-row">
            <div class="kv-label">退房日期</div>
            <div class="kv-value">
              {{ fmtDate(selectedOrder.checkoutDate as any) }}
            </div>
          </div>
          <div class="kv-row">
            <div class="kv-label">人數</div>
            <div class="kv-value">{{ selectedOrder.people }} 位</div>
          </div>
        </div>

        <!-- 金額 -->
        <div class="section-title mt-6">費用</div>
        <div class="kv-list">
          <div class="kv-row">
            <div class="kv-label">平台手續費</div>
            <div class="kv-value">
              NT$
              {{ fmtAmount(Number(selectedOrder.platformFeeAmount as any)) }}
            </div>
          </div>
          <div class="kv-row" v-if="selectedOrder.hostNetAmount != null">
            <div class="kv-label">房東淨收</div>
            <div class="kv-value">
              NT$ {{ fmtAmount(Number(selectedOrder.hostNetAmount as any)) }}
            </div>
          </div>
          <div class="kv-row" v-if="selectedOrder.cartotal != null">
            <div class="kv-label">租車金額</div>
            <div class="kv-value">
              NT$ {{ fmtAmount(Number(selectedOrder.cartotal as any)) }}
            </div>
          </div>
          <div class="kv-row highlight">
            <div class="kv-label">總金額</div>
            <div class="kv-value total">
              NT$ {{ fmtAmount(Number(selectedOrder.grandtotal as any)) }}
            </div>
          </div>
        </div>
      </v-card-text>

      <v-divider />
      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="showDetail = false">關閉</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.fw-700 {
  font-weight: 700;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", monospace;
}

/* 外層卡片背景微微紋理 */
.order-wrap {
  background: linear-gradient(180deg, #ffffff 0%, #fffaf4 100%);
}

/* 單筆卡片樣式與 hover 動畫 */
.order-card {
  background: #fff;
  border: 1px solid #eee;
  transition: transform 0.18s ease, box-shadow 0.18s ease,
    border-color 0.18s ease;
}
.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
  border-color: #f2ab27;
}

.label {
  font-size: 0.85rem;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 4px;
}
.value {
  font-size: 1rem;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 600;
  word-break: break-all;
}
.section-title {
  font-size: 0.9rem;
  color: rgba(0, 0, 0, 0.6);
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.kv-list {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
}
.kv-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px 14px;
  border-bottom: 1px solid #eee;
}
.kv-row:last-child {
  border-bottom: none;
}
.kv-label {
  width: 96px;
  flex: none;
  color: rgba(0, 0, 0, 0.55);
}
.kv-value {
  flex: 1;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 600;
  word-break: break-word;
}
.kv-row.highlight {
  background: #fffaf1;
}
.kv-row .total {
  font-weight: 800;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", monospace;
}
</style>