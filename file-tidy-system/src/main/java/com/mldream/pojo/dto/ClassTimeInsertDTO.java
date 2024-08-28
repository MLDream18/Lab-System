package com.mldream.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTimeInsertDTO {
    private Integer labId;
    private Integer courseNameId;
    private Integer teacherId;
    private Integer classId;
    private Integer weekday;
    private String section;
    private Integer startWeek;
    private Integer endWeek;
    private String nature;
    private Integer sumTime;
}