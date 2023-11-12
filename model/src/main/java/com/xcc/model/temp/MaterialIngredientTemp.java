package com.xcc.model.temp;

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
 * 原料成分信息临时表
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialIngredientTemp extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 原料编码
     */
    private String materialCode;

    /**
     * 原料名称
     */
    private String materialName;

    /**
     * 原料中的成分名称
     */
    private String cName;

    /**
     * 原料中的成分INCI名称
     */
    private String inciName;

    /**
     * 原料中的成分的百分比
     */
    private BigDecimal ingredientContent;

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
