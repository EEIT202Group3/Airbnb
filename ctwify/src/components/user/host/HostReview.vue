<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "axios";
axios.defaults.withCredentials = true;

// 新增於 <script setup> 內
const replying = ref(false);
const replyDialog = ref(false);
const replyText = ref("");
const snackbar = ref({ show: false, color: "success", text: "" });

function showReplyBox() {
  if (!selected.value) return;
  replyText.value = selected.value.hostComm || ""; // 若已有回覆，帶入可再編修
  replyDialog.value = true;
}

async function submitReply() {
  if (!selected.value) return;
  if (!replyText.value.trim()) {
    snackbar.value = { show: true, color: "error", text: "請輸入回覆內容" };
    return;
  }

  try {
    replying.value = true;

    // 假設你的後端提供這個端點：PATCH /api/reviews/{reviewId}/host-reply
    // 也可以 POST 看你設計
    await axios.patch(`/api/reviews/${selected.value.reviewId}/host-reply`, {
      hostComm: replyText.value.trim(),
    });

    // 前端立即更新（避免重整）
    selected.value.hostComm = replyText.value.trim();
    const card = fullMap.value[selected.value.reviewId];
    if (card) card.hostComm = replyText.value.trim();

    replyDialog.value = false;
    snackbar.value = { show: true, color: "success", text: "已送出回覆" };
  } catch (e: any) {
    snackbar.value = {
      show: true,
      color: "error",
      text: e?.response?.data?.message || "送出失敗，請稍後再試",
    };
  } finally {
    replying.value = false;
  }
}

// ===== 後端 DTO（依你的實際欄位微調） =====
type HostReview = {
  reviewId: number | string;
  bookingId: string;
  houseName: string;
  customerEmail: string; // 或 username
  reviewDate: string | Date;
  cleanScore: number;
  commScore: number;
  valueScore: number;
  custComm: string;
  hostComm: string;
  images?: string[]; // 圖片檔名（後端若回傳逗號字串，可在 mounted 轉陣列）
};

// 卡片顯示資料
type ReviewCard = {
  id: string | number;
  title: string;
  subtitle: string;
  date: string | Date;
  avg: number;
  snippet: string;
};

const reviews = ref<ReviewCard[]>([]);
const fullMap = ref<Record<string | number, HostReview>>({});
const showDetail = ref(false);
const selected = ref<HostReview | null>(null);
const error = ref<string | null>(null);
const loading = ref(true);

const totalReviews = computed(() => reviews.value.length);

// 圖片基底（依你的 ResourceHandler 調整：/images/reviews 或 /images/listings）
const IMG_BASE = "http://localhost:8080/images/reviews";

// ===== 工具 =====
function pad2(n: number) {
  return n.toString().padStart(2, "0");
}
function fmtDate(v?: string | Date) {
  if (!v) return "-";
  const d = new Date(v);
  const y = d.getFullYear();
  const M = pad2(d.getMonth() + 1);
  const D = pad2(d.getDate());
  return `${y}-${M}-${D}`;
}
function avgScore(r: HostReview) {
  const a = Number(r.cleanScore || 0);
  const b = Number(r.commScore || 0);
  const c = Number(r.valueScore || 0);
  return Math.round(((a + b + c) / 3) * 10) / 10;
}
function snippet(s: string, len = 80) {
  const t = (s || "").trim();
  return t.length > len ? t.slice(0, len) + "…" : t;
}

// ===== 取資料 =====
onMounted(loadReviews);
async function loadReviews() {
  loading.value = true;
  try {
    // 依你的後端路由調整：/api/reviews/byhost 或 /api/reviews/host
    const { data } = await axios.get<HostReview[]>("/api/hosts/reviews/token/byHost");

    const list = Array.isArray(data) ? data : [];

    // 若 images 是逗號字串，可轉成陣列
    // list.forEach(r => { if (typeof (r as any).images === 'string') r.images = (r as any).images.split(',').map((s: string) => s.trim()).filter(Boolean) })

    fullMap.value = Object.fromEntries(list.map((r) => [r.reviewId, r]));
    reviews.value = list.map((r) => ({
      id: r.reviewId,
      title: r.houseName,
      subtitle: r.customerEmail,
      date: r.reviewDate,
      avg: avgScore(r),
      snippet: snippet(r.custComm, 90),
    }));
    console.log(reviews.value);
  } catch (e: any) {
    error.value = e?.response?.data?.error || "無法取得評論資料";
  } finally {
    loading.value = false;
  }
}

