package com.xcc.model.order;

import lombok.Data;

@Data
public class OrderSearchVo {
    private Integer id;
    private String orderCode;
    private String orderType;
    private String orderStatus;
    private String customerName;

    private String productCode;
    private String selectModel;  //用于接收前端传回的产品数据来源，用以决定从以往订单还是产品库中获取产品编码建议列表

    private String model;       //用于接收前端修改时自动发送给后端的数据中的model，其代表所发送的（下面的）数据的类型
    private OrderData orderData;
    private OrderProduct orderProduct;
    private OrderProductFormula orderProductFormula;
    private OrderFormulaComponent orderFormulaComponent;
    private OrderSemiComponent orderSemiComponent;
    private String selectAddSource;
    private String newAddCode;

}
