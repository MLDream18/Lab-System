package com.mldream.controller.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mldream.mapper.UserMapper;
import com.mldream.pojo.dto.User;
import com.mldream.pojo.vo.Result;
import com.mldream.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (Exception e) {
            return Result.error("用户名或密码错误");
        }
        userDetailsService.loadUserByUsername(user.getUsername());

        user = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("id", user.getUserId());
        String token = JwtUtils.generateJwt(claims);
        return Result.success(token);
    }

}
