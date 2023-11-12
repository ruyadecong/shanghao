package com.xcc.model.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单产品数据表
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderProduct extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 订单编码，不可重复，关联订单信息
     */
    private String orderCode;

    /**
     * 产品编码，关联产品信息
     */
    private String productCode;

    /**
     * 产品数量，默认为0，单位为pcs
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal productAmount;

    /**
     * 产品类别（粉类F，蜡基L，乳化类R）
     */
    private String productType;

    /**
     * 产品的特殊要求
     */
    private String specialReqirement;

    /**
     * 产品批号
     */
    private String batchNo;

    /**
     * 净含量
     */
    private BigDecimal netWeight;

    /**
     * 用于区分产品中配方净含量的数据模式（直接用净含量、百分比）
     */
    private String weightModel;

    /**
     * 净含量单位，默认g
     */
    private String unit;

    /**
     * 标记1
     */
    private String markOne;

    /**
     * 标记2
     */
    private String markTwo;

    /**
     * 标记3
     */
    private String markThree;

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
