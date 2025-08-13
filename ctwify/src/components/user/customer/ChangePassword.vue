<script setup lang="ts">
import { ref,computed } from 'vue';
import { updateCustomerInfo } from '@/service/user/customerService';
const newPassword = ref('')
const checkPassword = ref('')

//切換密碼顯示或隱藏
const show = ref(false)
//必填規則
const required  = (v: string | boolean) => (!!v || v === true) || '必填'
//密碼的規則
const signupRules = computed(() => {
  const pwd = newPassword.value
  return {
    length: pwd.length >= 8,
    lower: /[a-z]/.test(pwd),
    upper: /[A-Z]/.test(pwd),
    number: /\d/.test(pwd),
    special: /[!@#$%^&*()_+\-={}[\]|:;"'<>,.?/`~]/.test(pwd),
  }
})
//用來顯示及時確認密碼規則
const signupChecklist = computed(() => ([
  { key: 'length', label: '至少 8 碼', ok: signupRules.value.length },
  { key: 'lower',  label: '至少 1 個小寫字母', ok: signupRules.value.lower },
  { key: 'upper',  label: '至少 1 個大寫字母', ok: signupRules.value.upper },
  { key: 'number', label: '至少 1 個數字', ok: signupRules.value.number },
  { key: 'special',label: '至少 1 個特殊符號', ok: signupRules.value.special },
]))
const passwordOk = computed(() => signupChecklist.value.every(i => i.ok))

function submit(){
    if(newPassword.value===checkPassword.value){
        const payload = {password:newPassword.value};
        const response = updateCustomerInfo(payload);
        if(response){
            alert('密碼更新成功')
        }
    }else{
        alert('密碼不一致，請重新輸入')
    }
}
</script>
<template>
    <v-container class="d-flex align-center justify-center" style="margin-top: 80px;">
        <v-card class="rounded-xl pa-8" width="360">
            <v-form validate-on="input">
                <!-- 為了使用vuetify提供的表單驗證功能，這邊的:rules一定要加 -->
                <v-text-field
                v-model="newPassword"
                label="請輸入密碼"
                :type="show ? 'text' : 'password'"
                :append-inner-icon="show ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="show = !show"
                prepend-inner-icon="mdi-lock"
                variant="solo"
                density="comfortable"
                rounded="lg"
                :rules="[required]"
                class="mb-1"
                />
                <v-text-field
                v-model="checkPassword"
                label="請再次輸入密碼"
                :type="show ? 'text' : 'password'"
                :append-inner-icon="show ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                @click:append-inner="show = !show"
                prepend-inner-icon="mdi-lock"
                variant="solo"
                density="comfortable"
                rounded="lg"
                :rules="[required]"
                class="mb-1"
                />
                <div class="mb-3">
                    <div v-for="item in signupChecklist" :key="item.key" class="d-flex align-center mb-1">
                        <v-icon :icon="item.ok ? 'mdi-check-circle' : 'mdi-close-circle'"
                                :class="item.ok ? 'text-success' : 'text-medium-emphasis'"
                                size="18" class="mr-2" />
                        <span class="text-caption">{{ item.label }}</span>
                    </div>
                </div>
                <v-divider class="my-6" />
                <v-btn @click="submit" :disabled="!passwordOk" style="background-color: orange; color: white;">確認修改</v-btn>
            </v-form>
        </v-card>
    </v-container>
    
</template>
<style scoped>
</style>