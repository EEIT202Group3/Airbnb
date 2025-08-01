//import './assets/main.css'

import { createApp } from 'vue'

import App from './App.vue'
const app = createApp(App)

//router
import router from './router'
app.use(router)


// vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
 // Ensure you are using css-loader

const vuetify = createVuetify({
  
  components,
  directives,
})

app.use(vuetify)

//pinia
import { createPinia } from 'pinia'
app.use(createPinia())


app.mount('#app')
