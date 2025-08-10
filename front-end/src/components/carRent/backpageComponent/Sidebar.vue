<script setup lang="ts">
const props = defineProps<{
  visible: boolean;
  close: () => void;
}>();
</script>

<template>
  <v-navigation-drawer
      :model-value="visible"
      location="left"
      temporary
      scrim
      @update:model-value="(val) => { if (!val) close(); }"
  >
    <div class="pa-4">
      <v-btn variant="outlined" block class="mb-3" @click="close">
        關閉選單
      </v-btn>

      <v-divider class="my-2" />

      <v-list density="comfortable" nav>
        <v-list-item
            :router="true"
            to="/car-rent/back-homepage"
            title="首頁"
            prepend-icon="mdi-home"
        />
        <v-divider class="my-2" />

        <v-list-item
            :router="true"
            to="/car-rent/reservations/1"
            title="新增預定"
            prepend-icon="mdi-account"
        />
        <v-divider class="my-2" />

        <v-list-item
            :router="true"
            to="/car-rent/vehicles/1"
            title="新增車輛"
            prepend-icon="mdi-car-side"
        />
        <v-divider class="my-2" />

      </v-list>
    </div>
  </v-navigation-drawer>
</template>

<style scoped>
</style>
