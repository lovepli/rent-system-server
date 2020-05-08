package com.zhy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.constant.CodeType;
import com.zhy.mapper.CommunityMapper;
import com.zhy.mapper.HouseResourceMapper;
import com.zhy.mapper.LandlordMapper;
import com.zhy.model.Community;
import com.zhy.model.HouseResource;
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
 * @Date: 2020/5/8 14:50
 * Describe:
 */
@Service
public class RoomService {

    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private LandlordMapper landlordMapper;
    @Autowired
    private HouseResourceMapper houseResourceMapper;

    public DataMap getRoomInfo(HashMap hashMap){
        int roomId = Integer.parseInt(hashMap.get("roomId").toString());
        HouseResource houseResource = houseResourceMapper.findHouseResourcesByRoomId(roomId);

        if (houseResource == null) {
            return DataMap.fail(CodeType.SERVER_EXCEPTION);
        }

        JSON json = (JSON)JSON.toJSON(houseResource);
        Map<String, Object> houseInfo = (Map<String, Object>) JSONObject.parseObject(json.toString());

        houseInfo.put("allocation", StringUtil.StringToList(houseResource.getAllocation()));
        List<String> areaTags = StringUtil.StringToList(houseResource.getAreaTag());
        houseInfo.put("areaTag", areaTags);
        houseInfo.put("roomPic", StringUtil.StringToList(houseResource.getRoomPic()));
        houseInfo.put("facility", StringUtil.StringToList(houseResource.getFacility()));
        StringBuilder roomTitle = new StringBuilder();
        for(String s : areaTags){
            roomTitle.append(s).append(" ");
        }
        roomTitle.append(houseResource.getHouseName());
        houseInfo.put("roomTitle", roomTitle.toString());
        if(houseResource.getLift() == 0){
            houseInfo.put("lift", "无");
        } else {
            houseInfo.put("lift", "有");
        }

        List<HouseResource> resources = houseResourceMapper.findHouseResourcesByAreaTag(houseResource.getAreaTag());

        //添加室友信息
        HashMap<String, Object> chumMap;
        List<HashMap<String, Object>> chumList = new ArrayList<>(4);
        for(HouseResource h : resources){
            chumMap = new HashMap<String, Object>();
            chumMap.put("room", h.getHouseName());
            if(h.getId() == roomId){
                chumMap.put("state", 2);
            } else {
                chumMap.put("state", h.getRentState());
            }
            chumMap.put("gender", h.getHouseName());
            chumMap.put("areaArch", h.getBuildArea());
            List<String> facilities = StringUtil.StringToList(h.getFacility());
            if(facilities.contains("独卫")){
                chumMap.put("toilet", 1);
            } else {
                chumMap.put("toilet", 0);
            }
            if(facilities.contains("淋浴")){
                chumMap.put("bathroom", 1);
            } else {
                chumMap.put("bathroom", 0);
            }
            if(facilities.contains("阳台")){
                chumMap.put("balcony", 1);
            } else {
                chumMap.put("balcony", 0);
            }
            chumMap.put("rent", h.getRent());
            chumMap.put("id", h.getId());
            chumList.add(chumMap);
        }
        houseInfo.put("chumInfo", chumList);

        //添加小区信息
        int landlordId = houseResource.getLandlordId();
        String communityIdStr = landlordMapper.findCommunityById(landlordId);
        Community community = communityMapper.findCommunityById(Integer.parseInt(communityIdStr));

        JSON communityJson = (JSON)JSON.toJSON(community);
        Map<String, Object> communityInfo = (Map<String, Object>) JSONObject.parseObject(communityJson.toString());
        if(community.getHeatingMethod() == 0) {
            communityInfo.put("heatingMethod", "无");
        } else {
            communityInfo.put("heatingMethod", "有");
        }
        houseInfo.put("communityInfo", communityInfo);

        return DataMap.success().setData(houseInfo);
    }

    public DataMap getRoomInfoByCity(HashMap hashMap){
        String city = (String) hashMap.get("city");

        List<HouseResource> resources = houseResourceMapper.findHouseResourcesByCityAndRentStateAndSort(city, 0, "id", "desc");

        List<HouseResource> retData = handleHouseResource(resources);

        return DataMap.success().setData(retData);
    }

    public DataMap getRoomInfoByCityAndSort(HashMap hashMap){

        String city = (String) hashMap.get("city");
        String sort = (String) hashMap.get("sort");
        String rank = (String) hashMap.get("rank");
        if("priceSort".equals(sort)) {
            sort = "rent";
        } else if ("areaSort".equals(sort)){
            sort = "build_area";
        } else {
            sort = "id";
            rank = "desc";
        }
        List<HouseResource> resources = houseResourceMapper.findHouseResourcesByCityAndRentStateAndSort(city, 0, sort, rank);

        List<HouseResource> retData = handleHouseResource(resources);

        return DataMap.success().setData(retData);
    }

    private List<HouseResource> handleHouseResource(List<HouseResource> resources) {
        List<HouseResource> retData = new ArrayList<>();
        for(HouseResource h : resources){
            String roomPic = h.getRoomPic();
            if(roomPic.contains(",")){
                h.setRoomPic(roomPic.substring(0, roomPic.indexOf(",")));
            }
            StringBuilder roomTitle = new StringBuilder();
            if(!"整租".equals(h.getHouseName())){
                roomTitle.append("合租·");
            } else {
                roomTitle.append(h.getHouseName()).append("·");
            }
            List<String> areaTag = StringUtil.StringToList(h.getAreaTag());
            roomTitle.append(areaTag.get(areaTag.size()-1));
            roomTitle.append(h.getDoorModel()).append("·").append(h.getToward());
            h.setHouseName(roomTitle.toString());

            retData.add(h);
        }
        return retData;
    }

}
