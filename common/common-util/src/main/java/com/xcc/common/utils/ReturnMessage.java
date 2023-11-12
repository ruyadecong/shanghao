package com.xcc.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ReturnMessage {

    public static Map<String,String> message(String code,String msg){
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        map.put("msg",msg);     //这个msg是要在前端根据code显示出来的
        return map;
    }
}
