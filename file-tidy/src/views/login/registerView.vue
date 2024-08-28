<template>
	<div class="container">
		<div class="bubbles">
			<span v-for="(randomValue, index) in randomValues" :key="index"
				:style="{ '--i': (randomValue + 1) / 7, '--color': colors[index % colors.length], '--colorA': colorsA[index % colorsA.length] }"></span>
		</div>
		<div class="form-container">
			<div class="login-form">
				<div style="width: 60%; margin: 0 auto;">
					<el-form ref="ruleFormRef" style="width: 100%" :model="form" status-icon label-width="auto">
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
								<el-image style="margin-left: 2.5%;" :src="captchaImg" fit="scale-down"
									@click="refreshCaptcha" />
							</div>
						</el-form-item>
						<el-form-item>
							<div style="width: 100%; display: flex; justify-content: center; align-items: center;">
								<el-button type="primary" @click="register(ruleFormRef)">
									注册
								</el-button>
								<el-button type="primary" @click="router.push('/login')">
									返回登录
								</el-button>
							</div>
							<div style="position: relative; top: 150%; width: 100%; display: inline-flex; justify-content: center; font-size: 15px; color: white;">
								© 22计科-WF版权所有
							</div>
						</el-form-item>
					</el-form>
				</div>
			</div>
		</div>
		<div>
            <el-dialog title="完善信息" v-model="dialogVisible">
                <div>
                    您的姓名是：
                    <el-input v-model="form.name" type="text" style="margin-left: 10px; width: 200px;" />
                </div>
                <div style="margin-top: 1%;">
                    <el-button type="primary" @click="submitInfo">
                        完成
                    </el-button>
                    <el-button type="primary" @click="dialogVisible = false">
                        取消
                    </el-button>
                </div>                
            </el-dialog>
        </div>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import axios from 'axios';
import router from '../../router';

const randomValues = ref(Array.from({ length: 80 }, () => Math.floor(Math.random() * 100)));
const colors = [
	'#4fc3dc', '#ff2d75', '#ffcc00', '#00cc99', '#cc00cc',
	'#3399ff', '#ff6666', '#99cc00', '#ff9900', '#cc3399',
	'#0099cc', '#ff3366', '#66cc66', '#ffcc99', '#9933cc',
	'#0066cc', '#ff6699', '#33cc33', '#ff99cc', '#cc6699'
];
const colorsA = [
	'#4fc3dc44', '#ff2d7544', '#ffcc0044', '#00cc9944', '#cc00cc44',
	'#3399ff44', '#ff666644', '#99cc0044', '#ff990044', '#cc339944',
	'#0099cc44', '#ff336644', '#66cc6644', '#ffcc9944', '#9933cc44',
];

const dialogVisible = ref(false)
const captchaImg = ref('/api/admin/login/captcha');
const ruleFormRef = ref<FormInstance>()
const form = reactive({
	username: '',
	password: '',
	name: '',
	captcha: ''
})

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

const register = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.validate(async (valid) => {
		if (valid) {
			dialogVisible.value = true;
		}
	})
}

const submitInfo = async () => {
	if (!form.name) {
		ElMessage.error('请填写姓名');
		return;
	}

	const res = await axios.post('/api/teacher/register', {
		username: form.username,
		password: form.password,
		name: form.name,
		captcha: form.captcha
	});
	if (res.data.code === 1) {
		ElMessage.success(res.data.data);
	} else {
		ElMessage.error(res.data.msg);
	}

	dialogVisible.value = false;
}

refreshCaptcha();
</script>

<style scoped>
.container {
	width: 100%;
	height: 100%;
	background-color: #0c192c;
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
	width: 15px;
	height: 15px;
	background-color: var(--color);
	border-radius: 50%;
	margin: 0 4px;
	box-shadow: 0 0 0 10px var(--colorA), 0 0 50px var(--color), 0 0 100px var(--color);
	animation: animate calc(120s / var(--i)) ease-in-out infinite;
}



@keyframes animate {
	0% {
		transform: translateY(100vh) scale(0);
	}

	100% {
		transform: translateY(-10vh) scale(1);
	}
}

.form-container {
	width: 30%;
	height: 30%;
	border-radius: 10px;
	padding: 5%;
	background-color: #233a5c;
	box-shadow: 0 0 0 10px #233a5c, 0 0 50px #233a5c, 0 0 100px #233a5c;
	z-index: 2;
	display: flex;
	justify-content: center;
	align-items: center;
	opacity: 0.5;
}

.login-form {
	width: 100%;
}

.login-form .el-segmented {
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
