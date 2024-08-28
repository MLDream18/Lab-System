package com.mldream.service.impl;

import com.mldream.mapper.ClassTimeMapper;
import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.vo.ClassTimeInfoVO;
import com.mldream.service.ClassTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTimeServiceImpl implements ClassTimeService {

    @Autowired
    private ClassTimeMapper classTimeMapper;

    @Override
    public void addClassTimes(List<ClassTime> classTimeList) {
        classTimeMapper.insertClassTimes(classTimeList);
    }

    @Override
    public List<ClassTimeInfoVO> getClassTimeInfo(List<ClassTime> classTimeList) {
        return classTimeMapper.getClassTimeInfo(classTimeList);
    }

}
