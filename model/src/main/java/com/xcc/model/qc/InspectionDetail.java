package com.xcc.model.qc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 检验项目汇总表
 * </p>
 *
 * @author xcc
 * @since 2023-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InspectionDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 检验项目标记
     */
    private String mark;

    /**
     * 检验项目
     */
    private String inspectionItem;

    /**
     * 检验标准
     */
    private String inspectionStandard;

    /**
     * 检验方法
     */
    private String inspectionMethod;

    /**
     * 检验项目优先级
     */
    private Integer inspectionOrder;

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
