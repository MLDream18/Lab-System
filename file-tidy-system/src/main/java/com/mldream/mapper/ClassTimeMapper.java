package com.mldream.mapper;

import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.vo.ClassTimeInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ClassTimeMapper {

    void insertClassTimes(List<ClassTime> classTimeList);

    List<ClassTimeInfoVO> getClassTimeInfo(List<ClassTime> classTimeList);
}
