<script setup lang="ts">
</script>

<template>
  <v-app-bar
      app
      flat
      color="surface"
      density="comfortable"
      height="56"
      elevation="1"
      border="b"
  >
    <v-container class="d-flex align-center">
      <v-btn
          icon
          variant="text"
          :ripple="false"
          to="/car-front-homepage"
          aria-label="回首頁"
      >
        <v-icon size="28">mdi-steering</v-icon>
      </v-btn>

      <v-spacer />
    </v-container>
  </v-app-bar>
</template>

<style scoped>
.v-btn { margin-left: -4px; }
</style>
