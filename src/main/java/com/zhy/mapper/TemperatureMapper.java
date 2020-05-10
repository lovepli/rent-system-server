package com.zhy.mapper;

import com.zhy.model.Temperature;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 17:08
 * Describe:
 */
@Mapper
@Repository
public interface TemperatureMapper {

    @Insert("insert into temperature(temperature, humidity, room_id, home_user_id) values(#{temperature}, #{humidity}, #{roomId}, #{homeUserId})")
    void save(Temperature temperature);

    @Select("select * from temperature where home_user_id=#{homeUserId}")
    @Results(id = "temperatureMap", value = {
            @Result(property = "roomId", column = "room_id"),
            @Result(property = "homeUserId", column = "home_user_id")
    })
    Temperature findByHomeUserId(int homeUserId);

}
