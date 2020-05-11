package com.zhy.mapper;

import com.zhy.model.BillRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/11 13:54
 * Describe:
 */
@Mapper
@Repository
public interface BillRecordMapper {

    @Insert("insert into bill_record(bill_serial, contract_serial, bill_date, consume_project, consume_money, payment_state) " +
            "values(#{billSerial}, #{contractSerial}, #{billDate}, #{consumeProject}, #{consumeMoney}, #{paymentState})")
    void save(BillRecord billRecord);

    @Select("select * from bill_record where contract_serial=#{contractSerial}")
    @Results(id = "billRecordMap", value = {
            @Result(property = "billSerial", column = "bill_serial"),
            @Result(property = "contractSerial", column = "contract_serial"),
            @Result(property = "billDate", column = "bill_date"),
            @Result(property = "consumeProject", column = "consume_project"),
            @Result(property = "consumeMoney", column = "consume_money"),
            @Result(property = "paymentState", column = "payment_state")
    })
    List<BillRecord> findByContractSerial(String contractSerial);

    @Select("select consume_money,payment_state from bill_record where bill_serial=#{billSerial}")
    @ResultMap("billRecordMap")
    BillRecord findConsumeMoneyByBillSerial(String billSerial);

    @Update("update bill_record set payment_state=#{paymentState} where bill_serial=#{billSerial}")
    void updatePaymentStateByBillSerial(String billSerial, int paymentState);

}
