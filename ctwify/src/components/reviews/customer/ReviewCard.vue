<template>
  <v-col cols="12" class="d-flex justify-center">
    <v-card
      class="w-100 soft-card hoverable"
      elevation="2"
      rounded="xl"
      max-width="1000"
    >
      <!-- Header / Title -->
      <v-card-title class="d-flex align-center gap-4">
        <v-img
          :src="
            review?.image1
              ? `http://localhost:8080/images/reviews/${review.image1}`
              : defaultImg
          "
          alt="Cover"
          height="100"
          width="100"
          class="rounded"
          cover
        />
        <div class="text-subtitle-1 font-weight-bold text-deep-orange">
          {{ title }}
        </div>
        <v-spacer />
      </v-card-title>

      <v-divider class="mx-4"></v-divider>

      <!-- Summary -->
      <v-card-text class="text-wrap py-3">
        {{ review?.cusComm || "（尚無評論內容）" }}
      </v-card-text>

      <!-- Actions -->
      <v-card-actions class="px-4 pb-4">
        <v-spacer />
        <v-btn
          color="orange-darken-1"
          variant="elevated"
          :disabled="saving || deleting"
          @click="showDialog(review)"
          prepend-icon="mdi-eye"
        >
          顯示評論
        </v-btn>
      </v-card-actions>
    </v-card>

    <!-- Dialog -->
    <v-dialog v-model="dialog" max-width="720">
      <v-card class="soft-card" rounded="xl">
        <v-card-title class="text-h6 d-flex align-center">
          <v-icon class="mr-2" color="deep-orange-accent-3"
            >mdi-file-document-alert-outline</v-icon
          >
          <span class="card-title">評論詳情</span>
          <v-spacer />
          <v-btn icon :disabled="saving || deleting" @click="closeDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-divider class="mx-4"></v-divider>

        <v-card-text>
          <!-- Meta -->
          <v-row dense class="mb-2">
            <v-col cols="12" sm="6">
              <span class="kv">訂單編號：</span>
              <span class="value">{{ selectedReview.bookingId }}</span>
            </v-col>
            <v-col cols="12" sm="6">
              <span class="kv">房客信箱：</span>
              <span class="value">{{ selectedReview.customerEmail }}</span>
            </v-col>
            <v-col cols="12" sm="6">
              <span class="kv">房東信箱：</span>
              <span class="value">{{ selectedReview.hostEmail }}</span>
            </v-col>
            <v-col cols="12" sm="6">
              <span class="kv">評論日期：</span>
              <span class="value">{{ selectedReview.reviewDate || "-" }}</span>
            </v-col>
          </v-row>

          <!-- Ratings -->
          <div class="mb-2">
            <span class="kv">乾淨評分：</span>
            <v-rating
              v-model="selectedReview.cleanScore"
              :length="5"
              size="32"
              color="yellow-darken-3"
              background-color="grey-lighten-1"
              :readonly="!isEditing"
            />
          </div>
          <div class="mb-2">
            <span class="kv">溝通評分：</span>
            <v-rating
              v-model="selectedReview.commScore"
              :length="5"
              size="32"
              color="yellow-darken-3"
              background-color="grey-lighten-1"
              :readonly="!isEditing"
            />
          </div>
          <div class="mb-4">
            <span class="kv">性價比：</span>
            <v-rating
              v-model="selectedReview.valueScore"
              :length="5"
              size="32"
              color="yellow-darken-3"
              background-color="grey-lighten-1"
              :readonly="!isEditing"
            />
          </div>

          <!-- Texts -->
          <div class="mb-3">
            <span class="kv">房東回覆：</span>
            <div class="mt-1 value">
              {{ selectedReview.hostComm || "（無房東回覆）" }}
            </div>
          </div>

          <div class="mb-4">
            <span class="kv">房客評論：</span>
            <div v-if="!isEditing" class="mt-1 value">
              {{ selectedReview.cusComm || "（無評論）" }}
            </div>
            <v-textarea
              v-else
              v-model="selectedReview.cusComm"
              label="編輯房客評論"
              rows="3"
              variant="outlined"
              auto-grow
              density="comfortable"
              class="mt-1"
            />
          </div>

          <!-- Images -->
          <v-row dense class="mt-2">
            <v-col
              v-for="(img, index) in selectedReview.images"
              :key="`img-${index}`"
              cols="4"
              class="d-flex justify-center"
            >
              <div class="thumb-wrapper">
                <v-img :src="previewUrl(img)" cover class="rounded thumb" />
                <v-btn
                  v-if="isEditing && img !== null"
                  size="x-small"
                  icon
                  class="thumb-close"
                  @click="removeImage(index)"
                >
                  <v-icon size="18">mdi-close</v-icon>
                </v-btn>
              </div>
            </v-col>
          </v-row>

          <!-- Add Images -->
          <div class="d-flex justify-center mt-4" v-if="isEditing">
            <v-file-input
              v-model="fileInputModel"
              label="新增圖片（最多 3 張）"
              accept="image/*"
              prepend-icon="mdi-plus"
              show-size
              multiple
              hide-details
              variant="outlined"
              density="comfortable"
              style="max-width: 320px"
              :disabled="saving || cannotAddMore"
              @change="onFilesPicked"
            />
            <div v-if="cannotAddMore" class="text-caption ml-2">
              （已達 3 張上限）
            </div>
          </div>
        </v-card-text>

        <v-card-actions class="px-4 pb-4 justify-end">
          <v-btn
            color="orange-darken-1"
            variant="elevated"
            :loading="saving"
            :disabled="deleting"
            @click="toggleEdit"
          >
            {{ isEditing ? "儲存" : "修改" }}
          </v-btn>
          <v-btn
            color="error"
            variant="tonal"
            :loading="deleting"
            :disabled="saving"
            @click="deleteReview"
          >
            刪除
          </v-btn>
          <v-btn
            color="orange-darken-1"
            variant="text"
            :disabled="saving || deleting"
            @click="closeDialog"
          >
            關閉
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-col>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from "vue";
import axios from "axios";

