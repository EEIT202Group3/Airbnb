<script setup>
import { ref,onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useCustomerStore } from "@/stores/customer";
import LoginSignup from "@/components/user/customer/LoginSignup.vue";
import defaultAvatar from '@/images/default.png'

const showAuthPage = ref(false);

const customerStore = useCustomerStore();
const {customer} = storeToRefs(customerStore);

async function logout(){
  try {
    await customerStore.logout()
    alert('登出成功')
  } catch (error) {
    console.log(error.response)
    alert('登出失敗')
  }
}

onMounted(
  async()=>{
    if(!customer.value){
      await customerStore.fetchUser()
    }
  }
)
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
    <v-btn text to="/" class="ml-2 mr-4">規劃行程</v-btn>

    <!-- 登入 / 註冊 -->
    <router-link v-if="customer" to="/customer">
      <v-avatar size="32">
        <v-img :src="customer?.avatarURL ? 'http://localhost:8080' + customer.avatarURL : defaultAvatar" cover/>
      </v-avatar>
    </router-link>
    <router-link v-else class="text-decoration-none" color="black">
      <v-btn
        variant="text"
        class="d-flex align-center text-black"
        @click="showAuthPage = true"
      >
        <v-icon class="me-1" color="black">mdi-account-circle-outline</v-icon>
        登入 / 註冊
      </v-btn>
    </router-link>
    <!-- 漢堡選單（Vuetify Menu） -->
    <v-menu offset-y>
      
      <v-list>
        
        <v-list-item to="/hostLogin">
          <v-list-item-title>房東專區</v-list-item-title>
        </v-list-item>
        <v-list-item to="/">
          <v-list-item-title>租車服務</v-list-item-title>
        </v-list-item>
        <v-list-item to="/">
          <v-list-item-title>帳號設定</v-list-item-title>
        </v-list-item>
        <v-list-item to="/reviews">
          <v-list-item-title>我的評論</v-list-item-title>
        </v-list-item>
        <v-list-item to="/main/getList">
          <v-list-item-title>訂房紀錄</v-list-item-title>
        </v-list-item>
        <v-list-item to="/main/list">
          <v-list-item-title>我的房源</v-list-item-title>
        </v-list-item>
        <v-list-item @click="logout()">
          <v-list-item-title>登出</v-list-item-title>
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
