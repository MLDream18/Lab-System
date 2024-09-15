package com.mldream.controller.teacher;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.ApplyFormMapper;
import com.mldream.mapper.TeacherMapper;
import com.mldream.pojo.dto.TeacherLoginDTO;
import com.mldream.pojo.vo.Result;
import com.mldream.pojo.db.Teacher;
import com.mldream.service.SemesterService;
import com.mldream.service.TeacherService;
import com.mldream.utils.JwtUtils;
import com.mldream.utils.QCloudCOSUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private static final String UPLOAD_PATH = "upload-files/";

    @Autowired
    private QCloudCOSUtils qCloudCOSUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ApplyFormMapper applyFormMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @PostMapping("/login")
    public Result login(@RequestBody TeacherLoginDTO teacherLoginDTO) {
        String captcha = (String) redisTemplate.opsForValue().get("captcha");
        if(captcha == null || !captcha.equalsIgnoreCase(teacherLoginDTO.getCaptcha())) {
            return Result.error(captcha != null? "验证码错误" : "验证码过期");
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(teacherLoginDTO.getUsername());
        teacher.setPassword(teacherLoginDTO.getPassword());

        Teacher loginTeacher = teacherService.login(teacher);
        if (loginTeacher == null) {
            return Result.error("用户名或密码错误");
        }

        applyFormMapper.checkApplyForm();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", loginTeacher.getId());
        claims.put("name", loginTeacher.getName());
        claims.put("college", loginTeacher.getCollege());
        String token = JwtUtils.generateJwt(claims);
        return Result.success(token);
    }

//    @PostMapping("/register")
//    public Result register(@RequestBody TeacherLoginDTO teacherLoginDTO) {
//        String captcha = (String) redisTemplate.opsForValue().get("captcha");
//        if(captcha == null || !captcha.equalsIgnoreCase(teacherLoginDTO.getCaptcha())) {
//            return Result.error("验证码错误");
//        }
//        Teacher teacher = new Teacher();
//        teacher.setUsername(teacherLoginDTO.getUsername());
//        teacher.setPassword(teacherLoginDTO.getPassword());
//        teacher.setName(teacherLoginDTO.getName());
//
//        return teacherService.register(teacher);
//    }

    @GetMapping("/getName")
    public Result getName(@RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJwt(token);
        Integer id = (Integer) claims.get("id");
        Teacher teacher = teacherMapper.selectById(id);
        return Result.success(teacher.getName());
    }

    @PostMapping("/uploadFile")
    public Result uploadFile(MultipartFile file,
                             Integer zoneNowIndex,
                             Integer zoneTotalCount,
                             Long zoneTotalSize,
                             String fileUUID) throws Exception {
//        String url = qCloudCOSUtils.upload(file);
        return teacherService.uploadFile(file, zoneNowIndex, zoneTotalCount, zoneTotalSize, fileUUID);
    }


}
