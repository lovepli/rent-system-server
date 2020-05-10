package com.zhy.mapper;

import com.zhy.model.CollectRoom;
import com.zhy.model.Landlord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 12:57
 * Describe:
 */
@Mapper
@Repository
public interface CollectRoomMapper {

    @Insert("insert into collect_room(room_id, collect_user_id) values(#{roomId}, #{collectUserId})")
    int save(CollectRoom collectRoom);

    @Select("select IFNULL(max(id),0) from collect_room where room_id=#{roomId} and collect_user_id=#{collectUserId}")
    int findIsExistByPhone(int roomId, int collectUserId);

    @Delete("delete from collect_room where room_id=#{roomId} and collect_user_id=#{collectUserId}")
    void deleteRoomIdAndCollectUserId(int roomId, int collectUserId);

    @Select("select room_id from collect_room where collect_user_id=#{collectUserId}")
    List<Integer> findRoomIdByCollectUserId(int collectUserId);
}
