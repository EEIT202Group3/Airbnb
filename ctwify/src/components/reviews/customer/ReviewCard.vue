<template>
  <v-col cols="12" class="d-flex justify-center">
    <v-card style="max-width: 1000px; width: 100%">
      <template #title>
        <img
          :src="
            review.image1
              ? `http://localhost:8080/images/listing/${review.image1}`
              : defaultImg
          "
          height="100px"
        />
        {{ title }}
      </template>
      <template #text class="text-wrap"> {{ review.cusComm }}</template>
      <v-card-actions>
        <v-btn @click="showDialog(review)">
          <v-icon>mdi-eye</v-icon>
          顯示評論
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center"
          >評論詳情
          <v-btn icon @click="dialog = false" style="margin-left: auto">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <div><strong>評論編號：</strong>{{ selectedReview.reviewId }}</div>
          <div><strong>房源編號：</strong>{{ selectedReview.listId }}</div>
          <div><strong>訂單編號：</strong>{{ selectedReview.bookingId }}</div>
          <div><strong>房客編號：</strong>{{ selectedReview.custId }}</div>
          <div><strong>房東編號：</strong>{{ selectedReview.hostId }}</div>
          <div><strong>評論日期：</strong>{{ selectedReview.reviewDate }}</div>
          <!-- 其他不可編輯欄位... -->
          <div>
            <strong>乾淨評分：</strong>
            <v-rating
              v-model="selectedReview.cleanScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="乾淨度"
              :readonly="!isEditing"
            ></v-rating>
          </div>
          <div>
            <strong>溝通評分：</strong>
            <v-rating
              v-model="selectedReview.commScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="溝通"
              :readonly="!isEditing"
            ></v-rating>
          </div>

          <div>
            <strong>性價比：</strong>
            <v-rating
              v-model="selectedReview.valueScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="性價比"
              :readonly="!isEditing"
            ></v-rating>
          </div>

          <div><strong>房東回覆：</strong>{{ selectedReview.hostComm }}</div>
          <div>
            <strong>房客評論：</strong>
            <div v-if="!isEditing">{{ selectedReview.cusComm }}</div>
            <v-textarea
              v-else
              v-model="selectedReview.cusComm"
              label="編輯房客評論"
              rows="3"
              outlined
              auto-grow
            ></v-textarea>
          </div>
          <v-row class="mt-4">
            <v-row class="mt-4">
              <v-col
                v-for="(img, index) in selectedReview.images"
                :key="index"
                cols="4"
                class="d-flex justify-center"
              >
                <div
                  style="position: relative; width: 150px; aspect-ratio: 1 / 1"
                >
                  <v-img
                    :src="previewUrl(img)"
                    cover
                    class="rounded"
                    style="width: 100%; height: 100%"
                  />
                  <div
                    v-if="isEditing"
                    @click="removeImage(index)"
                    style="
                      position: absolute;
                      top: 6px;
                      right: 6px;
                      width: 24px;
                      height: 24px;
                      background: #fff;
                      border-radius: 50%;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      cursor: pointer;
                    "
                  >
                    <v-icon size="18" color="grey" style="opacity: 0.8"
                      >mdi-close</v-icon
                    >
                  </div>
                </div>
              </v-col>
            </v-row>

            <!-- 新增圖片按鈕 -->
            <v-col
              cols="12"
              class="d-flex justify-center mt-4"
              v-if="isEditing"
            >
              <v-file-input
                label="新增圖片"
                accept="image/*"
                prepend-icon="mdi-plus"
                show-size
                multiple
                @change="uploadImage"
                hide-details
                outlined
                dense
                style="max-width: 300px"
              ></v-file-input>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-btn color="primary" text @click="toggleEdit">
            {{ isEditing ? "儲存" : "修改" }}
          </v-btn>
          <v-btn color="primary" text @click="deleteReview">刪除</v-btn>
          <v-btn color="primary" text @click="dialog = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-col>

  <!-- 刪除確認視窗 -->
  <v-dialog v-model="deleteDialog" max-width="400">
    <v-card>
      <v-card-title class="text-h6">確認刪除</v-card-title>
      <v-card-text>
        確定要刪除評論編號為
        <strong>{{ selectedReview?.reviewId }}</strong> 的紀錄嗎？
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="deleteDialog = false">取消</v-btn>
        <v-btn color="error" text @click="deleteReview">確認刪除</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

