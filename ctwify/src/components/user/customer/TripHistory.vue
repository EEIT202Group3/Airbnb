<script setup lang="ts">
import { ref } from 'vue';
const history = [
    {location:'Taipei',date:'2025/01/01',price:'2500',id:'1'},
    {location:'Taipei',date:'2025/01/01',price:'2500',id:'2'},
    {location:'Taipei',date:'2025/01/01',price:'2500',id:'3'},
    {location:'Taipei',date:'2025/01/01',price:'2500',id:'4'},
]

const hovered = ref(null)
function getHistory(data:any){
    alert(data.id)
}

</script>
<template>
    <v-card class="pa-8 elevation-6 rounded-xl mx-auto">
        <h1 style="font-weight: bolder;">過去的旅程</h1>
        <v-divider class="my-6" />
        <v-item-group mandatory>
            <v-container>
                <v-row>
                    <v-col
                    v-for="h in history"
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
            </v-container>
        </v-item-group>
    </v-card>
</template>
<style scoped>
.h5{
    font-weight: bolder;
}
</style>