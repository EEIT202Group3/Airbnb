<script setup>
import { ref } from 'vue';
import LoginSignup from '@/components/user/customer/LoginSignup.vue';
const showAuthPage = ref(false);

</script>
<template>
  <div class="container2">
    <header class="naver">
      <v-img
        class="logo"
        src="../src/icon/logo.png"
        contain
      ></v-img>

      <nav> 
        <router-link to="#">刊登旅宿</router-link>

        <!-- 會員登入 -->
       <router-link to="#" @click.prevent="showAuthPage = true">
        <i class="fa-regular fa-circle-user fa-lg"></i> 登入/註冊
        </router-link>

        <div class="menu">
          <input type="checkbox" id="menu-toggle" />
          <label for="menu-toggle" class="menu-icon">
            <i class="fa-solid fa-bars fa-lg"></i>
          </label>
          <div class="dropdown">
            <router-link to="/">帳號設定</router-link>
            <router-link to="/">我的最愛</router-link>
            <router-link to="/">訂房紀錄</router-link>
            <router-link to="/">我的房源</router-link>
          </div>
        </div>
      </nav>
    </header>
  </div>
   <v-dialog v-model="showAuthPage" max-width="420" scrollable>
      <LoginSignup asDialog/>
  </v-dialog>
</template>

<style scoped>
@import '/src/assets/listing/list1.css';

.naver {
  height: 70px;
  position: sticky;
  top: 0;
  z-index: 5000;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 0vw;   
  padding-right: 13vw; 
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: visible;
}

.logo {
  max-height: 90px;
  width: auto;
  margin-right: 400px; 
}

</style>
