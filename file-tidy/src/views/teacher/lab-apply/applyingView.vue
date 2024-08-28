<template>
	<el-main class="main" :style="{'margin-left': !collapse.isCollapse? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)'}">
		<div
			style="width: 20%; height: auto; background-color: white; margin-bottom: 1.5%;">
			<el-segmented v-model="defaultOption" :options="options" block @change="changeOption" style="background-color: white;" />
		</div>
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<el-table :data="applyInfoCurrentAccount" border style="width: 100%"
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
									<el-descriptions border :column="1"
										style="margin-bottom: 20px; width: 100%;">
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
											<h4 v-if="props.row.state === 0" style="color: #409EFF;">申请单位审核中
											</h4>
											<h4 v-else-if="props.row.state >= 1 || props.row.state === -2"
												style="color: rgb(103, 194, 58);">申请单位审核通过✅</h4>
											<h4 v-else-if="props.row.state === -1" style="color: red;">
												申请单位审核未通过❌</h4>
											<p v-if="props.row.state === 0" style="color: white;"></p>
											<p v-else-if="props.row.state >= 1 || props.row.state === -2"
												style="color: rgb(103, 194, 58);">
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
											<h4 v-if="props.row.state >= -1 && props.row.state < 1"
												style="color: white;">审批单位审核</h4>
											<h4 v-else-if="props.row.state === 1" style="color: #409EFF;">
												审批单位审核中
											</h4>
											<h4 v-else-if="props.row.state === 2"
												style="color: rgb(103, 194, 58);">审批单位审核通过✅</h4>
											<h4 v-else-if="props.row.state === -2" style="color: red;">
												审批单位审核未通过❌</h4>
											<p v-if="props.row.state >= -1 && props.row.state < 2"
												style="color: white;"></p>
											<p v-else-if="props.row.state === 2"
												style="color: rgb(103, 194, 58);">
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
											<h4 v-else-if="props.row.state === 2"
												style="color: rgb(103, 194, 58);">同意申请✅</h4>
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
				<el-table-column label="审核状态">
					<template #default="props">
						<span v-if="props.row.state === 0"><el-tag>{{ `申请单位审核中` }}</el-tag></span>
						<span v-else-if="props.row.state === 1"><el-tag>{{ `审批单位审核中` }}</el-tag></span>
					</template>
				</el-table-column>
			</el-table>
		</div>
	</el-main>
</template>

<script lang="ts" setup>
// import axios from 'axios';
import { ElMessage } from 'element-plus';
import {  ref } from 'vue'
import router from '../../../router';
import { useCollapseStore } from '../../../stores/store'; 

const collapse = useCollapseStore();

const defaultOption = '正在审批'
const options = [
	'正在审批',
	'历史申请',
]
const changeOption = () => {
	router.push({ path: '/history-apply' });
}

// const parentBorder = ref(false)
// const childBorder = ref(false)
const token = `${localStorage.getItem('token')}`;
var ws = new WebSocket('/ws/teacher/apply/applying', [token]);
const applyInfoCurrentAccount = ref<any[]>([]);
const states = ['空闲', '正常上课', '调课', '补课', '培训', '竞赛', '考试', '课程设计', '其他'];

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
		// console.log(weeks, sections);
		let result = '';
		for (let i = 0; i < weeks.length - 1; ++i) {
			result += `${weeks[i]}：${sections[i]}`;
			if (i !== weeks.length - 2) {
				result += '，';
			}
		}
		return result;
	}
	return `${week}:${section}`;
}

ws.onmessage = (e: any) => {
	if(e.data === 'heartbeat') {
        ws.send('heartbeatAsk');
        return;
    }
	/* 获取当前账号的申请信息 参数1: 当前登录用户的id 参数2: 学期id */
	let data = JSON.parse(e.data).data;
	// console.log(e.data);
	if (data) {
		/* 过滤掉审核状态为2的申请信息 */
		applyInfoCurrentAccount.value = data.filter((item: any) => {
			if (item.state === 0 || item.state === 1) {
				return {
					...item,
					usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
				}
			}
		});
		applyInfoCurrentAccount.value = applyInfoCurrentAccount.value.map((item: any) => {
			return {
				...item,
				usedTime: combinedWeekAndSection(item.usedWeek, item.usedSection),
			}
		});
	}
}

ws.onerror = (e: any) => {
	// console.log(e);
	if (e.target.readyState === WebSocket.CLOSED) {
		fetch(`/ws/teacher/apply/applying`, {
			headers: {
				'Sec-WebSocket-Protocol': token,
			}
		}).then(res => {
			if (res.status === 401) {
				// console.log('登录已过期，请重新登录');
				ElMessage.error('NOT_LOGIN');
				// 提示用户重新登录
				router.push('/login');
			} else {
				ElMessage.error('服务器出错，请联系管理员');
			}
		})
	}
}

/* 路由跳转前将websocket关闭 */
router.beforeEach((_to, _from, next) => {
	ws.close();
	next();
});

</script>

<style>
</style>