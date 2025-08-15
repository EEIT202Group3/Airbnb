<template>
  <NBar></NBar>
  <v-main>
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
