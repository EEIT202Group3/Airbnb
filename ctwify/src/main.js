import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

import vue3GoogleLogin from 'vue3-google-login'

const app = createApp(App)

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
    },
})


app.use(createPinia())
app.use(router)
app.use(vuetify)
app.use(vue3GoogleLogin, {
    clientId: '784447494371-8fmribkj12vq9mpsotjopnkk8g8ob29s.apps.googleusercontent.com'
})

app.mount('#app')
