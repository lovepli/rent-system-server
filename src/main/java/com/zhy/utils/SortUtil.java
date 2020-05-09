package com.zhy.utils;

import com.zhy.model.HouseResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 19:03
 * Describe:
 */
public class SortUtil {

    private final static String PRICE_SORT = "priceSort";
    private final static String AREA_SORT = "areaSort";
    private final static String ID_SORT = "id";
    private final static String DESC_SORT = "desc";


    public static List<HouseResource> sortHouseResource(String sortSign, String rank, List<HouseResource> list){

        if (sortSign.equals(PRICE_SORT)) {
            list.sort((o1, o2) -> {
                if(o1.getRent() >=0 || o2.getRent() >= 0){
                    //返回1，将o1拿出来跟后面的比较
                    if(o1.getRent()<0){
                        return 1;
                    }
                    if (o2.getRent()<0){
                        return -1;
                    }
                    int i = o1.getRent() - o2.getRent();
                    if (i > 0) {
                        return 1;
                    } else if (i < 0) {
                        return -1;
                    }
                    return 0;
                }
                return -1;
            });
        } else if (sortSign.equals(AREA_SORT)){
            list.sort((o1, o2) -> {
                if(o1.getBuildArea() >=0 || o2.getBuildArea() >= 0){
                    //返回1，将o1拿出来跟后面的比较
                    if(o1.getBuildArea()<0){
                        return 1;
                    }
                    if (o2.getBuildArea()<0){
                        return -1;
                    }
                    int i = o1.getBuildArea() - o2.getBuildArea();
                    if (i > 0) {
                        return 1;
                    } else if (i < 0) {
                        return -1;
                    }
                    return 0;
                }
                return -1;
            });
        } else {
            list.sort((o1, o2) -> {
                if(o1.getId() >=0 || o2.getId() >= 0){
                    //返回1，将o1拿出来跟后面的比较
                    if(o1.getId()<0){
                        return 1;
                    }
                    if (o2.getId()<0){
                        return -1;
                    }
                    int i = o1.getId() - o2.getId();
                    if (i > 0) {
                        return 1;
                    } else if (i < 0) {
                        return -1;
                    }
                    return 0;
                }
                return -1;
            });
        }

        //降序
        List<HouseResource> descSort = new ArrayList<>();
        if(sortSign.equals(ID_SORT) || rank.equals(DESC_SORT)){
            for(int i=list.size()-1;i>=0;i--){
                descSort.add(list.get(i));
            }
            list = descSort;
        }

        return list;
    }

}
