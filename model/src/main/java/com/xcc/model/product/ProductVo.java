package com.xcc.model.product;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ProductVo {
    private Integer id;
    private String orderCode;
    private String modifyReason;
    private String productName;                     //产品名称
    private String productCode;                     //产品编码，搜索时最好用这个来接收前端传回的产品名称数据，这样可以更精确的定位产品
    private Integer onlyExport;                     //仅供出口（0：有内销；1：仅出口；2：不确定）
    private String executiveStandard;               //产品执行标准
    private String filingNumber;                    //产品备案编号
    private String token;                           //前端传回的token
    private String searchModel;                     //前端传回的搜索模式（["所有产品","仅出口","有内销","不确定"]）
    private String tableName;                       //前端传回的页面名称（用于关联文件类别）
    private String compositionStatus;               //前端传回的组件状态（["所有","缺少组件","已经有组件"]
    private String standardName;                    //前端传回的用于搜索的标准名称，也可以接收标准编码
    private String standardClass;
}
