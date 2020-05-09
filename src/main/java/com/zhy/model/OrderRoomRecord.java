package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:37
 * Describe:
 */
@Data
@NoArgsConstructor
public class OrderRoomRecord {

    private int id;

    private String orderTime;

    private String orderPhone;

    private int roomId;

    private int orderUserId;

    private String roomArea;

    private int state = 0;

    public OrderRoomRecord(String orderTime, String orderPhone, int roomId, int orderUserId, String roomArea) {
        this.orderTime = orderTime;
        this.orderPhone = orderPhone;
        this.roomId = roomId;
        this.orderUserId = orderUserId;
        this.roomArea = roomArea;
    }
}
