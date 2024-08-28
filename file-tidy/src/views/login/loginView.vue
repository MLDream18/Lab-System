<template>
	<div class="layer1"></div>
	<div class="layer2"></div>
	<div class="layer3"></div>
	<div class="container">
		<!-- <div class="bubbles">
			<span v-for="(randomValue, index) in randomValues" :key="index"
				:style="{ '--i': (randomValue + 1) / 7, '--color': colors[index % colors.length], '--colorA': colorsA[index % colorsA.length] }"></span>
		</div> -->
		<div class="form-container">
			<div style="width: 100%;">
				<el-segmented style="width: 60%; margin: 0 auto; display: flex; justify-content: center; align-items: center; 
					background-color: #233a5c;" v-model="isAdmin" :options="options" block />
			</div>
			<div class="login-form">
				<el-form ref="ruleFormRef" :model="form" label-width="auto">
					<el-form-item label="账号：" prop="username"
						:rules="{ validator: usernameValidator, trigger: 'blur' }">
						<el-input v-model="form.username" type="text" autocomplete="on" />
					</el-form-item>
					<el-form-item label="密码：" prop="password"
						:rules="{ validator: passwordValidator, trigger: 'blur' }">
						<el-input v-model="form.password" type="password" autocomplete="on" />
					</el-form-item>
					<el-form-item label="验证码：" prop="captcha" :rules="{ validator: captchaValidator, trigger: 'blur' }">
						<div
							style="width: 100%; display: inline-flex; justify-content: space-between; align-items: center;">
							<el-input v-model="form.captcha" />
							<el-image
								style="margin-left: 2.5%; border-radius: var(--el-input-border-radius, var(--el-border-radius-base));"
								:src="captchaImg" fit="scale-down" @click="refreshCaptcha" />
						</div>
					</el-form-item>
					<el-form-item>
						<div style="width: 100%; display: inline-flex; justify-content: center;">
							<el-button type="primary" @click="submitForm(ruleFormRef)">
								登录
							</el-button>
							<el-button v-if="!isAdmin" type="primary" @click="register">
								注册
							</el-button>
						</div>
					</el-form-item>
				</el-form>
				<div style="width: 100%; text-align: center; font-size: 15px; color: white;">
					<p>本院教师登录账号为：工号，密码为：123456</p>
				</div>
			</div>
			<div
				style="position: absolute; bottom: -2.5%; width: 100%; text-align: center; font-size: 15px; color: white;">
				<p>© 22计科-WF版权所有</p>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import axios from 'axios';
import router from '../../router';
import { useUserStore } from '../../stores/store';

// const randomValues = ref(Array.from({ length: (window.outerWidth / 15) }, () => Math.floor(Math.random() * 100)));
// const colors = [
// 	'#4fc3dc', '#ff2d75', '#ffcc00', '#00cc99', '#cc00cc',
// 	'#3399ff', '#ff6666', '#99cc00', '#ff9900', '#cc3399',
// 	'#0099cc', '#ff3366', '#66cc66', '#ffcc99', '#9933cc',
// 	'#0066cc', '#ff6699', '#33cc33', '#ff99cc', '#cc6699'
// ];
// const colorsA = [
// 	'#4fc3dc44', '#ff2d7544', '#ffcc0044', '#00cc9944', '#cc00cc44',
// 	'#3399ff44', '#ff666644', '#99cc0044', '#ff990044', '#cc339944',
// 	'#0099cc44', '#ff336644', '#66cc6644', '#ffcc9944', '#9933cc44',
// ];

const captchaImg = ref('/api/admin/login/captcha');
const isAdmin = ref(true);
const ruleFormRef = ref<FormInstance>()
const form = reactive({
	username: '',
	password: '',
	captcha: ''
})
const options = [{ label: '管理员', value: true }, { label: '老师', value: false }]
const roleStore = useUserStore();

const refreshCaptcha = async () => {
	captchaImg.value = '/api/admin/login/captcha?' + new Date().getTime();
}

const usernameValidator = (_rule: any, value: string, callback: any) => {
	if (!value) {
		return callback(new Error('请输入账号'))
	}
	return callback();
}

const passwordValidator = (_rule: any, value: string, callback: any) => {
	if (!value) {
		return callback(new Error('请输入密码'))
	}
	return callback();
}

const captchaValidator = (_rule: any, value: string, callback: any) => {
	if (!value) {
		return callback(new Error('请输入验证码'))
	}
	return callback();
}

