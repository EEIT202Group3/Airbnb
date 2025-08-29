<script setup lang="ts">
import { ref,onMounted,watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useHostStore } from '@/stores/host';
import { useRouter } from 'vue-router';
import { updateHostInfo,updateAvatar } from '@/service/host/hostService';
import defaultAvatar from '@/images/default.png';

const router = useRouter();
const hostStore = useHostStore();
const{host} = storeToRefs(hostStore);

const introActive = ref(false);

const avatarActive = ref(false);
const avatar = ref(null);

type FieldItem = { icon: string; title: string; key: string; value: any };
const items = ref<FieldItem[]>([]);

const dialogActive = ref(false);
const selected = ref<FieldItem|null>(null);
const tempValue = ref('');

const updateData = ref<{key:string,value:any}[]>([]);

function addUpdateData(item:any,key:string,value:any){
    const i = updateData.value.findIndex(x=>x.key===key)
    if(i>=0){
        updateData.value[i].value = value;
    }else{
        updateData.value.push({key,value});
    }
    item.value = value;
    tempValue.value = '';
    dialogActive.value = false;
    introActive.value = false;
}

function openDialog(item:FieldItem){
    selected.value = item
    tempValue.value = item.value ?? ''
    dialogActive.value = true
}

function openIntro(item:FieldItem){
    selected.value = item
    tempValue.value = item.value ?? ''
    introActive.value = true
}

function buildItems(){
    if(!host.value){
        items.value = []
        return
    }
    items.value = [
        { icon: 'mdi-account-outline', title: '更改使用者名稱', key: 'username', value: host.value.username },
        { icon: 'mdi-phone-outline',   title: '更改電話號碼',   key: 'phone',    value: host.value.phone },
        { icon: 'mdi-text-account',   title: '更改自我介紹',   key: 'intro',    value: host.value.intro?host.value.intro:'此房東沒有自我介紹' },
        { icon: 'mdi-lock-outline',    title: '更改密碼',       key: 'password', value: '' },
    ]
}

async function editAvatar(){
    if(!avatar.value){
        alert('請選擇照片')
        return
    }
    try {
        const files = new FormData();
        files.append('avatar',avatar.value)
        const response = await updateAvatar(files)
        if(response){
            await hostStore.fetchUser()
            alert('更新成功')
            avatarActive.value = false
            avatar.value = null
        }else{
            alert('更新失敗')
            return
        }
    } catch (error) {
        alert('更新失敗')
    }
}

async function submit(){
    const payload = Object.fromEntries(updateData.value.map(x=>[x.key,x.value]))
    try{
        await updateHostInfo(payload);
        await hostStore.fetchUser();
        alert('更新成功');
        updateData.value = [];
        router.push({name:'HostHomepage'})    
    }catch(error){
        console.log(error)
        alert('更新失敗')
    }
}

onMounted(
    async()=>{
        if(!host.value){
            await hostStore.fetchUser();
        }
        buildItems();
    }
)

watch(host,()=>buildItems())
</script>
<template>
    <v-container class="py-10" style="width: 75%;">
        <!-- Avatar 與標題區 -->
        <v-row>
            <v-col cols="12" md="3" class="d-flex flex-column align-center">
                <div class="position-relative">
                    <v-avatar size="250">
                        <v-img :src="host?.avatarURL ? 'http://localhost:8080' + host.avatarURL : defaultAvatar" cover/>
                    </v-avatar>
                    <v-btn
                        class="avatar-edit-btn"
                        size="small"
                        color="white"
                        elevation="3"
                        @click="avatarActive=true"
                    >
                        <v-icon start>mdi-camera</v-icon> 編輯
                    </v-btn>
                </div>
            </v-col>

            <v-col cols="12" md="9" style="margin-top: 20px; position:static;">
                <h2 class="text-h4 font-weight-bold mb-3">個人資訊</h2>
                <v-divider class="my-6" />
                <div v-if="host">
                    <v-list class="py-0">
                        <template v-for="item in items" :key="item.key">
                            <!-- 需要路由跳轉的 -->
                            <v-list-item
                            v-if="item.key === 'password'"
                            :to="{ name: 'HostPassword' }"
                            >
                            <template #prepend>
                                <v-icon :icon="item.icon" />
                            </template>
                            <v-list-item-title>{{ item.title }}</v-list-item-title>
                            </v-list-item>

                            <!-- 只要觸發函式的 -->
                            <v-list-item
                            v-else
                            @click="item.key === 'intro' ? openIntro(item) : openDialog(item)"
                            >
                            <template #prepend>
                                <v-icon :icon="item.icon" />
                            </template>
                            <v-list-item-title>{{ item.value }}</v-list-item-title>
                            </v-list-item>
                        </template>
                    </v-list>
                                    </div>
                <div v-else>找不到資料</div>
                <v-divider class="my-6" />
                <v-btn style="background-color: orange; color: white;" @click="submit">完成</v-btn>
            </v-col>
        </v-row>
    </v-container>
    <v-dialog v-model="dialogActive" max-width="520">
        <v-card>
            <v-card-title class="text-h6">
            {{ selected ? selected.title : '' }}
            </v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="tempValue"
                    :placeholder="selected?.value || '請輸入資料'"
                    clearable
                />
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn variant="text" @click="dialogActive = false">取消</v-btn>
                <v-btn color="primary" @click="addUpdateData(selected,selected.key,tempValue)">儲存</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-dialog v-model="introActive" max-width="520">
        <v-card>
            <v-card-title class="text-h6">
            {{ selected ? selected.title : '' }}
            </v-card-title>
            <v-card-text>
                <v-textarea
                    v-model="tempValue"
                    :placeholder="selected?.value || '請輸入資料'"
                    clearable
                />
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn variant="text" @click="introActive = false">取消</v-btn>
                <v-btn color="primary" @click="addUpdateData(selected,selected.key,tempValue)">儲存</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-dialog v-model="avatarActive" max-width="520">
        <v-card>
            <v-card-title class="text-h6">
            請選擇照片
            </v-card-title>
            <v-card-text>
                <v-file-input v-model="avatar" label="大頭照" accept="image/*"></v-file-input>
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn variant="text" @click="avatarActive = false">取消</v-btn>
                <v-btn color="primary" @click="editAvatar">確認</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<style scoped>
.position-relative { position: relative; }

/* Avatar 下方的小「編輯」按鈕 */
.avatar-edit-btn {
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 999px;
  padding-inline: 12px;
}

.v-list-item:hover{
    background-color:#FFF2E0;
    color:orange;
}
</style>