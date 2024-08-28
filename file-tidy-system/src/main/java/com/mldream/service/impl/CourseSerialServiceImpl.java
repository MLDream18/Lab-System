package com.mldream.service.impl;

import com.mldream.mapper.CourseSerialMapper;
import com.mldream.pojo.db.CourseSerial;
import com.mldream.service.CourseSerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSerialServiceImpl implements CourseSerialService {

    @Autowired
    private CourseSerialMapper courseSerialMapper;

    @Override
    public void addCourseSerial(List<CourseSerial> courseSerialList) {
        courseSerialMapper.insertCourseSerial(courseSerialList);
    }

}
