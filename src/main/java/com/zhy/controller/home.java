package com.zhy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhangocean
 * @Date: 2020/3/29 13:46
 * Describe:
 */
@RestController
public class home {

    @GetMapping("/")
    public String pageHome(){
        return "home";
    }

}
