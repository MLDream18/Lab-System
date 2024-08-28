package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseName {
    private Integer id;
    private String courseName;
    private String subject;
}
