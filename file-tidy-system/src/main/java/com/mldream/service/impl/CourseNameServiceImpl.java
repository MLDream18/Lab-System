package com.mldream.service.impl;

import com.mldream.mapper.CourseNameMapper;
import com.mldream.pojo.db.CourseName;
import com.mldream.service.CourseNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CourseNameServiceImpl implements CourseNameService {

    @Autowired
    private CourseNameMapper courseNameMapper;

    @Override
    public List<CourseName> getAllCourseNames() {
        return courseNameMapper.selectAllCourseNames();
    }

    @Override
    public void addCourseNames(List<CourseName> courseNameList) {
        courseNameMapper.insertCourseNames(courseNameList);
    }

    @Override
    public Integer getCourseNameIdByName(String name) {
        return courseNameMapper.selectCourseNameIdByName(name);
    }

    @Override
    public Set<CourseName> getCourseNamesBySemesterId(Integer semesterId) {
        return courseNameMapper.selectCourseNamesBySemesterId(semesterId);
    }

}
