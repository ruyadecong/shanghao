package com.xcc.model.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InPutFormula {

    @ExcelProperty("序号")
    private Integer rankNumber;

    @ExcelProperty("原料编码")
    private String materialCode;

    @ExcelProperty("使用目的")
    private BigDecimal materialPurpose;

    @ExcelProperty("成分比例")
    private String materialContent;

}
