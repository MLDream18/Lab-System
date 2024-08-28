package com.mldream.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperimentProjectDTO {
    private Integer courseNameId;
    private Integer labId;
    private Integer experimentCategory;
    private Integer experimentPeople;
    private Integer experimentDemand;
    private List<Integer> classId;
    private Integer semesterId;
    private List<ExperimentProjectItemDTO> domains;
}
