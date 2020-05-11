package com.zhy.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: zhangocean
 * @Date: 2020/5/11 13:45
 * Describe:
 */
@Data
public class BillRecord {

    private int id;
    private String billSerial;
    private String contractSerial;
    private String billDate;
    private String consumeProject;
    private BigDecimal consumeMoney;

    //未支付
    private int paymentState = 0;

}
