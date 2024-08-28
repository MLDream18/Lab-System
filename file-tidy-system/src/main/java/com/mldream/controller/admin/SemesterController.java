package com.mldream.controller.admin;

import com.mldream.pojo.vo.Result;
import com.mldream.pojo.db.Semester;
import com.mldream.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("adminSemesterController")
@RequestMapping("/admin")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @GetMapping("/class-time/semesters")
    public Result getAllSemesters() {
//        LocalDateTime now = LocalDateTime.now();
        List<Semester> semesterList = semesterService.getAllSemesters();
        return Result.success(semesterList);
    }
}
