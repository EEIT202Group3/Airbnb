<script setup lang="ts">
import { ref } from 'vue';
const listings = [
    {id: 101, name: '海景日落民宿', address: '台北市信義區松壽路101號'},
    {id: 102, name: '山林溫泉小屋', address: '台中市和平區谷關路88號'},
    {id: 103, name: '星空露營車', address: '花蓮縣壽豐鄉池南路55號'},
    {id: 104, name: '城市公寓', address: '高雄市前鎮區成功路200號'},
    {id: 105, name: '湖畔度假別墅', address: '南投縣魚池鄉日月村10號'}
];
const hovered = ref(null)
function getList(data:any){
    alert(data.id+data.name+data.address)
}

</script>
<template>
    <v-card class="pa-8 elevation-2 rounded-xl mx-auto">
        <h1 style="font-weight: bolder;">我的房源</h1>
        <v-divider class="my-6" />
        <v-item-group mandatory>
            <v-container>
                <v-row>
                    <v-col
                    v-for="l in listings"
                    cols="12"
                    md="4"
                    >
                    <v-item>
                        <v-card
                        class="d-flex flex-column justify-center align-center"
                        height="200"
                        dark
                        @click="getList(l)"
                        @mouseover="hovered = l.id"
                        @mouseleave="hovered = null"
                        :style="hovered === l.id ? 'background-color: #FFDCB9;' : ''"
                        >
                        <div>{{ l.id }}</div>
                        <div>{{ l.name }}</div>
                        <div>{{ l.address }}</div>
                        </v-card>
                    </v-item>
                    </v-col>
                </v-row>
            </v-container>
        </v-item-group>
    </v-card>
</template>
<style scoped>
.div{
    font-weight: bolder;
}

</style>