package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 17:05
 * Describe:
 */
@Data
@NoArgsConstructor
public class HouseResource {

    private int id;

    private int landlordId;

    private String houseName;

    private int rent;

    private int buildArea;

    private String toward;

    private String doorModel;

    private String location;

    private String floor;

    private int lift;

    private int era;

    private String allocation;

    private String areaTag;

    private String facility;

    private String roomPic;

    private int rentState = 0;

    private String houseCity;

    private String houseBrief;

    private String subway = "";
}
