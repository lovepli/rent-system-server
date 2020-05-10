package com.zhy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.aspect.PrincipalAspect;
import com.zhy.constant.CodeType;
import com.zhy.mapper.*;
import com.zhy.model.CollectRoom;
import com.zhy.model.Community;
import com.zhy.model.HouseResource;
import com.zhy.model.User;
import com.zhy.utils.DataMap;
import com.zhy.utils.SortUtil;
import com.zhy.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.crypto.Data;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author: zhangocean
 * @Date: 2020/5/8 14:50
 * Describe:
 */
@Service
public class RoomService {

    private final static String UN_LIMITED = "不限";
    private final static String ENTIRE_TENANCY = "整租";
    private final static String HAS_LIFT = "有电梯";
    private final static String NEAR_SUBWAY = "离地铁近";

    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private LandlordMapper landlordMapper;
    @Autowired
    private HouseResourceMapper houseResourceMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CollectRoomMapper collectRoomMapper;

    public DataMap getRoomInfo(HashMap hashMap, Object obj){

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

        String collectState = getCollectState(obj, roomId);
        houseInfo.put("collectState", collectState);

        return DataMap.success().setData(houseInfo);
    }

    public DataMap getRoomInfoByCity(HashMap hashMap){
        String city = (String) hashMap.get("city");

        List<HouseResource> resources = houseResourceMapper.findHouseResourcesByCityAndRentStateAndSort(city, 0, "id", "desc");

        List<HouseResource> retData = handleHouseResource(resources);

        return DataMap.success().setData(retData);
    }

    public DataMap getRoomInfoByCityAndSort(HashMap hashMap){

        String sort = (String) hashMap.get("sort");
        String rank = (String) hashMap.get("rank");
        if(!"priceSort".equals(sort) && !"areaSort".equals(sort)) {
            sort = "id";
            rank = "desc";
        }
//        List<HouseResource> resources = houseResourceMapper.findHouseResourcesByCityAndRentStateAndSort(city, 0, sort, rank);

        List<HouseResource> retData = getRoomInfoByCondition(hashMap);

        retData = SortUtil.sortHouseResource(sort, rank, retData);

        return DataMap.success().setData(retData);
    }

    public DataMap startLookingHouse(String searchText, String city){

        List<HouseResource> houseResources = houseResourceMapper.findLikeAreaTagBySearchTextAndCity(searchText, 0, city);

        Set<HouseResource> houseResourceSet = new HashSet<>(houseResources);

        houseResources = houseResourceMapper.findLikeSubwayBySearchTextAndCity(searchText, 0, city);
        houseResourceSet.addAll(houseResources);

        List<HouseResource> retData = handleHouseResource(new ArrayList<>(houseResourceSet));

        return DataMap.success().setData(retData);
    }

    public DataMap searchRoomByCondition(HashMap hashMap) {

        List<HouseResource> retData = getRoomInfoByCondition(hashMap);
        return DataMap.success().setData(retData);
    }

    public DataMap collectRoom(HashMap hashMap, String phone){

        int collectUserId = userMapper.findIdByPhone(phone);
        CollectRoom collectRoom = JSONObject.parseObject(JSONObject.toJSONString(hashMap), CollectRoom.class);
        collectRoom.setCollectUserId(collectUserId);

        int collectRoomId = collectRoomMapper.findIsExistByPhone(collectRoom.getRoomId(), collectUserId);

        if (collectRoomId != 0) {
            return DataMap.success(CodeType.COLLECT_ROOM_EXIST);
        }

        collectRoomMapper.save(collectRoom);

        return DataMap.success();
    }

