<template>
  <v-container class="py-8">
    <v-card class="mx-auto" max-width="760" elevation="3" rounded="xl">
      <!-- 頂部房源圖 + 標題區 -->
      <v-img :src="photoUrl" height="240" cover class="rounded-t-xl">
        <div class="hero-overlay">
          <div class="text-white text-h6 font-medium truncate">
            {{ houseName || "此房源" }}
          </div>
          <div class="text-white text-caption opacity-80">
            訂單：{{ bookingId || "-" }}
          </div>
        </div>
      </v-img>

      <v-card-text class="px-6 pb-6 pt-4">
        <v-form v-model="valid">
          <!-- 評分區 -->
          <v-card variant="tonal" class="mb-4" rounded="lg">
            <v-card-text class="pb-1">
              <div class="text-subtitle-1 font-medium mb-3">
                請給這次住宿評分
              </div>
              <v-row dense>
                <v-col cols="12" sm="4">
                  <div class="text-body-2 mb-1">乾淨度</div>
                  <v-rating
                    v-model="cleanScore"
                    :length="5"
                    :size="28"
                    hover
                    active-color="yellow-darken-1"
                  />
                </v-col>
                <v-col cols="12" sm="4">
                  <div class="text-body-2 mb-1">溝通</div>
                  <v-rating
                    v-model="commScore"
                    :length="5"
                    :size="28"
                    hover
                    active-color="yellow-darken-1"
                  />
                </v-col>
                <v-col cols="12" sm="4">
                  <div class="text-body-2 mb-1">性價比</div>
                  <v-rating
                    v-model="valueScore"
                    :length="5"
                    :size="28"
                    hover
                    active-color="yellow-darken-1"
                  />
                </v-col>
              </v-row>
              <div class="text-caption text-medium-emphasis mt-2">
                小提醒：星等越高代表體驗越好。
              </div>
            </v-card-text>
          </v-card>

          <!-- 文字評論 -->
          <v-textarea
            v-model="custComm"
            label="文字評論"
            variant="outlined"
            rows="3"
            auto-grow
            clearable
            :counter="200"
            maxlength="200"
            class="mb-4"
            :rules="[
              (v) => !!v || '請輸入評論',
              (v) => (v?.trim().length ?? 0) >= 10 || '至少 10 個字',
            ]"
          />

          <!-- 圖片上傳 -->
          <v-card variant="outlined" rounded="lg" class="mb-4">
            <v-card-text>
              <v-file-input
                v-model="files"
                multiple
                show-size
                accept="image/*"
                label="上傳評論圖片（最多 3 張）"
                counter
                :counter-size="3"
                prepend-icon="mdi-camera"
                :rules="[(f) => !f || f.length <= 3 || '最多 3 張']"
              />

              <v-row v-if="previews.length" class="mt-2" dense>
                <v-col
                  v-for="(p, i) in previews"
                  :key="p.url"
                  cols="4"
                  class="d-flex"
                >
                  <v-hover v-slot="{ isHovering, props }">
                    <div class="w-100 relative" v-bind="props">
                      <v-img
                        :src="p.url"
                        aspect-ratio="1"
                        cover
                        class="rounded-lg"
                      />
                      <v-btn
                        size="small"
                        icon="mdi-close"
                        variant="flat"
                        class="thumb-close"
                        @click="removeFile(i)"
                        v-show="isHovering"
                      />
                    </div>
                  </v-hover>
                </v-col>
              </v-row>
              <div class="text-caption text-medium-emphasis mt-2">
                支援 jpg / png / webp；單檔請控制大小適中，加快上傳速度。
              </div>
            </v-card-text>
          </v-card>

          <!-- 送出 -->
          <div class="text-center">
            <v-btn
              color="#e7630b"
              class="text-white"
              :loading="submitting"
              :disabled="!valid || !ready || submitting"
              @click="submit"
            >
              送出
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, computed, watch } from "vue";
import axios from "axios";
import { useRoute } from "vue-router";

