<template>
	<div>
		<div id="picker" class="select-file" :style="{ pointerEvents: uploadStatus ===  'uploading' ? 'none' : 'auto' }">
			<el-icon><Upload /></el-icon>选择文件
		</div>
		<div id="dragContainer" class="drag-container">
				<el-icon size="100" color="#409EFF"><upload-filled /></el-icon>
				<div class="el-upload__text">
      				将文件拖到此处上传
    			</div>
		</div>
		<div>
			<el-table :data="internalFileListData" width="auto">
				<el-table-column prop="fileName" label="文件名称" align="center"></el-table-column>
				<el-table-column prop="fileSize" align="center" label="文件大小" width="150"></el-table-column>
				<el-table-column label="进度" align="center" width="300">
					<template #default="scope">
						<div class="progress-container">
							<el-progress :text-inside="true" :stroke-width="15"
								:percentage="scope.row.percentage"></el-progress>
						</div>
					</template>
				</el-table-column>
				<el-table-column prop="speed" label="上传速度" align="center" width="150">
					<template #default="scope">
						<div>{{ scope.row.speed }}</div>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" align="center">
					<template #default="scope">
						<el-button type="text" class="red" :disabled="uploadStatus === 'uploading'" @click="removeRow(scope.$index, scope.row)">
							移除
						</el-button>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<div class="confirm-cancel">
			<el-button v-if="false" @click="cancelUpload">取消</el-button>
			<el-button v-if="false" type="primary" @click="uploadToServer">确定</el-button>
		</div>
	</div>
</template>


<script>
import webUploader from 'webuploader' // 引入WebUploader库

