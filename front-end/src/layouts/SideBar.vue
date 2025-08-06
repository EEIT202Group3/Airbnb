<script setup lang="ts">

import { ref,onMounted } from 'vue';
import { getCurrentAdmin } from '@/service/user/AdminService';
import { useRouter } from 'vue-router';
import { useAdminStore } from '@/stores/user/adminStore';
const loginDialog = ref(false);
const adminStore = useAdminStore();
const formData = ref({
  adminId: "",
  password: "",
});
const router = useRouter();

onMounted(async()=>{
    if(!adminStore.admin){
        const result = await getCurrentAdmin();
        adminStore.admin = result;
    }
})

async function logout(){
    try {
        const response = await fetch('http://localhost:8080/api/admins/logout', {
            method: 'POST',
            credentials: 'include'
        });
        if (response.ok) {
            adminStore.admin=null;
            alert("登出成功");
        } else {
            alert("登出失敗");
        }
        router.push({name:'Homepage'})
    } catch (error) {
        console.error("登出錯誤：", error);

    }
    router.push({ name: "Homepage" });
}
async function login() {
    loginDialog.value = false;
    try {
        const response = await fetch('http://localhost:8080/api/admins/login', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                adminId: formData.value.adminId,
                password: formData.value.password
            })
        });
        if(response.ok){
            adminStore.admin = await getCurrentAdmin();
            router.push({name:'Homepage'});
        }
    } catch (error) {
        console.error("發生錯誤：", error);
        alert('無法連線後端伺服器');
        router.push({name:'Homepage'});
    }
}

const reviewLinks = [{
    name: "評論",
    icon: "mdi-comment-outline",
    children: [
      { path: "/reviews/insert", name: "撰寫評論" },
      { path: "/reviews/list", name: "查看評論" },
    ],
  }]
</script>
<template>
  <v-app-bar>
    <v-menu>
      <template v-slot:activator="{ props }">
        <v-app-bar-nav-icon v-bind="props"></v-app-bar-nav-icon>
      </template>
      <v-card>
        <v-list-item title="登入" @click="loginDialog = true"></v-list-item>
        <v-list-item title="登出" @click="logout()"></v-list-item>
      </v-card>
    </v-menu>
    <router-link to="/" style="text-decoration: none; color: inherit">
      <v-app-bar-title>Application Bar</v-app-bar-title>
    </router-link>
  </v-app-bar>
  <v-navigation-drawer>
    <v-list nav>
      <v-list-item v-if="adminStore.admin">
        <div>{{ adminStore.admin.adminId }}</div>
        <div>{{ adminStore.admin.username }}</div>
      </v-list-item>
      <v-list-item v-else>
        <div>未登入</div>
      </v-list-item>
      <v-list-item to="/customers"
        ><v-icon icon="mdi-account"></v-icon>客戶</v-list-item
      >
      <v-list-item to="/hosts"
        ><v-icon icon="mdi-home-account"></v-icon>房東</v-list-item
      >
      <v-list-item to="/AdminOrder"
        ><v-icon icon="mdi-invoice-text-multiple-outline"></v-icon
        >訂單管理系統</v-list-item
      >
      <v-list>
  <v-list-group
    v-for="link in reviewLinks"
    :key="link.name"
    :prepend-icon="link.icon"
    :value="link.name"
  >
    <template v-slot:activator="{ props }">
      <v-list-item v-bind="props" :title="link.name" />
    </template>

    <v-list-item
      v-for="child in link.children"
      :key="child.path"
      :to="child.path"
      :title="child.name"
      router
    />
  </v-list-group>
</v-list>
<v-list-item to="/AdminOrder"
        ><v-icon icon="mdi-invoice-text-multiple-outline"></v-icon
        >訂單管理系統</v-list-item
      >


    </v-list>
  </v-navigation-drawer>
  <!-- 登入表單 -->
  <v-dialog v-model="loginDialog">
    <v-card class="pa-6 mx-auto" style="width: 25%; height: auto">
      <form @submit.prevent="login">
        <v-text-field
          v-model="formData.adminId"
          label="員工編號"
          prepend-icon="mdi-account"
          required
        ></v-text-field>
        <v-text-field
          v-model="formData.password"
          label="密碼"
          prepend-icon="mdi-lock"
          type="password"
          required
        ></v-text-field>
        <v-btn type="submit">送出</v-btn>
      </form>
    </v-card>
  </v-dialog>

</template>
<style scoped>
</style>