<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllCustomers } from '@/service/user/CustomerService';
const customers = ref([]);
onMounted(async()=>{customers.value = await getAllCustomers();});
</script>
<template>
    <h1>客戶清單</h1>
    <v-table v-if="!(customers===null)">
        <thead>
            <tr>
                <th>email</th>
                <th>username</th>
                <th>createAt</th>
                <th>isActive</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="customer in customers">
                <td>{{ customer.email }}</td>
                <td>{{ customer.username }}</td>
                <td>{{ customer.createAt }}</td>
                <td>{{ customer.isActive }}</td>
            </tr>
        </tbody>
    </v-table>
</template>
<style scoped>
</style>