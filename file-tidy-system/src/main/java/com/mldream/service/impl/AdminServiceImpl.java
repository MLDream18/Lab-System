package com.mldream.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mldream.mapper.AdminMapper;
import com.mldream.pojo.db.Admin;
import com.mldream.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(Admin admin) {
        return adminMapper.selectByUsernameAndPassword(admin);
    }

    @Override
    public Admin getAdminByUsername(String username) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return adminMapper.selectOne(queryWrapper);
    }
}
