<template>
    <v-container>
        <v-row class="border-xl mt-10">
      <ListCard
        v-for="l in listings"
        :index="l.id"
        :list="l"
      ></ListCard>
    </v-row>
    </v-container>
</template>

<script setup>
import { ref,onMounted } from 'vue'
import axios from 'axios'
import ListCard from '@/components/listing/ListCard.vue';

const hostId = "8D7CC70F-6369-4A41-9A58-05F97ABFB688"
const listings = ref({})

onMounted (async () => {
    const res = await axios.get(`http://localhost:8080/listings/host/8D7CC70F-6369-4A41-9A58-05F97ABFB688`,
    {withCredentials:true})
    listings.value = res.data
    console.log(res.data);
} )

</script>

<style></style>
