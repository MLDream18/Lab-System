package com.mldream.controller.admin;

import com.mldream.pojo.db.Class;
import com.mldream.pojo.db.Semester;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ClassService;
import com.mldream.service.SemesterService;
import com.mldream.utils.SemesterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class ClassController {

    @Autowired
    private ClassService classService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SemesterUtils semesterUtils;

    @GetMapping("/admin/class-time/class")
    public Result getClassList() {
        List<Class> classList = classService.getAllClasses();
        return Result.success(classList);
    }

}
