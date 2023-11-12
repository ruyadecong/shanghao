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
 * 产品信息表
 * </p>
 *
 * @author xcc
 * @since 2023-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductDataHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
*/

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编码（默认使用产品名称，如果产品名称有重复，则在产品编码中进行区分）
     */
    private String productCode;

    /**
     * 产品编号（某些产品可能会有）
     */
    private String productNumber;

    /**
     * 产品是否仅供出口（0：有内销；1：仅出口；2：不确定）
     */
    private Integer onlyExport;

    /**
     * 保质期限，年
     */
    private BigDecimal shelfLife;

    /**
     * 净含量，g
     */
    private BigDecimal netWeight;

    /**
     * 用于区分产品中配方净含量的数据模式（直接用净含量、百分比）
     */
    private String  weightModel;

    /**
     * 净含量单位，默认g
     */
    private String unit;

    /**
     * 执行标准
     */
    private String executiveStandard;

    /**
     * 备案号（也即产品执行的标准编号）
     */
    private String filingNumber;

    /**
     * 备案人
     */
    private String filingPerson;

    /**
     * 产品介绍
     */
    private String productIntroduction;

    /**
     * 使用方法
     */
    private String usageMethod;

    /**
     * 注意事项
     */
    private String productNote;

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
     * 确认阶段
     */
    private Integer isConfirm;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 修改原有
     */
    private String updateReason;

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
