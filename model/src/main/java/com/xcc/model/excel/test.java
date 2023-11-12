package com.xcc.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class test {

    @ExcelProperty("id")
    private Integer id;
    @ExcelProperty("旧编码")
    private String oldCode;
    @ExcelProperty("新编码")
    private String code;
    @ExcelProperty("原料名称")
    private String ingredientName;
    @ExcelProperty("产品名称")
    private String productName;
    @ExcelProperty("厂家名称")
    private String producterName;
    @ExcelProperty("报送码")
    private String submitCode;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("状态")
    private Boolean status;
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;
    @ExcelProperty("是否删除")
    private LocalDateTime isDeleted;


}
