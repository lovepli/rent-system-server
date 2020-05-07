package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/7 14:35
 * Describe:
 */
@Data
@NoArgsConstructor
public class Landlord {

    private int id;

    private String name;

    private String phone;

    private String houseCity;

    private String community;

    public Landlord(String name, String phone, String houseCity) {
        this.name = name;
        this.phone = phone;
        this.houseCity = houseCity;
    }
}
