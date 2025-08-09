<template>
  <v-row>
    <ReviewCard
      v-for="r in reviews"
      :index="r.id"
      :review="r"
      justify="center"
    ></ReviewCard>
  </v-row>
</template>

<script setup>
import api from "@/api";
import ReviewCard from "@/components/reviews/customer/ReviewCard.vue";
import { ref, onMounted } from "vue";
const reviews = ref({});
const list = ref({});

onMounted(async () => {
  reviews.value = (await api.get("/api/reviews")).data;
  console.log(reviews.value);
  list.value = (await api.get("/listings/simple")).data;
  console.log(list.value);
});
</script>

<style></style>