/** ======== 常數 / 工具 ======== */
const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080"; // 建議用環境變數
const IMAGE_BASE = `${API_BASE}/images/listing`; // 依後端實際路徑調整
const MAX_IMAGES = 3;
const MAX_FILE_SIZE_MB = 5; // 單檔 5MB 上限，可調整
const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp"]; // 可擴充
const isFile = (v: unknown): v is File =>
  typeof File !== "undefined" && v instanceof File;

/** ======== Props / Emits ======== */
type Review = {
  reviewId: number;
  listId: number;
  bookingId: number;
  customerEmail: String;
  hostEmail: String;
  reviewDate: string;
  cleanScore: number;
  commScore: number;
  valueScore: number;
  cusComm: string;
  hostComm: string;
  image1?: string | null;
  image2?: string | null;
  image3?: string | null;
};

const props = defineProps<{
  review: Review;
  title?: string;
  defaultImgSrc?: string;
  imageBasePath?: string; // 若後端路徑不同可自訂
}>();

const emit = defineEmits<{
  (e: "update", payload: Review): void;
  (e: "deleted"): void;
}>();

/** ======== 靜態資源 ======== */
const defaultImg = props.defaultImgSrc ?? "/icon/default.png";

/** ======== UI 狀態 ======== */
const dialog = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const deleting = ref(false);

/** ======== 選中評論與影像槽 ======== */
type MixedImage = File | string | null;
// 原始檔名快照：用於判斷哪些槽被刪除
const originals = ref<(string | null)[]>([null, null, null]);
// 預設選取之評論
const selectedReview = ref<Review & { images: MixedImage[] }>({
  ...(props.review as Review),
  images: [
    props.review?.image1 ?? null,
    props.review?.image2 ?? null,
    props.review?.image3 ?? null,
  ],
});

