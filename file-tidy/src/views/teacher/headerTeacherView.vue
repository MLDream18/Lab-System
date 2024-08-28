<template>
	<div
		style="width: 100%; height: 5%; display: flex; background-color: #303133; color: #fff; font-family: '宋体', sans-serif; font-size: 16px; font-weight: bold;">
		<el-header style="width: 10%; height: 100%;">
			<!-- 人工智能学院资料整理系统 -->
			<div style="width: 100%; height: 100%; display: flex; justify-content: flex-start;">
				<div style="margin-left: -10%;">
					<el-image style="width: 95%; height: 95%;" src="/icon/logo.png" fit="scale-down"></el-image>
				</div>
				<div class="flicker"
					style="margin-left: 10%; display: flex; justify-content: center; align-items: center;">
					<span>
						人智
					</span>
				</div>
			</div>
		</el-header>
		<el-header style="width: 90%; height: 100%; display: flex;">
			<h3 style="width: 90%; display: flex; justify-content: center; align-items: center;">
				人工智能学院资料整理系统
			</h3>
			<div style="width: 10%; height: 100%; display: flex; justify-content: flex-end;">
				<div
					style="width: 80%; margin-right: 10%; display: flex; justify-content: center; align-items: center;">
					<div
						style="width: 100%; height: 50%; display: flex; justify-content: center; align-items: center; color: white; font-weight: bold;">
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

const teacherName = ref('');

onMounted(async () => {
	let tRes = await axios.get('/api/teacher/getName');
	teacherName.value = tRes.data.data;
});

const logout = () => {
	localStorage.removeItem('token');
	localStorage.removeItem('role');
	localStorage.removeItem('basic-data');
	router.push({ path: '/login' });
}

</script>