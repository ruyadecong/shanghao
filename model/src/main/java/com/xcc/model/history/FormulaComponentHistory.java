package com.xcc.model.history;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配方成分表（历史数据）
 * </p>
 *
 * @author xcc
 * @since 2023-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaComponentHistory extends BaseEntity implements Serializable {

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
