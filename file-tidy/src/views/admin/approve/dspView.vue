<template>
	<el-main class="main"
		:style="{ 'margin-left': !collapse.isAdminCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
		<div style="width: 20%; height: auto; background-color: white; margin-bottom: 1.5%;">
			<el-segmented v-model="defaultOption" :options="options" block @change="changeOption"
				style="background-color: white;" />
		</div>
		<div class="myHover"
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<el-table :data="dspApplyInfo" border style="width: 100%"
				:cell-style="{ 'textAlign': 'center', 'width': '10px' }"
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
											<h4 v-if="props.row.state === 0" style="color: #409EFF;">
												申请单位审核中
											</h4>
											<h4 v-else-if="props.row.state >= 1" style="color: rgb(103, 194, 58);">
												申请单位审核通过✅</h4>
											<h4 v-else-if="props.row.state === -1" style="color: red;">
												申请单位审核未通过❌</h4>
											<p v-if="props.row.state === 0" style="color: white;"></p>
											<p v-else-if="props.row.state >= 1" style="color: rgb(103, 194, 58);">处理时间
												{{
													props.row.unitOpinionDate }} </p>
											<p v-else-if="props.row.state === -1" style="color: red;">处理时间 {{
												props.row.unitOpinionDate }} </p>
										</el-timeline-item>
										<el-timeline-item :class="timeLineClass(props.row.state, 2)"
											:color="timeLineItemColor(props.row.state, 2)" center>
											<h4 v-if="props.row.state >= -1 && props.row.state < 1"
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
											<p v-else-if="props.row.state === 2" style="color: rgb(103, 194, 58);">处理时间
												{{
													props.row.approvalOpinionDate }} </p>
											<p v-else-if="props.row.state === -2" style="color: red;">处理时间 {{
												props.row.approvalOpinionDate }} </p>
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
				<el-table-column :label="currentRole === 2 ? '审批单位审核' : `申请单位审核`">
					<template #default="props">
						<span><el-button @click="handleTagClick(props.row)"><span
									style="color: #409EFF;">审核</span></el-button></span>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<el-dialog title="审核" v-model="approvalDialogVisible" width="30%">
			<el-form ref="approvalFormRef" :model="approvalForm" label-width="auto" status-icon>
				<div v-if="currentRole === 1">
					<el-form-item label="审核意见" prop="unitOpinion" :rules="[
						{ validator: validateUnitOpinion, trigger: 'blur' }
					]">
						<el-input v-model="approvalForm.unitOpinion" placeholder="审核意见" type="textarea" />
					</el-form-item>
					<el-form-item label="审核人" prop="unitOpinionName" :rules="[
						{ validator: validateUnitOpinionName, trigger: 'blur' }
					]">
						<el-input v-model="approvalForm.unitOpinionName" placeholder="审核人" type="text" />
					</el-form-item>
				</div>
				<div v-if="currentRole === 2">
					<el-form-item label="审核意见" prop="approvalOpinion" :rules="[
						{ validator: validateOpinion, trigger: 'blur' }
					]">
						<el-input v-model="approvalForm.approvalOpinion" placeholder="审核意见" type="textarea" />
					</el-form-item>
					<el-form-item label="审核人" prop="approvalOpinionName" :rules="[
						{ validator: validateOpinionName, trigger: 'blur' }
					]">
						<el-input v-model="approvalForm.approvalOpinionName" placeholder="审核人" type="text" />
					</el-form-item>
				</div>
				<el-form-item>
					<el-button type="primary" @click="handleApprovalSubmit(approvalFormRef)">同 意</el-button>
					<el-button type="danger" @click="handleApprovalReject(approvalFormRef)">拒 绝</el-button>
					<el-button @click="approvalDialogVisible = false">取 消</el-button>
				</el-form-item>
			</el-form>
		</el-dialog>
	</el-main>
</template>

<script lang="ts" setup>
// import axios from 'axios';
import { ElMessage, FormInstance } from 'element-plus';
import { onBeforeUnmount, ref } from 'vue'
import router from '../../../router';
import { useTaskStore } from '../../../stores/store';
import { useCollapseStore } from '../../../stores/store';
import { init } from '../../../utils/ws';
 
const collapse = useCollapseStore();
const taskStore = useTaskStore();
taskStore.task = 0;

const defaultOption = ref('待审批');
const options = ['待审批', '审批记录'];
const changeOption = () => {
	router.push({ path: `/approved-list` });
}

const ws = init(`/ws/admin/approval`);
const approvalFormRef = ref<FormInstance>();
const currentRole = ref(); // 当前角色
const dspApplyInfo = ref<any[]>([]);
const states = ['空闲', '正常上课', '调课', '补课', '培训', '竞赛', '考试', '课程设计', '其他'];
const approvalDialogVisible = ref(false); // 审核弹窗
const approvalForm = ref({
	unitOpinion: '',
	unitOpinionName: '',
	unitOpinionDate: new Date(),
	approvalOpinion: '',
	approvalOpinionName: '',
	approvalOpinionDate: new Date(),
	state: 0,
	applyFormId: 1,
});

