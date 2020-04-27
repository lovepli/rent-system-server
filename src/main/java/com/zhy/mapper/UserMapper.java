package com.zhy.mapper;

import com.zhy.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2018/6/4 15:52
 * Describe: user表SQL语句
 */
@Mapper
@Repository
public interface UserMapper {

    @Insert("insert into user(phone, username, password, roles) values(#{phone}, #{username}, #{password}, #{roles})")
    int save(User user);

    @Select("select * from user where phone=#{phone}")
    User findUserByPhone(String phone);
}
