<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllCustomers } from '@/service/user/CustomerService';
import { suspendService,permission } from '@/service/user/CustomerService';
import { useRouter } from 'vue-router';
import { findLike } from '@/service/user/CustomerService';
const router = useRouter();
const customers = ref([]);
const keyword = ref(null);
const context = ref(null);

const options = [
    {title:'郵件',value:'email'},
    {title:'使用者',value:'username'},
    {title:'電話',value:'phone'},
    {title:'全部',value:'reset'},
];
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
        await search();
    }else{
        alert('更新失敗')
    }
}



async function search(){
    if(keyword.value==='reset'||keyword.value===null){
        customers.value = await getAllCustomers();
        return;
    }
    if(keyword.value===null){
        alert('請選擇搜尋條件');
        return;
    }else if(context.value===null){
        alert('請輸入資料')
        return;
    }else{
        const data = await findLike(keyword.value,context.value);
        if(!data){
            alert('查無結果');
            return;
        }
        customers.value = data;
    }
}

const suspendDialog = ref(false)
const suspendReason = ref('')
const suspendEmail = ref('')

function openSuspendDialog(email: string) {
  suspendEmail.value = email;
  suspendDialog.value = true;
}


async function suspend(){
    try {
        await suspendService(suspendReason.value,suspendEmail.value)
        alert('更新成功')
        await search();
        suspendDialog.value = false
    } catch (error) {
        alert('更新失敗')   
        suspendDialog.value = false
    }
}

function testData(){
    suspendReason.value='多次發表不當言論、多次違反平台規範、惡意給予不實評價、檢舉次數過多且屬實'
}


</script>
<template>
    <h1>客戶清單</h1>
    <v-row>
        <v-col cols="2" style="padding-right: 0px;margin-right: 0px;">
            <v-select
                v-model="keyword"
                label="搜尋條件"
                :items="options"
                item-title="title"
                item-value="value"
                variant="solo"
            ></v-select>
        </v-col>
        <v-col cols="8" style="padding-left: 0px;margin-left: 0px;">
            <v-text-field 
                v-model="context"
                label="輸入"
                variant="solo"
                required
            ></v-text-field>
        </v-col>
        <v-col cols="2">
            <v-icon icon="mdi-magnify" @click="search()" style="margin-top: 18px;"></v-icon>
        </v-col>

    </v-row>
    <v-table v-if="!(customers===null)">
        <thead>
            <tr>
                <th>email</th>
                <th>username</th>
                <th>phone</th>
                <th>createAt</th>
                <th>isActive</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="customer in customers">
                <td>{{ customer.email }}</td>
                <td>{{ customer.username }}</td>
                <td>{{ customer.phone }}</td>
                <td>{{ customer.createAt }}</td>
                <td v-if="customer.active" style="color: green;">啟用中</td>
                <td v-else style="color: red;">停權中</td>
                <td v-if="customer.active"><v-btn color="red" @click="openSuspendDialog(customer.email)">停權</v-btn></td>
                <td v-else><v-btn color="green" @click="updatePermission(customer.active,customer.email)">啟用</v-btn></td>
            </tr>
        </tbody>
    </v-table>
    <v-dialog v-model="suspendDialog">
        <v-card class="pa-6 mx-auto" style="width: 25%; height: 300px">
        <form @submit.prevent="suspend()">
            <v-textarea
            label="停權原因"
            variant="outlined"
            prepend-icon="mdi-block-helper"
            v-model="suspendReason"
            required
            ></v-textarea>
            <v-btn type="submit">送出</v-btn>
            <v-btn @click="testData" class="ml-5">測試資料</v-btn>
        </form>
        </v-card>
    </v-dialog>
</template>
<style scoped>
</style>