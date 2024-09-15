package com.mldream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
