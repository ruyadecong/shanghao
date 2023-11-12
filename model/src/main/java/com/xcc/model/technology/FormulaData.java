package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配方信息表
 * </p>
 *
 * @author xcc
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 打样编码
     */
    private String proofCode;

    /**
     * 配方编码
     */
    private String formulaCode;

/*    *//**
     * 非表字段
     * 产品中此配方占净含量的百分比
     * 在Controller中添加
     *//*
    @TableField(exist = false)
    private BigDecimal formulaContent;*/

    /**
     * 配方名称
     */
    private String formulaName;

    /**
     * 色号
     */
    private String colorNumber;

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
     * 配方开发人员
     */
    private String formulaDev;

    /**
     * 配方开发日期
     */
    private LocalDate formulaDate;

    /**
     * 确认阶段
     */
    private Integer isConfirm;

    /**
     * 类型标记，默认为0（产品配方），1为半成品配方
     */
    private Integer classMark;

    /**
     * 申请日期
     * 这个属性不存在于表中，是使用createTime来生成的
     */
    @TableField(exist = false)
    private String applyDate;

    /**
     * 非表数据
     * 这个属性不存在于表中，用于记录配方的工艺步骤的数量，通过sql语句获得
     */
    @TableField(exist = false)
    private Integer craftNumber;

    /**
     * 配方类型
     * 这个属性不存在于表中，是使用classMark来生成的
     */
    @TableField(exist = false)
    private String classModel;

    /**
     * 配方原料配比合计
     * 这个属性不存在于表中，是使用配方的成分数据表来生成的，目的在于标识出配方原料合计不是100%的记录
     */
    @TableField(exist = false)
    private BigDecimal sumOfContent;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1正常 0停用）
     */
    private Boolean status;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
