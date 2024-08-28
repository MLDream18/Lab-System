package com.mldream.pojo.db;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Teacher {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String telephone;
    private String college;

    public Teacher(String name) {
        this.name = name;
    }

}
