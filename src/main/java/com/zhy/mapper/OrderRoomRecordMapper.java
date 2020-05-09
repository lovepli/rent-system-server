package com.zhy.mapper;

import com.zhy.model.OrderRoomRecord;
import com.zhy.model.Steward;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:42
 * Describe:
 */
@Mapper
@Repository
public interface OrderRoomRecordMapper {

    @Insert("insert into order_room_record(order_time, order_phone, room_id, order_user_id, room_area, state) values(#{orderTime}, #{orderPhone}, #{roomId}, #{orderUserId}, #{roomArea}, #{state})")
    void save(OrderRoomRecord orderRoomRecord);

    @Select("select IFNULL(max(id),0) from order_room_record where room_id=#{roomId} and order_user_id=#{orderUserId}")
    int findIsExistByPhone(int roomId, int orderUserId);
}
