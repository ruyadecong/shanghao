package com.xcc.model.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配方信息表
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaDataTemp extends BaseEntity implements Serializable {

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
     * 原料数据修改的原因
     */
    private String updateReason;

    /**
     * 状态
     * -2 申请修改原料数据，待同意修改
     * -1 同意修改，待修改
     * 0  修改完成（新增原料），待审批
     * 1  审批完成，可使用
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
     * 配方类型
     * 这个属性不存在于表中，是使用classMark来生成的
     */
    @TableField(exist = false)
    private String classModel;

    /**
     * 所处状态
     * 这个属性不存在于表中，是使用is_confirm来生成的
     */
    @TableField(exist = false)
    private String applyStatus;

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
