package com.mldream.service.impl;

import com.mldream.mapper.SemesterMapper;
import com.mldream.pojo.db.Semester;
import com.mldream.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterMapper semesterMapper;

    @Override
    public Semester getSemester(Semester semester) {
        return semesterMapper.selectSemester(semester);
    }

    @Override
    public List<Semester> getAllSemesters() {
        return semesterMapper.selectAllSemesters();
    }

    @Override
    public void addSemester(Semester semester) {
        semesterMapper.addSemester(semester);
    }

    @Override
    public Integer getLastSemesterId() {
        return semesterMapper.selectLastSemesterId();
    }

}
