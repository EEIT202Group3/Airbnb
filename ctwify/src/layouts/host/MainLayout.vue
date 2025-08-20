<template>
  <NBar></NBar>
  <v-main style="margin-top: 45px;">
    <router-view></router-view>
  </v-main>
  <Footer></Footer>
</template>
<script setup>
import NBar from './NBar.vue';
import Footer from './Footer.vue';
</script>
<style scoped>
.v-btn:hover {
  background-color: #fef3e2 !important;
  color: #d35400 !important;
}
</style>
