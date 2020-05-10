package com.zhy.mapper;

import com.zhy.model.Steward;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 21:00
 * Describe:
 */
@Mapper
@Repository
public interface StewardMapper {

    @Insert("insert into steward(name, phone, management_area, portrait, city) values(#{name}, #{phone}, #{ManagementArea}, #{portrait}, #{city})")
    void save(Steward steward);

    @Select("select * from steward where management_area=#{area}")
    @Results(id = "stewardMap", value = {
            @Result(property = "managementArea", column = "management_area")
    })
    Steward findByManagementArea(String area);
}
