package com.xcc.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 产品配套数据
 * </p>
 *
 * @author xcc
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductComposition extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 产品在product_data表中的id
     */
    private Integer productId;

    /**
     * 组件顺序号
     */
    private Integer rankNumber;

    /**
     * 组件类别
     */
    private String compositionClass;

    /**
     * 组件名称
     */
    private String compositionName;

    /**
     * 比例（产品的量）
     */
    private Integer ratioProduct;

    /**
     * 比例（组件的量）
     */
    private Integer ratioComposition;

    /**
     * 组件数量所使用的单位
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

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
