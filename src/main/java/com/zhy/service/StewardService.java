package com.zhy.service;

import com.zhy.mapper.StewardMapper;
import com.zhy.model.Steward;
import com.zhy.utils.DataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2020/5/9 22:19
 * Describe:
 */
@Service
public class StewardService {

    @Autowired
    private StewardMapper stewardMapper;

    public DataMap getStewardInfo(HashMap hashMap){

        String managementArea = (String) hashMap.get("managementArea");

        Steward steward = stewardMapper.findByManagementArea(managementArea);

        if(steward == null) {
            steward = new Steward();
        }
        return DataMap.success().setData(steward);
    }

}
