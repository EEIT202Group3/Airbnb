<script setup lang="ts">
const history = [
    {location:'Taipei',date:'2025/01/01',price:'2500'},
    {location:'Taipei',date:'2025/01/01',price:'2500'},
    {location:'Taipei',date:'2025/01/01',price:'2500'},
    {location:'Taipei',date:'2025/01/01',price:'2500'},
]

</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
        <h1 style="font-weight: bolder;">過去旅程</h1>
        
        <v-divider class="my-6" />

        <v-row>
            <v-col
                cols="4"
                v-for="h in history"
            >
                <v-card-item
                class="elevation-1 rounded-xl"
                style="background-color: #FFFAF4;"
                >
                    <h5>地點</h5>
                    <div>{{ h.location }}</div>
                    <h5>時間</h5>
                    <div>{{ h.date }}</div>
                    <h5>金額</h5>
                    <div>{{ h.price }}</div>
                </v-card-item>
            </v-col>
        </v-row>
    </v-card>
</template>
<style scoped>
.h5{
    font-weight: bolder;
}
</style>