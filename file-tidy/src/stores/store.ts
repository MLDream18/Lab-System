import { defineStore } from 'pinia'

// 状态仓库
const useCollapseStore = defineStore('Collapse', {
    state: () => ({
        isCollapse: false,
        isAdminCollapse: false,
    }),
    actions: {
        flipCollapse() {
            this.isCollapse = !this.isCollapse
        },
        flipAdminCollapse() {
            this.isAdminCollapse = !this.isAdminCollapse
        }
    }
})

const useTaskStore = defineStore('Task', {
    state: () => ({
        task: 0
    }),
    actions: {
        addTask() {
            this.task ++;
        },
    }
})

const useUserStore = defineStore('User', {
    state: () => ({
        role: null
    }),
    actions: {
        setUser(user: any) {
            this.role = user
        }
    }
})
export { useCollapseStore, useTaskStore, useUserStore } // 导出状态仓库