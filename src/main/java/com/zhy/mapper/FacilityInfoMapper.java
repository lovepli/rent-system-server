package com.zhy.mapper;

import com.zhy.model.FacilityInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 17:58
 * Describe:
 */
@Mapper
@Repository
public interface FacilityInfoMapper {

    @Insert("insert into facility_info(facility_serial, facility_name, facility_state, facility_room) values(#{facilitySerial}, #{facilityName}, #{facilityState}, #{facilityRoom})")
    int save(FacilityInfo facilityInfo);

    @Select("select * from facility_info where facility_room=#{facilityRoom}")
    @Results(id = "facilityInfoMap", value = {
            @Result(property = "facilitySerial", column = "facility_serial"),
            @Result(property = "facilityName", column = "facility_name"),
            @Result(property = "facilityState", column = "facility_state"),
            @Result(property = "facilityRoom", column = "facility_room"),
            @Result(property = "repairMan", column = "repair_man"),
            @Result(property = "orderTime", column = "order_time")
    })
    List<FacilityInfo> findByFacilityRoom(String facilityRoom);

    @Update("update facility_info set " +
            "facility_state=#{facilityState},repair_man=#{repairMan},order_time=#{orderTime},phone=#{phone} " +
            "where facility_serial=#{facilitySerial} and facility_room=#{facilityRoom}")
    void updateByFacilitySerialAndFacilityRoom(FacilityInfo facilityInfo);

    @Select("select facility_state from facility_info where facility_serial=#{facilitySerial} and facility_room=#{facilityRoom}")
    int findFacilityIsRepaired(FacilityInfo facilityInfo);
}
