<template>
    <el-main class="main" :style="{'margin-left': !collapse.isCollapse? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)'}">
        <div
            style="width: auto; height: auto; background-color: white; padding: 1%; margin-bottom: 3%; border-radius: 10px;">
            <h3 style="text-align: center;">本科实验教学环境需求</h3>
            <el-divider></el-divider>
            <el-form ref="suggestionFormRef" :model="suggestionForm" label-width="auto">
                <el-form-item label="学期：" :prop="`semesterId`">
                    <el-select-v2 v-model="suggestionForm.semesterId" placeholder="请选择学期" value="value" :disabled="true"
                        label="label" :options="semesterList" />
                </el-form-item>
                <el-form-item label="课程名称：" :prop="`courseNameId`" :rules="[
                    { validator: validateCourseName, trigger: 'blur' }
                ]">
                    <el-select-v2 v-model="suggestionForm.courseNameId" placeholder="课程名称" value="value"
                        filterable clearable label="label" :options="courseNames" />
                </el-form-item>
                <el-form-item label="任课教师：" :prop="`teacherId`" :rules="[
                    { validator: validateTeacherName, trigger: 'blur' }
                ]">
                    <el-select-v2 v-model="suggestionForm.teacherId" placeholder="请选择任课教师" value="value"
                        filterable clearable label="label" :options="teacherList" />
                </el-form-item>
                <el-form-item label="实验室：" :prop="`labId`" :rules="[
                    { validator: validateLabName, trigger: 'blur' }
                ]">
                    <el-select-v2 v-model="suggestionForm.labId" placeholder="请选择实验室" value="value" filterable
                        clearable label="label" :options="labList" />
                </el-form-item>
                <el-form-item label="留言：" :prop="`environmentCommand`" :rules="[
                    { validator: validateEnvironmentCommand, trigger: 'blur' }
                ]">
                    <el-input type="textarea" v-model="suggestionForm.environmentCommand"
                        placeholder="留言"></el-input>
                </el-form-item>
                <!--<el-form-item label="上传文件：" :prop="`file`">
                    <el-switch v-model="isUploadFile" class="mt-2" style="margin-left: 24px" inline-prompt :disabled="uploading"
                        :active-icon="Check" :inactive-icon="Close" />
                </el-form-item>
                <div v-if="isUploadFile"
                    style="width: auto; border: 1px solid #ddd; border-radius: 10px; padding: 1%; margin-bottom: 1%;">
                     分片上传组件 
                    <WebUpload ref="webUpload"></WebUpload>
                </div>-->
            </el-form>
            <el-divider></el-divider>
            <div style="width: 100%; display: inline-flex; align-items: center; justify-content: center;">
                <el-button type="primary" @click="submitUploadForm(suggestionFormRef)"
                    :disabled="uploading">提交</el-button>
            </div>
        </div>
        <div
            style="width: auto; height: auto; background-color: white; padding: 1%; margin-bottom: 3%; border-radius: 10px;">
            <h4>tips：</h4>
            <el-text>
                1.对于实验室有什么建议或者要求，可以在此留言。
            </el-text>
        </div>
        <!-- <div
            style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
            <h3 style="font-weight: bold; text-align: center;">管理员回复</h3>
            <el-divider></el-divider>
            <div v-for="suggest in suggestList" :key="suggest.id"
                style="border: 1px solid #ddd; padding: 1%; margin-bottom: 1%;">
                <div
                    style="width: 100%; height: auto; display: flex; justify-content: space-between; align-items: center; margin-bottom: 1%;">
                    <div>学期：{{ suggest.semester }}</div>
                    <div>日期：{{ suggest.submitDate }}</div>
                    <div>课程名称：{{ suggest.courseName }}</div>
                    <div>老师名字：{{ suggest.teacherName }}</div>
                    <div>实验室：{{ suggest.labName }}</div>
                </div>
                <div
                    style="width: 100%; height: auto; display: inline-flex; justify-content: center; margin-bottom: 1%;">
                    <span style="width: 4%;">留言：</span>
                    <div style="width: 96%;">
                        <el-text>{{ suggest.environmentCommand }}</el-text>
                    </div>
                </div>
                <div
                    style="width: 100%; height: auto; display: inline-flex; justify-content: center; align-items: center;">
                    <span style="width: 4%;">附件：</span>
                    <div style="width: 96%;">
                        <el-link v-if="suggest.appUrl.substring(13)" type="primary">{{
                            suggest.appUrl.substring(13) }}</el-link>
                        <el-text v-else>无附件</el-text>
                    </div>
                </div>
                <div style="width: 100%; display: inline-flex; justify-content: center; margin-top: 1%;">
                    <div style="width: 4%;">
                        <span>回复：</span>
                    </div>
                    <div style="width: 96%;">
                        <el-text>{{ suggest.adminReply }}</el-text>
                    </div>
                </div>
            </div>
            <el-divider></el-divider>
            <div
                style="width: 100%; margin-top: 1%; display: inline-flex; justify-content: center; align-items: center;">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :size="size" :disabled="disabled" :background="true"
                    layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handleCurrentPageChange" />
            </div>
        </div> -->
    </el-main>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import {  ElMessage, FormInstance } from "element-plus";
import { useCollapseStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const basicData = JSON.parse(`${localStorage.getItem('teacherBasicData')}`);

const collapse = useCollapseStore();

const webUpload = ref();

const isUploadFile = ref(false); // 是否上传文件

var ws: any = null;

const semesterList = ref<any[]>([]); // 学期列表
const courseNames = ref<any[]>([]); // 课程名称列表
const teacherList = ref<any[]>([]); // 教师列表
const labList = ref<any[]>([]); // 实验室列表
const uploading = ref(false); // 是否正在上传

const suggestionFormRef = ref<FormInstance>();
const suggestionForm = reactive({
    semesterId: 0,
    courseNameId: '',
    teacherId: '',
    labId: '',
    environmentCommand: '',
});

/*  提交  */
const submitUploadForm = (formEl: FormInstance | undefined) => {
    if (!formEl) {
        return;
    }
    formEl.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('请检查输入项');
            return;
        }
        uploading.value = true;
        if (isUploadFile.value) {
            webUpload.value.uploadToServer();
            webUpload.value.uploader.on('uploadSuccess', (_file: any, response: any) => {
                ws.send(JSON.stringify({
                    semesterId: suggestionForm.semesterId,
                    courseNameId: suggestionForm.courseNameId,
                    teacherId: suggestionForm.teacherId,
                    labId: suggestionForm.labId,
                    environmentCommand: suggestionForm.environmentCommand,
                    appUrl: response.data.path,
                }));
            });
        } else {
            ws.send(JSON.stringify({
                semesterId: suggestionForm.semesterId,
                courseNameId: suggestionForm.courseNameId,
                teacherId: suggestionForm.teacherId,
                labId: suggestionForm.labId,
                environmentCommand: suggestionForm.environmentCommand,
            }));
        }
    });
}

// if(!webSocketStore.wsSuggestion.ws) {
ws = init(`/ws/teacher/suggestions`);
//     webSocketStore.setWsSuggestionWs(ws);
// } else {
//     ws = webSocketStore.wsSuggestion.ws;
// }

ws.onmessage = (e: any) => {
    if (e.data === 'heartbeat') {
        ws.send('heartbeatAsk');
        return;
    }

    uploading.value = false;
    isUploadFile.value = false;
    resetForm();
    ElMessage.success('提交成功');
    return;

}

// ws.onerror = (e: any) => {
//     if (e.target.readyState === WebSocket.CLOSED) {
//         fetch(`/ws/teacher/suggestions`, {
//             headers: {
//                 'Sec-WebSocket-Protocol': token,
//             }
//         }).then(res => {
//             if (res.status === 401) {
//                 console.log(token);
//                 // console.log('登录已过期，请重新登录');
//                 ElMessage.error('NOT_LOGIN');
//                 // 提示用户重新登录
//                 router.push('/login');
//             } else {
//                 ElMessage.error('服务器出错，请联系管理员');
//             }
//         })
//     }
// }

const validateCourseName = (_rule: any, value: any, callback: any) => {
    if (!value || value === '') {
        return callback(new Error('请选择课程名称'));
    }
    return callback();
}

const validateTeacherName = (_rule: any, value: any, callback: any) => {
    if (!value || value === '') {
        return callback(new Error('请选择任课教师'));
    }
    return callback();
}

const validateLabName = (_rule: any, value: any, callback: any) => {
    if (!value || value === '') {
        return callback(new Error('请选择实验室'));
    }
    return callback();
}

const validateEnvironmentCommand = (_rule: any, value: any, callback: any) => {
    if (!value || value === '') {
        return callback(new Error('请输入留言'));
    }
    return callback();
}

const resetForm = () => {
    suggestionForm.semesterId = semesterList.value[semesterList.value.length - 1].value;
    suggestionForm.courseNameId = '';
    suggestionForm.teacherId = '';
    suggestionForm.labId = '';
    suggestionForm.environmentCommand = '';
}


// const suggestList = ref<any[]>([]);

// const size = ref<ComponentSize>('default') // 分页大小
// const pageSize = ref(10) // 每页显示条数
// const disabled = ref(false) // 是否禁用分页
// const currentPage = ref(1) // 当前页
// const total = ref(0) // 总条数

// const handleSizeChange = (val: number) => {
//     pageSize.value = val;
//     ws.send(JSON.stringify({
//         pagination: true,
//         pageSize: val,
//         currentPage: currentPage.value
//     }))
// }

// const handleCurrentPageChange = (val: number) => {
//     currentPage.value = val;
//     ws.send(JSON.stringify({
//         pagination: true,
//         pageSize: pageSize.value,
//         currentPage: val
//     }))
// }

onMounted(async () => {
    const semesterRes = basicData.semesters;
    const semesterData = semesterRes.data;
    semesterList.value = semesterData.map((item: any) => {
        return {
            value: item.id,
            label: item.startYear + '-' + item.endYear + '-' + item.stage,
        }
    });
    suggestionForm.semesterId = semesterList.value[semesterList.value.length - 1].value;

    let courseNameRes = basicData.courseNames;
    courseNames.value = courseNameRes.data.map((item: any) => ({
        value: item.id,
        label: item.courseName
    }));

    let teacherRes = basicData.teachers;
    teacherList.value = teacherRes.data.map((item: any) => ({
        value: item.id,
        label: item.name
    }));
    // console.log(teacherList.value)

    let labRes = basicData.labs;
    labList.value = labRes.data.map((item: any) => ({
        value: item.id,
        label: item.name,
    }));

});

/* 路由跳转前将websocket关闭 */
// router.beforeEach((_to, _from, next) => {
//     ws.close();
//     next();
// });
onBeforeUnmount(() => {
    ws?.close();
});

</script>

