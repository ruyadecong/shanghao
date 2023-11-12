package com.xcc.model.purchase;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料采购信息暂存数据表
 * </p>
 *
 * @author xcc
 * @since 2023-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialPurchaseTemporarily extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 供应商代码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 原料编码
     */
    private String materialCode;

    /**
     * 原料名称
     */
    private String materialName;

    /**
     * 原料批号
     */
    private String batchNo;

    /**
     * 生产日期
     */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date productDate;

    /**
     * 有效期限
     */
    private Integer lifeSpan;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;

    /**
     * 录入量，kg
     */
    private BigDecimal quantityKg;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 送货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    /**
     * 送货单号
     */
    private String deliveryCode;

    /**
     * 实收数量，kg
     */
    private BigDecimal receivedQuantity;

    /**
     * 入库日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inDate;

    /**
     * 入库单号
     */
    private String entryNumber;

    /**
     * 整包装单价，元
     */
    private String wholePackingPrice;

    /**
     * 拆包单价，元
     */
    private String unpackingUnitPrice;

    /**
     * 包装规格及可否拆包
     */
    private String unpackingSituation;

    private String storageWarehouse;

    /**
     * 付款申请日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date subscriptionDate;



    /**
     * 付款申请日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applicationDate;

    /**
     * 实际付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate;

    /**
     * 单价，元/kg
     */
    private BigDecimal unitPrice;

    /**
     * 原料总价，元
     */
    private BigDecimal materialPrice;

    /**
     * 其他费用，元
     */
    private BigDecimal otherPrice;

    /**
     * 总价，元
     */
    private BigDecimal totalPrice;

    /**
     * 付款方式
     */
    private String paymentMethod;

    /**
     * 产家
     */
    private String materialProducer;

    /**
     * 是否报检，0否、1是
     */
    private Boolean isCallInspect;

    /**
     * 是否紧急放行，0否、1是
     */
    private Boolean isEmergencyRelease;

    /**
     * 是否放行，0否、1是
     */
    private Boolean isRelease;

    /**
     * 报告日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reportDate;

    /**
     * 备注
     */
    private String remark;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
