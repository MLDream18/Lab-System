<template>
	<div style="height: 100%; position: fixed; width: 100%" v-if="$route.meta.keepAlive">
		<header-admin-view v-if="roleStore.role === 'admin'"></header-admin-view>
		<header-teacher-view v-if="roleStore.role === 'teacher'"></header-teacher-view>
		<div style="width: 100%; height: 100%; display: inline-flex">
			<aside-admin-view v-if="roleStore.role === 'admin'"></aside-admin-view>
			<aside-teacher-view v-if="roleStore.role === 'teacher'"></aside-teacher-view>
			<transition name="fade" mode="out-in">
				<router-view :key="$route.path" />
			</transition>
		</div>
	</div>
	<transition name="fade" mode="out-in" v-if="!$route.meta.keepAlive">
		<router-view :key="$route.path" />
	</transition>
</template>

<script setup>
import { ref } from "vue";
import { onMounted } from "vue";
import axios from "axios";
import headerAdminView from "./views/admin/headerAdminView.vue";
import headerTeacherView from "./views/teacher/headerTeacherView.vue";
import asideTeacherView from "./views/teacher/asideTeacherView.vue";
import asideAdminView from "./views/admin/asideAdminView.vue";
import { useCollapseStore, useUserStore } from "./stores/store";

const collapse = useCollapseStore();
const roleStore = useUserStore();
roleStore.role = `${localStorage.getItem("role")}`;

// onMounted(async () => {
// 	if(`${localStorage.getItem('role')}` === 'admin') {
// 		const classroomRes = await axios.get('/api/admin/class-time/classrooms');
// 		const semesterRes = await axios.get('/api/admin/class-time/semesters');
// 		localStorage.setItem('basic-data', JSON.stringify({ 'classrooms': classroomRes.data, 'semesters': semesterRes.data }));
// 	} else if(`${localStorage.getItem('role')}` === 'teacher') {
// 		axios.defaults.headers['token'] = `${localStorage.getItem('token')}`;
// 		const semesterRes = await axios.get('/api/teacher/select-semesters');
// 		const courseNameByIdRes = await axios.get(`/api/teacher/course/get-course-names-by-semester-id/${semesterRes.data.data.at(-1).id}`);
// 		const classRes = await axios.get(`/api/teacher/lab/apply/getClassList`);
// 		const teacherRes = await axios.get(`/api/teacher/lab/apply/getTeacherList`);
// 		const labRes = await axios.get(`/api/teacher/lab/apply/getLabList`);
// 		const placeLabRes = await axios.get(`/api/teacher/lab/apply/classrooms/逸夫楼`);
// 		localStorage.setItem('basic-data', JSON.stringify({
// 			'semesters': semesterRes.data,
// 			'courseNames': courseNameByIdRes.data,
// 			'classes': classRes.data,
// 			'teachers': teacherRes.data,
// 			'labs': labRes.data,
// 			'placeLabs': placeLabRes.data }));
// 	}
// });

window.addEventListener("offline", () => {
	ElMessage.error("网络连接已断开，请检查网络连接");
});
</script>

<style lang="scss">
.loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.8);
    display: none;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    flex-direction: column;
}

.circle-container {
    position: relative;
    width: 100px;
    height: 100px;
}

.circle {
    position: absolute;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 2px solid #2196f3;
    animation: rotate-circle 3s infinite linear;
}

.dot {
    position: absolute;
    width: 15px;
    height: 15px;
    background-color: #82b1ff;
    border-radius: 50%;
    top: -7.5px;
    left: 42.5px;
    animation: flash-dot 1s infinite ease-in-out alternate;
}

.dot:nth-child(2) { animation-delay: 0.3s; }
.dot:nth-child(3) { animation-delay: 0.6s; }
.dot:nth-child(4) { animation-delay: 0.9s; }

.loading-text {
    margin-top: 30px;
    font-size: 24px;
    text-transform: uppercase;
    letter-spacing: 3px;
    color: #82b1ff;
}

