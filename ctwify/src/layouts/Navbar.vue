<script setup>
import { ref,onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useCustomerStore } from '@/stores/customer';
import LoginSignup from '@/components/user/customer/LoginSignup.vue';
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
      <LoginSignup asDialog @login-success="showAuthPage=false"/>
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
  padding-right: 15.5vw; 
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: visible;
}

.logo {
  max-height: 90px;
  width: auto;
  margin-right: 300px; 
}
</style>
