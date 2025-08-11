<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { findMe,updateCustomerInfo,updateAvatar } from '@/service/user/customerService'
import { useRouter } from 'vue-router'
const router = useRouter()
const user = ref(null)
const avatarActive = ref(false)
const avatar = ref(null)

type FieldItem = { icon: string; title: string; key: string; value: any }
const items = ref<FieldItem[]>([])

const dialogActive = ref(false)
const selected = ref<FieldItem | null>(null)
const tempValue = ref('')
const updateData = ref<{key:string,value:any}[]>([])

function addUpdateData(item:any, key:string, value:any) {
  const i = updateData.value.findIndex(x => x.key === key)
  if (i >= 0) updateData.value[i].value = value
  else updateData.value.push({ key, value })

  item.value = value
  tempValue.value = ''
  console.log(updateData.value)
  dialogActive.value = false
}

function openDialog(item: FieldItem) {
  selected.value = item
  tempValue.value = item.value ?? ''
  dialogActive.value = true
}

async function editAvatar() {
    const files = new FormData();
    files.append('avatar',avatar.value)
    const response = await updateAvatar(files)
    if(response){
        alert('更新成功')
    }else{
        alert('失敗，重作')
    }
}

async function submit() {
    const payload = Object.fromEntries(updateData.value.map(x => [x.key, x.value]))
    const response = await updateCustomerInfo(payload);
    if(response){
        alert('更新成功');
        router.push({name:'CustomerInfo'})
    }  
}

onMounted(async () => {
  const me = await findMe()
  user.value = me

  items.value = [
    { icon: 'mdi-account-outline', title: '更改使用者名稱', key: 'username', value: me.username },
    { icon: 'mdi-phone-outline',   title: '更改電話號碼',   key: 'phone',    value: me.phone },
    { icon: 'mdi-lock-outline',    title: '更改密碼',       key: 'password', value: '' },
  ]
})
</script>

<template>
    <v-container class="py-10" style="width: 75%;">
        <!-- Avatar 與標題區 -->
        <v-row>
            <v-col cols="12" md="3" class="d-flex flex-column align-center">
                <div class="position-relative">
                    <v-avatar size="250">
                        <v-img :src="user?.avatarURL ? 'http://localhost:8080' + user.avatarURL : '../src/assets/user/account.svg'" cover />
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
                <div v-if="user">
                    <v-list class="py-0">
                        <v-list-item
                        v-for="item in items"
                        :key="item.key"
                        :to="item.key === 'password' ? { name: 'ChangePassword' } : undefined"
                        @click="item.key !== 'password' && openDialog(item)"
                        >
                        <template #prepend>
                            <v-icon :icon="item.icon" />
                        </template>
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                        </v-list-item>
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