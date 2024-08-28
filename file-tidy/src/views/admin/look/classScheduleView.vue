<template>
	<el-main class="main"
		:style="{ 'margin-left': !collapse.isAdminCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
		<div style="width: 50%; height: auto; background-color: white; margin-bottom: 1.5%;">
			<el-segmented v-model="form.place" :options="placeList" block @change="changePlaceTable"
				style="background-color: white;" />
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div style="width: 100%; height: auto; display: inline-flex; align-items: center;">
				<div style="width: 20%; height: auto;">
					<el-upload ref="uploadClassSchedule" name="classSchedule" :limit="1"
						:on-exceed="handleClassScheduleExceed" :auto-upload="false" show-file-list
						v-model:file-list="classScheduleList">
						<template #trigger>
							<el-button type="primary">上传课程表</el-button>
						</template>
						<el-button style="margin-left: 2%;" type="success"
							@click="submitUploadClassSchedule">导入</el-button>
					</el-upload>
				</div>
				<div style="width: 60%; height: auto; display: inline-flex; align-items: center;">
					<div style="width: 50%;">
						<el-select-v2 v-model="form.semesterId" placeholder="学期" style="width: 80%"
							@change="changeCourseTable" :options="semesterList" value="value" label="label">
							<template #label="{ label }">
								<span>{{ '学期' }}: </span>
								<span style="font-weight: bold">{{ label }}</span>
							</template>
						</el-select-v2>
					</div>
					<div style="width: 50%; display: inline-flex; justify-content: space-between; align-items: center;">
						<div style="width: 47.5%;">
							<el-select-v2 v-model="form.startWeek" :options="weeks" placeholder="开始周"
								@change="changeStartWeek" value="value" label="label">
								<template #label="{ label }">
									<span>{{ '开始周' }}: </span>
									<span style="font-weight: bold">{{ label }}</span>
								</template>
							</el-select-v2>
						</div>
						<div style="width: 2%; display: inline-flex; justify-content: center; align-items: center;">
							至</div>
						<div style="width: 47.5%;">
							<el-select-v2 v-model="form.endWeek" :options="weeks" placeholder="结束周"
								@change="changeEndWeek" value="value" label="label">
								<template #label="{ label }">
									<span>{{ '结束周' }}: </span>
									<span style="font-weight: bold">{{ label }}</span>
								</template>
							</el-select-v2>
						</div>
					</div>
				</div>
				<div style="width: 20%; height: auto;">
					<div
						style="width: 100%; height: 100%; display: inline-flex; justify-content: flex-end; align-items: center;">
						<el-button type="primary" @click="dialogFormVisible = true">上传开课计划</el-button>
						<el-button @click="exportAsExcel">导出为Excel</el-button>
					</div>
				</div>
			</div>
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<el-table :data="tableData" border ref="tableRef"
				style="width: 100%; border: 1px solid black; background-color: white;"
				:cell-style="{ 'textAlign': 'center', 'height': '10vh', 'color': 'black', 'width': '1vw' }"
				:header-cell-style="{ 'text-align': 'center', 'background-color': 'white', 'color': 'black', 'width': '1vw' }"
				:row-style="{ 'fontSize': '15px', 'textAlign': 'center', }">
				<el-table-column v-for="(column, index) in tableHeader" :key="index" :label="column.label"
					:prop="column.prop" :children="column.children">
					<template v-if="column.children">
						<el-table-column v-for="(grandChild, grandChildIndex) in column.children" :key="grandChildIndex"
							:label="grandChild.label" :prop="grandChild.prop" :column-key="grandChild.key" />
					</template>
				</el-table-column>
			</el-table>
		</div>
		<div>
			<el-dialog v-model="dialogFormVisible" title="开课计划" width="500">
				<el-form>
					<el-form-item label="学期起止日期：	">
						<el-date-picker v-model="startToEndDate" type="daterange" range-separator="至"
							start-placeholder="Start date" end-placeholder="End date" size="default"
							value-format="YYYY-MM-DD" />
					</el-form-item>
					<el-form-item label="开课计划表：" label-width="auto">
						<el-upload ref="uploadCommencementPlan" name="commencementPlan" :limit="1"
							:on-exceed="handleCommencementPlanExceed" :auto-upload="false" show-file-list
							v-model:file-list="commencementPlanList">
							<template #trigger>
								<el-button type="primary">选择文件</el-button>
							</template>
						</el-upload>
					</el-form-item>
				</el-form>
				<template #footer>
					<div class="dialog-footer">
						<el-button @click="dialogFormVisible = false">取消</el-button>
						<el-button type="primary" @click="dialogFormConfirm">
							确认
						</el-button>
					</div>
				</template>
			</el-dialog>
		</div>
	</el-main>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, reactive, ref } from 'vue';
