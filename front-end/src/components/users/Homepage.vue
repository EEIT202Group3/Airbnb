<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getMonthlyCustomers } from '@/service/user/CustomerService';
import Chart from 'chart.js/auto';

const months = ref([]);
const monthlyCustomers = ref([]);


onMounted(
    async()=>{
        const customerData = await getMonthlyCustomers();
        console.log(customerData)
        months.value = customerData.months
        monthlyCustomers.value = customerData.registrations
        const ctx = document.getElementById('monthlyCustomers') as HTMLCanvasElement
        new Chart(ctx,{
            data:{
                datasets:[{
                    type:'line',
                    label:'折線圖',
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
                }
            }
        })
    }
)

</script>
<template>
    <h1>這是首頁</h1>
    <div style="width: 75%; height: auto; margin-left: 20px;">
        <canvas id="monthlyCustomers"></canvas>
    </div>
</template>
<style scoped>
</style>