const submitForm = async (formEl: FormInstance | undefined) => {
	if (!formEl) return
	formEl.validate(async (valid) => {
		if (valid) {
			if (isAdmin.value) {
				let res = await axios.post('/api/admin/login', form);
				let data = res.data;
				if (data.code === 1) {
					refreshCaptcha();
					localStorage.setItem('token', data.data);
					localStorage.setItem('role', 'admin');
					roleStore.setUser('admin');
					const classroomRes = await axios.get('/api/admin/class-time/classrooms');
					const semesterRes = await axios.get('/api/admin/class-time/semesters');
					localStorage.setItem('basic-data', JSON.stringify({ 'classrooms': classroomRes.data, 'semesters': semesterRes.data }));
					ElMessage.success('登录成功！');
					router.push({
						path: '/pending-approval'
					});
				}
			} else {
				let res = await axios.post('/api/teacher/login', form);
				let data = res.data;
				if (data.code === 1) {
					refreshCaptcha();
					localStorage.setItem('token', data.data);
					localStorage.setItem('role', 'teacher');
					roleStore.setUser('teacher');
					const semesterRes = await axios.get('/api/teacher/select-semesters');
					const courseNameByIdRes = await axios.get(`/api/teacher/course/get-course-names-by-semester-id/${semesterRes.data.data.at(-1).id}`);
					const classRes = await axios.get(`/api/teacher/lab/apply/getClassList`);
					const teacherRes = await axios.get(`/api/teacher/lab/apply/getTeacherList`);
					const labRes = await axios.get(`/api/teacher/lab/apply/getLabList`);
					const placeLabRes = await axios.get(`/api/teacher/lab/apply/classrooms/逸夫楼`);
					localStorage.setItem('basic-data', JSON.stringify({
						'semesters': semesterRes.data,
						'courseNames': courseNameByIdRes.data,
						'classes': classRes.data,
						'teachers': teacherRes.data,
						'labs': labRes.data,
						'placeLabs': placeLabRes.data
					}));
					ElMessage.success('登录成功！');
					router.push({
						path: '/apply'
					});
				}
			}
			resetForm(formEl);
		}
	})
}

const register = () => {
	router.push({
		path: '/register'
	});
}

const resetForm = (formEl: FormInstance | undefined) => {
	if (!formEl) return
	formEl.resetFields()
}

refreshCaptcha();

</script>

<style scoped lang="scss">
@function getShadows($n) {
	$shadows: '#{random(100)}vw #{random(100)}vh #fff';

	@for $i from 2 through $n {
		$shadows: '#{$shadows}, #{random(100)}vw #{random(100)}vh #fff';
	}

	@return unquote($shadows);
}

$duration: 400s;
$count: 1000;
@for $i from 1 through 3 {
	$duration: floor($duration / 2);
	$count: floor($count / 2);
	.layer#{$i} {
		z-index: 2;
		$size: #{$i}px;
		position: fixed;
		width: $size;
		height: $size;
		border-radius: 50%;
		left: 0;
		top: 0;
		box-shadow: getShadows($count);
		animation: moveUp $duration linear infinite;

		&::after {
			content: '';
			position: fixed;
			left: 0;
			top: 100vh;
			width: $size;
			height: $size;
			border-radius: inherit;
			box-shadow: inherit;
		}
	}
}

$maxOpacity: 0.9;
$minOpacity: 0.3;
@keyframes moveUp {
	0% {
		opacity: $maxOpacity;
	}

	12.5% {
		opacity: $minOpacity;
	}

	25% {
		opacity: $maxOpacity;
	}

	37.5% {
		opacity: $minOpacity;
	}

	50% {
		opacity: $maxOpacity;
	}

	62.5% {
		opacity: $minOpacity;
	}

	75% {
		opacity: $maxOpacity;
	}

	87.5% {
		opacity: $minOpacity;
	}

	100% {
		transform: translateY(-100vh);
		opacity: $maxOpacity;
	}
}

.container {
	width: 100%;
	height: 100%;
	background: radial-gradient(ellipse at bottom, #1b2735 0%, #090a0f 100%);
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	position: relative;
}

.bubbles {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: flex;
	z-index: 1;
}

.bubbles span {
	width: 1px;
	height: 1px;
	background-color: var(--color);
	border-radius: 50%;
	margin: 0 4px;
	box-shadow: 0 0 0 10px var(--colorA), 0 0 50px var(--color), 0 0 100px var(--color);
	animation: animate calc(120s / var(--i)) ease-in-out infinite;
}



@keyframes animate {
	0% {
		transform: translateY(100vh) scale(0);
		opacity: 1;
	}

	12.5% {
		opacity: 0.1;
	}

	25% {
		opacity: 1;
	}

	37.5% {
		opacity: 0.1;
	}

	50% {
		opacity: 1;
	}

	62.5% {
		opacity: 0.1;
	}

	75% {
		opacity: 1;
	}

	87.5% {
		opacity: 0.1;
	}

	100% {
		transform: translateY(-10vh) scale(1);
		opacity: 1;
	}
}

.form-container {
	width: auto;
	height: auto;
	padding: 2.5%;
	border-radius: 10px;
	background-color: #233a5c;
	box-shadow: 0 0 0 10px #233a5c, 0 0 50px #233a5c, 0 0 100px #233a5c;
	z-index: 2;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	opacity: 0.5;
	position: relative;
}

.login-form {
	width: 100%;
}

.el-segmented {
	--el-segmented-item-selected-color: #0066ff;
	--el-segmented-item-selected-bg-color: #ffffff;
	--el-border-radius-base: 15px;
	color: white;
	font-weight: bold;
	font-size: larger;
}

.el-form-item {
	--el-text-color-regular: white !important;
	font-weight: bold !important;
	font-size: larger;
}

.el-input {
	--el-input-text-color: black !important;
	--el-text-color-regular: black !important;
}
</style>
