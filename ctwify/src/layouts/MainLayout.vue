<template>
  <Nav></Nav>
  <v-main>
    <router-view></router-view>
  </v-main>
  <Footer></Footer>
</template>
<script setup>
import Nav from "@/layouts/Navbar.vue";
import Footer from "@/layouts/Footer.vue";
</script>
<style scoped>
.v-btn:hover {
  background-color: #fef3e2 !important;
  color: #d35400 !important;
}
</style>
