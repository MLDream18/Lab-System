package com.mldream.service;

import com.mldream.pojo.db.Semester;

import java.util.List;

public interface SemesterService {

    Semester getSemester(Semester semester);

    List<Semester> getAllSemesters();

    void addSemester(Semester semester);

    Integer getLastSemesterId();

}
