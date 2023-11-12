package com.xcc.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单产品的半成品配方表（隔离于产品的配方表）
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderSemiFormula extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 订单产品数据表中的产品id（不可用产品编码，因其在订单产品数据表中不唯一）
     */
    private Integer orderProductId;

    /**
     * 半成品配方编码，唯一
     */
    private String semiCode;

    /**
     * 半成品配方的特殊要求
     */
    private String specialReqirement;

    /**
     * 类型标记，默认为0（产品配方），1为半成品配方
     */
    private Integer classMark;

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