export default {
	name: 'WebFileUpload',
	props: {
		headers: {
			type: String,
			default: ''
		},
		fileNumLimit: {
			type: Number,
			default: 1
		},
		fileSize: {
			type: Number,
			default: 5 *  1024 * 1024 * 1024 // 1gb
		},
		chunkSize: {
			type: Number,
			default: 5 * 1024 * 1024 // 5mb
		},
		uploadSuffixUrl: {
			type: String,
			default: '/api'
		},
		multiple: {
			type: Boolean,
			default: false // 是否支持多文件上传
		},
		options: {
			type: Object,
			default: () => ({
				fileType: 'doc,docx,pdf,xls,xlsx,jpg,jpeg,png,mp4,avi,exe,msi,md', // 允许上传的文件类型
				fileUploadUrl: '/teacher/uploadFile', // 分片上传接口
				headers: {token: localStorage.getItem('token')} // 上传请求头
			})
		},
		fileListData: {
			type: Array,
			default: () => []
		}
	},
	data() {
		return {
			uploader: null,
			percentage: 0, // 上传进度
			internalFileListData: [], // 使用内部数据属性来保存文件列表数据
			uploadStatus: '', // 上传状态
			fList: [],
			fileTimestamps: {} // 用于存储每个文件的时间戳
		}
	},
	watch: {
		fileListData(newValue) {
			// 当parentData变化时，执行相应逻辑
			this.internalFileListData = newValue
			console.log(this.internalFileListData)
		}
	},
	mounted() {
		this.internalFileListData = [...this.fileListData]
		this.initUploader()
		this.initEvents()
	},
	methods: {
		/**
		 * 初始化上传组件
		 */
		initUploader() {
			this.uploader = webUploader.create({
				dnd: '#dragContainer', // 指定Drag And Drop容器，如果不指定，则不启动拖拽上传。
				auto: false, // 选完文件后，是否自动上传。
				resize: false, // 不压缩image
				swf: '../../../assets/Uploader.swf', // swf文件路径
				server: this.uploadSuffixUrl + this.options.fileUploadUrl, // 默认文件接收服务端。
				pick: {
					id: '#picker', // 上传按钮
					multiple: this.multiple, // 是否开启文件多选,
				},
				accept: [
					{
						title: 'file',
						extensions: this.options.fileType,
						mimeTypes: this.buildFileType(this.options.fileType)
					}
				],

				// 单位字节，如果图片大小小于此值，不会采用压缩。512k  512*1024，如果设置为0，原图尺寸大于设置的尺寸就会压缩；如果大于0，只有在原图尺寸大于设置的尺寸，并且图片大小大于此值，才会压缩
				compressSize: 0,
				fileNumLimit: this.fileNumLimit, //验证文件总数量, 超出则不允许加入队列,默认值：undefined,如果不配置，则不限制数量
				fileSizeLimit: 5 * 1024 * 1024 * 1024, // 1kb=1024*1024,验证文件总大小是否超出限制, 超出则不允许加入队列。
				fileSingleSizeLimit: this.fileSize, //单个文件大小是否超出限制, 超出则不允许加入队列。
				chunkSize: this.chunkSize, // 单个分片大小为5MB,1024 * 1024 * 5表示5MB

				chunked: true, //是否开启分片上传
				threads: 8, // 并发上传数
				chunkRetry: 8, // 网络错误重试次数

				prepareNextFile: false, //在上传当前文件时是否准备好下一个文件

				// 上传时添加的请求头，例如需要传送token等
				headers: {
				  token: localStorage.getItem('token')
				}
			})
		},

		initEvents() {
			// 文件添加到队列
			this.uploader.on('fileQueued', file => {
				if (!this.multiple) {
					// 清空现有文件列表，实现只上传单个文件
					this.internalFileListData = []
				}

				// 生成唯一的时间戳并存储在 fileTimestamps 对象中
				const timestamp = Date.now().toString()
				this.fileTimestamps[file.id] = timestamp

				const fileSize = this.formatFileSize(file.size)
				this.internalFileListData.push({
					fileId: file.id,
					fileName: file.name,
					fileSize: fileSize,
					percentage: 0, // 初始化进度为0
					speed: '0KB/s', // 初始化速度
					state: '就绪'
				})
				// this.uploadToServer() // 选择文件后直接开始上传
			})

			/**
			 * 监听上传成功事件
			 * @param file: 文件对象
			 * @param : 服务器返回的数据
			 */
			this.uploader.on('uploadSuccess', (file, response) => {
				this.fList = []
				// 如果code等于1,表示上传成功
				if (response.code === 1) {
					response.data.fileName = response.data.originalName
					response.data.percentage = this.internalFileListData[0].percentage
					response.data.fileSize = this.internalFileListData[0].fileSize
					response.data.speed = this.internalFileListData[0].speed
					this.fList.push(response.data)
					this.$emit('getFileList', this.fList)
					this.$message.success('上传完成')
				} else {
					this.$message.error('上传失败')
				}
			})

			/**
			 * 监听上传错误事件
			 * @param file: 文件对象
			 * @param : 服务器返回的数据
			 */
			this.uploader.on('uploadError', () => {
				this.$message.error('上传出错')
			})

			// 监听上传进度
			this.uploader.on('uploadProgress', (file, percentage) => {
				// 找到对应文件并更新进度
				let targetFile = this.internalFileListData.find(item => item.fileId === file.id)
				if (targetFile) {
					const currentTime = new Date().getTime()
					const elapsedTime = (currentTime - (targetFile.startTime || currentTime)) / 1000 // 秒
					const uploadedSize = percentage * file.size
					const speed = this.formatFileSize(uploadedSize / elapsedTime) + '/s'

					targetFile.percentage = parseFloat((percentage * 100).toFixed(2))
					targetFile.speed = speed
					targetFile.startTime = targetFile.startTime || currentTime
				}
			})

			// 上传之前发送的数据
			this.uploader.on('uploadBeforeSend', (block, data, headers) => {
				const fileTimestamp = this.fileTimestamps[block.file.id]
				data.fileMd5 = block.file.fileMd5
				data.contentType = block.file.type
				data.chunks = block.file.chunks
				data.zoneTotalMd5 = block.file.fileMd5
				data.zoneMd5 = block.zoneMd5
				data.zoneTotalCount = block.chunks
				data.zoneNowIndex = block.chunk
				data.zoneTotalSize = block.total
				data.zoneStartSize = block.start
				data.zoneEndSize = block.end
				data.fileUUID = fileTimestamp
				headers.token = this.options.headers.token
			})

			// 所有文件上传完成
			this.uploader.on('uploadFinished', () => {
				this.uploadBtnDisabled = false
				this.uploadStatus = 'uploaded'
				this.internalFileListData = []
				const files = this.uploader.getFiles()
				for (let i = 0; i < files.length; i++) {
					this.uploader.removeFile(files[i], true)
				}
				this.$message.success('文件上传完毕')
			})

			// 错误信息监听
			this.uploader.on('error', handler => {
				let errorMessage = ''
				if (handler === 'F_EXCEED_SIZE') {
					errorMessage =
						'上传的单个文件太大! 最大支持' +
						this.formatFileSize(this.fileSize) +
						'! 操作无法进行, 如有需求请联系管理员'
				} else if (handler === 'Q_TYPE_DENIED') {
					errorMessage = '不允许上传此类文件! 操作无法进行, 如有需求请联系管理员'
				} else if (handler === 'Q_EXCEED_SIZE_LIMIT') {
					errorMessage =
						'上传的文件大小超过限制! 最大支持' +
						this.formatFileSize(this.fileSize) +
						'! 操作无法进行, 如有需求请联系管理员'
				} else if (handler === 'Q_EXCEED_NUM_LIMIT') {
					errorMessage =
						'上传的文件数量超过限制! 最多只能上传' +
						this.fileNumLimit +
						'个文件! 操作无法进行, 如有需求请联系管理员'
				} 
				if (errorMessage) {
					this.$message.error({
						showClose: true,
						message: errorMessage
					})
				}
			})
		},
		uploadToServer() {
			// console.log(this.internalFileListData)
			if (this.internalFileListData.length <= 0) {
				this.$message.error({
					showClose: true,
					message: '没有上传的文件'
				})
				return
			}
			this.uploadBtnDisabled = true
			this.uploadStatus = 'uploading'
			this.uploader.upload()
		},

		/**
		 * 格式化文件大小
		 * @param {Number} size 文件大小
		 * @return {String} 格式化后的文件大小
		 */
		formatFileSize(size) {
			const units = ['KB', 'MB', 'GB']
			let unitIndex = -1
			do {
				size /= 1024
				unitIndex++
			} while (size >= 1024 && unitIndex < units.length - 1)
			return size.toFixed(2) + units[unitIndex]
		},

		/**
		 * 构建文件类型字符串，以便在文件选择对话框中使用
		 * @param {string} fileType - 用逗号分隔的文件扩展名字符串，例如 "jpg,png,gif"
		 * @return {string} - 以逗号分隔的文件类型字符串，每个扩展名前加一个点，例如 ".jpg,.png,.gif"
		 */
		buildFileType(fileType) {
			const fileTypes = fileType.split(',')
			return fileTypes.map(type => `.${type}`).join(',')
		},

		/**
		 * 操作中的移除
		 * @param {Number} index - 文件列表索引
		 * @param {Object} row - 文件对象
		 */
		removeRow(index, row) {
			this.internalFileListData.splice(index, 1)

			const files = this.uploader.getFiles()
			for (let i = 0; i < files.length; i++) {
				if (files[i].id === row.fileId) {
					this.uploader.removeFile(files[i], true)
					break
				}
			}
			this.$emit('removeRow', index)
		},

		/**
		 * 取消上传
		 * @description 取消上传，清空文件列表，停止上传，重置上传按钮状态
		 */
		cancelUpload() {
			
		},
		
		/**
		 * 获取返回数据
		 */
		getBackData() {
			return this.fList
		}
	}
}
</script>

<style lang="scss">
.drag-container {
	width: 100%;
	height: auto;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	border: 1px dashed #ddd;
}

.select-file {
	width: 10%;
	text-align: center;
	border: 1px solid #ddd;
	border-radius: 50%;
	position: fixed;
	bottom: 5px;
}

.webuploader-pick {
	padding: 2.5%;
	background-color: #409eff;
	color: white;
	font-size: small;
}

.confirm-cancel {
	margin-top: 1%;
	display: flex;
	justify-content: flex-end;
}

.webuploader-container {
	position: relative;
}

.webuploader-element-invisible {
	position: absolute !important;
	clip: rect(1px, 1px, 1px, 1px);
}

.webuploader-pick-hover {
	background: #3a8ee6;
}

.progress-container {
	width: 200px;
	/* 设置进度条容器的宽度 */
	margin: 0 auto;
}
</style>