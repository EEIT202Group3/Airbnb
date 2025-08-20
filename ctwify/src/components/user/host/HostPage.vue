<script setup>
import HostSideBar from './HostSideBar.vue';
</script>

<template>
    <v-container class="customer-page">
        <v-row class="d-flex justify-center">
        <v-col cols="3" style="margin-top: 40px;">
            <HostSideBar></HostSideBar>
        </v-col>
        <v-col cols="8" style="margin-top: 15px;">
            <router-view></router-view>
        </v-col>
    </v-row>
    </v-container>
</template>
<style scoped>
</style>