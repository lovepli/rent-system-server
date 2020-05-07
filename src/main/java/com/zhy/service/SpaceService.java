package com.zhy.service;

import com.alibaba.fastjson.JSONObject;
import com.zhy.mapper.CommunityMapper;
import com.zhy.mapper.HouseResourceMapper;
import com.zhy.mapper.LandlordMapper;
import com.zhy.mapper.UserMapper;
import com.zhy.model.Community;
import com.zhy.model.HouseResource;
import com.zhy.model.Landlord;
import com.zhy.utils.DataMap;
import com.zhy.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 14:42
 * Describe:
 */
@Service
public class SpaceService {

    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private LandlordMapper landlordMapper;
    @Autowired
    private HouseResourceMapper houseResourceMapper;
    @Autowired
    private UserMapper userMapper;

    public DataMap getSpaceInfo(String phone) {
        List<Landlord> landlords = landlordMapper.findLandlordByPhone(phone);

        HashMap<String, Object> spaceData = new HashMap<>(8);
        spaceData.put("landlords", landlords);

        return DataMap.success().setData(spaceData);
    }

    public DataMap saveLandlordInfo(HashMap hashMap){
        String name = (String) hashMap.get("name");
        String phone = (String) hashMap.get("phone");
        String houseCity = (String) hashMap.get("houseCity");
        String communityName = (String) hashMap.get("communityName");
        Integer buildingAge = Integer.parseInt(hashMap.get("buildingAge").toString());
        String buildingType = (String) hashMap.get("buildingType");
        Integer heatingMethod = Integer.parseInt(hashMap.get("heatingMethod").toString());
        Double greeningRate = Double.parseDouble(hashMap.get("greeningRate").toString());
        Double plotRatio = Double.parseDouble(hashMap.get("plotRatio").toString());
        String propertyCompany = (String) hashMap.get("propertyCompany");
        String propertyPhone = (String) hashMap.get("propertyPhone");

        Landlord landlord = new Landlord(name, phone, houseCity);
        Community community = new Community(communityName, houseCity, buildingAge, buildingType, heatingMethod, greeningRate, plotRatio, propertyCompany, propertyPhone);

        //保存小区
        int isExistCommunity = communityMapper.findIsExistByCommunityNameAndHouseCity(communityName, houseCity);
        System.out.println(isExistCommunity);

        if(isExistCommunity == 0){
            isExistCommunity = communityMapper.save(community);
        }

        //保存房东信息
        int landlordId = landlordMapper.findIsExistByPhone(phone);
        if(landlordId == 0){
            landlord.setCommunity(isExistCommunity+"");
            landlordMapper.save(landlord);
        } else {
            String landlordCommunity = landlordMapper.findCommunityByPhone(phone);
            landlord.setCommunity(landlordCommunity + "," + isExistCommunity);
            landlordMapper.updateLandlordByPhone(landlord.getCommunity(), phone);
        }

        return DataMap.success();
    }


    public DataMap saveHouseResource(HashMap hashMap, String phone) {

        HouseResource houseResource = JSONObject.parseObject(JSONObject.toJSONString(hashMap), HouseResource.class);

        String allocation = StringUtil.listToString((ArrayList) hashMap.get("allocations"));
        String areaTag = StringUtil.listToString((ArrayList) hashMap.get("areaTags"));
        String facility = StringUtil.listToString((ArrayList) hashMap.get("facilities"));

        //处理房间照片
        ArrayList roomPics = (ArrayList) hashMap.get("roomPics");
        StringBuilder roomPic = new StringBuilder();
        HashMap<String, String> roomPicMap;
        for(int i=0;i<roomPics.size();i++){
            roomPicMap = (HashMap<String, String>) roomPics.get(i);
            if(roomPic.length() == 0){
                roomPic.append(roomPicMap.get("url").trim());
            } else {
                roomPic.append(",").append(roomPicMap.get("url").trim());
            }
        }

        int landlordId = userMapper.findIdByPhone(phone);
        houseResource.setLandlordId(landlordId);
        houseResource.setAllocation(allocation);
        houseResource.setAreaTag(areaTag);
        houseResource.setFacility(facility);
        houseResource.setRoomPic(roomPic.toString());

        int houseId = houseResourceMapper.save(houseResource);

        System.out.println(houseId);
        HouseResource house = new HouseResource();
        house.setId(houseId);
        house.setHouseName(areaTag.replace(",", " ") + " " + houseResource.getHouseName());
        house.setRent(houseResource.getRent());
        house.setToward(houseResource.getToward());
        house.setFloor(houseResource.getFloor());
        house.setLift(houseResource.getLift());

        return DataMap.success().setData(house);
    }

    public DataMap getHouseResourceInfo(String phone){
        int landlordId = userMapper.findIdByPhone(phone);

        List<HouseResource> houseResources = houseResourceMapper.findHouseResourcesByLandlordId(landlordId);
        HouseResource houseResource;
        List<HouseResource> dataList = new ArrayList<>();
        for(HouseResource h : houseResources){
            houseResource = new HouseResource();
            List<String> areaTags = StringUtil.StringToList(h.getAreaTag());
            StringBuilder houseName = new StringBuilder();
            for(String s : areaTags){
                houseName.append(s).append(" ");
            }
            houseName.append(h.getHouseName());
            houseResource.setId(h.getId());
            houseResource.setHouseName(houseName.toString());
            houseResource.setRent(h.getRent());
            houseResource.setToward(h.getToward());
            houseResource.setFloor(h.getFloor());
            houseResource.setLift(h.getLift());
            dataList.add(houseResource);
        }

        return DataMap.success().setData(dataList);
    }
}
