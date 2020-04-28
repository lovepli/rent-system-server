package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.model.User;
import com.zhy.service.UserService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2020/3/29 13:46
 * Describe:
 */
@RestController
public class UserControl {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserLoginInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserLoginInfo(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        return JsonResult.success().data(username).toJSON();
    }

    @PostMapping("/registerUser")
    public String registerUser(@RequestBody HashMap hashMap) {
        DataMap dataMap = userService.registerUser(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getUserInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();
        DataMap dataMap = userService.getUserInfo(phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/registerCertInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String registerCertInfo(@RequestBody HashMap hashMap){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = userService.registerCertInfo(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/changePhone")
    @PermissionCheck(value = "ROLE_USER")
    public String changePhone(@RequestBody HashMap hashMap){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = userService.changePhone(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/changePassword")
    @PermissionCheck(value = "ROLE_USER")
    public String changePassword(@RequestBody HashMap hashMap) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = userService.changePassword(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/saveUserInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String saveUserInfo(@RequestBody HashMap hashMap){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = userService.saveUserInfo(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/updateHeadPortrait")
    @PermissionCheck(value = "ROLE_USER")
    public String updateHeadPortrait(@RequestParam("headPortraitImg") MultipartFile file){
        return "success";
    }

}
