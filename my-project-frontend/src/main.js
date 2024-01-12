import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router";
import axios from "axios";
import { createPinia } from "pinia";
import "echarts";
import ECharts from 'vue-echarts';


import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL="http://47.115.219.222:8088"

const app=createApp(App)

app.use(createPinia())
app.use(router)

router.beforeEach((to, from, next) => {
    /* 路由发生变化修改页面title */
    if (to.meta.title) {
        document.title = to.meta.title
    }
    next()
})

app.component('v-chart', ECharts)

app.mount('#app')
