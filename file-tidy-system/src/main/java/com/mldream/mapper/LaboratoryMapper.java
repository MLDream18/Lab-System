package com.mldream.mapper;

import com.mldream.pojo.db.Laboratory;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.dto.SearchClassScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LaboratoryMapper {

    void addLaboratories(List<Laboratory> laboratoryList);

    @Select("select * from laboratory")
    List<Laboratory> getAllLaboratories();

    @Select("select * from laboratory where name = #{name}")
    Laboratory getLaboratoryByName(String name);

    void addLaboratorySources(List<LaboratorySource> laboratorySourceList);

    List<LaboratorySource> selectLabSourcesByCondition(ApplySearchConditionsDTO applySearchConditionsDTO);

    List<LaboratorySource> selectLabSourcesByClassSchedule(SearchClassScheduleDTO searchClassScheduleDTO);

    @Select("select * from laboratory where name like concat('%', #{place}, '%')")
    List<Laboratory> selectClassroomsByPlace(String place);
}
