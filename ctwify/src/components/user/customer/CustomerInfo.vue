<script setup lang="ts">
import { ref } from 'vue'
const user = ref({
  id: 'nick-001',
  name: 'Nick',
  role: '旅客',
  avatar: '/avatar.jpg', // 可放 public/avatar.jpg，或改成你的圖
  verified: true,
  about: '嗨！我喜歡旅行、攝影與咖啡。'
})
</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
 
            <h1 style="font-weight: bolder;">關於我</h1>
            
            <v-btn size="small" style="margin-top: 10px; margin-left: 10px; border-radius: 20px;">修改個資</v-btn>
       
        
        <br>
        <br>
        
        <v-card class="pa-8 elevation-2 rounded-xl" style="width: 35%;">
            <div class="d-flex align-center">
                <v-avatar size="80" class="mr-4">
                    <v-img src="/src/avatar/logo.png" alt="avatar" cover />
                </v-avatar>
                <div>
                    <div class="text-h6">{{ user.name }}</div>
                    <div class="text-body-2 text-medium-emphasis">{{ user.role }}</div>
                </div>
            </div>
        </v-card>
        
        <v-divider class="my-6" />
        
        <div class="d-flex align-center mb-4">
            <v-icon class="mr-2">mdi-chat-processing-outline</v-icon>
            <span class="text-subtitle-1 font-weight-medium">我曾撰寫的評價</span>
        </div>
        <v-sheet class="pa-8 text-medium-emphasis rounded-lg bg-grey-lighten-5 text-center">
        目前沒有評價。
        </v-sheet>
    </v-card>
</template>
<style scoped>
</style>