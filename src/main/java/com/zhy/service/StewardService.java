package com.zhy.service;

import com.zhy.aspect.PrincipalAspect;
import com.zhy.mapper.StewardMapper;
import com.zhy.model.Steward;
import com.zhy.model.User;
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

    public DataMap getStewardInfo(HashMap hashMap, Object obj){

        String managementArea = (String) hashMap.get("managementArea");

        Steward steward = stewardMapper.findByManagementArea(managementArea);

        String userPhone = "";
        if (!obj.equals(PrincipalAspect.ANONYMOUS_USER)) {
            User user = (User) obj;
            userPhone = user.getPhone();
        }

        if(steward == null) {
            steward = new Steward();
        }

        HashMap<String, Object> retMap = new HashMap<>(2);
        retMap.put("userPhone", userPhone);
        retMap.put("steward", steward);

        return DataMap.success().setData(retMap);
    }

}
