package com.zhy.config.security;

import com.zhy.constant.CodeType;
import com.zhy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: zhangocean
 * @Date: 2020/4/27 10:33
 * Describe:
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    MyAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // 对登录注册要允许匿名访问;
                .antMatchers("/login").permitAll()
                // 其他的路径都要登录之后具备USER角色
//                .antMatchers("/**").hasRole("USER")
                //这里配置的loginProcessingUrl为页面中对应表单的 action ，该请求为 post，并设置可匿名访问
                .and().formLogin().loginProcessingUrl("/login").permitAll()
                //登录成功后的返回结果
                .successHandler(new AuthenticationSuccessHandlerImpl())
                //登录失败后的返回结果
                .failureHandler(new AuthenticationFailureHandlerImpl())
                //这里配置的logoutUrl为登出接口，并设置可匿名访问
                .and().logout().logoutUrl("/logout").permitAll()
                //登出后的返回结果
                .logoutSuccessHandler(new LogoutSuccessHandlerImpl())
                //这里配置的为当未登录访问受保护资源时，返回json，并且让springsecurity自带的登录界面失效
                .and().exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPointHandler());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    //定义登陆成功返回信息
    private class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setContentType("application/json;charset=utf-8");
            User userInfo = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            PrintWriter out = response.getWriter();
            out.write("{\"status\":" + CodeType.LOGIN_SUCCESS.getCode() +",\"message\":\"" + CodeType.LOGIN_SUCCESS.getMessage() + "\",\"data\":\"" + userInfo.getUsername() + "\"}");
            out.flush();
            out.close();
        }
    }

    //定义登出成功返回信息
    private class LogoutSuccessHandlerImpl extends SimpleUrlLogoutSuccessHandler {

        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException, ServletException {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write("{\"status\":" + CodeType.LOGIN_OUT_SUCCESS.getCode() +",\"message\":\"" + CodeType.LOGIN_OUT_SUCCESS.getMessage() + "\"}");
            out.flush();
            out.close();
        }
    }

    //定义登陆失败返回信息
    private class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write("{\"status\":" + CodeType.LOGIN_FAIL.getCode() +",\"message\":\"" + exception.getMessage() + "\"}");
            out.flush();
            out.close();
        }
    }

    public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
            response.sendRedirect("http://localhost:8080/#/index");
        }
    }

}
