package com.mldream.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.db.ExperimentProject;
import com.mldream.pojo.vo.ExperimentProjectVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExperimentProjectMapper extends BaseMapper<ExperimentProjectVO> {


    void insertBatch(List<ExperimentProject> projectList);

    List<ExperimentProjectVO> selectProjectData(Map<String, Object> params);

}
