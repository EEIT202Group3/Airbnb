<template>
  <v-data-table
    :headers="headers"
    :items="props.items"
    :items-per-page="10"
    class="elevation-1"
  >
    <template #item.actions="{ item }">
      <v-btn icon color="info" @click="viewReview(item)">
        <v-icon>mdi-eye</v-icon>
      </v-btn>
      <v-btn icon color="error" @click="confirmDelete(item)">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
      <v-btn icon color="success" @click="update(item)">
        <v-icon>mdi-pencil-circle</v-icon>
      </v-btn>
    </template>
  </v-data-table>

  <!-- 查看評論視窗 -->
  <v-dialog v-model="viewDialog" max-width="500">
    <v-card>
      <v-card-title>評論詳情</v-card-title>
      <v-card-text v-if="loading">
        <v-progress-circular indeterminate />
      </v-card-text>
      <v-card-text v-else-if="selectedReview">
        <div><strong>評論編號：</strong>{{ selectedReview.reviewId }}</div>
        <div><strong>房源編號：</strong>{{ selectedReview.listId }}</div>
        <div><strong>訂單編號：</strong>{{ selectedReview.bookingId }}</div>
        <div><strong>房客編號：</strong>{{ selectedReview.custId }}</div>
        <div><strong>房東編號：</strong>{{ selectedReview.hostId }}</div>
        <div><strong>評論日期：</strong>{{ selectedReview.reviewDate }}</div>
        <div><strong>乾淨評分：</strong>{{ selectedReview.cleanScore }}</div>
        <div><strong>溝通評分：</strong>{{ selectedReview.commScore }}</div>
        <div><strong>性價比：</strong>{{ selectedReview.valueScore }}</div>
        <div><strong>房客評論：</strong>{{ selectedReview.cusComm }}</div>
        <div><strong>房東評論：</strong>{{ selectedReview.hostComm }}</div>
        <div><strong>圖片1：</strong>
        <v-img
          :width="252"
          aspect-ratio="4/3"
          cover
           :src= "`http://localhost:8080/images/${selectedReview.image1}`"
        ></v-img>
        </div>
        <div><strong>圖片2：</strong>
        <v-img
          :width="252"
          aspect-ratio="4/3"
          cover
           :src= "`http://localhost:8080/images/${selectedReview.image2}`"
        ></v-img>
        </div>
        <div><strong>圖片3：</strong>
        <v-img
          :width="252"
          aspect-ratio="4/3"
          cover
           :src= "`http://localhost:8080/images/${selectedReview.image3}`"
        ></v-img>
        </div>
      </v-card-text>
      <v-card-text v-else> 找不到該筆資料。 </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn text @click="viewDialog = false">關閉</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 修改對話框 -->
  <v-dialog v-model="updateDialog" max-width="500">
    <v-card>
      <v-card-title>評論詳情</v-card-title>
      <v-card-text v-if="loading">
        <v-progress-circular indeterminate />
      </v-card-text>
      <v-card-text v-else-if="selectedReview">
        <div><strong>評論編號：</strong>{{ selectedReview.reviewId }}</div>
        <div><strong>房源編號：</strong>{{ selectedReview.listId }}</div>
        <div><strong>訂單編號：</strong>{{ selectedReview.bookingId }}</div>
        <div><strong>房客編號：</strong>{{ selectedReview.custId }}</div>
        <div><strong>房東編號：</strong>{{ selectedReview.hostId }}</div>
        <div><strong>評論日期：</strong>{{ selectedReview.reviewDate }}</div>
        <div>
          <!-- 乾淨評分 -->
          <strong>乾淨評分：</strong>{{ selectedReview.cleanScore }}
          <div>
            <v-rating
              v-model="selectedReview.cleanScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="乾淨度"
            ></v-rating>
          </div>
        </div>
        <div>
          <!-- 溝通評分 -->
          <strong>溝通評分：</strong>{{ selectedReview.commScore }}
          <div>
            <v-rating
              v-model="selectedReview.commScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="溝通"
            ></v-rating>
          </div>
        </div>

        <div>
          <!-- 性價比 -->
          <strong>性價比：</strong>{{ selectedReview.valueScore }}
          <div>
            <v-rating
              v-model="selectedReview.valueScore"
              length="5"
              size="32"
              color="yellow darken-3"
              background-color="grey lighten-1"
              label="性價比"
            ></v-rating>
          </div>
        </div>

        <!-- 房客評論 -->
        <v-textarea
          v-model="selectedReview.cusComm"
          label="房客評論"
          outlined
          rows="3"
          auto-grow
        ></v-textarea>
        <!-- 房東評論 -->
        <v-textarea
          v-model="selectedReview.hostComm"
          label="房東評論"
          outlined
          rows="3"
          auto-grow
        ></v-textarea>
      </v-card-text>
      <v-card-text v-else> 找不到該筆資料。 </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn text @click="updateConfirm">修改</v-btn>
        <v-btn text @click="updateDialog = false">關閉</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

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

