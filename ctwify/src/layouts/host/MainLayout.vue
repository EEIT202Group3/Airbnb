<template>
  <Nav></Nav>
   <v-app>
  <v-main style="padding-top: 70px;">
    <router-view></router-view>
  </v-main>
  <Footer app fixed></Footer>
  </v-app>
</template>
<script setup>

import Nav from "@/layouts/host/NBar.vue";
import Footer from "@/layouts/Footer.vue";
</script>
<style>

</style>
