<script setup lang="ts">
import { ref, onMounted } from "vue";
import { getCurrentAdmin } from "@/service/user/AdminService";
import { useRouter, useRoute } from "vue-router";
import { useAdminStore } from "@/stores/user/adminStore";
const loginDialog = ref(false);
const adminStore = useAdminStore();
const formData = ref({
  adminId: "",
  password: "",
});
const router = useRouter();
const route = useRoute();
const isOpen = ref(false);
const isActive = (to?: string) => to && route.path === to;
const items = [
  { title: "客戶清單", icon: "mdi-account", to: "/customers" },
  { title: "房東清單", icon: "mdi-home-account", to: "/hosts" },
  { title: "房源管理", icon: "mdi-home-city-outline", to:"/approveListing"},
  { title: "設備管理", icon: "mdi-tools", to:"/addEquipment"},
  { title: "評論清單", icon: "mdi-comment", to: "/reviews/list" },
  { title: "租車服務", icon: "mdi-car", to: "/car-rent/back-homepage" },
];

// const reviewLinks = [{
//     name: "評論",
//     icon: "mdi-comment-outline",
//     children: [
//       { path: "/reviews/insert", name: "撰寫評論" },
//       { path: "/reviews/list", name: "查看評論" },
//     ],
//   }]

//   const carRentBackLinks = [
//   {
//     name: "租車後台",
//     icon: "mdi-car-cog",
//     children: [
//       { path: "/car-rent/back-homepage/reservations/1", name: "訂單詳情" },
//       { path: "/car-rent/back-homepage/vehicles/1", name: "車輛詳情" },
//     ],
//   },
// ];

onMounted(async () => {
  if (!adminStore.admin) {
    const result = await getCurrentAdmin();
    adminStore.admin = result;
  }
});

async function logout() {
  try {
    const response = await fetch("http://localhost:8080/api/admins/logout", {
      method: "POST",
      credentials: "include",
    });
    if (response.ok) {
      adminStore.admin = null;
      alert("登出成功");
    } else {
      alert("登出失敗");
    }
    router.push({ name: "Homepage" });
  } catch (error) {
    console.error("登出錯誤：", error);
  }
  router.push({ name: "Homepage" });
}
async function login() {
  loginDialog.value = false;
  try {
    const response = await fetch("http://localhost:8080/api/admins/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        adminId: formData.value.adminId,
        password: formData.value.password,
      }),
    });
    if (response.ok) {
      adminStore.admin = await getCurrentAdmin();
      alert("登入成功");
      router.push({ name: "Homepage" });
    }
  } catch (error) {
    console.error("發生錯誤：", error);
    alert("無法連線後端伺服器");
    router.push({ name: "Homepage" });
  }
}
</script>

<template>
  <v-navigation-drawer app permanent width="300" class="pa-4">
    <!-- Logo / Title -->
    <div
      class="d-flex align-center mb-6"
      style="margin-bottom: 0px; padding-bottom: 0px"
    >
    <div
  style="
    width: 110px;
    height: 110px;
    border-radius: 50%;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  "
>
  <v-img
    src="../src/icon/logo.png"
    contain
    style="width: 100%; height: auto;"
  ></v-img>
</div>
      <div class="ml-3">
        <div class="sidebar-title">Ctwify</div>
        <div class="sidebar-subtitle">DASHBOARD</div>
      </div>
    </div>

    <v-list density="comfortable" nav>
      <v-list-item
        v-if="adminStore.admin"
        :title="adminStore.admin.username"
        :subtitle="adminStore.admin.adminId"
      >
        <template #append>
          <v-btn variant="text" prepend-icon="mdi-logout" @click="logout()">
            登出
          </v-btn>
        </template>
      </v-list-item>
      <v-list-item
        v-else
        link
        class="rounded-lg mb-1 sidebar-item"
        prepend-icon="mdi-login"
        title="登入"
        @click="loginDialog = true"
      />
      <template v-for="item in items" :key="item.title">
        <!-- 有子選單 -->
        <v-list-group
          v-if="item.children"
          v-model="isOpen"
          prepend-icon="mdi-shield-check"
        >
          <template #activator="{ props }">
            <v-list-item
              v-bind="props"
              :title="item.title"
              class="rounded-lg sidebar-item"
              :class="{
                'active-item': item.children.some((c) => isActive(c.to)),
              }"
            />
          </template>

          <v-list-item
            v-for="child in item.children"
            :key="child.title"
            :to="child.to"
            link
            class="rounded-lg ml-2 sidebar-item"
            :class="{ 'active-item': isActive(child.to) }"
            :title="child.title"
          />
        </v-list-group>

        <!-- 單一項目 -->
        <v-list-item
          v-else
          :to="item.to"
          link
          class="rounded-lg mb-1 sidebar-item"
          :prepend-icon="item.icon"
          :title="item.title"
          :class="{ 'active-item': isActive(item.to) }"
        />
      </template>
    </v-list>
  </v-navigation-drawer>
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

<!-- <template>
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
        <v-list-item to="/addEquipment"
        ><v-icon icon="mdi-home-account"></v-icon>新增設備</v-list-item
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
   <v-list-group
          v-for="link in carRentBackLinks"
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
      
    </v-list>
  </v-navigation-drawer> -->
<!-- 登入表單 -->

<!-- </template> -->
<style scoped>
/* 標題字體 */
.sidebar-title {
  font-size: 2rem;
  font-weight: bold;
}
/* 副標題字體 */
.sidebar-subtitle {
  font-size: 1rem;
  color: rgba(0, 0, 0, 0.6);
}

/* item字體 */
.sidebar-item {
  font-size: 2rem;
}

/* 設定目前路由的顏色 */
.active-item {
  background: #ff9800 !important;
  color: white !important;
}
.active-item .v-list-item-title,
.active-item .v-icon {
  color: white !important;
}
</style>