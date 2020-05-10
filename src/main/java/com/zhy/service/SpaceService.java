package com.zhy.service;

import com.alibaba.fastjson.JSONObject;
import com.zhy.constant.CodeType;
import com.zhy.mapper.*;
import com.zhy.model.*;
import com.zhy.utils.DataMap;
import com.zhy.utils.StringUtil;
import com.zhy.utils.TimeUtil;
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
    @Autowired
    private CollectRoomMapper collectRoomMapper;
    @Autowired
    private OrderRoomRecordMapper orderRoomRecordMapper;
    @Autowired
    private StewardMapper stewardMapper;
    @Autowired
    private TemperatureMapper temperatureMapper;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    public DataMap getLandlordInfo(String phone) {

        List<Landlord> landlords = landlordMapper.findLandlordByPhone(phone);

        return DataMap.success().setData(landlords);
    }

    public DataMap getCollectInfo(String phone) {
        int collectUserId = userMapper.findIdByPhone(phone);
        List<Integer> roomIds = collectRoomMapper.findRoomIdByCollectUserId(collectUserId);

        List<HouseResource> houseResources = new ArrayList<>();
        HouseResource houseResource;

        for (Integer roomId : roomIds) {
            houseResource = houseResourceMapper.findHouseResourcesByRoomId(roomId);

            String roomPic = houseResource.getRoomPic();
            if(roomPic.contains(",")){
                houseResource.setRoomPic(roomPic.substring(0, roomPic.indexOf(",")));
            }
            StringBuilder roomTitle = new StringBuilder();
            if(!"整租".equals(houseResource.getHouseName())){
                roomTitle.append("合租·");
            } else {
                roomTitle.append(houseResource.getHouseName()).append("·");
            }
            List<String> areaTag = StringUtil.StringToList(houseResource.getAreaTag());
            roomTitle.append(areaTag.get(areaTag.size()-1));
            roomTitle.append(houseResource.getDoorModel()).append("·").append(houseResource.getToward());
            houseResource.setHouseName(roomTitle.toString());

            houseResources.add(houseResource);
        }

        return DataMap.success().setData(houseResources);
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

    public DataMap deleteCollectRoom(int roomId, String phone){
        int collectUserId = userMapper.findIdByPhone(phone);
        collectRoomMapper.deleteRoomIdAndCollectUserId(roomId, collectUserId);
        return DataMap.success();
    }

    public DataMap getOrderInfo(String phone){
        int orderUserId = userMapper.findIdByPhone(phone);
        List<OrderRoomRecord> orderRoomRecords = orderRoomRecordMapper.findOrderRoomRecordByOrderUserId(orderUserId);

        List<HashMap<String, Object>> retList = new ArrayList<>();
        HashMap<String, Object> retMap;
        HouseResource houseResource;
        for (OrderRoomRecord o : orderRoomRecords) {
            retMap = new HashMap<>();
            retMap.put("orderSerial", o.getOrderSerial());
            retMap.put("orderTime", o.getOrderTime());
            if (o.getState() == 0) {
                retMap.put("orderState", "未约看");
            } else {
                retMap.put("orderState", "已约看");
            }

            String roomArea = o.getRoomArea();
            Steward steward = stewardMapper.findByManagementArea(roomArea);
            if (steward == null) {
                steward = new Steward();
            }
            retMap.put("stewardName", steward.getName());
            retMap.put("stewardPhone", steward.getPhone());

            int roomId = o.getRoomId();
            houseResource = houseResourceMapper.findHouseResourcesByRoomId(roomId);
            List<String> areaTags = StringUtil.StringToList(houseResource.getAreaTag());
            StringBuilder roomTitle = new StringBuilder();
            for (String s : areaTags) {
                roomTitle.append(s).append("·");
            }
            roomTitle.append("朝").append(houseResource.getToward()).append("·");
            if (!"整租".equals(houseResource.getHouseName())) {
                roomTitle.append(houseResource.getHouseName());
            }
            retMap.put("roomTitle", roomTitle);

            retList.add(retMap);
        }

        return DataMap.success().setData(retList);
    }

    public DataMap deleteOrder(String orderSerial){
        orderRoomRecordMapper.deleteOrderByOrderSerial(orderSerial);
        return DataMap.success();
    }

    public DataMap getHomeInfo(String phone){
        int homeUserId = userMapper.findIdByPhone(phone);

        Temperature temperature = temperatureMapper.findByHomeUserId(homeUserId);
        return DataMap.success().setData(temperature);
    }

    public DataMap getFacilityInfo(String phone){
        int userId = userMapper.findIdByPhone(phone);
        String facilityRoom = "西城区,玫瑰花城";
        //TODO 通过合同获得报修房间

        List<FacilityInfo> facilityInfoList = facilityInfoMapper.findByFacilityRoom(facilityRoom);

        List<HashMap<String, Object>> retList = new ArrayList<>();
        HashMap<String, Object> retMap;
        for(int i=0;i<facilityInfoList.size();i++){
            retMap = new HashMap<>();
            retMap.put("serialNum", i+1);
            retMap.put("facilitySerial", facilityInfoList.get(i).getFacilitySerial());
            retMap.put("facilityName", facilityInfoList.get(i).getFacilityName());
            retMap.put("repairMan", facilityInfoList.get(i).getRepairMan());
            retMap.put("orderTime", facilityInfoList.get(i).getOrderTime());
            if (facilityInfoList.get(i).getFacilityState() == 0) {
                retMap.put("facilityState", "报修中");
            } else {
                retMap.put("facilityState", "正常");
            }
            retList.add(retMap);
        }

        return DataMap.success().setData(retList);
    }

    public DataMap addFacility(HashMap hashMap, String phone){
        int userId = userMapper.findIdByPhone(phone);
        //TODO 通过合同获得报修房间
        String facilityRoom = "西城区,玫瑰花城";

        TimeUtil timeUtil = new TimeUtil();
        String facilitySerial = "rj" + timeUtil.getLongTime();

        FacilityInfo facilityInfo = JSONObject.parseObject(JSONObject.toJSONString(hashMap), FacilityInfo.class);
        facilityInfo.setFacilitySerial(facilitySerial);
        facilityInfo.setFacilityRoom(facilityRoom);

        facilityInfoMapper.save(facilityInfo);

        return DataMap.success().setData(facilitySerial);
    }

    public DataMap repairFacility(HashMap hashMap, String phone){
        int userId = userMapper.findIdByPhone(phone);
        //TODO 通过合同获得报修房间
        String facilityRoom = "西城区,玫瑰花城";

        FacilityInfo facilityInfo = JSONObject.parseObject(JSONObject.toJSONString(hashMap), FacilityInfo.class);
        facilityInfo.setFacilityRoom(facilityRoom);

        int facilityState = facilityInfoMapper.findFacilityIsRepaired(facilityInfo);

        // 已有人申请维修
        if (facilityState == 0) {
            return DataMap.success(CodeType.FACILITY_HAS_REPAIRED);
        }

        facilityInfo.setFacilityState(0);

        facilityInfoMapper.updateByFacilitySerialAndFacilityRoom(facilityInfo);

        return DataMap.success();
    }
}
