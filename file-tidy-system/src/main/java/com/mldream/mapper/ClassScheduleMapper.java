package com.mldream.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassScheduleMapper {

    List<Map<String, Object>> selectAllClassSchedules(String semester);

    @Delete("delete from class_time where semester_id = #{semesterId}")
    void deleteClassSchedules(Integer semesterId);
}
