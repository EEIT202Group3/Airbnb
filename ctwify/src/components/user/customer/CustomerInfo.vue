<script setup lang="ts">
import { ref,onMounted } from 'vue'
import { findMe } from '@/service/user/customerService'
const user = ref(null)
onMounted(
    async()=>{
        user.value = await findMe();
        console.log(user.value);
    }
)
</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
 
            <h1 style="font-weight: bolder;">關於我</h1>
            
            <v-btn size="small" style="margin-top: 10px; margin-left: 10px; border-radius: 20px;" :to="{name:'CustomerProfile'}">編輯</v-btn>
       
        
        <br>
        <br>
        
        <v-card class="pa-8 elevation-2 rounded-xl" style="width: 35%;">
            <div v-if="user" class="d-flex align-center">
                <v-avatar size="80" class="mr-4">
                    <!--  -->
                    <v-img :src="user?.avatarURL ? 'http://localhost:8080' + user.avatarURL : require('@/assets/user/account.svg')" cover />
                </v-avatar>
                <div>
                    <div class="text-h6">{{ user.username }}</div>
                    <div v-if="user.verified" class="text-body-2 text-medium-emphasis">
                        <v-icon icon="mdi-shield-check" color="green"></v-icon>已驗證
                    </div>
                    <div v-else class="text-body-2 text-medium-emphasis">
                        <v-icon icon="mdi-alert-circle" color="red"></v-icon>未驗證
                    </div>
                </div>
            </div>
            <div v-else>
                請重新登入
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