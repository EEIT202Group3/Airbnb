<script setup lang="ts">
import CustomerSideBar from './CustomerSideBar.vue';
</script>

<template>
  <v-container class="customer-page">
    <v-row class="d-flex justify-center">
        <v-col cols="3" style="margin-top: 40px;">
            <CustomerSideBar></CustomerSideBar>
        </v-col>
        <v-col cols="7" style="margin-top: 15px;">
            <router-view></router-view>
        </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.customer-page {
  position: relative; /* 要讓 ::before 定位基準 */
  min-height: 88vh;
  min-width: 100%;
  background-image: url('@/images/background.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

/* 半透明覆蓋層 */
.customer-page::before {
  content: "";
  position: absolute;
  inset: 0; /* 等同 top:0; right:0; bottom:0; left:0; */
  background-color: rgba(255, 255, 255, 0.4); /* 白色+透明度，越高越霧 */
  z-index: 0;
}

/* 內容要在遮罩上面 */
.customer-page > * {
  position: relative;
  z-index: 1;
}
</style>