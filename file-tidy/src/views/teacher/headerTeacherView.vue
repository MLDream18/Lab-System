<template>
	<div class="teacher-header"
		style="width: auto; height: 5%; display: flex; background: radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%); color: rgb(56, 73, 148); font-family: '宋体', sans-serif; font-size: 20px; font-weight: bold;">
		<el-header style="width: 10%; height: 100%;">
			<!-- 人工智能学院资料整理系统 -->
			<div style="width: 120%; height: 100%; display: flex; justify-content: flex-start;">
				<div style="margin-left: -10%;">
					<el-image style="width: 90%; height: 95%;" src="/icon/logo.png" fit="scale-down"></el-image>
				</div>
				<div class="flicker"
					style="width: auto; margin-left: 10%; display: flex; justify-content: center; align-items: center;">
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
				<div
					style="width: 80%; margin-right: 10%; display: flex; justify-content: center; align-items: center;">
					<div
						style="width: 100%; height: 50%; display: flex; justify-content: center; align-items: center;">
						{{ teacherName }}</div>
					<el-image style="width: 50%; height: 50%; cursor: pointer;" @click="logout" src="/icon/logout.png"
						fit="scale-down"></el-image>
				</div>
			</div>
		</el-header>
	</div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import axios from 'axios';
import router from '../../router';
import { useWebSocketStoreTeacher } from '../../stores/store';


const teacherName = ref('');
const webSocketStore = useWebSocketStoreTeacher();

onMounted(async () => {
	await axios.get('/api/getUserInfo');
	let tRes = await axios.get('/api/teacher/getName');
	teacherName.value = tRes.data.data;
});

const logout = () => {
	webSocketStore.clearWs();
	localStorage.removeItem('token');
	localStorage.removeItem('role');
	localStorage.removeItem('teacherBasicData');
	router.push({ path: '/login' });
}

</script>

<style lang="scss" scoped>
.teacher-header {
	box-shadow: 0px 0px 10px #888888;
	border: rgb(229, 239, 253) solid 1px;
	border-radius: 15px;
	margin: 5px 5px 5px 5px;
}
</style>