package com.mldream.mapper;

import com.mldream.pojo.db.Semester;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SemesterMapper {

    @Select("select * from semester order by start_year, end_year, stage")
    List<Semester> selectAllSemesters();

    void addSemester(Semester semester);

    @Select("select * from semester where start_year = #{startYear} and end_year = #{endYear} and stage = #{stage}")
    Semester selectSemester(Semester semester);

    @Select("select max(id) from semester")
    Integer selectLastSemesterId();

    @Select("select ceil(datediff(date(now()), start_date) / 7.0) from semester where id = #{semesterId}")
    Integer selectWeeksLeft(Integer semesterId);
}
