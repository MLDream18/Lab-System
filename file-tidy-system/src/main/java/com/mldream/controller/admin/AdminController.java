package com.mldream.controller.admin;

import com.mldream.mapper.ApplyFormMapper;
import com.mldream.pojo.db.Admin;
import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.db.Semester;
import com.mldream.pojo.db.Teacher;
import com.mldream.pojo.dto.AdminLoginDTO;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.AdminService;
import com.mldream.service.ApplyFormService;
import com.mldream.service.SemesterService;
import com.mldream.service.TeacherService;
import com.mldream.utils.JwtUtils;
import com.mldream.utils.SemesterUtils;
import com.mldream.utils.VerifyCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminService adminService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SemesterUtils semesterUtils;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ApplyFormService applyFormService;

    @Autowired
    private ApplyFormMapper applyFormMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }
    /*
    * 根据周次和星期获取日期
    * select date_add(start_date, interval ((week - 1) * 7 + (weekday - weekday(start_date) - 1)) day) AS new_date
    * from semester
    * where id = 1;
    *
    * */

//    @GetMapping("/class-time/teachers/{semester}")
//    public Result getTeachersBySemesterId(@PathVariable String semester) {
//        Semester tSemester = semesterUtils.getSemesterId(semester);
//        Set<Teacher> teachers = teacherService.getTeachersBySemesterId(tSemester.getId());
//        return Result.success(teachers);
//    }

    @PostMapping("/login")
    public Result login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("login: {}", adminLoginDTO);
        String captcha = (String) redisTemplate.opsForValue().get("captcha");
        if(captcha == null || !captcha.equalsIgnoreCase(adminLoginDTO.getCaptcha())) {
            return Result.error(captcha != null? "验证码错误" : "验证码过期");
        }
        Admin admin = adminService.getAdminByUsername(adminLoginDTO.getUsername());
        // TODO: 校验用户名和密码
//        Admin loginAdmin = adminService.login(admin);
        String password = adminLoginDTO.getPassword();
        String encodedPassword = admin.getPassword();
        if(!passwordEncoder.matches(password, encodedPassword)) {
            return Result.error("用户名或密码错误");
        }

        applyFormMapper.checkApplyForm();

        Integer role = admin.getRole();
        // TODO: 生成token并返回
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        String token = JwtUtils.generateJwt(claims);
        return Result.success(token);
    }

    @RequestMapping("/login/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: 生成验证码并返回
//        System.out.println("111");
        VerifyCode verifyCode = new VerifyCode();
        BufferedImage image = verifyCode.createImage();
        redisTemplate.opsForValue().set("captcha", verifyCode.getText());
        redisTemplate.expire("captcha", 60, TimeUnit.SECONDS);
//        System.out.println(verifyCode.getText());
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

    @GetMapping("/approval/get-apply-info-sp/{pageSize}/{currentPage}")
    public Result getApplyInfoDsp(@RequestHeader("token") String token, @PathVariable Integer pageSize, @PathVariable Integer currentPage) {
//        System.out.println(pageSize + " " + currentPage);
        Claims claims = JwtUtils.parseJwt(token);
        Integer role = (Integer) claims.get("role");
//        Integer semesterId = (Integer) claims.get("semesterId");
        PageBean result = applyFormService.getApplyFormInfoSp(pageSize, currentPage);;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("role", role);
        return Result.success(resultMap);
    }

    @PostMapping("/approval/submit-approval-select-form/{pageSize}/{currentPage}")
    public Result submitApprovalSelectForm(@RequestBody Map<String, Object> params, @PathVariable Integer pageSize, @PathVariable Integer currentPage) {
        PageBean result = applyFormService.submitApprovalSelectForm(pageSize, currentPage, params);
        return Result.success(result);
    }

    @GetMapping("/approval/get-lab-register-data/{semsterId}")
    public Result getLabRegisterData(@PathVariable Integer semsterId) {
        List<ApplyFormInfoVO> applyFormInfoVOList = applyFormService.getLabRegisterData(semsterId);
        return Result.success(applyFormInfoVOList);
    }

    @PutMapping("/approval/submit-approval")
    public Result submitApproval(@RequestBody ApplyFormDTO applyFormDTO) {
        applyFormService.submitApproval(applyFormDTO);
        return Result.success();
    }
}
