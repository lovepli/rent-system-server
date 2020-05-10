package com.zhy.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangocean
 * @Date: 2020/5/10 12:53
 * Describe:
 */
@Data
@NoArgsConstructor
public class CollectRoom {

    private int id;

    private int roomId;

    private int collectUserId;

}