// ===== 路由 & 狀態 =====
const route = useRoute();
const bookingId = String(route.query.bookingId || ""); // 不是 ref，直接字串即可

// 這些僅存在前端記憶體，不顯示在畫面
const listId = ref(null);
const custId = ref("");
const hostId = ref("");

// 顯示用資訊
const houseName = ref("");
const photo1 = ref("");
const IMG_BASE = "http://localhost:8080/images/listings"; // 若後端實際是 /images/reviews/**，請改這裡
const photoUrl = computed(() =>
  photo1.value ? `${IMG_BASE}/${photo1.value}` : ""
);

// 表單欄位
const valid = ref(false);
const submitting = ref(false);
const cleanScore = ref(1);
const commScore = ref(1);
const valueScore = ref(1);
const custComm = ref("");

// 上傳檔案 & 預覽（含 revoke 機制避免記憶體洩漏）
const files = ref([]);
const previews = ref([]); // [{ url, revoke }]

function refreshPreviews() {
  // 先釋放舊的 URL
  previews.value.forEach((p) => p.revoke && p.revoke());
  previews.value = [];
  (files.value || []).slice(0, 3).forEach((file) => {
    const url = URL.createObjectURL(file);
    const revoke = () => URL.revokeObjectURL(url);
    previews.value.push({ url, revoke });
  });
}

watch(files, refreshPreviews);
onBeforeUnmount(() => {
  previews.value.forEach((p) => p.revoke && p.revoke());
});

function removeFile(index) {
  const arr = [...(files.value || [])];
  arr.splice(index, 1);
  files.value = arr;
  refreshPreviews();
}

// 必要資料是否齊全
const ready = computed(() =>
  Boolean(
    listId.value &&
      custId.value &&
      hostId.value &&
      bookingId &&
      cleanScore.value &&
      commScore.value &&
      valueScore.value &&
      custComm.value?.trim().length >= 10
  )
);

// ===== 初始化載入 Insert 所需資料 =====
onMounted(async () => {
  try {
    const { data } = await axios.get(
      `http://localhost:8080/api/reviews/insertData/${bookingId}`,
      { withCredentials: true }
    );
    // 後端回傳鍵名請維持小駝峰
    listId.value = data.listId;
    custId.value = data.customerId;
    // hostId.value = data.hostId;
    hostId.value = ""; // 測試用，之後刪除
    photo1.value = data.listImg;
    houseName.value = data.houseName || "";
  } catch (e) {
    console.error("載入 insert 資料失敗", e);
    alert("無法載入評論資料，請返回重試");
  }
});

// ===== 送出 =====
async function submit() {
  if (!valid.value || !ready.value || submitting.value) return;
  submitting.value = true;

  try {
    const selected = (files.value || []).slice(0, 3);
    const formData = new FormData();
    formData.append("listId", listId.value ?? "");
    formData.append("bookingId", bookingId);
    formData.append("custId", custId.value ?? "");
    formData.append("hostId", hostId.value ?? "");
    formData.append("cleanScore", String(cleanScore.value));
    formData.append("commScore", String(commScore.value));
    formData.append("valueScore", String(valueScore.value));
    formData.append("custComm", custComm.value);
    selected.forEach((f) => formData.append("images", f)); // 多檔同名欄位

    await axios.post("http://localhost:8080/api/reviews/insert", formData, {
      withCredentials: true,
    });
    alert("送出成功！感謝您的評論 🙏");
    // 這裡可導回列表或清空表單
    // router.push('/reviews/mine')
  } catch (e) {
    console.error(e);
    alert("送出失敗，請稍後再試");
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.hero-overlay {
  position: absolute;
  inset: auto 0 0 0;
  padding: 16px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.65), rgba(0, 0, 0, 0));
}
.thumb-close {
  position: absolute;
  top: 6px;
  right: 6px;
  background: rgba(0, 0, 0, 0.55);
  color: white;
}
.v-rating {
  justify-content: center;
}
</style>