import * as XLSX from 'xlsx-js-style';
import { dragTable } from '../../../utils/common';
import { ElMessage, ElNotification, genFileId, UploadInstance, UploadProps, UploadRawFile, UploadUserFile } from 'element-plus'
import axios from 'axios';
import router from '../../../router';
import { useTaskStore } from '../../../stores/store';
import { useCollapseStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const basicData = JSON.parse(`${localStorage.getItem('basic-data')}`);

const collapse = useCollapseStore();
const taskStore = useTaskStore();

/* 全局变量 */
const wsDsp = init(`/ws/admin/approval`);
const currentRole = ref();
const currentRoleTask = ref();
// const applyStore = useApplyStore();
const ws = init(`/ws/admin/class-schedule`);
const classrooms = ref<{ value: number, label: string }[]>([]); // 实验室
const semesterList = ref<{ value: number, label: string }[]>([]); // 学期
const weeks = ref<{ value: number, label: string }[]>([
	{ value: 1, label: '1' },
	{ value: 2, label: '2' },
	{ value: 3, label: '3' },
	{ value: 4, label: '4' },
	{ value: 5, label: '5' },
	{ value: 6, label: '6' },
	{ value: 7, label: '7' },
	{ value: 8, label: '8' },
	{ value: 9, label: '9' },
	{ value: 10, label: '10' },
	{ value: 11, label: '11' },
	{ value: 12, label: '12' },
	{ value: 13, label: '13' },
	{ value: 14, label: '14' },
	{ value: 15, label: '15' },
	{ value: 16, label: '16' },
	{ value: 17, label: '17' },
	{ value: 18, label: '18' },
	{ value: 19, label: '19' },
	{ value: 20, label: '20' },
	{ value: 21, label: '21' },
	{ value: 22, label: '22' },
	{ value: 23, label: '23' },
	{ value: 24, label: '24' },
	{ value: 25, label: '25' },
	{ value: 26, label: '26' },
	{ value: 27, label: '27' },
	{ value: 28, label: '28' },
	{ value: 29, label: '29' },
	{ value: 30, label: '30' },
]); // 周次
const weekdayCN = ['', '一', '二', '三', '四', '五', '六', '日']; // 中文星期
const sections = ref<{ value: number, label: string }[]>([
	{ value: 0, label: '0102' },
	{ value: 1, label: '0304' },
	{ value: 2, label: '0506' },
	{ value: 3, label: '0708' },
	{ value: 4, label: '0910' },
]); // 节次
const placeList = ref<{ value: string, label: string }[]>([
	{ value: '逸夫楼', label: '逸夫楼' },
	{ value: '博达楼', label: '博达楼' },
	{ value: '文科综合实训大楼', label: '文综楼' },
]); // 地点
const labSourcesClassSchedule = ref<any[]>([]); // 实验室资源
const labSourcesApplyFormInfo = ref<any[]>([]); // 实验室资源
const labSourceMap = ref<any>({}); // 实验室资源映射
const tableData = ref<any[]>([]); // 表格数据
const tableHeader = ref<any[]>([]); // 表头数据
const form = reactive({ // 表单数据
	labId: null, // 实验室id
	startWeek: 1, // 开始周
	endWeek: 20, // 结束周
	startWeekday: 1, // 开始星期
	endWeekday: 7, // 结束星期
	startSection: 0, // 开始节次
	endSection: 4, // 结束节次
	semesterId: 0, // 学期
	place: '逸夫楼', // 地点
});

const uploadClassSchedule = ref<UploadInstance>(); // 上传课程表实例
const uploadCommencementPlan = ref<UploadInstance>(); // 上传开学计划实例
const dialogFormVisible = ref(false); // 开课计划弹窗
const startToEndDate = ref(''); // 学期起止日期
const commencementPlanList = ref<UploadUserFile[]>([]); // 开学计划列表
const classScheduleList = ref<UploadUserFile[]>([]); // 课程表列表

//设置el-table可进行鼠标左键按下左右拖动
const tableRef = ref();

const changeCourseTable = (val: any) => {
	form.semesterId = val;
	ws?.send(JSON.stringify({
		startWeek: form.startWeek,
		endWeek: form.endWeek,
		semesterId: form.semesterId,
		place: form.place,
	}));
}

const changePlaceTable = (val: any) => {
	form.place = val;
	ws?.send(JSON.stringify({
		startWeek: form.startWeek,
		endWeek: form.endWeek,
		semesterId: form.semesterId,
		place: form.place,
	}));
}

const changeStartWeek = (val: any) => {
	if (val > form.endWeek) {
		ElMessage.error('开始周不能大于结束周');
		return;
	}
	form.startWeek = val;
	ws?.send(JSON.stringify({
		startWeek: form.startWeek,
		endWeek: form.endWeek,
		semesterId: form.semesterId,
		place: form.place,
	}));
}

const changeEndWeek = (val: any) => {
	if (val < form.startWeek) {
		ElMessage.error('结束周不能小于开始周');
		return;
	}
	form.endWeek = val;
	ws?.send(JSON.stringify({
		startWeek: form.startWeek,
		endWeek: form.endWeek,
		semesterId: form.semesterId,
		place: form.place,
	}));
}

const taskLen = (data: any, role: number) => {
	let tmp = [];
	tmp = data.filter((item: any) => {
		if (role === 1 && item.state === 0) {
			return {
				...item,
			}
		} else if (role === 2 && item.state === 1) {
			return {
				...item,
			}
		}
	});
	tmp = tmp.map((item: any) => {
		return {
			...item,
		}
	});
	return tmp.length;
}

/* 获取实验室ID */
const getLabId = (labName: string) => {
	for (let i = 0; i < classrooms.value.length; ++i) {
		if (classrooms.value[i].label === labName) {
			return classrooms.value[i].value;
		}
	}
};

const combinedWeekAndSection = (week: string) => {
	let index = week.indexOf(';');
	if (index !== -1) {
		let weeks = week.split(';');
		let result = '';
		for (let i = 0; i < weeks.length - 1; ++i) {
			result += `${weeks[i]}`;
			if (i !== weeks.length - 2) {
				result += ',';
			}
		}
		return `(${result}周)`;
	}
	return `(${week}周)`;
}

ws.onmessage = async (e: any) => {
	if (e.data === 'heartbeat') {
		ws?.send('heartbeatAsk');
		return;
	}
	if (!classrooms.value.length && !semesterList.value.length) {
		const res = basicData.classrooms;
		const data = res.data;
		classrooms.value = [];
		// console.log(data);
		data.forEach((item: any) => {
			classrooms.value.push({
				value: item.id,
				label: item.name,
			});
		});

		const semesterRes = basicData.semesters;
		const semesterData = semesterRes.data;
		if (semesterData.length === 0) {
			return;
		}
		semesterList.value = semesterData.map((item: any) => {
			return {
				value: item.id,
				label: item.startYear + '-' + item.endYear + '-' + item.stage,
			}
		});
		form.semesterId = semesterList.value[semesterList.value.length - 1].value;
		// console.log(semesterList.value)
	}

	let dataRes = JSON.parse(e.data).data;
	form.startWeek = dataRes.startWeek;
	form.endWeek = dataRes.endWeek;
	form.place = dataRes.place;

	labSourcesClassSchedule.value = dataRes.result.classSchedule;
	labSourcesApplyFormInfo.value = dataRes.result.applyFormInfo.filter((item: any) => { if (item.state === 2) { return item } });
	// console.log(labSources.value);
	/* 清空 labSourceMap idMap */
	labSourceMap.value = {};

	/* 初始化 labSourceMap idMap */
	for (let i = 0; i < classrooms.value.length; ++i) {
		if (!classrooms.value[i].label.includes(form.place)) continue;
		for (let j = form.startWeekday; j <= form.endWeekday; ++j) {
			for (let k = form.startSection; k <= form.endSection; ++k) {
				let key = `${classrooms.value[i].value}${j}${sections.value[k].label}`;
				labSourceMap.value[key] = [];
			}
		}
	}

	/* 填充 labSourceMap idMap */
	labSourcesClassSchedule.value.forEach((item: any) => {
		let key = `${getLabId(item.labName)}${item.weekdaySection}`; // 教室id + 星期 + 节次
		labSourceMap.value[key].push(`${item.courseName} ${item.teacherName} ${item.weekdaySection} ${item.startEndWeek} ${item.className} 总课时：${item.sumCourseHour}`);
	});
	labSourcesApplyFormInfo.value.forEach((item: any) => {
		let labId = getLabId(item.labName);
		let sectionData = item.usedSection.split(';');
		for (let i = 0; i < sectionData.length - 1; ++i) {
			let key = `${labId}${sectionData[i]}`; // 教室id + 星期 + 节次
			labSourceMap.value[key].push(`${item.courseName || item.experimentContent} ${item.applicant} ${combinedWeekAndSection(item.usedWeek)} ${item.className || ''} 总课时：${item.sumCourseHours}`);
		}
	});

	/* 置空 tableData tableHeader */
	tableHeader.value = [{ label: '教室', children: [{ prop: 'room', label: '节次' }] }];
	tableData.value = [];

	/* 初始化空表格 */
	/* 初始化表头 */
	for (let i = form.startWeekday; i <= form.endWeekday; ++i) {
		tableHeader.value.push({ label: `星期${weekdayCN[i]}`, children: [] });
		let tmp = [];
		for (let j = form.startSection; j <= form.endSection; ++j) {
			// console.log(j);
			tmp.push({
				prop: `${i}${sections.value[j].label}`,
				label: `${sections.value[j].label}`,
				key: `${i}${sections.value[j].label}`,
			});
		}
		tableHeader.value[tableHeader.value.length - 1].children = tmp;
	}
	// console.log(classrooms.value);

	let tempTableData = [];

	/* 初始化表格字段名 */
	for (let i = 0; i < classrooms.value.length; ++i) {
		if (!classrooms.value[i].label.includes(form.place)) continue;
		let tmp: any = {};
		for (let j = form.startWeekday; j <= form.endWeekday; ++j) {
			for (let k = form.startSection; k <= form.endSection; ++k) {
				/* tmp[j: 星期 + k: 节次] = labSourceMap[教室id + 星期j + 节次k] */
				tmp[`${j}${sections.value[k].label}`] = labSourceMap.value[`${classrooms.value[i].value}${j}${sections.value[k].label}`] || ' ';
			}
		}
		// console.log(tmp);
		tempTableData.push({
			room: classrooms.value[i].label,
			...tmp,
		});
	}

	tableData.value = tempTableData;

	dragTable(tableRef);
}

ws.onerror = (e: any) => {
	// console.log(e, '1111');
	if (e.target.readyState === WebSocket.CLOSED) {
		// console.error('WebSocket connection failed');
		// 检查响应状态码 模拟握手过程
		fetch(`/ws/admin/class-schedule`, {
			headers: {
				'Sec-WebSocket-Protocol': `${localStorage.getItem('token')}`
			}
		}).then(response => {
			if (response.status === 401) {
				// console.log('登录已过期，请重新登录');
				ElMessage.error('NOT_LOGIN');
				// 提示用户重新登录
				router.push('/login');
			} else {
				// console.log('WebSocket连接失败，请检查网络连接');
				ElMessage.error('服务器出错，请联系管理员');
			}
		});
	}
}

wsDsp.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		wsDsp?.send('heartbeatAsk');
		return;
	}
	let resData = JSON.parse(e.data).data;
	if (!currentRole.value) {
		currentRole.value = resData.role;
		currentRoleTask.value = taskLen(resData.result, currentRole.value);
		return;
	}
	nextTick(() => {
		let task = taskLen(resData.result, currentRole.value);
		if (task > currentRoleTask.value) {
			ElNotification({
				title: '通知',
				message: '您有一条新的审批需要处理',
				type: 'info',
				onClick() {
					router.push('/pending-approval');
				},
			})
			taskStore.task = task - currentRoleTask.value;
		} else {
			currentRoleTask.value = task;
		}
	});
}

