package com.xcc.model.history;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料基础信息历史数据
 * </p>
 *
 * @author xcc
 * @since 2023-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialBasedataHistory extends BaseEntity implements Serializable {

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
     * 产品名称
     */
    private String productName;

    /**
     * 产家名称
     */
    private String producterName;

    /**
     * 报送码
     */
    private String submitCode;

    /**
     * 原料类别（决定储存地点）
     */
    private String inventoryClass;

    /**
     * 原料型号
     */
    private String materialModel;

    /**
     * 原料规格
     */
    private String materialSpecifications;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 备注
     */
    private String remark;

/*    private LocalDateTime createTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
