package com.xcc.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Material {

    private Integer id;

    private String materialCode;

    private Integer status;

    private String standardName;

    private List<StandardData> standardData;

    private String materialName;

    private String productName;

    private String producterName;

    private String submitCode;

    private String inventoryClass;

    private String updateReason;

    private Integer versionNo;

    private String materialModel;

    private String materialSpecifications;

    private String remark;

    private List<MaterialIngredient> materialIngredients;

    private Integer standardConfirm;

    //用于给页面的合并后的每一行做序号标记
    private Integer itemIndex;

    //用于确认原料是否处于已确认状态
    private Integer isConfirm;

    //原料成分百分比合计
    private BigDecimal sumContent;


}