wsDsp.onerror = (e: any) => {
	if (e.target.readyState === WebSocket.CLOSED) {
		// 检查响应状态码
		fetch(`/ws/admin/approval`, {
			headers: {
				'Sec-WebSocket-Protocol': `${localStorage.getItem('token')}`
			}
		}).then(response => {
			if (response.status === 401) {
				ElMessage.error('NOT_LOGIN');
				// 提示用户重新登录
				nextTick(() => {
					router.push('/login');
				});
			} else {
				ElMessage.error('服务器出错，请联系管理员');
			}
		});
	}
}


/* 文件上传限制 */
const handleCommencementPlanExceed: UploadProps['onExceed'] = (files) => {
	uploadCommencementPlan.value!.clearFiles();
	const file = files[0] as UploadRawFile;
	file.uid = genFileId();
	uploadCommencementPlan.value!.handleStart(file);
}

const handleClassScheduleExceed: UploadProps['onExceed'] = (files) => {
	uploadClassSchedule.value!.clearFiles();
	const file = files[0] as UploadRawFile;
	file.uid = genFileId();
	uploadClassSchedule.value!.handleStart(file);
}

/*  提交课程表  */
const submitUploadClassSchedule = async () => {
	// console.log(classScheduleList.value);
	if (classScheduleList.value.length === 0) {
		ElMessage.error('请上传课程表');
		return;
	}
	const formData = new FormData();
	const file = classScheduleList.value[0].raw;
	if (file) {
		formData.append('classSchedule', file);
		let response = await axios.post('/api/admin/class-time/uploadClassSchedule', formData);
		// console.log(response);
		const data = response.data;
		// console.log(data);
		uploadClassSchedule.value!.clearFiles();
		if (data.code === 1) {
			ElMessage.success('上传成功');
			await axios.get('/api/admin/class-time/classrooms').then(res => {
				classrooms.value = res.data.data.map((item: any) => {
					return {
						value: item.id,
						label: item.name,
					};
				});
			});
			ws?.send(JSON.stringify({
				startWeek: form.startWeek,
				endWeek: form.endWeek,
				semesterId: form.semesterId,
			}));
		} else {
			ElMessage.error(data.msg);
		}
	}
}


