<script setup lang="ts">
import SideBar from './SideBar.vue';
</script>

<template>
  <v-app>
    <SideBar></SideBar>
    <v-main style="margin-left: 30px; margin-top: 50px; margin-right: 30px;">
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<style scoped></style>
