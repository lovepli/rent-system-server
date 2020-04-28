package com.zhy.config.security;

import com.zhy.constant.CodeType;
import com.zhy.model.User;
import com.zhy.redis.StringRedisServiceImpl;
import com.zhy.service.RedisService;
import com.zhy.utils.MD5Util;
import com.zhy.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author: zhangocean
 * @Date: 2020/4/27 10:32
 * Describe:
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private UserDetailsService userDetailService;
    @Autowired
    private RedisService redisService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        String authCode = request.getParameter("authCode"); //短信验证码
        String phone = authentication.getName();  //手机号
        String password = (String) authentication.getCredentials(); //密码

        User userInfo = (User) userDetailService.loadUserByUsername(phone);

        if(!authCode.equals(StringUtil.BLANK)){
            //短信登录
            String trueAuthCode = (String) redisService.getMsgCode(phone);
            if(trueAuthCode == null || !trueAuthCode.equals(authCode)){
                throw new BadCredentialsException(CodeType.AUTH_CODE_ERROR.getMessage());
            }
            redisService.removeMsgCode(phone);
        } else {
            //账号登录
            if (userInfo == null) {
                throw new BadCredentialsException(CodeType.PHONE_NOT_EXIST.getMessage());
            }
            if (!userInfo.getPassword().equals(MD5Util.encode(password))) {
                throw new BadCredentialsException(CodeType.PASSWORD_ERROR.getMessage());
            }
        }
        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();
        return new UsernamePasswordAuthenticationToken(userInfo, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
