package com.zhy.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2019/11/18 15:42
 * Describe: 字符常量
 */
public class StringUtil {

    public static final String BLANK = "";

    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * 字符串转换成字符串数组
     * @param str 字符串
     * @return 转换后的字符串数组
     */
    public static List<String> StringToList(String str){
        String[] array = str.split(",");
        List<String> list = new ArrayList<>();
        for(int i=0;i<array.length;i++){
            list.add(array[i].trim());
        }
        return list;
    }

    /**
     * 字符串数组拼接成字符串
     * @param args 字符串数组
     * @return 拼接后的字符串
     */
    public static String listToString(List<String> args){
        StringBuilder sb = new StringBuilder();
        for(String arg : args){
            if(sb.length() == 0){
                sb.append(arg.trim());
            } else {
                sb.append(",").append(arg.trim());
            }
        }
        return sb.toString();
    }

}
