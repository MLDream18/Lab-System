<template>
    <el-main class="main"
        :style="{ 'margin-left': !collapse.isCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
        <div
            style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
            <el-form ref="formRef" style="margin: auto auto auto auto; max-width: 50%;
                border: 1px solid black; border-radius: 10px; padding: 2.5%;" status-icon :model="dynamicValidateForm"
                label-width="auto" class="demo-dynamic">
                <div style="width: 100%; text-align: center; margin-top: -2.5%;">
                    <h2>申请信息填写</h2>
                </div>
                <div style="width: 100%;">
                    <el-form-item :prop="`lab`" label="使用教室：" :rules="[
                        { validator: validateClassroom, trigger: 'blur' }
                    ]">
                        <el-input v-model="dynamicValidateForm.lab" :disabled="isDisabled" />
                    </el-form-item>
                    <el-form-item label="开始使用日期：" :prop="`startDate`" :rules="[
                        { validator: validateStartDate, trigger: ['change', 'blur'] }
                    ]">
                        <el-date-picker v-model="dynamicValidateForm.startDate" placeholder="选择日期" />
                    </el-form-item>
                </div>
                <div
                    style="width: 95%; border: 1px solid black; border-radius: 10px; padding: 2.5%; margin-top: 2%; margin-bottom: 2%;">
                    <div>
                        <div style="width: 80%; margin-left: 10%; display: inline-flex; justify-content: center;">
                            <h3>申请时间段</h3>
                        </div>
                        <div style="width: 10%; display: inline-flex; justify-content: flex-end;">
                            <el-popover placement="top-start" title="Tip" width="auto" trigger="click">
                                <span>周次：按以下两种格式之一填写</span>
                                <br />
                                <span>一、以<strong>减号 -</strong> 分隔，表示<strong>开始周-结束周</strong>，如
                                    <strong>1-3</strong></span>
                                <br />
                                <span>二、以<strong>英文逗号 ,</strong> 分隔，表示<strong>多个周次</strong>，如
                                    <strong>1,3,5</strong></span>
                                <br />
                                <span>时间：</span>
                                <br />
                                <span><strong>星期+节次</strong>，如 <strong>10102</strong> 表示周一第一、二节课</span>
                                <br />
                                <span>（若申请多个时间段，则用<strong>英文逗号 ,</strong> 分隔，如
                                    <strong>10102,10304,10506</strong>）</span>
                                <br />
                                <span>注：不要填写<strong>空格和前导0</strong>，否则格式会有误！！！</span>
                                <template #reference>
                                    <el-button>🥽tips</el-button>
                                </template>
                            </el-popover>
                        </div>
                    </div>
                    <div v-for="(domain, index) in dynamicValidateForm.domains" :key="index" style="width: 100%;">
                        <el-form-item label="使用周次：" :prop="`domains.${index}.week`" :rules="[
                            { validator: validateWeek, trigger: 'blur' },
                        ]">
                            <el-input v-model="domain.week" placeholder="使用周次" />
                        </el-form-item>
                        <el-form-item label="使用时间：" :prop="`domains.${index}.section`" :rules="[
                            { validator: validateSection, trigger: 'blur' },
                        ]">
                            <el-input v-model="domain.section" placeholder="使用时间" />
                        </el-form-item>
                        <div style="width: 100%; display: inline-flex; justify-content: flex-end;">
                            <el-button type="danger" @click.prevent="removeDomain(domain)">
                                删除
                            </el-button>
                        </div>
                        <div style="width: 100%; background-color: black;">
                            <el-divider></el-divider>
                        </div>
                    </div>
                    <div style="width: 100%; display: inline-flex; justify-content: center;">
                        <el-button @click="addDomain">添加时间段</el-button>
                    </div>
                </div>
                <el-form-item label="申请事由：" prop="`applyReason`">
                    <el-select-v2 v-model="dynamicValidateForm.applyReason" :options="states" value="value"
                        label="label" placeholder="申请事由" />
                </el-form-item>
                <el-form-item label="课程名称：" :prop="`courseNameId`">
                    <el-select-v2 v-model="dynamicValidateForm.courseNameId" filterable clearable :options="courseNames"
                        value="value" label="label" placeholder="选填" />
                </el-form-item>
                <el-form-item label="实验内容：" :prop="`experimentContent`" :rules="[
                    { validator: validateExperimentContent, trigger: 'blur' }
                ]">
                    <el-input v-model="dynamicValidateForm.experimentContent" placeholder="实验内容" autosize
                        type="textarea" />
                </el-form-item>
                <el-form-item label="班级：" :prop="`classId`">
                    <el-select-v2 v-model="dynamicValidateForm.classId" filterable clearable :options="classList"
                        value="value" label="label" placeholder="选填" />
                </el-form-item>
                <el-form-item label="实验人数：" :prop="`experimentPeople`" :rules="[
                    { validator: validateExperimentPeople, trigger: 'blur' }
                ]">
                    <el-input-number v-model="dynamicValidateForm.experimentPeople" min="1" max="60" />
                </el-form-item>
                <el-form-item label="申请人：" :prop="`applicantId`" :rules="[
                    { validator: validateApplicant, trigger: 'change' }
                ]">
                    <el-select-v2 v-model="dynamicValidateForm.applicantId" filterable clearable :options="teacherList"
                        placeholder="申请人" />
                </el-form-item>
                <el-form-item label="联系电话：" :prop="`phone`" :rules="[
                    { validator: validatePhone, trigger: 'blur' },
                ]">
                    <el-input v-model="dynamicValidateForm.phone" autocomplete="on" placeholder="联系电话" clearable />
                </el-form-item>
                <el-form-item label="学院：" :prop="`college`" :rules="[
                    { validator: validateCollege, trigger: 'blur' }
                ]">
                    <el-select-v2 v-model="dynamicValidateForm.college" filterable clearable :options="colleges"
                        value="value" label="label" placeholder="学院" />
                </el-form-item>
                <el-form-item>
                    <div style="width: 100%; display: inline-flex; justify-content: flex-end;">
                        <el-button type="primary" @click="submitForm(formRef)">提交</el-button>
                        <el-button @click="resetForm(formRef)">重置</el-button>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </el-main>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { useRoute } from 'vue-router';
import { useCollapseStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const basicData = JSON.parse(`${localStorage.getItem('teacherBasicData')}`);

const collapse = useCollapseStore();

const route = useRoute();

var ws = init('/ws/teacher/apply/fill');

// const bc = new BroadcastChannel('teacher-apply'); // 申请广播
const regex = /(10102|10304|10506|10708|10910|20102|20304|20506|20708|20910|30102|30304|30506|30708|30910|40102|40304|40506|40708|40910|50102|50304|50506|50708|50910|60102|60304|60506|60708|60910|70102|70304|70506|70708|70910)/i
const formRef = ref<FormInstance>()
const states = ref<{ value: number, label: string }[]>([]); // 状态
const courseNames = ref<{ value: number, label: string }[]>([]); // 课程名称
const classList = ref<{ value: number, label: string }[]>([]); // 班级列表
const teacherList = ref<{ value: number, label: string }[]>([]); // 教师列表
const labList = ref<{ value: string, label: string, id: number }[]>([]); // 实验室列表
const colleges = ref<{ value: string, label: string }[]>([{ value: '人工智能学院', label: '人工智能学院' }]); // 学院列表
const semesterId = ref(route.query.semesterId)
const isDisabled = ref(route.query.labName != '') // 是否禁用实验室选择

/* 时间格式化 */
const formatDate = (date: Date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const getLabId = (labName: string) => {
    return labList.value.find(item => item.label === labName)?.id || 0;
}


const dynamicValidateForm = reactive<{
    domains: DomainItem[]
    lab: string
    startDate: Date
    applyReason: number
    courseNameId: string
    experimentContent: string
    classId: string
    experimentPeople: number
    applicantId: string
    phone: string
    college: string
    applyDate: Date
}>({
    domains: [
        {
            key: 1,
            week: route.query.week as string || '',
            section: route.query.weekdaySection as string || '',
        },
    ],
    lab: route.query.labName as string || '',
    startDate: new Date(),
    applyReason: 8,
    courseNameId: '',
    experimentContent: '',
    classId: '',
    experimentPeople: 0,
    applicantId: '',
    phone: '',
    college: '',
    applyDate: new Date(),
})


/* 使用教室不为空 */
const validateClassroom = (_rule: any, value: any, callback: any) => {
    if (!value || value === '') {
        return callback(new Error('请填写使用教室'));
    }
    return callback();
};

/* 使用日期不能为空 不能小于当前日期 */
const validateStartDate = (_rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请选择开始使用日期'));
    }
    // console.log(formatDate(value), formatDate(new Date()))
    if (formatDate(value) >= formatDate(new Date())) {
        return callback();
    }
    return callback(new Error('开始使用日期不能小于当前日期'));
};

/* 1-3 或者 1,3,5 或者 1-3,5-7,8,9,10 */
const validateWeek = (_rule: any, value: any, callback: any) => {
    // console.log(value)
    if (!value) {
        return callback(new Error('请填写使用周次'));
    }
    let index = value.indexOf(',');
    let seenWeeks = new Set<number>();
    if (index !== -1) {
        let weekList = value.split(',');
        // console.log(weekList)
        for (let week of weekList) {
            // console.log(week)
            if (week == '') {
                console.log('week is empty')
                return callback(new Error('请填写正确的使用周次格式'));
            }
            if (week.match(/^\d+-\d+$/)) {
                const [start, end] = week.split('-');
                if (!/^[1-9][0-9]*$/.test(start) || !/^[1-9][0-9]*$/.test(end)) {
                    return callback(new Error('请填写正确的使用周次格式'));
                }
                const [tStart, tEnd] = week.split('-').map(Number);
                if (tStart > tEnd) {
                    return callback(new Error('开始周次不能大于结束周次'));
                }
                if (tStart < 1 || tStart > 24 || tEnd < 1 || tEnd > 24) {
                    return callback(new Error('周次必须在1-24之间'));
                }
                for (let i = tStart; i <= tEnd; i++) {
                    if (seenWeeks.has(i)) {
                        return callback(new Error(`周次范围 ${tStart}-${tEnd} 已经出现过`));
                    }
                    seenWeeks.add(i);
                }
            } else {
                if (!/^[1-9][0-9]*$/.test(week)) {
                    return callback(new Error('请填写正确的使用周次格式'));
                }
                let weekNumber = parseInt(week);
                if (weekNumber < 1 || weekNumber > 24) {
                    return callback(new Error('周次必须在1-24之间'));
                }
                if (seenWeeks.has(weekNumber)) {
                    return callback(new Error(`周次 ${weekNumber} 已经出现过`));
                }
                seenWeeks.add(weekNumber);
            }
        }
        return callback();
    }
    if (value.match(/^\d+-\d+$/)) {
        const [start, end] = value.split('-');
        if (!/^[1-9][0-9]*$/.test(start) || !/^[1-9][0-9]*$/.test(end)) {
            return callback(new Error('请填写正确的使用周次格式'));
        }
        const [tStart, tEnd] = value.split('-').map(Number);
        if (tStart > tEnd) {
            return callback(new Error('开始周次不能大于结束周次'));
        }
        if (tStart < 1 || tStart > 24 || tEnd < 1 || tEnd > 24) {
            return callback(new Error('周次必须在1-24之间'));
        }
        for (let i = tStart; i <= tEnd; i++) {
            if (seenWeeks.has(i)) {
                return callback(new Error(`周次范围 ${tStart}-${tEnd} 已经出现过`));
            }
            seenWeeks.add(i);
        }
        return callback();
    } else if (value.match(/^\d+(,\d+)*$/)) {
        let weekList = value.split(',');
        for (let week of weekList) {
            if (!/^[1-9][0-9]*$/.test(week)) {
                return callback(new Error('请填写正确的使用周次格式'));
            }
            const weekNumber = parseInt(week);
            if (weekNumber < 1 || weekNumber > 24) {
                return callback(new Error('周次必须在1-24之间'));
            }
            if (seenWeeks.has(weekNumber)) {
                return callback(new Error(`周次 ${weekNumber} 已经出现过`));
            }
            seenWeeks.add(weekNumber);
        }
        return callback();
    }
    return callback('请填写正确的使用周次格式');
};

/* 10102 或者 10102,10304,10506 */
const validateSection = (_rule: any, value: any, callback: any) => {
    // console.log(value)
    if (!value || value === '') {
        return callback(new Error('请填写使用时间'));
    }
    if (value.indexOf(',') === -1) {
        if (!regex.test(value) || value.length !== 5) {
            return callback(new Error('请填写正确的使用节次格式'));
        }
        return callback();
    }
    let seenSections = new Set<string>();
    let sectionList = value.split(',');
    for (let section of sectionList) {
        if (seenSections.has(section)) {
            return callback(new Error(`${section} 已经出现过`));
        }
        seenSections.add(section);
    }
    // console.log(sectionList)
    if (sectionList.some((section: any) => (!regex.test(section)) || section.length !== 5)) {
        return callback(new Error('请填写正确的使用节次格式'));
    }
    return callback();
};

/* 实验内容不为空 */
const validateExperimentContent = (_rule: any, value: any, callback: any) => {
    if (value === '') {
        return callback(new Error('请填写实验内容'));
    }
    return callback();
};

/* 1-60 */
const validateExperimentPeople = (_rule: any, value: any, callback: any) => {
    // console.log(value)
    if (!value || value === '') {
        return callback(new Error('请填写实验人数'));
    }
    if (!/^[1-9][0-9]*$/.test(value)) {
        return callback(new Error('请填写正确的实验人数'));
    }
    if (value < 1 || value > 60) {
        return callback(new Error('实验人数必须在1-60之间'));
    }
    return callback();
};

/* 汉字2-10个字 */
const validateApplicant = (_rule: any, value: any, callback: any) => {
    if (!value || value == 0) {
        return callback(new Error('请填写申请人'));
    }
    return callback();
};

/* 11位电话号码 */
const validatePhone = (_rule: any, value: any, callback: any) => {
    if (value === '') {
        return callback(new Error('请填写联系电话'));
    }
    if (!/^1\d{10}$/.test(value)) {
        return callback(new Error('请填写11位手机号码(1开头)'));
    }
    return callback();
};

const validateCollege = (_rule: any, value: any, callback: any) => {
    if (!value) {
        return callback(new Error('请选择学院'));
    }
    return callback();
};

const removeDomain = (item: DomainItem) => {
    if (dynamicValidateForm.domains.length === 1) {
		ElMessage.error('时间段至少要有一个');
		return;
	}
    const index = dynamicValidateForm.domains.indexOf(item)
    if (index !== -1) {
        dynamicValidateForm.domains.splice(index, 1)
    }
}

const addDomain = () => {
    dynamicValidateForm.domains.push({
        key: Date.now(),
        week: '',
        section: ''
    })
}

interface DomainItem {
    key: number
    week: string
    section: string
}

const submitForm = async (formEl: FormInstance | undefined) => {
    // console.log(dynamicValidateForm);
    if (!formEl) return
    formEl.validate(async (valid) => {
        if (valid) {
            let seenTimes = new Set<string>();
            let ok = true;
            dynamicValidateForm.domains.forEach((item: DomainItem) => {
                let weekList = item.week.split(',');
                let sectionList = item.section.split(',');

                sectionList.forEach((section: string) => {
                    weekList.forEach((week: string) => {
                        if (week.match(/^\d+-\d+$/)) {
                            const [start, end] = week.split('-').map(Number);
                            for (let i = start; i <= end; i++) {
                                if (seenTimes.has(`${i}${section}`)) {
                                    ok = false;
                                    return;
                                }
                                seenTimes.add(`${i}${section}`);
                            }
                        } else {
                            if (seenTimes.has(`${week}${section}`)) {
                                ok = false;
                                return;
                            }
                            seenTimes.add(`${week}${section}`);
                        }
                    });
                });
            });
            if (!ok) {
                ElMessage.error('使用时间有重复！！！');
                return;
            }
            // console.log(dynamicValidateForm)
            let dataDTO = {
                usedLabId: getLabId(dynamicValidateForm.lab), // 使用教室
                usedStartDate: formatDate(dynamicValidateForm.startDate), // 使用开始日期
                // usedWeek: dynamicValidateForm.domains.map((item: DomainItem) => item.week).join(','), // 使用周次
                // usedSection: dynamicValidateForm.domains.map((item: DomainItem) => item.section).join(','), // 使用节次
                arrWeekAndSection: dynamicValidateForm.domains.map((item: DomainItem) => ({
                    week: item.week,
                    weekdayAndSection: item.section
                })),
                applyReason: dynamicValidateForm.applyReason, // 申请原因
                experimentContent: dynamicValidateForm.experimentContent, // 实验内容
                courseNameId: dynamicValidateForm.courseNameId, // 课程名称
                classId: dynamicValidateForm.classId, // 班级
                experimentPeople: dynamicValidateForm.experimentPeople, // 实验人数
                applicantId: dynamicValidateForm.applicantId, // 申请人
                applicantTelephone: dynamicValidateForm.phone, // 联系电话
                applicantCollege: dynamicValidateForm.college, // 学院
                submitDate: formatDate(dynamicValidateForm.applyDate), // 申请日期
                state: 0, // 状态
                semesterId: semesterId.value, // 学期id
                submitTeacherId: 1 // 提交账号id
            }
            // console.log(dataDTO);
            // let submitRes = await axios.post(`/api/teacher/lab/apply/submitApplyForm`, dataDTO);
            if(ws.readyState !== WebSocket.CLOSED && ws.readyState !== WebSocket.CLOSING) {
                ws.send(JSON.stringify(dataDTO));
            } else {
                ElMessage.error('WebSocket连接已断开，刷新页面尝试重新连接');
            }
            // if (submitRes.data.code === 1) {
            //     ElMessage.success(submitRes.data.msg);
            // } else {
            //     ElMessage.error(submitRes.data.msg);
            // }
        } else {
            // console.log('error submit!')
            ElMessage.error('请检查输入项')
        }
    })
}

ws.onmessage = (event) => {
    if (event.data === 'heartbeat') {
        ws.send('heartbeatAsk');
        return;
    }
    const submitRes = JSON.parse(event.data);

    if (Array.isArray(submitRes.data)) {
        return;
    }
    if (submitRes.code === 1) {
        ElMessage.success(submitRes.data);
        resetForm(formRef.value);
    } else {
        ElMessage.error(submitRes.msg);
    }
}

// ws.onerror = (e: any) => {
//     // console.log(e);
//     if (e.target.readyState === WebSocket.CLOSED) {
//         fetch(`/ws/teacher/apply/fill`, {
//             headers: {
//                 'Sec-WebSocket-Protocol': token,
//             }
//         }).then(res => {
//             if (res.status === 401) {
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

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    dynamicValidateForm.domains = [{
        key: 1,
        week: route.query.week as string || '',
        section: route.query.weekdaySection as string || '',
    },]
    dynamicValidateForm.applyReason = 8;
}

