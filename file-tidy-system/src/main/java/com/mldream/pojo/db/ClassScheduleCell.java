package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassScheduleCell {
    private Integer classTimeId;
    private String courseName;
    private String teacherName;
    private String weekdaySection;
    private String startEndWeek;
    private String className;
    private String courseNature;
    private String sumCourseHour;
    private String semester;

    public ClassScheduleCell() {
        this.courseName = "";
        this.teacherName = "";
        this.weekdaySection = "";
        this.startEndWeek = "";
        this.courseNature = "";
        this.sumCourseHour = "";
        this.semester = "";
    }
}
