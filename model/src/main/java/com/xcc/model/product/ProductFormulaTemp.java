package com.xcc.model.product;

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
 * 产品配方构成信息临时表
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductFormulaTemp extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 产品编码（默认使用产品名称，如果产品名称有重复，则在产品编码中进行区分）
     */
    private String productCode;
    /**
     * 配方编码
     */
    private String formulaCode;

    /**
     * 配方在产品中的色号
     */
    private String colorNumber;

    /**
     * 产品中此配方占净含量的百分比
     */
    private BigDecimal formulaContent;

    /**
     * 配方在此产品中的标记
     */
    private String formulaMark;

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