/* 表单对话框确认 */
const dialogFormConfirm = async () => {
	if (startToEndDate.value.length === 0) {
		ElMessage.error('请选择学期起止日期！');
		return;
	}
	if (commencementPlanList.value.length === 0) {
		ElMessage.error('请上传开课计划！');
		return;
	}
	const formData = new FormData();

	const file = commencementPlanList.value[0].raw;
	if (file) {
		formData.append('commencementPlan', file);
		formData.append('startDate', startToEndDate.value[0]);
		formData.append('endDate', startToEndDate.value[1]);
		// console.log(formData.getAll('commencementPlan'));
		const res = await axios.post('/api/admin/class-time/uploadCommencementPlan', formData, {
			headers: {
				'Content-Type': 'multipart/form-data'
			}
		});
		const data = res.data;
		// console.log(data);
		if (data.code === 0) {
			ElMessage.error(data.msg);
		} else {
			uploadCommencementPlan.value!.clearFiles();
			const semesterRes = await axios.get('/api/admin/class-time/semesters');
			const semesterData = semesterRes.data.data;
			semesterList.value = semesterData.map((item: any) => {
				return {
					value: item.id,
					label: item.startYear + '-' + item.endYear + '-' + item.stage,
				}
			});
			form.semesterId = semesterList.value[semesterList.value.length - 1].value;
			dialogFormVisible.value = false;
			ElMessage.success('上传成功！');
		}
	}
}

