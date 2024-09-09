package com.mldream.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mldream.mapper.ApplyFormMapper;
import com.mldream.mapper.LaboratoryMapper;
import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.dto.ApplyFormItemDTO;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.service.LaboratoryService;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplyFormServiceImpl implements ApplyFormService {

    @Autowired
    private ApplyFormMapper applyFormMapper;

    @Autowired
    @Lazy
    private LaboratoryService laboratoryService;

    @Override
    public List<ApplyFormInfoVO> getApplyFormsDetail(List<ApplyForm> applyFormIds) {
        return applyFormMapper.selectApplyFormsDetail(applyFormIds);
    }

    @Override
    public Result submitApplyForm(ApplyFormDTO applyFormDTO) {

        List<LaboratorySource> laboratorySourceList = new ArrayList<>();
        StringBuilder usedWeekFinal = new StringBuilder(); // 用于存储usedWeek的字符串，总的周次
        StringBuilder usedWeekdayAndSectionFinal = new StringBuilder(); // 用于存储usedWeekdayAndSection的字符串，总的星期+节次
        Integer sumCourseHour = 0; // 总课时
        /* 解析usedWeek usedSection */
        for (ApplyFormItemDTO applyFormItem : applyFormDTO.getArrWeekAndSection()){
            String usedWeek = applyFormItem.getWeek(); // 用户输入的每个周次
            usedWeekFinal.append(usedWeek).append(";"); // 加到总周次中，以 ; 分隔
            String usedWeekdayAndSection = applyFormItem.getWeekdayAndSection(); // 用户输入的每个星期+节次
            usedWeekdayAndSectionFinal.append(usedWeekdayAndSection).append(";"); // 加到总星期+节次中，以 ; 分隔

            List<String> usedWeekList = new ArrayList<>(); // 存储用户输入的每个周次中的每一项，以 , 分隔
            List<String> usedSectionList = new ArrayList<>(); // 存储用户输入的每个星期+节次中的每一项，以 , 分隔

//            List<Integer> usedWeekIntList = new ArrayList<>(); // 用户的周次分解
            List<Pair<Integer, String>> usedWeekdayIntListSectionListStr = new ArrayList<>(); // 用户的星期+节次分解

            int index = usedWeek.indexOf(","); // 用于判断是否有多个周次

            if(index != -1) { // 有多个周次
                usedWeekList = List.of(usedWeek.split(",")); // 按 , 分割多个周次
            }
            else {
                usedWeekList.add(usedWeek); // 只有一个周次
            }

            index = usedWeekdayAndSection.indexOf(","); // 用于判断是否有多个星期+节次

            if(index != -1) { // 有多个星期+节次
                usedSectionList = List.of(usedWeekdayAndSection.split(",")); // 按 , 分割多个星期+节次
            }
            else {
                usedSectionList.add(usedWeekdayAndSection); // 只有一个星期+节次
            }

            for(String section : usedSectionList) {
                Integer weekday = Integer.parseInt(section.substring(0, 1));
                String tSection = section.substring(1);
                usedWeekdayIntListSectionListStr.add(new Pair<>(weekday, tSection));

            }

            for(String week : usedWeekList) {
                index = week.indexOf("-");
                if(index != -1) {
                    String startWeek = week.substring(0, index);
                    String endWeek = week.substring(index+1);
                    int start = Integer.parseInt(startWeek);
                    int end = Integer.parseInt(endWeek);
                    for (Pair<Integer, String> weekdayIntSectionPair : usedWeekdayIntListSectionListStr) {
                        List<LaboratorySource> sourceList = laboratoryService.searchLab(new ApplySearchConditionsDTO(
                                applyFormDTO.getUsedLabId(),
                                start,
                                end,
                                weekdayIntSectionPair.getFirst(),
                                weekdayIntSectionPair.getFirst(),
                                weekdayIntSectionPair.getSecond(),
                                weekdayIntSectionPair.getSecond(),
                                null,
                                applyFormDTO.getSemesterId()
                        ));
                        if(!sourceList.isEmpty()) {
                            return Result.error("时间段冲突");
                        }
                    }

                    for(int i = start; i <= end; i++) {
//                        usedWeekIntList.add(i);
                        for (Pair<Integer, String> weekdayIntSectionPair : usedWeekdayIntListSectionListStr) {
                            laboratorySourceList.add(new LaboratorySource(
                                    null,
                                    applyFormDTO.getUsedLabId(),
                                    i,
                                    weekdayIntSectionPair.getFirst(),
                                    weekdayIntSectionPair.getSecond(),
                                    applyFormDTO.getApplyReason(),
                                    null,
                                    null,
                                    applyFormDTO.getSemesterId()
                            ));
                            sumCourseHour += 2;
                        }
                    }
                }
                else {
                    for (Pair<Integer, String> weekdayIntSectionPair : usedWeekdayIntListSectionListStr) {
                        List<LaboratorySource> sourceList = laboratoryService.searchLab(new ApplySearchConditionsDTO(
                                applyFormDTO.getUsedLabId(),
                                Integer.parseInt(week),
                                Integer.parseInt(week),
                                weekdayIntSectionPair.getFirst(),
                                weekdayIntSectionPair.getFirst(),
                                weekdayIntSectionPair.getSecond(),
                                weekdayIntSectionPair.getSecond(),
                                null,
                                applyFormDTO.getSemesterId()
                        ));
                        if(!sourceList.isEmpty()) {
                            return Result.error("时间段冲突");
                        }
                    }
                    Integer weekInt = Integer.parseInt(week);
//                    usedWeekIntList.add(weekInt);
                    for (Pair<Integer, String> weekdayIntSectionPair : usedWeekdayIntListSectionListStr) {
                        laboratorySourceList.add(new LaboratorySource(
                                null,
                                applyFormDTO.getUsedLabId(),
                                weekInt,
                                weekdayIntSectionPair.getFirst(),
                                weekdayIntSectionPair.getSecond(),
                                applyFormDTO.getApplyReason(),
                                null,
                                null,
                                applyFormDTO.getSemesterId()
                        ));
                        sumCourseHour += 2;
                    }
                }
            }

        }
        ApplyForm applyForm = new ApplyForm(
            null,
            applyFormDTO.getUsedLabId(),
            applyFormDTO.getUsedStartDate(),
            usedWeekFinal.toString(),
            usedWeekdayAndSectionFinal.toString(),
            sumCourseHour,
            applyFormDTO.getApplyReason(),
            applyFormDTO.getExperimentContent(),
            applyFormDTO.getCourseNameId(),
            applyFormDTO.getClassId(),
            applyFormDTO.getExperimentPeople(),
            applyFormDTO.getApplicantId(),
            applyFormDTO.getApplicantTelephone(),
            applyFormDTO.getApplicantCollege(),
            applyFormDTO.getSubmitDate(),
            applyFormDTO.getState(),
            applyFormDTO.getUnitOpinion(),
            applyFormDTO.getUnitOpinionDate(),
            applyFormDTO.getUnitOpinionName(),
            null,
            null,
            null,
            applyFormDTO.getSemesterId(),
            applyFormDTO.getSubmitTeacherId()
        );
        applyFormMapper.insertApplyForm(applyForm);

        Integer applyFormId = applyForm.getId();


        for(LaboratorySource laboratorySource : laboratorySourceList) {
            laboratorySource.setApplyFormId(applyFormId);
        }

        laboratoryService.addLaboratorySources(laboratorySourceList);

        return Result.success("提交成功！");
    }

    @Override
    public List<ApplyFormInfoVO> getApplyFormsBySubmitTeacherId(Integer submitTeacherId) {
        return applyFormMapper.getApplyFormsBySubmitTeacherId(submitTeacherId);
    }

    @Override
    public PageBean getApplyFormInfoSp(Integer pageSize, Integer currentPage) {
        PageHelper.startPage(currentPage, pageSize);
        List<ApplyFormInfoVO> applyFormInfoVOList = applyFormMapper.selectApplyFormInfoSp();
        Page<ApplyFormInfoVO> page = (Page<ApplyFormInfoVO>) applyFormInfoVOList;
        return new PageBean(page.getTotal(), page.getResult());
    }

    @Override
    public void submitApproval(ApplyFormDTO applyFormDTO) {
        applyFormMapper.updateApplyFormState(applyFormDTO);
    }

    @Override
    public PageBean submitApprovalSelectForm(Integer pageSize, Integer currentPage, Map<String, Object> searchParams) {
        PageHelper.startPage(currentPage, pageSize);
        List<ApplyFormInfoVO> applyFormInfoVOList = applyFormMapper.selectApplyFormInfoByCondition(searchParams);
        Page<ApplyFormInfoVO> page = (Page<ApplyFormInfoVO>) applyFormInfoVOList;
        return new PageBean(page.getTotal(), page.getResult());
    }

    @Override
    public List<ApplyFormInfoVO> getLabRegisterData(Integer semesterId) {
        return applyFormMapper.selectLabRegisterData(semesterId);
    }

    @Override
    public PageBean getApplyFormsAppliedBySubmitTeacherId(Integer pageSize, Integer currentPage, Integer teacherId) {
        PageHelper.startPage(currentPage, pageSize);
        List<ApplyFormInfoVO> applyFormInfoVOList = applyFormMapper.selectApplyFormsAppliedBySubmitTeacherId(teacherId);
        Page<ApplyFormInfoVO> page = (Page<ApplyFormInfoVO>) applyFormInfoVOList;
        return new PageBean(page.getTotal(), page.getResult());
    }
}
