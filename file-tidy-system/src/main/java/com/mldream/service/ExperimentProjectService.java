package com.mldream.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mldream.pojo.dto.ExperimentProjectDTO;
import com.mldream.pojo.vo.ExperimentProjectVO;
import com.mldream.pojo.vo.PageBean;

import java.util.List;
import java.util.Map;

public interface ExperimentProjectService {


    void submitProjectData(ExperimentProjectDTO projectData, Integer teacherId);

    PageBean selectProjectData(Integer pageSize, Integer currentPage, Map<String, Object> params);

    List<ExperimentProjectVO> selectProjectAll(Map<String, Object> params);
}
