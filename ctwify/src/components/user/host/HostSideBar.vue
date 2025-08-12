<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const leftMenu = ref([
  { title: '關於我', icon: 'mdi-account-circle', to:{name:'HostInfo'} },
  { title: '房源管理', icon: 'mdi-home', to:{name:'HostListing'} },
  { title: '訂單一覽', icon: 'mdi-list-box-outline', to:{name:'HostOrder'} },
])
</script>
<template>
    <v-list nav class="rounded-lg pa-2">
        <h1 style="font-weight: bolder;">個人簡介</h1>
        <br>
        <v-list-item
            v-for="m in leftMenu"
            :to="m.to"
            :prepend-icon="m.icon"
            :title="m.title"
            class="rounded-lg mb-1 sidebar-item"
            :active="route.name === m.to.name" 
            active-class="left-active"           
        />
    </v-list>
</template>
<style scoped>
.left-active {
  background-color:	#F0F0F0;
}
.sidebar-item {
  font-size: 30px;
}
</style>