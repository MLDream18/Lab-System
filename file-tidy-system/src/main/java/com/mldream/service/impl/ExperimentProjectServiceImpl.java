package com.mldream.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mldream.mapper.ExperimentProjectMapper;
import com.mldream.pojo.db.ExperimentProject;
import com.mldream.pojo.dto.ExperimentProjectDTO;
import com.mldream.pojo.dto.ExperimentProjectItemDTO;
import com.mldream.pojo.vo.ExperimentProjectVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.service.ExperimentProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ExperimentProjectServiceImpl implements ExperimentProjectService {

    @Autowired
    private ExperimentProjectMapper experimentProjectMapper;

    @Override
    public void submitProjectData(ExperimentProjectDTO projectData, Integer teacherId) {
        List<ExperimentProject> projectList = new ArrayList<>();

        /* 将List<Integer> classIds 转换为String classIds */

        List<Integer> classIds = projectData.getClassId();
        String classIdsStr = classIds.toString();
        classIdsStr = classIdsStr.replace("[", "").replace("]", "").replace(" ", "");
//        System.out.println(classIdsStr);

        for (ExperimentProjectItemDTO item : projectData.getDomains()) {
            ExperimentProject project = ExperimentProject.builder()
                    .labId(projectData.getLabId())
                    .courseNameId(projectData.getCourseNameId())
                    .experimentCategory(projectData.getExperimentCategory())
                    .experimentPeople(projectData.getExperimentPeople())
                    .experimentDemand(projectData.getExperimentDemand())
                    .classIds(classIdsStr)
                    .semesterId(projectData.getSemesterId())
                    .experimentNo(item.getExperimentNo())
                    .experimentHours(item.getExperimentHours())
                    .experimentContent(item.getExperimentContent())
                    .experimentType(item.getExperimentType())
                    .teacherId(teacherId)
                    .build();
            projectList.add(project);
        }
        experimentProjectMapper.insertBatch(projectList);

    }

    @Override
    public PageBean selectProjectData(Integer pageSize, Integer currentPage, Map<String, Object> params) {
//        PageHelper.startPage(currentPage, pageSize);
        PageHelper.startPage(currentPage, pageSize);

        List<ExperimentProjectVO> projectList = experimentProjectMapper.selectProjectData(params);
        Page<ExperimentProjectVO> page = (Page<ExperimentProjectVO>) projectList;
        return new PageBean(page.getTotal(), page.getResult());

    }

    @Override
    public List<ExperimentProjectVO> selectProjectAll(Map<String, Object> params) {
        return experimentProjectMapper.selectProjectData(params);
    }
}
