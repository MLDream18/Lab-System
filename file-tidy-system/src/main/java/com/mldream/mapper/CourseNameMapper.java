package com.mldream.mapper;


import com.mldream.pojo.db.CourseName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface CourseNameMapper {

    @Select("select * from course_name")
    public List<CourseName> selectAllCourseNames();

    void insertCourseNames(List<CourseName> courseNameList);

    @Select("select id from course_name where course_name = #{name}")
    Integer selectCourseNameIdByName(String name);

    @Select("select cn.* from course_name cn, course_serial cs where cn.id = cs.course_name_id and cs.semester_id = #{semesterId}")
    Set<CourseName> selectCourseNamesBySemesterId(Integer semesterId);
}
