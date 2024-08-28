package com.mldream.controller.teacher;

import com.mldream.pojo.db.Laboratory;
import com.mldream.pojo.db.Semester;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ClassScheduleService;
import com.mldream.service.LaboratoryService;
import com.mldream.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("teacherLaboratoryController")
@RequestMapping("/teacher")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    @Autowired
    private ClassScheduleService classScheduleService;

    @Autowired
    private SemesterService semesterService;

    @GetMapping("/class-time/classrooms")
    public Result getAllClassrooms() {
        List<Laboratory> classrooms = laboratoryService.getAllLaboratories();
        return Result.success(classrooms);
    }

    @GetMapping("/class-time/classes/{semester}")
    public Result getAllClasses(@PathVariable String semester) {
        List<Map<String, Object>> classSchedules = classScheduleService.getAllClassSchedules(semester);
        Map<String, Object> semesterMap = new HashMap<>();
        semesterMap.put("semester", semester);
        classSchedules.add(semesterMap);
        return Result.success(classSchedules);
    }

    @GetMapping("/class-time/semesters")
    public Result getAllSemesters() {
//        LocalDateTime now = LocalDateTime.now();
        List<Semester> semesterList = semesterService.getAllSemesters();
        return Result.success(semesterList);
    }
}