    public DataMap getCollectState(HashMap hashMap, Object obj){
        System.out.println("roomId:" + hashMap);
        int roomId = Integer.parseInt(hashMap.get("roomId").toString());

        String collectState = getCollectState(obj, roomId);

        return DataMap.success().setData(collectState);
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

    private List<HouseResource> getRoomInfoByCondition(HashMap hashMap){
        String city = (String) hashMap.get("city");
        String area = (String) hashMap.get("area");
        String subway = (String) hashMap.get("subway");
        String areaStand = (String) hashMap.get("areaStand");
        String subwayStand = (String) hashMap.get("subwayStand");
        String rent = (String) hashMap.get("rent");
        String doorModel = (String) hashMap.get("doorModel");
        String roomType = (String) hashMap.get("roomType");
        String feature = (String) hashMap.get("feature");

        //没有搜索条件时
        if (area.equals(UN_LIMITED) &&
                subway.equals(UN_LIMITED) &&
                rent.equals(UN_LIMITED) &&
                doorModel.equals(UN_LIMITED) &&
                roomType.equals(UN_LIMITED) &&
                feature.equals(UN_LIMITED)){
            List<HouseResource> resources = houseResourceMapper.findHouseResourcesByCityAndRentStateAndSort(city, 0, "id", "desc");
            return handleHouseResource(resources);
        }

        List<HouseResource> resources;
        Set<HouseResource> areaSet;
        Set<HouseResource> subwaySet;
        Set<HouseResource> rentSet;
        Set<HouseResource> doorModelSet;
        Set<HouseResource> roomTypeSet;
        Set<HouseResource> featureSet;
        Set<HouseResource> retSet = new HashSet<>();
        List<Set<HouseResource>> listSets = new ArrayList<>();

        if (!area.equals(UN_LIMITED)) {
            if (!areaStand.equals(UN_LIMITED)) {
                area += area + "," + areaStand;
            }
            resources = houseResourceMapper.findLikeAreaTagBySearchTextAndCity(area, 0, city);
            areaSet = new HashSet<>(resources);
            listSets.add(areaSet);
        }
        if (!subway.equals(UN_LIMITED)){
            if (!subwayStand.equals(UN_LIMITED)) {
                subway += subway + "," + subwayStand;
            }
            resources = houseResourceMapper.findLikeSubwayBySearchTextAndCity(subway, 0, city);
            subwaySet = new HashSet<>(resources);
            listSets.add(subwaySet);
        }

        if(!rent.equals(UN_LIMITED)){
            String[] rents = rent.split("-");
            resources = houseResourceMapper.findByLowAndHighPriceAndCity(Integer.parseInt(rents[0]), Integer.parseInt(rents[1]), 0, city);
            rentSet = new HashSet<>(resources);
            listSets.add(rentSet);
        }

        if(!doorModel.equals(UN_LIMITED)){
            resources = houseResourceMapper.findByDoorModelAndCity(doorModel, 0, city);
            doorModelSet = new HashSet<>(resources);
            listSets.add(doorModelSet);
        }

        if(!roomType.equals(UN_LIMITED)){
            if (roomType.equals(ENTIRE_TENANCY)) {
                resources = houseResourceMapper.findByEntireRoomTypeAndCity(roomType, 0, city);
            } else {
                resources = houseResourceMapper.findByRoomTypeAndCity(ENTIRE_TENANCY, 0, city);
            }
            roomTypeSet = new HashSet<>(resources);
            listSets.add(roomTypeSet);
        }

        if(!feature.equals(UN_LIMITED)){
            if (feature.equals(HAS_LIFT)) {
                resources = houseResourceMapper.findByLiftTypeAndCity(1, 0, city);
            } else if (feature.equals(NEAR_SUBWAY)){
                resources = houseResourceMapper.findIsNearSubwayByCity(StringUtil.BLANK, 0, city);
            } else {
                resources = houseResourceMapper.findLikeFacilityBySearchTextAndCity(feature, 0, city);
            }
            featureSet = new HashSet<>(resources);
            listSets.add(featureSet);
        }
        for(int i=0;i<listSets.size();i++){
            if(i == 0){
                retSet.addAll(listSets.get(i));
            } else {
                retSet.retainAll(listSets.get(i));
            }
        }

        List<HouseResource> retData = handleHouseResource(new ArrayList<>(retSet));
        return retData;
    }

    private String getCollectState(Object obj, int roomId){
        if (obj.equals(PrincipalAspect.ANONYMOUS_USER)) {
            return "收藏";
        } else {
            User user = (User) obj;
            int collectUserId = userMapper.findIdByPhone(user.getPhone());
            int collectRoomId = collectRoomMapper.findIsExistByPhone(roomId, collectUserId);
            if (collectRoomId == 0) {
                return "收藏";
            }
        }

        return "已收藏";
    }
}
