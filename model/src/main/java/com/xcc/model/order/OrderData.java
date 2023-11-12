package com.xcc.model.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单信息
 * </p>
 *
 * @author xcc
 * @since 2023-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 订单编码，不可重复
     */
    private String orderCode;

    /**
     * 订单类别，内单、外单
     */
    private String orderType;

    /**
     * 订单状态（进行中、已完成、已取消）
     */
    private String orderStatus;

    /**
     * 订单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate orderDate;

    /**
     * 订单期限
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate orderTerm;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 业务员
     */
    private String salesMan;

    /**
     * 订单金额（元）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal orderMoney;

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
     * 版本号
     */
    private Integer versionNo;

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
