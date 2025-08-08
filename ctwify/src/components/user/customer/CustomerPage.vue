<script setup lang="ts">
import CustomerSideBar from './CustomerSideBar.vue';
</script>

<template>
  <v-container style="margin-top: 50px;">
    <v-row>
        <v-col cols="3" style="margin-top: 15px;">
            <CustomerSideBar></CustomerSideBar>
        </v-col>
        <v-col cols="9">
            <router-view></router-view>
        </v-col>
    </v-row>
  </v-container>
</template>