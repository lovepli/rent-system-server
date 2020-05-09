package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 20:54
 * Describe:
 */
@Data
@NoArgsConstructor
public class Steward {

    private int id;

    private String name;

    private String phone;

    private String managementArea;

    private String portrait;

}
