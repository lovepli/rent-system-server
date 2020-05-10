package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.model.User;
import com.zhy.service.RoomService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2020/5/8 13:14
 * Describe:
 */
@RestController
public class RoomControl {

    @Autowired
    private RoomService roomService;

    @PostMapping("/getRoomInfo")
    public String getRoomInfo(@RequestBody HashMap hashMap){
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DataMap dataMap = roomService.getRoomInfo(hashMap, obj);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getRoomInfoByCity")
    public String getRoomInfoByCity(@RequestBody HashMap hashMap){
        DataMap dataMap = roomService.getRoomInfoByCity(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getRoomInfoByCityAndSort")
    public String getRoomInfoByCityAndSort(@RequestBody HashMap hashMap){
        DataMap dataMap = roomService.getRoomInfoByCityAndSort(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @GetMapping("/startLookingHouse")
    public String startLookingHouse(@RequestParam("search_text") String searchText,
                                    @RequestParam("city") String city){
        DataMap dataMap = roomService.startLookingHouse(searchText, city);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/searchRoomByCondition")
    public String searchRoomByCondition(@RequestBody HashMap hashMap){
        DataMap dataMap = roomService.searchRoomByCondition(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/collectRoom")
    @PermissionCheck(value = "ROLE_USER")
    public String collectRoom(@RequestBody HashMap hashMap){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = roomService.collectRoom(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getCollectState")
    public String getCollectState(@RequestBody HashMap hashMap){
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DataMap dataMap = roomService.getCollectState(hashMap, obj);
        return JsonResult.build(dataMap).toJSON();
    }

}
