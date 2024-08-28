package com.mldream.pojo.dto;

import com.alibaba.fastjson2.annotation.JSONBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyFormDTO {
    private Integer applyFormId;
    private Integer usedLabId; // 实验室id
    private LocalDate usedStartDate; // 实验开始日期
//    private String usedWeek;
//    private String usedSection;
    List<ApplyFormItemDTO> arrWeekAndSection;
    private Integer applyReason; // 申请事由
    private String experimentContent; // 实验内容
    private Integer courseNameId; // 课程名称id
    private Integer classId; // 班级id
    private Integer experimentPeople; // 实验人数
    private Integer applicantId; // 申请人id
    private String applicantTelephone; // 申请人电话
    private String applicantCollege; // 学院
    private LocalDate submitDate; // 提交日期
    private Integer state; // 状态
    private String unitOpinion;
    private LocalDate unitOpinionDate;
    private String unitOpinionName;
    private String approvalOpinion;
    private LocalDate approvalOpinionDate;
    private String approvalOpinionName;
    private Integer semesterId; // 学期id
    private Integer submitTeacherId; // 提交老师id
}
