package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.model.Landlord;
import com.zhy.model.User;
import com.zhy.service.SpaceService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sun.net.idn.Punycode;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 14:14
 * Describe:
 */
@RestController
public class SpaceControl {

    @Autowired
    private SpaceService spaceService;

    @PostMapping("/getSpaceInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getSpaceInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.getSpaceInfo(phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/saveLandlordInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String saveLandlordInfo(@RequestBody HashMap hashMap) {

        DataMap dataMap = spaceService.saveLandlordInfo(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/saveHouseResource")
    @PermissionCheck(value = "ROLE_USER")
    public String saveHouseResource(@RequestBody HashMap hashMap) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.saveHouseResource(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getHouseResourceInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getHouseResourceInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.getHouseResourceInfo(phone);
        return JsonResult.build(dataMap).toJSON();
    }

}
