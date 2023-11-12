package com.xcc.model.technology;

import java.math.BigDecimal;
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
 * 配方成分表
 * </p>
 *
 * @author xcc
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaComponent extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 配方编码
     */
    private String formulaCode;

    /**
     * 原料序号
     */
    private Integer rankNumber;

    /**
     * 原料编码
     */
    private String materialCode;

    /**
     * 配方中原料的百分比
     */
    private BigDecimal materialContent;

    /**
     * 原料使用目的
     */
    private String materialPurpose;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用来向前端反馈原料情况，如果无此原料为0，半成品为1，原料为2
     * 前端可根据此值来调整“查看原料信息”按钮的形态
     */
    @TableField(exist = false)
    private Integer mark;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
