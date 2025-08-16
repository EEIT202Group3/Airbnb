<script setup lang="ts">
import CustomerSideBar from './CustomerSideBar.vue';
</script>

<template>
  <v-container class="customer-page">
    <v-row class="d-flex justify-center">
        <v-col cols="3" style="margin-top: 40px;">
            <CustomerSideBar></CustomerSideBar>
        </v-col>
        <v-col cols="7" style="margin-top: 15px;">
            <router-view></router-view>
        </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>

</style>