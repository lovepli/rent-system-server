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

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into house_resource(house_name, rent, build_area, toward, door_model, location, floor, lift, era, allocation, area_tag, facility, room_pic, landlord_id, rent_state, house_city, house_brief, subway) " +
            "values(#{houseName}, #{rent}, #{buildArea}, #{toward}, #{doorModel}, #{location}, #{floor}, #{lift}, #{era}, #{allocation}, #{areaTag}, #{facility}, #{roomPic}, #{landlordId}, #{rentState}, #{houseCity}, #{houseBrief}, #{subway})")
    void save(HouseResource houseResource);

    @Select("select * from house_resource where landlord_id=#{landlordId}")
    @Results(id = "houseResourceMap", value = {
            @Result(property = "houseName", column = "house_name"),
            @Result(property = "buildArea", column = "build_area"),
            @Result(property = "areaTag", column = "area_tag"),
            @Result(property = "roomPic", column = "room_pic"),
            @Result(property = "landlordId", column = "landlord_id"),
            @Result(property = "doorModel", column = "door_model"),
            @Result(property = "rentState", column = "rent_state"),
            @Result(property = "houseCity", column = "house_city"),
            @Result(property = "houseBrief", column = "house_brief")
    })
    List<HouseResource> findHouseResourcesByLandlordId(int landlordId);

    @Select("select * from house_resource where id=#{roomId}")
    @ResultMap("houseResourceMap")
    HouseResource findHouseResourcesByRoomId(int roomId);

    @Select("select id,house_name,rent_state,build_area,rent,facility from house_resource where area_tag=#{areaTag}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findHouseResourcesByAreaTag(String areaTag);

    @Select("select id,house_name,rent,build_area,floor,location,room_pic,area_tag,door_model,toward from house_resource where house_city=#{city} and rent_state=#{rentState} order by ${sortCol} ${sortSign}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findHouseResourcesByCityAndRentStateAndSort(String city, int rentState, String sortCol, String sortSign);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and area_tag like concat('%' ,#{searchText}, '%')")
    @ResultMap("houseResourceMap")
    List<HouseResource> findLikeAreaTagBySearchTextAndCity(String searchText, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and subway like concat('%' ,#{searchText}, '%')")
    @ResultMap("houseResourceMap")
    List<HouseResource> findLikeSubwayBySearchTextAndCity(String searchText, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and facility like concat('%' ,#{searchText}, '%')")
    @ResultMap("houseResourceMap")
    List<HouseResource> findLikeFacilityBySearchTextAndCity(String searchText, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and rent >= #{lowPrice} and rent <= #{highPrice}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findByLowAndHighPriceAndCity(int lowPrice, int highPrice, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and door_model=#{doorModel}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findByDoorModelAndCity(String doorModel, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and house_name<>#{roomType}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findByRoomTypeAndCity(String roomType, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and house_name=#{roomType}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findByEntireRoomTypeAndCity(String roomType, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and lift=#{lift}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findByLiftTypeAndCity(int lift, int rentState, String city);

    @Select("select * from house_resource where house_city=#{city} and rent_state=#{rentState} and subway<>#{subway}")
    @ResultMap("houseResourceMap")
    List<HouseResource> findIsNearSubwayByCity(String subway, int rentState, String city);
}
