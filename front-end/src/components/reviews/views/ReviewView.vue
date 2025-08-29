<template>
  <v-row>
    <v-col cols="2" style="padding-right: 0px; margin-right: 0px">
      <v-select
        v-model="searchType"
        label="搜尋條件"
        :items="[
          { title: '房東編號', value: 'hostEmail' },
          { title: '房客編號', value: 'customerEmail' },
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
  <!-- 檢舉提示 icon：顯示在 customerEmail 欄位 -->
  <template #item.reviewId="{ item }">
    <span>{{ item.reviewId }}</span>
    <v-icon
      v-if="item.report === 1"
      color="error"
      size="18"
      style="margin-left: 6px"
      title="此評論已被檢舉"
    >
      mdi-alert-circle-outline
    </v-icon>
  </template>

  <!-- 操作欄 -->
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
        <div><strong>房客編號：</strong>{{ selectedReview.customerEmail }}</div>
        <div><strong>房東編號：</strong>{{ selectedReview.hostEmail }}</div>
        <div><strong>評論日期：</strong>{{ selectedReview.reviewDate }}</div>
        <div><strong>乾淨評分：</strong>{{ selectedReview.cleanScore }}</div>
        <div><strong>溝通評分：</strong>{{ selectedReview.commScore }}</div>
        <div><strong>性價比：</strong>{{ selectedReview.valueScore }}</div>
        <div><strong>房客：</strong>{{ selectedReview.cusComm }}</div>
        <div><strong>房東：</strong>{{ selectedReview.hostComm }}</div>
        <div>
          <strong>圖片1：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/reviews/${selectedReview.image1}`"
          ></v-img>
        </div>
        <div>
          <strong>圖片2：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/reviews/${selectedReview.image2}`"
          ></v-img>
        </div>
        <div>
          <strong>圖片3：</strong>
          <v-img
            :width="252"
            aspect-ratio="4/3"
            cover
            :src="`http://localhost:8080/images/reviews/${selectedReview.image3}`"
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
  { title: "房東", align: "start", key: "hostEmail" },
  { title: "房客", align: "start", key: "customerEmail" },
  { title: "操作", align: "start", key: "actions", sortable: false },
];

import axios from "axios";
import { fetchReviews, getReviews } from "@/service/review/AdminService";
const reviews = ref([]);
// 封裝取得數據方法
/*
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
*/
onMounted(async () => {
  reviews.value = await fetchReviews(keyword.value, searchType.value);
  console.log(reviews.value, "reviews fetched on mounted");
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
    selectedReview.value = await getReviews(id);
    viewDialog.value = true;
    console.log(selectedReview.value);
  } catch (err) {
    console.error("取得評論失敗:", err);
    selectedReview.value = null;
  } finally {
    loading.value = false;
  }
};

import { ref, computed, onMounted } from "vue";
import router from "@/router";
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
  if (searchType.value === "listId" && isNaN(parseInt(keyword.value))) {
    alert("請輸入有效的房源ID（整數）");
    return;
  }
  if (!keyword.value) return; // ✅ 只呼叫這個
  reviews.value = await fetchReviews(keyword.value, searchType.value);
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
    const res = await fetchReviews(keyword.value, searchType.value);
    reviews.value = res; // ✅ 更新 reviews
    // 刪除成功後局部刷新 table
  } catch (err) {
    console.error("刪除失敗:", err);
  }
};
</script>

<style></style>
