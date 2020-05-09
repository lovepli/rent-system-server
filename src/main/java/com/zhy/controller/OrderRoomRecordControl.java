package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.model.User;
import com.zhy.service.OrderRoomRecordService;
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
 * @Date: 2020/5/9 22:40
 * Describe:
 */
@RestController
public class OrderRoomRecordControl {

    @Autowired
    private OrderRoomRecordService orderRoomRecordService;

    @PostMapping("/orderRoom")
    @PermissionCheck(value = "ROLE_USER")
    public String orderRoom(@RequestBody HashMap hashMap) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = orderRoomRecordService.orderRoom(hashMap, phone);
        return JsonResult.build(dataMap).toJSON();
    }

}
