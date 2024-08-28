package com.mldream.mapper;

import com.mldream.pojo.db.CourseSerial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseSerialMapper {

    void insertCourseSerial(List<CourseSerial> courseSerialList);

}
