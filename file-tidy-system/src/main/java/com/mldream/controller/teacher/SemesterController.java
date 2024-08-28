package com.mldream.controller.teacher;

import com.mldream.pojo.db.Semester;
import com.mldream.pojo.vo.Result;
import com.mldream.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("teacherSemesterController")
@RequestMapping("/teacher")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @GetMapping("/select-semesters")
    public Result getAllSemesters() {
//        LocalDateTime now = LocalDateTime.now();
        List<Semester> semesterList = semesterService.getAllSemesters();
        return Result.success(semesterList);
    }
}
