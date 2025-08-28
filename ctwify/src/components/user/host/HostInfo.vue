<script setup lang="ts">
import { ref,onMounted } from 'vue';
import Chart from 'chart.js/auto';
import { storeToRefs } from 'pinia';
import { useHostStore } from '@/stores/host';
import { getRevenue,getYearlyRevenue } from '@/service/host/hostService';
import defaultAvatar from '@/images/default.png'

const hostStore = useHostStore()
const {host} = storeToRefs(hostStore)
const revenue = ref()
const months = ref([]);
const monthlyRevenue = ref([]);

onMounted(
    async()=>{
        if(!host.value){
            hostStore.fetchUser();
        }
        revenue.value = await getRevenue()
        const revenueData = await getYearlyRevenue()
        months.value = revenueData.months
        monthlyRevenue.value = revenueData.revenues
        const ctx1 = document.getElementById('yearlyRevenue') as HTMLCanvasElement
        new Chart(ctx1,{
            data:{
                datasets:[{
                    type:'line',
                    label:'折線圖',
                    tension:0.1,
                    pointRadius: 3,
                    borderColor: 'rgb(255, 145, 36)',
                    data:monthlyRevenue.value
                },{
                    type:'bar',
                    label:'柱狀圖',
                    backgroundColor:'rgb(255, 229, 204)',
                    data:monthlyRevenue.value
                }],
                labels:months.value
            },
            options:{
                plugins:{
                    title:{
                        display:true,
                        text:'收益統計',
                        font:{
                            size:28,
                            weight:'bold'
                        }
                    }
                },
                responsive: true,
                maintainAspectRatio: false
            }
        })
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
                <v-card class="pa-8 elevation-2 rounded-xl" style="width: 170%;">
                    <div v-if="host" class="d-flex align-center">
                        <v-avatar size="80" class="mr-4">
                            <!--  -->
                            <v-img :src="host?.avatarURL ? 'http://localhost:8080' + host.avatarURL : defaultAvatar" 
                            />
                            
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
            <v-col cols="8">
                <v-card class="pa-8 elevation-2 rounded-xl" style="width: 90%; margin-left: 85px;" >
                    <h3 style="font-weight: bold;">房東介紹</h3>
                    <p>{{ host?.intro?host.intro:'此房東沒有介紹' }}</p>
                </v-card>
            </v-col>
        </v-row>
        

        <v-divider class="my-6" />

        <div class="d-flex align-center mb-4">
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            <span class="text-subtitle-1 font-weight-medium">收益</span>
        </div>
        
        <v-row>
            <v-col cols="12" md="4">
                <v-card class="d-flex elevation-2 rounded-xl" style="height: 100px;">
                    <v-card-text class="text-center">
                        <h1 style="color: green;">${{revenue?revenue:0}}</h1>
                        <div>本月收益</div>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <v-card class="d-flex elevation-2 rounded-xl" style="height: 300px;">
                    <v-card-text class="text-center">
                        <canvas id="yearlyRevenue"></canvas>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-card>
</template>
<style scoped>
.v-btn{
    background-color:#FFFAF4;
}
</style>