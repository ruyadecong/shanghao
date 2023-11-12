package com.xcc.model.qc;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ExcelIngredient {

    @ExcelProperty("序号")
    private String id;
    @ExcelProperty("供应商编码")
    private String supplierCode;
    @ExcelProperty("原料编码")
    private String ingredientCode;
    @ExcelProperty("供应商名称")
    private String supplierName;
    @ExcelProperty("原料名称")
    private String ingredientName;
    @ExcelProperty("原料批号")
    private String batch;
    @ExcelProperty("录入量，kg")
    private double kg;
    @ExcelProperty("报检/入库日期")
    private Date inDate;
    @ExcelProperty("生产日期")
    private Date productDate;
    @ExcelProperty("有效期至")
    private Date expireDate;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("产家")
    private String production;
    @ExcelProperty("检验依据")
    private String testStandard;
    @ExcelProperty("报告日期")
    private Date reportDate;


}
