<script setup lang="ts">
</script>

<template>
  <v-app-bar
      flat
      color="surface"
      elevation="1"
      density="comfortable"
  >
    <v-container class="d-flex align-center">
      <RouterLink to="/car-front-homepage" class="text-decoration-none">
        <v-img
            src="/carPicture/logo.jpg"
            alt="logo"
            width="100"
            height="80"
            cover
            class="mr-2"
        />
      </RouterLink>

      <v-spacer />
    </v-container>
  </v-app-bar>
</template>

<style scoped>
</style>
