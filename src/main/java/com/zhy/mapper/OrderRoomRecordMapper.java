package com.zhy.mapper;

import com.zhy.model.OrderRoomRecord;
import com.zhy.model.Steward;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:42
 * Describe:
 */
@Mapper
@Repository
public interface OrderRoomRecordMapper {

    @Insert("insert into order_room_record(order_serial, order_time, room_id, order_user_id, room_area, state) values(#{orderSerial}, #{orderTime}, #{roomId}, #{orderUserId}, #{roomArea}, #{state})")
    void save(OrderRoomRecord orderRoomRecord);

    @Select("select IFNULL(max(id),0) from order_room_record where room_id=#{roomId} and order_user_id=#{orderUserId}")
    int findIsExistByRoomIdAndOrderUserId(int roomId, int orderUserId);

    @Select("select * from order_room_record where order_user_id=#{orderUserId}")
    @Results(id = "orderRoomRecordMap", value = {
            @Result(property = "orderSerial", column = "order_serial"),
            @Result(property = "orderTime", column = "order_time"),
            @Result(property = "roomId", column = "room_id"),
            @Result(property = "orderUserId", column = "order_user_id"),
            @Result(property = "roomArea", column = "room_area")
    })
    List<OrderRoomRecord> findOrderRoomRecordByOrderUserId(int orderUserId);

    @Delete("delete from order_room_record where order_serial=#{orderSerial}")
    void deleteOrderByOrderSerial(String orderSerial);

    @Select("select room_id,order_user_id from order_room_record where order_serial=#{orderSerial}")
    @ResultMap("orderRoomRecordMap")
    OrderRoomRecord findByOrderSerial(String orderSerial);
}
