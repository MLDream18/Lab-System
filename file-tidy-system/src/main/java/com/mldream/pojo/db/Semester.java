package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Semester {
    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer stage;
    private Integer startYear;
    private Integer endYear;
}
