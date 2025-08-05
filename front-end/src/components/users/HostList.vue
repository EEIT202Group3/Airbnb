<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllHosts } from '@/service/user/HostService';
const hosts = ref([]);
onMounted(async()=>{hosts.value = await getAllHosts();});
</script>
<template>
    <h1>房東清單</h1>
    <v-table v-if="!(hosts===null)">
        <thead>
            <tr>
                <th>email</th>
                <th>username</th>
                <th>createAt</th>
                <th>isActive</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="host in hosts">
                <td>{{ host.email }}</td>
                <td>{{ host.username }}</td>
                <td>{{ host.createAt }}</td>
                <td>{{ host.isActive }}</td>
            </tr>
        </tbody>
    </v-table>
</template>
<style scoped>
</style>