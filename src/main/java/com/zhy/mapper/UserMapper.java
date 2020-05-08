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

    @Update("update user set real_name=#{realName},id_number=#{idNumber} where phone=#{phone}")
    void updateCertInfo(String realName, String idNumber, String phone);

    @Update("update user set phone=#{newPhone} where phone=#{oldPhone}")
    void updatePhoneByOldPhone(String newPhone, String oldPhone);

    @Update("update user set password=#{password} where phone=#{phone}")
    void updatePassword(String password, String phone);

    @Update("update user set head_portrait=#{headPortrait} where phone=#{phone}")
    void updateHeadPortrait(String headPortrait, String phone);

    @Update("update user set username=#{username},email=#{email},gender=#{gender} where phone=#{phone}")
    void updateUserInfo(String username, String email, String phone, String gender);

    @Select("select * from user where phone=#{phone}")
    @Results(id = "userMap", value = {
            @Result(property = "realName", column = "real_name"),
            @Result(property = "headPortrait", column = "head_portrait"),
            @Result(property = "idNumber", column = "id_number")
            })
    User findUserByPhone(String phone);

    @Select("select username from user where phone=#{phone}")
    String findUsernameByPhone(String phone);

    @Select("select id from user where phone=#{phone}")
    int findIdByPhone(String phone);
}
