<template>
  <v-container class="py-6" max-width="1000">
    <div class="header-row">
      <v-icon size="30" class="mr-2" color="deep-orange-darken-1"
        >mdi-clipboard-text-clock</v-icon
      >
      <h2 class="page-title">我的評論</h2>
    </div>
    <v-row>
      <ReviewCard
        v-for="r in reviews"
        :index="r.reviewId"
        :review="r"
        justify="center"
        @update="updateReviewInList"
        @deleted="fetchReviews"
      ></ReviewCard>
    </v-row>
  </v-container>
</template>

<script setup>
import api from "@/api";
import ReviewCard from "@/components/reviews/customer/ReviewCard.vue";
import { ref, onMounted } from "vue";
const reviews = ref([]);
const list = ref({});

const fetchReviews = async () => {
  reviews.value = (await api.get("/api/reviews")).data;
  console.log(reviews.value);
};

onMounted(async () => {
  fetchReviews();
  console.log(reviews.value);
  // list.value = (await api.get("/listings/simple")).data;
  // console.log(list.value);
});

function updateReviewInList(updatedReview) {
  console.log(reviews.value.findIndex);
  console.log(reviews.value.reviewId);

  const i = reviews.value.findIndex(
    (r) => r.reviewId === updatedReview.reviewId
  );
  if (i !== -1) {
    reviews.value.splice(i, 1, updatedReview); // 建議用 splice 觸發畫面更新
  } else {
  }

  console.log("評論已更新！");
}
</script>

<style>
.header-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #c2410c;
  margin: 0;
}
</style>
