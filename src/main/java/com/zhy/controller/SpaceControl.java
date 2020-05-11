package com.zhy.controller;

import com.zhy.aspect.annotation.PermissionCheck;
import com.zhy.model.Landlord;
import com.zhy.model.User;
import com.zhy.service.SpaceService;
import com.zhy.utils.DataMap;
import com.zhy.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/getLandlordInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getLandlordInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.getLandlordInfo(phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getCollectInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getCollectInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.getCollectInfo(phone);
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

    @GetMapping("/deleteCollectRoom")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteCollectRoom(@RequestParam("roomId") int roomId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.deleteCollectRoom(roomId, phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getOrderInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getOrderInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String phone = user.getPhone();

        DataMap dataMap = spaceService.getOrderInfo(phone);
        return JsonResult.build(dataMap).toJSON();
    }

    @GetMapping("/deleteOrder")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteOrder(@RequestParam("orderSerial") String orderSerial) {

        DataMap dataMap = spaceService.deleteOrder(orderSerial);
        return JsonResult.build(dataMap).toJSON();
    }

    @GetMapping("/getHomeInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getHomeInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = user.getId();

        DataMap dataMap = spaceService.getHomeInfo(userId);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getFacilityInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getFacilityInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = user.getId();

        DataMap dataMap = spaceService.getFacilityInfo(userId);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/addFacility")
    @PermissionCheck(value = "ROLE_USER")
    public String addFacility(@RequestBody HashMap hashMap) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = user.getId();

        DataMap dataMap = spaceService.addFacility(hashMap, userId);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/repairFacility")
    @PermissionCheck(value = "ROLE_USER")
    public String repairFacility(@RequestBody HashMap hashMap) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = user.getId();

        DataMap dataMap = spaceService.repairFacility(hashMap, userId);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/payOrderClick")
    @PermissionCheck(value = "ROLE_USER")
    public String payOrderClick(@RequestBody HashMap hashMap) {

        DataMap dataMap = spaceService.payOrderClick(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/getContractInfo")
    @PermissionCheck(value = "ROLE_USER")
    public String getContractInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = user.getId();

        DataMap dataMap = spaceService.getContractInfo(userId);
        return JsonResult.build(dataMap).toJSON();
    }

    @PostMapping("/payBill")
    @PermissionCheck(value = "ROLE_USER")
    public String payBill(@RequestBody HashMap hashMap) {

        DataMap dataMap = spaceService.payBill(hashMap);
        return JsonResult.build(dataMap).toJSON();
    }
}
