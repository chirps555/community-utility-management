import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { setUnauthorizedHandler } from './api/request'
import { useAuthStore } from './stores/authStore'
import { useDataStore } from './stores/dataStore'
import './assets/global.css'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

setUnauthorizedHandler(() => {
  const authStore = useAuthStore()
  const dataStore = useDataStore()
  const role = authStore.role
  authStore.logout()
  dataStore.inited = false
  if (router.currentRoute.value.path !== '/login') {
    router.push({ path: '/login', query: { role: role || 'ADMIN' } })
  }
})

app.mount('#app')
