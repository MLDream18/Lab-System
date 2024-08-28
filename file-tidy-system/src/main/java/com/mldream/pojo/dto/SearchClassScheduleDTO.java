package com.mldream.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SearchClassScheduleDTO {
    private Integer semesterId;
    private Integer startWeek;
    private Integer endWeek;
    private String place;
}
