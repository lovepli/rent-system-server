package com.zhy.controller;

import com.zhy.service.StewardService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:18
 * Describe:
 */
@RestController
public class StewardControl {

    @Autowired
    private StewardService stewardService;

    @PostMapping("/getStewardInfo")
    public String getStewardInfo(@RequestBody HashMap hashMap){
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DataMap dataMap =  stewardService.getStewardInfo(hashMap, obj);
        return JsonResult.build(dataMap).toJSON();
    }

}
