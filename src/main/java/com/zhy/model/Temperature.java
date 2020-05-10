package com.zhy.model;

import lombok.Data;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 17:05
 * Describe:
 */
@Data
public class Temperature {

    private int id;

    private int temperature;

    private int humidity;

    private int roomId;

    private int homeUserId;

}
