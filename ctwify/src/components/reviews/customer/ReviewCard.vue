<template>
  <v-col cols="12" class="d-flex justify-center">
    <v-card style="max-width: 1000px; width: 100%">
      <template #title>
        <img
          :src="
            review.image1
              ? `http://localhost:8080/images/${review.image1}`
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
          <div><strong>房東回覆：</strong>{{ selectedReview.hostComm }}</div>
          <v-row>
            <v-col cols="4" class="d-flex justify-center">
              <div>
                <strong>圖片1：</strong>
                <v-img
                  :width="200"
                  aspect-ratio="4/3"
                  cover
                  :src="`http://localhost:8080/images/${selectedReview.image1}`"
                ></v-img>
              </div>
            </v-col>

            <v-col cols="4" class="d-flex justify-center">
              <div>
                <strong>圖片2：</strong>
                <v-img
                  :width="200"
                  aspect-ratio="4/3"
                  cover
                  :src="`http://localhost:8080/images/${selectedReview.image2}`"
                ></v-img>
              </div>
            </v-col>

            <v-col cols="4" class="d-flex justify-center">
              <div>
                <strong>圖片3：</strong>
                <v-img
                  :width="200"
                  aspect-ratio="4/3"
                  cover
                  :src="`http://localhost:8080/images/${selectedReview.image3}`"
                ></v-img>
              </div>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-btn color="primary" text @click="toggleEdit">
            {{ isEditing ? "儲存" : "修改" }}
          </v-btn>
          <v-btn color="primary" text @click="dialog = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-col>
</template>

<script setup>
import { ref } from "vue";
defineProps(["review"]);
const defaultImg = "../src/icon/default.png";
const title = "海漾小木屋：一邊森林一邊太平洋(適合1-3人獨棟+廚房)";
const dialog = ref(false);
const selectedReview = ref({});

function showDialog(item) {
  selectedReview.value = item;
  console.log(selectedReview.value);
  dialog.value = true;
}
const isEditing = ref(false);
function toggleEdit() {
  if (isEditing.value) {
    // 儲存邏輯
    saveReview();
  }
  isEditing.value = !isEditing.value;
}

function saveReview() {
  // 這裡放呼叫 API 或其他儲存動作
  console.log("儲存評論：", selectedReview.value);
}
</script>

<style>
.text-wrap {
  white-space: normal;
  word-break: break-word;
}
</style>
