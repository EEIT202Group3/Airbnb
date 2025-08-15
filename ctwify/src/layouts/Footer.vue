<template>
  <v-footer height="64" app fixed class="footer-overflow"
    ><v-row class="w-100" justify="space-between" align="center">
      <span>頁尾不受畫面影響</span>
      <div class="robot-wrapper">
        <robot />
      </div>
    </v-row>
  </v-footer>
</template>

<script setup>
import Robot from "@/components/chat/Robot.vue";
</script>

<style scoped>
.footer-overflow {
  overflow: visible !important;
  z-index: 10;
  position: relative;
}

.robot-wrapper {
  position: fixed; /* 用 fixed 脫離 Footer 限制 */
  bottom: 10px; /* Footer 以上一點點 */
  right: 20px;
  z-index: 999;
}
</style>
