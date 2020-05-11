package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 17:05
 * Describe:
 */
@Data
@NoArgsConstructor
public class Temperature {

    private int id;

    private int temperature;

    private int humidity;

    private int roomId;

    private int homeUserId;

    public Temperature(int temperature, int humidity, int roomId) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.roomId = roomId;
    }
}
