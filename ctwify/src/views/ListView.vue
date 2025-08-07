<template>
  <Detail></Detail>
  <!-- <ReviewTable></ReviewTable> -->
  
</template>

<script setup>
import Detail from "@/components/listing/Detail.vue";
import ReviewTable from "@/components/reviews/ReviewTable.vue";
import { useRoute } from 'vue-router'

const route = useRoute()
const id = route.params.id
console.log(id);

</script>

<style></style>
