package com.mldream.service;

import com.mldream.pojo.db.Admin;

public interface AdminService {


    Admin login(Admin admin);

    Admin getAdminByUsername(String username);
}
