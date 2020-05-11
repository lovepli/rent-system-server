package com.zhy.mapper;

import com.zhy.model.Contract;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/11 12:16
 * Describe:
 */
@Mapper
@Repository
public interface ContractMapper {

    @Insert("insert into contract(contract_serial, contract_name, pay_way, rent, pay_time, contract_renew_time, room_id, user_id, contract_state) " +
            "values(#{contractSerial}, #{contractName}, #{payWay}, #{rent}, #{payTime}, #{contractRenewTime}, #{roomId}, #{userId}, #{contractState})")
    void save(Contract contract);

    @Select("select * from contract where user_id=#{userId} order by id desc")
    @Results(id = "contractMap", value = {
            @Result(property = "contractSerial", column = "contract_serial"),
            @Result(property = "contractName", column = "contract_name"),
            @Result(property = "payWay", column = "pay_way"),
            @Result(property = "payTime", column = "pay_time"),
            @Result(property = "contractRenewTime", column = "contract_renew_time"),
            @Result(property = "roomId", column = "room_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "contractState", column = "contract_state")
    })
    List<Contract> findByUserId(int userId);

    @Select("select IFNULL(max(id),0) from contract where user_id=#{userId} and contract_state=#{contractState}")
    int findContractNotOverdueByUserIdAndContractState(int userId, int contractState);

    @Select("select IFNULL(max(room_id),0) from contract where user_id=#{userId} and contract_state=#{contractState}")
    int findRoomIdByUserIdAndContractState(int userId, int contractState);
}
