package com.xcc.model.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.BitSet;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单的配方表（隔离于配方表）
 * </p>
 *
 * @author xcc
 * @since 2023-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderSemiComponent extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
*/

    /**
     * 订单产品的配方中的半成品配方的id，可以与订单的配方、产品及订单关联）
     */
    private Integer orderSemiId;

    /**
     * 配方类型标记，默认为0（产品配方），1为半成品配方
     */
    private Integer classMark;

    /**
     * 配方中的原料的类型标记，默认为2（原料），1为半成品
     * 这个字段改为保存二级半成品在order_semi_formula中的id
     */
    private Integer componentMark;

    /**
     * 原料序号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer rankNumber;

    /**
     * 原料编码
     */
    private String materialCode;

    /**
     * 配方中原料的百分比
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal materialContent;

    /**
     * 原料使用目的
     */
    private String materialPurpose;

    /**
     * 原料的特殊要求
     */
    private String specialReqirement;

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

/*
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    */
/**
     * 删除标记（0:可用 1:已删除）
     *//*

    private Integer isDeleted;
*/


}
