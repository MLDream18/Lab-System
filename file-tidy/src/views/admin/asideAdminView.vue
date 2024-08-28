<template>
    <el-aside class="aside"
        :style="{ 'width': !collapse.isAdminCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': '#303133', 'position': 'fixed', }">
        <el-menu :style="{ 'width': 'calc(100% + 1px)' }" background-color="#303133" :default-active="deactivate"
            active-text-color="black" text-color="#fff" :collapse="collapse.isAdminCollapse"
            :collapse-transition="false">
            <div @click="changeMenu('1')">
                <el-menu-item index="1">
                    <div v-if="deactivate === '1'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            待审批
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            待审批
                        </span>
                        <div v-if="taskStore.task > 0"
                            style="width: 20px; height: 20px; position: absolute; top: 10%; right: 5%; background-color: rgb(245, 108, 108); border-radius: 50%; display: inline-flex; justify-content: center; align-items: center; font-size: 12px; color: white; font-weight: bold;">
                            <span>{{ taskStore.task }}</span>
                        </div>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('2')">
                <el-menu-item index="2">
                    <div v-if="deactivate === '2'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <DocumentCopy />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            实验项目
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <DocumentCopy />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            实验项目
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('3')">
                <el-menu-item index="3">
                    <div v-if="deactivate === '3'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <Calendar />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            课程表
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <Calendar />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            课程表
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('4')">
                <el-menu-item index="4">
                    <div v-if="deactivate === '4'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            教师建议
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isAdminCollapse">
                            教师建议
                        </span>
                    </div>
                </el-menu-item>
            </div>
        </el-menu>
        <el-icon size="30" style="width: 30px; cursor: pointer; border: 1px solid white; border-radius: 10px;" color="white"
            @click="collapse.flipAdminCollapse">
            <Fold v-if="!collapse.isAdminCollapse" />
            <Expand v-if="collapse.isAdminCollapse" />
        </el-icon>
    </el-aside>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useCollapseStore } from '../../stores/store';
import { useTaskStore } from '../../stores/store';
import router from '../../router';
import debounce from 'lodash/debounce';

const taskStore = useTaskStore();
const deactivate = ref('1');
const collapse = useCollapseStore();

router.beforeEach((to, _from, next) => {
    if (to.path === '/404' || to.path === '/login' || to.path === '/register' || to.path === '/' || (to.name?.toString().includes('admin') && `${localStorage.getItem('role')}` === 'admin') || (to.name?.toString().includes('teacher') && `${localStorage.getItem('role')}` === 'teacher')) {
        next();
    } else {
        next({ path: '/404' });
    }
});

router.afterEach((to, _from) => {
    switch (to.path.toString()) {
        case '/pending-approval':
            taskStore.task = 0;
            deactivate.value = '1';
            break;
        case '/experiment-project':
            deactivate.value = '2';
            break;
        case '/class-schedule':
            deactivate.value = '3';
            break;
        case '/suggest':
            deactivate.value = '4';
            break;
        default:
            break;
    }
});

const changeMenu = debounce((index: string) => {
    switch (index) {
        case '1':
            router.push({ path: '/pending-approval' });
            break;
        case '2':
            router.push({ path: '/experiment-project' });
            break;
        case '3':
            router.push({ path: '/class-schedule' });
            break;
        case '4':
            router.push({ path: '/suggest' });
            break;
        default:
            break;
    }
});

switch (router.currentRoute.value.fullPath) {
    case '/pending-approval':
        taskStore.task = 0;
        deactivate.value = '1';
        break;
    case '/experiment-project':
        deactivate.value = '2';
        break;
    case '/class-schedule':
        deactivate.value = '3';
        break;
    case '/suggest':
        deactivate.value = '4';
        break;
    default:
        break;
}

</script>

<style></style>