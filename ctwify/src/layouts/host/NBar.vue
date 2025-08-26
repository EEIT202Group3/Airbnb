<script setup>
import { ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useHostStore } from "@/stores/host";
import { useRouter } from "vue-router";
import logo from '@/icon/logo.png';

const router = useRouter();
const hostStore = useHostStore();
const { host } = storeToRefs(hostStore);

async function logout() {
  try {
    await hostStore.logout()
    router.push('/')
  } catch (error) {
    console.log(error.response)
    alert('登出失敗')
  }
}

async function goHome() {
  await hostStore.logout()
  router.push('/')
}

onMounted(async () => {
  if (!host.value) {
    await hostStore.fetchUser()
  }
})
</script>

<template>
   <v-app-bar position: relative density="comfortable" elevation="2.5" class="d-flex justify-center" style="height: 70px;" >
    <div class="navbar-container">
    <!-- 左：Logo（不重整導回首頁） -->
    <div role="button" tabindex="0" @click="goHome" style="margin-left: 0px;">
  <v-img :src="logo" alt="Ctwify" height="150" width="150" eager class="me-2" />
</div>


      <v-spacer />

      <v-btn :to="{ name:'addlistings' }" variant="text" class="text-black" size="large" style="font-size: large;">
        刊登旅宿
      </v-btn>

      <!-- 漢堡選單 -->
      <v-menu location="bottom end" offset="8" transition="fade-transition" close-on-content-click>
        <template #activator="{ props }">
          <v-btn v-bind="props" icon variant="text" aria-label="開啟選單" class="ms-1">
            <v-icon>mdi-menu</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="logout()">
            <v-list-item-title>登出</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </div>
  </v-app-bar>
</template>

<style scoped>
.navbar-container {
  width: 100%;
  max-width: 1200px; /* 與內容區域一致 */
  margin: 0 auto;    /* 置中 */
  display: flex;
  justify-content: space-between; /* 左右對齊 */
  align-items: center;
  padding: 0 16px;
  box-sizing: border-box;
}
</style>