// 檔案輸入暫存（為了能 reset）
const fileInputModel = ref<File[] | null>(null);

/** ======== 封面圖（卡片標題用） ======== */
const coverSrc = computed(() => {
  const base = props.imageBasePath ?? IMAGE_BASE;
  const img = props.review?.image1;
  return img ? `${base}/${img}` : defaultImg;
});

/** ======== 標題 ======== */
const title = computed(
  () => props.title || "海漾小木屋：一邊森林一邊太平洋(適合1-3人獨棟+廚房)"
);

/** ======== Object URL 快取與釋放 ======== */
const objectUrlCache = new Map<File, string>();

function previewUrl(img: MixedImage) {
  if (!img) return defaultImg;
  if (isFile(img)) {
    if (!objectUrlCache.has(img)) {
      objectUrlCache.set(img, URL.createObjectURL(img));
    }
    return objectUrlCache.get(img)!;
  }
  // 舊檔名
  const base = props.imageBasePath ?? IMAGE_BASE;
  return typeof img === "string" && img.trim() ? `${base}/${img}` : defaultImg;
}

onBeforeUnmount(() => {
  for (const url of objectUrlCache.values()) URL.revokeObjectURL(url);
  objectUrlCache.clear();
});

/** ======== Dialog ======== */
function showDialog(item: Review) {
  selectedReview.value = {
    ...(item as Review),
    images: [
      item.image1 ?? null,
      item.image2 ?? null,
      item.image3 ?? null,
    ].slice(0, MAX_IMAGES),
  };
  originals.value = [
    item.image1 ?? null,
    item.image2 ?? null,
    item.image3 ?? null,
  ];
  fileInputModel.value = null;
  isEditing.value = false;
  dialog.value = true;
}

function closeDialog() {
  dialog.value = false;
  isEditing.value = false;
  fileInputModel.value = null;
}

/** ======== 判斷是否可再新增圖片 ======== */
const currentCount = computed(
  () => selectedReview.value.images.filter((v) => v !== null).length
);
const cannotAddMore = computed(() => currentCount.value >= MAX_IMAGES);

/** ======== 編輯/儲存 ======== */
async function toggleEdit() {
  if (isEditing.value) {
    await saveReview();
  } else {
    isEditing.value = true;
  }
}

/** ======== 刪除評論 ======== */
async function deleteReview() {
  if (!selectedReview.value?.reviewId) return;
  const id = selectedReview.value.reviewId;
  const confirmed = window.confirm(`確定要刪除評論 #${id} 嗎？`);
  if (!confirmed) return;

  try {
    deleting.value = true;
    await axios.delete(`${API_BASE}/api/reviews/del/${id}`, {
      withCredentials: true,
    });
    dialog.value = false;
    emit("deleted");
    notify("已刪除", "success");
  } catch (err) {
    console.error("刪除失敗:", err);
    notify("刪除失敗", "error");
  } finally {
    deleting.value = false;
  }
}

/** ======== 移除某一槽影像（標記為 null） ======== */
function removeImage(index: number) {
  const imgArr = [...selectedReview.value.images];
  imgArr[index] = null;
  selectedReview.value.images = imgArr; // 強制 reactivity
}

/** ======== 上傳影像（追加到空槽，最多 3 張） ======== */
function onFilesPicked() {
  const files = fileInputModel.value;
  uploadImage(files);
  // 清空 input，避免同檔案無法再次選取
  fileInputModel.value = null;
}

