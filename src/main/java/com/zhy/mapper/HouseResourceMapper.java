package com.zhy.mapper;

import com.zhy.model.HouseResource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 18:46
 * Describe:
 */
@Mapper
@Repository
public interface HouseResourceMapper {

    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, statementType = StatementType.STATEMENT, resultType = int.class)
    @Insert("insert into house_resource(house_name, rent, build_area, toward, door_model, location, floor, lift, era, allocation, area_tag, facility, room_pic, landlord_id) " +
            "values(#{houseName}, #{rent}, #{buildArea}, #{toward}, #{doorModel}, #{location}, #{floor}, #{lift}, #{era}, #{allocation}, #{areaTag}, #{facility}, #{roomPic}, #{landlordId})")
    int save(HouseResource houseResource);

    @Select("select * from house_resource where landlord_id=#{landlordId}")
    @Results({@Result(property = "houseName", column = "house_name"),
            @Result(property = "buildArea", column = "build_area"),
            @Result(property = "areaTag", column = "area_tag"),
            @Result(property = "roomPic", column = "room_pic"),
            @Result(property = "landlordId", column = "landlord_id"),
            @Result(property = "doorModel", column = "door_model")
    })
    List<HouseResource> findHouseResourcesByLandlordId(int landlordId);
}
