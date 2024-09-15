package com.mldream.pojo.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user")
public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private Integer userId; // 外键关联
}