function uploadImage(files: File[] | File | null) {
  const list: File[] = Array.isArray(files) ? files : files ? [files] : [];
  if (list.length === 0) return;

  const next = [...selectedReview.value.images];

  for (const f of list) {
    // 基本驗證
    if (!ALLOWED_TYPES.includes(f.type)) {
      notify(`不支援的檔案類型：${f.type}`, "warning");
      continue;
    }
    if (f.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
      notify(`檔案過大（上限 ${MAX_FILE_SIZE_MB}MB）：${f.name}`, "warning");
      continue;
    }

    // 找空槽
    const emptyIdx = next.findIndex((v) => v === null);
    if (emptyIdx !== -1) {
      next[emptyIdx] = f;
    } else {
      notify("已達 3 張上限（將覆蓋第一張）", "info");
      next[0] = f; // 或者直接跳過
    }
  }

  selectedReview.value.images = next; // 觸發 reactivity
}

/** ======== 儲存（PATCH） ======== */
async function saveReview() {
  try {
    if (!selectedReview.value?.reviewId) {
      console.error("reviewId 不存在");
      return;
    }
    saving.value = true;

    const id = selectedReview.value.reviewId;
    const fd = new FormData();

    // 純文字欄位
    const reviewData = {
      cleanScore: selectedReview.value.cleanScore,
      commScore: selectedReview.value.commScore,
      valueScore: selectedReview.value.valueScore,
      cusComm: selectedReview.value.cusComm,
    };
    fd.append(
      "review",
      new Blob([JSON.stringify(reviewData)], { type: "application/json" })
    );

    // 新圖才 append；舊檔名與 null 都不傳
    const imgs = selectedReview.value.images ?? [null, null, null];
    if (imgs[0] instanceof File) fd.append("image1", imgs[0]);
    if (imgs[1] instanceof File) fd.append("image2", imgs[1]);
    if (imgs[2] instanceof File) fd.append("image3", imgs[2]);

    // 告知後端：哪些槽位被刪（原本有檔名，現在為 null）
    originals.value.forEach((old, i) => {
      const now = imgs[i];
      if (old && now === null) {
        fd.append("deleteSlot", String(i + 1)); // 後端可接收 List<Integer> deleteSlot
      }
    });

    const url = `${API_BASE}/api/reviews/update/${id}`;
    const res = await axios.patch(url, fd, {
      headers: {}, // axios 會自動帶 multipart/form-data 邊界
      withCredentials: true,
    });

    // 後端建議回傳 DTO（避免 Lazy Proxy）
    const updated: Review = {
      ...res.data,
      reviewId: selectedReview.value.reviewId, // 防止漏傳
    };

    selectedReview.value = {
      ...(updated as Review),
      images: [
        updated.image1 ?? null,
        updated.image2 ?? null,
        updated.image3 ?? null,
      ],
    };

    // 更新封面/畫面
    isEditing.value = false;
    dialog.value = false;
    emit("update", updated);
    notify("更新成功", "success");
  } catch (err: any) {
    console.error("更新失敗", err);
    console.log("Server response data:", err?.response?.data);
    notify("更新失敗", "error");
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
/* 與「我的訂單」一致的樣式 */
.soft-card {
  background: #fff7ed;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
}

.text-deep-orange {
  color: #c2410c;
}

.kv {
  font-size: 15px;
  color: #1f2937; /* 文字主色，讀性好 */
}
.value {
  font-weight: 700;
  color: #7c2d12;
}
.value.emphasis {
  color: #b45309;
}

.hoverable {
  transition: box-shadow 0.2s ease, transform 0.1s ease;
  cursor: pointer;
}
.hoverable:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 22px rgba(0, 0, 0, 0.06);
}

/* 內文用的連結樣式（沿用你的「value-link」語意） */
.value-link {
  font-weight: 700;
  padding: 0 6px;
  min-width: 0;
  text-transform: none;
}
.value-link :deep(.v-btn__content) {
  text-decoration: underline;
}

/* 縮圖按鈕覆蓋 */
.thumb-wrapper {
  position: relative;
  width: 100%;
}
.thumb {
  width: 100%;
  height: 100px;
}
.thumb-close {
  position: absolute;
  top: 4px;
  right: 4px;
}
</style>
