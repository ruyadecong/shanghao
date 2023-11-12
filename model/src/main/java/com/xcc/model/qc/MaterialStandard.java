package com.xcc.model.qc;

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
 * 原料标准信息
 * </p>
 *
 * @author xcc
 * @since 2023-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialStandard extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;*/

    /**
     * 原料编码
     */
    private String materialCode;

    /**
     * 检验项目类别
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
     * 状态
     */
    private Integer status;


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
