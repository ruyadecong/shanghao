package com.xcc.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialPurchaseVo {
    private String materialCode;
    private String supplierCode;
    private String purchaseDataBegin;
    private String purchaseDataEnd;
    private String paymentMethod;
    private String orderCode;
    private String materialName;
    private String otherMaterialName;
    private String entryNumber;
    private boolean switchValue;
}
