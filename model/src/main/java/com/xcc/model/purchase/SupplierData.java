package com.xcc.model.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 供应商信息
 * </p>
 *
 * @author xcc
 * @since 2023-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SupplierData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商类别
     */
    private String supplierSort;

    /**
     * 供应商简称
     */
    private String simpleName;

    /**
     * 供应商全称
     */
    private String totalName;

    /**
     * 供应商国别
     */
    private String country;

    /**
     * 供应商地址
     */
    private String address;

    /**
     * 供应商营业执照编码
     */
    private String businessLicense;

    /**
     * 供应商许可证编码
     */
    private String permitCode;

    /**
     * 是否关键原料供应商
     */
    private Integer isKey;

    /**
     * 联系人
     */
    private String liaisonPerson;

    /**
     * 联系方式
     */
    private String liaisonMethod;

    /**
     * 采购的原料
     */
    private String materials;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1正常 0停用）
     */
    private Integer status;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
