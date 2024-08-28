package com.mldream.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperimentProjectVO {
    private Integer experimentProjectId;
    private String courseSerial;
    private String experimentContent;
    private String labName;
    private String courseName;
    private Integer experimentCategory;
    private String classIds;
    private Integer experimentPeople;
    private Integer experimentDemand;
    private Integer experimentType;
    private Integer experimentHours;
    private String teacherName;
    private String semester;
}
