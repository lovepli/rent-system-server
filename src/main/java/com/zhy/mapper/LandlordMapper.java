package com.zhy.mapper;

import com.zhy.model.Landlord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 15:04
 * Describe:
 */
@Mapper
@Repository
public interface LandlordMapper {

    @Insert("insert into landlord(name, phone, house_city, community) values(#{name}, #{phone}, #{houseCity}, #{community})")
    int save(Landlord landlord);

    @Select("select IFNULL(max(id),0) from landlord where phone=#{phone}")
    int findIsExistByPhone(@Param("phone") String phone);

    @Update("update landlord set community=#{community} where phone=#{phone}")
    void updateLandlordByPhone(@Param("community") String community, @Param("phone") String phone);

    @Select("select community from landlord where phone=#{phone}")
    String findCommunityByPhone(@Param("phone") String phone);

    @Select("select * from landlord where phone=#{phone}")
    @Results({@Result(property = "houseCity", column = "house_city")
    })
    List<Landlord> findLandlordByPhone(@Param("phone") String phone);
}
