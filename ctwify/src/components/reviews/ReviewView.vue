<template>
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

<style></style>
