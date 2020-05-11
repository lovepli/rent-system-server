package com.zhy.service;

import com.alibaba.fastjson.JSONObject;
import com.zhy.constant.CodeType;
import com.zhy.mapper.HouseResourceMapper;
import com.zhy.mapper.OrderRoomRecordMapper;
import com.zhy.mapper.UserMapper;
import com.zhy.model.HouseResource;
import com.zhy.model.OrderRoomRecord;
import com.zhy.utils.DataMap;
import com.zhy.utils.StringUtil;
import com.zhy.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:41
 * Describe:
 */
@Service
public class OrderRoomRecordService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderRoomRecordMapper orderRoomRecordMapper;
    @Autowired
    private HouseResourceMapper houseResourceMapper;

    public DataMap orderRoom(HashMap hashMap, String phone){

        OrderRoomRecord orderRoomRecord = JSONObject.parseObject(JSONObject.toJSONString(hashMap), OrderRoomRecord.class);

        HouseResource houseResource = houseResourceMapper.findHouseResourcesByRoomId(orderRoomRecord.getRoomId());

        int rentState = houseResource.getRentState();
        if(rentState == 1){
            return DataMap.fail(CodeType.ROOM_HAS_RENT);
        }

        int orderUserId = userMapper.findIdByPhone(phone);

        orderRoomRecord.setOrderUserId(orderUserId);
        int isExist = orderRoomRecordMapper.findIsExistByRoomIdAndOrderUserId(orderRoomRecord.getRoomId(), orderUserId);

        if (isExist == 0) {
            String roomArea = houseResource.getAreaTag();
            List<String> roomAreas = StringUtil.StringToList(roomArea);
            orderRoomRecord.setRoomArea(roomAreas.get(0));

            TimeUtil timeUtil = new TimeUtil();
            String orderSerial = "rj" + timeUtil.getLongTime();
            orderRoomRecord.setOrderSerial(orderSerial);

            orderRoomRecordMapper.save(orderRoomRecord);
        } else {
            return DataMap.fail(CodeType.ORDER_ROOM_EXIST);
        }

        return DataMap.success();
    }

}
