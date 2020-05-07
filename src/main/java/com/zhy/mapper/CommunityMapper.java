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

    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, statementType = StatementType.STATEMENT, resultType = int.class)
    @Insert("insert into community(community_name, house_city, building_age, building_type, heating_method, greening_rate, plot_ratio, property_company, property_phone) " +
            "values(#{communityName}, #{houseCity}, #{buildingAge}, #{buildingType}, #{heatingMethod}, #{greeningRate}, #{plotRatio}, #{propertyCompany}, #{propertyPhone})")
    int save(Community community);

    @Select("select IFNULL(max(id),0) from community where community_name=#{communityName} and house_city=#{houseCity}")
    int findIsExistByCommunityNameAndHouseCity(@Param("communityName") String communityName, @Param("houseCity") String houseCity);
}