states.value.push({
    'value': 2,
    'label': '调课',
});
states.value.push({
    'value': 3,
    'label': '补课',
});
states.value.push({
    'value': 4,
    'label': '培训',
});
states.value.push({
    'value': 5,
    'label': '竞赛',
});
states.value.push({
    'value': 6,
    'label': '考试',
});
states.value.push({
    'value': 7,
    'label': '课程设计',
});
states.value.push({
    'value': 8,
    'label': '其他',
});

onMounted(async () => {
    let courseNameRes = basicData.courseNames;
    courseNames.value = courseNameRes.data.map((item: any) => ({
        value: item.id,
        label: item.courseName
    }));
    // console.log(courseNames.value)

    let classRes = basicData.classes;
    classList.value = classRes.data.map((item: any) => ({
        value: item.id,
        label: item.name
    }));
    // console.log(classList.value)
    let teacherRes = basicData.teachers;
    teacherList.value = teacherRes.data.map((item: any) => ({
        value: item.id,
        label: item.name
    }));
    // console.log(teacherList.value)

    let labRes = basicData.labs;
    labList.value = labRes.data.map((item: any) => ({
        value: item.name,
        label: item.name,
        id: item.id
    }));
});


/* 路由跳转前将websocket关闭 */
// router.beforeEach((_to, _from, next) => {
//     ws.close();
//     next();
// });

onBeforeUnmount(() => {
	ws?.close();
})

</script>
