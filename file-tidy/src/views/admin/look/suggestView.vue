<template>
	<el-main class="main" :style="{'margin-left': !collapse.isAdminCollapse? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)'}">
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<h3 style="font-weight: bold; text-align: center;">教师留言</h3>
			<el-divider></el-divider>
			<div v-for="suggest in suggestList" :key="suggest.id" style="border: 1px solid #ddd; padding: 1%; margin-bottom: 1%;">
				<div style="width: 100%; height: auto; display: flex; justify-content: space-between; align-items: center; margin-bottom: 1%;">
					<div>学期：{{ suggest.semester }}</div>
					<div>日期：{{ suggest.submitDate }}</div>
					<div>课程名称：{{ suggest.courseName }}</div>
					<div>老师名字：{{ suggest.teacherName }}</div>
					<div>实验室：{{ suggest.labName }}</div>
				</div>
				<div style="width: 100%; height: auto; display: inline-flex; justify-content: center; margin-bottom: 1%;">
					<span style="width: 4%;">留言：</span>
					<div style="width: 96%;">
						<el-text>{{ suggest.environmentCommand }}</el-text>
					</div>
				</div>
				<div style="width: 100%; height: auto; display: inline-flex; justify-content: center; align-items: center;">
					<span style="width: 4%;">附件：</span>
					<div style="width: 96%;">
						<el-link v-if="suggest.appUrl" type="primary" :href="suggest.url">{{ suggest.appUrl.substring(suggest.appUrl.lastIndexOf('\\') + 1).substring(37) }}</el-link>
						<el-text v-else>无附件</el-text>
					</div>
				</div>
				<!-- <div style="width: 100%; display: inline-flex; justify-content: center; margin-top: 1%;">
					<div style="width: 4%;">
						<span>回复：</span>
						<el-button style="width: 60%;" type="primary" @click="reply(suggest.id, suggest.adminReply)">回复</el-button>
					</div>
					<div style="width: 96%;">
						<el-input type="textarea" placeholder="请输入回复内容" v-model="suggest.adminReply"></el-input>
					</div>
				</div> -->
			</div>
			<el-divider></el-divider>
			<div
				style="width: 100%; margin-top: 1%; display: inline-flex; justify-content: center; align-items: center;">
				<el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
					:page-sizes="[10, 20, 50, 100]" :size="size" :disabled="disabled" :background="true"
					layout="total, sizes, prev, pager, next, jumper" :total="total"
					@size-change="handleSizeChange" @current-change="handleCurrentPageChange" />
			</div>
		</div>
	</el-main>
</template>

<script lang="ts" setup>
import { ComponentSize, ElMessage, ElNotification } from 'element-plus';
import { nextTick, onBeforeUnmount, ref } from 'vue';
import router from '../../../router';
import { useTaskStore } from '../../../stores/store';
import { useCollapseStore, useWebSocketStore } from '../../../stores/store';
import { init } from '../../../utils/ws';

const webSocketStore = useWebSocketStore();
const collapse = useCollapseStore();
const taskStore = useTaskStore();

var ws: any = null;
var wsDsp: any = null;
const currentRole = ref();
const currentRoleTask = ref();

const suggestList = ref<any[]>([]);

const size = ref<ComponentSize>('default') // 分页大小
const pageSize = ref(10) // 每页显示条数
const disabled = ref(false) // 是否禁用分页
const currentPage = ref(1) // 当前页
const total = ref(0) // 总条数

const handleSizeChange = (val: number) => {
	pageSize.value = val;
	if(ws.readyState !== WebSocket.CLOSED && ws.readyState !== WebSocket.CLOSING) {
		ws?.send(JSON.stringify({
			pageSize: val,
			currentPage: currentPage.value
		}))
	} else {
		ElMessage.error('WebSocket连接已断开，刷新页面尝试重新连接');
	}
}

const handleCurrentPageChange = (val: number) => {
	currentPage.value = val;
	if(ws.readyState !== WebSocket.CLOSED && ws.readyState !== WebSocket.CLOSING) {
		ws?.send(JSON.stringify({
			pageSize: pageSize.value,
			currentPage: val
		}))
	} else {
		ElMessage.error('WebSocket连接已断开，刷新页面尝试重新连接');
	}
}

// const reply = (id: number, replyContent: string) => {
	// 	ws.send(JSON.stringify({
		// 		pageSize: pageSize.value,
// 		currentPage: currentPage.value,
// 		Reply: true,
// 		suggestionId: id,
// 		replyContent: replyContent
// 	}));
// }

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

const handleMessage = (data: any) => {
	webSocketStore.setWsSuggestionData(data);
	suggestList.value = data.rows.map((item: any) => {
		// console.log(item);
		return {
			...item,
			url: `/api/admin/download/${item.appUrl}`, 
		}
	});

	total.value = suggestList.value.length;
	// console.log(suggestList.value);
}
	
// if(!webSocketStore.wsSuggestion.ws) {
ws = init('/ws/admin/suggestions');
// 	webSocketStore.setWsSuggestionWs(ws);
// } else {
// 	ws = webSocketStore.wsSuggestion.ws;
// 	handleMessage(webSocketStore.wsSuggestion.data);
// }

ws.onmessage = (e: any) => {
	if (e.data === 'heartbeat') {
		ws?.send('heartbeatAsk');
		return;
	}
	const data = JSON.parse(e.data);
	handleMessage(data);	
}

// ws.onerror = (e: any) => {
// 	// console.log(e, '1111');
// 	if (e.target.readyState === WebSocket.CLOSED) {
// 		// console.error('WebSocket connection failed');
// 		// 检查响应状态码
// 		fetch(`/ws/admin/suggestions`, {
// 			headers: {
// 				'Sec-WebSocket-Protocol': token
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

const handleMessageSp = (data: any) => {
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
wsDsp = init('/ws/admin/approval');
// 	webSocketStore.setWsSpWs(wsDsp);
// } else {
// 	wsDsp = webSocketStore.wsSp.ws;
// 	handleMessageSp(webSocketStore.wsSp.data);
// }

wsDsp.onmessage = (e: any) => {
	if(e.data === 'heartbeat') {
		wsDsp?.send('heartbeatAsk');
		return;
	}
	// ElNotification({
	// 	title: '✉️通知',
	// 	message: '您有一条新的审批需要处理',
	// 	type: 'info',
	// })
	handleMessageSp(e.data);
}

// wsDsp.onerror = (e: any) => {
// 	// console.log(e, '1111');
// 	if (e.target.readyState === WebSocket.CLOSED) {
// 		// console.error('WebSocket connection failed');
// 		// 检查响应状态码
// 		fetch(`/ws/admin/approval`, {
// 			headers: {
// 				'Sec-WebSocket-Protocol': token
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
	ws?.close();
	wsDsp?.close();
});

</script>

<style></style>