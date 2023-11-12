package com.xcc.model.history;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配方信息表（历史数据）
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaDataHistory extends BaseEntity implements Serializable {

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
     * 版本号
     */
    private Integer versionNo;

    /**
     * 类型标记，默认为0（产品配方），1为半成品配方
     */
    private Integer classMark;

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
