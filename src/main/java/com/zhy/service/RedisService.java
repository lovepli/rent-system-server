package com.zhy.service;

import com.zhy.redis.StringRedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: zhangocean
 * @Date: 2020/4/27 13:08
 * Describe: redis操作
 */
@Service
public class RedisService {

    @Autowired
    StringRedisServiceImpl stringRedisServiceImpl;

    //保存短信验证码
    public void saveMsgCode(String phone, String authCode){
        stringRedisServiceImpl.set(phone, authCode, 300);
    }

    //获得短信验证码
    public String getMsgCode(String phone){
        return (String) stringRedisServiceImpl.get(phone);
    }

}
