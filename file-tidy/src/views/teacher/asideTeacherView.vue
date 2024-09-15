<template>
    <el-aside class="aside"
        :style="{ 'width': !collapse.isCollapse ? '10%' : '3.5%', 'height': '100%', 'background': 'radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%)', 'position': 'fixed' }">
        <el-menu
            :style="{ 'width': 'calc(100% + 1px)', 'background': 'radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%)', }"
            :default-active="deactivate" active-text-color="white" text-color="rgb(56, 73, 148)" :collapse="collapse.isCollapse"
            :collapse-transition="false">
            <div @click="changeMenu('1')" class="menu-item">
                <el-menu-item index="1" :style="{'background': deactivate === '1' ? 'rgb(125, 149, 255)' :  'white', 'color': deactivate === '1' ? 'white' : 'rgb(56, 73, 148)', }">
                    <div v-if="deactivate === '1'">
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验项目录入
                        </span>
                    </div>
                    <div v-else>
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <EditPen />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验项目录入
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('2')" class="menu-item">
                <el-menu-item index="2" :style="{'background': deactivate === '2' ? 'rgb(125, 149, 255)' :  'white', 'color': deactivate === '2' ? 'white' : 'rgb(56, 73, 148)', }">
                    <div v-if="deactivate === '2'">
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <School />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室申请
                        </span>
                    </div>
                    <div v-else>
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <School />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室申请
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('3')" class="menu-item">
                <el-menu-item index="3" :style="{'background': deactivate === '3' ? 'rgb(125, 149, 255)' :  'white', 'color': deactivate === '3' ? 'white' : 'rgb(56, 73, 148)', }">
                    <div v-if="deactivate === '3'">
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            申请记录
                        </span>
                    </div>
                    <div v-else>
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <List />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            申请记录
                        </span>
                    </div>
                </el-menu-item>
            </div>
            <div @click="changeMenu('4')" class="menu-item">
                <el-menu-item index="4" :style="{'background': deactivate === '4' ? 'rgb(125, 149, 255)' :  'white', 'color': deactivate === '4' ? 'white' : 'rgb(56, 73, 148)', }">
                    <div v-if="deactivate === '4'">
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <Promotion />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室环境需求
                        </span>
                    </div>
                    <div v-else>
                        <el-icon :style="{'margin-left': collapse.isCollapse ? '25%' : '0%'}" size="25">
                            <Promotion />
                        </el-icon>
                        <span style="font-weight: bold;" v-if="!collapse.isCollapse">
                            实验室环境需求
                        </span>
                    </div>
                </el-menu-item>
            </div>
        </el-menu>
        <el-icon size="30" style="width: 30px; cursor: pointer;"
            color="rgb(125, 149, 255)" @click="collapse.flipCollapse">
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

<style lang="scss" scoped>
.menu-item {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 10px;
}

.menu-item .el-menu-item {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 90%;
    padding: 2.5%;
    border: 1px solid white;
    border-radius: 10px;
    background-color: white;
}

.menu-item .el-menu-item div {
    width: 100%;
}

.menu-item .el-menu-item:hover {
    background: rgb(125, 149, 255) !important;
    color: rgb(0, 88, 167) !important;
}
</style>
