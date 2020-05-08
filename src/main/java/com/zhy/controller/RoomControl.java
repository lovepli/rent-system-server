package com.zhy.controller;

import com.zhy.service.RoomService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        DataMap dataMap = roomService.getRoomInfo(hashMap);
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

}
