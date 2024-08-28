<template>
    <el-aside class="aside"
        :style="{ 'width': !collapse.isCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': '#303133', 'position': 'fixed', }">
        <el-menu :style="{ 'width': 'calc(100% + 1px)' }" background-color="#303133" :default-active="deactivate"
            active-text-color="black" text-color="#fff" :collapse="collapse.isCollapse" :collapse-transition="false">
            <div @click="changeMenu('1')">
                <el-menu-item index="1">
                    <div v-if="deactivate === '1'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验项目录入
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验项目录入
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('2')">
                <el-menu-item index="2">
                    <div v-if="deactivate === '2'"
                        style="display: inline-flex; justify-content: center; align-items: center;
								width: auto; height: 50%; padding: 2.5%; border: 1px solid white; border-radius: 10px; background-color: white;">
                        <el-icon style="margin-left: 3px;" size="25">
                            <School />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室申请
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <School />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室申请
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
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            申请记录
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            申请记录
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
                            <Promotion />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室环境需求
                        </span>
                    </div>
                    <div v-else>
                        <el-icon style="margin-left: 3px;" size="25">
                            <Promotion />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室环境需求
                        </span>
                    </div>
                </el-menu-item>
            </div>
        </el-menu>
        <el-icon size="30" style="width: 30px; cursor: pointer; border: 1px solid white; border-radius: 10px;" color="white"
            @click="collapse.flipCollapse">
            <Fold v-if="!collapse.isCollapse" />
            <Expand v-if="collapse.isCollapse" />
        </el-icon>
    </el-aside>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useCollapseStore } from '../../stores/store';
import { School } from '@element-plus/icons-vue';
import debounce from 'lodash/debounce';
import router from '../../router';

const collapse = useCollapseStore();
const deactivate = ref('2');

router.beforeEach((to, _from, next) => {
    if (to.path === '/404' || to.path === '/login' || to.path === '/register' || to.path === '/' || (to.name?.toString().includes('admin') && `${localStorage.getItem('role')}` === 'admin') || (to.name?.toString().includes('teacher') && `${localStorage.getItem('role')}` === 'teacher')) {
        next();
    } else {
        next({ path: '/404' });
    }
});

router.afterEach((to, _from) => {
    switch (to.path.toString()) {
        case '/experiment-project-entry':
            deactivate.value = '1';
            break;
        case '/apply':
            deactivate.value = '2';
            break;
        case '/applying':
            deactivate.value = '3';
            break;
        case '/history-apply':
            deactivate.value = '3';
            break;
        case '/suggest-entry':
            deactivate.value = '4';
            break;
        default:
            break;
    }
});

const changeMenu = debounce((index: string) => {
    switch (index) {
        case '1':
            router.push({ path: '/experiment-project-entry' });
            break;
        case '2':
            router.push({ path: '/apply' });
            break;
        case '3':
            router.push({ path: '/applying' });
            break;
        case '4':
            router.push({ path: '/suggest-entry' });
            break;
        default:
            break;
    }
});
switch (router.currentRoute.value.fullPath) {
    case '/experiment-project-entry':
        deactivate.value = '1';
        break;
    case '/apply':
        deactivate.value = '2';
        break;
    case '/applying':
        deactivate.value = '3';
        break;
    case '/history-apply':
        deactivate.value = '3';
        break;
    case '/suggest-entry':
        deactivate.value = '4';
        break;
    default:
        break;
}

</script>
