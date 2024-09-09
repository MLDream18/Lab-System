package com.mldream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.db.Class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {

    @Select("select * from class order by name")
    List<Class> getAllClasses();

    void addClasses(List<Class> classList);

    @Select("select * from class where name = #{className}")
    Class getClassByName(String className);

    @Select("select id from class where name = #{name}")
    Integer getClassIdByName(String name);

}
