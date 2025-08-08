<script setup lang="ts">
const props = defineProps<{
  visible: boolean;
  close: () => void;
}>();
</script>

<template>
  <div v-if="visible" class="sidebar-overlay" @click.self="close">
    <div class="sidebar-popup">
      <button class="btn btn-outline-secondary w-100 mb-3" @click="close">關閉選單</button>

      <hr />
      <router-link class="menu-link" to="/car-rent/back-homepage">
        <i class="fa-solid fa-house me-2"></i> 主頁面
      </router-link>
      <hr />

      <router-link class="menu-link" to="/car-rent/reservations/1">
        <i class="fa-solid fa-user me-2"></i> 訂單資訊
      </router-link>
      <hr />

      <router-link class="menu-link" to="/car-rent/vehicles/1">
        <i class="fa-solid fa-car-side me-2"></i> 車輛管理
      </router-link>
      <hr />
      <router-link class="menu-link" to="/">
        <i class="fa-solid fa-car-side me-2"></i> 回首頁
      </router-link>
    </div>
  </div>
</template>


<style scoped>
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1050;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  padding: 2rem;
}

.sidebar-popup {
  background-color: #fff;
  position: absolute;
  top: 1rem;
  left: 1rem;
  width: 220px;
  height: auto;
  max-height: 60vh;
  padding: 1rem 1rem;
  border-radius: 12px;
  border: 1px solid #ddd;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  overflow-y: auto;
}

.menu-link {
  display: block;
  padding: 0.5rem 0.75rem;
  color: #333;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.menu-link:hover {
  background-color: #f0f0f0;
}

hr {
  margin: 0.5rem 0;
  border: none;
  border-top: 1px solid #ccc;
}
</style>
