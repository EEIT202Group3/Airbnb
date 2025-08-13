<script setup lang="ts">
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useHostStore } from '@/stores/host';
import defaultAvatar from '@/images/default.png'

const hostStore = useHostStore()
const {host} = storeToRefs(hostStore)


onMounted(
    async()=>{
        if(!host.value){
            hostStore.fetchUser();
        }
    }
)
</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
        
        <h1 style="font-weight: bolder;">關於我</h1>
            
            <v-btn size="small" style="margin-top: 10px; margin-left: 10px; border-radius: 20px;" :to="{name:'HostProfile'}">編輯</v-btn>
       
        
        <br>
        <br>
        <v-row>
            <v-col cols="3">
                <v-card class="pa-8 elevation-2 rounded-xl" style="width: 100%;">
                    <div v-if="host" class="d-flex align-center">
                        <v-avatar size="80" class="mr-4">
                            <!--  -->
                            <v-img :src="host?.avatarURL ? 'http://localhost:8080' + host.avatarURL : defaultAvatar" />
                        </v-avatar>
                        <div>
                            <div class="text-h6">{{ host.username }}</div>
                            <div v-if="host.verified" class="text-body-2 text-medium-emphasis">
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
            </v-col>
            <v-col cols="9">
                <v-card class="pa-8 elevation-2 rounded-xl" style="width: 100%;">
                    <h3 style="font-weight: bold;">房東介紹</h3>
                    <p>{{ host?.intro?host.intro:'此房東沒有介紹' }}</p>
                </v-card>
            </v-col>
        </v-row>
        

        <v-divider class="my-6" />

        <div class="d-flex align-center mb-4">
            <v-icon class="mr-2">mdi-chat-processing-outline</v-icon>
            <span class="text-subtitle-1 font-weight-medium">這邊看要放甚麼</span>
        </div>
        <v-sheet class="pa-8 text-medium-emphasis rounded-lg bg-grey-lighten-5 text-center">
        待定
        </v-sheet>
    </v-card>
</template>
<style scoped>
.v-btn{
    background-color: #F29727;
    color: white;
}
</style>