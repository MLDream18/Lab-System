package com.mldream.pojo.dto;

import com.mldream.pojo.db.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLoginDTO {
    private String captcha;
    private String username;
    private String password;
}