/* 导出课表 */
const exportAsExcel = () => {
	// console.log(tableData.value);
	const title = `人工智能学院${form.place}课表 ${semesterList.value[form.semesterId - 1].label}`;
	const week = ['星期',
		'星期一', '', '', '', '',
		'星期二', '', '', '', '',
		'星期三', '', '', '', '',
		'星期四', '', '', '', '',
		'星期五', '', '', '', '',
		'星期六', '', '', '', '',
		'星期七', '', '', '', '',];
	const times = ['节次',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910',
		'0102', '0304', '0506', '0708', '0910'];
	const rc = [
		[title],
		week,
		times,
	]

	for (let i = 0; i < classrooms.value.length; ++i) {
		if (!classrooms.value[i].label.includes(form.place)) continue;
		const room = classrooms.value[i].label;
		const row = [room];
		for (let j = 1; j <= 7; ++j) {
			for (let k = form.startSection; k <= form.endSection; ++k) {
				const key = `${j}${sections.value[k].label}`;
				const value = labSourceMap.value[`${classrooms.value[i].value}${key}`];
				if (value && value.length) {
					if (value.length > 1) {
						let r = value.join(',');
						row.push(r);
					} else {
						row.push(...value);
					}
				} else {
					row.push(' ');
				}
			}
		}
		rc.push(row);
	}
	rc.push(['备注：与总课时相比，课表上的周次均预留了3-5周的时间，以备考试、节假日等事由调停使用。请各任课教师视具体情况而定，完成课程总课时的教学。']);

	const data = XLSX.utils.json_to_sheet(rc, {
		skipHeader: true
	})//此处tableData.value为表格的数据

	const rowHeights = [
		{ hpx: 30 },
		{ hpx: 20 },
		{ hpx: 20 },
	];
	for (let i = 3; i < rc.length - 1; ++i) {
		rowHeights.push({ hpx: 100 });
	}
	rowHeights.push({ hpx: 20 });
	data['!rows'] = rowHeights;

	const colWidths = new Array(36).fill({ wch: 7.5 }); // 假设有36列，每列初始宽度为20

	for (let j = 1; j < 36; j++) {
		let flag = 0;
		for (let i = 3; i < rc.length - 1; i++) {
			const cell = data[XLSX.utils.encode_cell({ r: i, c: j })];
			if (cell && cell.v !== ' ') {
				// 如果单元格不为空，增加列宽
				flag++;
			}
			// console.log(cell);
		}
		// 如果单元格为空，减小列宽
		if (flag == 0) {
			colWidths[j] = { wch: Math.min(colWidths[j].wch, 2.5) };
		}
		// console.log(flag);
	}

	data['!cols'] = colWidths;

	data['!merges'] = [
		{ s: { r: 0, c: 0 }, e: { r: 0, c: 35 } },
		{ s: { r: 1, c: 1 }, e: { r: 1, c: 5 } },
		{ s: { r: 1, c: 6 }, e: { r: 1, c: 10 } },
		{ s: { r: 1, c: 11 }, e: { r: 1, c: 15 } },
		{ s: { r: 1, c: 16 }, e: { r: 1, c: 20 } },
		{ s: { r: 1, c: 21 }, e: { r: 1, c: 25 } },
		{ s: { r: 1, c: 26 }, e: { r: 1, c: 30 } },
		{ s: { r: 1, c: 31 }, e: { r: 1, c: 35 } },
		{ s: { r: rc.length - 1, c: 0 }, e: { r: rc.length - 1, c: 35 } }
	];

	const style = {
		alignment: {
			horizontal: 'center',
			vertical: 'center',
			wrapText: true,
		},
		border: {
			top: { style: 'thin', color: { rgb: '000000' } },
			bottom: { style: 'thin', color: { rgb: '000000' } },
			left: { style: 'thin', color: { rgb: '000000' } },
			right: { style: 'thin', color: { rgb: '000000' } }
		}
	};

	const rs = data['!ref'];
	if (rs) {
		const range = XLSX.utils.decode_range(rs);
		for (let R = range.s.r; R <= range.e.r; ++R) {
			for (let C = range.s.c; C <= range.e.c; ++C) {
				const cellAddress = { r: R, c: C };
				const cellRef = XLSX.utils.encode_cell(cellAddress);
				if (data[cellRef]) {
					data[cellRef].s = style;
				}
			}
		}

		const wb = XLSX.utils.book_new();
		XLSX.utils.book_append_sheet(wb, data, `${form.place} 课表`); // test-data为自定义的sheet表名
		XLSX.writeFile(wb, `${form.place}课表 ${semesterList.value[form.semesterId - 1].label}.xlsx`); // test.xlsx为自定义的文件名
	}
}

onBeforeUnmount(() => {
	ws?.close();
	wsDsp?.close();
})

</script>

<style scoped></style>