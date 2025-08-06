<template>
  <v-card class="pa-4" max-width="600">
    <v-row dense align="center">
      <!-- 下拉式選單 -->
      <v-col cols="4">
        <v-select
          v-model="searchType"
          :items="[
            { title: '房東編號', value: 'hostId' },
            { title: '房客編號', value: 'custId' },
            { title: '房源編號', value: 'listId' },
          ]"
          label="搜尋類型"
          variant="outlined"
          dense
        ></v-select>
      </v-col>

      <!-- 關鍵字輸入欄 -->
      <v-col cols="5">
        <v-text-field
          v-model="keyword"
          :label="`搜尋`"
          @keyup:enter="searchReviews"
          variant="outlined"
          dense
        ></v-text-field>
      </v-col>

      <!-- 搜尋按鈕 -->
      <v-col cols="3">
        <v-btn color="primary" block @click="searchReviews"> 搜尋 </v-btn>
      </v-col>
    </v-row>
  </v-card>

  <v-row>
    <v-card class="ma-4 pa-4">
      <v-card-title>所有評論列表</v-card-title>
      <ReviewTable :items="reviews" @delete="handleDelete"></ReviewTable>
    </v-card>
  </v-row>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import ReviewTable from "@/components/reviews/components/ReviewTable.vue";

const searchType = ref("");
const keyword = ref("");

// 取得 reviews 數據
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

// 掛載時自動 取得數據
onMounted(() => {
  fetchReviews();
});

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
const handleDelete = async (reviewId) => {
  console.log(reviewId);

  try {
    await axios.delete(`http://localhost:8080/api/admins/reviews/del/${reviewId}`, {
      withCredentials: true,
    });
    await fetchReviews(); // 刪除成功後局部刷新 table
  } catch (err) {
    console.error("刪除失敗:", err);
  }
};
</script>