function openDetail(id: string | number) {
  const r = fullMap.value[id];
  if (r) {
    selected.value = r;
    showDetail.value = true;
  }
}

function maskEmail(email) {
  if (!email) return "";

  const [name, domain] = email.split("@");
  if (!domain) return email;

  // 保留前2與後2，中間補*
  if (name.length > 4) {
    return (
      name.slice(0, 2) +
      "*".repeat(name.length - 4) +
      name.slice(-2) +
      "@" +
      domain
    );
  } else {
    // 太短的名字，至少留頭尾
    return (
      name[0] +
      "*".repeat(Math.max(name.length - 2, 1)) +
      name.slice(-1) +
      "@" +
      domain
    );
  }
}
</script>

<template>
  <v-card
    class="pa-6 elevation-1 rounded-xl mx-auto review-wrap"
    max-width="1200"
  >
    <div class="d-flex align-center justify-space-between mb-4">
      <div class="d-flex align-center ga-2">
        <v-icon size="28">mdi-star-box-multiple</v-icon>
        <h2 class="m-0 fw-700">評論總覽</h2>
        <v-chip size="small" variant="tonal" color="primary"
          >{{ totalReviews }} 筆</v-chip
        >
      </div>
    </div>

    <v-divider class="mb-6" />

    <!-- 錯誤 -->
    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">{{
      error
    }}</v-alert>

    <!-- Loading skeleton -->
    <template v-if="loading">
      <v-row>
        <v-col v-for="i in 6" :key="i" cols="12" md="4">
          <v-skeleton-loader type="card" class="rounded-lg" />
        </v-col>
      </v-row>
    </template>

    <!-- 空狀態 -->
    <template v-else-if="reviews.length === 0">
      <v-sheet class="py-12 text-center rounded-lg" color="grey-lighten-4">
        <v-icon size="48" color="grey">mdi-emoticon-sad-outline</v-icon>
        <div class="text-h6 mt-2 mb-1">尚無評論</div>
        <div class="text-medium-emphasis">當旅客留下評論後會顯示在這裡</div>
      </v-sheet>
    </template>

    <!-- 清單 -->
    <template v-else>
      <v-row>
        <v-col v-for="r in reviews" :key="r.id" cols="12" md="4">
          <v-card
            class="review-card rounded-xl pa-4"
            :elevation="2"
            :ripple="true"
            @click="openDetail(r.id)"
          >
            <div class="d-flex align-center justify-space-between mb-2">
              <div class="text-body-2 text-medium-emphasis">
                {{ r.title || "—" }}
              </div>
              <div class="d-flex align-center ga-1">
                <v-icon size="18">mdi-star</v-icon>
                <b>{{ r.avg.toFixed(1) }}</b>
              </div>
            </div>

            <div class="text-caption text-medium-emphasis mb-2">
              住客：{{ maskEmail(r.subtitle) || "匿名" }}
            </div>

            <div class="text-body-2 mb-4 two-line">{{ r.snippet }}</div>

            <div class="d-flex align-center justify-space-between">
              <div class="text-caption text-disabled">
                <v-icon size="16">mdi-calendar</v-icon>
                <span class="ml-1">{{ fmtDate(r.date) }}</span>
              </div>
              <v-btn color="orange" variant="elevated" density="comfortable"
                >查看評論</v-btn
              >
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
    <v-card v-if="selected" class="rounded-xl">
      <v-toolbar density="comfortable" flat>
        <v-toolbar-title>評論明細</v-toolbar-title>
        <v-spacer />
        <div class="d-flex align-center ga-1 mr-2">
          <v-icon size="18">mdi-star</v-icon>
          <b>{{ avgScore(selected).toFixed(1) }}</b>
        </div>
      </v-toolbar>

      <v-divider />

      <v-card-text class="pt-6">
        <!-- 基本資料 -->
        <div class="section-title">基本資料</div>
        <div class="kv-list">
          <div class="kv-row">
            <div class="kv-label">訂單編號</div>
            <div class="kv-value mono">{{ selected.bookingId }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">房源</div>
            <div class="kv-value">{{ selected.listId }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">住客</div>
            <div class="kv-value">{{ selected.customerEmail }}</div>
          </div>
          <div class="kv-row">
            <div class="kv-label">評論時間</div>
            <div class="kv-value">
              {{ fmtDate(selected.reviewDate as any) }}
            </div>
          </div>
        </div>

        <!-- 評分明細 -->
        <div class="section-title mt-6">評分</div>
        <v-row class="mb-2" dense>
          <v-col cols="12" sm="4">
            <div class="label">乾淨度</div>
            <v-rating
              :model-value="Number(selected.cleanScore)"
              :length="5"
              :size="24"
              color="amber"
              readonly
            ></v-rating>
          </v-col>
          <v-col cols="12" sm="4">
            <div class="label">溝通</div>
            <v-rating
              :model-value="Number(selected.commScore)"
              :length="5"
              :size="24"
              color="amber"
              readonly
            ></v-rating>
          </v-col>
          <v-col cols="12" sm="4">
            <div class="label">性價比</div>
            <v-rating
              :model-value="Number(selected.valueScore)"
              :length="5"
              :size="24"
              color="amber"
              readonly
            ></v-rating>
          </v-col>
        </v-row>

        <!-- 文字評論 -->
        <div class="section-title mt-2">住客評論</div>
        <v-sheet class="pa-3 rounded-lg" color="grey-lighten-4">{{
          selected.cusComm
        }}</v-sheet>

        <div class="section-title mt-2">房東回覆</div>
        <v-sheet class="pa-3 rounded-lg" color="grey-lighten-4">{{
          selected.hostComm
        }}</v-sheet>
        <v-btn color="orange-darken-1" variant="elevated" @click="showReplyBox">
          回覆評論
        </v-btn>

        <!-- 圖片 -->
        <div
          v-if="selected.images && selected.images.length"
          class="section-title mt-6"
        >
          評論圖片
        </div>
        <v-row v-if="selected.images && selected.images.length" dense>
          <v-col v-for="(img, i) in selected.images" :key="i" cols="4">
            <a :href="`${IMG_BASE}/${img}`" target="_blank" rel="noopener">
              <v-img
                :src="`${IMG_BASE}/${img}`"
                aspect-ratio="1"
                cover
                class="rounded-lg"
              />
            </a>
          </v-col>
        </v-row>
      </v-card-text>

      <v-divider />
      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="showDetail = false">關閉</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-dialog v-model="replyDialog" max-width="600">
    <v-card class="rounded-xl">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-reply</v-icon> 房東回覆
        <v-spacer />
        <v-btn icon @click="replyDialog = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      <v-divider></v-divider>
      <v-card-text class="pt-4">
        <v-textarea
          v-model="replyText"
          label="輸入回覆內容"
          rows="5"
          auto-grow
          variant="outlined"
          density="comfortable"
          counter="500"
          :maxlength="500"
        />
      </v-card-text>
      <v-card-actions class="px-4 pb-4 justify-end">
        <v-btn variant="text" @click="replyDialog = false">取消</v-btn>
        <v-btn
          color="orange-darken-1"
          variant="elevated"
          :loading="replying"
          @click="submitReply"
        >
          送出回覆
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">
    {{ snackbar.text }}
  </v-snackbar>
</template>

<style scoped>
.fw-700 {
  font-weight: 700;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", monospace;
}

/* 外層與卡片樣式：沿用你的訂單樣式，實現一致視覺 */
.review-wrap {
  background: linear-gradient(180deg, #ffffff 0%, #fffaf4 100%);
}
.review-card {
  background: #fff;
  border: 1px solid #eee;
  transition: transform 0.18s ease, box-shadow 0.18s ease,
    border-color 0.18s ease;
}
.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
  border-color: #f2ab27;
}

/* Key–Value 區塊（與你的 orders 相同風格） */
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

/* 兩行截斷效果 */
.two-line {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.label {
  font-size: 0.85rem;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 4px;
}
</style>
