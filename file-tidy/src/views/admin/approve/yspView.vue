<template>
	<el-main class="main"
		:style="{ 'margin-left': !collapse.isAdminCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
		<div style="width: 20%; height: auto; background-color: white; margin-bottom: 1.5%;">
			<el-segmented v-model="defaultOption" :options="options" block @change="changeOption"
				style="background-color: white;" />
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<div style="width: 100%; height: 100%; display: inline-flex; justify-content: center; align-items: center;">
				<el-form :model="form"
					style="width: 100%; display: inline-flex; justify-content: center; align-items: center;">
					<el-form-item label="学期：" style="width: 33.3%;">
						<el-select-v2 v-model="form.semesterId" :options="semesterList" style="width: 87.5%;"
							value="value" label="label" />
					</el-form-item>
					<el-form-item label="实验室：" style="width: 33.3%;">
						<el-select-v2 v-model="form.labName" value="value" label="label" style="width: 87.5%" clearable
							:options="classrooms" placeholder="实验室名称" />
					</el-form-item>
					<el-form-item label="状态：" style="width: 33.3%;">
						<el-select-v2 v-model="form.state" value="value" label="label" :options="applyStates" clearable
							placeholder="请选择状态" style="width: 100%" />
					</el-form-item>
				</el-form>
			</div>
			<div style="width: 100%; height: 100%; display: inline-flex; justify-content: center; align-items: center;">
				<div style="width: 50%;">
					<el-button type="primary" @click="exportApplyTable"
						:disabled="!exportApplyTableButtonEnabled">导出申请表</el-button>
					<el-button type="primary" @click="exportLabRegisterTable">导出实验室登记表</el-button>
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
				<el-table ref="tableRef" :data="dspApplyInfo" border style="width: 100%" highlight-current-row
					@row-click="handleCurrentChange" :cell-style="{ 'textAlign': 'center', 'width': '10px' }"
					:header-cell-style="{ 'text-align': 'center', 'background-color': 'white', 'color': 'black', 'width': '1vw' }"
					:row-style="{ 'fontSize': '15px', 'textAlign': 'center', 'width': '10px' }"
					:row-class-name="tableRowClassName">
					<el-table-column type="index" label="序号" width="70%" />
					<el-table-column type="expand">
						<template #default="props">
							<div>
								<div class="line1"></div>
								<div class="line2"></div>
							</div>
							<div :class="expandedRowClassName(props.row.state)">
								<div style="display: inline-flex; width: 100%;">
									<div style="width: 50%;">
										<h2>申请信息</h2>
										<el-descriptions border :column="1" style="margin-bottom: 20px; width: 100%;">
											<el-descriptions-item label-align="left" label="实验内容：" width="15%">
												{{ props.row.experimentContent }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验课程：" width="15%">
												{{ props.row.courseName }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验班级：" width="15%">
												{{ props.row.className }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验人数：" width="15%">
												{{ props.row.experimentPeople }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="实验课时：" width="15%">
												{{ props.row.sumCourseHours }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="申请事由：" width="15%">
												{{ states[props.row.applyReason] }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="申请单位审核意见：" width="15%">
												{{ props.row.unitOpinion }}
											</el-descriptions-item>
											<el-descriptions-item label-align="left" label="审批单位审核意见：" width="15%">
												{{ props.row.approvalOpinion }}
											</el-descriptions-item>
										</el-descriptions>
									</div>
									<div style="width: 50%; margin-left: 5%;">
										<h2>审核进度</h2>
										<el-timeline>
											<el-timeline-item :class="timeLineClass(props.row.state, 1)"
												:color="timeLineItemColor(props.row.state, 1)" center>
												<h4 v-if="props.row.state === -3" style="color: white;">申请单位审核中
												</h4>
												<h4 v-else-if="props.row.state >= 1 || props.row.state === -2" style="color: rgb(103, 194, 58);">
													申请单位审核通过✅</h4>
												<h4 v-else-if="props.row.state === -1" style="color: red;">
													申请单位审核未通过❌</h4>
												<p v-if="props.row.state === 0" style="color: white;"></p>
												<p v-else-if="props.row.state >= 1 || props.row.state === -2" style="color: rgb(103, 194, 58);">
													审核人：{{ props.row.unitOpinionName }}
													处理时间： {{ props.row.unitOpinionDate }}
												</p>
												<p v-else-if="props.row.state === -1" style="color: red;">
													审核人：{{ props.row.unitOpinionName }}
													处理时间： {{ props.row.unitOpinionDate }}
												</p>
											</el-timeline-item>
											<el-timeline-item :class="timeLineClass(props.row.state, 2)"
												:color="timeLineItemColor(props.row.state, 2)" center>
												<h4 v-if="props.row.state >= -1 && props.row.state < 1 || props.row.state === -3"
													style="color: white;">审批单位审核</h4>
												<h4 v-else-if="props.row.state === 1" style="color: #409EFF;">
													审批单位审核中
												</h4>
												<h4 v-else-if="props.row.state === 2" style="color: rgb(103, 194, 58);">
													审批单位审核通过✅</h4>
												<h4 v-else-if="props.row.state === -2" style="color: red;">
													审批单位审核未通过❌</h4>
												<p v-if="props.row.state >= -1 && props.row.state < 2"
													style="color: white;"></p>
												<p v-else-if="props.row.state === 2" style="color: rgb(103, 194, 58);">
													审核人：{{ props.row.approvalOpinionName }}
													处理时间： {{ props.row.approvalOpinionDate }}
												</p>
												<p v-else-if="props.row.state === -2" style="color: red;">
													审核人：{{ props.row.approvalOpinionName }}
													处理时间： {{ props.row.approvalOpinionDate }}
												</p>
											</el-timeline-item>
											<el-timeline-item :class="timeLineClass(props.row.state, 3)"
												:color="timeLineItemColor(props.row.state, 3)" center>
												<h4 v-if="props.row.state !== 2" style="color: white;">同意申请</h4>
												<h4 v-else-if="props.row.state === 2" style="color: rgb(103, 194, 58);">
													同意申请✅</h4>
											</el-timeline-item>
										</el-timeline>
									</div>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column label="提交日期" prop="submitDate" />
					<el-table-column label="申请人姓名" prop="applicant" />
					<el-table-column label="申请实验室" prop="labName" />
					<el-table-column label="使用时间" prop="usedStartDate" />
					<el-table-column label="时间段" prop="usedTime" />
					<el-table-column label="审核结果">
						<template #default="props">
							<span v-if="props.row.state === 1"><el-tag>申请单位已审核</el-tag></span>
							<span v-if="props.row.state === 2"><el-tag type="success">同意</el-tag></span>
							<span v-if="props.row.state === -1"><el-tag type="danger">申请单位拒绝</el-tag></span>
							<span v-else-if="props.row.state === -2"><el-tag type="danger">审批单位拒绝</el-tag></span>
							<span v-if="props.row.state === -3"><el-tag type="danger">受理过期</el-tag></span>
						</template>
					</el-table-column>
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
import { ComponentSize, ElMessage } from 'element-plus';
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import router from '../../../router';
import Docxtemplater from 'docxtemplater';
import { saveAs } from 'file-saver';
import PizZip from 'pizzip';
import * as XLSX from 'xlsx-js-style';
import { useCollapseStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const basicData = JSON.parse(`${localStorage.getItem('basic-data')}`);

const collapse = useCollapseStore();

const defaultOption = ref('审批记录');
const options = ['待审批', '审批记录'];
const changeOption = () => {
	router.push({ path: `/pending-approval` });
}

// const parentBorder = ref(false)
// const childBorder = ref(false)
const ws = init(`/ws/admin/approval/record`);// 建立WebSocket连接, 监听是否有申请
// const bc = new BroadcastChannel('teacher-apply'); // 建立广播通道
const tableRef = ref<any>();
const dspApplyInfo = ref<any[]>([]);
const semesterList = ref<{ value: number, label: string }[]>([]); // 学期
const classrooms = ref<{ value: number, label: string }[]>([]); // 实验室
const applyStates = ref<{ value: number, label: string }[]>([]); // 状态
const form = reactive({ // 表单数据
	labName: null, // 实验室名称
	state: null, // 状态
	semesterId: 0, // 学期
});

const states = ['空闲', '正常上课', '调课', '补课', '培训', '竞赛', '考试', '课程设计', '其他'];
const exportApplyTableButtonEnabled = ref(false);
const currentRow = ref<any>(null);

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

const setCurrent = (row?: any) => {
	tableRef.value!.setCurrentRow(row)
}

const handleCurrentChange = (val: any) => {
	// console.log(val);
	// console.log(currentRow.value != null);
	if (currentRow.value != null && currentRow.value.applyFormId === val.applyFormId) {
		exportApplyTableButtonEnabled.value = false;
		setCurrent();
		// console.log('禁用导出按钮');
		currentRow.value = null; // 取消选中
		return;
	}
	// console.log('启用导出按钮');
	exportApplyTableButtonEnabled.value = true;
	currentRow.value = val;
	// console.log(currentRow.value);
	// console.log(currentRow.value.applyFormId === val.applyFormId);
}


const exportApplyTable = async () => {
	if (currentRow.value.state !== 2) {
		ElMessage.error('未同意的申请无法导出');
		return;
	}
	// 加载模板文件
	const response = await fetch('/template.docx');
	const arrayBuffer = await response.arrayBuffer();

	// 初始化 JSZip 对象
	const zip = new PizZip(arrayBuffer);
	// console.log(currentRow.value);
	const doc = new Docxtemplater(zip, { paragraphLoop: true, linebreaks: true });
	doc.setData({
		labName: currentRow.value.labName,
		usedTime: currentRow.value.usedTime,
		applyReason: states[currentRow.value.applyReason],
		experimentContent: currentRow.value.experimentContent,
		courseName: currentRow.value.courseName,
		className: currentRow.value.className,
		experimentPeople: currentRow.value.experimentPeople,
		unitOpinion: currentRow.value.unitOpinion,
		approvalOpinion: currentRow.value.approvalOpinion,
		unitOpinionName: currentRow.value.unitOpinionName,
		unitOpinionDateYear: (currentRow.value.unitOpinionDate as string).slice(0, 4),
		unitOpinionDateMonth: (currentRow.value.unitOpinionDate as string).slice(5, 7),
		unitOpinionDateDay: (currentRow.value.unitOpinionDate as string).slice(8, 10),
		approvalOpinionName: currentRow.value.approvalOpinionName,
		approvalOpinionDateYear: (currentRow.value.approvalOpinionDate as string).slice(0, 4),
		approvalOpinionDateMonth: (currentRow.value.approvalOpinionDate as string).slice(5, 7),
		approvalOpinionDateDay: (currentRow.value.approvalOpinionDate as string).slice(8, 10),
		submitDateYear: (currentRow.value.submitDate as string).slice(0, 4),
		submitDateMonth: (currentRow.value.submitDate as string).slice(5, 7),
		submitDateDay: (currentRow.value.submitDate as string).slice(8, 10),
		applicant: currentRow.value.applicant,
		applicantTelephone: currentRow.value.applicantTelephone,
		applicantCollege: currentRow.value.applicantCollege,
	});

	try {
		doc.render();
	} catch (error) {
		const e = error as Error;
		console.error('Error rendering document:', e);
		return;
	}
	// 生成Blob对象
	const blob = doc.getZip().generate({
		type: 'blob',
		mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
	});

	// 保存文件
	saveAs(blob, '申请表.docx');
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

const timeLineItemColor = (state: number, index: number) => {
	switch (index) {
		case 1:
			if (state === 0) {
				return '#409EFF';
			} else if (state === -1) {
				return 'red';
			} else if (state === -3) {
				return '';
			}
			return 'rgb(103, 194, 58)';
		case 2:
			if (state >= -1 && state < 1) {
				return 'white';
			} else if (state === 1) {
				return '#409EFF';
			} else if (state === -2) {
				return 'red';
			} else if (state === -3) {
				return '';
			}
			return 'rgb(103, 194, 58)';
		case 3:
			if (state === 2) {
				return 'rgb(103, 194, 58)';
			} else if (state === -3) {
				return '';
			}
			return 'white';
	}
}

const timeLineClass = (state: number, index: number) => {
	switch (index) {
		case 1:
			if (state === 0 || state === -3) {
				return '';
			} else if (state === -1) {
				return 'line_tail4';
			}
			return 'line_tail1';
		case 2:
			if (state >= -1 && state < 2 || state === -3) {
				return '';
			} else if (state === -2) {
				return 'line_tail4';
			}
			return 'line_tail2';
		case 3:
			if (state === 2) {
				return 'line_tail3';
			}
			return '';
	}
}

const submitForm = async () => {
	ws?.send(JSON.stringify({
		pageSize: pageSize.value,
		currentPage: currentPage.value,
		form: form,
	}));
}
const resetForm = () => {
	form.labName = null;
	form.state = null;
	submitForm();

}

const combinedWeekAndSection = (week: string, section: string) => {
	let index = week.indexOf(';');
	if (index !== -1) {
		let weeks = week.split(';');
		let sections = section.split(';');
		// console.log(weeks, sections);
		let result = '';
		for (let i = 0; i < weeks.length - 1; ++i) {
			result += `周次：${weeks[i]} 节次：${sections[i]}`;
			if (i !== weeks.length - 2) {
				result += '；\n';
			}
		}
		return result;
	}
	return `周次：${week} 节次：${section}`;
}

ws.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		ws?.send('heartbeatAsk');
		return;
	}
	let dspApplyInfoData = JSON.parse(e.data).data;
	// console.log(dspApplyInfoData);
	if (dspApplyInfoData) {
		dspApplyInfo.value = dspApplyInfoData.rows.filter((item: any) => {
			if (item.state !== 0) {
				return {
					...item,
					usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
				}
			}
		});
		dspApplyInfo.value = dspApplyInfo.value.map((item: any) => {
			return {
				...item,
				usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
			}
		});
		total.value = dspApplyInfoData.total;
	}
}

ws.onerror = async (e: any) => {
	// console.log(e, '1111');
	if (e.target.readyState === WebSocket.CLOSED) {
		// console.error('WebSocket connection failed');
		// 检查响应状态码 模拟握手过程
		fetch(`/ws/admin/approval/record`, {
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


const exportLabRegisterTable = async () => {
	const registerDataRes = await axios.get(`/api/admin/approval/get-lab-register-data/${form.semesterId}`);

	const registerData = registerDataRes.data.data;
	const title = semesterList.value[form.semesterId - 1].label + '人工智能学院开放实验室登记表';
	const header = [
		'序号',
		'日期',
		'项目或实验名称',
		'人数',
		'课时',
		'人时数',
		'申请人',
		'实验场地'
	];
	const rows = registerData.map((item: any, index: number) => {
		return [
			index + 1,
			item.usedStartDate,
			item.experimentContent,
			item.experimentPeople,
			item.sumCourseHours,
			item.sumCourseHours * item.experimentPeople,
			item.applicant,
			item.labName
		];
	});
	const tableData = [[title], header, ...rows];
	const data = XLSX.utils.json_to_sheet(tableData, {
		skipHeader: true
	});
	const rowHeights = [
		{ hpx: 30 },
	]
	for (let i = 0; i < rows.length; ++i) {
		rowHeights.push({ hpx: 20 });
	}
	data['!rows'] = rowHeights;
	const colWidths = [
		{ wch: 10 },
		{ wch: 20 },
		{ wch: 50 },
		{ wch: 10 },
		{ wch: 10 },
		{ wch: 10 },
		{ wch: 10 },
		{ wch: 30 },
	];
	data['!cols'] = colWidths;

	data['!merges'] = [
		{ s: { r: 0, c: 0 }, e: { r: 0, c: 7 } }
	]

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
		XLSX.utils.book_append_sheet(wb, data, `${semesterList.value[form.semesterId - 1].label}`); // test-data为自定义的sheet表名
		XLSX.writeFile(wb, `实验室开放登记表 ${semesterList.value[form.semesterId - 1].label}.xlsx`); // test.xlsx为自定义的文件名
	}
}

onMounted(async () => {
	const classroomRes = basicData.classrooms;
	const classroomData = classroomRes.data;

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
			'value': item.id,
			'label': item.startYear + '-' + item.endYear + '-' + item.stage,
		});
	});
	form.semesterId = semesterList.value[semesterList.value.length - 1].value;

	applyStates.value.push({
		value: 0,
		label: '申请单位审核中',
	});

	applyStates.value.push({
		value: 1,
		label: '审批单位审核中',
	});

	applyStates.value.push({
		value: 2,
		label: '同意',
	});

	applyStates.value.push({
		value: -3,
		label: '受理过期',
	});

	applyStates.value.push({
		value: -1,
		label: '申请单位拒绝',
	});

	applyStates.value.push({
		value: -2,
		label: '审批单位拒绝',
	});
})

onBeforeUnmount(() => {
	ws?.close();
});

</script>

<style></style>