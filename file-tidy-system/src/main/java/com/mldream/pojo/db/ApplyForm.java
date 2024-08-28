package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyForm {
    private Integer id;
    private Integer usedLabId; // 实验室id
    private LocalDate usedStartDate; // 实验开始日期
    private String usedWeek;
    private String usedSection;
    private Integer sumCourseHours; // 总课时
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
