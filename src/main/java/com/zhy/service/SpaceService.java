package com.zhy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Integer buildingAge = 0;
        if(!hashMap.get("buildingAge").equals(StringUtil.BLANK)){
            buildingAge = Integer.parseInt(hashMap.get("buildingAge").toString());
        }
        String buildingType = (String) hashMap.get("buildingType");
        Integer heatingMethod = 0;
        if(!hashMap.get("heatingMethod").equals(StringUtil.BLANK)){
            heatingMethod = Integer.parseInt(hashMap.get("heatingMethod").toString());
        }
        Double greeningRate = 0.0;
        if(!hashMap.get("greeningRate").equals(StringUtil.BLANK)){
            greeningRate = Double.parseDouble(hashMap.get("greeningRate").toString());
        }
        Double plotRatio = 0.0;
        if(!hashMap.get("plotRatio").equals(StringUtil.BLANK)){
            plotRatio = Double.parseDouble(hashMap.get("plotRatio").toString());
        }
        String propertyCompany = (String) hashMap.get("propertyCompany");
        String propertyPhone = (String) hashMap.get("propertyPhone");

        Landlord landlord = new Landlord(name, phone, houseCity);
        Community community = new Community(communityName, houseCity, buildingAge, buildingType, heatingMethod, greeningRate, plotRatio, propertyCompany, propertyPhone);

        //保存小区
        int isExistCommunity = communityMapper.findIsExistByCommunityNameAndHouseCity(communityName, houseCity);

        if(isExistCommunity == 0){
            communityMapper.save(community);
        } else {
            community.setId(isExistCommunity);
        }

        //保存房东信息
        int landlordId = landlordMapper.findIsExistByPhone(phone);
        if(landlordId == 0){
            landlord.setCommunity(community.getId()+"");
            landlordMapper.save(landlord);
        } else {
            String landlordCommunity = landlordMapper.findCommunityByPhone(phone);
            landlord.setCommunity(landlordCommunity + "," + community.getId());
            landlordMapper.updateLandlordByPhone(landlord.getCommunity(), phone);
        }

        return DataMap.success();
    }


    public DataMap saveHouseResource(HashMap hashMap, String phone) {

        HouseResource houseResource = JSONObject.parseObject(JSONObject.toJSONString(hashMap), HouseResource.class);

        String allocation = StringUtil.listToString((ArrayList) hashMap.get("allocations"));
        String areaTag = StringUtil.listToString((ArrayList) hashMap.get("areaTags"));
        String facility = StringUtil.listToString((ArrayList) hashMap.get("facilities"));
        String subway = StringUtil.listToString((ArrayList) hashMap.get("subway"));

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
        houseResource.setSubway(subway);
        houseResource.setRoomPic(roomPic.toString());
        String communityIdStr = landlordMapper.findCommunityById(landlordId);
        houseResource.setHouseCity(communityMapper.findHouseCityById(Integer.parseInt(communityIdStr)));

        houseResourceMapper.save(houseResource);

        HashMap<String, Object> houseMap = new HashMap<>();
        houseMap.put("id", houseResource.getId());
        houseMap.put("houseName", areaTag.replace(",", " ") + " " + houseResource.getHouseName());
        houseMap.put("rent", houseResource.getRent());
        houseMap.put("toward", houseResource.getToward());
        houseMap.put("floor", houseResource.getFloor());
        if(houseResource.getLift() == 0){
            houseMap.put("lift", "无");
        } else {
            houseMap.put("lift", "有");
        }
        houseMap.put("rentState", "未出租");

        return DataMap.success().setData(houseMap);
    }

    public DataMap getHouseResourceInfo(String phone){
        int landlordId = userMapper.findIdByPhone(phone);

        List<HouseResource> houseResources = houseResourceMapper.findHouseResourcesByLandlordId(landlordId);
        HashMap<String, Object> houseMap;
        List<HashMap<String, Object>> dataList = new ArrayList<>();
        for(HouseResource h : houseResources){
            houseMap = new HashMap<>();
            List<String> areaTags = StringUtil.StringToList(h.getAreaTag());
            StringBuilder houseName = new StringBuilder();
            for(String s : areaTags){
                houseName.append(s).append(" ");
            }
            houseName.append(h.getHouseName());
            houseMap.put("id", h.getId());
            houseMap.put("houseName", houseName.toString());
            houseMap.put("rent", h.getRent());
            houseMap.put("toward", h.getToward());
            houseMap.put("floor", h.getFloor());
            if(h.getLift() == 0){
                houseMap.put("lift", "无");
            } else {
                houseMap.put("lift", "有");
            }
            if(h.getRentState() == 0){
                houseMap.put("rentState", "未出租");
            } else {
                houseMap.put("rentState", "已出租");
            }
            dataList.add(houseMap);
        }

        return DataMap.success().setData(dataList);
    }
}
