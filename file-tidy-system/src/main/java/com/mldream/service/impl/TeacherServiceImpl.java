package com.mldream.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mldream.mapper.TeacherMapper;
import com.mldream.pojo.db.Teacher;
import com.mldream.pojo.db.TeacherSuggestion;
import com.mldream.pojo.vo.Archive;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.pojo.vo.TeacherSuggestionVO;
import com.mldream.service.TeacherService;
import com.mldream.ws.config.WsSessionManager;
import com.mldream.ws.handler.admin.AdminSuggestionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    private static final String UPLOAD_PATH = "upload-files/";

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherMapper.getAllTeachers();
    }

    @Override
    public void addTeachers(List<Teacher> teacherList) {
        teacherMapper.addTeachers(teacherList);
    }

    @Override
    public Integer getTeacherIdByName(String name) {
        return teacherMapper.getTeacherIdByName(name);
    }

    @Override
    public Teacher login(Teacher teacher) {
        return teacherMapper.selectByUsernameAndPassword(teacher);
    }

    @Override
    public Result register(Teacher teacher) {
        if (teacherMapper.getTeacherByUsername(teacher.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        teacherMapper.insert(teacher);
        return Result.success("注册成功");
    }

//    @Override
//    public PageBean getRepliedTeacherSuggestions(Integer pageSize, Integer currentPage, Integer teacherId) {
//        PageHelper.startPage(currentPage, pageSize);
//        List<TeacherSuggestionVO> teacherSuggestionVOList = teacherMapper.selectRepliedTeacherSuggestions(teacherId);
//        Page<TeacherSuggestionVO> page = (Page<TeacherSuggestionVO>) teacherSuggestionVOList;
//        return new PageBean(page.getTotal(), page.getResult());
//    }

    @Override
    public void addSuggestion(TeacherSuggestion teacherSuggestion) {
        teacherMapper.insertSuggestion(teacherSuggestion);
    }

    @Override
    public PageBean getNotReplyTeacherSuggestions(Integer pageSize, Integer currentPage) {
        PageHelper.startPage(currentPage, pageSize);
        List<TeacherSuggestionVO> teacherSuggestionVOList = teacherMapper.selectNotReplyTeacherSuggestions();
        Page<TeacherSuggestionVO> page = (Page<TeacherSuggestionVO>) teacherSuggestionVOList;
        return new PageBean(page.getTotal(), page.getResult());
    }

//    @Override
//    public void replyTeacherSuggestion(Integer suggestionId, String replyContent) {
//        teacherMapper.updateReplyContent(suggestionId, replyContent);
//    }

    @Override
    public Result uploadFile(MultipartFile file, Integer currentChunk,
                             Integer zoneTotalCount,
                             Long zoneTotalSize,
                             String fileUUID) throws Exception {
        // 获取文件的原始文件名 以及 扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileNameUtils.getExtension(originalFilename);

        // 构建上传路径
        String uploadPath = Paths.get(UPLOAD_PATH, extension).toString();
        log.info("上传路径：{}", uploadPath);
        FileUtils.forceMkdir(new File(uploadPath)); // 创建上传目录 如不存在

        // 写入临时文件
        String tempFileName = (currentChunk != null) ? currentChunk + "_" + fileUUID + "_" + originalFilename : fileUUID + "_" + originalFilename;
        File tempFile = new File(uploadPath, tempFileName);
        log.info("临时文件：{}", tempFile.getAbsolutePath());
        file.transferTo(tempFile.getAbsoluteFile());

        // 如果是最后一块 / 只有一块，则合并文件
        if (currentChunk == null || currentChunk + 1 == zoneTotalCount) {
            // 获取最终文件路径
            String finalFileName = fileUUID + "_" + originalFilename;
            File finalFile = new File(uploadPath, finalFileName);

            // 合并文件
            mergeChunkFiles(uploadPath, fileUUID, originalFilename, zoneTotalCount, finalFile);

            // 移动文件到指定目录 示例：upload-files/jpg/2021-01-01/uuid_filename.jpg
            Path filePath = Paths.get(extension, DateFormat.getDateInstance().format(new Date()), UUID.randomUUID().toString() + "_" + originalFilename);

            FileUtils.moveFile(finalFile, new File(Paths.get(UPLOAD_PATH, filePath.toString()).toString()));

            // 保存附件信息到数据库
            Archive archive = new Archive();
            archive.setSid(UUID.randomUUID().toString());
            archive.setFileName(filePath.getFileName().toString());
            archive.setOriginalName(originalFilename);
            archive.setPath(filePath.toString());
            archive.setSize(zoneTotalSize != null ? zoneTotalSize : (long) tempFile.length());
            archive.setFileType(extension);
            log.info("文件上传完毕：{}", filePath.toString());

            return Result.success(archive);
//            teacherMapper.insertSuggestion();
        }
        return Result.success("分片上传成功");
    }

    private void mergeChunkFiles(String uploadPath, String fileUUID, String originalFilename, Integer zoneTotalCount, File finalFile) throws Exception {
        long start = System.currentTimeMillis();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(finalFile))) {
            for (int i = 0; i < zoneTotalCount; ++i) {
                File chunkFile = new File(uploadPath, i + "_" + fileUUID + "_" + originalFilename);
                while(!chunkFile.exists()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("正在合并分片文件：{}", chunkFile.getName());
                // 读入分片数据并写入最终文件
                try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(chunkFile))) {
                    byte[] buffer = new byte[8192]; // 8KB 缓冲区
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                // 删除已合并的分片文件
                long deleteStart = System.currentTimeMillis();
                if(!chunkFile.delete()) {
                    log.warn("删除分片文件失败：{}", chunkFile.getName());
                } else {
                    log.info("删除分片耗时：{}ms", System.currentTimeMillis() - deleteStart);
                }

            }
        }
        long end = System.currentTimeMillis();
        log.info("合并完成，合并文件耗时：{}ms", end - start);
    }
}
