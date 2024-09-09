package com.mldream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.db.ClassTime;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassScheduleMapper extends BaseMapper<ClassTime> {

    List<Map<String, Object>> selectAllClassSchedules(String semester);

    @Delete("delete from class_time where semester_id = #{semesterId}")
    void deleteClassSchedules(Integer semesterId);
}
