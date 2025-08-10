<template>
  <v-row>
    <v-col cols="2" style="padding-right: 0px; margin-right: 0px">
      <v-select
        v-model="searchType"
        label="搜尋條件"
        :items="[
          { title: '房東編號', value: 'hostId' },
          { title: '房客編號', value: 'custId' },
          { title: '房源編號', value: 'listId' },
        ]"
        item-title="title"
        item-value="value"
        variant="solo"
      ></v-select>
    </v-col>
    <v-col cols="8" style="padding-left: 0px; margin-left: 0px">
      <v-text-field
        v-model="keyword"
        label="輸入"
        variant="solo"
        required
        @keyup:enter="searchReviews"
      ></v-text-field>
    </v-col>
    <v-col cols="2">
      <v-icon
        icon="mdi-magnify"
        @click="searchReviews()"
        style="margin-top: 18px"
      ></v-icon>
    </v-col>
  </v-row>

  <v-data-table
    :headers="headers"
    :items="reviews"
    :items-per-page="10"
    class="elevation-1"
    style="font-size: 22px"
  >
    <template #item.actions="{ item }">
      <v-btn icon color="info" @click="viewReview(item)">
        <v-icon>mdi-eye</v-icon>
      </v-btn>
      <v-btn icon color="error" @click="handleDelete(item)">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </template>
  </v-data-table>
  <!-- 查看評論視窗 -->
  <v-dialog v-model="viewDialog" max-width="500" style="font-size: 22px">
    <v-card>
      <v-card-title><strong>評論詳情</strong></v-card-title>
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
        <div>
          <strong>圖片1：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/${selectedReview.image1}`"
          ></v-img>
        </div>
        <div>
          <strong>圖片2：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/${selectedReview.image2}`"
          ></v-img>
        </div>
        <div>
          <strong>圖片3：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/${selectedReview.image3}`"
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
</template>

<script setup lang="ts">
const headers = [
  { title: "評論編號", align: "start", key: "reviewId" },
  { title: "房源編號", align: "start", key: "listId" },
  { title: "訂單編號", align: "start", key: "bookingId" },
  { title: "房東編號", align: "start", key: "hostId" },
  { title: "房客編號", align: "start", key: "custId" },
  { title: "操作", align: "start", key: "actions", sortable: false },
];

import axios from "axios";
const reviews = ref([]);
// 封裝取得數據方法
const fetchReviews = async (keyword = "", type = "") => {
  const res = await axios.get("http://localhost:8080/api/admins/reviews", {
    params: {
      keyword,
      type,
    },
    withCredentials: true,
  });
  reviews.value = res.data;
};

onMounted(() => {
  fetchReviews();
  console.log(reviews);
});

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
      `http://localhost:8080/api/admins/reviews/get/${id}`,
      { withCredentials: true }
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

import { ref, computed, onMounted } from "vue";
// import ReviewTable from "@/components/reviews/components/ReviewTable.vue";

const searchType = ref("");
const keyword = ref("");

/**
 * 下拉式選單：選擇查詢模式
 * 模糊查詢 ?
 * 前端判斷 查詢模式 呼叫後端 controller
 * 查詢欄輸入關鍵字
 */

const searchReviews = async () => {
  if (!keyword.value) return;
  fetchReviews(keyword.value.trim(), searchType.value.trim()); // ✅ 只呼叫這個
  console.log(keyword.value, searchType.value);
};

// 傳給子元件的刪除處理

const handleDelete = async (item) => {
  const id = item.reviewId;

  const confirmed = window.confirm(`確定要刪除評論 #${id} 嗎？`);

  if (!confirmed) return;
  try {
    await axios.delete(`http://localhost:8080/api/admins/reviews/del/${id}`, {
      withCredentials: true,
    });
    await fetchReviews(); // 刪除成功後局部刷新 table
  } catch (err) {
    console.error("刪除失敗:", err);
  }
};
</script>

<style></style>
