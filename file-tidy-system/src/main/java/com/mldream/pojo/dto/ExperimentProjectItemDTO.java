package com.mldream.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperimentProjectItemDTO {
    private String experimentNo;
    private String experimentContent;
    private Integer experimentType;
    private Integer experimentHours;
}
