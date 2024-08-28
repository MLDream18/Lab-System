package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperimentProject {
    private Integer id;
    private String experimentContent;
    private String experimentNo;
    private Integer labId;
    private Integer courseNameId;
    private Integer experimentCategory;
    private String classIds;
    private Integer experimentPeople;
    private Integer experimentDemand;
    private Integer experimentType;
    private Integer experimentHours;
    private Integer teacherId;
    private Integer semesterId;
}
