<script setup lang="ts">
import { ref } from 'vue';
const orders = [
    {id: 'ORD-20250811-001', amount: 3200, date: '2025/08/01'},
    {id: 'ORD-20250811-002', amount: 4500, date: '2025/08/03'},
    {id: 'ORD-20250811-003', amount: 2800, date: '2025/08/05'},
    {id: 'ORD-20250811-004', amount: 5200, date: '2025/08/08'},
    {id: 'ORD-20250811-005', amount: 3900, date: '2025/08/10'}
];
const hovered = ref(null)

function getOrder(data : any){
    alert(data.id+data.amount+data.date)
}

</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
        <h1 style="font-weight: bolder;">訂單總覽</h1>
        <v-divider class="my-6" />
        <v-item-group mandatory>
            <v-container>
                <v-row>
                    <v-col
                    v-for="o in orders"
                    cols="12"
                    md="4"
                    >
                    <v-item>
                        <v-card
                        class="d-flex flex-column justify-center align-center"
                        height="200"
                        dark
                        @click="getOrder(o)"
                        @mouseover="hovered = o.id"
                        @mouseleave="hovered = null"
                        :style="hovered === o.id ? 'background-color:#F2AB27;color:white;' : ''"
                        >
                        <div>{{ o.id }}</div>
                        <div>{{ o.amount }}</div>
                        <div>{{ o.date }}</div>
                        </v-card>
                    </v-item>
                    </v-col>
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