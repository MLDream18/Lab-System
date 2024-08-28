<template>
	<el-main class="main" :style="{'margin-left': !collapse.isCollapse? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)'}">
		<div
			style="width: 50%; height: auto; background-color: white; margin-bottom: 1.5%;">
			<el-segmented v-model="form.place" :options="placeList" block @change="changePlace" style="background-color: white;" />
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div
				style="width: 100%; height: 100%; display: inline-flex; justify-content: center; align-items: center;">
				<el-form :model="form" style="width: 100%;" inline label-width="auto" label-position="left">
					<el-form-item label="学期：" style="width: 30%;">
						<el-select-v2 v-model="form.semesterId" :options="semesterList" style="width: 87.5%"
							value="value" label="label" />
					</el-form-item>
					<el-form-item label="实验室：" style="width: 30%;">
						<el-select-v2 v-model="form.labId" value="value" label="label" style="width: 87.5%"
							clearable :options="classrooms" placeholder="实验室名称" />
					</el-form-item>
					<el-form-item label="状态：" style="width: 30%;">
						<el-select-v2 v-model="form.state" value="value" label="label" :options="states"
							placeholder="请选择状态" style="width: 87.5%" />
					</el-form-item>
					<br />
					<el-form-item label="周次：" style="width: 30%;">
						<el-select-v2 v-model="form.startWeek" :options="weeks" placeholder="请选择起始周"
							style="width: 40%" value="value" label="label" />
						<span style="margin-left: 2%; margin-right: 2%;">至</span>
						<el-select-v2 v-model="form.endWeek" :options="weeks" placeholder="请选择结束周"
							style="width: 40%" value="value" label="label" />
					</el-form-item>
					<el-form-item label="星期：" style="width: 30%;">
						<el-select-v2 v-model="form.startWeekday" :options="weekdays" placeholder="请选择起始星期"
							style="width: 40%" />
						<span style="margin-left: 2%; margin-right: 2%;">至</span>
						<el-select-v2 v-model="form.endWeekday" :options="weekdays" placeholder="请选择结束星期"
							style="width: 40%" value="value" label="label" />
					</el-form-item>
					<el-form-item label="节次：" style="width: 30%;">
						<el-select-v2 v-model="form.startSection" :options="sections" placeholder="请选择起始节次"
							style="width: 40%" value="value" label="label" />
						<span style="margin-left: 2%; margin-right: 2%;">至</span>
						<el-select-v2 v-model="form.endSection" :options="sections" placeholder="请选择结束节次"
							style="width: 40%" value="value" label="label" />
					</el-form-item>
				</el-form>
			</div>
			<div style="width: 100%; height: 100%;">
				<el-button type="primary" @click="submitForm">查询</el-button>
				<el-button type="default" @click="resetForm">重置</el-button>
			</div>
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div style="width: 100%; margin-bottom: 1%; justify-content: space-between; display: inline-flex;">
				<span style="font-size: 20px; font-weight: bold;">正常上课：🟢</span>
				<span style="font-size: 20px; font-weight: bold;">调课：🔴</span>
				<span style="font-size: 20px; font-weight: bold;">补课：🟡</span>
				<span style="font-size: 20px; font-weight: bold;">培训：🟠</span>
				<span style="font-size: 20px; font-weight: bold;">竞赛：🔵</span>
				<span style="font-size: 20px; font-weight: bold;">考试：🟣</span>
				<span style="font-size: 20px; font-weight: bold;">课程设计：🟤</span>
				<span style="font-size: 20px; font-weight: bold;">其他：⚫</span>
			</div>
			<div style="width: 100%;">
				<el-table :data="tableData" border ref="tableRef"
					style="width: 100%; border: 1px solid black; background-color: white;"
					:cell-style="{ 'textAlign': 'center', 'height': '10vh', 'color': 'black', 'width': '1vw' }"
					:header-cell-style="{ 'text-align': 'center', 'background-color': 'white', 'color': 'black', 'width': '1vw' }"
					:row-style="{ 'fontSize': '15px', 'textAlign': 'center', }"
					@cell-dblclick="handleCellDblclick">
					<el-table-column v-for="(column, index) in tableHeader" :key="index" :label="column.label"
						:prop="column.prop" :children="column.children">
						<template v-if="column.children">
							<el-table-column v-for="(child, childIndex) in column.children" :key="childIndex"
								:label="child.label" :prop="child.prop">
								<template v-if="child.children">
									<el-table-column v-for="(grandChild, grandChildIndex) in child.children"
										:key="grandChildIndex" :label="grandChild.label" :prop="grandChild.prop"
										:column-key="grandChild.key" />
								</template>
							</el-table-column>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
		<el-dialog title="实验室使用详情" draggable v-model="detailDialogVisible" width="50%">
			<!-- <el-scrollbar height="400px"> -->
				<div v-for="(item, index) in classTimeData" :key="index" style="width: 100%;">
					<el-descriptions title="课程信息" border :column="1" style="margin-bottom: 20px; width: 70%;">
						<el-descriptions-item label-align="right" label="🖳 实验室名称：" width="15%">
							{{ item.labName }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="课程名称：">
							{{ item.courseName }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="教师名称：">
							{{ item.teacherName }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="节次：">
							{{ item.weekdaySection }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="󠁚󠁚󠁚周次：">
							{{ item.startEndWeek }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="班级：">
							{{ item.className }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="课程性质：">
							{{ item.courseNature }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="总课时：">
							{{ item.sumCourseHour }}
						</el-descriptions-item>
					</el-descriptions>
					<el-divider />
				</div>
				<div v-for="(item, index) in applyFormData" :key="index" style="width: 100%;">
					<el-descriptions title="申请信息" border :column="1" style="margin-bottom: 20px; width: 70%;">
						<el-descriptions-item label-align="right" label="🖳 实验室名称：" width="15%">
							{{ item.labName }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="实验内容：">
							{{ item.experimentContent }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="实验课程：">
							{{ item.courseName }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="实验班级：">
							{{ item.className }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="实验人数：">
							{{ item.experimentPeople }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="申请人：">
							{{ item.applicant }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="使用时间：">
							{{ item.usedTime }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="学院：">
							{{ item.applicantCollege }}
						</el-descriptions-item>
						<el-descriptions-item label-align="right" label="申请事由：">
							{{ states[item.applyReason].label }}
						</el-descriptions-item>
					</el-descriptions>
					<el-divider />
				</div>
			<!-- </el-scrollbar> -->
		</el-dialog>

	</el-main>
</template>

<script setup name="applyView" lang="ts">
// import useApplyStore from '../../stores/store';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router'
import { dragTable } from '../../../utils/common';
import { useCollapseStore } from '../../../stores/store';

const basicData = JSON.parse(`${localStorage.getItem('basic-data')}`);

const collapse = useCollapseStore();

// const applyStore = useApplyStore();
const token = `${localStorage.getItem('token')}`;
var ws = new WebSocket(`/ws/teacher/apply`, [token]); // 建立websocket连接
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
const weekdays = ref<{ value: number, label: string }[]>([
	{ value: 1, label: '1' },
	{ value: 2, label: '2' },
	{ value: 3, label: '3' },
	{ value: 4, label: '4' },
	{ value: 5, label: '5' },
	{ value: 6, label: '6' },
	{ value: 7, label: '7' }
]); // 星期
const weekdayCN = ['', '一', '二', '三', '四', '五', '六', '日']; // 中文星期
const sections = ref<{ value: number, label: string }[]>([
	{ value: 0, label: '0102' },
	{ value: 1, label: '0304' },
	{ value: 2, label: '0506' },
	{ value: 3, label: '0708' },
	{ value: 4, label: '0910' },
]); // 节次
const states = ref<{ value: number, label: string }[]>([
	{ value: 0, label: '空闲' },
	{ value: 1, label: '正常上课' },
	{ value: 2, label: '调课' },
	{ value: 3, label: '补课' },
	{ value: 4, label: '培训' },
	{ value: 5, label: '竞赛' },
	{ value: 6, label: '考试' },
	{ value: 7, label: '课程设计' },
	{ value: 8, label: '其他' },
]); // 状态
const placeList = ref<string[]>([
	'逸夫楼',
	'博达楼',
	'文综楼',
]); // 地点
const statesIcon = [' ', '🟢', '🔴', '🟡', '🟠', '🔵', '🟣', '🟤', '⚫']; // 状态图标
const labSources = ref<any[]>([]); // 实验室资源
const labSourceMap = ref<any>({}); // 实验室资源映射
const idMap = ref<any>({}); // 实验室资源映射
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
	state: null, // 状态
	semesterId: 0, // 学期
	place: '逸夫楼'
});
const idsClassTime = ref<any[]>([]); // 课表ID集合
const idsApplyForm = ref<any[]>([]); // 申请表ID集合
const classTimeData = ref<any[]>([]); // 课表数据
const applyFormData = ref<any[]>([]); // 申请表单数据
const detailDialogVisible = ref(false); // 实验室使用详情弹窗
const router = useRouter(); // 路由
// const drawer = ref(false); // 抽屉
const tableRef = ref(); // 表格拖拽

// 阻止URL变化
// router.beforeEach((_to, _from, next) => {
//   	next(false);
// });

const changePlace = async (val: any) => {
	form.place = val;
	let place = val === '文综楼'? '文科综合实训大楼' : val;
	const res = await axios.get(`/api/teacher/lab/apply/classrooms/${place}`);
	const data = res.data.data;
	// console.log(data);
	classrooms.value = data.map((item: any) => {
		return {
			'value': item.id,
			'label': item.name,
		};
	});
	form.labId = null;
	submitForm();
}

/* 获取实验室ID */
const getLabId = (labName: string) => {
	for (let i = 0; i < classrooms.value.length; ++i) {
		if (classrooms.value[i].label === labName) {
			return classrooms.value[i].value;
		}
	}
};

/* 获取实验室名称 */
const getLabName = (labId: number) => {
	for (let i = 0; i < classrooms.value.length; ++i) {
		if (classrooms.value[i].value === labId) {
			return classrooms.value[i].label;
		}
	}
};

const combinedWeekAndSection = (week: string, section: string) => {
	let index = week.indexOf(';');
	if (index !== -1) {
		let weeks = week.split(';');
		let sections = section.split(';');
		// console.log(weeks, sections);
		let result = '';
		for (let i = 0; i < weeks.length - 1; ++i) {
			result += `${weeks[i]}：${sections[i]}`;
			if (i !== weeks.length - 2) {
				result += '；';
			}
		}
		return result;
	}
	return `${week}:${section}`;
}

ws.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		ws.send('heartbeatAsk');
		return;
	}

	labSources.value = JSON.parse(e.data).data;
	// console.log(labSources.value.filter((item: any) => {
	// 	return item.labId === 1 && item.weekday === 4 && item.section === '0102';
	// }))
	/* 清空 labSourceMap idMap */
	labSourceMap.value = {};
	idMap.value = {};

	/* 初始化 labSourceMap idMap */
	labSources.value.forEach((item: any) => {
		let key = `${item.labId}${item.weekday}${item.section}`; // 教室id + 星期 + 节次
		labSourceMap.value[key] = [];
		idMap.value[key] = [];
	});

	/* 填充 labSourceMap idMap */
	labSources.value.forEach((item: any) => {
		let key = `${item.labId}${item.weekday}${item.section}`; // 教室id + 星期 + 节次
		if (labSourceMap.value[key].indexOf(statesIcon[item.state]) === -1) {
			labSourceMap.value[key].push(statesIcon[item.state]);
		}
		idMap.value[key].push({
			applyFormId: item.applyFormId,
			classTimeId: item.classTimeId,
		});
	});

	/* 置空 tableData tableHeader */
	tableHeader.value = [{ prop: 'room', label: '教室' }, { label: '星期', children: [] }];
	tableData.value = [];

	/* 初始化空表格 */
	/* 初始化表头 */
	for (let i = form.startWeekday; i <= form.endWeekday; ++i) {
		tableHeader.value[1].children.push({ label: `星期${weekdayCN[i]}`, children: [] });
		let tmp = [];
		for (let j = form.startSection; j <= form.endSection; ++j) {
			// console.log(j);
			tmp.push({
				prop: `${i}${sections.value[j].label}`,
				label: `${sections.value[j].label}`,
				key: `${i}${sections.value[j].label}`,
			});
		}
		tableHeader.value[1].children.at(-1).children = tmp;
	}

	// console.log(tableHeader.value);

	/* 初始化表格字段名 */
	if (!form.labId) {
		for (let i = 0; i < classrooms.value.length; ++i) {
			let tmp: any = {};
			let cnt = 0;
			for (let j = form.startWeekday; j <= form.endWeekday; ++j) {
				for (let k = form.startSection; k <= form.endSection; ++k) {
					/* tmp[j: 星期 + k: 节次] = labSourceMap[教室id + 星期j + 节次k] */
					tmp[`${j}${sections.value[k].label}`] = labSourceMap.value[`${classrooms.value[i].value}${j}${sections.value[k].label}`];
					// console.log(labSourceMap.value[`${classrooms.value[i].value}${j}${sections.value[k].label}`]);
					if (labSourceMap.value[`${classrooms.value[i].value}${j}${sections.value[k].label}`]) {
						++cnt;
					} else {
						tmp[`${j}${sections.value[k].label}`] = ' ';
					}
				}
			}
			if (form.state === 0 && cnt === (form.endSection - form.startSection + 1) * (form.endWeekday - form.startWeekday + 1)) {
				continue;
			}
			tableData.value.push({
				room: classrooms.value[i].label,
				...tmp,
			});
		}
	} else {
		let tmp: any = {};
		let cnt = 0;
		for (let j = form.startWeekday; j <= form.endWeekday; ++j) {
			for (let k = form.startSection; k <= form.endSection; ++k) {
				/* tmp[j: 星期 + k: 节次] = labSourceMap[教室id + 星期j + 节次k] */
				tmp[`${j}${sections.value[k].label}`] = labSourceMap.value[`${form.labId}${j}${sections.value[k].label}`];
				if (labSourceMap.value[`${form.labId}${j}${sections.value[k].label}`]) {
					++cnt;
				} else {
					tmp[`${j}${sections.value[k].label}`] = ' ';
				}
			}
		}
		if (!(form.state === 0 && cnt === (form.endSection - form.startSection + 1) * (form.endWeekday - form.startWeekday + 1))) {
			tableData.value.push({
				room: getLabName(form.labId),
				...tmp,
			});
		}
	}

	dragTable(tableRef);
}

const submitForm = async () => {
	if (form.endWeek < form.startWeek) {
		ElMessage.error('开始周不能大于结束周！');
		return;
	}
	if (form.endWeekday < form.startWeekday) {
		ElMessage.error('开始星期不能大于结束星期！');
		return;
	}
	if (form.endSection < form.startSection) {
		ElMessage.error('开始节次不能大于结束节次！');
		return;
	}

	const formData = {
		'labId': form.labId,
		'startWeek': form.startWeek,
		'endWeek': form.endWeek,
		'startWeekday': form.startWeekday,
		'endWeekday': form.endWeekday,
		'startSection': sections.value[form.startSection].label,
		'endSection': sections.value[form.endSection].label,
		'state': form.state,
		'semesterId': form.semesterId,
	};

	ws.send(JSON.stringify(formData));
};

/* 双击表格事件 */
const handleCellDblclick = async (row: any, column: any, _cell: any, _event: any) => {
	// console.log(column.columnKey)
	if (!column.columnKey) {
		return;
	}
	/* 重置数据 */
	classTimeData.value = [];
	applyFormData.value = [];
	idsClassTime.value = [];
	idsApplyForm.value = [];
	// console.log(row);
	// console.log(idMap.value)
	// console.log(column.columnKey);
	// console.log(idMap.value[`${getLabId(row.room)}${column.columnKey}`]);
	// idMap[`${getLabId(row.room)}${column.columnKey}`] 对应的是申请表id: applyFormId 课表id: classTimeId
	if (idMap.value[`${getLabId(row.room)}${column.columnKey}`]) {
		idsClassTime.value = idMap.value[`${getLabId(row.room)}${column.columnKey}`].filter((item: any) => { if (item.classTimeId) { return item } }).map((item: any) => { return { id: item.classTimeId } });
		idsApplyForm.value = idMap.value[`${getLabId(row.room)}${column.columnKey}`].filter((item: any) => { if (item.applyFormId) { return item } }).map((item: any) => { return { id: item.applyFormId } });
	}
	// console.log(idsClassTime.value);
	// console.log(idsApplyForm.value);
	let classTimeRes = await axios.post('/api/teacher/lab/search-class-time', idsClassTime.value);
	// console.log(classTimeRes.data.data);
	let applyFormRes = await axios.post('/api/teacher/lab/search-apply-form', idsApplyForm.value);
	// console.log(applyFormRes.data, classTimeRes.data);
	if (classTimeRes.data.data || applyFormRes.data.data) {
		classTimeData.value = classTimeRes.data.data;

		let data = applyFormRes.data.data;
		if (data) {
			applyFormData.value = data.map((item: any) => {
				return {
					...item,
					usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
				}
			});
		}

		// console.log(applyFormData.value);
		detailDialogVisible.value = true;
	} else {
		const routeUrl = router.resolve({
			name: 'teacher-applyFill',
			query: {
				labName: row.room,
				weekdaySection: column.columnKey,
				week: `${form.startWeek}-${form.endWeek}`,
				semesterId: form.semesterId,
			},
		});

		window.open(routeUrl.href, '_blank');
	}
}

/* 确认关闭抽屉 */
// const handleClose = (done: () => void) => {
// 	ElMessageBox.confirm('Are you sure you want to close this?')
// 	.then(() => {
// 		done()
// 	})
// 	.catch(() => {
// 		// catch error
// 	})
// }

const resetForm = () => {
	form.labId = null;
	form.startWeek = 1;
	form.endWeek = 20;
	form.startWeekday = 1;
	form.endWeekday = 7;
	form.startSection = 0;
	form.endSection = 4;
	form.state = null;
	submitForm();
};

ws.onerror = (e: any) => {
	// console.log(e, '1111');
	if (e.target.readyState === WebSocket.CLOSED) {
		// console.error('WebSocket connection failed');
		// 检查响应状态码 模拟握手过程
		fetch(`/ws/teacher/apply`, {
			headers: {
				'Sec-WebSocket-Protocol': token
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

onMounted(async () => {
	const res = basicData.placeLabs;
	const data = res.data;
	// console.log(data);
	data.forEach((item: any) => {
		classrooms.value.push({
			'value': item.id,
			'label': item.name,
		});
	});

	const semesterRes = basicData.semesters;
	const semesterData = semesterRes.data;
	semesterList.value = semesterData.map((item: any) => {
		return {
			value: item.id,
			label: item.startYear + '-' + item.endYear + '-' + item.stage,
		}
	});
	form.semesterId = semesterList.value[semesterList.value.length - 1].value;
});

/* 路由跳转前将websocket关闭 */
router.beforeEach((_to, _from, next) => {
	ws.close();
	next();
});


</script>

<style></style>
