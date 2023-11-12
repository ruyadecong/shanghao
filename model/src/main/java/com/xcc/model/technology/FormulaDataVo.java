package com.xcc.model.technology;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FormulaDataVo {

    private Integer id;

    private String formulaCode;

    /**
     * 类型标记，默认为0（产品配方），1为半成品配方，2为原料
     * 0有时也表示无此原料
     */
    private Integer classMark;

    private String materialCode;

    private BigDecimal materialContent;

    private String materialPurpose;

    private String remark;

    private Integer rankNumber;

    private String formulaType;         //接收前端工艺列表页面提交的配方类型

    private String craftStatus;         //接收前端工艺列表页面提交的配方状态

    private String productCode;         //接收前端工艺列表页面提交的产品编码


}
