<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useCustomerStore } from '@/stores/customer'
import { useHostStore } from '@/stores/host';
import LoginSignup from '@/components/user/customer/LoginSignup.vue'
import defaultAvatar from '@/images/default.png'
import logo from '@/icon/logo.png'

const router = useRouter()
const showAuthPage = ref(false)
const customerStore = useCustomerStore()
const { customer } = storeToRefs(customerStore)
const hostStore = useHostStore()

const avatarUrl = computed(
  () => (customer.value?.avatarURL ? 'http://localhost:8080' + customer.value.avatarURL : defaultAvatar)
)

onMounted(async () => {
  if (!customer.value) await customerStore.fetchUser()
})

const goHome = () => router.replace({ name: 'Homepage' })

async function logout(){
  try {
    await customerStore.logout()
    alert('登出成功')
  } catch (error) {
    console.log(error.response)
    alert('登出失敗')
  }
}

async function toHost(){
  const request = {
    email:'nickck0527@gmail.com',
    password:'@Test123',
  }
  await hostStore.login(request);
  router.push('/host');
}
</script>

<template>
  <v-app-bar position: relative density="comfortable" elevation="2.5" class="d-flex justify-center" style="height: 9%;" >
    <!-- 左：Logo（不重整導回首頁） -->
    <div role="button" tabindex="0" @click="goHome" style="margin-left:13%;">

      <v-img :src="logo" alt="Ctwify" height="150" width="150" eager class="me-2" />

    </div>

    <v-spacer />


     <div class="right-section d-flex align-center">

    <router-link
      v-if="customer"
      to="/customer"
      class="d-inline-flex align-center ms-2 me-1 text-decoration-none"
      aria-label="Profile"
    >
      <v-avatar size="32">
        <v-img :src="avatarUrl" cover />
      </v-avatar>
    </router-link>
    <v-btn
      v-else
      variant="text"
      class="text-black ms-2"
      size="large"
      style="font-size: large;"
      @click="showAuthPage = true"
    >
      登入 / 註冊
    </v-btn>

    <v-btn  
      variant="text" 
      class="text-black" 
      size="large"
      style="font-size: large;"  
      @click="toHost"
    >房東專區</v-btn>

    <!-- 右：漢堡選單 -->
    <v-menu location="bottom end" offset="8" transition="fade-transition" close-on-content-click>
      <template #activator="{ props }">
        <v-btn v-bind="props" icon variant="text" aria-label="開啟選單" class="ms-1">
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-list-item to="/reviews">
          <v-list-item-title>我的評論</v-list-item-title>
        </v-list-item>
        <v-list-item to="/order/list">
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
    </div>
  </v-app-bar>

  <!-- 登入 / 註冊 Dialog -->
  <v-dialog v-model="showAuthPage" max-width="420" scrollable>
    <LoginSignup asDialog @login-success="showAuthPage = false" />
  </v-dialog>
</template>

<style scoped>

.right-section {
  margin-right: 14%;
}

</style>