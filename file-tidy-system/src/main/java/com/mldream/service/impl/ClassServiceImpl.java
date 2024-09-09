package com.mldream.service.impl;

import com.mldream.mapper.ClassMapper;
import com.mldream.pojo.db.Class;
import com.mldream.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public List<Class> getAllClasses() {
        return classMapper.getAllClasses();
    }

    @Override
    public void addClasses(List<Class> classList) {
        classMapper.addClasses(classList);
    }

    @Override
    public Integer getClassIdByName(String name) {
        return classMapper.getClassIdByName(name);
    }

}