@keyframes rotate-circle {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

@keyframes flash-dot {
    0% { opacity: 0.1; }
    100% { opacity: 1; }
}





$table-color: #337ecc;


.fade-enter-active,
.fade-leave-active {
	transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
}


.aside {
	transition: width 0.3s ease;
}

.main {
	/* 添加过渡动画 */
	transition: margin-left 0.3s ease;
	/* 初始位置 */
}

html,
body,
#app {
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%;
	font-family: "微软雅黑";
}

a {
	color: white;
	text-decoration: none;
}

.el-descriptions__body .el-descriptions__table.is-bordered .el-descriptions__cell {
	border: none;
	padding: 8px 11px;
}

.el-table .unitAudit {
	/* --el-table-tr-bg-color: black; */
	/* 蓝色 */
	background: $table-color;
	/* background: linear-gradient(to right, #90EE90, #006400); */
	color: white;
	font-weight: bold;
	z-index: 1;
}

.el-table .approvalAudit {
	/* 蓝色 */
	background: $table-color;
	/* background: linear-gradient(to right, #90EE90, #006400); */
	color: white;
	font-weight: bold;
}

.el-table .approved {
	background: $table-color;
	/* background: linear-gradient(to right, #90EE90, #006400); */
	color: white;
	font-weight: bold;
}

/* 取消表格默认的hover背景色 */
.myHover .el-table__body tr:hover>td {
	background-color: #79bbff !important;
}

.myHover .el-table__body tr.current-row>td.el-table__cell {
	background-color: #79bbff !important;
}

td:hover {
	background-color: #c6e2ff !important;
}

.expanded-row {
	border: 1px solid #dcdfe6;
	border-radius: 10px;
	padding: 10px;
}

/* 展开行的背景色 */
.expanded-row-unitAudit {
	background-color: $table-color;
	/* background: linear-gradient(to right, #00008B, #ADD8E6); */
	/* padding: 16px; */
	color: white;
}

.expanded-row-approvalAudit {
	/* background: linear-gradient(to right, #FF4500, #FFA500); */
	/* background-color: rgb(255, 165, 0); */
	background-color: $table-color;
	color: white;
}

.expanded-row-approved {
	/* background: linear-gradient(to right, #006400, #90EE90);  */
	/* background-color: rgb(71, 168, 71); */
	background-color: $table-color;
	color: white;
}

.el-timeline .el-timeline-item__center:first-child .el-timeline-item__tail {
	height: calc(100% + 10px) !important;
}

.el-timeline .el-timeline-item__center:nth-child(2) .el-timeline-item__tail {
	top: 40% !important;
	height: calc(90%) !important;
}

.el-timeline .el-timeline-item__center:nth-child(3) .el-timeline-item__tail {
	top: 40% !important;
	height: calc(0) !important;
}

.line_tail1 .el-timeline-item__tail {
	// height: 80% !important;
	border-left-color: rgb(103, 194, 58) !important;
}

.line_tail2 .el-timeline-item__tail {
	/* transform: translateY(50%); */
	/* height: 80% !important; */
	border-left-color: rgb(103, 194, 58) !important;
}

.line_tail3 .el-timeline-item__tail {
	/* transform: translateY(50%); */
	height: 0% !important;
	border-left-color: rgb(103, 194, 58) !important;
}

.line_tail4 .el-timeline-item__tail {
	// height: 80% !important;
	// z-index: 4;
	border-left-color: red !important;
}

.line1 {
	width: 0.5vw;
	height: 2vh;
	margin-left: 30%;
	margin-top: -1vh;
	background-color: rgb(86, 108, 185);
	border-radius: 3px;
	/* background: linear-gradient(to right, #ADD8E6, #00008B); */
}

.line2 {
	width: 0.5vw;
	height: 2vh;
	margin-left: 60%;
	margin-top: -2vh;
	background-color: rgb(86, 108, 185);
	border-radius: 3px;
}
</style>