package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Laboratory {
    private Integer id;
    private String name;

    private List<ClassScheduleCell> labScheduleList;
}
