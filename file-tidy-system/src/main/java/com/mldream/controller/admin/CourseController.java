package com.mldream.controller.admin;

import com.mldream.pojo.db.CourseName;
import com.mldream.pojo.db.Semester;
import com.mldream.pojo.vo.Result;
import com.mldream.service.CourseNameService;
import com.mldream.service.SemesterService;
import com.mldream.utils.SemesterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/admin")
public class CourseController {

    @Autowired
    private CourseNameService courseNameService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SemesterUtils semesterUtils;

//    @GetMapping("/class-time/course-names/{semester}")
//    public Result getCourseNamesBySemesterId(@PathVariable String semester) {
//        Semester tSemester = semesterUtils.getSemesterId(semester);
//        Set<CourseName> courseNames = courseNameService.getCourseNamesBySemesterId(tSemester.getId());
//        return Result.success(courseNames);
//    }

}
