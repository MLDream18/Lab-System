package com.mldream.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTimeInfoVO {
    private Integer classTimeId;
    private String labName;
    private String courseName;
    private String teacherName;
    private String weekdaySection;
    private String startEndWeek;
    private String className;
    private String courseNature;
    private String sumCourseHour;
    private String semester;
}
