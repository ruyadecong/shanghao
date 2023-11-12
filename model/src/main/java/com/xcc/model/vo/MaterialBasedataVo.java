package com.xcc.model.vo;

import lombok.Data;

@Data
public class MaterialBasedataVo {
    private String materialCode;

    private String oldCode;

    private String producterName;

    private String tableName;

    private String token;

    //用于接收前端传回来的原料搜索模式：["所有原料","无成分数据的原料","无报送码的原料"]
    private String searchModel;

    private String inspectSerialNumber;

    private String ingredient;

    private String materialName;


}
