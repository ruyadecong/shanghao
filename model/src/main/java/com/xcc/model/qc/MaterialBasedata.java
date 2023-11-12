package com.xcc.model.qc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.xcc.model.base.BaseEntity;
import com.xcc.model.technology.FileData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料基础信息
 * </p>
 *
 * @author xcc
 * @since 2023-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialBasedata extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;*/

    /**
     * 旧编码
     */
    private String oldCode;

    /**
     * 新编码
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
     * 原料检验项目数，-1表示未定稿
     */
    private Integer standardNumber;

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
     * 原料标准更新原因
     */
    private String updateReason;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1正常 0停用）
     */
    private Integer status;

    /**
     * 状态
     * -2 申请修改原料数据，待同意修改
     * -1 同意修改，待修改
     * 0  修改完成（新增原料），待审批
     * 1  审批完成，可使用
     */
    private Integer isConfirm;

    /**
     * 非表数据
     * 用于给页面传递原料对应的文件列表
     */
    @TableField(exist = false)
    private List<FileData> fileDataList;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
