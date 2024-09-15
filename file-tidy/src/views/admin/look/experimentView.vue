<template>
	<el-main class="main"
		:style="{ 'margin-left': !collapse.isAdminCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div style="width: 100%; height: 100%; display: inline-flex; justify-content: center; align-items: center;">
				<el-form :model="form"
					style="width: 100%; display: inline-flex; justify-content: center; align-items: center;">
					<el-form-item label="学期：" style="width: 20%;">
						<el-select-v2 v-model="form.semester" :options="semesterList" style="width: 87.5%" clearable
							value="value" label="label" />
					</el-form-item>
					<el-form-item label="实验室：" style="width: 20%;">
						<el-select-v2 v-model="form.labName" value="value" label="label" style="width: 87.5%" clearable
							:options="classrooms" placeholder="实验室名称" />
					</el-form-item>
					<el-form-item label="老师：" style="width: 20%;">
						<el-input v-model="form.teacherName" style="width: 87.5%" placeholder="老师姓名" clearable />
					</el-form-item>
					<el-form-item label="班级：" style="width: 20%;">
						<el-select-v2 v-model="form.classId" value="value" label="label" style="width: 87.5%" filterable
							remote clearable :options="classList" placeholder="班级名称" />
					</el-form-item>
					<el-form-item label="课程名称：" style="width: 20%;">
						<el-input v-model="form.courseName" style="width: 100%" placeholder="课程名称" clearable />
					</el-form-item>
					<br />
				</el-form>
			</div>
			<div style="width: 100%; display: inline-flex; justify-content: center; align-items: center;">
				<div style="width: 50%;">
					<el-button type="primary" @click="exportLabRegisterTable">导出项目登记表</el-button>
				</div>
				<div style="width: 50%;">
					<div style="width: 100%; display: inline-flex; justify-content: flex-end; align-items: center;">
						<el-button type="primary" @click="submitForm">查询</el-button>
						<el-button type="default" @click="resetForm">重置</el-button>
					</div>
				</div>
			</div>
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div style="width: 100%;" class="myHover">
				<el-table ref="tableRef" :data="experimentProjectInfo" border style="width: 100%"
					:cell-style="{ 'textAlign': 'center', 'width': '10px' }"
					:header-cell-style="{ 'text-align': 'center', 'background-color': 'white', 'color': 'black', 'width': '1vw' }"
					:row-style="{ 'fontSize': '15px', 'textAlign': 'center', 'width': '10px' }"
					:row-class-name="tableRowClassName">
					<el-table-column type="expand">
						<template #default="props">
							<div>
								<div class="line1"></div>
								<div class="line2"></div>
							</div>
							<div :class="expandedRowClassName(1)">
								<div style="display: inline-flex; width: 100%;">
									<div style="width: 50%; margin-left: auto; margin-right: auto;">
										<h2>申请信息</h2>
										<el-descriptions border :column="1" style="margin-bottom: 20px; width: 100%;">
											<el-descriptions-item label-align="left" label="实验编号：" width="15%">
												{{ props.row.courseSerial }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验课程：" width="15%">
												{{ props.row.courseName }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验人数：" width="15%">
												{{ props.row.experimentPeople }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验课时：" width="15%">
												{{ props.row.experimentHours }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验类别：" width="15%">
												{{ experimentCategoryList[props.row.experimentCategory] }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验要求：" width="15%">
												{{ experimentDemandList[props.row.experimentDemand] }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验类型：" width="15%">
												{{ experimentTypeList[props.row.experimentType] }}
											</el-descriptions-item>
										</el-descriptions>
									</div>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column type="index" label="序号" width="70%" />
					<el-table-column label="老师" prop="teacherName" />
					<el-table-column label="班级" prop="classNames" />
					<el-table-column label="课程名称" prop="courseName" />
					<el-table-column label="实验场地" prop="labName" />
					<el-table-column label="实验内容" prop="experimentContent" />
				</el-table>
			</div>
			<div
				style="width: 100%; margin-top: 1%; display: inline-flex; justify-content: center; align-items: center;">
				<el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
					:page-sizes="[10, 20, 50, 100]" :size="size" :disabled="disabled" :background="true"
					layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
					@current-change="handleCurrentPageChange" />
			</div>
		</div>
	</el-main>
</template>

<script lang="ts" setup>
import axios from 'axios';
import { ComponentSize, ElNotification } from 'element-plus';
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
// import router from '../../router';
import * as XLSX from 'xlsx-js-style';
import router from '../../../router';
import { useTaskStore } from '../../../stores/store';
import { useCollapseStore, useWebSocketStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const basicData = JSON.parse(`${localStorage.getItem('adminBasicData')}`);

const webSocketStore = useWebSocketStore();
const collapse = useCollapseStore();
const taskStore = useTaskStore();


// const parentBorder = ref(false)
// const childBorder = ref(false)
// const token = `${localStorage.getItem('token')}`;
// const ws = new WebSocket(`/ws/admin/record`, [token]); // 建立websocket连接
const tableRef = ref<any>();
const experimentProjectInfo = ref<any[]>([]);
const classList = ref<{ value: number, label: string }[]>([]); // 班级
const semesterList = ref<{ value: string, label: string }[]>([]); // 学期
const classrooms = ref<{ value: number, label: string }[]>([]); // 实验室
const experimentCategoryList = ref(['', '基础', '专业基础', '专业', '其他']); // 实验类别
const experimentDemandList = ref(['', '必修', '选修', '其他']); // 实验要求
const experimentTypeList = ref(['', '演示性', '验证性', '综合性', '设计研究', '其他']); // 实验类型
const form = reactive({ // 表单数据
	labName: null, // 实验室名称
	semester: '', // 学期
	teacherName: '', // 教师姓名
	classId: '', // 班级
	courseName: '' // 课程名称
});

const size = ref<ComponentSize>('default') // 分页大小
const pageSize = ref(10) // 每页显示条数
const disabled = ref(false) // 是否禁用分页
const currentPage = ref(1) // 当前页
const total = ref(0) // 总条数

const handleSizeChange = (val: number) => {
	//   console.log(`${val} items per page`)
	pageSize.value = val;
	submitForm();
}

const handleCurrentPageChange = (val: number) => {
	currentPage.value = val;
	submitForm();
}

const tableRowClassName = (row: any, _rowIndex: number) => {
	let state = row.row.state;
	if (state === 0) {
		return 'unitAudit';
	} else if (state === 1) {
		return 'approvalAudit'
	}
	// console.log(row.row.state);
	return 'approved';
}

const expandedRowClassName = (state: number) => {
	if (state === 0) {
		return 'expanded-row expanded-row-unitAudit';
	} else if (state === 1) {
		return 'expanded-row expanded-row-approvalAudit'
	}
	return 'expanded-row expanded-row-approved';
}

const submitForm = async () => {
	// console.log(form);
	let experimentProjectInfoRes = await axios.post(`/api/admin/experiment-project/select-project-data/${pageSize.value}/${currentPage.value}`, form);
	// console.log(experimentProjectInfoRes.data);
	let experimentProjectInfoData = experimentProjectInfoRes.data.data;
	if (experimentProjectInfoData) {
		// console.log(dspApplyInfoData);
		total.value = experimentProjectInfoData.total;
		experimentProjectInfo.value = experimentProjectInfoData.rows.map((item: any) => {
			return {
				...item,
				classNames: item.classIds.split(',').map((classId: string) => {
					return classList.value.find((classItem: any) => classItem.value === Number(classId))?.label || '';
				}).join('，'),
			}
		});
	}
}
const resetForm = () => {
	form.labName = null;
	form.semester = semesterList.value[semesterList.value.length - 1].value;
	form.teacherName = '';
	form.classId = '';
	form.courseName = '';
	submitForm();

}

const exportLabRegisterTable = async () => {
	let exportDataRes = await axios.get(`/api/admin/experiment-project/select-project-all/${form.semester}`);
	let exportData = exportDataRes.data.data.map((item: any) => {
		return {
			...item,
			classNames: item.classIds.split(',').map((classId: string) => {
				return classList.value.find((classItem: any) => classItem.value === Number(classId))?.label || '';
			}).join('，'),
		}
	});
	// const title = semesterList.value[form.semesterId - 1].label + '人工智能学院开放实验室登记表';
	const header = [
		'实验编号',
		'实验名称',
		'实验类别',
		'实验类型',
		'所属学科',
		'实验要求',
		'实验者人数',
		'每组人数',
		'实验学时数',
		'课程名称',
		'班级',
		'科任教师',
		'教室',
		'学期'
	];
	const rows = exportData.map((item: any) => {
		return [
			item.courseSerial,
			item.experimentContent,
			item.experimentCategory,
			item.experimentType,
			'0809',
			item.experimentDemand,
			item.experimentPeople,
			'1',
			item.experimentHours,
			item.courseName,
			item.classNames,
			item.teacherName,
			item.labName,
			item.semester
		];
	});
	const tableData = [header, ...rows];
	const data = XLSX.utils.json_to_sheet(tableData, {
		skipHeader: true
	});
	const rowHeights = [
		{ hpx: 20 },
	]
	for (let i = 0; i < rows.length; ++i) {
		rowHeights.push({ hpx: 30 });
	}
	data['!rows'] = rowHeights;
	const colWidths = [
		{ wch: 30 },
		{ wch: 50 },
		{ wch: 20 },
		{ wch: 20 },
		{ wch: 20 },
		{ wch: 20 },
		{ wch: 20 },
		{ wch: 30 },
		{ wch: 20 },
		{ wch: 40 },
		{ wch: 50 },
		{ wch: 20 },
		{ wch: 20 },
		{ wch: 30 },
	];
	data['!cols'] = colWidths;

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
		XLSX.utils.book_append_sheet(wb, data, form.semester); // test-data为自定义的sheet表名
		XLSX.writeFile(wb, '实验项目登记表.xlsx'); // test.xlsx为自定义的文件名
	}
}

onMounted(async () => {
	const classroomRes = basicData.classrooms;
	const classroomData = classroomRes.data;
	// console.log(data);
	classroomData.forEach((item: any) => {
		classrooms.value.push({
			'value': item.name,
			'label': item.name,
		});
	});

	const semesterRes = basicData.semesters;
	const semesterData = semesterRes.data;
	semesterData.forEach((item: any) => {
		semesterList.value.push({
			'value': item.startYear + '-' + item.endYear + '-' + item.stage,
			'label': item.startYear + '-' + item.endYear + '-' + item.stage,
		});
	});

	form.semester = semesterList.value[semesterList.value.length - 1].value;

	const classRes = await axios.get(`/api/admin/class-time/class`);
	const classData = classRes.data.data;
	classData.forEach((item: any) => {
		classList.value.push({
			'value': item.id,
			'label': item.name,
		});
	});

	let experimentProjectInfoRes = await axios.post(`/api/admin/experiment-project/select-project-data/${pageSize.value}/${currentPage.value}`, form);
	// console.log(experimentProjectInfoRes.data);
	let experimentProjectInfoData = experimentProjectInfoRes.data.data;
	if (experimentProjectInfoData) {
		// console.log(dspApplyInfoData);
		total.value = experimentProjectInfoData.total;
		experimentProjectInfo.value = experimentProjectInfoData.rows.map((item: any) => {
			return {
				...item,
				classNames: item.classIds.split(',').map((classId: string) => {
					return classList.value.find((classItem: any) => classItem.value === Number(classId))?.label || '';
				}).join('，'),
			}
		});
	}
})

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

var wsDsp: any = null;
const currentRole = ref();
const currentRoleTask = ref();

const handleMessage = (data: any) => {
	webSocketStore.setWsSpData(data);
	let resData = JSON.parse(data).data;
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

// if(!webSocketStore.wsSp.ws) {
wsDsp = init(`/ws/admin/approval`);
// 	webSocketStore.setWsSpWs(wsDsp);
// } else {
// 	wsDsp = webSocketStore.wsSp.ws;
// 	handleMessage(webSocketStore.wsSp.data);
// }

wsDsp.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		wsDsp?.send('heartbeatAsk');
		return;
	}
	// ElNotification({
	// 	title: '✉️通知',
	// 	message: '您有一条新的审批需要处理',
	// 	type: 'info',
	// })
	handleMessage(e.data);
}

// wsDsp.onerror = (e: any) => {
// 	// console.log(e, '1111');
// 	if (e.target.readyState === WebSocket.CLOSED) {
// 		// console.error('WebSocket connection failed');
// 		// 检查响应状态码
// 		fetch(`/ws/admin/approval`, {
// 			headers: {
// 				'Sec-WebSocket-Protocol': `${localStorage.getItem('token')}`
// 			}
// 		}).then(response => {
// 			if (response.status === 401) {
// 				// console.log('登录已过期，请重新登录');
// 				ElMessage.error('NOT_LOGIN');
// 				// 提示用户重新登录
// 				router.push('/login');
// 			} else {
// 				// console.log('WebSocket连接失败，请检查网络连接');
// 				ElMessage.error('服务器出错，请联系管理员');
// 			}
// 		});
// 	}
// }

onBeforeUnmount(() => {
	wsDsp?.close();
});

</script>

<style>
/* .el-table__expanded-cell {
	background-color: lightblue; /* 你可以根据需要调整颜色 
} */
</style>