package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 14:48
 * Describe:
 */
@Data
@NoArgsConstructor
public class Community {

    private int id;

    private String communityName;

    private String houseCity;

    private int buildingAge;

    private String buildingType;

    private int heatingMethod;

    private double greeningRate;

    private double plotRatio;

    private String propertyCompany;

    private String propertyPhone;


    public Community(String communityName, String houseCity, int buildingAge, String buildingType, int heatingMethod, double greeningRate, double plotRatio, String propertyCompany, String propertyPhone) {
        this.communityName = communityName;
        this.houseCity = houseCity;
        this.buildingAge = buildingAge;
        this.buildingType = buildingType;
        this.heatingMethod = heatingMethod;
        this.greeningRate = greeningRate;
        this.plotRatio = plotRatio;
        this.propertyCompany = propertyCompany;
        this.propertyPhone = propertyPhone;
    }
}
