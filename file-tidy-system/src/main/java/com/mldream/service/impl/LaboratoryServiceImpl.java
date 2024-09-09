package com.mldream.service.impl;

import com.mldream.mapper.LaboratoryMapper;
import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.db.Laboratory;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.dto.SearchClassScheduleDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.service.ApplyFormService;
import com.mldream.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class LaboratoryServiceImpl implements LaboratoryService {

    @Autowired
    private LaboratoryMapper laboratoryMapper;

    @Autowired
    private ApplyFormService applyFormService;

    @Override
    public void addLaboratories(List<Laboratory> laboratoryList) {
        laboratoryMapper.addLaboratories(laboratoryList);
    }

    @Override
    public List<Laboratory> getAllLaboratories() {
        return laboratoryMapper.getAllLaboratories();
    }

    @Override
    public Laboratory getLaboratoryByName(String name) {
        return laboratoryMapper.getLaboratoryByName(name);
    }

    @Override
    public void addLaboratorySources(List<LaboratorySource> laboratorySourceList) {
        laboratoryMapper.addLaboratorySources(laboratorySourceList);
    }

    @Override
    public List<LaboratorySource> searchLab(ApplySearchConditionsDTO applySearchConditionsDTO) {
        List<LaboratorySource> sourceList = laboratoryMapper.selectLabSourcesByCondition(applySearchConditionsDTO);
        Set<Integer> removeIndexList = new HashSet<>();
        for (LaboratorySource source : sourceList) {
            if (source.getApplyFormId() != null) {
                List<ApplyForm> applyForms = new ArrayList<>();
                applyForms.add(ApplyForm.builder().id(source.getApplyFormId()).build());
                List<ApplyFormInfoVO> detail = applyFormService.getApplyFormsDetail(applyForms);
                if (detail.get(0).getState() < 0) {
                    removeIndexList.add(source.getApplyFormId());
                }
            }
        }

        List<LaboratorySource> newSourceList = new ArrayList<>();
        for (LaboratorySource laboratorySource : sourceList) {
            if (!removeIndexList.contains(laboratorySource.getApplyFormId())) {
                newSourceList.add(laboratorySource);
            }
        }

        return newSourceList;
    }

    @Override
    public List<LaboratorySource> searchLabClassSchedule(SearchClassScheduleDTO searchClassScheduleDTO) {
        return laboratoryMapper.selectLabSourcesByClassSchedule(searchClassScheduleDTO);
    }

    @Override
    public List<Laboratory> getClassroomsByPlace(String place) {
        return laboratoryMapper.selectClassroomsByPlace(place);
    }
}
