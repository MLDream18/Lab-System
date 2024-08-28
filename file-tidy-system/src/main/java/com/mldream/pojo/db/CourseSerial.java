package com.mldream.pojo.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSerial {
    private Integer id;
    private String courseSerial;
    private Integer courseNameId;
    private Integer semesterId;
}
