import {createRouter, createWebHistory} from 'vue-router'

// 定义路由配置
const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/login/loginView.vue'),
        meta: {
            keepAlive: false,
            title: '登录',
        }
    },
    {
        path: '/register',
        name:'register',
        component: () => import('../views/login/registerView.vue'),
        meta: {
            keepAlive: false,
            title: '注册',
        }
    },
    {
        path: '/pending-approval',
        name: 'admin-pending-approval',
        component: () => import('../views/admin/approve/dspView.vue'),
        meta: {
            keepAlive: true,
            title: '待审批'
        }
    },
    {
        path: '/approved-list',
        name: 'admin-approved-list',
        component: () => import('../views/admin/approve/yspView.vue'),
        meta: {
            keepAlive: true,
            title: '已审批'
        }
    },
    {
        path: '/experiment-project',
        name: 'admin-experiment-project',
        component: () => import('../views/admin/look/experimentView.vue'),
        meta: {
            keepAlive: true,
            title: '实验项目'
        }
    },
    {
        path: '/class-schedule',
        name: 'admin-class-schedule',
        component: () => import('../views/admin/look/classScheduleView.vue'),
        meta: {
            keepAlive: true,
            title: '课程表'
        }
    },
    {
        path: '/suggest',
        name: 'admin-suggest',
        component: () => import('../views/admin/look/suggestView.vue'),
        meta: {
            keepAlive: true,
            title: '教师建议'
        }
    },
    {
        path: '/apply',
        name: 'teacher-apply',
        component: () => import('../views/teacher/lab-apply/applyView.vue'),
        meta: {
            keepAlive: true,
            title: '实验室申请'
        }
    },
    {
        path: '/applyFill',
        name: 'teacher-applyFill',
        component: () => import('../views/teacher/lab-apply/applyFillView.vue'),
        meta: {
            keepAlive: true,
            title: '实验室申请信息填写'
        }
    },
    {
        path: '/applying',
        name: 'teacher-applying',
        component: () => import('../views/teacher/lab-apply/applyingView.vue'),
        meta: {
            keepAlive: true,
            title: '申请记录'
        }
    },
    {
        path: '/history-apply',
        name: 'teacher-history-apply',
        component: () => import('../views/teacher/lab-apply/historyView.vue'),
        meta: {
            keepAlive: true,
            title: '历史申请'
        }
    },
    {
        path: '/experiment-project-entry',
        name: 'teacher-experiment-project-entry',
        component: () => import('../views/teacher/entry/experimentView.vue'),
        meta: {
            keepAlive: true,
            title: '实验项目录入'
        }
    },
    {
        path: '/suggest-entry',
        name: 'teacher-suggest-entry',
        component: () => import('../views/teacher/entry/suggestView.vue'),
        meta: {
            keepAlive: true,
            title: '教师建议填写'
        }
    },
    {
        path: '/404',
        name: '404',
        component: () => import('../views/error/404.vue'),
        meta: {
            keepAlive: false,
            title: '404'
        }
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

// 导出路由实例
export default router