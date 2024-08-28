import { createApp } from 'vue'
// import './style.css'
import App from './App.vue'
import ElementPlus, { ElMessage } from 'element-plus'
import { dragTable } from './utils/common'
import 'element-plus/dist/index.css'
import router from './router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { createPinia } from 'pinia'
import 'vue-simple-uploader/dist/style.css'
import axios, { AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
const pinia = createPinia();
// 全局方法挂载
app.config.globalProperties.$dragTable = dragTable;
// 请求拦截器
axios.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['token'] = `${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
axios.interceptors.response.use(
    (response: AxiosResponse) => {
        if(response.data.code === 0) {
            if(response.data.msg !== 'NOT_LOGIN') {
                /* 不跳转 */
                ElMessage.error(response.data.msg);
                return Promise.reject(response);
            }
            ElMessage.error(response.data.msg);
            router.push('/login');
        }
        return response;
    }
);


app
    .use(ElementPlus, {
        locale: zhCn
    })
    .use(router)
    .use(pinia)
    .mount('#app')