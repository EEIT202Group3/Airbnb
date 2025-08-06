<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllCustomers } from '@/service/user/CustomerService';
import { permission } from '@/service/user/CustomerService';
import { useRouter } from 'vue-router';
const router = useRouter();
const customers = ref([]);
onMounted(
    async()=>{
        const result = await getAllCustomers();
        if(!result){
            router.push({name:'Homepage'});
        }else{
            customers.value = result;
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
        customers.value = await getAllCustomers();
    }else{
        alert('更新失敗')
    }
}


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
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="customer in customers">
                <td>{{ customer.email }}</td>
                <td>{{ customer.username }}</td>
                <td>{{ customer.createAt }}</td>
                <td v-if="customer.active" style="color: green;">啟用中</td>
                <td v-else style="color: red;">停權中</td>
                <td v-if="customer.active"><v-btn color="red" @click="updatePermission(customer.active,customer.email)">停權</v-btn></td>
                <td v-else><v-btn color="green" @click="updatePermission(customer.active,customer.email)">啟用</v-btn></td>
            </tr>
        </tbody>
    </v-table>
</template>
<style scoped>
</style>