<template>
  <v-col cols="12" class="d-flex justify-center">
    <v-card class="w-100" max-width="1000">
      <!-- Header / Title -->
      <v-card-title class="d-flex align-center gap-4">
        <v-img :src="coverSrc" height="100" width="100" class="rounded" cover />
        <div class="text-subtitle-1 font-weight-medium">{{ title }}</div>
      </v-card-title>

      <!-- Summary -->
      <v-card-text class="text-wrap">
        {{ review?.cusComm || "（尚無評論內容）" }}
      </v-card-text>

      <!-- Actions -->
      <v-card-actions>
        <v-btn
          @click="showDialog(review)"
          variant="elevated"
          prepend-icon="mdi-eye"
        >
          顯示評論
        </v-btn>
      </v-card-actions>
    </v-card>

    <!-- Dialog -->
    <v-dialog v-model="dialog" max-width="720">
      <v-card>
        <v-card-title class="d-flex align-center">
          <span>評論詳情</span>
          <v-spacer />
          <v-btn icon :disabled="saving || deleting" @click="dialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <!-- Meta -->
          <v-row dense class="mb-2">
            <v-col cols="12" sm="6"
              ><strong>評論編號：</strong>{{ selectedReview.reviewId }}</v-col
            >
            <v-col cols="12" sm="6"
              ><strong>房源編號：</strong>{{ selectedReview.listId }}</v-col
            >
            <v-col cols="12" sm="6"
              ><strong>訂單編號：</strong>{{ selectedReview.bookingId }}</v-col
            >
            <v-col cols="12" sm="6"
              ><strong>房客編號：</strong
              >{{ maskEmail(selectedReview.customerEmail) }}</v-col
            >
            <v-col cols="12" sm="6"
              ><strong>房東編號：</strong
              >{{ maskEmail(selectedReview.hostEmail) }}</v-col
            >
            <v-col cols="12" sm="6"
              ><strong>評論日期：</strong>{{ selectedReview.reviewDate }}</v-col
            >
          </v-row>

          <!-- Ratings -->
          <div class="mb-2">
            <strong>乾淨評分：</strong>
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
            <strong>溝通評分：</strong>
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
            <strong>性價比：</strong>
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
            <strong>房東回覆：</strong>
            <div class="mt-1">
              {{ selectedReview.hostComm || "（無房東回覆）" }}
            </div>
          </div>

          <div class="mb-4">
            <strong>房客評論：</strong>
            <div v-if="!isEditing" class="mt-1">
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
                  v-if="isEditing"
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
              label="新增圖片（最多 3 張）"
              accept="image/*"
              prepend-icon="mdi-plus"
              show-size
              multiple
              hide-details
              variant="outlined"
              density="comfortable"
              style="max-width: 320px"
              :disabled="saving"
              @update:model-value="uploadImage"
            />
          </div>
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn
            color="primary"
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
            variant="text"
            :disabled="saving || deleting"
            @click="dialog = false"
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
const API_BASE = "http://localhost:8080";
const MAX_IMAGES = 3;
const isFile = (v: unknown): v is File =>
  typeof File !== "undefined" && v instanceof File;

/** ======== Props / Emits ======== */
type Review = {
  reviewId: number;
  listId: number;
  bookingId: number;
  customerEmail: number;
  hostEmail: number;
  reviewDate: string;
  cleanScore: number;
  commScore: number;
  valueScore: number;
  cusComm: string;
  hostComm: string;
  // 後端原始三個欄位（檔名或 null）
  image1?: string | null;
  image2?: string | null;
  image3?: string | null;
};

const props = defineProps<{
  review: Review;
  title?: string;
}>();

const emit = defineEmits<{
  (e: "update", payload: Review): void;
  (e: "deleted"): void;
}>();

/** ======== 靜態資源 ======== */
// 若你用 Vite，建議用 URL 方式確保路徑正確
const defaultImg = "../icon/default.png"; // 替換為實際的預設圖片路徑

/** ======== UI 狀態 ======== */
const dialog = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const deleting = ref(false);

/** ======== 選中評論與影像槽 ======== */
type MixedImage = File | string | null;

// 進入對話框時的原始檔名快照（用來判斷哪些槽被刪）
const originals = ref<(string | null)[]>([null, null, null]);

