package com.xcc.model.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料基础信息临时表
 * </p>
 *
 * @author xcc
 * @since 2023-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialBasedataTemp extends BaseEntity implements Serializable {

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

    /**
     * 原料数据修改的原因
     */
    private String updateReason;

    /**
     * 状态
     * -2 申请修改原料数据，待同意修改
     * -1 同意修改，待修改
     * 0  修改完成（新增原料），待审批
     * 1  审批完成，可使用
     */
    private Integer isConfirm;

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
