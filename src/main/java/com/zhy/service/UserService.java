package com.zhy.service;

import com.zhy.constant.CodeType;
import com.zhy.constant.RoleConstant;
import com.zhy.mapper.UserMapper;
import com.zhy.model.User;
import com.zhy.utils.DataMap;
import com.zhy.utils.MD5Util;
import com.zhy.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2018/6/4 15:54
 * Describe: user业务操作
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {


        User user = userMapper.findUserByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    public DataMap registerUser(HashMap hashMap) {
        String phone = (String) hashMap.get("phone");
        String authCode = (String) hashMap.get("authCode");
        String password = (String) hashMap.get("password");
        String username = "rj"+ new TimeUtil().getLongTime();
        String encryptionPsw = MD5Util.encode(password);

        String trueAuthCode = redisService.getMsgCode(phone);
        if(trueAuthCode == null || !trueAuthCode.equals(authCode)) {
            return DataMap.fail(CodeType.AUTH_CODE_ERROR);
        }

        User user = userMapper.findUserByPhone(phone);
        if(user != null) {
            return DataMap.fail(CodeType.PHONE_EXIST);
        }

        User userInfo = new User(phone, username, encryptionPsw, RoleConstant.ROLE_USER);
        userMapper.save(userInfo);

        return DataMap.success(CodeType.REGISTER_SUCCESS);
    }
}
