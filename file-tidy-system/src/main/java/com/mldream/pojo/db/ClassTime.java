package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassTime {
    private Integer id;
    private Integer weekday;
    private String section;
    private Integer startWeek;
    private Integer endWeek;
    private String nature;
    private Integer courseHour;
    private Integer labId;
    private Integer courseNameId;
    private Integer teacherId;
    private Integer classId;
    private Integer semesterId;
}