ws.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		ws?.send('heartbeatAsk');
		return;
	}
	let data = e.data;
	// console.log(JSON.parse(data));
	let resData = JSON.parse(data).data;
	currentRole.value = resData.role;
	dspApplyInfo.value = resData.result.filter((item: any) => {
		if (currentRole.value === 1 && item.state === 0) {
			return {
				...item,
				usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
			}
		} else if (currentRole.value === 2 && item.state === 1) {
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
	// console.log(dspApplyInfo.value);
}

ws.onerror = (e: any) => {
	// console.log(e, '1111');
	if (e.target.readyState === WebSocket.CLOSED) {
		// console.error('WebSocket connection failed');
		// 检查响应状态码
		fetch(`/ws/admin/approval`, {
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

const validateUnitOpinion = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请输入审核意见'));
	}
	return callback();
}

const validateUnitOpinionName = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请输入审核人'));
	}
	return callback();
}

const validateOpinion = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请输入审核意见'));
	}
	return callback();
}

const validateOpinionName = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请输入审核人'));
	}
	return callback();
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
			}
			return 'rgb(103, 194, 58)';
		case 2:
			if (state >= -1 && state < 1) {
				return 'white';
			} else if (state === 1) {
				return '#409EFF';
			} else if (state === -2) {
				return 'red';
			}
			return 'rgb(103, 194, 58)';
		case 3:
			if (state === 2) {
				return 'rgb(103, 194, 58)';
			}
			return 'white';
	}
}

const timeLineClass = (state: number, index: number) => {
	switch (index) {
		case 1:
			if (state === 0) {
				return '';
			} else if (state === -1) {
				return 'line_tail4';
			}
			return 'line_tail1';
		case 2:
			if (state >= -1 && state < 2) {
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

const combinedWeekAndSection = (week: string, section: string) => {
	let index = week.indexOf(';');
	if (index !== -1) {
		let weeks = week.split(';');
		let sections = section.split(';');

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

/* 处理审核 */
const handleTagClick = (applyFormInfo: any) => {
	/* 将已有的意见填到表单中 */
	approvalForm.value.applyFormId = applyFormInfo.applyFormId;
	approvalForm.value.unitOpinion = applyFormInfo.unitOpinion;
	approvalForm.value.unitOpinionName = applyFormInfo.unitOpinionName;
	approvalForm.value.unitOpinionDate = applyFormInfo.unitOpinionDate;
	approvalForm.value.approvalOpinion = applyFormInfo.approvalOpinion;
	approvalForm.value.approvalOpinionName = applyFormInfo.approvalOpinionName;
	approvalForm.value.approvalOpinionDate = applyFormInfo.approvalOpinionDate;
	approvalForm.value.state = applyFormInfo.state;

	approvalDialogVisible.value = true;
}

const handleApprovalSubmit = async (formEl: FormInstance | undefined) => {
	// console.log(approvalForm.value);
	if ((currentRole.value === 1 && !approvalForm.value.unitOpinion) || (currentRole.value === 2 && !approvalForm.value.approvalOpinion)) {
		ElMessage.error('请输入审核意见');
		return;
	}
	if ((currentRole.value === 1 && !approvalForm.value.unitOpinionName) || (currentRole.value === 2 && !approvalForm.value.approvalOpinionName)) {
		ElMessage.error('请输入审核人');
		return;
	}

	if (currentRole.value === 1) {
		if (!approvalForm.value.unitOpinionName) {
			ElMessage.error('请输入审核人');
			return;
		}
		approvalForm.value.state = 1;
		approvalForm.value.unitOpinionDate = new Date();
	} else {
		approvalForm.value.state = 2;
		approvalForm.value.approvalOpinionDate = new Date();
	}

	ws?.send(JSON.stringify(approvalForm.value));

	approvalDialogVisible.value = false;

	ElMessage.success('审核成功');
	resetForm(formEl);
}

const handleApprovalReject = (formEl: FormInstance | undefined) => {
	if (!approvalForm.value.unitOpinion) {
		ElMessage.error('请输入审核意见');
		return;
	}
	if (!approvalForm.value.unitOpinionName) {
		ElMessage.error('请输入审核人');
		return;
	}
	if (currentRole.value === 1) {
		approvalForm.value.state = -1;
		approvalForm.value.unitOpinionDate = new Date();
	} else {
		approvalForm.value.state = -2;
		approvalForm.value.approvalOpinionDate = new Date();
	}

	ws?.send(JSON.stringify(approvalForm.value));

	approvalDialogVisible.value = false;

	ElMessage.success('审核成功');
	resetForm(formEl);
}

/* 重置表单 */
const resetForm = (formEl: FormInstance | undefined) => {
	if (!formEl) return
	// formEl.resetFields()
	// formEl.clearValidate()
	approvalForm.value.unitOpinion = ''
	approvalForm.value.unitOpinionName = ''
	approvalForm.value.unitOpinionDate = new Date()
	approvalForm.value.approvalOpinion = ''
	approvalForm.value.approvalOpinionName = ''
	approvalForm.value.approvalOpinionDate = new Date()
	approvalForm.value.state = 0
	approvalForm.value.applyFormId = 0
	approvalDialogVisible.value = false
}

onBeforeUnmount(() => {
	ws?.close();
})

</script>

<style></style>