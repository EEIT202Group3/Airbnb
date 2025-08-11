<script setup>
import { ref } from 'vue';
import LoginSignup from '@/components/user/LoginSignup.vue';
const showAuthPage = ref(false);
</script>

<template>
  <v-app-bar app color="white" flat class="elevation-1">
    <!-- Logo -->
    <v-toolbar-title class="text-h6 font-weight-bold">
      <RouterLink to="/" style="text-decoration: none;">
        <span class="text-orange-darken-2">My</span>
        <span style="color: black;">Booking</span>
      </RouterLink>
    </v-toolbar-title>

    <!-- Spacer -->
    <v-spacer></v-spacer>

    <!-- 主選單連結 -->
    <v-btn text to="/listing/addlisting">刊登旅宿</v-btn>
    <v-btn text to="/">規劃行程</v-btn>

    <!-- 登入 / 註冊 -->
    <router-link class="text-decoration-none" color="black">
      <v-btn variant="text" class="d-flex align-center text-black" @click="showAuthPage=true">
        <v-icon class="me-1" color="black">mdi-account-circle-outline</v-icon>
        登入 / 註冊
      </v-btn>
    </router-link>
    <!-- 漢堡選單（Vuetify Menu） -->
    <v-menu offset-y>
      <template #activator="{ props }">
        <v-btn icon v-bind="props">
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <v-list>
        <v-list-item to="/customer">
            <v-list-item-title>客戶首頁</v-list-item-title>
          </v-list-item>
        <v-list-item to="/">
          <v-list-item-title>租車服務</v-list-item-title>
        </v-list-item>
        <v-list-item to="/">
          <v-list-item-title>帳號設定</v-list-item-title>
        </v-list-item>
        <v-list-item to="/">
          <v-list-item-title>我的最愛</v-list-item-title>
        </v-list-item>
        <v-list-item to="/main/getList">
          <v-list-item-title>訂房紀錄</v-list-item-title>
        </v-list-item>
        <v-list-item to="/main/list">
          <v-list-item-title>我的房源</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>
  <v-dialog v-model="showAuthPage" max-width="420" scrollable>
      <LoginSignup asDialog @login-success="showAuthPage=false"/>
  </v-dialog>
</template>

<style scoped>
.v-btn:hover {
  background-color: #fef3e2 !important;
  color: #d35400 !important;
}
</style>
