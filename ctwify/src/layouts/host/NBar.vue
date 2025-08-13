<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useHostStore } from "@/stores/host";
import { useRouter } from "vue-router";

const router = useRouter();
const hostStore = useHostStore();
const {host} = storeToRefs(hostStore);

async function logout(){
  try {
    await hostStore.logout()
    alert('登出成功')
  } catch (error) {
    console.log(error.response)
    alert('登出失敗')
  }
}

onMounted(
  async()=>{
    if(!host.value){
      await hostStore.fetchUser()
    }
  }
)

</script>

<template>
  <v-app-bar app color="white" flat class="elevation-1">
    <!-- Logo -->
    <v-toolbar-title class="text-h6 font-weight-bold">
      <RouterLink :to="{name:'Homepage'}" @click.prevent="logout" style="text-decoration: none;">
        <span class="text-orange-darken-2">My</span>
        <span style="color: black;">Booking</span>
      </RouterLink>
    </v-toolbar-title>

    <!-- Spacer -->
    <v-spacer></v-spacer>

    <!-- 漢堡選單（Vuetify Menu） -->
    <v-menu offset-y>
      <template #activator="{ props }">
        <v-btn icon v-bind="props">
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <v-list>
        <v-list-item @click="logout()">
          <v-list-item-title>登出</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>
</template>

<style scoped>
.v-btn:hover {
  background-color: #fef3e2 !important;
  color: #d35400 !important;
}
</style>
