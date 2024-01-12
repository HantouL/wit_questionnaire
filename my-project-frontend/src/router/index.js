import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "@/net";
import {useStore} from "@/store";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: ()=>import('@/components/views/WelcomeView.vue'),
            meta:{
                // 页面标题title
                title: '欢迎使用武汉工程大学问卷调查系统'
            },
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: ()=>import('@/components/views/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: ()=>import('@/components/views/welcome/RegisterPage.vue')
                },
                {
                    path: 'reset',
                    name: 'welcome-reset',
                    component: ()=>import('@/components/views/welcome/ResetPage.vue')
                }
            ]
        },
        {
            path: '/index',
            name: 'index',
            redirect: '/index/questionnaire-filling',
            component: ()=>import('@/components/views/IndexView.vue'),
            meta:{
                // 页面标题title
                title: '首页-武工大问调查系统'
            },
            children:[
                {
                    path: 'user-setting',
                    name: 'user-setting',
                    component: ()=>import('@/components/views/settings/UserSetting.vue'),
                    meta:{
                        // 页面标题title
                        title: '用户设置-武工大问调查系统'
                    },
                },
                {
                    path: 'user-management',
                    name: 'user-management',
                    component: () => import('@/components/views/settings/UserManagement.vue'),
                    meta:{
                        // 页面标题title
                        title: '用户管理/问卷查询-武工大问调查系统'
                    },
                    beforeEnter(to,from,next){
                        const store = useStore();
                        if(store.user.role === 'admin'||store.user.role === 'superAdmin'){
                            next();
                        }else{
                            next('/index')
                        }
                    }
                },
                {
                    path: 'user-answer/:userId/:userName',
                    name: 'user-answer',
                    component: ()=>import('@/components/views/settings/UserAnswers/UserAnswers.vue'),
                    meta:{
                        // 页面标题title
                        title: '用户问卷填写情况-武工大问调查系统'
                    },
                },
                {
                    path: 'questionnaire-checking',
                    name: 'questionnaire-checking',
                    meta:{
                        // 页面标题title
                        title: '我填写的问卷-武工大问调查系统'
                    },
                    component: ()=> import('@/components/views/questionnaire/MyWrittenQuestionnaire.vue')
                },
                {
                    path: 'questionnaire-filling',
                    name: 'questionnaire-filling',
                    meta:{
                        // 页面标题title
                        title: '填写问卷-武工大问调查系统'
                    },
                    component: ()=> import('@/components/views/questionnaire/FillingQuestionnaire.vue'),
                },
                {
                    path: 'questionnaire-releasing',
                    name: 'questionnaire-releasing',
                    meta:{
                        // 页面标题title
                        title: '我发布的问卷-武工大问调查系统'
                    },
                    component: ()=> import('@/components/views/questionnaire/MyPostedQuestionnaire.vue'),
                    beforeEnter(to,from,next){
                        const store = useStore();
                        if(store.user.role === 'admin'||store.user.role === 'superAdmin'){
                            next();
                        }else{
                            next('/index')
                        }
                    }
                },
                {
                    path: 'questionnaire-statistics/:questionnaireId',
                    name: 'questionnaire-statics',
                    component: ()=>import('@/components/views/questionnaire/QuestionnaireStatistic/QuestionnaireStatistic.vue'),
                    meta:{
                        // 页面标题title
                        title: '问卷数据统计-武工大问调查系统'
                    },
                },
                {
                    path: 'test',
                    name: 'test',
                    component: ()=>import('@/components/views/questionnaire/test.vue')
                }
            ]
        }
    ]
})

//添加路由守卫,防止非法访问
router.beforeEach((to,from,next)=>{
    const isUnauthorized = unauthorized()
    if(to.name.startsWith('welcome-')&&!isUnauthorized){//已登录用户访问登录页面
        next('/index')
    }else if(to.fullPath.startsWith('/index') && isUnauthorized){//未登录用户访问index页面
        next('/')
    }else{
        next();
    }
})

export default router