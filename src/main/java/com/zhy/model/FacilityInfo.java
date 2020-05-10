package com.zhy.model;

import lombok.Data;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 17:52
 * Describe:
 */
@Data
public class FacilityInfo {

    private int id;

    private String facilitySerial;

    private String facilityName;

    private int facilityState = 1;

    private String repairMan;

    private String orderTime;

    private String facilityRoom;

    private String phone;
}