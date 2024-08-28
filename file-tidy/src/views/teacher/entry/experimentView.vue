<template>
	<el-main class="main"
		:style="{ 'margin-left': !collapse.isCollapse ? '10%' : '3.5%', 'height': '100%', 'background-color': 'rgb(243, 244, 247)' }">
		<div
			style="width: auto; height: auto; background-color: white; padding: 2.5%; margin-bottom: 3%; border-radius: 10px;">
			<el-form ref="formRef" style="margin: auto auto auto auto; max-width: 50%; 
				border: 1px solid black; border-radius: 10px; padding: 2.5%;" status-icon :model="dynamicValidateForm"
				label-width="auto">
				<div
					style="width: 95%; padding: 2.5%; border: 1px solid black; border-radius: 10px; margin-bottom: 2.5%;">
					<div style="width: 100%; text-align: center; margin-top: -2.5%;">
						<h2>实验课程基本信息</h2>
					</div>
					<div style="width: 100%;">
						<el-form-item label="学期：">
							<el-select-v2 v-model="dynamicValidateForm.semesterId" clearable :options="semesterList"
								value="value" label="label" @change="changeSemester" />
						</el-form-item>
						<el-form-item label="课程名称：" :prop="`courseNameId`" :rules="[
							{ validator: validateCourseName }
						]">
							<el-select-v2 v-model="dynamicValidateForm.courseNameId" placeholder="课程名称" filterable
								clearable :options="courseNames" value="value" label="label" />
						</el-form-item>
						<el-form-item label="实验类别：" :prop="`experimentCategory`" :rules="[
							{ validator: validateExperimentCategory }
						]">
							<el-select-v2 v-model="dynamicValidateForm.experimentCategory" placeholder="实验类别" clearable
								:options="experimentCategoryList" value="value" label="label" />
						</el-form-item>
						<el-form-item label="班级：" :prop="`classId`" :rules="[
							{ validator: validateClass }
						]">
							<el-select-v2 v-model="dynamicValidateForm.classId" placeholder="班级" filterable clearable
								multiple :options="classList" value="value" label="label" />
						</el-form-item>
						<el-form-item label="实验人数：" :prop="`experimentPeople`">
							<el-input-number v-model="dynamicValidateForm.experimentPeople" min="1" max="1000" />
						</el-form-item>
						<el-form-item label="实验要求：" :prop="`experimentDemand`" :rules="[
							{ validator: validateExperimentDemand }
						]">
							<el-select-v2 v-model="dynamicValidateForm.experimentDemand" placeholder="实验要求" clearable
								:options="experimentDemandList" value="value" label="label" />
						</el-form-item>
						<el-form-item :prop="`labId`" label="使用教室：" :rules="[
							{ validator: validateClassroom }
						]">
							<el-select-v2 filterable clearable placeholder="使用教室" :options="labList"
								v-model="dynamicValidateForm.labId" value="value" label="label" />
						</el-form-item>
					</div>
				</div>
				<div
					style="width: 95%; padding: 2.5%; border: 1px solid black; border-radius: 10px; margin-bottom: 2.5%;">
					<div style="width: 100%; text-align: center; margin-top: -2.5%;">
						<h2>实验内容填报</h2>
					</div>
					<div v-for="(domain, index) in dynamicValidateForm.domains" :key="index">
						<div style="width: 100%;">
							<el-form-item label="实验序号：" :prop="`domains.${index}.experimentNo`">
								<el-input v-model="domain.experimentNo" disabled />
							</el-form-item>
							<el-form-item label="实验内容：" :prop="`domains.${index}.experimentContent`" :rules="[
								{ validator: validateExperimentContent, trigger: 'blur' }
							]">
								<el-input v-model="domain.experimentContent" placeholder="实验内容" autosize>
									<template #prepend>实验{{ index + 1 }}：</template>
								</el-input>
							</el-form-item>
							<el-form-item label="实验类型：" :prop="`domains.${index}.experimentType`" :rules="[
								{ validator: validateExperimentType }
							]">
								<el-select-v2 v-model="domain.experimentType" clearable :options="experimentTypeList"
									value="value" placeholder="实验类型" />
							</el-form-item>
							<div style="width: 100%; display: inline-flex; justify-content: space-between;">
								<div>
									<el-form-item label="实验学时数：" :prop="`domains.${index}.experimentHours`">
										<el-input-number v-model="domain.experimentHours" :min="1" :max="100" />
									</el-form-item>
								</div>
								<div>
									<el-button type="danger" style="" @click.prevent="removeDomain(domain)">
										删除
									</el-button>
								</div>
							</div>
						</div>
						<div style="width: 100%; background-color: black;">
							<el-divider></el-divider>
						</div>
					</div>
					<div style="width: 100%; display: inline-flex; justify-content: space-between;">
						<div>
							<el-form-item>
								<el-button @click="addDomain">添加实验</el-button>
							</el-form-item>
						</div>
						<div>
							<el-form-item>
								<el-popconfirm confirm-button-text="Yes" cancel-button-text="No"
									title="请确保填写完所有实验项目后提交！" @confirm="submitForm(formRef)">
									<template #reference>
										<el-button type="primary">提交</el-button>
									</template>
								</el-popconfirm>
								<el-button type="info" @click="temporarySaveForm(formRef)">暂存</el-button>
								<!-- <el-button @click="resetForm(formRef)">重置</el-button> -->
							</el-form-item>
						</div>
					</div>
				</div>
			</el-form>
		</div>
	</el-main>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import axios from 'axios';
