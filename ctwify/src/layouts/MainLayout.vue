<template>
  <Nav></Nav>
   <v-app>
  <v-main style="padding-top: 70px;">
    <router-view></router-view>
  </v-main>
  <v-footer><Chat></Chat></v-footer>
  <Footer app fixed></Footer>
  </v-app>
</template>
<script setup>
import Nav from "@/layouts/Navbar.vue";
import Footer from "@/layouts/Footer.vue";
import Chat from "@/components/chat/Robot.vue";
</script>
<style></style>
