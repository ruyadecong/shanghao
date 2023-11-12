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
 * 2021已使用原料目录
 * </p>
 *
 * @author xcc
 * @since 2023-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UsedIngredient extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
*/

    /**
     * 已使用原料目录中的序号
     */
    private String indexUsed;

    /**
     * 成分标准中文名称
     */
    private String cName;

    /**
     * 成分INCI名称
     */
    private String inciName;

    /**
     * 淋洗类最高使用量，%
     */
    private String maximumLeaching;

    /**
     * 驻留类最高使用量，%
     */
    private String maximumResidency;

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
