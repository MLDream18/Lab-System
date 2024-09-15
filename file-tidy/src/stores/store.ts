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
        role: null as string | null,
    }),
    actions: {
        setUser(user: any) {
            this.role = user
        }
    }
})

const useWebSocketStore = defineStore('WebSocket', {
    state: () => ({
        wsSp: {
            ws: null as any,
            data: null as any,
            message: null as any,
        },
        wsRecord: {
            ws: null as any,
            data: null as any,
        },
        wsClassSchedule: {
            ws: null as any,
            data: null as any,
        },
        wsSuggestion: {
            ws: null as any,
            data: null as any,
        },
    }),
    actions: {
        setWsSpWs(ws: any) {
            this.wsSp.ws = ws
        },
        setWsSpData(data: any) {
            this.wsSp.data = data
        },
        setWsSpMessage(message: any) {
            this.wsSp.message = message
        },
        setWsRecordWs(ws: any) {
            this.wsRecord.ws = ws
        },
        setWsRecordData(data: any) {
            this.wsRecord.data = data
        },
        setWsClassScheduleWs(ws: any) {
            this.wsClassSchedule.ws = ws
        },
        setWsClassScheduleData(data: any) {
            this.wsClassSchedule.data = data
        },
        setWsSuggestionWs(ws: any) {
            this.wsSuggestion.ws = ws
        }, 
        setWsSuggestionData(data: any) {
            this.wsSuggestion.data = data
        },
        clearWs() {
            this.wsSp.ws?.close();

            this.wsRecord.ws?.close();

            this.wsClassSchedule.ws?.close();

            this.wsSuggestion.ws?.close();

            this.wsSp.ws = null
            this.wsSp.data = null
            this.wsSp.message = null
            this.wsRecord.ws = null
            this.wsRecord.data = null
            this.wsClassSchedule.ws = null
            this.wsClassSchedule.data = null
            this.wsSuggestion.ws = null
            this.wsSuggestion.data = null
        }
    }
})

const useWebSocketStoreTeacher = defineStore('WebSocketTeacher', {
    state: () => ({
        wsClassSchedule: {
            ws: null as any,
            data: null as any,
        },
        wsSuggestion: {
            ws: null as any,
            data: null as any,
        },
        wsApplying: {
            ws: null as any,
            data: null as any,
        },
        wsHistory: {
            ws: null as any,
            data: null as any,
        }
    }),
    actions: {
        setWsClassScheduleWs(ws: any) {
            this.wsClassSchedule.ws = ws
        },
        setWsClassScheduleData(data: any) {
            this.wsClassSchedule.data = data
        },
        setWsSuggestionWs(ws: any) {
            this.wsSuggestion.ws = ws
        },
        setWsSuggestionData(data: any) {
            this.wsSuggestion.data = data
        },
        setWsApplyingWs(ws: any) {
            this.wsApplying.ws = ws
        },
        setWsApplyingData(data: any) {
            this.wsApplying.data = data
        },
        setWsHistoryWs(ws: any) {
            this.wsHistory.ws = ws
        },
        setWsHistoryData(data: any) {
            this.wsHistory.data = data
        },
        clearWs() {
            this.wsClassSchedule.ws?.close();

            this.wsSuggestion.ws?.close();

            this.wsApplying.ws?.close();

            this.wsHistory.ws?.close();

            this.wsClassSchedule.ws = null
            this.wsClassSchedule.data = null
            this.wsSuggestion.ws = null
            this.wsSuggestion.data = null
            this.wsApplying.ws = null
            this.wsApplying.data = null
            this.wsHistory.ws = null
            this.wsHistory.data = null
        }
    }
})

const useBasicData = defineStore('BasicData', {
    state: () => ({
        adminBasicData: '',
        teacherBasicData: '',
    }),
    actions: {
        setAdminBasicData(data: string) {
            console.log(111)
            this.adminBasicData = data
        },
        setTeacherBasicData(data: string) {
            console.log(222)
            this.teacherBasicData = data
        }
    }
})

export { useCollapseStore, useTaskStore, useUserStore, useWebSocketStore, useWebSocketStoreTeacher, useBasicData } // 导出状态仓库