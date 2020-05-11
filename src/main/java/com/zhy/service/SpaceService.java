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

import java.math.BigDecimal;
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
    @Autowired
    private ContractMapper contractMapper;
    @Autowired
    private BillRecordMapper billRecordMapper;

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

        Temperature temperature = new Temperature(0, 0, houseResource.getId());
        temperatureMapper.save(temperature);

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

    public DataMap getHomeInfo(int userId){

        Temperature temperature = temperatureMapper.findByHomeUserId(userId);
        if (temperature == null) {
            return DataMap.fail(CodeType.NOT_RENT_ROOM);
        }
        return DataMap.success().setData(temperature);
    }

    public DataMap getFacilityInfo(int userId){
        int roomId = contractMapper.findRoomIdByUserIdAndContractState(userId, 0);
        List<HashMap<String, Object>> retList = new ArrayList<>();
        if (roomId == 0) {
            return DataMap.fail(CodeType.NOT_RENT_ROOM);
        }
        String facilityRoom = houseResourceMapper.findAreaTagByRoomId(roomId);

        List<FacilityInfo> facilityInfoList = facilityInfoMapper.findByFacilityRoom(facilityRoom);

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

    public DataMap addFacility(HashMap hashMap, int userId){
        int roomId = contractMapper.findRoomIdByUserIdAndContractState(userId, 0);
        String facilityRoom = houseResourceMapper.findAreaTagByRoomId(roomId);

        TimeUtil timeUtil = new TimeUtil();
        String facilitySerial = "rj" + timeUtil.getLongTime();

        FacilityInfo facilityInfo = JSONObject.parseObject(JSONObject.toJSONString(hashMap), FacilityInfo.class);
        facilityInfo.setFacilitySerial(facilitySerial);
        facilityInfo.setFacilityRoom(facilityRoom);

        facilityInfoMapper.save(facilityInfo);

        return DataMap.success().setData(facilitySerial);
    }

    public DataMap repairFacility(HashMap hashMap, int userId){
        int roomId = contractMapper.findRoomIdByUserIdAndContractState(userId, 0);
        String facilityRoom = houseResourceMapper.findAreaTagByRoomId(roomId);

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

    public DataMap payOrderClick(HashMap hashMap){
        String orderSerial = (String) hashMap.get("orderSerial");
        int payWay = Integer.parseInt(hashMap.get("payWay").toString());

        OrderRoomRecord orr = orderRoomRecordMapper.findByOrderSerial(orderSerial);

        int roomId = orr.getRoomId();
        int orderUserId = orr.getOrderUserId();

        int contractNotOutDue = contractMapper.findContractNotOverdueByUserIdAndContractState(orderUserId, 0);

        if (contractNotOutDue != 0) {
            return DataMap.fail(CodeType.CONTRACT_NOT_OUT_DUE);
        }

        HouseResource houseResource = houseResourceMapper.findHouseResourcesByRoomId(roomId);

        int rent = houseResource.getRent();
        int consumeMoney = rent;

        TimeUtil timeUtil = new TimeUtil();
        long contractRenewTime = timeUtil.getLongTime();

        if (payWay == 2) {
            consumeMoney *= 3;
            contractRenewTime += StringUtil.ONT_MONTH_SECONDS * 3;
        } else if (payWay == 3) {
            consumeMoney *= 6;
            contractRenewTime += StringUtil.ONT_MONTH_SECONDS * 6;
        } else if (payWay == 4) {
            consumeMoney *= 12;
            contractRenewTime += StringUtil.ONT_MONTH_SECONDS *12;
        } else {
            contractRenewTime += StringUtil.ONT_MONTH_SECONDS;
        }

        List<String> areaTags = StringUtil.StringToList(houseResource.getAreaTag());
        String city = "(" + houseResource.getHouseCity() + ") -- ";
        StringBuilder contractName = new StringBuilder(city);
        for(String s : areaTags){
            contractName.append(s).append(" ");
        }
        contractName.append("朝").append(houseResource.getToward()).append(" ");
        if (!"整租".equals(houseResource.getHouseName())) {
            contractName.append(houseResource.getHouseName());
        }

        String payTime = timeUtil.getFormatDateForThree();

        Contract contract = new Contract(orderSerial,contractName.toString(),payWay,rent,payTime,contractRenewTime,roomId,orderUserId);

        contractMapper.save(contract);

        houseResourceMapper.updateRentStateByRoomId(roomId, 1);

        orderRoomRecordMapper.deleteOrderByOrderSerial(orderSerial);

        BillRecord billRecord = new BillRecord();
        billRecord.setBillSerial("ZD" + timeUtil.getLongTime());
        billRecord.setContractSerial(orderSerial);
        BigDecimal bigDecimal = new BigDecimal(consumeMoney);
        billRecord.setConsumeMoney(bigDecimal);
        billRecord.setPaymentState(1);
        String time = timeUtil.getFormatDateForThree();
        String billDate = time;
        billRecord.setBillDate(billDate);
        billRecord.setConsumeProject("房租缴费"+time.replace("-",""));
        billRecordMapper.save(billRecord);

        temperatureMapper.updateHomeUserIdByRoomId(orderUserId, roomId);

        return DataMap.success();
    }

    public DataMap getContractInfo(int userId){

        List<Contract> contracts = contractMapper.findByUserId(userId);

        List<HashMap<String, Object>> retList = new ArrayList<>();
        HashMap<String, Object> retMap;

        int count = 1;
        for(Contract contract : contracts){
            retMap = new HashMap<>();

            retMap.put("contractName", "合同" + count + contract.getContractName());
            retMap.put("contractSerial", contract.getContractSerial());
            if (contract.getPayWay() == 1) {
                retMap.put("payWay", "月付");
            } else if (contract.getPayWay() == 2) {
                retMap.put("payWay", "季付");
            } else if (contract.getPayWay() == 3) {
                retMap.put("payWay", "半年付");
            } else {
                retMap.put("payWay", "年付");
            }
            retMap.put("rent", contract.getRent());
            retMap.put("contractState", contract.getContractState());

            List<BillRecord> billRecords;
            List<HashMap<String, Object>> billRecordList = new ArrayList<>();
            billRecords = billRecordMapper.findByContractSerial(contract.getContractSerial());
            HashMap<String, Object> billRecordMap;
            int billCount = 1;
            for (BillRecord billRecord : billRecords) {
                billRecordMap = new HashMap<>();
                billRecordMap.put("serialNumber", billCount);
                billRecordMap.put("billSerial", billRecord.getBillSerial());
                billRecordMap.put("billDate", billRecord.getBillDate());
                billRecordMap.put("consumeProject", billRecord.getConsumeProject());
                billRecordMap.put("consumeMoney", billRecord.getConsumeMoney()+"元");
                if (billRecord.getPaymentState() == 0) {
                    billRecordMap.put("paymentState", "未支付");
                } else {
                    billRecordMap.put("paymentState", "已支付");
                }
                billCount++;

                billRecordList.add(billRecordMap);
            }

            retMap.put("billRecords", billRecordList);

            count++;

            retList.add(retMap);
        }

        return DataMap.success().setData(retList);
    }

    public DataMap payBill(HashMap hashMap){
        String billSerial = (String) hashMap.get("billSerial");

        BillRecord billRecord = billRecordMapper.findConsumeMoneyByBillSerial(billSerial);
        if (billRecord.getPaymentState() == 1) {
            return DataMap.fail(CodeType.BILL_HAS_PAY);
        }
        BigDecimal consumeMoney = billRecord.getConsumeMoney();
        billRecordMapper.updatePaymentStateByBillSerial(billSerial, 1);

        return DataMap.success();
    }
}
