package com.mldream.service;

import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.vo.ClassTimeInfoVO;

import java.util.List;

public interface ClassTimeService {

    void addClassTimes(List<ClassTime> classTimeList);

    List<ClassTimeInfoVO> getClassTimeInfo(List<ClassTime> classTimeList);
}
