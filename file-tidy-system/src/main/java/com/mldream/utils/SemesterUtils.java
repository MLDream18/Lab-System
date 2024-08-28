package com.mldream.utils;

import com.mldream.pojo.db.Semester;
import com.mldream.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SemesterUtils {

    @Autowired
    private SemesterService semesterService;

    public Semester getSemesterId(String semester) {
        Integer startYear = Integer.parseInt(semester.split("-")[0]);
        Integer EndYear = Integer.parseInt(semester.split("-")[1]);
        Integer stage = Integer.parseInt(semester.split("-")[2]);
        return semesterService.getSemester(new Semester(null, null, null, stage, startYear, EndYear));
    }
}