// defineProps(["reviews"]);

// 顯示所有評論 表格
const props = defineProps({
  items: {
    type: Array,
    required: true,
  },
});

const headers = [
  { title: "評論編號", key: "reviewId", sortable: true },
  { title: "房源編號", key: "listId", sortable: true },
  { title: "訂單編號", key: "bookingId", sortable: true },
  { title: "房客編號", key: "custId", sortable: true },
  { title: "房東編號", key: "hostId", sortable: true },
  { title: "操作", key: "actions", sortable: false },
];

const selectedReview = ref(null);
const viewDialog = ref(false);
const loading = ref(false);

// 顯示單筆評論
const viewReview = async (item) => {
  const id = item.reviewId;
  console.log("正在讀取評論 ID:", id);
  
  
  loading.value = true;
  try {
    const resp = await axios.get(
      `http://localhost:8080/api/admins/reviews/get/${id}`
      , { withCredentials: true }
    );
    selectedReview.value = resp.data;
    viewDialog.value = true;
    
  } catch (err) {
    console.error("取得評論失敗:", err);
    selectedReview.value = null;
  } finally {
    loading.value = false;
  }
};

// 刪除功能
const del = defineEmits(["delete"]);
const deleteDialog = ref(false);

const confirmDelete = (item) => {
  selectedReview.value = item;
  deleteDialog.value = true;
};
const deleteReview = () => {
  // console.log(selectedReview.value.reviewId);
  del("delete", selectedReview.value.reviewId);
  deleteDialog.value = false;
  selectedReview.value = null;
};

// 修改評論
const updateDialog = ref(false);
const update = async (item) => {
  const id = item.reviewId;
  console.log("update", id);
  try {
    const resp = await axios.get(
      `http://localhost:8080/api/admins/reviews/get/${id}`,
      { withCredentials: true }
    );
    selectedReview.value = resp.data;
    updateDialog.value = true;
  } catch (err) {
    console.error("取得評論失敗:", err);
    selectedReview.value = null;
  }
};

/**
 * 按下對話視窗修改後 取得選取的資料
 * 圖片???
 */
const files = ref([]); // 圖片上傳
const updateConfirm = async (item) => {
  const u = selectedReview.value;
  const formData = new FormData();
  formData.append("reviewId", u.reviewId);
  formData.append("listId", u.listId);
  formData.append("bookingId", u.bookingId);
  formData.append("custId", u.custId);
  formData.append("hostId", u.hostId);
  formData.append("reviewDate", u.reviewDate);
  formData.append("cleanScore", u.cleanScore);
  formData.append("commScore", u.commScore);
  formData.append("valueScore", u.valueScore);
  formData.append("cusComm", u.cusComm);
  formData.append("hostComm", u.hostComm);
  console.log(u);
  try {
    const res = await axios.patch(
      `http://localhost:8080/api/admins/reviews/update/${u.reviewId}`,
      formData,
      { withCredentials: true }
    );
    alert("送出成功");

    // 送出後刷新頁面 -> 跳轉至個人的所有評論 ?
    //window.location.reload();
  } catch (err) {
    console.error(err);
    alert("送出失敗");
    // window.location.reload();
  }
};
</script>