import { useCollapseStore } from '../../../stores/store';

const basicData = JSON.parse(`${localStorage.getItem('basic-data')}`);

const collapse = useCollapseStore();

const formRef = ref<FormInstance>()
const courseNames = ref<{ value: number, label: string }[]>([]); // 课程名称
const classList = ref<{ value: number, label: string }[]>([]); // 班级列表
const teacherList = ref<{ value: number, label: string }[]>([]); // 教师列表
const labList = ref<{ value: number, label: string }[]>([]); // 实验室列表
const experimentCategoryList = ref<{ value: number, label: string }[]>([]); // 实验类别列表
const experimentDemandList = ref<{ value: number, label: string }[]>([]); // 实验需求列表
const semesterList = ref<{ value: number, label: string }[]>([]); // 学期列表
const experimentNoList = ref<{ value: string, label: string }[]>([]); // 实验号
const experimentTypeList = ref<{ value: number, label: string }[]>([]); // 实验类型列表

const dynamicValidateForm = reactive({
	domains: [
		{
			key: 1,
			experimentNo: '001',
			experimentContent: '',
			experimentType: '',
			experimentHours: 1,
		},
	],
	labId: '',
	courseNameId: '',
	experimentContent: '',
	classId: '',
	experimentPeople: 1,
	experimentCategory: '',
	experimentDemand: '',
	semesterId: 0,
})

const changeSemester = async () => {
	const semesterId = dynamicValidateForm.semesterId;
	let courseNamesDataRes = await axios.get(`/api/teacher/course/get-course-names-by-semester-id/${semesterId}`);
	courseNames.value = courseNamesDataRes.data.data.map((item: any) => {
		return {
			value: item.id,
			label: item.courseName
		}
	});
	console.log(courseNamesDataRes.data, semesterId)
}

/* 使用教室不为空 */
const validateClassroom = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请填写使用教室'));
	}
	return callback();
};

/* 课程名称不为空 */
const validateCourseName = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请选择课程名称'));
	}
	return callback();
};

/* 实验类别不为空 */
const validateExperimentCategory = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请选择实验类别'));
	}
	return callback();
};

/* 班级不为空 */
const validateClass = (_rule: any, value: any, callback: any) => {
	if (!value || value.length === 0) {
		return callback(new Error('请选择班级'));
	}
	return callback();
};

/* 实验需求不为空 */
const validateExperimentDemand = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请选择实验需求'));
	}
	return callback();
};

/* 实验内容不为空 */
const validateExperimentContent = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请填写实验内容'));
	}
	return callback();
};

/* 实验类型不为空 */
const validateExperimentType = (_rule: any, value: any, callback: any) => {
	if (!value || value === '') {
		return callback(new Error('请选择实验类型'));
	}
	return callback();
};


const removeDomain = (item: any) => {
	if (dynamicValidateForm.domains.length === 1) {
		ElMessage.error('实验项目不能为空');
		return;
	}
	const index = dynamicValidateForm.domains.indexOf(item)
	if (index !== -1) {
		dynamicValidateForm.domains.splice(index, 1);
		dynamicValidateForm.domains = dynamicValidateForm.domains.map((item: any, index: number) => {
			return {
				key: item.key,
				experimentNo: experimentNoList.value[index % experimentNoList.value.length].value,
				experimentContent: item.experimentContent,
				experimentType: item.experimentType,
				experimentHours: item.experimentHours,
			}
		});
	}
}

const addDomain = () => {
	dynamicValidateForm.domains.push({
		key: Date.now(),
		experimentNo: experimentNoList.value[dynamicValidateForm.domains.length % experimentNoList.value.length].value,
		experimentContent: '',
		experimentType: '',
		experimentHours: 1,
	})
}

const submitForm = async (formEl: FormInstance | undefined) => {
	if (!formEl) return
	formEl.validate(async (valid) => {
		if (valid) {
			dynamicValidateForm.domains.map((item: any, index: number) => {
				item.experimentContent = `实验${index + 1}：${item.experimentContent.trim()}`;
			});
			// console.log(dynamicValidateForm);
			await axios.post('/api/teacher/experiment-project/submit-project-data', dynamicValidateForm);
			resetForm(formEl);
			ElMessage.success('提交成功')
		} else {
			// console.log('error submit!')
			ElMessage.error('请检查输入项')
		}
	})
}

