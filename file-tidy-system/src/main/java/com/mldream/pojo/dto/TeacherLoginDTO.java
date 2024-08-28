package com.mldream.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherLoginDTO {
    private String username;
    private String password;
    private String name;
    private String captcha;
}
