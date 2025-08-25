<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getMonthlyCustomers,getTotalCustomers,getVerifiedCustomers } from '@/service/user/CustomerService';
import { getMonthlyHosts,getTotalHosts,getVerifiedHosts } from '@/service/user/HostService';
import Chart from 'chart.js/auto';

const months = ref([]);
const monthlyCustomers = ref([]);
const monthlyHosts = ref([]);
const totalCustomers = ref();
const totalHosts = ref();


onMounted(
    async()=>{
        const customerData = await getMonthlyCustomers();
        const hostData = await getMonthlyHosts();
        const verifiedCustomer = await getVerifiedCustomers();
        const verifiedHost = await getVerifiedHosts();
        totalCustomers.value = await getTotalCustomers();
        totalHosts.value = await getTotalHosts();
        months.value = customerData.months
        monthlyCustomers.value = customerData.registrations
        monthlyHosts.value = hostData.registrations
        const ctx1 = document.getElementById('monthlyCustomers') as HTMLCanvasElement
        new Chart(ctx1,{
            data:{
                datasets:[{
                    type:'line',
                    label:'折線圖',
                    tension:0.1,
                    pointRadius: 3,
                    borderColor: 'rgb(255, 145, 36)',
                    data:monthlyCustomers.value
                },{
                    type:'bar',
                    label:'柱狀圖',
                    backgroundColor:'rgb(255, 229, 204)',
                    data:monthlyCustomers.value
                }],
                labels:months.value
            },
            options:{
                plugins:{
                    title:{
                        display:true,
                        text:'過去12個月客戶註冊人數',
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
        const ctx2 = document.getElementById('monthlyHosts') as HTMLCanvasElement
        new Chart(ctx2,{
            data:{
                datasets:[{
                    type:'line',
                    label:'折線圖',
                    tension:0.1,
                    pointRadius: 3,
                    borderColor: 'rgb(255, 145, 36)',
                    data:monthlyHosts.value
                },{
                    type:'bar',
                    label:'柱狀圖',
                    backgroundColor:'rgb(255, 229, 204)',
                    data:monthlyHosts.value
                }],
                labels:months.value
            },
            options:{
                plugins:{
                    title:{
                        display:true,
                        text:'過去12個月房東註冊人數',
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
        const ctx3 = document.getElementById('customerPie') as HTMLCanvasElement
        new Chart(ctx3,{
            type:'pie',
            data:{
                labels:["已驗證","未驗證"],
                datasets:[{
                    data:[verifiedCustomer,(totalCustomers.value-verifiedCustomer)],
                    backgroundColor: ['#FFE4CA', '#FF9797'],
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        })
        const ctx4 = document.getElementById('hostPie') as HTMLCanvasElement
        new Chart(ctx4,{
            type:'pie',
            data:{
                labels:["已驗證","未驗證"],
                datasets:[{
                    data:[verifiedHost,(totalHosts.value-verifiedHost)],
                    backgroundColor: ['#FFE4CA', '#FF9797'],
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        })
    }
)

</script>
<template>
  <v-container fluid class="pa-4">
    <v-row dense>
      <!-- 左側：KPI + 客戶圓餅 -->
      <v-col cols="12" md="4">
        <v-card elevation="3" rounded="lg" class="mb-4 kpi-card">
          <v-card-text class="text-center">
            <div class="kpi-number">{{ totalCustomers }}</div>
            <div class="kpi-label">客戶總數</div>
          </v-card-text>
        </v-card>

        <v-card elevation="3" rounded="lg">
          <v-card-text class="pie-wrap">
            <div class="pie-inner">
              <canvas id="customerPie"></canvas>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 右側：客戶趨勢圖 -->
      <v-col cols="12" md="8">
        <v-card elevation="3" rounded="lg" class="chart-card">
          <v-card-text class="chart-wrap">
            <canvas id="monthlyCustomers"></canvas>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row dense class="mt-2">
      <!-- 左側：KPI + 房東圓餅 -->
      <v-col cols="12" md="4">
        <v-card elevation="3" rounded="lg" class="mb-4 kpi-card">
          <v-card-text class="text-center">
            <div class="kpi-number">{{ totalHosts }}</div>
            <div class="kpi-label">房東總數</div>
          </v-card-text>
        </v-card>

        <v-card elevation="3" rounded="lg">
          <v-card-text class="pie-wrap">
            <div class="pie-inner">
              <canvas id="hostPie"></canvas>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 右側：房東趨勢圖 -->
      <v-col cols="12" md="8">
        <v-card elevation="3" rounded="lg" class="chart-card">
          <v-card-text class="chart-wrap">
            <canvas id="monthlyHosts"></canvas>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
<style scoped>
/* KPI 卡片的字體樣式 */
.kpi-card { display:flex; align-items:center; height:110px; }
.kpi-number { font-size:40px; font-weight:700; color:#1f2937; } /* gray-800 */
.kpi-label { margin-top:6px; color:#6b7280; }                    /* gray-500 */

/* 統一圖表容器高度，Chart.js 才不會被擠壓 */
.chart-wrap { height: 400px; }

/* 圓餅圖置中 + 固定大小 */
.pie-wrap { height:300px; display:flex; align-items:center; justify-content:center; }
.pie-inner { width:260px; height:260px; }

/* 不建議用 .v-card { elevation:16 }；請用 v-card 的 prop 或 utility class */
</style>