package com.mldream.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplySearchConditionsDTO {
    private Integer labId;
    private Integer startWeek;
    private Integer endWeek;
    private Integer startWeekday;
    private Integer endWeekday;
    private String startSection;
    private String endSection;
    private Integer state;
    private Integer semesterId;
}
