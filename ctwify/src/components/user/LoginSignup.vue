<script setup lang="ts">
import { ref, computed } from 'vue'
import { loginService,signupService } from '@/service/user/customerService'

//用來切換登入、註冊，0登入1註冊
const tabIndex = ref(0)

//為了在呼叫api之前先做驗證(就是下面的.validate())，所以需要這兩個響應式來存表單
const loginFormRef = ref()
const registerFormRef = ref()

//紀錄登入資訊的響應式
const login = ref({ email: '', password: '' })
const register = ref({ username: '', email: '', password: '', phone: '' })

//登入、註冊的規則
const required  = (v: string | boolean) => (!!v || v === true) || '必填'
const emailRule = (v: string) => /^\S+@\S+\.\S+$/.test(v) || 'Email 格式不正確'
const phoneRuleTW = (v: string) => /^09\d{8}$/.test(v) || '電話格式不正確'

//密碼的規則
const signupRules = computed(() => {
  const pwd = register.value.password
  return {
    length: pwd.length >= 8,
    lower: /[a-z]/.test(pwd),
    upper: /[A-Z]/.test(pwd),
    number: /\d/.test(pwd),
    special: /[!@#$%^&*()_+\-={}[\]|:;"'<>,.?/`~]/.test(pwd),
  }
})
//切換密碼顯示或隱藏
const show = ref(false)

//用來顯示及時確認密碼規則
const signupChecklist = computed(() => ([
  { key: 'length', label: '至少 8 碼', ok: signupRules.value.length },
  { key: 'lower',  label: '至少 1 個小寫字母', ok: signupRules.value.lower },
  { key: 'upper',  label: '至少 1 個大寫字母', ok: signupRules.value.upper },
  { key: 'number', label: '至少 1 個數字', ok: signupRules.value.number },
  { key: 'special',label: '至少 1 個特殊符號', ok: signupRules.value.special },
]))
const registerAllPass = computed(() => signupChecklist.value.every(i => i.ok))

//登入成功之後要讓父元件把登入介面關掉
const emit = defineEmits(['login-success'])


//提交登入資訊
async function onLogin() {
  //這邊的.validate()是vuetify提供的表單驗證功能，會去讀每個欄位上用:rules定義的驗證方法(所以上面才要先設定那些方法)
  const ok = await (loginFormRef.value as any)?.validate()
  if (!ok.valid) return
  console.log('login payload:', login.value)
  const response = loginService(login.value)
  console.log(response)
  alert('登入成功，檢查cookie有沒有東西')
  emit('login-success')
}

//提交註冊資訊
async function onRegister() {
  const ok = await (registerFormRef.value as any)?.validate()
  if (!ok.valid || !registerAllPass.value) return
  const response = signupService(register.value)
  console.log(response)
  alert('註冊成功，看看有沒有驗證信')
}

//忘記密碼
async function forgetPassword(){
    alert('請串接忘記密碼api')
}
</script>

<template>
    <v-card class="auth-card rounded-xl pa-8" width="360">
      <div class="text-h5 text-center font-weight-bold mb-6">Login Form</div>

      <!-- 上方切換 -->
      <div class="toggle-wrap mb-6">
        <v-btn-toggle v-model="tabIndex" class="rounded-pill w-100" mandatory>
          <v-btn class="toggle-btn" rounded="pill" :class="{ 'is-active': tabIndex===0 }">Login</v-btn>
          <v-btn class="toggle-btn" rounded="pill" :class="{ 'is-active': tabIndex===1 }">Signup</v-btn>
        </v-btn-toggle>
      </div>

      <!-- Login -->
      <v-form v-if="tabIndex===0" ref="loginFormRef" validate-on="input">
        <!-- 為了使用vuetify提供的表單驗證功能，這邊的:rules一定要加 -->
        <v-text-field
          v-model="login.email"
          label="Email Address"
          prepend-inner-icon="mdi-email"
          variant="solo"
          density="comfortable"
          rounded="lg"
          :rules="[required, emailRule]" 
          class="mb-3"
        />
        <v-text-field
          v-model="login.password"
          label="Password"
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

        <div class="text-caption mb-4">
          <a href="#" @click.prevent="forgetPassword()" style="color:	#FF8000;">Forgot password?</a>
        </div>

        <v-btn block class="gradient-btn mb-4" rounded="lg" size="large" @click="onLogin">
          Login
        </v-btn>
      </v-form>

      <!-- Signup -->
      <v-form v-else ref="registerFormRef" validate-on="input">
        <v-text-field
          v-model="register.username"
          label="Username"
          prepend-inner-icon="mdi-account"
          variant="solo"
          density="comfortable"
          rounded="lg"
          :rules="[required]"
          class="mb-3"
        />
        <v-text-field
          v-model="register.email"
          label="Email Address"
          prepend-inner-icon="mdi-email"
          variant="solo"
          density="comfortable"
          rounded="lg"
          :rules="[required, emailRule]"
          class="mb-3"
        />
        <v-text-field
          v-model="register.phone"
          label="Phone"
          prepend-inner-icon="mdi-account"
          variant="solo"
          density="comfortable"
          rounded="lg"
          :rules="[required,phoneRuleTW]"
          class="mb-3"
        />
        <v-text-field
          v-model="register.password"
          label="Password"
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

        <!-- 密碼規則即時打勾 -->
        <div class="mb-3">
          <div v-for="item in signupChecklist" :key="item.key" class="d-flex align-center mb-1">
            <v-icon :icon="item.ok ? 'mdi-check-circle' : 'mdi-close-circle'"
                    :class="item.ok ? 'text-success' : 'text-medium-emphasis'"
                    size="18" class="mr-2" />
            <span class="text-caption">{{ item.label }}</span>
          </div>
        </div>

        <v-btn
          block
          class="gradient-btn"
          rounded="lg"
          size="large"
          :disabled="!registerAllPass"
          @click="onRegister"
        >
          CREATE ACCOUNT
        </v-btn>
      </v-form>
    </v-card>
</template>

<style scoped>
.auth-card {
  box-shadow: 0 20px 40px rgba(0,0,0,.08);
}

/* 上方圓角切換 */
.toggle-wrap :deep(.v-btn-toggle) {
  background: #e9eef7;
  padding: 4px;
}
.toggle-btn {
  flex: 1 1 50%;
  text-transform: none;
  font-weight: 600;
}
.toggle-btn.is-active {
  color: white !important;
  background-color:	#FF8000 !important;
}

/* 主要送出按鈕 */
.gradient-btn {
  color: white !important;
  background-color:	#FF8000 !important;
}
</style>