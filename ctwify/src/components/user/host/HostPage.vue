<script setup>
import HostSideBar from './HostSideBar.vue';
</script>

<template>
      <div class="container">
    <v-container class="customer-page">
        <v-row class="d-flex justify-center">
        <v-col cols="3" style="margin-top: 40px;">
            <HostSideBar></HostSideBar>
        </v-col>
        <v-col cols="8" style="margin-top: 15px;">
            <router-view></router-view>
        </v-col>
    </v-row>
    </v-container>
    </div>
</template>

<style scoped>
.container {
  width: 100%;
  max-width: 1100px; /* 與 navbar 對齊 */
  margin: 0 auto;    /* 置中 */
  padding: 0 16px;   /* 預留左右邊距 */
  box-sizing: border-box;
}

</style>