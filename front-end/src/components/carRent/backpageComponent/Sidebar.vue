<script setup lang="ts">
import {defineProps} from 'vue';

const props = defineProps<{
  visible: boolean;
  close: () => void;
}>();
</script>

<template>
  <div v-if="visible" class="sidebar-overlay" @click.self="close">
    <div class="sidebar-popup">
      <button class="btn btn-outline-secondary mt-1 mb-4" @click="close">關閉選單</button>
      <router-link to="/car-rent/back-homepage"><i class="fa-solid fa-house"></i> 主頁面</router-link>
      <router-link to="/car-rent/reservations/22"><i class="fa-solid fa-user"></i> 訂單資訊</router-link>
      <router-link to="/car-rent/vehicles/4"><i class="fa-solid fa-car-side"></i> 車輛管理</router-link>
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
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1050;
}

.sidebar-popup {
  background-color: #fff;
  width: 250px;
  height: 100%;
  padding: 1.5rem;
  position: absolute;
  top: 0;
  left: 0;
  box-shadow: 3px 0 10px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}

.sidebar-popup a {
  display: block;
  margin-bottom: 1rem;
  color: #333;
  text-decoration: none;
  text-align: center;
}

.sidebar-popup a:hover {
  background-color: #e0e0e0;
  border-radius: 4px;
  padding: 0.25rem 0.5rem;
}
</style>
