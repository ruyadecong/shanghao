package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配方投料工艺表
 * </p>
 *
 * @author xcc
 * @since 2023-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormulaCraft extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 配方表中的配方id
     */
    private Long formulaId;

    /**
     * 工艺顺序号
     */
    private Integer rankNumber;

    /**
     * 步骤描述
     */
    private String stageState;

    /**
     * 步骤时长下限
     */
    private Long stageDurationFloor;

    /**
     * 步骤时长上限
     */
    private Long stageDurationCeiling;

    /**
     * 步骤时长单位
     */
    private String unit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1正常 0停用）
     */
    private Boolean status;

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
