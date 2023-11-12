package com.xcc.model.qc;


import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 产品标准列表
 * </p>
 *
 * @author xcc
 * @since 2023-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductStandardList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 在standard_list中的id
     */
    private Long standardId;

    /**
     * 检验项目序号
     */
    private Integer rankNumber;

    /**
     * 标准类型（感官、理化、卫生、其他）
     */
    private String quoatType;

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
     * 备注
     */
    private String remark;

    /**
     * 是否半成品检验
     */
    private Boolean semiInspection;

    /**
     * 是否成品检验
     */
    private Boolean finishedInspection;

    /**
     * 是否完工检验
     */
    private Boolean finalInspection;

    /**
     * 是否型式检验
     */
    private Boolean typeInspection;

    /**
     * 状态（1正常 0停用）
     */
    private Boolean status;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Boolean isDeleted;*/


}
