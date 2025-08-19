<template>
  <Nav></Nav>
  <v-main style="margin-top: 45px;">
    <router-view></router-view>
  </v-main>
  <Footer app fixed></Footer>
</template>
<script setup>

import Nav from "@/layouts/Navbar.vue";
//import Nav from "@/layouts/Nbar.vue";

import Footer from "@/layouts/Footer.vue";
</script>
<style scoped>

</style>
