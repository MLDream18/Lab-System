package com.mldream.service;

import com.mldream.pojo.db.CourseName;

import java.util.List;
import java.util.Set;

public interface CourseNameService {

    List<CourseName> getAllCourseNames();

    void addCourseNames(List<CourseName> courseNameList);

    Integer getCourseNameIdByName(String name);

    Set<CourseName> getCourseNamesBySemesterId(Integer semesterId);
}
