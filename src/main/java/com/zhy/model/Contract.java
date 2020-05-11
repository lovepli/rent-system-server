package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.LongFunction;

/**
 * @author: zhangocean
 * @Date: 2020/5/11 11:36
 * Describe:
 */
@Data
@NoArgsConstructor
public class Contract {

    private int id;

    private String contractSerial;

    private String contractName;

    private int payWay;

    private int rent;

    private String payTime;

    private long contractRenewTime;

    private int roomId;

    private int userId;

    //合同签约中
    private int contractState = 0;

    public Contract(String contractSerial, String contractName, int payWay, int rent, String payTime, long contractRenewTime, int roomId, int userId) {
        this.contractSerial = contractSerial;
        this.contractName = contractName;
        this.payWay = payWay;
        this.rent = rent;
        this.payTime = payTime;
        this.contractRenewTime = contractRenewTime;
        this.roomId = roomId;
        this.userId = userId;
    }
}
