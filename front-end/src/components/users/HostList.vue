<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllHosts } from '@/service/user/HostService';
import { permission } from '@/service/user/HostService';
import { useRouter } from 'vue-router';
const router = useRouter();
const hosts = ref([]);
onMounted(
    async()=>{
        const result = await getAllHosts();
        if(!result){
            router.push({name:'Homepage'});
        }else{
            hosts.value = result;
        }
    }
);
async function updatePermission(active,email){
    let status = '';
    if(active){
        status = 'SUSPEND';
    }else{
        status = 'ACTIVE';
    }
    const result = await permission(status,email);
    if(result.message==='success'){
        alert('更新成功')
        hosts.value = await getAllHosts();
    }else{
        alert('更新失敗')
    }
}
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
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="host in hosts">
                <td>{{ host.email }}</td>
                <td>{{ host.username }}</td>
                <td>{{ host.createAt }}</td>
                <td v-if="host.active" style="color: green;">啟用中</td>
                <td v-else style="color: red;">停權中</td>
                <td v-if="host.active"><v-btn color="red" @click="updatePermission(host.active,host.email)">停權</v-btn></td>
                <td v-else><v-btn color="green" @click="updatePermission(host.active,host.email)">啟用</v-btn></td>
            </tr>
        </tbody>
    </v-table>
</template>
<style scoped>
</style>