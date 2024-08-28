package com.mldream.service;

import com.mldream.pojo.db.Laboratory;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.dto.SearchClassScheduleDTO;

import java.util.List;

public interface LaboratoryService {

    void addLaboratories(List<Laboratory> laboratoryList);

    List<Laboratory> getAllLaboratories();

    Laboratory getLaboratoryByName(String name);

    void addLaboratorySources(List<LaboratorySource> laboratorySourceList);

    List<LaboratorySource> searchLab(ApplySearchConditionsDTO applySearchConditionsDTO);

    List<LaboratorySource> searchLabClassSchedule(SearchClassScheduleDTO searchClassScheduleDTO);

    List<Laboratory> getClassroomsByPlace(String place);
}
