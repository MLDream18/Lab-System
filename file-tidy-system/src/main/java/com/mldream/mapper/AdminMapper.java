package com.mldream.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.db.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("select * from admin where username = #{username} and password = #{password}")
    Admin selectByUsernameAndPassword(Admin admin);

}
