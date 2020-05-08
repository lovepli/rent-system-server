package com.zhy.mapper;

import com.zhy.model.Community;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 14:53
 * Describe:
 */
@Mapper
@Repository
public interface CommunityMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into community(community_name, house_city, building_age, building_type, heating_method, greening_rate, plot_ratio, property_company, property_phone) " +
            "values(#{communityName}, #{houseCity}, #{buildingAge}, #{buildingType}, #{heatingMethod}, #{greeningRate}, #{plotRatio}, #{propertyCompany}, #{propertyPhone})")
    void save(Community community);

    @Select("select IFNULL(max(id),0) from community where community_name=#{communityName} and house_city=#{houseCity}")
    int findIsExistByCommunityNameAndHouseCity(@Param("communityName") String communityName, @Param("houseCity") String houseCity);

    @Select("select * from community where id=#{id}")
    @Results(id = "communityMap", value = {
            @Result(property = "communityName", column = "community_name"),
            @Result(property = "houseCity", column = "house_city"),
            @Result(property = "buildingAge", column = "building_age"),
            @Result(property = "buildingType", column = "building_type"),
            @Result(property = "heatingMethod", column = "heating_method"),
            @Result(property = "greeningRate", column = "greening_rate"),
            @Result(property = "plotRatio", column = "plot_ratio"),
            @Result(property = "propertyCompany", column = "property_company"),
            @Result(property = "propertyPhone", column = "property_phone")
    })
    Community findCommunityById(int id);

    @Select("select house_city from community where id=#{id}")
    String findHouseCityById(int id);
}
