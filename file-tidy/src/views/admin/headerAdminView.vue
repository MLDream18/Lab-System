<template>
	<div class="teacher-header"
	style="width: auto; height: 5%; display: flex; background: radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%); color: rgb(56, 73, 148); font-family: '宋体', sans-serif; font-size: 20px; font-weight: bold;">
		<el-header style="width: 10%; height: 100%;">
			<!-- 人工智能学院资料整理系统 -->
			<div style="width: 120%; height: 100%; display: flex; justify-content: flex-start;">
				<div style="margin-left: -10%;">
					<el-image style="width: 95%; height: 95%;" src="/icon/logo.png" fit="scale-down"></el-image>
				</div>
				<div class="flicker"
					style="margin-left: 10%; display: flex; justify-content: center; align-items: center;">
					<span>
						人工智能学院
					</span>
				</div>
			</div>
		</el-header>
		<el-header style="width: 90%; height: 100%; display: flex;">
			<h3 style="width: 90%; display: flex; justify-content: center; align-items: center;">
				实验室信息统计及开放预约系统
			</h3>
			<div style="width: 10%; height: 100%; display: flex; justify-content: flex-end;">
				<div style="width: 80%; margin-right: 10%; display: flex; justify-content: center; align-items: center; cursor: pointer;"
					@click="logout">
					<div
						style="width: 100%; height: 50%; display: flex; justify-content: center; align-items: center;">
						退出登录</div>
					<el-image style="width: 50%; height: 50%;" src="/icon/logout.png" fit="scale-down"></el-image>
				</div>
			</div>
		</el-header>
	</div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue';
import router from '../../router';
import { useWebSocketStore } from '../../stores/store';
import axios from 'axios';


const webSocketStore = useWebSocketStore();
const logout = () => {
	webSocketStore.clearWs();
	localStorage.removeItem('token');
	localStorage.removeItem('role');
	localStorage.removeItem('adminBasicData');
	router.push({ path: '/login' });
}
onMounted(async() => {
	await axios.get('/api/getUserInfo');
})
</script>

<style>
.flicker {
	color: rgb(56, 73, 148);
	/*设置字体大小*/
	font-weight: bolder;
	/*设置字体粗细*/
	animation: flicker 2s infinite;
	/*设置动画*/
}

@keyframes flicker {
	/*创建动画*/
	0% {
		opacity: 1;
	}

	50% {
		opacity: 0.7;
	}

	100% {
		opacity: 1;
	}
}

.teacher-header {
	box-shadow: 0px 0px 10px #888888;
	border: rgb(229, 239, 253) solid 1px;
	border-radius: 15px;
	margin: 5px 5px 5px 5px;
}
</style>