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

    private String orderSerial;

    private String orderTime;

    private int roomId;

    private int orderUserId;

    private String roomArea;

    private int state = 0;
}
