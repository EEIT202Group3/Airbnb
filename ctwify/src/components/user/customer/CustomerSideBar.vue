<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const leftMenu = ref([
  { title: '關於我', icon: 'mdi-account-circle', to:{name:'CustomerInfo'} },
  { title: '過往旅程', icon: 'mdi-briefcase-clock', to:{name:'TripHistory'} },
])
</script>
<template>
    <v-list nav class="transparent-card rounded-lg pa-2" style="display: flex; flex-direction: column; align-items: center;">
        <h1 style="font-weight: bolder;">個人簡介</h1>
        <br>
        <v-list-item
            v-for="m in leftMenu"
            :to="m.to"
            :prepend-icon="m.icon"
            class="rounded-lg mb-1 sidebar-item"
            :active="route.name === m.to.name"           
        >{{ m.title }}</v-list-item>
    </v-list>
</template>
<style scoped>
.sidebar-item {
  width: 45%;
  font-size: 20px;
  font-weight: bold;
}
.transparent-card {
  background-color: transparent !important; /* 背景透明 */
  box-shadow: none !important; /* 移除陰影，若需要 */
}
</style>
