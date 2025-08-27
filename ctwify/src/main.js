import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'



// Google Maps
import VueGoogleMaps from '@fawmi/vue-google-maps'
import vue3GoogleLogin from 'vue3-google-login'

// 引入全局字體樣式
import './assets/global.css'


const app = createApp(App)

// Vuetify 設定
const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
  },
})

// 使用套件
app.use(createPinia())
app.use(router)
app.use(vuetify)
app.use(vue3GoogleLogin, {
  clientId: '784447494371-8fmribkj12vq9mpsotjopnkk8g8ob29s.apps.googleusercontent.com'
})

// Google Maps 設定
app.use(VueGoogleMaps, {
  load: {
       key: import.meta.env.VITE_GOOGLE_MAPS_API_KEY,

    libraries: 'places', // 如果要用地點自動補全
  },
})
app.mount('#app')
