<script setup lang="ts">
import { ref,onMounted } from 'vue';
import { getAllHosts,permission,findLike } from '@/service/user/HostService';
import { useRouter } from 'vue-router';
const router = useRouter();
const hosts = ref([]);

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
        await search();
    }else{
        alert('更新失敗')
    }
}
async function search(){
    if(keyword.value==='reset'||keyword.value===null){
        hosts.value = await getAllHosts();
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
        hosts.value = data;
    }

}

</script>
<template>
    <h1>房東清單</h1>
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
    <v-table v-if="!(hosts===null)">
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
            <tr v-for="host in hosts">
                <td>{{ host.email }}</td>
                <td>{{ host.username }}</td>
                <td>{{ host.phone }}</td>
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