const temporarySaveForm = (formEl: FormInstance | undefined) => {
	if (!formEl) return
	formEl.validate(async (valid) => {
		if (valid) {
			await axios.post('/api/teacher/experiment-project/temporary-save-project-data', dynamicValidateForm);
			ElMessage.success('暂存成功')
		} else {
			// console.log('error submit!')
			ElMessage.error('请检查输入项')
		}
	})
}

const resetForm = (formEl: FormInstance | undefined) => {
	if (!formEl) return
	formEl.resetFields()
	dynamicValidateForm.domains = [
		{
			key: 1,
			experimentNo: '001',
			experimentContent: '',
			experimentType: '',
			experimentHours: 1,
		},
	];
}


onMounted(async () => {
	const semesterRes = basicData.semesters;
	const semesterData = semesterRes;
	semesterList.value = semesterData.data.map((item: any) => {
		return {
			value: item.id,
			label: item.startYear + '-' + item.endYear + '-' + item.stage,
		}
	});
	dynamicValidateForm.semesterId = semesterList.value[semesterList.value.length - 1].value;

	let courseNameRes = basicData.courseNames;
	courseNames.value = courseNameRes.data.map((item: any) => ({
		value: item.id,
		label: item.courseName
	}));

	let classRes = basicData.classes;
	classList.value = classRes.data.map((item: any) => ({
		value: item.id,
		label: item.name
	}));
	// console.log(classList.value)
	let teacherRes = basicData.teachers;
	teacherList.value = teacherRes.data.map((item: any) => ({
		value: item.id,
		label: item.name
	}));
	// console.log(teacherList.value)

	let labRes = basicData.labs;
	labList.value = labRes.data.map((item: any) => ({
		value: item.id,
		label: item.name,
	}));

	let temporaryExperimentProject = await axios.get('/api/teacher/experiment-project/temporary-get-project-data');
	if (temporaryExperimentProject.data.data) {
		dynamicValidateForm.domains = temporaryExperimentProject.data.data.domains;
		dynamicValidateForm.labId = temporaryExperimentProject.data.data.labId;
		dynamicValidateForm.courseNameId = temporaryExperimentProject.data.data.courseNameId;
		dynamicValidateForm.experimentContent = temporaryExperimentProject.data.data.experimentContent;
		dynamicValidateForm.classId = temporaryExperimentProject.data.data.classId;
		dynamicValidateForm.experimentPeople = temporaryExperimentProject.data.data.experimentPeople;
		dynamicValidateForm.experimentCategory = temporaryExperimentProject.data.data.experimentCategory;
		dynamicValidateForm.experimentDemand = temporaryExperimentProject.data.data.experimentDemand;
		dynamicValidateForm.semesterId = temporaryExperimentProject.data.data.semesterId;
	}


	experimentCategoryList.value = [
		{
			value: 1,
			label: '基础'
		},
		{
			value: 2,
			label: '专业基础'
		},
		{
			value: 3,
			label: '专业'
		},
		{
			value: 4,
			label: '其他'
		}
	];

	experimentDemandList.value = [
		{
			value: 1,
			label: '必修'
		},
		{
			value: 2,
			label: '选修'
		},
		{
			value: 3,
			label: '其他'
		}
	];

	experimentNoList.value = [
		{
			value: '001',
			label: '001'
		},
		{
			value: '002',
			label: '002'
		},
		{
			value: '003',
			label: '003'
		},
		{
			value: '004',
			label: '004'
		},
		{
			value: '005',
			label: '005'
		},
		{
			value: '006',
			label: '006'
		},
		{
			value: '007',
			label: '007'
		},
		{
			value: '008',
			label: '008'
		},
		{
			value: '009',
			label: '009'
		},
		{
			value: '010',
			label: '010'
		},
		{
			value: '011',
			label: '011'
		},
		{
			value: '012',
			label: '012'
		},
		{
			value: '013',
			label: '013'
		},
		{
			value: '014',
			label: '014'
		},
		{
			value: '015',
			label: '015'
		},
		{
			value: '016',
			label: '016'
		},
		{
			value: '017',
			label: '017'
		},
		{
			value: '018',
			label: '018'
		},
		{
			value: '019',
			label: '019'
		},
		{
			value: '020',
			label: '020'
		},
	];

	experimentTypeList.value = [
		{
			value: 1,
			label: '演示性'
		},
		{
			value: 2,
			label: '验证性'
		},
		{
			value: 3,
			label: '综合性'
		},
		{
			value: 4,
			label: '设计研究'
		},
		{
			value: 5,
			label: '其他'
		},
	];

});

</script>

<style scoped>

</style>