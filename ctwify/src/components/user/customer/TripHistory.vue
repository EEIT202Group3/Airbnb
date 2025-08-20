<script setup lang="ts">
import axios from 'axios';
import { ref,onMounted } from 'vue';

const orders = ref([]);

const hovered = ref(null)
function getHistory(data:any){
    alert(data.id)
}

async function fetchOrders(){
    try{
        const response = await axios.get('http://localhost:8080/api/orderconfirm/byCustome',{
            withCredentials:true,
        })
        console.log(response.data)
        return response.data;
    }catch(error){
        console.log(error.response)
        return null;
    }
}

onMounted(
    async()=>{
        orders.value = await fetchOrders();
    }
)

</script>
<template>
    <v-card class="pa-8 elevation-6 rounded-xl mx-auto">
        <h1 style="font-weight: bolder;">過去的旅程</h1>
        <v-divider class="my-6" />
        <v-item-group mandatory>
            <v-container>
                <v-row v-if="orders">
                    <v-col
                    v-for="h in orders"
                    cols="12"
                    md="4"
                    >
                    <v-item>
                        <v-card
                        class="d-flex flex-column justify-center align-center"
                        height="200"
                        dark
                        @click="getHistory(h)"
                        @mouseover="hovered = h.id"
                        @mouseleave="hovered = null"
                        :style="hovered === h.id ? 'background-color: #F2AB27;color:white;' : 'background-color: #FFFAF4;'"
                        style="border-radius: 20px;"
                        >
                        <div>{{ h.location }}</div>
                        <div>{{ h.price }}</div>
                        <div>{{ h.date }}</div>
                        </v-card>
                    </v-item>
                    </v-col>
                </v-row>
                <v-row v-else>
                    <div>找不到資料!!!</div>
                </v-row>
            </v-container>
        </v-item-group>
    </v-card>
</template>
<style scoped>
.h5{
    font-weight: bolder;
}
</style>