package com.mldream.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyFormInfoVO {
    private Integer applyFormId;
    private String labName;
    private LocalDate usedStartDate;
    private String usedWeek;
    private String usedSection;
    private Integer applyReason;
    private Integer sumCourseHours;
    private String courseName;
    private String experimentContent;
    private String className;
    private Integer experimentPeople;
    private String applicant;
    private String applicantTelephone;
    private String applicantCollege;
    private LocalDate submitDate; // 提交日期
    private String unitOpinion;
    private LocalDate unitOpinionDate;
    private String unitOpinionName;
    private String approvalOpinion;
    private LocalDate approvalOpinionDate;
    private String approvalOpinionName;
    private Integer semesterId;
    private Integer submitTeacherId;
    private Integer state;
}
