package com.xcc.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientVo {
    private String materialCode;
    private String materialName;
    private String cname;
    private String inciName;
    private BigDecimal ingredientContent;
    private String remark;

}
