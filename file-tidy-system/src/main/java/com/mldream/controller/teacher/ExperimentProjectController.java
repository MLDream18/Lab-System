package com.mldream.controller.teacher;


import com.mldream.pojo.db.CourseName;
import com.mldream.pojo.dto.ExperimentProjectDTO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ClassService;
import com.mldream.service.CourseNameService;
import com.mldream.service.ExperimentProjectService;
import com.mldream.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController("teacherExperimentProjectController")
@RequestMapping("/teacher")
public class ExperimentProjectController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ExperimentProjectService experimentProjectService;

    @Autowired
    private CourseNameService courseNameService;

    @Autowired
    private ClassService classService;

    @GetMapping("/experiment-project/temporary-get-project-data")
    public Result temporaryGetProjectData(@RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJwt(token);
        Integer teacherId = (Integer) claims.get("id");
//        System.out.println(projectData.toString());
        Map<String, Object> projectData = (Map<String, Object>) redisTemplate.opsForValue().get("temporarySaveProjectData" + teacherId);
        if(projectData == null) {
            return null;
        }
        return Result.success(projectData);
    }

    @PostMapping("/experiment-project/temporary-save-project-data")
    public void temporarySaveProjectData(@RequestBody Map<String, Object> projectData, @RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJwt(token);
        Integer teacherId = (Integer) claims.get("id");
//        System.out.println(projectData.toString());
        redisTemplate.opsForValue().set("temporarySaveProjectData" + teacherId, projectData);
        redisTemplate.expire("temporarySaveProjectData", 60 * 60 * 24 * 30, java.util.concurrent.TimeUnit.SECONDS); // 30 days
    }

    @PostMapping("/experiment-project/submit-project-data")
    public void submitProjectData(@RequestBody ExperimentProjectDTO projectData, @RequestHeader("token") String token) {
        Claims claims = JwtUtils.parseJwt(token);
        Integer teacherId = (Integer) claims.get("id");
//        System.out.println(projectData.toString());
        experimentProjectService.submitProjectData(projectData, teacherId);

        redisTemplate.delete("temporarySaveProjectData" + teacherId);
    }

    @GetMapping("/course/get-course-names-by-semester-id/{semesterId}")
    public Result getCourseNamesBySemesterId(@PathVariable Integer semesterId) {
        Set<CourseName> courseNames = courseNameService.getCourseNamesBySemesterId(semesterId);
        return Result.success(courseNames);
    }
}
