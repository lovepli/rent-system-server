package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.service.UserService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

}
