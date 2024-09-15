<template>
	<div style="height: 100%; position: fixed; width: 100%; background: radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%);"
		v-if="$route.meta.keepAlive">
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
	<transition v-if="!$route.meta.keepAlive">
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
import router from './router';
import { ElMessage, ElNotification } from "element-plus";
const collapse = useCollapseStore();
const roleStore = useUserStore();
roleStore.role = `${localStorage.getItem("role")}`;
const title = document.querySelector('title');

router.beforeEach((to, _from, next) => {
	if (to.path === '/404' || to.path === '/login' || to.path === '/' || (to.name?.toString().includes('admin') && `${localStorage.getItem('role')}` === 'admin') || (to.name?.toString().includes('teacher') && `${localStorage.getItem('role')}` === 'teacher')) {
		next();
		title.innerText = `${to.meta.title} | 实验室信息统计及开放预约系统`;
	} else {
		next({ path: '/404' });
	}
});

window.addEventListener("offline", () => {
	ElMessage.error("网络连接已断开，请检查网络连接");
});
</script>

<style lang="scss">
$table-color: #337ecc;


// .fade-enter-active,
// .fade-leave-active {
// 	transition: opacity 1s ease;
// }

// .fade-enter-from,
// .fade-leave-to {
// 	opacity: 0;
// }


.aside {
	transition: width 0.3s ease;
	margin-top: 1%;
	color: rgb(56, 73, 148);
}

.main {
	/* 添加过渡动画 */
	transition: margin-left 0.3s ease;
	background: radial-gradient(ellipse at bottom, rgb(229, 239, 253) 0%, rgb(204, 224, 247) 100%);
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



.el-segmented {
	--el-segmented-item-selected-color: #fff !important;
	--el-segmented-item-selected-bg-color: rgb(125, 149, 255) !important;
	--el-segmented-item-hover-color: #fff !important;
	--el-segmented-item-hover-bg-color: rgb(56, 73, 148) !important;
	--el-border-radius-base: 10px !important;
	color: rgb(56, 73, 148) !important;
	font-weight: bold;
	font-size: large !important;
}
</style>