// 對話框中可編輯的評論物件；images 固定三槽
const selectedReview = ref<Review & { images: MixedImage[] }>({
  ...(props.review as Review),
  images: [
    props.review?.image1 ?? null,
    props.review?.image2 ?? null,
    props.review?.image3 ?? null,
  ],
});

/** ======== 封面圖（卡片標題用） ======== */
const coverSrc = computed(() => {
  const src = props.review?.image1
    ? `${API_BASE}/images/listing/${props.review.image1}`
    : defaultImg;
  return src;
});

/** ======== 標題 ======== */
const title = computed(
  () => props.title || "海漾小木屋：一邊森林一邊太平洋(適合1-3人獨棟+廚房)"
);

/** ======== 預覽與資源釋放 ======== */
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
  return `${API_BASE}/images/listing/${img}`;
}
/*
onBeforeUnmount(() => {
  for (const url of objectUrlCache.values()) URL.revokeObjectURL(url);
  objectUrlCache.clear();
});
*/
/** ======== Dialog 開關 ======== */
function showDialog(item: Review) {
  // 深拷貝 + 初始化三槽（保持固定長度）
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
  isEditing.value = false;
  dialog.value = true;
}

/** ======== 編輯/儲存 ======== */
async function toggleEdit() {
  if (isEditing.value) {
    await saveReview();
  } else {
    isEditing.value = true;
  }
}

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
  } catch (err) {
    console.error("刪除失敗:", err);
  } finally {
    deleting.value = false;
  }
}

/** ======== 移除某一槽影像 ======== */
function removeImage(index: number) {
  if (!Array.isArray(selectedReview.value.images)) return;
  // 標註為空槽（null）
  selectedReview.value.images[index] = null;
}

/** ======== 上傳影像（追加到空槽，最多 3 張） ======== */
function uploadImage(files: File[] | File | null) {
  const list: File[] = Array.isArray(files) ? files : files ? [files] : [];
  if (!Array.isArray(selectedReview.value.images)) {
    selectedReview.value.images = new Array(MAX_IMAGES).fill(null);
  }
  for (const f of list) {
    const emptyIdx = selectedReview.value.images.findIndex((v) => v === null);
    if (emptyIdx !== -1) {
      selectedReview.value.images[emptyIdx] = f;
    } else {
      // 已滿則覆蓋第 0 槽（可改成提示）
      selectedReview.value.images[0] = f;
    }
  }
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

    // 送純文字欄位
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

    // 2) 圖片欄位（重點）
    //    只要該槽是 File 才 append；若是 null 或 舊檔名（string），一律不 append
    const imgs = selectedReview.value.images ?? [null, null, null];
    if (imgs[0] instanceof File) fd.append("image1", imgs[0]);
    if (imgs[1] instanceof File) fd.append("image2", imgs[1]);
    if (imgs[2] instanceof File) fd.append("image3", imgs[2]);
    // ⛔ 不要 append 任何空 Blob 或 deleteSlots

    // 若後端是接收多值同 key，可重複 append；若是 JSON，改成一次 append JSON 字串即可。
    // 這裡用多值同 key：
    const url = `${API_BASE}/api/reviews/update/${id}`;
    const res = await axios.patch(url, fd, {
      headers: {}, // axios 會自動帶 multipart/form-data 邊界
      withCredentials: true,
    });

    // 後端回傳最新資料後，更新本地並關閉
    const updated: Review = {
      ...res.data,
      reviewId: selectedReview.value.reviewId, // ✅ 補上 ID
    };
    selectedReview.value = {
      ...(updated as Review),
      images: [
        updated.image1 ?? null,
        updated.image2 ?? null,
        updated.image3 ?? null,
      ],
    };
    isEditing.value = false;
    dialog.value = false;
    emit("update", updated);
    console.log("後端回傳 res.data：", res.data);
    console.log("更新後的評論：", selectedReview.value);
    console.log("更新後的評論 ID：", updated.reviewId);
    console.log("即將 emit 的 review：", updated);
  } catch (err: any) {
    console.error("更新失敗", err);
    console.log("Server response data:", err?.response?.data);
  } finally {
    saving.value = false;
  }
}
// 隱藏email
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

<style scoped>
.text-wrap {
  white-space: normal;
  word-break: break-word;
}

/* 縮圖樣式 */
.thumb-wrapper {
  position: relative;
  width: 150px;
  aspect-ratio: 1 / 1;
}
.thumb {
  width: 100%;
  height: 100%;
}
.thumb-close {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #fff;
  opacity: 0.9;
}
</style>
