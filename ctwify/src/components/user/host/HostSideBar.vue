<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const leftMenu = ref([
  { title: '關於我', icon: 'mdi-account-circle', to:{name:'HostInfo'} },
  { title: '房源管理', icon: 'mdi-home', to:{name:'HostListing'} },
  { title: '訂單一覽', icon: 'mdi-list-box-outline', to:{name:'HostOrder'} },
  { title: '評論一覽', icon: 'mdi-list-box-outline', to:{name:'HostReview'} },
])
</script>
<template>
  <div class="container">
        <h1 style="font-weight: bolder;">個人簡介</h1>  <v-list nav class="transparent-card rounded-lg pa-2" style="display: flex; flex-direction: column; align-items:  flex-start;">
        <br>
        <v-list-item
            v-for="m in leftMenu"
            :to="m.to"
            :prepend-icon="m.icon"
            class="rounded-lg mb-1 sidebar-item"
            :active="route.name === m.to.name"           
        >{{ m.title }}</v-list-item>
    </v-list>
    </div>
</template>
<style scoped>
.sidebar-item {
  width: 100%;
  font-size: 20px;
  font-weight: bold;
}
.transparent-card {
  background-color: transparent !important; /* 背景透明 */
  box-shadow: none !important; /* 移除陰影，若需要 */
}

.container {
  max-width: 1100px; /* 與 navbar 對齊 */
  margin: 0 auto;    /* 置中 */
  padding: 0 16px;   /* 預留左右邊距 */
  box-sizing: border-box;
}


</style>