defineProps(["review"]);
const emit = defineEmits(["updated"]);
const defaultImg = "../src/icon/default.png";
const title = "海漾小木屋：一邊森林一邊太平洋(適合1-3人獨棟+廚房)";

const dialog = ref(false);
const isEditing = ref(false);
// 目前選中的評論（含臨時上傳檔）
const selectedReview = ref({
  images: [], // 這是本次欲上傳的新檔案清單（File[]）
});

function showDialog(item) {
  // 複製一份，避免直接改到父層資料
  selectedReview.value = {
    ...item,
    images: [item.image1 || null, item.image2 || null, item.image3 || null],
  };
  originals.value = [
    item.image1 || null,
    item.image2 || null,
    item.image3 || null,
  ];
  dialog.value = true;
}

function toggleEdit() {
  if (isEditing.value) {
    // 按下「儲存」時送出
    saveReview();
  }
  isEditing.value = !isEditing.value;
}

// 刪除功能
const deleteDialog = ref(false);

async function deleteReview() {
  const id = selectedReview.value.reviewId;
  console.log("刪除的評論 ID:", id);
  const confirmed = window.confirm(`確定要刪除評論 #${id} 嗎？`);
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8080/api/reviews/del/${id}`, {
      withCredentials: true,
    });

    emit("updated", { reviewId: id, deleted: true }); // 通知父組件
  } catch (err) {
    console.error("刪除失敗:", err);
  }
  dialog.value = false;

  deleteDialog.value = false;
  selectedReview.value = null;
}

function removeImage(index) {
  if (!Array.isArray(selectedReview.value.images)) return;
  selectedReview.value.images[index] = null; // 標記刪除此槽
}

// 接收 <v-file-input> 選的檔案（多檔）
function uploadImage(files) {
  const list = Array.isArray(files)
    ? files
    : Array.from(files?.target?.files || []);
  if (!Array.isArray(selectedReview.value.images)) {
    selectedReview.value.images = [null, null, null];
  }
  for (const f of list) {
    const emptyIdx = selectedReview.value.images.findIndex((v) => v === null);
    if (emptyIdx !== -1) {
      selectedReview.value.images[emptyIdx] = f;
    } else {
      // 三槽都滿→覆蓋第0槽（你也可改成彈窗提示）
      selectedReview.value.images[0] = f;
    }
  }
}

const originals = ref([null, null, null]); // 記住進入對話框時的原始檔名

function previewUrl(img) {
  if (!img) return defaultImg; // 無圖→顯示佔位
  return img instanceof File
    ? URL.createObjectURL(img) // 新上傳檔→blob URL
    : `http://localhost:8080/images/listing/${img}`; // 舊檔名→後端 URL
}

async function saveReview() {
  try {
    const id = selectedReview.value.reviewId;
    if (!id) {
      console.error("reviewId 不存在");
      return;
    }

    const fd = new FormData();

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

    // 決定圖片處理策略
    // 計算要刪除的槽（原本有圖，現在是 null）
    const deleteSlots = [];
    for (let i = 0; i < 3; i++) {
      const img = selectedReview.value.images?.[i];

      if (img instanceof File) {
        fd.append("images", img);
      } else {
        // ✅ 核心修正：傳 fake 空檔案＋合法檔名
        fd.append(
          "images",
          new Blob([], { type: "application/octet-stream" }),
          `empty${i + 1}.jpg`
        );
      }
    }

    // 把要刪的槽傳給後端
    deleteSlots.forEach((s) => fd.append("deleteSlots", String(s)));

    const url = `http://localhost:8080/api/reviews/update/${id}`;
    const res = await axios.patch(url, fd, {
      headers: {},
      withCredentials: true,
    });

    selectedReview.value = { ...selectedReview.value, ...res.data, images: [] };
    isEditing.value = false;
    dialog.value = false;
    emit("updated", res.data);
  } catch (err) {
    console.error("更新失敗", err);
    console.log("Server response data:", err?.response?.data);
  }
}
</script>

<style>
.text-wrap {
  white-space: normal;
  word-break: break-word;
}
</style>
